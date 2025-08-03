package winter.upload;

import winter.http.HttpRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HTTP Multipart 요청을 파싱하는 유틸리티 클래스
 *
 * RFC 2388 Multipart 표준에 따라 multipart/form-data를 처리합니다.
 * boundary를 기준으로 각 part를 분리하고, 파일과 일반 파라미터를 구분합니다.
 */
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
     * @throws IOException 파싱 실패시
     * @throws IllegalArgumentException multipart 요청이 아니거나 boundary가 없을 때
     */
    public static MultipartRequest parseRequest(HttpRequest request) throws IOException {
        // Content-Type 검증
        String contentType = request.getHeader("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
            throw new IllegalArgumentException("Request is not multipart/form-data");
        }

        // boundary 추출
        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new IllegalArgumentException("No boundary found in Content-Type");
        }

        // 요청 본문 읽기
        String body = readRequestBody(request);

        // 파트 분리 및 파싱
        Map<String, List<String>> parameters = new HashMap<>(request.getParameterMap());
        Map<String, List<MultipartFile>> files = new HashMap<>();

        parseParts(body, boundary, parameters, files);

        return new MultipartRequest(
                request.getPath(),
                request.getMethod(),
                request.getHeaders(),
                parameters,
                request.getBody(),
                files
        );
    }

    /**
     * Content-Type 헤더에서 boundary를 추출합니다.
     *
     * 예: "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"
     *
     * @param contentType Content-Type 헤더값
     * @return boundary 문자열, 없으면 null
     */
    private static String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                String boundary = part.substring("boundary=".length()).trim();
                // 따옴표 제거
                if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
                return boundary;
            }
        }
        return null;
    }

    /**
     * 요청 본문을 문자열로 읽습니다.
     *
     * @param request HTTP 요청
     * @return 요청 본문 문자열
     * @throws IOException 읽기 실패시
     */
    private static String readRequestBody(HttpRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getBody();
        String line;

        // 첫 번째 줄은 CRLF 없이 읽기
        boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) {
                sb.append(CRLF);
            }
            sb.append(line);
            first = false;
        }

        return sb.toString();
    }

    /**
     * boundary를 기준으로 파트를 분리하고 파싱합니다.
     *
     * @param body 요청 본문
     * @param boundary boundary 문자열
     * @param parameters 일반 파라미터 맵 (출력)
     * @param files 파일 맵 (출력)
     */
    private static void parseParts(String body, String boundary,
                                   Map<String, List<String>> parameters,
                                   Map<String, List<MultipartFile>> files) {

        String delimiter = BOUNDARY_PREFIX + boundary;
        String endDelimiter = delimiter + BOUNDARY_PREFIX;

        // 시작과 끝 boundary 제거
        int startIndex = body.indexOf(delimiter);
        int endIndex = body.lastIndexOf(endDelimiter);

        if (startIndex == -1) {
            return; // boundary를 찾을 수 없음
        }

        String content = body.substring(startIndex + delimiter.length(),
                endIndex != -1 ? endIndex : body.length());

        // 각 파트 분리
        String[] parts = content.split(delimiter);

        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                parsePart(part, parameters, files);
            }
        }
    }

    /**
     * 개별 파트를 파싱합니다.
     *
     * @param part 파트 내용
     * @param parameters 일반 파라미터 맵 (출력)
     * @param files 파일 맵 (출력)
     */
    private static void parsePart(String part,
                                  Map<String, List<String>> parameters,
                                  Map<String, List<MultipartFile>> files) {

        // 헤더와 바디 분리
        int headerEndIndex = part.indexOf(CRLF + CRLF);
        if (headerEndIndex == -1) {
            return; // 올바르지 않은 파트 형식
        }

        String headerSection = part.substring(0, headerEndIndex);
        String bodySection = part.substring(headerEndIndex + (CRLF + CRLF).length());

        // 헤더 파싱
        Map<String, String> headers = parseHeaders(headerSection);

        // Content-Disposition 파싱
        String contentDisposition = headers.get(CONTENT_DISPOSITION.toLowerCase());
        if (contentDisposition == null || !contentDisposition.toLowerCase().contains(FORM_DATA)) {
            return;
        }

        String name = extractAttribute(contentDisposition, NAME);
        String filename = extractAttribute(contentDisposition, FILENAME);

        if (name == null) {
            return;
        }

        // 파일 vs 일반 파라미터 구분
        if (filename != null) {
            // 파일 파라미터
            String contentType = headers.get(CONTENT_TYPE.toLowerCase());
            byte[] fileContent = bodySection.getBytes(StandardCharsets.UTF_8);

            MultipartFile multipartFile = new StandardMultipartFile(
                    name, filename, contentType, fileContent
            );

            files.computeIfAbsent(name, k -> new ArrayList<>()).add(multipartFile);
        } else {
            // 일반 파라미터
            parameters.computeIfAbsent(name, k -> new ArrayList<>()).add(bodySection);
        }
    }

    /**
     * 헤더 섹션을 파싱하여 맵으로 변환합니다.
     *
     * @param headerSection 헤더 섹션 문자열
     * @return 헤더명(소문자) -> 헤더값 맵
     */
    private static Map<String, String> parseHeaders(String headerSection) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = headerSection.split(CRLF);

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim().toLowerCase();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        return headers;
    }

    /**
     * Content-Disposition에서 특정 속성값을 추출합니다.
     *
     * 예: 'form-data; name="file"; filename="test.txt"'에서
     * extractAttribute(contentDisposition, "name") → "file"
     *
     * @param contentDisposition Content-Disposition 헤더값
     * @param attributeName 추출할 속성명
     * @return 속성값, 없으면 null
     */
    private static String extractAttribute(String contentDisposition, String attributeName) {
        // name="value" 또는 name=value 형태 찾기
        String pattern1 = attributeName + "=\"";
        String pattern2 = attributeName + "=";

        int startIndex = contentDisposition.indexOf(pattern1);
        if (startIndex != -1) {
            // name="value" 형태
            startIndex += pattern1.length();
            int endIndex = contentDisposition.indexOf('"', startIndex);
            if (endIndex != -1) {
                return contentDisposition.substring(startIndex, endIndex);
            }
        } else {
            // name=value 형태 (따옴표 없음)
            startIndex = contentDisposition.indexOf(pattern2);
            if (startIndex != -1) {
                startIndex += pattern2.length();
                int endIndex = contentDisposition.indexOf(';', startIndex);
                if (endIndex == -1) {
                    endIndex = contentDisposition.length();
                }
                return contentDisposition.substring(startIndex, endIndex).trim();
            }
        }

        return null;
    }
}