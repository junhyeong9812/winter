package winter.http;

/**
 * HTTP 쿠키를 표현하는 클래스
 * RFC 6265 표준을 준수합니다.
 * 세션 관리를 위한 핵심 구성요소로 사용됩니다.
 */
public class Cookie {

    // 쿠키의 이름 (필수값, 공백 불허)
    private String name;

    // 쿠키의 값 (null 허용, null인 경우 빈 문자열로 처리)
    private String value;

    // 쿠키가 유효한 경로 (예: "/", "/admin")
    private String path;

    // 쿠키가 유효한 도메인 (예: ".example.com")
    private String domain;

    // 쿠키 수명(초 단위): -1=세션쿠키(브라우저 종료시 삭제), 0=즉시삭제, >0=초단위 수명
    private int maxAge = -1;

    // XSS 방지를 위한 설정 - JavaScript에서 접근 차단
    private boolean httpOnly = false;

    // HTTPS에서만 전송되도록 하는 보안 설정
    private boolean secure = false;

    // CSRF 방지 설정 (Strict: 동일사이트만, Lax: 일부허용, None: 모두허용)
    private String sameSite;

    /**
     * 쿠키 생성자
     * @param name 쿠키 이름 (필수, null이나 공백 불허)
     * @param value 쿠키 값 (null 허용)
     */
    public Cookie(String name, String value) {
        // 쿠키 이름 유효성 검증 - RFC 6265 표준에 따라 이름은 필수
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cookie name cannot be null or empty");
        }

        // 이름에서 앞뒤 공백 제거하여 저장
        this.name = name.trim();

        // 값이 null인 경우 빈 문자열로 처리 (방어적 프로그래밍)
        this.value = value != null ? value : "";
    }

    // === Getter 메서드들 - 각 필드의 값을 반환 ===

    /**
     * 쿠키 이름 반환
     * @return 쿠키 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 쿠키 값 반환
     * @return 쿠키 값
     */
    public String getValue() {
        return value;
    }

    /**
     * 쿠키 경로 반환
     * @return 쿠키가 유효한 경로
     */
    public String getPath() {
        return path;
    }

    /**
     * 쿠키 도메인 반환
     * @return 쿠키가 유효한 도메인
     */
    public String getDomain() {
        return domain;
    }

    /**
     * 쿠키 최대 수명 반환
     * @return 초 단위 수명 (-1: 세션, 0: 즉시삭제, >0: 초)
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * HttpOnly 속성 확인
     * @return true면 JavaScript 접근 차단
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Secure 속성 확인
     * @return true면 HTTPS에서만 전송
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * SameSite 속성 반환
     * @return SameSite 값 (Strict/Lax/None)
     */
    public String getSameSite() {
        return sameSite;
    }

    // === Setter 메서드들 - 체이닝 패턴으로 구현 ===

    /**
     * 쿠키 값 설정 (체이닝 지원)
     * @param value 설정할 값
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setValue(String value) {
        // null 값을 빈 문자열로 변환하여 저장
        this.value = value != null ? value : "";
        return this; // 메서드 체이닝을 위해 자기 자신 반환
    }

    /**
     * 쿠키 경로 설정 (체이닝 지원)
     * @param path 설정할 경로
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setPath(String path) {
        this.path = path;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 쿠키 도메인 설정 (체이닝 지원)
     * @param domain 설정할 도메인
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setDomain(String domain) {
        this.domain = domain;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 쿠키 최대 수명 설정 (체이닝 지원)
     * @param maxAge 초 단위 수명
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this; // 메서드 체이닝 지원
    }

    /**
     * HttpOnly 속성 설정 (체이닝 지원)
     * @param httpOnly true면 JavaScript 접근 차단
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this; // 메서드 체이닝 지원
    }

    /**
     * Secure 속성 설정 (체이닝 지원)
     * @param secure true면 HTTPS에서만 전송
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setSecure(boolean secure) {
        this.secure = secure;
        return this; // 메서드 체이닝 지원
    }

    /**
     * SameSite 속성 설정 (체이닝 지원)
     * @param sameSite CSRF 방지를 위한 SameSite 값
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setSameSite(String sameSite) {
        // RFC 6265bis 표준에 따른 유효한 SameSite 값 검증
        if (sameSite != null &&
                !sameSite.equalsIgnoreCase("Strict") &&  // 동일 사이트에서만 전송
                !sameSite.equalsIgnoreCase("Lax") &&     // 일부 크로스 사이트 요청 허용
                !sameSite.equalsIgnoreCase("None")) {    // 모든 요청에서 전송 (Secure 필요)
            throw new IllegalArgumentException("Invalid SameSite value: " + sameSite);
        }
        this.sameSite = sameSite;
        return this; // 메서드 체이닝 지원
    }

    // === 편의 메서드들 - 자주 사용되는 설정의 단축 메서드 ===

    /**
     * 세션 쿠키로 설정 (브라우저 종료시 삭제)
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie makeSessionCookie() {
        this.maxAge = -1; // -1로 설정하면 세션 쿠키가 됨
        return this;
    }

    /**
     * 즉시 삭제되는 쿠키로 설정 (쿠키 삭제용)
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie makeExpired() {
        this.maxAge = 0; // 0으로 설정하면 브라우저가 즉시 삭제
        return this;
    }

    /**
     * 쿠키 수명을 일 단위로 설정하는 편의 메서드
     * @param days 일 수
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setMaxAgeDays(int days) {
        // 일을 초로 변환: 일 * 24시간 * 60분 * 60초
        this.maxAge = days * 24 * 60 * 60;
        return this;
    }

    /**
     * 쿠키 수명을 시간 단위로 설정하는 편의 메서드
     * @param hours 시간 수
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie setMaxAgeHours(int hours) {
        // 시간을 초로 변환: 시간 * 60분 * 60초
        this.maxAge = hours * 60 * 60;
        return this;
    }

    /**
     * 보안 쿠키로 설정하는 편의 메서드
     * HTTPS + HttpOnly + SameSite=Strict를 모두 활성화
     * @return 현재 Cookie 객체 (체이닝용)
     */
    public Cookie makeSecure() {
        this.secure = true;      // HTTPS에서만 전송
        this.httpOnly = true;    // JavaScript 접근 차단
        this.sameSite = "Strict"; // 동일 사이트에서만 전송
        return this;
    }

    // === 상태 확인 메서드들 ===

    /**
     * 쿠키가 만료되었는지 확인
     * @return true면 즉시 삭제 대상 쿠키
     */
    public boolean isExpired() {
        return maxAge == 0; // maxAge가 0이면 만료된 쿠키
    }

    /**
     * 쿠키가 세션 쿠키인지 확인
     * @return true면 브라우저 종료시 삭제되는 세션 쿠키
     */
    public boolean isSessionCookie() {
        return maxAge == -1; // maxAge가 -1이면 세션 쿠키
    }

    // === Object 클래스 오버라이드 메서드들 ===

    /**
     * 쿠키 정보를 문자열로 출력 (디버깅용)
     * @return 쿠키의 모든 속성을 포함한 문자열
     */
    @Override
    public String toString() {
        // 모든 쿠키 속성을 포함한 가독성 좋은 문자열 반환
        return String.format("Cookie{name='%s', value='%s', path='%s', domain='%s', " +
                        "maxAge=%d, httpOnly=%b, secure=%b, sameSite='%s'}",
                name, value, path, domain, maxAge, httpOnly, secure, sameSite);
    }

    /**
     * 쿠키 동등성 비교
     * RFC 6265에 따라 name, path, domain이 같으면 동일한 쿠키로 간주
     * @param obj 비교할 객체
     * @return 동일한 쿠키면 true
     */
    @Override
    public boolean equals(Object obj) {
        // 동일한 객체 참조인지 확인 (성능 최적화)
        if (this == obj) return true;

        // null이거나 다른 클래스면 false
        if (obj == null || getClass() != obj.getClass()) return false;

        Cookie cookie = (Cookie) obj;

        // RFC 6265에 따라 name, path, domain이 모두 같아야 동일한 쿠키
        return name.equals(cookie.name) &&  // name은 필수값이므로 null 체크 불필요
                (path != null ? path.equals(cookie.path) : cookie.path == null) &&     // path null 체크
                (domain != null ? domain.equals(cookie.domain) : cookie.domain == null); // domain null 체크
    }

    /**
     * 해시코드 생성
     * equals()와 일관성을 유지하기 위해 name, path, domain 기반으로 생성
     * @return 해시코드
     */
    @Override
    public int hashCode() {
        // name을 기본으로 해시코드 시작 (name은 항상 존재)
        int result = name.hashCode();

        // path가 있으면 해시코드에 반영 (31은 소수로 충돌 최소화)
        result = 31 * result + (path != null ? path.hashCode() : 0);

        // domain이 있으면 해시코드에 반영
        result = 31 * result + (domain != null ? domain.hashCode() : 0);

        return result;
    }
}