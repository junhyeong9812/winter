package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.util.Set;

/**
 * CorsInterceptor는 CORS(Cross-Origin Resource Sharing) 정책을 처리하는 인터셉터입니다.
 * 브라우저의 Same-Origin Policy를 우회하여 다른 도메인에서의 API 접근을 허용합니다.
 *
 * 주요 기능:
 * - CORS 헤더 자동 설정
 * - Preflight 요청 처리 (OPTIONS 메서드)
 * - Origin 검증 및 화이트리스트 관리
 * - 허용된 HTTP 메서드 제한
 * - 허용된 헤더 관리
 * - 인증 정보 포함 여부 제어
 *
 * CORS 처리 과정:
 * 1. Simple Request: 직접 요청 처리
 * 2. Preflight Request: OPTIONS 요청으로 사전 검증
 * 3. Actual Request: Preflight 성공 후 실제 요청
 *
 * @author Winter Framework
 * @since 27단계
 */
public class CorsInterceptor implements HandlerInterceptor {

    /**
     * 허용된 Origin 목록 (화이트리스트)
     */
    private final Set<String> allowedOrigins = Set.of(
            "http://localhost:3000",     // React 개발 서버
            "http://localhost:8080",     // 로컬 개발 서버
            "https://myapp.com",         // 프로덕션 도메인
            "https://api.myapp.com"      // API 서버 도메인
    );

    /**
     * 허용된 HTTP 메서드 목록
     */
    private final Set<String> allowedMethods = Set.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    /**
     * 허용된 요청 헤더 목록
     */
    private final Set<String> allowedHeaders = Set.of(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Cache-Control",
            "X-File-Name"
    );

    /**
     * 클라이언트에게 노출할 응답 헤더 목록
     */
    private final Set<String> exposedHeaders = Set.of(
            "Content-Length",
            "Content-Range",
            "X-Total-Count"
    );

    /**
     * Preflight 요청 결과 캐시 시간 (초)
     */
    private final int maxAge = 3600; // 1시간

    /**
     * 인증 정보(쿠키, Authorization 헤더) 포함 허용 여부
     */
    private final boolean allowCredentials = true;

    /**
     * 모든 Origin 허용 여부 (* 사용)
     * 보안상 권장하지 않으며, 개발 환경에서만 사용
     */
    private final boolean allowAllOrigins = false;

    /**
     * 요청 처리 전에 CORS 헤더를 설정하고 Preflight 요청을 처리합니다.
     */
    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        String origin = request.getHeader("Origin");
        String method = request.getMethod();

        System.out.println("🌐 [CORS] 요청 분석: " + method + " " + request.getPath());
        System.out.println("   Origin: " + (origin != null ? origin : "없음"));

        // 1. Origin 헤더가 없으면 Same-Origin 요청 (CORS 처리 불필요)
        if (origin == null) {
            System.out.println("   ✅ Same-Origin 요청 - CORS 처리 생략");
            return true;
        }

        // 2. Origin 검증
        if (!isOriginAllowed(origin)) {
            System.out.println("   ❌ 허용되지 않은 Origin: " + origin);
            response.setStatus(403);
            response.setBody("CORS: Origin not allowed");
            return false;
        }

        System.out.println("   ✅ Origin 허용됨: " + origin);

        // 3. CORS 기본 헤더 설정
        setCorsHeaders(response, origin);

        // 4. Preflight 요청 처리 (OPTIONS 메서드)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return handlePreflightRequest(request, response, origin);
        }

        // 5. 실제 요청 처리 (Simple Request 또는 Preflight 후 실제 요청)
        return handleActualRequest(request, response, origin);
    }

    /**
     * 핸들러 실행 후 추가 CORS 헤더를 설정합니다.
     */
    @Override
    public void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String origin = request.getHeader("Origin");

        // CORS 요청인 경우 추가 헤더 설정
        if (origin != null && isOriginAllowed(origin)) {
            // 응답 타입별 추가 헤더 설정
            String contentType = response.getHeaders().get("Content-Type");
            if (contentType != null && contentType.contains("application/json")) {
                // JSON 응답의 경우 추가 헤더
                response.addHeader("X-Content-Type-Options", "nosniff");
            }

            System.out.println("🌐 [CORS] postHandle 완료 - 추가 헤더 설정됨");
        }
    }

    /**
     * 요청 처리 완료 후 CORS 관련 로그를 기록합니다.
     */
    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        String origin = request.getHeader("Origin");

        if (origin != null) {
            String method = request.getMethod();
            String path = request.getPath();
            int status = response.getStatus();

            if (ex != null) {
                System.err.println("🌐 [CORS-ERROR] " + method + " " + path +
                        " from " + origin + " → " + status + " (" + ex.getMessage() + ")");
            } else {
                System.out.println("🌐 [CORS] " + method + " " + path +
                        " from " + origin + " → " + status + " ✅");
            }
        }
    }

    /**
     * Origin이 허용된 목록에 있는지 확인합니다.
     */
    private boolean isOriginAllowed(String origin) {
        if (allowAllOrigins) {
            return true; // 모든 Origin 허용 (개발 환경용)
        }

        return allowedOrigins.contains(origin);
    }

    /**
     * 기본 CORS 헤더를 설정합니다.
     */
    private void setCorsHeaders(HttpResponse response, String origin) {
        // 1. Access-Control-Allow-Origin 설정
        if (allowAllOrigins) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        } else {
            response.addHeader("Access-Control-Allow-Origin", origin);
        }

        // 2. 인증 정보 포함 허용 여부
        if (allowCredentials && !allowAllOrigins) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }

        // 3. 노출할 응답 헤더 설정
        if (!exposedHeaders.isEmpty()) {
            response.addHeader("Access-Control-Expose-Headers",
                    String.join(", ", exposedHeaders));
        }

        // 4. Vary 헤더 설정 (캐싱 최적화)
        response.addHeader("Vary", "Origin");
    }

    /**
     * Preflight 요청을 처리합니다.
     */
    private boolean handlePreflightRequest(HttpRequest request, HttpResponse response, String origin) {
        System.out.println("🌐 [CORS] Preflight 요청 처리");

        // 1. 요청된 메서드 확인
        String requestMethod = request.getHeader("Access-Control-Request-Method");
        if (requestMethod == null) {
            System.out.println("   ❌ Access-Control-Request-Method 헤더 없음");
            response.setStatus(400);
            response.setBody("Bad Request: Missing Access-Control-Request-Method header");
            return false;
        }

        // 2. 메서드 허용 여부 확인
        if (!allowedMethods.contains(requestMethod.toUpperCase())) {
            System.out.println("   ❌ 허용되지 않은 메서드: " + requestMethod);
            response.setStatus(405);
            response.setBody("Method Not Allowed: " + requestMethod);
            return false;
        }

        // 3. 요청된 헤더 확인
        String requestHeaders = request.getHeader("Access-Control-Request-Headers");
        if (requestHeaders != null) {
            String[] headers = requestHeaders.split(",");
            for (String header : headers) {
                String trimmedHeader = header.trim();
                if (!isHeaderAllowed(trimmedHeader)) {
                    System.out.println("   ❌ 허용되지 않은 헤더: " + trimmedHeader);
                    response.setStatus(400);
                    response.setBody("Bad Request: Header not allowed: " + trimmedHeader);
                    return false;
                }
            }
        }

        // 4. Preflight 응답 헤더 설정
        response.addHeader("Access-Control-Allow-Methods",
                String.join(", ", allowedMethods));

        if (!allowedHeaders.isEmpty()) {
            response.addHeader("Access-Control-Allow-Headers",
                    String.join(", ", allowedHeaders));
        }

        response.addHeader("Access-Control-Max-Age", String.valueOf(maxAge));

        // 5. Preflight 응답 완료
        response.setStatus(200);
        response.setBody(""); // Preflight는 빈 응답

        System.out.println("   ✅ Preflight 요청 승인");
        System.out.println("   허용 메서드: " + String.join(", ", allowedMethods));
        System.out.println("   허용 헤더: " + String.join(", ", allowedHeaders));
        System.out.println("   캐시 시간: " + maxAge + "초");

        return false; // Preflight 요청은 여기서 종료 (실제 핸들러 실행 안 함)
    }

    /**
     * 실제 요청을 처리합니다.
     */
    private boolean handleActualRequest(HttpRequest request, HttpResponse response, String origin) {
        String method = request.getMethod();

        // 1. 메서드 허용 여부 확인
        if (!allowedMethods.contains(method.toUpperCase())) {
            System.out.println("   ❌ 허용되지 않은 메서드: " + method);
            response.setStatus(405);
            response.setBody("Method Not Allowed: " + method);
            return false;
        }

        System.out.println("   ✅ 실제 요청 승인: " + method);
        return true; // 핸들러 실행 계속
    }

    /**
     * 헤더가 허용된 목록에 있는지 확인합니다.
     */
    private boolean isHeaderAllowed(String header) {
        // 대소문자 무시하고 비교
        return allowedHeaders.stream()
                .anyMatch(allowedHeader -> allowedHeader.equalsIgnoreCase(header));
    }

    /**
     * CORS 설정 정보를 반환합니다. (디버깅 및 모니터링용)
     */
    public String getCorsConfiguration() {
        return String.format(
                "CORS Configuration:\n" +
                        "  Allowed Origins: %s\n" +
                        "  Allowed Methods: %s\n" +
                        "  Allowed Headers: %s\n" +
                        "  Exposed Headers: %s\n" +
                        "  Allow Credentials: %s\n" +
                        "  Max Age: %d seconds",
                allowAllOrigins ? "*" : String.join(", ", allowedOrigins),
                String.join(", ", allowedMethods),
                String.join(", ", allowedHeaders),
                String.join(", ", exposedHeaders),
                allowCredentials,
                maxAge
        );
    }
}