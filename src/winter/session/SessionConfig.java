package winter.session;

/**
 * 세션 및 쿠키 관련 설정을 관리하는 클래스
 * Winter 프레임워크의 세션 관리 시스템 전반적인 동작을 제어합니다.
 */
public class SessionConfig {

    // === 세션 생명주기 설정 ===

    // 세션 비활성 상태 유지 시간 (초 단위) - 기본값 30분
    // 사용자가 마지막 요청 이후 이 시간이 지나면 세션이 만료됨
    private int maxInactiveInterval = 1800;

    // 만료된 세션 정리 주기 (초 단위) - 기본값 5분
    // 백그라운드에서 만료된 세션을 정리하는 스케줄러 실행 간격
    private int cleanupInterval = 300;

    // === 세션 쿠키 설정 ===

    // 세션 ID를 저장할 쿠키 이름 - 표준 Servlet 호환
    private String cookieName = "JSESSIONID";

    // 쿠키가 유효한 경로 - 루트 경로로 설정하여 모든 경로에서 접근 가능
    private String cookiePath = "/";

    // 쿠키가 유효한 도메인 - null이면 현재 도메인에만 적용
    private String cookieDomain = null;

    // JavaScript에서 쿠키 접근 차단 여부 - XSS 공격 방지
    private boolean cookieHttpOnly = true;

    // HTTPS에서만 쿠키 전송 여부 - 프로덕션에서는 true 권장
    private boolean cookieSecure = false;

    // CSRF 공격 방지를 위한 SameSite 정책 - Lax가 일반적으로 안전
    private String cookieSameSite = "Lax";

    // === 보안 설정 ===

    // 세션 고정 공격 방지 - 로그인 성공 시 새로운 세션 ID 생성
    private boolean sessionFixationProtection = true;

    // 로그아웃 시 기존 세션 완전 무효화 여부
    private boolean invalidateSessionOnLogout = true;

    // 사용자당 최대 동시 세션 수 제한 (-1: 제한 없음)
    // 중복 로그인 방지나 라이선스 관리에 사용
    private int maxSessionsPerUser = -1;

    /**
     * 기본 생성자
     * 모든 설정값을 기본값으로 초기화
     */
    public SessionConfig() {
        // 기본값들이 이미 필드 선언부에서 설정됨
    }

    // === Getter 메서드들 - 현재 설정값들을 반환 ===

    /**
     * 세션 최대 비활성 시간 반환
     * @return 초 단위 비활성 시간
     */
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /**
     * 세션 정리 주기 반환
     * @return 초 단위 정리 주기
     */
    public int getCleanupInterval() {
        return cleanupInterval;
    }

    /**
     * 세션 쿠키 이름 반환
     * @return 쿠키 이름 (기본값: "JSESSIONID")
     */
    public String getCookieName() {
        return cookieName;
    }

    /**
     * 세션 쿠키 경로 반환
     * @return 쿠키가 유효한 경로
     */
    public String getCookiePath() {
        return cookiePath;
    }

    /**
     * 세션 쿠키 도메인 반환
     * @return 쿠키가 유효한 도메인 (null이면 현재 도메인만)
     */
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * 쿠키 HttpOnly 속성 확인
     * @return true면 JavaScript 접근 차단 (XSS 방지)
     */
    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }

    /**
     * 쿠키 Secure 속성 확인
     * @return true면 HTTPS에서만 전송
     */
    public boolean isCookieSecure() {
        return cookieSecure;
    }

    /**
     * 쿠키 SameSite 속성 반환
     * @return SameSite 값 (Strict/Lax/None)
     */
    public String getCookieSameSite() {
        return cookieSameSite;
    }

    /**
     * 세션 고정 공격 방지 활성화 여부
     * @return true면 로그인 시 세션 ID 갱신
     */
    public boolean isSessionFixationProtection() {
        return sessionFixationProtection;
    }

    /**
     * 로그아웃 시 세션 무효화 여부
     * @return true면 로그아웃 시 세션 완전 삭제
     */
    public boolean isInvalidateSessionOnLogout() {
        return invalidateSessionOnLogout;
    }

    /**
     * 사용자당 최대 세션 수 반환
     * @return 최대 동시 세션 수 (-1: 제한 없음)
     */
    public int getMaxSessionsPerUser() {
        return maxSessionsPerUser;
    }

    // === Setter 메서드들 - 체이닝 패턴으로 구현 ===

    /**
     * 세션 최대 비활성 시간 설정 (초 단위)
     * @param maxInactiveInterval 초 단위 시간 (양수만 허용)
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setMaxInactiveInterval(int maxInactiveInterval) {
        // 음수나 0은 허용하지 않음 - 세션이 즉시 만료되거나 무한정 지속될 수 없음
        if (maxInactiveInterval <= 0) {
            throw new IllegalArgumentException("Max inactive interval must be positive");
        }
        this.maxInactiveInterval = maxInactiveInterval;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 세션 최대 비활성 시간을 분 단위로 설정하는 편의 메서드
     * @param minutes 분 단위 시간
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setMaxInactiveMinutes(int minutes) {
        // 분을 초로 변환하여 설정
        return setMaxInactiveInterval(minutes * 60);
    }

    /**
     * 세션 정리 주기 설정 (초 단위)
     * @param cleanupInterval 초 단위 정리 주기 (양수만 허용)
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCleanupInterval(int cleanupInterval) {
        // 음수나 0은 허용하지 않음 - 정리 작업이 실행되지 않으면 메모리 누수 발생
        if (cleanupInterval <= 0) {
            throw new IllegalArgumentException("Cleanup interval must be positive");
        }
        this.cleanupInterval = cleanupInterval;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 세션 정리 주기를 분 단위로 설정하는 편의 메서드
     * @param minutes 분 단위 정리 주기
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCleanupMinutes(int minutes) {
        // 분을 초로 변환하여 설정
        return setCleanupInterval(minutes * 60);
    }

    /**
     * 세션 쿠키 이름 설정
     * @param cookieName 쿠키 이름 (null이나 공백 불허)
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookieName(String cookieName) {
        // 쿠키 이름은 필수 - HTTP 표준에서 이름 없는 쿠키는 불가능
        if (cookieName == null || cookieName.trim().isEmpty()) {
            throw new IllegalArgumentException("Cookie name cannot be null or empty");
        }
        // 앞뒤 공백 제거하여 저장
        this.cookieName = cookieName.trim();
        return this; // 메서드 체이닝 지원
    }

    /**
     * 세션 쿠키 경로 설정
     * @param cookiePath 쿠키가 유효한 경로
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 세션 쿠키 도메인 설정
     * @param cookieDomain 쿠키가 유효한 도메인
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 쿠키 HttpOnly 속성 설정
     * @param cookieHttpOnly true면 JavaScript 접근 차단
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookieHttpOnly(boolean cookieHttpOnly) {
        this.cookieHttpOnly = cookieHttpOnly;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 쿠키 Secure 속성 설정
     * @param cookieSecure true면 HTTPS에서만 전송
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 쿠키 SameSite 속성 설정
     * @param cookieSameSite CSRF 방지를 위한 SameSite 값
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setCookieSameSite(String cookieSameSite) {
        // RFC 6265bis 표준에 따른 유효한 SameSite 값 검증
        if (cookieSameSite != null &&
                !cookieSameSite.equalsIgnoreCase("Strict") &&  // 동일 사이트에서만 전송
                !cookieSameSite.equalsIgnoreCase("Lax") &&     // 일부 크로스 사이트 요청 허용
                !cookieSameSite.equalsIgnoreCase("None")) {    // 모든 요청에서 전송 (Secure 필요)
            throw new IllegalArgumentException("Invalid SameSite value: " + cookieSameSite);
        }
        this.cookieSameSite = cookieSameSite;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 세션 고정 공격 방지 설정
     * @param sessionFixationProtection true면 로그인 시 세션 ID 갱신
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setSessionFixationProtection(boolean sessionFixationProtection) {
        this.sessionFixationProtection = sessionFixationProtection;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 로그아웃 시 세션 무효화 설정
     * @param invalidateSessionOnLogout true면 로그아웃 시 세션 완전 삭제
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setInvalidateSessionOnLogout(boolean invalidateSessionOnLogout) {
        this.invalidateSessionOnLogout = invalidateSessionOnLogout;
        return this; // 메서드 체이닝 지원
    }

    /**
     * 사용자당 최대 세션 수 설정
     * @param maxSessionsPerUser 최대 동시 세션 수 (-1: 제한 없음)
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setMaxSessionsPerUser(int maxSessionsPerUser) {
        this.maxSessionsPerUser = maxSessionsPerUser;
        return this; // 메서드 체이닝 지원
    }

    // === 편의 메서드들 - 자주 사용되는 설정 조합 ===

    /**
     * 보안 쿠키 설정 활성화
     * Secure와 SameSite=Strict를 동시에 설정
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig enableSecureCookies() {
        this.cookieSecure = true;      // HTTPS에서만 전송
        this.cookieSameSite = "Strict"; // 동일 사이트에서만 전송
        return this;
    }

    /**
     * 프로덕션 환경용 보안 설정 활성화
     * 모든 보안 옵션을 최고 수준으로 설정
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig enableProductionSecurity() {
        this.cookieHttpOnly = true;              // JavaScript 접근 차단
        this.cookieSecure = true;                // HTTPS에서만 전송
        this.cookieSameSite = "Strict";          // 동일 사이트에서만 전송
        this.sessionFixationProtection = true;   // 세션 고정 공격 방지
        return this;
    }

    /**
     * 개발 환경용 설정 활성화
     * HTTP 환경에서도 동작하도록 보안 수준을 낮춤
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig enableDevelopmentMode() {
        this.cookieSecure = false;     // HTTP에서도 전송 가능
        this.cookieSameSite = "Lax";   // 일부 크로스 사이트 요청 허용
        return this;
    }

    /**
     * 짧은 세션 설정 (5분)
     * 보안이 중요한 관리자 페이지나 결제 과정에서 사용
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setShortSession() {
        return setMaxInactiveMinutes(5);  // 5분으로 설정
    }

    /**
     * 긴 세션 설정 (2시간)
     * 사용자 편의성이 중요한 일반 웹사이트에서 사용
     * @return 현재 SessionConfig 객체 (체이닝용)
     */
    public SessionConfig setLongSession() {
        return setMaxInactiveMinutes(120); // 2시간으로 설정
    }

    /**
     * 설정 유효성 검증
     * 설정값들이 논리적으로 올바른지 확인
     * SessionManager 초기화 전에 호출되어야 함
     */
    public void validate() {
        // 세션 비활성 시간이 양수인지 확인
        if (maxInactiveInterval <= 0) {
            throw new IllegalStateException("Max inactive interval must be positive");
        }

        // 세션 정리 주기가 양수인지 확인
        if (cleanupInterval <= 0) {
            throw new IllegalStateException("Cleanup interval must be positive");
        }

        // 쿠키 이름이 유효한지 확인
        if (cookieName == null || cookieName.trim().isEmpty()) {
            throw new IllegalStateException("Cookie name cannot be null or empty");
        }

        // SameSite=None인 경우 Secure=true여야 함 (브라우저 정책)
        if (cookieSecure && "None".equalsIgnoreCase(cookieSameSite)) {
            // 이미 secure가 true이므로 문제없음
            // 참고: SameSite=None은 반드시 Secure=true와 함께 사용해야 함
        }
    }

    /**
     * 설정 정보를 문자열로 출력 (디버깅용)
     * @return 모든 설정값을 포함한 가독성 좋은 문자열
     */
    @Override
    public String toString() {
        // 모든 설정값을 포함한 상세한 문자열 반환
        return String.format("SessionConfig{" +
                        "maxInactiveInterval=%d, cleanupInterval=%d, " +
                        "cookieName='%s', cookiePath='%s', cookieDomain='%s', " +
                        "cookieHttpOnly=%b, cookieSecure=%b, cookieSameSite='%s', " +
                        "sessionFixationProtection=%b, invalidateSessionOnLogout=%b, " +
                        "maxSessionsPerUser=%d}",
                maxInactiveInterval, cleanupInterval,
                cookieName, cookiePath, cookieDomain,
                cookieHttpOnly, cookieSecure, cookieSameSite,
                sessionFixationProtection, invalidateSessionOnLogout,
                maxSessionsPerUser);
    }
}