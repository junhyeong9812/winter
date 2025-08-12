package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.util.Set;

/**
 * SecurityInterceptor는 웹 애플리케이션의 보안 헤더를 자동으로 설정하는 인터셉터입니다.
 * OWASP 보안 가이드라인에 따라 다양한 보안 위협을 방지하는 HTTP 헤더들을 추가합니다.
 *
 * 주요 기능:
 * - XSS(Cross-Site Scripting) 방지 헤더 설정
 * - 클릭재킹(Clickjacking) 방지
 * - MIME 타입 스니핑 방지
 * - 콘텐츠 보안 정책(CSP) 설정
 * - HTTPS 강제 리다이렉트 (HSTS)
 * - Referrer 정책 설정
 * - 권한 정책(Permissions Policy) 설정
 *
 * 보안 헤더 설명:
 * - X-Content-Type-Options: MIME 타입 스니핑 공격 방지
 * - X-Frame-Options: 클릭재킹 공격 방지
 * - X-XSS-Protection: 브라우저 내장 XSS 필터 활성화
 * - Content-Security-Policy: XSS, 데이터 주입 공격 방지
 * - Strict-Transport-Security: HTTPS 연결 강제
 * - Referrer-Policy: Referrer 정보 유출 방지
 * - Permissions-Policy: 브라우저 기능 접근 제한
 *
 * @author Winter Framework
 * @since 27단계
 */
public class SecurityInterceptor implements HandlerInterceptor {

    /**
     * 보안 헤더가 필요 없는 경로들 (정적 리소스 등)
     */
    private final Set<String> excludePaths = Set.of(
            "/static/",
            "/favicon.ico",
            "/robots.txt"
    );

    /**
     * 개발 모드 여부 (프로덕션에서는 false로 설정)
     */
    private final boolean developmentMode = true;

    /**
     * CSP(Content Security Policy) 정책
     * 개발 모드에서는 느슨하게, 프로덕션에서는 엄격하게 설정
     */
    private final String contentSecurityPolicy = developmentMode ?
            "default-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self' data: https:; font-src 'self' https:" :
            "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'";

    /**
     * 요청 처리 전에 보안 관련 확인을 수행합니다.
     */
    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        String requestPath = request.getPath();

        System.out.println("🛡️ [SECURITY] 보안 검사: " + requestPath);

        // 1. 보안 헤더가 필요 없는 경로 확인
        if (isExcludedPath(requestPath)) {
            System.out.println("   ✅ 제외 경로 - 보안 헤더 생략");
            return true;
        }

        // 2. 위험한 요청 패턴 검사
        if (containsSuspiciousPatterns(request)) {
            System.err.println("   ❌ 의심스러운 요청 패턴 감지: " + requestPath);
            response.setStatus(400);
            response.setBody("Bad Request: Suspicious pattern detected");
            return false;
        }

        // 3. SQL Injection 패턴 검사
        if (containsSqlInjectionPatterns(request)) {
            System.err.println("   ❌ SQL Injection 시도 감지: " + requestPath);
            response.setStatus(400);
            response.setBody("Bad Request: SQL Injection attempt detected");
            return false;
        }

        // 4. XSS 패턴 검사
        if (containsXssPatterns(request)) {
            System.err.println("   ❌ XSS 시도 감지: " + requestPath);
            response.setStatus(400);
            response.setBody("Bad Request: XSS attempt detected");
            return false;
        }

        System.out.println("   ✅ 보안 검사 통과");
        return true;
    }

    /**
     * 핸들러 실행 후 보안 헤더를 추가합니다.
     */
    @Override
    public void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestPath = request.getPath();

        // 제외 경로가 아닌 경우에만 보안 헤더 추가
        if (!isExcludedPath(requestPath)) {
            addSecurityHeaders(response);
            System.out.println("🛡️ [SECURITY] 보안 헤더 추가 완료: " + requestPath);
        }
    }

    /**
     * 요청 처리 완료 후 보안 관련 로그를 기록합니다.
     */
    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        String requestPath = request.getPath();
        int responseStatus = response.getStatus();

        // 보안 이벤트 로깅
        if (responseStatus >= 400) {
            System.err.println("🛡️ [SECURITY-ALERT] 보안 이슈 발생: " +
                    requestPath + " → " + responseStatus);

            // 보안 로그 상세 정보
            logSecurityIncident(request, responseStatus, ex);
        } else {
            System.out.println("🛡️ [SECURITY] 정상 요청 완료: " + requestPath + " → " + responseStatus);
        }
    }

    /**
     * 보안 헤더들을 응답에 추가합니다.
     */
    private void addSecurityHeaders(HttpResponse response) {
        // 1. X-Content-Type-Options: MIME 타입 스니핑 방지
        response.addHeader("X-Content-Type-Options", "nosniff");

        // 2. X-Frame-Options: 클릭재킹 방지
        response.addHeader("X-Frame-Options", "DENY");

        // 3. X-XSS-Protection: 브라우저 XSS 필터 활성화 (구형 브라우저용)
        response.addHeader("X-XSS-Protection", "1; mode=block");

        // 4. Content-Security-Policy: 콘텐츠 보안 정책
        response.addHeader("Content-Security-Policy", contentSecurityPolicy);

        // 5. Strict-Transport-Security: HTTPS 강제 (프로덕션에서만)
        if (!developmentMode) {
            response.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        // 6. Referrer-Policy: Referrer 정보 유출 방지
        response.addHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // 7. Permissions-Policy: 브라우저 기능 접근 제한
        response.addHeader("Permissions-Policy",
                "geolocation=(), microphone=(), camera=(), payment=(), usb=()");

        // 8. Cache-Control: 민감한 정보 캐싱 방지
        String contentType = response.getHeaders().get("Content-Type");
        if (contentType != null && contentType.contains("text/html")) {
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", "0");
        }

        // 9. 사용자 정의 보안 헤더
        response.addHeader("X-Powered-By", "Winter-Framework-Secure");
        response.addHeader("X-Security-Level", developmentMode ? "Development" : "Production");
    }

    /**
     * 제외 경로인지 확인합니다.
     */
    private boolean isExcludedPath(String path) {
        return excludePaths.stream().anyMatch(path::startsWith);
    }

    /**
     * 의심스러운 요청 패턴을 검사합니다.
     */
    private boolean containsSuspiciousPatterns(HttpRequest request) {
        String path = request.getPath();

        // 쿼리 스트링 가져오기 (HttpRequest에는 getQueryString이 없으므로 파라미터로 유추)
        StringBuilder queryBuilder = new StringBuilder();
        for (String paramName : request.getParameterNames()) {
            if (queryBuilder.length() > 0) queryBuilder.append("&");
            queryBuilder.append(paramName).append("=").append(request.getParameter(paramName));
        }
        String queryString = queryBuilder.toString();

        // 경로 순회 공격 패턴
        String[] pathTraversalPatterns = {
                "../", "..\\", "%2e%2e%2f", "%2e%2e%5c", "....//", "....\\\\",
                "%252e%252e%252f", "%c0%ae%c0%ae%c0%af"
        };

        for (String pattern : pathTraversalPatterns) {
            if (path.toLowerCase().contains(pattern.toLowerCase()) ||
                    (queryString.length() > 0 && queryString.toLowerCase().contains(pattern.toLowerCase()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * SQL Injection 패턴을 검사합니다.
     */
    private boolean containsSqlInjectionPatterns(HttpRequest request) {
        String[] sqlPatterns = {
                "' or '1'='1", "' or 1=1--", "' union select", "' drop table",
                "' insert into", "' update ", "' delete from", "exec(", "sp_",
                "xp_", "/*", "*/", "--", "@@", "char(", "nchar(", "varchar(",
                "nvarchar(", "alter table", "create table", "drop database"
        };

        // 모든 파라미터 값 검사
        for (String paramName : request.getParameterNames()) {
            String paramValue = request.getParameter(paramName);
            if (paramValue != null) {
                String lowerValue = paramValue.toLowerCase();
                for (String pattern : sqlPatterns) {
                    if (lowerValue.contains(pattern)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * XSS 패턴을 검사합니다.
     */
    private boolean containsXssPatterns(HttpRequest request) {
        String[] xssPatterns = {
                "<script", "</script>", "javascript:", "vbscript:", "onload=",
                "onerror=", "onclick=", "onmouseover=", "onfocus=", "onblur=",
                "<iframe", "<object", "<embed", "<applet", "document.cookie",
                "document.write", "window.location", "eval(", "setTimeout(",
                "setInterval(", "alert(", "confirm(", "prompt("
        };

        // 모든 파라미터 값 검사
        for (String paramName : request.getParameterNames()) {
            String paramValue = request.getParameter(paramName);
            if (paramValue != null) {
                String lowerValue = paramValue.toLowerCase();
                for (String pattern : xssPatterns) {
                    if (lowerValue.contains(pattern)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 보안 사고 상세 정보를 로깅합니다.
     */
    private void logSecurityIncident(HttpRequest request, int responseStatus, Exception ex) {
        System.err.println("🚨 [SECURITY-INCIDENT] 보안 사고 상세 정보");
        System.err.println("   시간: " + java.time.LocalDateTime.now());
        System.err.println("   경로: " + request.getPath());
        System.err.println("   메서드: " + request.getMethod());

        // 파라미터 정보 출력
        if (!request.getParameterNames().isEmpty()) {
            System.err.println("   파라미터:");
            for (String paramName : request.getParameterNames()) {
                System.err.println("     " + paramName + " = " + request.getParameter(paramName));
            }
        }

        System.err.println("   상태: " + responseStatus);
        System.err.println("   User-Agent: " + request.getHeader("User-Agent"));
        System.err.println("   Referer: " + request.getHeader("Referer"));
        System.err.println("   X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));

        if (ex != null) {
            System.err.println("   예외: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }

        // 실제 운영 환경에서는 보안 모니터링 시스템으로 전송
        // sendToSecurityMonitoring(incidentData);
    }

    /**
     * 보안 설정 정보를 반환합니다. (디버깅 및 모니터링용)
     */
    public String getSecurityConfiguration() {
        return String.format(
                "Security Configuration:\n" +
                        "  Development Mode: %s\n" +
                        "  CSP Policy: %s\n" +
                        "  Excluded Paths: %s\n" +
                        "  Security Headers: Enabled\n" +
                        "  Pattern Detection: SQL Injection, XSS, Path Traversal",
                developmentMode,
                contentSecurityPolicy,
                String.join(", ", excludePaths)
        );
    }
}