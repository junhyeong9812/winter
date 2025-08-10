package winter.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 응답 정보를 표현하는 클래스
 * 25단계: 쿠키 관리 및 세션 지원 기능 추가
 */
public class HttpResponse {
    private int status = 200;
    private String body = "";
    private final Map<String, String> headers = new HashMap<>();

    // 25단계: 쿠키 관리 기능 추가
    private final List<Cookie> cookies = new ArrayList<>();

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    // ===== 25단계: 쿠키 관리 메서드 추가 =====

    /**
     * 쿠키를 응답에 추가합니다.
     *
     * @param cookie 추가할 Cookie 객체
     */
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            // 기존에 같은 이름의 쿠키가 있으면 제거
            cookies.removeIf(c -> c.getName().equals(cookie.getName()));
            cookies.add(cookie);
        }
    }

    /**
     * 간단한 쿠키를 생성하여 추가합니다.
     *
     * @param name 쿠키 이름
     * @param value 쿠키 값
     */
    public void addCookie(String name, String value) {
        addCookie(new Cookie(name, value));
    }

    /**
     * 세션 쿠키를 설정합니다.
     *
     * @param sessionId 세션 ID
     * @param maxAge 쿠키 수명 (초), -1이면 브라우저 세션까지만
     * @param secure HTTPS에서만 전송할지 여부
     * @param httpOnly JavaScript 접근 차단 여부
     */
    public void setSessionCookie(String sessionId, int maxAge, boolean secure, boolean httpOnly) {
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setMaxAge(maxAge);
        sessionCookie.setPath("/");
        sessionCookie.setSecure(secure);
        sessionCookie.setHttpOnly(httpOnly);
        addCookie(sessionCookie);
    }

    /**
     * 세션 쿠키를 기본 설정으로 설정합니다.
     *
     * @param sessionId 세션 ID
     */
    public void setSessionCookie(String sessionId) {
        setSessionCookie(sessionId, -1, false, true);
    }

    /**
     * 세션 쿠키를 삭제합니다.
     */
    public void deleteSessionCookie() {
        Cookie deleteCookie = new Cookie("JSESSIONID", "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        addCookie(deleteCookie);
    }

    /**
     * 쿠키를 삭제합니다.
     *
     * @param name 삭제할 쿠키 이름
     */
    public void deleteCookie(String name) {
        Cookie deleteCookie = new Cookie(name, "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        addCookie(deleteCookie);
    }

    /**
     * 모든 쿠키를 반환합니다.
     *
     * @return Cookie 리스트
     */
    public List<Cookie> getCookies() {
        return new ArrayList<>(cookies);
    }

    /**
     * 특정 이름의 쿠키를 찾습니다.
     *
     * @param name 쿠키 이름
     * @return Cookie 객체 또는 null
     */
    public Cookie getCookie(String name) {
        return cookies.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 쿠키가 설정되었는지 확인합니다.
     *
     * @param name 쿠키 이름
     * @return 존재하면 true
     */
    public boolean hasCookie(String name) {
        return getCookie(name) != null;
    }

    /**
     * Content-Type 헤더를 설정합니다.
     *
     * @param contentType MIME 타입
     */
    public void setContentType(String contentType) {
        addHeader("Content-Type", contentType);
    }

    /**
     * 캐시 제어 헤더를 설정합니다.
     *
     * @param cacheControl 캐시 제어 값
     */
    public void setCacheControl(String cacheControl) {
        addHeader("Cache-Control", cacheControl);
    }

    /**
     * JSON 응답으로 설정합니다.
     */
    public void setJsonResponse() {
        setContentType("application/json; charset=UTF-8");
    }

    /**
     * HTML 응답으로 설정합니다.
     */
    public void setHtmlResponse() {
        setContentType("text/html; charset=UTF-8");
    }

    /**
     * 플레인 텍스트 응답으로 설정합니다.
     */
    public void setTextResponse() {
        setContentType("text/plain; charset=UTF-8");
    }

    /**
     * 리다이렉트 응답을 설정합니다.
     *
     * @param location 리다이렉트할 URL
     */
    public void sendRedirect(String location) {
        setStatus(302);
        addHeader("Location", location);
        setBody("");
    }

    /**
     * 에러 응답을 설정합니다.
     *
     * @param statusCode HTTP 상태 코드
     * @param message 에러 메시지
     */
    public void sendError(int statusCode, String message) {
        setStatus(statusCode);
        setHtmlResponse();
        setBody("<html><body><h1>" + statusCode + " Error</h1><p>" + message + "</p></body></html>");
    }

    /**
     * 응답을 출력합니다. (쿠키 헤더 포함)
     */
    public void send() {
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
     * 실제 HTTP 응답 헤더 문자열을 생성합니다.
     *
     * @return HTTP 응답 헤더 문자열
     */
    public String toHttpString() {
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
     * 상태 코드에 따른 상태 메시지를 반환합니다.
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
        return "HttpResponse{" +
                "status=" + status +
                ", headers=" + headers.size() + " entries" +
                ", cookies=" + cookies.size() + " entries" +
                ", bodyLength=" + (body != null ? body.length() : 0) +
                '}';
    }
}