package winter.upload;

import winter.http.HttpRequest;

import java.io.BufferedReader;
import java.util.*;

/**
 * Multipart 요청을 처리하는 HttpRequest 확장 클래스
 *
 * multipart/form-data 형태의 요청에서 파일과 일반 파라미터를 분리하여 관리합니다.
 * 기존 HttpRequest의 모든 기능을 유지하면서 파일 업로드 관련 기능을 추가합니다.
 */
public class MultipartRequest extends HttpRequest {

    private final Map<String, List<MultipartFile>> files;

    /**
     * MultipartRequest 생성자
     *
     * @param method HTTP 메서드 (GET, POST 등)
     * @param path 요청 경로
     * @param headers HTTP 헤더 맵
     * @param parameters 일반 파라미터 맵
     * @param body 요청 본문 BufferedReader
     * @param files 업로드된 파일 맵
     */
    public MultipartRequest(String method, String path, Map<String, String> headers,
                            Map<String, List<String>> parameters, BufferedReader body,
                            Map<String, List<MultipartFile>> files) {
        super(method, path, headers, parameters, body);
        this.files = files != null ? files : new HashMap<>();
    }

    /**
     * 지정된 이름의 첫 번째 파일을 반환합니다.
     *
     * 같은 이름으로 여러 파일이 업로드된 경우 첫 번째 파일만 반환됩니다.
     * 여러 파일을 모두 가져오려면 getFiles() 메서드를 사용하세요.
     *
     * @param name 파일 필드명
     * @return 업로드된 파일, 없으면 null
     */
    public MultipartFile getFile(String name) {
        List<MultipartFile> fileList = files.get(name);
        return (fileList != null && !fileList.isEmpty()) ? fileList.get(0) : null;
    }

    /**
     * 지정된 이름의 모든 파일을 반환합니다.
     *
     * multiple 속성으로 여러 파일이 업로드된 경우나
     * 같은 이름의 input이 여러 개 있는 경우에 사용합니다.
     *
     * @param name 파일 필드명
     * @return 업로드된 파일 목록 (빈 리스트일 수 있음)
     */
    public List<MultipartFile> getFiles(String name) {
        return new ArrayList<>(files.getOrDefault(name, Collections.emptyList()));
    }

    /**
     * 모든 파일의 맵을 반환합니다.
     *
     * 반환되는 맵은 방어적 복사본으로, 수정해도 원본에 영향을 주지 않습니다.
     *
     * @return 파일명 -> 파일 목록 맵
     */
    public Map<String, List<MultipartFile>> getFileMap() {
        Map<String, List<MultipartFile>> result = new HashMap<>();
        for (Map.Entry<String, List<MultipartFile>> entry : files.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    /**
     * 업로드된 파일이 있는지 확인합니다.
     *
     * @return 파일이 하나라도 있으면 true
     */
    public boolean hasFiles() {
        return !files.isEmpty() && files.values().stream()
                .anyMatch(fileList -> !fileList.isEmpty());
    }

    /**
     * 특정 이름의 파일이 있는지 확인합니다.
     *
     * @param name 파일 필드명
     * @return 해당 이름의 파일이 있으면 true
     */
    public boolean hasFile(String name) {
        List<MultipartFile> fileList = files.get(name);
        return fileList != null && !fileList.isEmpty();
    }

    /**
     * 업로드된 모든 파일 필드명을 반환합니다.
     *
     * @return 파일 필드명 집합
     */
    public Set<String> getFileNames() {
        return new HashSet<>(files.keySet());
    }

    /**
     * 총 업로드된 파일 개수를 반환합니다.
     *
     * @return 전체 파일 개수
     */
    public int getFileCount() {
        return files.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * 특정 필드의 파일 개수를 반환합니다.
     *
     * @param name 파일 필드명
     * @return 해당 필드의 파일 개수
     */
    public int getFileCount(String name) {
        List<MultipartFile> fileList = files.get(name);
        return fileList != null ? fileList.size() : 0;
    }

    /**
     * 업로드된 모든 파일의 총 크기를 반환합니다.
     *
     * @return 총 파일 크기 (바이트)
     */
    public long getTotalFileSize() {
        return files.values().stream()
                .flatMap(List::stream)
                .mapToLong(MultipartFile::getSize)
                .sum();
    }

    @Override
    public String toString() {
        return "MultipartRequest{" +
                "method='" + getMethod() + '\'' +
                ", path='" + getPath() + '\'' +
                ", fileCount=" + getFileCount() +
                ", totalFileSize=" + getTotalFileSize() + " bytes" +
                ", fileNames=" + getFileNames() +
                '}';
    }
}