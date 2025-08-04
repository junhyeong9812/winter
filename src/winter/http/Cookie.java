package winter.http;

/**
 * HTTP 쿠키를 표현하는 클래스
 * RFC 6265 표준을 준수합니다.
 * */
public class Cookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private int maxAge = -1;        // -1: 세션 쿠키, 0: 즉시 삭제, >0: 초 단위 수명
    private boolean httpOnly = false; // XSS 방지
    private boolean secure = false;   // HTTPS 전용
    private String sameSite;         // CSRF 방지 (Strict, Lax, None)
    
    /**
     * 쿠키 생성자
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * */
}
