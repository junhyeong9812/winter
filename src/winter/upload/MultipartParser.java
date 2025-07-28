package winter.upload;

import winter.http.HttpRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP Multipart 요청을 파싱하는 유틸리티 클래스
 *
 * RFC 2388 Multipart 표준에 따라 multipart/form-data를 처리합니다.
 * boundary를 기준으로 각 part를 분리하고, 파일과 일반 파라미터를 구분합니다.
 * */
public class MultipartParser {

    private static final String BOUNDARY_PREFIX = "--";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_DATA = "form-data";
    private static final String FILENAME = "filename";
    private static final String NAME = "name";
    private static final String CRLF = "\r\n";

    /**
     * HttpRequest를 MultipartRequest로 파싱합니다.
     *
     * multipart/form-data 요청을 파싱하여 파일과 일반 파라미터를 분리합니다.
     *
     * @param request 원본 HTTP 요청
     * @return 파싱된 Multipart 요청
     * @throws IOException 파싱 실패 시
     * @throws IllegalArgumentException multipart 요청이 아니거나 boundary가 없을 때
     * */
    public static MultipartRequest parseRequest(HttpRequest request) throws IOException{
        //Content-Type 검증
        String contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")){
            throw new IllegalArgumentException("Request is not multipart/form-data");
        }
        
        // boundary 추출
        String boundary = extractBoundary(contentType);
        if(boundary == null) {
            throw new IllegalArgumentException("No boundary found in Content-Type");
        }

        //요청 본문 찾기
        String body = readRequestBody(request);

        //파트 분리 및 파싱
        Map<String, List<String>> parameters = new HashMap<>(request.getParameter());
        Map<String, List<MultipartFile>> files = new HashMap<>();

        parseParts(body,boundary, parameters, files);

        return new MultipartRequest(
                request.getMethod(),
                request.getPath(),
                request.getHeaders(),
                parameters,
                request.getBody(),
                files
        );
        
        
    }

}
