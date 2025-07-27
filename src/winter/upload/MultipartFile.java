package winter.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 업로드된 파일을 추상화하는 인터페이스
 * HTTP multipart/form-data 요청에서 업로드된 파일을 나타냅니다.
 *
 * 이 인터페이스는 Spring의 MultipartFile을 참고하여 설계되었으며,
 * 파일의 메타데이터와 내용에 접근할 수 있는 메서드들을 제공합니다.
 */
public interface MultipartFile {

    /**
     * HTML 폼에서 사용된 필드명을 반환합니다.
     *
     * 예: <input type="file" name="avatar"> → "avatar"
     *
     * @return 폼 필드명
     */
    String getName();

    /**
     * 클라이언트가 업로드한 원본 파일명을 반환합니다.
     *
     * 예 : "profile.jpg", "document.pdf"
     *
     * @return 원본 파일명, 파일이 선택되지 않았다면 빈 문자열
     * */
    String getOriginalFilename();

    /**
     * 파일의 Content-Tpye을 반환합니다.
     *
     * 예 : "image/jpeg", "application/pdf", "text/plain"
     *
     * @return Content-Type,알 수 없다면 null
     * */
    String getContentType();

    /**
     * 업로드된 파일이 비여있는지 확인합니다.
     *
     * @return 파일이 비여있거나 없로드 되지 않았다면 true
     * */
    boolean isEmpty();

    /**
     * 파일의 크기를 바이트 단위로 반환합니다.
     *
     * @return 파일 크기 (바이트)
     * */
    long getSize();

    /**
     * 파일의 전체 내용을 byte 배열로 반환합니다.
     *
     * 주의 : 대용량 파일의 경우 메모리 사용량이 클 수 있으므로
     * getInputStream()을 사용하는 것이 좋습니다.
     *
     * @return 파일 내용
     * @throws IOException 파일 읽기 실패시
     * */
    byte[] getBytes() throws IOException;

    /**
     * 파일 내용을 읽을 수 있는 InputStream을 반환합니다.
     *
     * 스트림을 사용한 후에는 반드시 close()를 해야합니다.
     * try-with-resources 구문 사용을 권장합니다.
     *
     * @return 파일 내용 스트림
     * @thows IOException 스트립 생성 실패시
     * */
    InputStream getInputStream() throws IOException;

    /**
     * 업로드된 파일을 지정된 위치에 저장합니다.
     *
     * 이 메서드는 파일을 효율적으로 저장하며,
     * 대용량 파일도 메모리를 적게 사용하여 처리합니다.
     *
     * @param dest 저장할 파일 위치
     * @throws IOException 파일 저장 실패시
     * @throws IllegalStateException 파일이 이미 처리되었거나 이동할 수 없는 상태일 때
     * */
    void transferTo(File dest) throws IOException;
}
