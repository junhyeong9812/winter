package winter.http;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

/**
 * HTTP 요청 정보를 표현하는 클래스
 *
 * 경로, 쿼리 파라미터, HTTP 메서드, 헤더, 요청 본문을 포함합니다.
 * Multipart 요청 처리를 위해 기존 구조를 확장했습니다.
 * 25단계: 세션 관리 기능 추가
 *
 * 주요 변경사항:
 * 1. parameters를 Map<String, List<String>>으로 변경 (다중값 지원)
 * 2. 요청 본문 처리를 위한 BufferedReader 추가
 * 3. 새로운 생성자와 메서드 추가
 * 4. 세션 접근 및 쿠키 파싱 기능 추가
 */
public class HttpRequest {
    private final String path;
    private final String method;
    private final Map<String, List<String>> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final BufferedReader body;

    // 25단계: 세션 관련 필드 추가
    private final Map<String, Cookie> cookies = new HashMap<>();
    private HttpSession session;

    /**
     * 기본 생성자 (GET 요청 전용)
     */
    public HttpRequest(String rawPath) {
        this(rawPath, "GET");
    }

    /**
     * 생성자 - 요청 경로 및 HTTP 메서드를 지정
     *
     * @param rawPath "/register?name=jun&tags=java&tags=spring"
     * @param method "GET", "POST", ...
     */
    public HttpRequest(String rawPath, String method) {
        this(rawPath, method, new HashMap<>(), new HashMap<>(), null);
    }

    /**
     * 완전한 생성자 - 모든 요청 정보를 지정
     *
     * @param rawPath 요청 경로 (쿼리 파라미터 포함 가능)
     * @param method HTTP 메서드
     * @param headers HTTP 헤더 맵
     * @param parameters 파라미터 맵
     * @param body 요청 본문 BufferedReader
     */
    public HttpRequest(String rawPath, String method, Map<String, String> headers,
                       Map<String, List<String>> parameters, BufferedReader body) {
        this.method = method != null ? method.toUpperCase() : "GET";
        this.body = body != null ? body : new BufferedReader(new StringReader(""));

        // 기존 헤더 복사
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.headers.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }

        // 기존 파라미터 복사
        if (parameters != null) {
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                this.parameters.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }

        // 경로에서 쿼리 파라미터 파싱
        String[] parts = rawPath.split("\\?", 2);
        this.path = parts[0];

        if (parts.length > 1) {
            parseQueryString(parts[1]);
        }

        // 25단계: 쿠키 파싱
        parseCookies();
    }

    /**
     * 쿼리 문자열을 파싱하여 파라미터 맵에 추가
     */
    private void parseQueryString(String queryString) {
        if (queryString == null || queryString.trim().isEmpty()) {
            return;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length >= 1) {
                String key = urlDecode(kv[0]);
                String value = kv.length == 2 ? urlDecode(kv[1]) : "";
                addParameter(key, value);
            }
        }
    }

    /**
     * 25단계: 쿠키 헤더를 파싱하여 쿠키 맵에 추가
     */
    private void parseCookies() {
        String cookieHeader = getHeader("Cookie");
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return;
        }

        String[] cookiePairs = cookieHeader.split(";");
        for (String cookiePair : cookiePairs) {
            String[] parts = cookiePair.trim().split("=", 2);
            if (parts.length == 2) {
                String name = parts[0].trim();
                String value = parts[1].trim();
                cookies.put(name, new Cookie(name, value));
            }
        }
    }

    /**
     * 간단한 URL 디코딩 (+ → 공백, %20 → 공백 등)
     */
    private String urlDecode(String value) {
        if (value == null) return "";
        return value.replace("+", " ").replace("%20", " ");
    }

    /**
     * 순수 요청 경로 반환 (예: "/register")
     */
    public String getPath() {
        return path;
    }

    /**
     * HTTP 메서드 반환 (예: GET, POST)
     */
    public String getMethod() {
        return method;
    }

    /**
     * 요청 본문 BufferedReader 반환
     */
    public BufferedReader getBody() {
        return body;
    }

    /**
     * 개별 파라미터의 첫 번째 값 조회
     *
     * @param key 파라미터 이름
     * @return 첫 번째 값, 없으면 null
     */
    public String getParameter(String key) {
        List<String> values = parameters.get(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * 개별 파라미터의 모든 값 조회
     *
     * @param key 파라미터 이름
     * @return 값 목록, 없으면 빈 리스트
     */
    public List<String> getParameterValues(String key) {
        return new ArrayList<>(parameters.getOrDefault(key, Collections.emptyList()));
    }

    /**
     * 전체 파라미터 Map 반환 (하위 호환성을 위해 첫 번째 값만)
     *
     * @deprecated getParameters() 대신 getParameterMap() 사용 권장
     */
    @Deprecated
    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            List<String> values = entry.getValue();
            if (!values.isEmpty()) {
                result.put(entry.getKey(), values.get(0));
            }
        }
        return result;
    }

    /**
     * 전체 파라미터 Map 반환 (다중값 지원)
     */
    public Map<String, List<String>> getParameterMap() {
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    /**
     * 파라미터 추가 (기존 값에 추가)
     */
    public void addParameter(String key, String value) {
        parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * 파라미터 설정 (기존 값 덮어쓰기)
     */
    public void setParameter(String key, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        parameters.put(key, values);
    }

    /**
     * 파라미터 설정 (다중값)
     */
    public void setParameterValues(String key, List<String> values) {
        parameters.put(key, new ArrayList<>(values));
    }

    /**
     * 파라미터 존재 여부 확인
     */
    public boolean hasParameter(String key) {
        return parameters.containsKey(key) && !parameters.get(key).isEmpty();
    }

    /**
     * 모든 파라미터 이름 반환
     */
    public Set<String> getParameterNames() {
        return new HashSet<>(parameters.keySet());
    }

    /**
     * 헤더 추가
     */
    public void addHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    /**
     * 헤더 조회
     */
    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    /**
     * 전체 헤더 Map 반환
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    /**
     * 헤더 존재 여부 확인
     */
    public boolean hasHeader(String key) {
        return headers.containsKey(key.toLowerCase());
    }

    /**
     * Content-Type 헤더 편의 메서드
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }

    /**
     * Content-Length 헤더 편의 메서드
     */
    public long getContentLength() {
        String contentLength = getHeader("Content-Length");
        if (contentLength != null) {
            try {
                return Long.parseLong(contentLength);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 요청이 특정 Content-Type인지 확인
     */
    public boolean isContentType(String contentType) {
        String actualContentType = getContentType();
        return actualContentType != null &&
                actualContentType.toLowerCase().startsWith(contentType.toLowerCase());
    }

    /**
     * POST 요청인지 확인
     */
    public boolean isPost() {
        return "POST".equals(method);
    }

    /**
     * GET 요청인지 확인
     */
    public boolean isGet() {
        return "GET".equals(method);
    }

    // ===== 25단계: 세션 관련 메서드 추가 =====

    /**
     * 현재 세션을 반환합니다. 세션이 없으면 null을 반환합니다.
     *
     * @return 현재 HttpSession 또는 null
     */
    public HttpSession getSession() {
        return getSession(true);
    }

    /**
     * 현재 세션을 반환합니다.
     *
     * @param create true이면 세션이 없을 때 새로 생성, false이면 기존 세션만 반환
     * @return HttpSession 또는 null
     */
    public HttpSession getSession(boolean create) {
        // 이미 세션이 설정되어 있으면 반환
        if (session != null) {
            return session;
        }

        // 외부 SessionManager에 의해 설정될 예정 (Dispatcher에서 처리)
        // 여기서는 기본 동작만 제공
        return null;
    }

    /**
     * 세션을 설정합니다. (내부적으로 SessionManager가 호출)
     *
     * @param session 설정할 HttpSession
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * 요청된 세션 ID를 반환합니다.
     *
     * @return 쿠키에서 찾은 세션 ID 또는 null
     */
    public String getRequestedSessionId() {
        Cookie sessionCookie = getCookie("JSESSIONID");
        return sessionCookie != null ? sessionCookie.getValue() : null;
    }

    /**
     * 요청된 세션 ID가 유효한지 확인합니다.
     *
     * @return 세션이 존재하고 유효하면 true
     */
    public boolean isRequestedSessionIdValid() {
        return session != null && getRequestedSessionId() != null &&
                getRequestedSessionId().equals(session.getId());
    }

    /**
     * 쿠키를 조회합니다.
     *
     * @param name 쿠키 이름
     * @return Cookie 객체 또는 null
     */
    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    /**
     * 모든 쿠키를 반환합니다.
     *
     * @return Cookie 배열 (없으면 빈 배열)
     */
    public Cookie[] getCookies() {
        return cookies.values().toArray(new Cookie[0]);
    }

    /**
     * 특정 이름의 쿠키 값을 반환합니다.
     *
     * @param name 쿠키 이름
     * @return 쿠키 값 또는 null
     */
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 쿠키가 존재하는지 확인합니다.
     *
     * @param name 쿠키 이름
     * @return 존재하면 true
     */
    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", parameters=" + parameters.size() + " entries" +
                ", headers=" + headers.size() + " entries" +
                ", cookies=" + cookies.size() + " entries" +
                ", sessionId='" + (session != null ? session.getId() : "none") + '\'' +
                ", contentType='" + getContentType() + '\'' +
                '}';
    }
}