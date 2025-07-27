package winter.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * MultipartFile의 표준 구현체
 *
 * 업로드된 파일의 메타데이터와 내용을 메모리에 저장하고 관리합니다.
 * 파일 내용은 byte 배열로 저장되므로 대용량 파일 처리 시 메모리 사용량에 주의해야 합니다.
 * */
public class StandardMultipartFile implements MultipartFile{
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    /**
     * StandardMultipartFile 생성자
     *
     * @param name 폼 필드명
     * @param originalFilename 원본 파일명
     * @param contentType MIME 타입
     * @param content 파일 내용
     * */
    public StandardMultipartFile(String name,String originalFilename,
                                 String contentType, byte[] content){
        this.name = name != null ? name : "";
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        // 방어적 복사로 원본 데이터 보호
        return content.clone();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException{
        if(dest == null){
            throw new IllegalArgumentException("Destination file cannot be null");
        }

        //목적지 디렉토리가 존재하지 않으면 생성
        File parentDir = dest.getParentFile();
        if(parentDir != null && !parentDir.exists()){
            if(!parentDir.mkdir()){
                throw new IOException("Could not create directory: " + parentDir.getAbsolutePath());
            }
        }

        //파일 저장
        try ( InputStream is = getInputStream()){
            Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public String toString() {
        return "StandardMultipartFile{" +
                "name='" + name + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + getSize() +
                ", isEmpty=" + isEmpty() +
                '}';
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        StandardMultipartFile that = (StandardMultipartFile) obj;
        return name.equals(that.name) &&
                originalFilename.equals(that.originalFilename) &&
                (contentType != null ? contentType.equals(that.contentType) : that.contentType== null) &&
                java.util.Arrays.equals(content,that.content);

    }

    @Override
    public int hashCode(){
        int result = name.hashCode();
        result = 31 * result + originalFilename.hashCode();
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + java.util.Arrays.hashCode(content);
        return  result;
    }


}
