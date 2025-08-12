package winter.controller;

import winter.annotation.Controller;
import winter.annotation.RequestMapping;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.interceptor.AuthenticationInterceptor;
import winter.view.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * InterceptorTestController는 27단계 인터셉터 체인 기능을 테스트하기 위한 컨트롤러입니다.
 *
 * 테스트 시나리오:
 * 1. 기본 요청 처리 (모든 인터셉터 정상 실행)
 * 2. 인증이 필요한 요청 (AuthenticationInterceptor 테스트)
 * 3. 느린 요청 처리 (PerformanceInterceptor 테스트)
 * 4. 예외 발생 요청 (인터셉터 예외 처리 테스트)
 * 5. CORS 요청 테스트 (CorsInterceptor 테스트)
 * 6. JSON 응답 테스트 (인터셉터와 JSON 뷰의 조합)
 *
 * @author Winter Framework
 * @since 27단계
 */
@Controller
public class InterceptorTestController {

    /**
     * 기본 인터셉터 체인 테스트
     * 모든 인터셉터가 정상적으로 실행되는지 확인
     */
    @RequestMapping("/interceptor/basic")
    public ModelAndView basicTest(HttpRequest request, HttpResponse response) {
        System.out.println("🎯 [CONTROLLER] 기본 인터셉터 테스트 실행");

        Map<String, Object> model = new HashMap<>();
        model.put("message", "인터셉터 체인이 정상적으로 실행되었습니다!");
        model.put("requestPath", request.getPath());
        model.put("requestMethod", request.getMethod());
        model.put("timestamp", System.currentTimeMillis());

        return new ModelAndView("interceptor-basic", model);
    }

    /**
     * 인증이 필요한 요청 테스트
     * AuthenticationInterceptor가 동작하는지 확인
     */
    @RequestMapping("/secure/test")
    public ModelAndView secureTest(HttpRequest request, HttpResponse response) {
        System.out.println("🔐 [CONTROLLER] 보안 페이지 접근 - 인증된 사용자만 접근 가능");

        // 인터셉터에서 설정한 사용자 정보 사용
        String currentUser = request.getParameter("currentUser");
        String userRole = request.getParameter("userRole");

        Map<String, Object> model = new HashMap<>();
        model.put("message", "보안 페이지에 성공적으로 접근했습니다!");
        model.put("currentUser", currentUser);
        model.put("userRole", userRole);
        model.put("accessTime", System.currentTimeMillis());

        return new ModelAndView("secure-test", model);
    }

    /**
     * 관리자 전용 페이지 테스트
     * AuthenticationInterceptor의 역할 기반 접근 제어 확인
     */
    @RequestMapping("/admin/dashboard")
    public ModelAndView adminTest(HttpRequest request, HttpResponse response) {
        System.out.println("👑 [CONTROLLER] 관리자 대시보드 접근");

        String currentUser = request.getParameter("currentUser");
        String userRole = request.getParameter("userRole");

        Map<String, Object> model = new HashMap<>();
        model.put("message", "관리자 대시보드에 오신 것을 환영합니다!");
        model.put("adminUser", currentUser);
        model.put("userRole", userRole);
        model.put("serverTime", System.currentTimeMillis());

        // 관리자용 추가 정보
        model.put("systemInfo", "Winter Framework v27 - InterceptorChain");
        model.put("interceptorCount", 4); // 등록된 인터셉터 수

        return new ModelAndView("admin-dashboard", model);
    }

    /**
     * 느린 요청 테스트
     * PerformanceInterceptor의 성능 측정 기능 확인
     */
    @RequestMapping("/interceptor/slow")
    public ModelAndView slowTest(HttpRequest request, HttpResponse response) {
        System.out.println("🐌 [CONTROLLER] 느린 요청 처리 시작");

        // 의도적으로 2초 대기 (성능 임계값 테스트)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("🐌 [CONTROLLER] 느린 요청 처리 완료");

        Map<String, Object> model = new HashMap<>();
        model.put("message", "2초 지연 후 처리가 완료되었습니다.");
        model.put("processingTime", "2000ms (의도적 지연)");
        model.put("performanceWarning", "이 요청은 성능 임계값을 초과했습니다!");

        return new ModelAndView("slow-test", model);
    }

    /**
     * 예외 발생 테스트
     * 인터셉터의 예외 처리 동작 확인
     */
    @RequestMapping("/interceptor/error")
    public ModelAndView errorTest(HttpRequest request, HttpResponse response) {
        System.out.println("💥 [CONTROLLER] 예외 발생 테스트");

        // 의도적으로 예외 발생
        throw new RuntimeException("인터셉터 예외 처리 테스트용 예외입니다!");
    }

    /**
     * JSON 응답 테스트
     * 인터셉터와 JSON 뷰의 조합 확인
     */
    @RequestMapping("/api/interceptor-test")
    public void jsonTest(HttpRequest request, HttpResponse response) {
        System.out.println("📄 [CONTROLLER] JSON API 테스트");

        // JSON 직접 응답 (ModelAndView 없이)
        response.setStatus(200);
        response.addHeader("Content-Type", "application/json");

        String jsonResponse = "{\n" +
                "  \"status\": \"success\",\n" +
                "  \"message\": \"인터셉터와 JSON 응답이 정상적으로 동작합니다\",\n" +
                "  \"timestamp\": " + System.currentTimeMillis() + ",\n" +
                "  \"interceptors\": [\n" +
                "    \"CorsInterceptor\",\n" +
                "    \"LoggingInterceptor\",\n" +
                "    \"PerformanceInterceptor\",\n" +
                "    \"AuthenticationInterceptor\"\n" +
                "  ]\n" +
                "}";

        response.setBody(jsonResponse);
    }

    /**
     * CORS 테스트용 API
     * CorsInterceptor의 Cross-Origin 요청 처리 확인
     */
    @RequestMapping("/api/cors-test")
    public void corsTest(HttpRequest request, HttpResponse response) {
        System.out.println("🌐 [CONTROLLER] CORS 테스트 API");

        response.setStatus(200);
        response.addHeader("Content-Type", "application/json");

        String origin = request.getHeader("Origin");
        String method = request.getMethod();

        String jsonResponse = "{\n" +
                "  \"status\": \"success\",\n" +
                "  \"message\": \"CORS 요청이 정상적으로 처리되었습니다\",\n" +
                "  \"origin\": \"" + (origin != null ? origin : "없음") + "\",\n" +
                "  \"method\": \"" + method + "\",\n" +
                "  \"corsEnabled\": true\n" +
                "}";

        response.setBody(jsonResponse);
    }

    /**
     * 로그인 페이지 (인증 테스트용)
     * AuthenticationInterceptor가 리다이렉트하는 페이지
     */
    @RequestMapping(value = "/login", method = "GET")
    public ModelAndView loginPage(HttpRequest request, HttpResponse response) {
        System.out.println("🔑 [CONTROLLER] 로그인 페이지 표시");

        Map<String, Object> model = new HashMap<>();
        model.put("message", "로그인이 필요합니다.");
        model.put("returnUrl", request.getParameter("returnUrl"));

        return new ModelAndView("login", model);
    }

    /**
     * 로그인 처리 (POST)
     * 사용자 인증 및 세션 설정
     */
    @RequestMapping(value = "/login", method = "POST")
    public ModelAndView processLogin(HttpRequest request, HttpResponse response) {
        System.out.println("🔑 [CONTROLLER] 로그인 처리");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 간단한 인증 로직 (실제로는 데이터베이스 확인)
        if ("admin".equals(username) && "admin123".equals(password)) {
            // 관리자 로그인
            AuthenticationInterceptor.login(request.getSession(), username, "ADMIN");

            // 원래 요청 페이지로 리다이렉트
            String originalUrl = AuthenticationInterceptor.getOriginalRequestUrl(request.getSession());
            if (originalUrl != null) {
                response.setStatus(302);
                response.addHeader("Location", originalUrl);
                response.setBody("로그인 성공! 원래 페이지로 이동합니다.");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("message", "관리자로 로그인했습니다!");
            model.put("username", username);
            model.put("role", "ADMIN");
            return new ModelAndView("login-success", model);

        } else if ("user".equals(username) && "user123".equals(password)) {
            // 일반 사용자 로그인
            AuthenticationInterceptor.login(request.getSession(), username, "USER");

            Map<String, Object> model = new HashMap<>();
            model.put("message", "사용자로 로그인했습니다!");
            model.put("username", username);
            model.put("role", "USER");
            return new ModelAndView("login-success", model);

        } else {
            // 로그인 실패
            AuthenticationInterceptor.recordLoginFailure(request.getSession());

            Map<String, Object> model = new HashMap<>();
            model.put("error", "잘못된 사용자명 또는 비밀번호입니다.");
            model.put("username", username);
            return new ModelAndView("login", model);
        }
    }

    /**
     * 로그아웃 처리
     */
    @RequestMapping("/logout")
    public ModelAndView logout(HttpRequest request, HttpResponse response) {
        System.out.println("🚪 [CONTROLLER] 로그아웃 처리");

        AuthenticationInterceptor.logout(request.getSession());

        Map<String, Object> model = new HashMap<>();
        model.put("message", "성공적으로 로그아웃되었습니다.");

        return new ModelAndView("logout", model);
    }

    /**
     * 인터셉터 상태 정보 API
     * 현재 등록된 인터셉터들의 상태 확인
     */
    @RequestMapping("/api/interceptor-status")
    public void interceptorStatus(HttpRequest request, HttpResponse response) {
        System.out.println("📊 [CONTROLLER] 인터셉터 상태 조회");

        response.setStatus(200);
        response.addHeader("Content-Type", "application/json");

        String jsonResponse = "{\n" +
                "  \"status\": \"active\",\n" +
                "  \"interceptors\": [\n" +
                "    {\n" +
                "      \"name\": \"CorsInterceptor\",\n" +
                "      \"order\": 1,\n" +
                "      \"description\": \"CORS 헤더 설정 및 Preflight 처리\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"LoggingInterceptor\",\n" +
                "      \"order\": 2,\n" +
                "      \"description\": \"요청/응답 로깅 및 추적\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"PerformanceInterceptor\",\n" +
                "      \"order\": 3,\n" +
                "      \"description\": \"성능 측정 및 모니터링\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"AuthenticationInterceptor\",\n" +
                "      \"order\": 4,\n" +
                "      \"description\": \"사용자 인증 및 권한 확인\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
                "}";

        response.setBody(jsonResponse);
    }
}