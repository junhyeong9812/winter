package winter.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 파일 업로드 관련 유틸리티 메서드를 제공하는 클래스
 *
 * 파일명 생성, 확장자 검증, 디렉토리 관리, 보안 검증 등
 * 파일 업로드와 관련된 다양한 유틸리티 기능을 제공합니다.
 */
public class FileUploadUtil {

    // 위험한 파일명 패턴들
    private static final Pattern DANGEROUS_FILENAME_PATTERN =
            Pattern.compile(".*[<>:\"|?*\\\\/.]+.*");

    // 실행 파일 확장자들 (보안상 위험)
    private static final String[] DANGEROUS_EXTENSIONS = {
            ".exe", ".bat", ".cmd", ".com", ".scr", ".pif", ".vbs", ".js", ".jar", ".sh"
    };

    private FileUploadUtil() {
        // 유틸리티 클래스는 인스턴스 생성 방지
    }

    /**
     * 원본 파일명을 기반으로 고유한 파일명을 생성합니다.
     *
     * UUID를 사용하여 고유성을 보장하고 원본 확장자를 유지합니다.
     *
     * @param originalFilename 원본 파일명
     * @return 고유한 파일명 (UUID + 확장자)
     */
    public static String generateUniqueFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }

        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 타임스탬프를 포함한 고유한 파일명을 생성합니다.
     *
     * @param originalFilename 원본 파일명
     * @return 타임스탬프가 포함된 고유한 파일명
     */
    public static String generateTimestampFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return generateTimestamp();
        }

        String nameWithoutExt = getFileNameWithoutExtension(originalFilename);
        String extension = getFileExtension(originalFilename);
        String timestamp = generateTimestamp();

        return sanitizeFileName(nameWithoutExt) + "_" + timestamp + extension;
    }

    /**
     * 파일명에서 확장자를 추출합니다.
     *
     * @param filename 파일명
     * @return 확장자 (점 포함), 확장자가 없으면 빈 문자열
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 파일명에서 확장자를 제외한 이름을 추출합니다.
     *
     * @param filename 파일명
     * @return 확장자를 제외한 파일명
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }

        return filename.substring(0, lastDotIndex);
    }

    /**
     * 파일의 확장자가 허용된 확장자 목록에 포함되는지 확인합니다.
     *
     * @param filename 파일명
     * @param allowedExtensions 허용된 확장자 배열
     * @return 허용된 확장자면 true, 제한이 없으면(빈 배열) true
     */
    public static boolean isAllowedExtension(String filename, String[] allowedExtensions) {
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return true; // 제한이 없으면 모든 확장자 허용
        }

        String extension = getFileExtension(filename);
        if (extension.isEmpty()) {
            return false; // 확장자가 없으면 거부
        }

        for (String allowed : allowedExtensions) {
            if (allowed != null && allowed.toLowerCase().equals(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 파일 확장자가 위험한 실행 파일인지 확인합니다.
     *
     * @param filename 파일명
     * @return 위험한 확장자면 true
     */
    public static boolean isDangerousExtension(String filename) {
        String extension = getFileExtension(filename);
        if (extension.isEmpty()) {
            return false;
        }

        for (String dangerous : DANGEROUS_EXTENSIONS) {
            if (dangerous.equals(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 파일 크기가 허용된 최대 크기 이내인지 확인합니다.
     *
     * @param size 파일 크기 (바이트)
     * @param maxSize 최대 허용 크기 (바이트)
     * @return 허용된 크기면 true
     */
    public static boolean isValidFileSize(long size, long maxSize) {
        return size >= 0 && size <= maxSize;
    }

    /**
     * 지정된 디렉토리가 존재하지 않으면 생성합니다.
     *
     * @param dirPath 디렉토리 경로
     * @throws IOException 디렉토리 생성 실패시
     */
    public static void ensureDirectoryExists(String dirPath) throws IOException {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path cannot be null or empty");
        }

        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        } else if (!Files.isDirectory(path)) {
            throw new IOException("Path exists but is not a directory: " + dirPath);
        }
    }

    /**
     * 안전한 파일명을 생성합니다. (위험한 문자 제거)
     *
     * @param filename 원본 파일명
     * @return 안전한 파일명
     */
    public static String sanitizeFileName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }

        // 경로 구분자 제거
        String sanitized = filename.replaceAll("[/\\\\]", "_");

        // 위험한 문자 제거
        sanitized = sanitized.replaceAll("[<>:\"|?*]", "_");

        // 상대 경로 제거
        sanitized = sanitized.replaceAll("\\.\\.", "_");

        // 제어 문자 제거
        sanitized = sanitized.replaceAll("[\\x00-\\x1f\\x7f]", "_");

        // 앞뒤 공백 및 점 제거
        sanitized = sanitized.trim().replaceAll("^\\.*", "").replaceAll("\\.*$", "");

        // 빈 문자열이면 기본값 사용
        if (sanitized.isEmpty()) {
            sanitized = "file_" + System.currentTimeMillis();
        }

        return sanitized;
    }

    /**
     * 안전한 파일 경로를 생성합니다. (경로 조작 공격 방지)
     *
     * @param uploadDir 업로드 디렉토리
     * @param filename 파일명
     * @return 안전한 파일 경로
     */
    public static String createSafeFilePath(String uploadDir, String filename) {
        String safeFilename = sanitizeFileName(filename);
        return Paths.get(uploadDir, safeFilename).toString();
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환합니다.
     *
     * @param bytes 바이트 크기
     * @return 사람이 읽기 쉬운 형태의 크기 (예: "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "Unknown";
        } else if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 파일이 이미지 파일인지 확인합니다.
     *
     * @param contentType Content-Type 헤더값
     * @return 이미지 파일이면 true
     */
    public static boolean isImageFile(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }

    /**
     * 파일이 텍스트 파일인지 확인합니다.
     *
     * @param contentType Content-Type 헤더값
     * @return 텍스트 파일이면 true
     */
    public static boolean isTextFile(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("text/");
    }

    /**
     * 파일명이 안전한지 검증합니다.
     *
     * @param filename 파일명
     * @return 안전하면 true
     */
    public static boolean isSafeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        // 위험한 패턴 확인
        if (DANGEROUS_FILENAME_PATTERN.matcher(filename).matches()) {
            return false;
        }

        // 위험한 확장자 확인
        if (isDangerousExtension(filename)) {
            return false;
        }

        // 예약어 확인 (Windows)
        String nameWithoutExt = getFileNameWithoutExtension(filename).toUpperCase();
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4",
                "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2",
                "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

        for (String reserved : reservedNames) {
            if (reserved.equals(nameWithoutExt)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 현재 시각으로 타임스탬프를 생성합니다.
     *
     * @return yyyyMMdd_HHmmss 형태의 타임스탬프
     */
    private static String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    /**
     * 임시 파일을 생성합니다.
     *
     * @param prefix 파일명 접두사
     * @param suffix 파일명 접미사
     * @return 생성된 임시 파일
     * @throws IOException 파일 생성 실패시
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix != null ? prefix : "upload", suffix);
    }
}