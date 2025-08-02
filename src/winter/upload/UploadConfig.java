package winter.upload;

import java.util.Arrays;

/**
 * 파일 업로드 관련 설정을 관리하는 클래스
 *
 * 업로드 디렉토리, 파일 크기 제한, 허용 확장자 등
 * 파일 업로드와 관련된 모든 설정을 중앙에서 관리합니다.
 */
public class UploadConfig {

    // 기본 설정값들
    private static final String DEFAULT_UPLOAD_DIR = "./uploads";
    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long DEFAULT_MAX_REQUEST_SIZE = 50 * 1024 * 1024; // 50MB
    private static final String[] DEFAULT_ALLOWED_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif", ".bmp",  // 이미지
            ".pdf", ".doc", ".docx", ".txt", ".rtf",  // 문서
            ".zip", ".rar", ".7z",                    // 압축
            ".mp3", ".wav", ".mp4", ".avi"            // 미디어
    };

    private String uploadDir;
    private long maxFileSize;
    private long maxRequestSize;
    private String[] allowedExtensions;
    private boolean createUploadDir;
    private boolean overwriteExisting;

    /**
     * 기본 설정으로 UploadConfig를 생성합니다.
     */
    public UploadConfig() {
        this.uploadDir = DEFAULT_UPLOAD_DIR;
        this.maxFileSize = DEFAULT_MAX_FILE_SIZE;
        this.maxRequestSize = DEFAULT_MAX_REQUEST_SIZE;
        this.allowedExtensions = DEFAULT_ALLOWED_EXTENSIONS.clone();
        this.createUploadDir = true;
        this.overwriteExisting = true;
    }

    /**
     * 지정된 업로드 디렉토리로 UploadConfig를 생성합니다.
     *
     * @param uploadDir 업로드 디렉토리 경로
     */
    public UploadConfig(String uploadDir) {
        this();
        this.uploadDir = uploadDir != null ? uploadDir : DEFAULT_UPLOAD_DIR;
    }

    /**
     * 업로드 디렉토리 경로를 반환합니다.
     *
     * @return 업로드 디렉토리 경로
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * 업로드 디렉토리 경로를 설정합니다.
     *
     * @param uploadDir 업로드 디렉토리 경로
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir != null ? uploadDir : DEFAULT_UPLOAD_DIR;
        return this;
    }

    /**
     * 최대 파일 크기를 반환합니다. (바이트 단위)
     *
     * @return 최대 파일 크기
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * 최대 파일 크기를 설정합니다. (바이트 단위)
     *
     * @param maxFileSize 최대 파일 크기
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setMaxFileSize(long maxFileSize) {
        this.maxFileSize = Math.max(0, maxFileSize);
        return this;
    }

    /**
     * 최대 요청 크기를 반환합니다. (바이트 단위)
     *
     * @return 최대 요청 크기
     */
    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * 최대 요청 크기를 설정합니다. (바이트 단위)
     *
     * @param maxRequestSize 최대 요청 크기
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = Math.max(0, maxRequestSize);
        return this;
    }

    /**
     * 허용된 파일 확장자 목록을 반환합니다.
     *
     * @return 허용된 확장자 배열 (복사본)
     */
    public String[] getAllowedExtensions() {
        return allowedExtensions.clone();
    }

    /**
     * 허용된 파일 확장자 목록을 설정합니다.
     *
     * @param allowedExtensions 허용된 확장자 배열
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setAllowedExtensions(String... allowedExtensions) {
        this.allowedExtensions = allowedExtensions != null ?
                allowedExtensions.clone() : DEFAULT_ALLOWED_EXTENSIONS.clone();
        return this;
    }

    /**
     * 업로드 디렉토리 자동 생성 여부를 반환합니다.
     *
     * @return 자동 생성 여부
     */
    public boolean isCreateUploadDir() {
        return createUploadDir;
    }

    /**
     * 업로드 디렉토리 자동 생성 여부를 설정합니다.
     *
     * @param createUploadDir 자동 생성 여부
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setCreateUploadDir(boolean createUploadDir) {
        this.createUploadDir = createUploadDir;
        return this;
    }

    /**
     * 기존 파일 덮어쓰기 여부를 반환합니다.
     *
     * @return 덮어쓰기 허용 여부
     */
    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    /**
     * 기존 파일 덮어쓰기 여부를 설정합니다.
     *
     * @param overwriteExisting 덮어쓰기 허용 여부
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
        return this;
    }

    /**
     * 편의 메서드: 이미지 파일만 허용하도록 설정합니다.
     *
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig allowImagesOnly() {
        this.allowedExtensions = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        return this;
    }

    /**
     * 편의 메서드: 문서 파일만 허용하도록 설정합니다.
     *
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig allowDocumentsOnly() {
        this.allowedExtensions = new String[]{".pdf", ".doc", ".docx", ".txt", ".rtf"};
        return this;
    }

    /**
     * 편의 메서드: 모든 파일을 허용하도록 설정합니다.
     *
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig allowAllFiles() {
        this.allowedExtensions = new String[0]; // 빈 배열 = 모든 확장자 허용
        return this;
    }

    /**
     * 편의 메서드: 파일 크기를 MB 단위로 설정합니다.
     *
     * @param maxFileSizeMB 최대 파일 크기 (MB)
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setMaxFileSizeMB(int maxFileSizeMB) {
        this.maxFileSize = (long) maxFileSizeMB * 1024 * 1024;
        return this;
    }

    /**
     * 편의 메서드: 요청 크기를 MB 단위로 설정합니다.
     *
     * @param maxRequestSizeMB 최대 요청 크기 (MB)
     * @return 체이닝을 위한 this 객체
     */
    public UploadConfig setMaxRequestSizeMB(int maxRequestSizeMB) {
        this.maxRequestSize = (long) maxRequestSizeMB * 1024 * 1024;
        return this;
    }

    @Override
    public String toString() {
        return "UploadConfig{" +
                "uploadDir='" + uploadDir + '\'' +
                ", maxFileSize=" + maxFileSize + " bytes" +
                ", maxRequestSize=" + maxRequestSize + " bytes" +
                ", allowedExtensions=" + Arrays.toString(allowedExtensions) +
                ", createUploadDir=" + createUploadDir +
                ", overwriteExisting=" + overwriteExisting +
                '}';
    }
}