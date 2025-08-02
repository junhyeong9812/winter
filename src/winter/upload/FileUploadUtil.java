package winter.upload;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 파일 업로드 관련 유틸리티 메서드를 제공하는 클래스
 *
 * 파일명 생성, 확장자 검증, 디렉토리 관리, 보안 검증 등
 * 파일 업로드와 관련된 다양한 유틸리티 기능을 제공합니다.
 * */
public class FileUploadUtil {

    //위험한 파일명 패턴들
    private static final Pattern DANGEROUS_FILENAME_PATTERN =
            Pattern.compile(".*[<>:\"|?*\\\\/.]+.*");

    //실행 파일 확장자들 (보안상 위험)
    private static final String[] DANGEROUS_EXTENSIONS = {
            ".exe", ".bat", ".cmd", ".com", ".scr", ".pif", ".vbs", ".js", ".jar", ".sh"
    };

    private FileUploadUtil(){
        //유틸리티 클래스는 인스턴스 생성 방지
    }

    /**
     * 원본 파일명을 기반으로 고유한 파일명을 생성합니다.
     *
     * UUID를 사용하여 고유성을 보장하고 원본 확장자를 유지합니다.
     *
     * @param originalFilename 원본 파일명
     * @return 고유한 파일명 (UUID + 확장자)
     * */
    public static String generateUniqueFileName(String originalFilename){
        if(originalFilename == null || originalFilename.trim().isEmpty()){
            return UUID.randomUUID().toString();
        }
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * 타임스탬프를 포함한 고유한 파일을 생성합니다.
     *
     * @param originalFilename 원본 파일명
     * @return 타임스탬프가 포함된 고유한 파일명
     * */
    public static String generateTimestampFileName(String originalFilename){
        if(originalFilename == null || originalFilename.trim().isEmpty()){
            return generateTimestamp():
        }

        String nameWithoutExt = getFileNameWithoutExtension(originalFilename);
        String extension = getFIleExtension(originalFilename);
        String timestamp = generateTimestamp();

        return sanitizeFileName(nameWithoutExt) + "-" + timestamp + extension;
    }
    /**
     * 파일명에서 확장자를 추출합니다.
     * 
     * @param filename 파일명
     * @return 확장자 ( 점 포함), 확장자가 없으면 빈 문자열
     * */
    public static String getFileExtension(String filename){
        if(filename == null || filename.trim().isEmpty()){
            return "";
        }

        int lastDontIndex = filename.lastIndexOf(".");
        if(lastDontIndex == -1 || lastDontIndex == filename.length() -1){
            return "";
        }
        return "";
    }
}
