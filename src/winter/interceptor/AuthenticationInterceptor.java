package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.HttpSession;
import winter.view.ModelAndView;

import java.util.Set;

/**
 * AuthenticationInterceptor는 사용자 인증을 담당하는 인터셉터입니다.
 * 세션 기반 인증을 통해 보호된 리소스에 대한 접근을 제어합니다.
 *
 * 주요 기능:
 * - 세션 기반 사용자 인증 확인
 * - 보호된 URL 패턴 관리
 * - 인증 실패 시 로그인 페이지 리다이렉트
 * - 인증 성공 후 원래 요청 페이지로 복귀
 * - 관리자 권한 확인
 * - 로그인 시도 횟수 제한
 *
 * 활용 사례:
 * - 관리자 페이지 보호
 * - 사용자 전용 기능 제한
 * - API 접근 제어
 * - 세션 기반 보안
 *
 * @author Winter Framework
 * @since 27단계
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * 인증이 필요한 URL 패턴들
     */
    private final Set<String> protectedPaths = Set.of(
            "/admin",           // 관리자 페이지
            "/secure",          // 보안 페이지
            "/profile",         // 사용자 프로필
            "/api/private"      // 인증이 필요한 API
    );

    /**
     * 관리자 권한이 필요한 URL 패턴들
     */
    private final Set<String> adminPaths = Set.of(
            "/admin"            // 관리자 전용 페이지
    );

    /**
     * 로그인 페이지 URL
     */
    private final String loginUrl = "/login";

    /**
     * 세션에서 사용자 정보를 저장하는 키
     */
    private static final String USER_SESSION_KEY = "authenticated_user";

    /**
     * 세션에서 사용자 역할을 저장하는 키
     */
    private static final String USER_ROLE_KEY = "user_role";

    /**
     * 원래 요청 URL을 저장하는 키 (로그인 후 리다이렉트용)
     */
    private static final String ORIGINAL_REQUEST_KEY = "original_request_url";

    /**
     * 로그인 시도 횟수를 저장하는 키
     */
    private static final String LOGIN_ATTEMPT_COUNT_KEY = "login_attempt_count";

    /**
     * 현재 사용자를 요청 파라미터에 저장하는 키
     */
    private static final String CURRENT_USER_PARAM = "currentUser";

    /**
     * 사용자 역할을 요청 파라미터에 저장하는 키
     */
    private static final String USER_ROLE_PARAM = "userRole";

    /**
     * 최대 로그인 시도 횟수
     */
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    /**
     * 요청 처리 전에 사용자 인증을 확인합니다.
     */
    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        String requestPath = request.getPath();
        String requestMethod = request.getMethod();

        System.out.println("🔐 [AUTH] 인증 확인: " + requestMethod + " " + requestPath);

        // 1. 로그인 페이지나 공개 리소스는 인증 불필요
        if (isPublicResource(requestPath)) {
            System.out.println("   ✅ 공개 리소스 - 인증 생략");
            return true;
        }

        // 2. 보호된 리소스인지 확인
        if (!isProtectedResource(requestPath)) {
            System.out.println("   ✅ 비보호 리소스 - 인증 생략");
            return true;
        }

        // 3. 세션에서 사용자 인증 정보 확인
        HttpSession session = request.getSession();
        if (session == null) {
            System.out.println("   ❌ 세션 없음 - 로그인 필요");
            redirectToLogin(request, response, requestPath);
            return false;
        }

        // 4. 로그인 시도 횟수 확인
        if (isLoginAttemptsExceeded(session)) {
            System.out.println("   ❌ 로그인 시도 횟수 초과 - 접근 차단");
            response.setStatus(429); // Too Many Requests
            response.setBody("로그인 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
            return false;
        }

        // 5. 사용자 인증 상태 확인
        Object authenticatedUser = session.getAttribute(USER_SESSION_KEY);
        if (authenticatedUser == null) {
            System.out.println("   ❌ 인증되지 않은 사용자 - 로그인 필요");
            redirectToLogin(request, response, requestPath);
            return false;
        }

        // 6. 관리자 권한이 필요한 경우 권한 확인
        if (isAdminResource(requestPath)) {
            String userRole = (String) session.getAttribute(USER_ROLE_KEY);
            if (!"ADMIN".equals(userRole)) {
                System.out.println("   ❌ 관리자 권한 필요 - 접근 거부");
                response.setStatus(403); // Forbidden
                response.setBody("관리자 권한이 필요합니다.");
                return false;
            }
            System.out.println("   ✅ 관리자 권한 확인됨");
        }

        // 7. 인증 성공
        System.out.println("   ✅ 인증 성공: " + authenticatedUser);

        // 8. 요청에 사용자 정보 설정 (컨트롤러에서 사용 가능)
        request.addParameter(CURRENT_USER_PARAM, authenticatedUser.toString());
        String userRole = (String) session.getAttribute(USER_ROLE_KEY);
        if (userRole != null) {
            request.addParameter(USER_ROLE_PARAM, userRole);
        }

        return true;
    }

    /**
     * 핸들러 실행 후 인증 관련 후처리를 수행합니다.
     */
    @Override
    public void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 인증된 사용자 정보를 모델에 추가 (뷰에서 사용 가능)
        if (modelAndView != null) {
            String currentUser = request.getParameter(CURRENT_USER_PARAM);
            String userRole = request.getParameter(USER_ROLE_PARAM);

            if (currentUser != null) {
                modelAndView.addAttribute("currentUser", currentUser);
                modelAndView.addAttribute("userRole", userRole);
                modelAndView.addAttribute("isAuthenticated", true);

                System.out.println("🔐 [AUTH] 사용자 정보를 모델에 추가: " + currentUser);
            } else {
                modelAndView.addAttribute("isAuthenticated", false);
            }
        }
    }

    /**
     * 요청 처리 완료 후 인증 관련 정리 작업을 수행합니다.
     */
    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        // 인증 관련 로그 기록
        String currentUser = request.getParameter(CURRENT_USER_PARAM);
        String requestPath = request.getPath();
        int responseStatus = response.getStatus();

        if (currentUser != null) {
            System.out.println("🔐 [AUTH] 인증된 요청 완료: " + currentUser +
                    " → " + requestPath + " (" + responseStatus + ")");
        }

        // 예외 발생 시 보안 로그 기록
        if (ex != null) {
            System.err.println("🔐 [AUTH-ERROR] 인증된 사용자의 요청에서 예외 발생: " +
                    currentUser + " → " + requestPath + " → " + ex.getMessage());
        }
    }

    /**
     * 공개 리소스인지 확인합니다.
     */
    private boolean isPublicResource(String path) {
        return path.equals(loginUrl) ||
                path.startsWith("/static/") ||
                path.equals("/") ||
                path.equals("/hello") ||
                path.equals("/register") ||
                path.startsWith("/api/public") ||
                path.equals("/interceptor/basic") ||  // 27단계 테스트용
                path.equals("/interceptor/slow") ||   // 27단계 테스트용
                path.equals("/interceptor/error") ||  // 27단계 테스트용
                path.startsWith("/api/interceptor") || // 27단계 테스트용
                path.startsWith("/api/cors");         // 27단계 테스트용
    }

    /**
     * 보호된 리소스인지 확인합니다.
     */
    private boolean isProtectedResource(String path) {
        return protectedPaths.stream().anyMatch(path::startsWith);
    }

    /**
     * 관리자 권한이 필요한 리소스인지 확인합니다.
     */
    private boolean isAdminResource(String path) {
        return adminPaths.stream().anyMatch(path::startsWith);
    }

    /**
     * 로그인 시도 횟수가 초과되었는지 확인합니다.
     */
    private boolean isLoginAttemptsExceeded(HttpSession session) {
        Object attemptCountObj = session.getAttribute(LOGIN_ATTEMPT_COUNT_KEY);
        if (attemptCountObj instanceof Integer) {
            Integer attemptCount = (Integer) attemptCountObj;
            return attemptCount >= MAX_LOGIN_ATTEMPTS;
        }
        return false;
    }

    /**
     * 로그인 페이지로 리다이렉트합니다.
     */
    private void redirectToLogin(HttpRequest request, HttpResponse response, String originalPath) {
        // 원래 요청 URL을 세션에 저장 (로그인 후 돌아가기 위해)
        HttpSession session = request.getSession();
        if (session != null && !originalPath.equals(loginUrl)) {
            session.setAttribute(ORIGINAL_REQUEST_KEY, originalPath);
            System.out.println("🔐 [AUTH] 원래 요청 URL 저장: " + originalPath);
        }

        // 로그인 페이지로 리다이렉트
        response.setStatus(302); // Found (Redirect)
        response.addHeader("Location", loginUrl);
        response.setBody("로그인이 필요합니다. <a href='" + loginUrl + "'>로그인 페이지로 이동</a>");

        System.out.println("🔐 [AUTH] 로그인 페이지로 리다이렉트: " + loginUrl);
    }

    /**
     * 사용자를 로그인 처리합니다. (컨트롤러에서 호출)
     */
    public static void login(HttpSession session, String username, String role) {
        session.setAttribute(USER_SESSION_KEY, username);
        session.setAttribute(USER_ROLE_KEY, role);

        // 로그인 성공 시 시도 횟수 초기화
        session.removeAttribute(LOGIN_ATTEMPT_COUNT_KEY);

        System.out.println("🔐 [AUTH] 로그인 성공: " + username + " (역할: " + role + ")");
    }

    /**
     * 사용자를 로그아웃 처리합니다. (컨트롤러에서 호출)
     */
    public static void logout(HttpSession session) {
        Object user = session.getAttribute(USER_SESSION_KEY);
        session.removeAttribute(USER_SESSION_KEY);
        session.removeAttribute(USER_ROLE_KEY);
        session.removeAttribute(ORIGINAL_REQUEST_KEY);
        session.removeAttribute(LOGIN_ATTEMPT_COUNT_KEY);

        System.out.println("🔐 [AUTH] 로그아웃: " + user);
    }

    /**
     * 로그인 실패를 기록합니다. (컨트롤러에서 호출)
     */
    public static void recordLoginFailure(HttpSession session) {
        Object currentCountObj = session.getAttribute(LOGIN_ATTEMPT_COUNT_KEY);
        int currentCount = 0;

        if (currentCountObj instanceof Integer) {
            currentCount = (Integer) currentCountObj;
        }

        int newCount = currentCount + 1;
        session.setAttribute(LOGIN_ATTEMPT_COUNT_KEY, newCount);

        System.out.println("🔐 [AUTH] 로그인 실패 기록: " + newCount + "/" + MAX_LOGIN_ATTEMPTS);
    }

    /**
     * 원래 요청 URL을 반환합니다. (로그인 후 리다이렉트용)
     */
    public static String getOriginalRequestUrl(HttpSession session) {
        Object originalUrlObj = session.getAttribute(ORIGINAL_REQUEST_KEY);
        return originalUrlObj instanceof String ? (String) originalUrlObj : null;
    }

    /**
     * 현재 인증된 사용자를 반환합니다.
     */
    public static String getCurrentUser(HttpSession session) {
        Object userObj = session.getAttribute(USER_SESSION_KEY);
        return userObj instanceof String ? (String) userObj : null;
    }

    /**
     * 현재 사용자의 역할을 반환합니다.
     */
    public static String getCurrentUserRole(HttpSession session) {
        Object roleObj = session.getAttribute(USER_ROLE_KEY);
        return roleObj instanceof String ? (String) roleObj : null;
    }
}