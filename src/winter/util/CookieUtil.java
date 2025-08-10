package winter.util;

import winter.http.Cookie;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 쿠키 처리를 위한 유틸리티 클래스
 * RFC 6265 표준을 준수합니다.
 */
public class CookieUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Cookie 헤더 문자열을 파싱하여 Cookie 객체 리스트로 변환
     * @param cookieHeader "name1=value1; name2=value2" 형식의 문자열
     * @return Cookie 객체 리스트
     */
    public static List<Cookie> parseCookies(String cookieHeader) {
        List<Cookie> cookies = new ArrayList<>();

        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return cookies;
        }

        // 세미콜론으로 분리
        String[] cookiePairs = cookieHeader.split(";");

        for (String cookiePair : cookiePairs) {
            String trimmed = cookiePair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // 등호로 이름과 값 분리
            int equalIndex = trimmed.indexOf('=');
            if (equalIndex > 0) {
                String name = trimmed.substring(0, equalIndex).trim();
                String value = trimmed.substring(equalIndex + 1).trim();

                // 유효한 쿠키 이름인지 확인
                if (isValidCookieName(name)) {
                    // 값 디코딩
                    String decodedValue = decodeCookieValue(value);
                    cookies.add(new Cookie(name, decodedValue));
                }
            }
        }

        return cookies;
    }

    /**
     * Cookie 객체를 Set-Cookie 헤더 문자열로 변환
     * @param cookie Cookie 객체
     * @return Set-Cookie 헤더 문자열
     */
    public static String formatSetCookieHeader(Cookie cookie) {
        if (cookie == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // 이름=값
        sb.append(cookie.getName()).append("=").append(encodeCookieValue(cookie.getValue()));

        // Path 속성
        if (cookie.getPath() != null) {
            sb.append("; Path=").append(cookie.getPath());
        }

        // Domain 속성
        if (cookie.getDomain() != null) {
            sb.append("; Domain=").append(cookie.getDomain());
        }

        // Max-Age 속성
        if (cookie.getMaxAge() >= 0) {
            sb.append("; Max-Age=").append(cookie.getMaxAge());
        }

        // HttpOnly 속성
        if (cookie.isHttpOnly()) {
            sb.append("; HttpOnly");
        }

        // Secure 속성
        if (cookie.isSecure()) {
            sb.append("; Secure");
        }

        // SameSite 속성
        if (cookie.getSameSite() != null) {
            sb.append("; SameSite=").append(cookie.getSameSite());
        }

        return sb.toString();
    }

    /**
     * 쿠키 리스트에서 특정 이름의 쿠키 찾기
     * @param cookies 쿠키 리스트
     * @param name 찾을 쿠키 이름
     * @return 찾은 Cookie 객체, 없으면 null
     */
    public static Cookie findCookie(List<Cookie> cookies, String name) {
        if (cookies == null || name == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 유효한 쿠키 이름인지 확인
     * RFC 6265에 따른 검증
     * @param name 쿠키 이름
     * @return 유효하면 true
     */
    public static boolean isValidCookieName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // 제어 문자, 공백, 구분자 등이 포함되면 안됨
        for (char c : name.toCharArray()) {
            if (c <= 0x20 || c >= 0x7f ||
                    c == '(' || c == ')' || c == '<' || c == '>' || c == '@' ||
                    c == ',' || c == ';' || c == ':' || c == '\\' || c == '"' ||
                    c == '/' || c == '[' || c == ']' || c == '?' || c == '=' ||
                    c == '{' || c == '}') {
                return false;
            }
        }
        return true;
    }

    /**
     * 유효한 쿠키 값인지 확인
     * @param value 쿠키 값
     * @return 유효하면 true
     */
    public static boolean isValidCookieValue(String value) {
        if (value == null) {
            return true; // null 값은 허용
        }

        // 제어 문자, 공백, 세미콜론, 컴마, 백슬래시가 포함되면 안됨
        for (char c : value.toCharArray()) {
            if (c <= 0x20 || c >= 0x7f || c == '"' || c == ',' || c == ';' || c == '\\') {
                return false;
            }
        }
        return true;
    }

    /**
     * 쿠키 값을 URL 인코딩
     * @param value 원본 값
     * @return 인코딩된 값
     */
    public static String encodeCookieValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 이미 유효한 값이면 인코딩하지 않음
        if (isValidCookieValue(value)) {
            return value;
        }

        try {
            return URLEncoder.encode(value, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            // UTF-8은 항상 지원되므로 발생하지 않음
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * 쿠키 값을 URL 디코딩
     * @param value 인코딩된 값
     * @return 디코딩된 값
     */
    public static String decodeCookieValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // URL 인코딩된 것 같으면 디코딩 시도
        if (value.contains("%")) {
            try {
                return URLDecoder.decode(value, DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                // 디코딩 실패시 원본 반환
                return value;
            } catch (IllegalArgumentException e) {
                // 잘못된 인코딩 형식인 경우 원본 반환
                return value;
            }
        }

        return value;
    }

    /**
     * 쿠키 만료 시간을 현재 시간 기준으로 계산
     * @param maxAgeSeconds 수명 (초)
     * @return 만료 시간 (밀리초)
     */
    public static long calculateExpirationTime(int maxAgeSeconds) {
        if (maxAgeSeconds <= 0) {
            return 0; // 즉시 만료 또는 세션 쿠키
        }
        return System.currentTimeMillis() + (maxAgeSeconds * 1000L);
    }

    /**
     * 쿠키가 만료되었는지 확인
     * @param cookie 확인할 쿠키
     * @return 만료되었으면 true
     */
    public static boolean isCookieExpired(Cookie cookie) {
        if (cookie == null) {
            return true;
        }

        int maxAge = cookie.getMaxAge();
        if (maxAge < 0) {
            return false; // 세션 쿠키는 브라우저가 관리
        }
        if (maxAge == 0) {
            return true; // 즉시 만료
        }

        // 실제 만료 시간 계산은 쿠키 생성 시점이 필요하므로
        // 여기서는 Max-Age 값만 확인
        return false;
    }

    /**
     * 여러 Set-Cookie 헤더를 생성
     * @param cookies Cookie 리스트
     * @return Set-Cookie 헤더 문자열 배열
     */
    public static String[] formatSetCookieHeaders(List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return new String[0];
        }

        String[] headers = new String[cookies.size()];
        for (int i = 0; i < cookies.size(); i++) {
            headers[i] = formatSetCookieHeader(cookies.get(i));
        }
        return headers;
    }

    /**
     * 쿠키 이름 목록 추출
     * @param cookies 쿠키 리스트
     * @return 쿠키 이름 리스트
     */
    public static List<String> getCookieNames(List<Cookie> cookies) {
        List<String> names = new ArrayList<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                names.add(cookie.getName());
            }
        }
        return names;
    }

    /**
     * 디버깅용 쿠키 정보 문자열 생성
     * @param cookies 쿠키 리스트
     * @return 포매팅된 쿠키 정보
     */
    public static String cookiesToString(List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return "No cookies";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cookies (").append(cookies.size()).append("):\n");

        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            sb.append("  ").append(i + 1).append(". ")
                    .append(cookie.getName()).append("=").append(cookie.getValue());

            if (cookie.getPath() != null) {
                sb.append(" [Path=").append(cookie.getPath()).append("]");
            }
            if (cookie.getMaxAge() >= 0) {
                sb.append(" [MaxAge=").append(cookie.getMaxAge()).append("]");
            }
            if (cookie.isHttpOnly()) {
                sb.append(" [HttpOnly]");
            }
            if (cookie.isSecure()) {
                sb.append(" [Secure]");
            }
            if (cookie.getSameSite() != null) {
                sb.append(" [SameSite=").append(cookie.getSameSite()).append("]");
            }

            if (i < cookies.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
