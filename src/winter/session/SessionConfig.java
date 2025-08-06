package winter.session;


/**
 * 세션 및 쿠키 관련 설정을 관리하는 클래스
 * */
public class SessionConfig {

    //세션 설정
    private int maxInactiveInterval = 1800; // 30분 (초 단위)
    private int cleanupInterval = 300;      // 5분마다 정리 (초 단위)

    //쿠키 설정
    private String cookieName = "JSESSIONID";
    private String cookiePath = "/";
    private String cookieDomain = null;
    private boolean cookieHttpOnly = true;  //XSS 방지
    private boolean cookieSecure = false;   // HTTP 환경에서는 true
    private String cookieSameSite = "Lax";  // CSRF 방지

    //보안 설정
    private boolean sessionFixationProtection = true;   //로그인 시 세션 ID 갱신
    private boolean invalidateSessionOnLogout = true;   //로그아웃 시 세션 무효화
    private int maxSessionPerUser = -1;                 //동시 세션 제한 (-1 : 제한 없음)


    //기본 생성자
    public SessionConfig(){

    }

}
