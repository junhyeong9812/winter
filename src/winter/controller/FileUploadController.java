package winter.controller;

import winter.annotation.Controller;
import winter.annotation.ModelAttribute;
import winter.annotation.RequestMapping;
import winter.annotation.RequestParam;
import winter.form.UserProfile;
import winter.upload.FileUploadUtil;
import winter.upload.MultipartFile;
import winter.upload.UploadConfig;
import winter.view.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 업로드 기능을 테스트하는 컨트롤러
 *
 * 다양한 파일 업로드 시나리오를 처리하여
 * Winter Framework의 파일 업로드 기능을 검증합니다.
 *
 * 지원하는 기능:
 * - 단일 파일 업로드
 * - 다중 파일 업로드
 * - 파일 + 폼 데이터 혼합 업로드
 * - 파일 정보 조회 (AJAX)
 * - 프로필 + 아바타 업로드
 * - 파일 크기 및 확장자 검증
 */
@Controller
public class FileUploadController {

    private final UploadConfig uploadConfig;

    /**
     * 기본 생성자
     * 업로드 설정을 초기화합니다.
     */
    public FileUploadController() {
        this.uploadConfig = new UploadConfig()
                .setUploadDir("./uploads")           // 업로드 디렉토리
                .setMaxFileSizeMB(10)               // 최대 파일 크기: 10MB
                .setMaxRequestSizeMB(50)            // 최대 요청 크기: 50MB
                .setAllowedExtensions(".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".doc", ".docx")
                .setCreateUploadDir(true);          // 디렉토리 자동 생성
    }

    /**
     * 파일 업로드 폼 페이지
     * GET /upload/form
     */
    @RequestMapping(value = "/upload/form", method = "GET")
    public ModelAndView uploadForm() {
        ModelAndView mav = new ModelAndView("upload-form");
        mav.addAttribute("maxFileSize", FileUploadUtil.formatFileSize(uploadConfig.getMaxFileSize()));
        mav.addAttribute("allowedExtensions", String.join(", ", uploadConfig.getAllowedExtensions()));
        mav.addAttribute("uploadDir", uploadConfig.getUploadDir());
        return mav;
    }

    /**
     * 단일 파일 업로드 처리
     * POST /upload
     */
    @RequestMapping(value = "/upload", method = "POST")
    public ModelAndView uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description
    ) {
        try {
            System.out.println("=== 단일 파일 업로드 처리 ===");
            System.out.println("파일명: " + file.getOriginalFilename());
            System.out.println("파일 크기: " + FileUploadUtil.formatFileSize(file.getSize()));
            System.out.println("Content-Type: " + file.getContentType());
            System.out.println("설명: " + description);

            // 1. 파일 존재 여부 검증
            if (file == null || file.isEmpty()) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "파일이 선택되지 않았습니다.");
                mav.addAttribute("errorCode", "NO_FILE");
                return mav;
            }

            // 2. 파일 크기 검증
            if (!FileUploadUtil.isValidFileSize(file.getSize(), uploadConfig.getMaxFileSize())) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "파일 크기가 너무 큽니다. 최대 " +
                        FileUploadUtil.formatFileSize(uploadConfig.getMaxFileSize()) + "까지 가능합니다.");
                mav.addAttribute("errorCode", "FILE_TOO_LARGE");
                mav.addAttribute("actualSize", FileUploadUtil.formatFileSize(file.getSize()));
                mav.addAttribute("maxSize", FileUploadUtil.formatFileSize(uploadConfig.getMaxFileSize()));
                return mav;
            }

            // 3. 확장자 검증
            if (!FileUploadUtil.isAllowedExtension(file.getOriginalFilename(), uploadConfig.getAllowedExtensions())) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "허용되지 않은 파일 형식입니다.");
                mav.addAttribute("errorCode", "INVALID_EXTENSION");
                mav.addAttribute("filename", file.getOriginalFilename());
                mav.addAttribute("allowedExtensions", String.join(", ", uploadConfig.getAllowedExtensions()));
                return mav;
            }

            // 4. 파일명 안전성 검증
            if (!FileUploadUtil.isSafeFilename(file.getOriginalFilename())) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "안전하지 않은 파일명입니다.");
                mav.addAttribute("errorCode", "UNSAFE_FILENAME");
                mav.addAttribute("filename", file.getOriginalFilename());
                return mav;
            }

            // 5. 파일 저장
            String savedPath = saveFile(file);
            System.out.println("파일 저장 완료: " + savedPath);

            // 6. 성공 응답
            ModelAndView mav = new ModelAndView("upload-success");
            mav.addAttribute("filename", file.getOriginalFilename());
            mav.addAttribute("savedPath", savedPath);
            mav.addAttribute("fileSize", FileUploadUtil.formatFileSize(file.getSize()));
            mav.addAttribute("contentType", file.getContentType());
            mav.addAttribute("description", description);
            mav.addAttribute("isImage", FileUploadUtil.isImageFile(file.getContentType()));
            mav.addAttribute("uploadTime", System.currentTimeMillis());
            return mav;

        } catch (Exception e) {
            System.err.println("파일 업로드 중 오류: " + e.getMessage());
            e.printStackTrace();

            ModelAndView mav = new ModelAndView("upload-error");
            mav.addAttribute("error", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            mav.addAttribute("errorCode", "UPLOAD_FAILED");
            mav.addAttribute("exception", e.getClass().getSimpleName());
            return mav;
        }
    }

    /**
     * 다중 파일 업로드 처리
     * POST /upload/multiple
     */
    @RequestMapping(value = "/upload/multiple", method = "POST")
    public ModelAndView uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("category") String category
    ) {
        try {
            System.out.println("=== 다중 파일 업로드 처리 ===");
            System.out.println("파일 개수: " + (files != null ? files.length : 0));
            System.out.println("카테고리: " + category);

            // 1. 파일 배열 검증
            if (files == null || files.length == 0) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "업로드할 파일이 선택되지 않았습니다.");
                mav.addAttribute("errorCode", "NO_FILES");
                return mav;
            }

            List<UploadResult> results = new ArrayList<>();
            long totalSize = 0;

            // 2. 각 파일 처리
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                System.out.println("처리 중인 파일 " + (i + 1) + ": " + file.getOriginalFilename());

                // 빈 파일은 건너뛰기
                if (file.isEmpty()) {
                    System.out.println("빈 파일 건너뛰기: " + file.getOriginalFilename());
                    continue;
                }

                // 전체 요청 크기 검증
                totalSize += file.getSize();
                if (totalSize > uploadConfig.getMaxRequestSize()) {
                    ModelAndView mav = new ModelAndView("upload-error");
                    mav.addAttribute("error", "전체 파일 크기가 너무 큽니다. 최대 " +
                            FileUploadUtil.formatFileSize(uploadConfig.getMaxRequestSize()) + "까지 가능합니다.");
                    mav.addAttribute("errorCode", "REQUEST_TOO_LARGE");
                    mav.addAttribute("totalSize", FileUploadUtil.formatFileSize(totalSize));
                    return mav;
                }

                // 개별 파일 검증 및 저장
                try {
                    if (FileUploadUtil.isValidFileSize(file.getSize(), uploadConfig.getMaxFileSize()) &&
                            FileUploadUtil.isAllowedExtension(file.getOriginalFilename(), uploadConfig.getAllowedExtensions()) &&
                            FileUploadUtil.isSafeFilename(file.getOriginalFilename())) {

                        String savedPath = saveFile(file);
                        results.add(new UploadResult(
                                file.getOriginalFilename(),
                                savedPath,
                                file.getSize(),
                                file.getContentType(),
                                true,
                                null
                        ));
                        System.out.println("파일 저장 성공: " + savedPath);

                    } else {
                        String errorMsg = "파일 검증 실패";
                        if (!FileUploadUtil.isValidFileSize(file.getSize(), uploadConfig.getMaxFileSize())) {
                            errorMsg = "파일 크기 초과";
                        } else if (!FileUploadUtil.isAllowedExtension(file.getOriginalFilename(), uploadConfig.getAllowedExtensions())) {
                            errorMsg = "허용되지 않은 확장자";
                        } else if (!FileUploadUtil.isSafeFilename(file.getOriginalFilename())) {
                            errorMsg = "안전하지 않은 파일명";
                        }

                        results.add(new UploadResult(
                                file.getOriginalFilename(),
                                null,
                                file.getSize(),
                                file.getContentType(),
                                false,
                                errorMsg
                        ));
                        System.out.println("파일 검증 실패: " + file.getOriginalFilename() + " - " + errorMsg);
                    }
                } catch (Exception e) {
                    results.add(new UploadResult(
                            file.getOriginalFilename(),
                            null,
                            file.getSize(),
                            file.getContentType(),
                            false,
                            "저장 실패: " + e.getMessage()
                    ));
                    System.err.println("파일 저장 실패: " + file.getOriginalFilename() + " - " + e.getMessage());
                }
            }

            // 3. 결과 통계 계산
            long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
            long failCount = results.size() - successCount;

            System.out.println("업로드 완료 - 성공: " + successCount + ", 실패: " + failCount);

            // 4. 성공 응답
            ModelAndView mav = new ModelAndView("upload-multiple-success");
            mav.addAttribute("results", results);
            mav.addAttribute("category", category);
            mav.addAttribute("totalFiles", files.length);
            mav.addAttribute("successCount", successCount);
            mav.addAttribute("failCount", failCount);
            mav.addAttribute("totalSize", FileUploadUtil.formatFileSize(totalSize));
            return mav;

        } catch (Exception e) {
            System.err.println("다중 파일 업로드 중 오류: " + e.getMessage());
            e.printStackTrace();

            ModelAndView mav = new ModelAndView("upload-error");
            mav.addAttribute("error", "다중 파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            mav.addAttribute("errorCode", "MULTIPLE_UPLOAD_FAILED");
            mav.addAttribute("exception", e.getClass().getSimpleName());
            return mav;
        }
    }

    /**
     * 프로필과 아바타 업로드 처리 (파일 + 폼 데이터 혼합)
     * POST /upload/profile
     */
    @RequestMapping(value = "/upload/profile", method = "POST")
    public ModelAndView uploadProfile(
            @ModelAttribute UserProfile profile,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        try {
            System.out.println("=== 프로필 업로드 처리 ===");
            System.out.println("프로필: " + profile);
            System.out.println("아바타: " + (avatar != null ? avatar.getOriginalFilename() : "없음"));

            // 1. 프로필 정보 검증
            if (profile.getName() == null || profile.getName().trim().isEmpty()) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", "이름은 필수 입력 항목입니다.");
                mav.addAttribute("errorCode", "MISSING_NAME");
                return mav;
            }

            // 2. 프로필 기본 유효성 검증
            String validationError = profile.getValidationError();
            if (validationError != null) {
                ModelAndView mav = new ModelAndView("upload-error");
                mav.addAttribute("error", validationError);
                mav.addAttribute("errorCode", "PROFILE_VALIDATION_FAILED");
                return mav;
            }

            // 3. 아바타 이미지 처리
            if (avatar != null && !avatar.isEmpty()) {
                System.out.println("아바타 처리 중: " + avatar.getOriginalFilename());

                // 이미지 파일인지 확인
                if (!FileUploadUtil.isImageFile(avatar.getContentType())) {
                    ModelAndView mav = new ModelAndView("upload-error");
                    mav.addAttribute("error", "아바타는 이미지 파일만 업로드 가능합니다.");
                    mav.addAttribute("errorCode", "INVALID_AVATAR_TYPE");
                    mav.addAttribute("contentType", avatar.getContentType());
                    return mav;
                }

                // 아바타 크기 검증 (이미지는 더 작은 제한)
                long maxAvatarSize = 5 * 1024 * 1024; // 5MB
                if (avatar.getSize() > maxAvatarSize) {
                    ModelAndView mav = new ModelAndView("upload-error");
                    mav.addAttribute("error", "아바타 이미지 크기가 너무 큽니다. 최대 " +
                            FileUploadUtil.formatFileSize(maxAvatarSize) + "까지 가능합니다.");
                    mav.addAttribute("errorCode", "AVATAR_TOO_LARGE");
                    return mav;
                }

                // 아바타 파일 저장
                String avatarPath = saveFile(avatar);
                profile.setAvatarPath(avatarPath);
                System.out.println("아바타 저장 완료: " + avatarPath);
            }

            // 4. 프로필 저장 (실제로는 데이터베이스에 저장)
            System.out.println("프로필 저장: " + profile);

            // 5. 성공 응답
            ModelAndView mav = new ModelAndView("profile-updated");
            mav.addAttribute("profile", profile);
            mav.addAttribute("hasAvatar", avatar != null && !avatar.isEmpty());
            mav.addAttribute("updateTime", System.currentTimeMillis());
            return mav;

        } catch (Exception e) {
            System.err.println("프로필 업로드 중 오류: " + e.getMessage());
            e.printStackTrace();

            ModelAndView mav = new ModelAndView("upload-error");
            mav.addAttribute("error", "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            mav.addAttribute("errorCode", "PROFILE_UPDATE_FAILED");
            mav.addAttribute("exception", e.getClass().getSimpleName());
            return mav;
        }
    }

    /**
     * 파일 정보 조회 (AJAX용)
     * POST /upload/info
     */
    @RequestMapping(value = "/upload/info", method = "POST")
    public ModelAndView getFileInfo(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== 파일 정보 조회 ===");

            ModelAndView mav = new ModelAndView("json");

            if (file == null || file.isEmpty()) {
                mav.addAttribute("success", false);
                mav.addAttribute("error", "파일이 없습니다.");
                mav.addAttribute("errorCode", "NO_FILE");
                return mav;
            }

            // 파일 정보 수집
            FileInfo info = new FileInfo();
            info.setName(file.getOriginalFilename());
            info.setSize(file.getSize());
            info.setFormattedSize(FileUploadUtil.formatFileSize(file.getSize()));
            info.setContentType(file.getContentType());
            info.setIsImage(FileUploadUtil.isImageFile(file.getContentType()));
            info.setIsText(FileUploadUtil.isTextFile(file.getContentType()));
            info.setIsSafe(FileUploadUtil.isSafeFilename(file.getOriginalFilename()));
            info.setIsAllowedExtension(FileUploadUtil.isAllowedExtension(
                    file.getOriginalFilename(), uploadConfig.getAllowedExtensions()));
            info.setIsValidSize(FileUploadUtil.isValidFileSize(file.getSize(), uploadConfig.getMaxFileSize()));

            System.out.println("파일 정보: " + file.getOriginalFilename() +
                    " (" + info.getFormattedSize() + ", " + file.getContentType() + ")");

            mav.addAttribute("success", true);
            mav.addAttribute("fileInfo", info);
            return mav;

        } catch (Exception e) {
            System.err.println("파일 정보 조회 중 오류: " + e.getMessage());

            ModelAndView mav = new ModelAndView("json");
            mav.addAttribute("success", false);
            mav.addAttribute("error", "파일 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            mav.addAttribute("errorCode", "INFO_QUERY_FAILED");
            return mav;
        }
    }

    /**
     * 파일 저장 유틸리티 메서드
     *
     * @param file 저장할 파일
     * @return 저장된 파일의 경로
     * @throws IOException 파일 저장 실패시
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 1. 업로드 디렉토리 생성
        FileUploadUtil.ensureDirectoryExists(uploadConfig.getUploadDir());

        // 2. 고유한 파일명 생성 (UUID 기반)
        String uniqueFilename = FileUploadUtil.generateUniqueFileName(file.getOriginalFilename());

        // 3. 안전한 파일 경로 생성
        String filePath = FileUploadUtil.createSafeFilePath(uploadConfig.getUploadDir(), uniqueFilename);

        // 4. 파일 저장
        File destFile = new File(filePath);
        file.transferTo(destFile);

        System.out.println("파일 저장: " + file.getOriginalFilename() + " -> " + filePath);
        return filePath;
    }

    /**
     * 업로드 결과를 담는 내부 클래스
     */
    public static class UploadResult {
        private String originalFilename;
        private String savedPath;
        private long size;
        private String contentType;
        private boolean success;
        private String errorMessage;

        public UploadResult(String originalFilename, String savedPath, long size,
                            String contentType, boolean success, String errorMessage) {
            this.originalFilename = originalFilename;
            this.savedPath = savedPath;
            this.size = size;
            this.contentType = contentType;
            this.success = success;
            this.errorMessage = errorMessage;
        }

        // getter 메서드들
        public String getOriginalFilename() { return originalFilename; }
        public String getSavedPath() { return savedPath; }
        public long getSize() { return size; }
        public String getFormattedSize() { return FileUploadUtil.formatFileSize(size); }
        public String getContentType() { return contentType; }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public boolean isImage() { return FileUploadUtil.isImageFile(contentType); }

        @Override
        public String toString() {
            return "UploadResult{" +
                    "originalFilename='" + originalFilename + '\'' +
                    ", success=" + success +
                    ", size=" + getFormattedSize() +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    /**
     * 파일 정보를 담는 내부 클래스
     */
    public static class FileInfo {
        private String name;
        private long size;
        private String formattedSize;
        private String contentType;
        private boolean isImage;
        private boolean isText;
        private boolean isSafe;
        private boolean isAllowedExtension;
        private boolean isValidSize;

        // getter/setter 메서드들
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }

        public String getFormattedSize() { return formattedSize; }
        public void setFormattedSize(String formattedSize) { this.formattedSize = formattedSize; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }

        public boolean getIsImage() { return isImage; }
        public void setIsImage(boolean isImage) { this.isImage = isImage; }

        public boolean getIsText() { return isText; }
        public void setIsText(boolean isText) { this.isText = isText; }

        public boolean getIsSafe() { return isSafe; }
        public void setIsSafe(boolean isSafe) { this.isSafe = isSafe; }

        public boolean getIsAllowedExtension() { return isAllowedExtension; }
        public void setIsAllowedExtension(boolean isAllowedExtension) { this.isAllowedExtension = isAllowedExtension; }

        public boolean getIsValidSize() { return isValidSize; }
        public void setIsValidSize(boolean isValidSize) { this.isValidSize = isValidSize; }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "name='" + name + '\'' +
                    ", size=" + formattedSize +
                    ", contentType='" + contentType + '\'' +
                    ", isImage=" + isImage +
                    ", isSafe=" + isSafe +
                    '}';
        }
    }
}