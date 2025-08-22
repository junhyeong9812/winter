package winter.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpResponse 인터페이스의 표준 구현체 (29단계)
 *
 * ===== 기존 26단계 HttpResponse 클래스 코드를 완전히 이동 =====
 * - 모든 기능을 인터페이스 구현체로 변경
 * - 기존 코드와 100% 동일한 동작 보장
 * - 변경 사항 없이 완전 호환
 *
 * ===== 29단계 확장 사항 =====
 * - getHeader() 메서드 구현 추가
 * - ResponseEntity와의 호환성 개선
 * - 더 명확한 클래스 구조
 */
public class StandardHttpResponse implements HttpResponse {

    // ===== 26단계 기존 필드들 완전 유지 =====

    private int status = 200;
    private String body = "";
    private final Map<String, String> headers = new HashMap<>();

    // 25단계: 쿠키 관리 기능
    private final List<Cookie> cookies = new ArrayList<>();

    // 26단계: Writer 지원을 위한 필드들
    private PrintWriter writer; // 뷰 엔진이 스트림 방식으로 출력할 수 있도록 하는 Writer
    private StringWriter stringWriter; // 메모리에 문자열을 저장하는 Writer

    // ===== 26단계 생성자 완전 유지 =====

    /**
     * 생성자: Writer 초기화 (26단계와 완전 동일)
     */
    public StandardHttpResponse() {
        // StringWriter로 메모리에 출력 내용을 저장
        this.stringWriter = new StringWriter();
        // PrintWriter로 편리한 출력 메서드 제공
        this.writer = new PrintWriter(stringWriter);
    }

    // ===== 기본 HTTP 응답 메서드들 (기존과 완전 동일) =====

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }

    // ===== HTTP 헤더 관리 메서드들 =====

    @Override
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 29단계 신규: HTTP 헤더 조회 메서드
     * ResponseEntity에서 헤더 값을 확인할 때 사용
     *
     * @param name 헤더 이름
     * @return 헤더 값, 없으면 null
     */
    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    // ===== 26단계 Writer 지원 메서드들 (완전 유지) =====

    /**
     * 응답 본문을 작성하기 위한 PrintWriter 반환 (26단계와 완전 동일)
     * 뷰 엔진에서 템플릿 렌더링 결과를 스트림 방식으로 출력할 때 사용
     * @return PrintWriter 객체
     */
    @Override
    public PrintWriter getWriter() {
        return writer; // IntegratedView에서 response.getWriter()로 호출하는 메서드
    }

    /**
     * Writer에 작성된 내용을 body 필드로 설정 (26단계와 완전 동일)
     * Writer를 통해 출력된 내용을 최종 응답 본문으로 반영
     */
    @Override
    public void flushWriter() {
        if (writer != null) { // Writer가 null이 아닌 경우에만 실행
            writer.flush(); // 버퍼에 있는 내용을 StringWriter로 플러시
            this.body = stringWriter.toString(); // StringWriter의 내용을 body로 설정
        }
    }

    /**
     * Writer 내용을 초기화 (26단계와 완전 동일)
     * 새로운 응답을 위해 Writer 상태를 리셋할 때 사용
     */
    @Override
    public void resetWriter() {
        if (stringWriter != null) { // StringWriter가 null이 아닌 경우
            stringWriter.getBuffer().setLength(0); // StringWriter 버퍼 초기화
        }
    }

    // ===== 25단계 쿠키 관리 메서드들 (완전 유지) =====

    /**
     * 쿠키를 응답에 추가합니다. (25단계와 완전 동일)
     *
     * @param cookie 추가할 Cookie 객체
     */
    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            // 기존에 같은 이름의 쿠키가 있으면 제거
            cookies.removeIf(c -> c.getName().equals(cookie.getName()));
            cookies.add(cookie);
        }
    }

    /**
     * 간단한 쿠키를 생성하여 추가합니다. (25단계와 완전 동일)
     *
     * @param name 쿠키 이름
     * @param value 쿠키 값
     */
    @Override
    public void addCookie(String name, String value) {
        addCookie(new Cookie(name, value));
    }

    /**
     * 세션 쿠키를 설정합니다. (25단계와 완전 동일)
     *
     * @param sessionId 세션 ID
     * @param maxAge 쿠키 수명 (초), -1이면 브라우저 세션까지만
     * @param secure HTTPS에서만 전송할지 여부
     * @param httpOnly JavaScript 접근 차단 여부
     */
    @Override
    public void setSessionCookie(String sessionId, int maxAge, boolean secure, boolean httpOnly) {
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setMaxAge(maxAge);
        sessionCookie.setPath("/");
        sessionCookie.setSecure(secure);
        sessionCookie.setHttpOnly(httpOnly);
        addCookie(sessionCookie);
    }

    /**
     * 세션 쿠키를 기본 설정으로 설정합니다. (25단계와 완전 동일)
     *
     * @param sessionId 세션 ID
     */
    @Override
    public void setSessionCookie(String sessionId) {
        setSessionCookie(sessionId, -1, false, true);
    }

    /**
     * 세션 쿠키를 삭제합니다. (25단계와 완전 동일)
     */
    @Override
    public void deleteSessionCookie() {
        Cookie deleteCookie = new Cookie("JSESSIONID", "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        addCookie(deleteCookie);
    }

    /**
     * 쿠키를 삭제합니다. (25단계와 완전 동일)
     *
     * @param name 삭제할 쿠키 이름
     */
    @Override
    public void deleteCookie(String name) {
        Cookie deleteCookie = new Cookie(name, "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        addCookie(deleteCookie);
    }

    /**
     * 모든 쿠키를 반환합니다. (25단계와 완전 동일)
     *
     * @return Cookie 리스트
     */
    @Override
    public List<Cookie> getCookies() {
        return new ArrayList<>(cookies);
    }

    /**
     * 특정 이름의 쿠키를 찾습니다. (25단계와 완전 동일)
     *
     * @param name 쿠키 이름
     * @return Cookie 객체 또는 null
     */
    @Override
    public Cookie getCookie(String name) {
        return cookies.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 쿠키가 설정되었는지 확인합니다. (25단계와 완전 동일)
     *
     * @param name 쿠키 이름
     * @return 존재하면 true
     */
    @Override
    public boolean hasCookie(String name) {
        return getCookie(name) != null;
    }

    // ===== 편의 메서드들 (기존 기능 완전 유지) =====

    /**
     * Content-Type 헤더를 설정합니다. (기존과 완전 동일)
     *
     * @param contentType MIME 타입
     */
    @Override
    public void setContentType(String contentType) {
        addHeader("Content-Type", contentType);
    }

    /**
     * 캐시 제어 헤더를 설정합니다. (기존과 완전 동일)
     *
     * @param cacheControl 캐시 제어 값
     */
    @Override
    public void setCacheControl(String cacheControl) {
        addHeader("Cache-Control", cacheControl);
    }

    /**
     * JSON 응답으로 설정합니다. (기존과 완전 동일)
     */
    @Override
    public void setJsonResponse() {
        setContentType("application/json; charset=UTF-8");
    }

    /**
     * HTML 응답으로 설정합니다. (기존과 완전 동일)
     */
    @Override
    public void setHtmlResponse() {
        setContentType("text/html; charset=UTF-8");
    }

    /**
     * 플레인 텍스트 응답으로 설정합니다. (기존과 완전 동일)
     */
    @Override
    public void setTextResponse() {
        setContentType("text/plain; charset=UTF-8");
    }

    /**
     * 리다이렉트 응답을 설정합니다. (기존과 완전 동일)
     *
     * @param location 리다이렉트할 URL
     */
    @Override
    public void sendRedirect(String location) {
        setStatus(302);
        addHeader("Location", location);
        setBody("");
    }

    /**
     * 에러 응답을 설정합니다. (기존과 완전 동일)
     *
     * @param statusCode HTTP 상태 코드
     * @param message 에러 메시지
     */
    @Override
    public void sendError(int statusCode, String message) {
        setStatus(statusCode);
        setHtmlResponse();
        setBody("<html><body><h1>" + statusCode + " Error</h1><p>" + message + "</p></body></html>");
    }

    /**
     * 응답을 출력합니다. (26단계와 완전 동일: 쿠키 헤더 포함, Writer 내용 반영)
     */
    @Override
    public void send() {
        flushWriter(); // 26챕터: Writer에 작성된 내용을 body로 반영
        System.out.println(" HTTP Response ");
        System.out.println("status = " + status);

        // 일반 헤더 출력
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // 쿠키 헤더 출력
        for (Cookie cookie : cookies) {
            System.out.println("Set-Cookie: " + cookie.toHeaderString());
        }

        System.out.println("body = " + body);
    }

    /**
     * 실제 HTTP 응답 헤더 문자열을 생성합니다. (기존과 완전 동일)
     *
     * @return HTTP 응답 헤더 문자열
     */
    @Override
    public String toHttpString() {
        flushWriter(); // 26챕터: Writer 내용을 먼저 body로 반영

        StringBuilder response = new StringBuilder();

        // 상태 라인
        response.append("HTTP/1.1 ").append(status).append(" ")
                .append(getStatusMessage(status)).append("\r\n");

        // 일반 헤더
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        // 쿠키 헤더
        for (Cookie cookie : cookies) {
            response.append("Set-Cookie: ").append(cookie.toHeaderString()).append("\r\n");
        }

        // 헤더 종료
        response.append("\r\n");

        // 본문
        if (body != null && !body.isEmpty()) {
            response.append(body);
        }

        return response.toString();
    }

    /**
     * 상태 코드에 따른 상태 메시지를 반환합니다. (기존과 완전 동일)
     *
     * @param statusCode HTTP 상태 코드
     * @return 상태 메시지
     */
    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 201: return "Created";
            case 204: return "No Content";
            case 302: return "Found";
            case 304: return "Not Modified";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 500: return "Internal Server Error";
            case 502: return "Bad Gateway";
            case 503: return "Service Unavailable";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "StandardHttpResponse{" +
                "status=" + status +
                ", headers=" + headers.size() + " entries" +
                ", cookies=" + cookies.size() + " entries" +
                ", bodyLength=" + (body != null ? body.length() : 0) +
                '}';
    }
}