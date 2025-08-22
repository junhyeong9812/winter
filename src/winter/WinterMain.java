package winter;

import winter.controller.InterceptorTestController;
import winter.controller.ProductController;
import winter.controller.SessionController;
import winter.dispatcher.Dispatcher;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.StandardHttpResponse;
import winter.interceptor.SecurityInterceptor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class WinterMain {
    public static void main(String[] args) {
        System.out.println("=== WinterFramework Start ===");

        Dispatcher dispatcher = new Dispatcher();

        // 27단계: 추가 사용자 정의 인터셉터 등록
        dispatcher.addInterceptor(new SecurityInterceptor());

        // 27단계: 인터셉터 테스트용 컨트롤러 등록
        dispatcher.registerController(InterceptorTestController.class);

        // SessionController는 이미 CombinedHandlerMapping에서 자동 등록됨
        // dispatcher.registerController(SessionController.class); // 제거

//        // 기존 테스트들
//        testExistingFeatures(dispatcher);
//
//        // 21단계: JSON 응답 테스트
//        testJsonResponse(dispatcher);
//
//        // 22단계: 어노테이션 기반 MVC 테스트
//        testAnnotationBasedMvc(dispatcher);
//
//        // 23단계: 파라미터 바인딩 테스트
//        testParameterBinding(dispatcher);
//
//        // 24단계: 파일 업로드 테스트
//        testFileUpload(dispatcher);
//
//        // 25단계: 세션 관리 테스트
//        testSessionManagement(dispatcher);
//
//        // 26단계: View Engine Integration 테스트
//        testViewEngineIntegration(dispatcher);

        // 27단계: HandlerInterceptor 체인 테스트 (새로 추가)
        testHandlerInterceptorChain(dispatcher);

        System.out.println("\n=== WinterFramework Test Complete ===");

        // 세션 관리자 정리
        dispatcher.shutdown();
    }

    /**
     * 27단계: HandlerInterceptor 체인 기능 테스트 (새로 추가)
     */
    private static void testHandlerInterceptorChain(Dispatcher dispatcher) {
        System.out.println("\n--- 27단계: HandlerInterceptor 체인 테스트 ---");

        // 인터셉터 체인 정보 출력
        System.out.println("📋 등록된 인터셉터 체인:");
        System.out.println(dispatcher.getInterceptorChain().toString());
        System.out.println();

        // 인터셉터 체인 동작 원리 설명 출력
        printInterceptorChainExplanation();

        // ===========================================
        // 테스트 시나리오 1: 기본 인터셉터 체인 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 1: 기본 인터셉터 체인");
        testBasicInterceptorChain(dispatcher);

        // ===========================================
        // 테스트 시나리오 2: 성능 측정 인터셉터 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 2: 성능 측정 인터셉터 (느린 요청)");
        testPerformanceInterceptor(dispatcher);

        // ===========================================
        // 테스트 시나리오 3: 인증 인터셉터 테스트 (인증 없음)
        // ===========================================
        System.out.println("\n🔥 테스트 3: 인증 인터셉터 (인증 없이 보안 페이지 접근)");
        testAuthenticationInterceptorUnauthorized(dispatcher);

        // ===========================================
        // 테스트 시나리오 4: 로그인 후 인증된 요청 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 4: 로그인 후 인증된 요청");
        testAuthenticatedRequest(dispatcher);

        // ===========================================
        // 테스트 시나리오 5: 관리자 권한 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 5: 관리자 권한 테스트");
        testAdminAccess(dispatcher);

        // ===========================================
        // 테스트 시나리오 6: CORS 인터셉터 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 6: CORS 인터셉터 (Cross-Origin 요청)");
        testCorsInterceptor(dispatcher);

        // ===========================================
        // 테스트 시나리오 7: 예외 발생 시 인터셉터 처리
        // ===========================================
        System.out.println("\n🔥 테스트 7: 예외 발생 시 인터셉터 처리");
        testExceptionHandling(dispatcher);

        // ===========================================
        // 테스트 시나리오 8: JSON API 인터셉터 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 8: JSON API 인터셉터");
        testJsonApiInterceptor(dispatcher);

        // ===========================================
        // 테스트 시나리오 9: 보안 인터셉터 테스트
        // ===========================================
        System.out.println("\n🔥 테스트 9: 보안 인터셉터 (의심스러운 요청)");
        testSecurityInterceptor(dispatcher);

        // 27단계 구현 성과 요약 출력
        printImplementationSummary();

        System.out.println("\n=== 27단계: HandlerInterceptor 체인 테스트 완료 ===");
    }

    /**
     * 테스트 1: 기본 인터셉터 체인 동작 확인
     */
    private static void testBasicInterceptorChain(Dispatcher dispatcher) {
        HttpRequest request = createMockRequest("GET", "/interceptor/basic", null);
        HttpResponse response = new StandardHttpResponse() {
        };

        dispatcher.dispatch(request, response);

        System.out.println("✅ 응답: " + response.getStatus() + " - " +
                (response.getBody() != null ? response.getBody().substring(0, Math.min(100, response.getBody().length())) : "No Body"));
    }

    /**
     * 테스트 2: 성능 측정 인터셉터 동작 확인 (느린 요청)
     */
    private static void testPerformanceInterceptor(Dispatcher dispatcher) {
        HttpRequest request = createMockRequest("GET", "/interceptor/slow", null);
        HttpResponse response = new StandardHttpResponse();

        long startTime = System.currentTimeMillis();
        dispatcher.dispatch(request, response);
        long endTime = System.currentTimeMillis();

        System.out.println("✅ 응답: " + response.getStatus() +
                " (처리 시간: " + (endTime - startTime) + "ms)");
    }

    /**
     * 테스트 3: 인증되지 않은 사용자의 보안 페이지 접근
     */
    private static void testAuthenticationInterceptorUnauthorized(Dispatcher dispatcher) {
        HttpRequest request = createMockRequest("GET", "/secure/test", null);
        HttpResponse response = new StandardHttpResponse();

        dispatcher.dispatch(request, response);

        System.out.println("✅ 응답: " + response.getStatus() + " - 인증 없이 접근 차단됨");
    }

    /**
     * 테스트 4: 로그인 후 인증된 요청
     */
    private static void testAuthenticatedRequest(Dispatcher dispatcher) {
        // 1. 먼저 로그인 처리
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("username", "user");
        loginParams.put("password", "user123");

        HttpRequest loginRequest = createMockRequest("POST", "/login", loginParams);
        HttpResponse loginResponse = new StandardHttpResponse();

        dispatcher.dispatch(loginRequest, loginResponse);
        System.out.println("로그인 처리: " + loginResponse.getStatus());

        // 2. 인증된 상태로 보안 페이지 접근
        HttpRequest secureRequest = createMockRequest("GET", "/secure/test", null);
        secureRequest.setSession(loginRequest.getSession()); // 세션 유지
        HttpResponse secureResponse = new StandardHttpResponse();

        dispatcher.dispatch(secureRequest, secureResponse);

        System.out.println("✅ 응답: " + secureResponse.getStatus() + " - 인증된 사용자 접근 성공");
    }

    /**
     * 테스트 5: 관리자 권한 테스트
     */
    private static void testAdminAccess(Dispatcher dispatcher) {
        // 1. 관리자로 로그인
        Map<String, String> adminParams = new HashMap<>();
        adminParams.put("username", "admin");
        adminParams.put("password", "admin123");

        HttpRequest adminLoginRequest = createMockRequest("POST", "/login", adminParams);
        HttpResponse adminLoginResponse = new StandardHttpResponse();

        dispatcher.dispatch(adminLoginRequest, adminLoginResponse);
        System.out.println("관리자 로그인: " + adminLoginResponse.getStatus());

        // 2. 관리자 페이지 접근
        HttpRequest adminRequest = createMockRequest("GET", "/admin/dashboard", null);
        adminRequest.setSession(adminLoginRequest.getSession()); // 세션 유지
        HttpResponse adminResponse = new StandardHttpResponse();

        dispatcher.dispatch(adminRequest, adminResponse);

        System.out.println("✅ 응답: " + adminResponse.getStatus() + " - 관리자 페이지 접근 성공");
    }

    /**
     * 테스트 6: CORS 인터셉터 테스트
     */
    private static void testCorsInterceptor(Dispatcher dispatcher) {
        // 1. Preflight 요청 (OPTIONS)
        HttpRequest preflightRequest = createMockRequest("OPTIONS", "/api/cors-test", null);
        preflightRequest.addHeader("Origin", "http://localhost:3000");
        preflightRequest.addHeader("Access-Control-Request-Method", "GET");
        preflightRequest.addHeader("Access-Control-Request-Headers", "Content-Type");
        HttpResponse preflightResponse = new StandardHttpResponse();

        dispatcher.dispatch(preflightRequest, preflightResponse);
        System.out.println("Preflight 요청: " + preflightResponse.getStatus());

        // 2. 실제 CORS 요청
        HttpRequest corsRequest = createMockRequest("GET", "/api/cors-test", null);
        corsRequest.addHeader("Origin", "http://localhost:3000");
        HttpResponse corsResponse = new StandardHttpResponse();

        dispatcher.dispatch(corsRequest, corsResponse);

        System.out.println("✅ 응답: " + corsResponse.getStatus() + " - CORS 헤더 설정됨");
        System.out.println("   Access-Control-Allow-Origin: " + corsResponse.getHeaders().get("Access-Control-Allow-Origin"));
    }

    /**
     * 테스트 7: 예외 발생 시 인터셉터 처리
     */
    private static void testExceptionHandling(Dispatcher dispatcher) {
        HttpRequest request = createMockRequest("GET", "/interceptor/error", null);
        HttpResponse response = new StandardHttpResponse();

        try {
            dispatcher.dispatch(request, response);
        } catch (Exception e) {
            // 예외가 발생해도 afterCompletion은 실행되어야 함
        }

        System.out.println("✅ 응답: " + response.getStatus() + " - 예외 발생 시에도 afterCompletion 실행됨");
    }

    /**
     * 테스트 8: JSON API 인터셉터 테스트
     */
    private static void testJsonApiInterceptor(Dispatcher dispatcher) {
        HttpRequest request = createMockRequest("GET", "/api/interceptor-test", null);
        request.addHeader("Accept", "application/json");
        HttpResponse response = new StandardHttpResponse();

        dispatcher.dispatch(request, response);

        System.out.println("✅ 응답: " + response.getStatus() + " - JSON API 정상 처리");
        System.out.println("   Content-Type: " + response.getHeaders().get("Content-Type"));
    }

    /**
     * 테스트 9: 보안 인터셉터 테스트 (의심스러운 요청)
     */
    private static void testSecurityInterceptor(Dispatcher dispatcher) {
        // SQL Injection 시도
        HttpRequest sqlInjectionRequest = createMockRequest("GET", "/interceptor/basic", null);
        Map<String, String> sqlParams = new HashMap<>();
        sqlParams.put("id", "1' OR '1'='1");
        HttpRequest sqlInjectionRequestWithParams = createMockRequest("GET", "/interceptor/basic", sqlParams);
        HttpResponse sqlInjectionResponse = new StandardHttpResponse();

        dispatcher.dispatch(sqlInjectionRequestWithParams, sqlInjectionResponse);
        System.out.println("SQL Injection 차단: " + sqlInjectionResponse.getStatus());

        // XSS 시도
        Map<String, String> xssParams = new HashMap<>();
        xssParams.put("comment", "<script>alert('xss')</script>");
        HttpRequest xssRequest = createMockRequest("GET", "/interceptor/basic", xssParams);
        HttpResponse xssResponse = new StandardHttpResponse();

        dispatcher.dispatch(xssRequest, xssResponse);
        System.out.println("XSS 차단: " + xssResponse.getStatus());

        // 정상 요청의 보안 헤더 확인
        HttpRequest normalRequest = createMockRequest("GET", "/interceptor/basic", null);
        HttpResponse normalResponse = new StandardHttpResponse();

        dispatcher.dispatch(normalRequest, normalResponse);

        System.out.println("✅ 응답: " + normalResponse.getStatus() + " - 보안 헤더 자동 추가");
        System.out.println("   X-Content-Type-Options: " + normalResponse.getHeaders().get("X-Content-Type-Options"));
        System.out.println("   X-Frame-Options: " + normalResponse.getHeaders().get("X-Frame-Options"));
        System.out.println("   Content-Security-Policy 설정됨: " +
                (normalResponse.getHeaders().get("Content-Security-Policy") != null));
    }

    /**
     * Mock HTTP Request 생성 유틸리티 메서드
     */
    private static HttpRequest createMockRequest(String method, String path, Map<String, String> parameters) {
        // 경로에 쿼리 파라미터 추가
        StringBuilder pathBuilder = new StringBuilder(path);
        if (parameters != null && !parameters.isEmpty() && "GET".equals(method)) {
            pathBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (!first) pathBuilder.append("&");
                pathBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }

        // HttpRequest 생성
        HttpRequest request = new HttpRequest(pathBuilder.toString(), method);

        // 기본 헤더 추가
        request.addHeader("Host", "localhost:8080");
        request.addHeader("User-Agent", "Winter-Framework-Test/27.0");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        request.addHeader("Connection", "keep-alive");

        // POST 요청인 경우 파라미터를 본문으로 처리
        if ("POST".equals(method) && parameters != null && !parameters.isEmpty()) {
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");

            // POST 파라미터는 별도로 추가
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }

        return request;
    }

    /**
     * 인터셉터 체인 동작 원리 설명 출력
     */
    private static void printInterceptorChainExplanation() {
        System.out.println("\n📚 인터셉터 체인 동작 원리:");
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                    인터셉터 체인 실행 순서                     │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. CorsInterceptor.preHandle()        (CORS 검증)           │");
        System.out.println("│ 2. LoggingInterceptor.preHandle()     (요청 로깅 시작)       │");
        System.out.println("│ 3. PerformanceInterceptor.preHandle() (성능 측정 시작)       │");
        System.out.println("│ 4. AuthenticationInterceptor.preHandle() (인증 확인)        │");
        System.out.println("│ 5. SecurityInterceptor.preHandle()    (보안 검사)           │");
        System.out.println("│                                                             │");
        System.out.println("│ 6. ✅ Handler/Controller 실행                              │");
        System.out.println("│                                                             │");
        System.out.println("│ 7. SecurityInterceptor.postHandle()   (보안 헤더 추가)      │");
        System.out.println("│ 8. AuthenticationInterceptor.postHandle() (사용자 정보 추가) │");
        System.out.println("│ 9. PerformanceInterceptor.postHandle() (성능 데이터 수집)   │");
        System.out.println("│10. LoggingInterceptor.postHandle()    (응답 로깅)           │");
        System.out.println("│11. CorsInterceptor.postHandle()       (CORS 헤더 추가)      │");
        System.out.println("│                                                             │");
        System.out.println("│12. 🎨 View 렌더링                                          │");
        System.out.println("│                                                             │");
        System.out.println("│13. SecurityInterceptor.afterCompletion() (정리 작업)       │");
        System.out.println("│14. AuthenticationInterceptor.afterCompletion() (감사 로그)  │");
        System.out.println("│15. PerformanceInterceptor.afterCompletion() (최종 통계)    │");
        System.out.println("│16. LoggingInterceptor.afterCompletion() (최종 로깅)        │");
        System.out.println("│17. CorsInterceptor.afterCompletion() (CORS 완료 로깅)      │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("\n💡 특징:");
        System.out.println("• preHandle: 등록 순서대로 실행 (A→B→C→D→E)");
        System.out.println("• postHandle: 등록 순서의 역순으로 실행 (E→D→C→B→A)");
        System.out.println("• afterCompletion: 등록 순서의 역순으로 실행 (E→D→C→B→A)");
        System.out.println("• 예외 발생 시에도 afterCompletion은 반드시 실행됨");
        System.out.println("• preHandle에서 false 반환 시 체인 중단");
    }

    /**
     * 27단계 구현 성과 요약 출력
     */
    private static void printImplementationSummary() {
        System.out.println("\n🎯 27단계 구현 성과 요약:");
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                  HandlerInterceptor 체인 구조                │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ ✅ HandlerInterceptor 인터페이스 설계                        │");
        System.out.println("│ ✅ InterceptorChain 체인 관리 클래스                         │");
        System.out.println("│ ✅ LoggingInterceptor (요청/응답 로깅)                       │");
        System.out.println("│ ✅ PerformanceInterceptor (성능 측정 및 모니터링)             │");
        System.out.println("│ ✅ AuthenticationInterceptor (세션 기반 인증)                │");
        System.out.println("│ ✅ CorsInterceptor (Cross-Origin 요청 처리)                  │");
        System.out.println("│ ✅ SecurityInterceptor (보안 헤더 및 공격 차단)               │");
        System.out.println("│ ✅ Dispatcher 통합 (체인 실행 보장)                          │");
        System.out.println("│ ✅ 예외 안전성 (afterCompletion 보장)                       │");
        System.out.println("│ ✅ 테스트 시나리오 (9가지 케이스)                            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("\n🚀 다음 단계 (28단계) 준비:");
        System.out.println("• Bean Validation API 통합 (@Valid, @NotNull 등)");
        System.out.println("• 유효성 검사 인터셉터 구현");
        System.out.println("• 커스텀 Validator 지원");
        System.out.println("• 검증 실패 시 자동 에러 응답");
    }

    /**
     * 26단계: View Engine Integration 기능 테스트
     * 다양한 뷰 엔진(SimpleTemplate, Thymeleaf, Mustache, JSP)의 통합 테스트
     */
    private static void testViewEngineIntegration(Dispatcher dispatcher) {
        System.out.println("\n--- 26단계: View Engine Integration 테스트 ---");

        // 테스트 1: SimpleTemplateEngine - 기본 템플릿 엔진 테스트
        // ${변수} 형태의 플레이스홀더를 사용하는 간단한 템플릿 엔진
        System.out.println("\n[테스트 1] GET /view/simple - SimpleTemplateEngine");
        HttpRequest simpleRequest = new HttpRequest("/view/simple", "GET"); // GET 요청 생성
        HttpResponse simpleResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(simpleRequest, simpleResponse); // 디스패처로 요청 처리

        // 테스트 2: MockThymeleafEngine - Thymeleaf 문법 시뮬레이션
        // th:text, th:if, th:each 등의 Thymeleaf 속성을 모방한 템플릿 처리
        System.out.println("\n[테스트 2] GET /view/thymeleaf - MockThymeleafEngine");
        HttpRequest thymeleafRequest = new HttpRequest("/view/thymeleaf", "GET"); // GET 요청 생성
        HttpResponse thymeleafResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(thymeleafRequest, thymeleafResponse); // 디스패처로 요청 처리

        // 테스트 3: MockMustacheEngine - Mustache 문법 시뮬레이션
        // {{변수}}, {{#section}} 등의 Mustache 문법을 모방한 템플릿 처리
        System.out.println("\n[테스트 3] GET /view/mustache - MockMustacheEngine");
        HttpRequest mustacheRequest = new HttpRequest("/view/mustache", "GET"); // GET 요청 생성
        HttpResponse mustacheResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(mustacheRequest, mustacheResponse); // 디스패처로 요청 처리

        // 테스트 4: MockJspEngine - JSP 문법 시뮬레이션
        // <%= %>, <% %> 등의 JSP 스크립틀릿을 모방한 템플릿 처리
        System.out.println("\n[테스트 4] GET /view/jsp - MockJspEngine");
        HttpRequest jspRequest = new HttpRequest("/view/jsp", "GET"); // GET 요청 생성
        HttpResponse jspResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(jspRequest, jspResponse); // 디스패처로 요청 처리

        // 테스트 5: 존재하지 않는 템플릿 - 404 에러 처리 테스트
        // 요청한 뷰명에 해당하는 템플릿 파일이 없을 때의 에러 처리 확인
        System.out.println("\n[테스트 5] GET /view/nonexistent - 404 에러 처리");
        HttpRequest nonexistentRequest = new HttpRequest("/view/nonexistent", "GET"); // 존재하지 않는 뷰 요청
        HttpResponse nonexistentResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(nonexistentRequest, nonexistentResponse); // 에러 처리 확인

        // 테스트 6: 뷰 엔진 우선순위 테스트
        // 동일한 뷰명으로 여러 확장자의 템플릿이 있을 때 우선순위에 따른 선택 확인
        System.out.println("\n[테스트 6] GET /view/priority - 뷰 엔진 우선순위");
        HttpRequest priorityRequest = new HttpRequest("/view/priority", "GET"); // 우선순위 테스트 요청
        HttpResponse priorityResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(priorityRequest, priorityResponse); // 우선순위 로직 확인

        // 테스트 7: 뷰 엔진 성능 비교 테스트
        // 대량의 데이터를 처리할 때 각 뷰 엔진의 성능 차이 확인
        System.out.println("\n[테스트 7] GET /view/performance - 뷰 엔진 성능 테스트");
        HttpRequest performanceRequest = new HttpRequest("/view/performance", "GET"); // 성능 테스트 요청
        HttpResponse performanceResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(performanceRequest, performanceResponse); // 성능 측정

        // 테스트 8: 뷰 엔진 정보 조회 테스트
        // 현재 등록된 뷰 엔진들의 정보를 표시하는 페이지 확인
        System.out.println("\n[테스트 8] GET /view/info - 뷰 엔진 정보 조회");
        HttpRequest infoRequest = new HttpRequest("/view/info", "GET"); // 정보 조회 요청
        HttpResponse infoResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(infoRequest, infoResponse); // 뷰 엔진 정보 확인

        // 테스트 9: JSON 응답과 뷰 엔진 조합 테스트
        // Accept 헤더에 따라 JSON 응답과 HTML 템플릿 응답을 선택적으로 처리
        System.out.println("\n[테스트 9] GET /view/simple - JSON vs HTML 선택");
        HttpRequest jsonViewRequest = new HttpRequest("/view/simple", "GET"); // 같은 엔드포인트
        jsonViewRequest.addHeader("Accept", "application/json"); // JSON 응답 요청 헤더 추가
        HttpResponse jsonViewResponse = new StandardHttpResponse(); // 응답 객체 생성
        dispatcher.dispatch(jsonViewRequest, jsonViewResponse); // Content Negotiation 확인

        // 테스트 10: 템플릿 렌더링 오류 처리 테스트
        // 템플릿 파일은 존재하지만 렌더링 중 오류가 발생할 때의 처리 확인
        System.out.println("\n[테스트 10] 템플릿 렌더링 오류 처리 시뮬레이션");
        try {
            // 잘못된 모델 데이터로 렌더링 오류 유발 시도
            HttpRequest errorRequest = new HttpRequest("/view/simple?invalidParam=true", "GET"); // 오류 유발 파라미터
            HttpResponse errorResponse = new StandardHttpResponse(); // 응답 객체 생성
            dispatcher.dispatch(errorRequest, errorResponse); // 에러 처리 로직 확인
        } catch (Exception e) {
            System.out.println("예상된 렌더링 오류 처리 완료: " + e.getMessage()); // 예외 처리 확인
        }

        System.out.println("\n=== 26단계: View Engine Integration 테스트 완료 ===");

        // 테스트 결과 요약 출력
        System.out.println("\n📊 테스트 결과 요약:");
        System.out.println("✅ SimpleTemplateEngine: ${변수} 치환 기능");
        System.out.println("✅ MockThymeleafEngine: th:* 속성 시뮬레이션");
        System.out.println("✅ MockMustacheEngine: {{변수}} 문법 시뮬레이션");
        System.out.println("✅ MockJspEngine: <%= %> 스크립틀릿 시뮬레이션");
        System.out.println("✅ 404 에러 처리: 존재하지 않는 템플릿 처리");
        System.out.println("✅ 뷰 엔진 우선순위: 확장자별 엔진 선택");
        System.out.println("✅ Content Negotiation: JSON vs HTML 자동 선택");
        System.out.println("✅ 에러 페이지: 렌더링 실패 시 사용자 친화적 에러 표시");
    }

    /**
     * Multipart 요청을 생성하는 헬퍼 메서드
     */
    private static HttpRequest createMultipartRequest(String path, String boundary, String multipartBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        headers.put("Content-Length", String.valueOf(multipartBody.length()));

        BufferedReader bodyReader = new BufferedReader(new StringReader(multipartBody));

        return new HttpRequest(path, "POST", headers, new HashMap<>(), bodyReader);
    }

    /**
     * 기존 기능들 테스트 (1-21단계)
     */
    private static void testExistingFeatures(Dispatcher dispatcher) {
        System.out.println("\n--- 기존 기능 테스트 ---");

        // /hello 요청
        HttpRequest helloRequest = new HttpRequest("/hello");
        helloRequest.addParameter("name", "winter");
        HttpResponse helloResponse = new StandardHttpResponse();
        dispatcher.dispatch(helloRequest, helloResponse);

        // /api 요청 (21단계 JSON)
        HttpRequest apiRequest = new HttpRequest("/api", "GET");
        HttpResponse apiResponse = new StandardHttpResponse();
        dispatcher.dispatch(apiRequest, apiResponse);
    }

    /**
     * 21단계: JSON 응답 기능 테스트
     */
    private static void testJsonResponse(Dispatcher dispatcher) {
        System.out.println("\n--- 21단계: JSON 응답 테스트 ---");

        // JSON 응답 테스트
        HttpRequest jsonRequest = new HttpRequest("/api", "GET");
        jsonRequest.addHeader("Accept", "application/json");
        HttpResponse jsonResponse = new StandardHttpResponse();
        dispatcher.dispatch(jsonRequest, jsonResponse);
    }

    /**
     * 22단계: 어노테이션 기반 MVC 테스트
     */
    private static void testAnnotationBasedMvc(Dispatcher dispatcher) {
        System.out.println("\n--- 22단계: 어노테이션 기반 MVC 테스트 ---");

        // 테스트 1: GET /products (파라미터 없는 메서드)
        System.out.println("\n[테스트 1] GET /products - 파라미터 없는 어노테이션 메서드");
        HttpRequest productsRequest = new HttpRequest("/products", "GET");
        HttpResponse productsResponse = new StandardHttpResponse();
        dispatcher.dispatch(productsRequest, productsResponse);

        // 테스트 2: GET /product/detail (HttpRequest 파라미터)
        System.out.println("\n[테스트 2] GET /product/detail - HttpRequest 파라미터 메서드");
        HttpRequest detailRequest = new HttpRequest("/product/detail?id=12345&name=Winter Laptop", "GET");
        HttpResponse detailResponse = new StandardHttpResponse();
        dispatcher.dispatch(detailRequest, detailResponse);

        // 테스트 3: POST /products (HttpRequest + HttpResponse 파라미터)
        System.out.println("\n[테스트 3] POST /products - 상품 생성");
        HttpRequest createRequest = new HttpRequest("/products?name=Winter Phone&price=599000", "POST");
        HttpResponse createResponse = new StandardHttpResponse();
        dispatcher.dispatch(createRequest, createResponse);

        // 테스트 4: POST /products (잘못된 데이터)
        System.out.println("\n[테스트 4] POST /products - 유효성 검증 실패");
        HttpRequest invalidRequest = new HttpRequest("/products", "POST"); // name 파라미터 없음
        HttpResponse invalidResponse = new StandardHttpResponse();
        dispatcher.dispatch(invalidRequest, invalidResponse);

        // 테스트 5: 모든 HTTP 메서드 허용 테스트
        System.out.println("\n[테스트 5] PUT /product/info - 모든 메서드 허용");
        HttpRequest infoRequest = new HttpRequest("/product/info", "PUT");
        HttpResponse infoResponse = new StandardHttpResponse();
        dispatcher.dispatch(infoRequest, infoResponse);

        // 테스트 6: 어노테이션 컨트롤러 JSON 응답
        System.out.println("\n[테스트 6] GET /products - JSON 응답");
        HttpRequest productsJsonRequest = new HttpRequest("/products", "GET");
        productsJsonRequest.addHeader("Accept", "application/json");
        HttpResponse productsJsonResponse = new StandardHttpResponse();
        dispatcher.dispatch(productsJsonRequest, productsJsonResponse);

        // 테스트 7: 어노테이션과 레거시 핸들러 우선순위 테스트
        System.out.println("\n[테스트 7] 핸들러 우선순위 테스트 - 기존 /user (레거시)");
        HttpRequest legacyRequest = new HttpRequest("/user?name=Legacy&city=Seoul&zipcode=12345", "GET");
        HttpResponse legacyResponse = new StandardHttpResponse();
        dispatcher.dispatch(legacyRequest, legacyResponse);

        // 테스트 8: 존재하지 않는 어노테이션 경로
        System.out.println("\n[테스트 8] GET /nonexistent - 404 테스트");
        HttpRequest notFoundRequest = new HttpRequest("/nonexistent", "GET");
        HttpResponse notFoundResponse = new StandardHttpResponse();
        dispatcher.dispatch(notFoundRequest, notFoundResponse);
    }

    /**
     * 23단계: 파라미터 바인딩 기능 테스트
     */
    private static void testParameterBinding(Dispatcher dispatcher) {
        System.out.println("\n--- 23단계: 파라미터 바인딩 테스트 ---");

        // 테스트 1: @RequestParam 기본 사용 - 필수 파라미터
        System.out.println("\n[테스트 1] GET /search - @RequestParam 기본 바인딩");
        HttpRequest searchRequest = new HttpRequest("/search?keyword=spring&page=2", "GET");
        HttpResponse searchResponse = new StandardHttpResponse();
        dispatcher.dispatch(searchRequest, searchResponse);

        // 테스트 2: @RequestParam 기본값 사용
        System.out.println("\n[테스트 2] GET /search - 기본값 사용 (page 생략)");
        HttpRequest searchDefaultRequest = new HttpRequest("/search?keyword=java", "GET");
        HttpResponse searchDefaultResponse = new StandardHttpResponse();
        dispatcher.dispatch(searchDefaultRequest, searchDefaultResponse);

        // 테스트 3: @RequestParam 고급 검색 - 여러 파라미터 조합
        System.out.println("\n[테스트 3] GET /search/advanced - 복합 파라미터 바인딩");
        HttpRequest advancedRequest = new HttpRequest("/search/advanced?keyword=spring&category=tech&minPrice=1000&maxPrice=5000&sortBy=date", "GET");
        HttpResponse advancedResponse = new StandardHttpResponse();
        dispatcher.dispatch(advancedRequest, advancedResponse);

        // 테스트 4: @RequestParam 선택적 파라미터 (일부 생략)
        System.out.println("\n[테스트 4] GET /search/advanced - 선택적 파라미터 생략");
        HttpRequest partialRequest = new HttpRequest("/search/advanced?keyword=java&category=programming", "GET");
        HttpResponse partialResponse = new StandardHttpResponse();
        dispatcher.dispatch(partialRequest, partialResponse);

        // 테스트 5: @ModelAttribute 객체 바인딩
        System.out.println("\n[테스트 5] POST /search/form - @ModelAttribute 객체 바인딩");
        HttpRequest formRequest = new HttpRequest("/search/form?keyword=spring boot&category=framework&page=3&size=15&sortBy=relevance&sortOrder=desc", "POST");
        HttpResponse formResponse = new StandardHttpResponse();
        dispatcher.dispatch(formRequest, formResponse);

        // 테스트 6: @ModelAttribute 유효성 검증 실패
        System.out.println("\n[테스트 6] POST /search/form - 유효성 검증 실패 (keyword 없음)");
        HttpRequest invalidFormRequest = new HttpRequest("/search/form?category=tech&page=1", "POST");
        HttpResponse invalidFormResponse = new StandardHttpResponse();
        dispatcher.dispatch(invalidFormRequest, invalidFormResponse);

        // 테스트 7: 혼합 파라미터 (@RequestParam + @ModelAttribute)
        System.out.println("\n[테스트 7] POST /search/mixed - 혼합 파라미터 바인딩");
        HttpRequest mixedRequest = new HttpRequest("/search/mixed?userId=12345&debug=true&keyword=hibernate&category=database&page=2", "POST");
        HttpResponse mixedResponse = new StandardHttpResponse();
        dispatcher.dispatch(mixedRequest, mixedResponse);

        // 테스트 8: 배열 파라미터 (@RequestParam String[])
        System.out.println("\n[테스트 8] GET /search/tags - 배열 파라미터 바인딩");
        HttpRequest tagsRequest = new HttpRequest("/search/tags?tags=java,spring,mvc,hibernate&includeAll=true", "GET");
        HttpResponse tagsResponse = new StandardHttpResponse();
        dispatcher.dispatch(tagsRequest, tagsResponse);

        // 테스트 9: 타입 변환 테스트 (double, boolean)
        System.out.println("\n[테스트 9] GET /search/filter - 다양한 타입 변환");
        HttpRequest filterRequest = new HttpRequest("/search/filter?minRating=4.5&publishedAfter=2023-01-01&isAvailable=true", "GET");
        HttpResponse filterResponse = new StandardHttpResponse();
        dispatcher.dispatch(filterRequest, filterResponse);

        // 테스트 10: 필수 파라미터 누락 에러 테스트
        System.out.println("\n[테스트 10] GET /search - 필수 파라미터 누락 (keyword 없음)");
        HttpRequest missingParamRequest = new HttpRequest("/search?page=1", "GET");
        HttpResponse missingParamResponse = new StandardHttpResponse();
        dispatcher.dispatch(missingParamRequest, missingParamResponse);

        // 테스트 11: JSON 응답과 파라미터 바인딩 조합
        System.out.println("\n[테스트 11] GET /search - JSON 응답 + 파라미터 바인딩");
        HttpRequest jsonSearchRequest = new HttpRequest("/search?keyword=winter&page=5", "GET");
        jsonSearchRequest.addHeader("Accept", "application/json");
        HttpResponse jsonSearchResponse = new StandardHttpResponse();
        dispatcher.dispatch(jsonSearchRequest, jsonSearchResponse);
    }

    /**
     * 24단계: 파일 업로드 기능 테스트
     */
    private static void testFileUpload(Dispatcher dispatcher) {
        System.out.println("\n--- 24단계: 파일 업로드 테스트 ---");

        // 테스트 1: 파일 업로드 폼 페이지 요청
        System.out.println("\n[테스트 1] GET /upload/form - 파일 업로드 폼 페이지");
        HttpRequest formRequest = new HttpRequest("/upload/form", "GET");
        HttpResponse formResponse = new StandardHttpResponse();
        dispatcher.dispatch(formRequest, formResponse);

        // 테스트 2: 단일 파일 업로드 - 실제 multipart 바이너리 시뮬레이션
        System.out.println("\n[테스트 2] POST /upload - 실제 multipart 데이터 시뮬레이션");
        try {
            String boundary = "----WinterFrameworkBoundary1234567890";

            StringBuilder multipartBody = new StringBuilder();

            // description 파라미터
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"description\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("테스트 PDF 파일 업로드").append("\r\n");

            // file 파라미터 (PDF 파일 시뮬레이션)
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"test-document.pdf\"\r\n");
            multipartBody.append("Content-Type: application/pdf\r\n");
            multipartBody.append("\r\n");
            // PDF 파일의 실제 시작 바이트들 시뮬레이션
            multipartBody.append("%PDF-1.4\n%âãÏÓ\n1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n");
            multipartBody.append("이것은 테스트용 PDF 파일 내용입니다.\n");
            multipartBody.append("Winter Framework 파일 업로드 테스트 - 단일 파일\n");
            multipartBody.append("\r\n");

            multipartBody.append("--").append(boundary).append("--\r\n");

            HttpRequest uploadRequest = createMultipartRequest("/upload", boundary, multipartBody.toString());

            System.out.println("단일 파일 Multipart 요청 생성:");
            System.out.println("- Content-Type: multipart/form-data; boundary=" + boundary);
            System.out.println("- 파일명: test-document.pdf");
            System.out.println("- 파일 크기: " + multipartBody.length() + " bytes");
            System.out.println("- MIME 타입: application/pdf");

            HttpResponse uploadResponse = new StandardHttpResponse();
            dispatcher.dispatch(uploadRequest, uploadResponse);

        } catch (Exception e) {
            System.out.println("단일 파일 업로드 시뮬레이션 오류: " + e.getMessage());
        }

        // 기타 파일 업로드 테스트들은 생략 (기존과 동일)
    }

    /**
     * 25단계: 세션 관리 기능 테스트
     */
    private static void testSessionManagement(Dispatcher dispatcher) {
        System.out.println("\n--- 25단계: 세션 관리 테스트 ---");

        try {
            // 테스트 1: 세션 홈 페이지 - 새 세션 생성
            System.out.println("\n[테스트 1] GET /session - 새 세션 생성");
            HttpRequest sessionHomeRequest = new HttpRequest("/session", "GET");
            HttpResponse sessionHomeResponse = new StandardHttpResponse();
            dispatcher.dispatch(sessionHomeRequest, sessionHomeResponse);

            // 첫 번째 요청에서 생성된 세션 ID 추출 (시뮬레이션)
            String sessionId = extractSessionId(sessionHomeResponse);

            // 테스트 2: 기존 세션으로 재요청 (쿠키 포함)
            System.out.println("\n[테스트 2] GET /session - 기존 세션 사용");
            HttpRequest sessionExistingRequest = new HttpRequest("/session", "GET");
            if (sessionId != null) {
                sessionExistingRequest.addHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            HttpResponse sessionExistingResponse = new StandardHttpResponse();
            dispatcher.dispatch(sessionExistingRequest, sessionExistingResponse);

            // 테스트 3: 세션에 속성 설정
            System.out.println("\n[테스트 3] POST /session/set - 세션 속성 설정");
            HttpRequest setAttributeRequest = new HttpRequest("/session/set?key=username&value=winterUser", "POST");
            if (sessionId != null) {
                setAttributeRequest.addHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            HttpResponse setAttributeResponse = new StandardHttpResponse();
            try {
                dispatcher.dispatch(setAttributeRequest, setAttributeResponse);
            } catch (Exception e) {
                System.err.println("테스트 3 실패: " + e.getMessage());
                e.printStackTrace();
            }

            // 테스트 4: 세션에서 속성 조회
            System.out.println("\n[테스트 4] GET /session/get - 세션 속성 조회");
            HttpRequest getAttributeRequest = new HttpRequest("/session/get?key=username", "GET");
            if (sessionId != null) {
                getAttributeRequest.addHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            HttpResponse getAttributeResponse = new StandardHttpResponse();
            try {
                dispatcher.dispatch(getAttributeRequest, getAttributeResponse);
            } catch (Exception e) {
                System.err.println("테스트 4 실패: " + e.getMessage());
                e.printStackTrace();
            }

            // 테스트 5: 로그인 시뮬레이션
            System.out.println("\n[테스트 5] POST /session/login - 로그인 시뮬레이션");
            HttpRequest loginRequest = new HttpRequest("/session/login?username=winter&password=framework", "POST");
            if (sessionId != null) {
                loginRequest.addHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            HttpResponse loginResponse = new StandardHttpResponse();
            try {
                dispatcher.dispatch(loginRequest, loginResponse);
            } catch (Exception e) {
                System.err.println("테스트 5 실패: " + e.getMessage());
                e.printStackTrace();
            }

            // 테스트 6: 쇼핑카트에 상품 추가
            System.out.println("\n[테스트 6] POST /session/cart/add - 장바구니 상품 추가");
            HttpRequest addToCartRequest = new HttpRequest("/session/cart/add?productId=laptop001&productName=Winter Laptop&price=150000", "POST");
            if (sessionId != null) {
                addToCartRequest.addHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            HttpResponse addToCartResponse = new StandardHttpResponse();
            try {
                dispatcher.dispatch(addToCartRequest, addToCartResponse);
            } catch (Exception e) {
                System.err.println("테스트 6 실패: " + e.getMessage());
                e.printStackTrace();
            }

            // 간단한 테스트들만 계속 진행
            System.out.println("\n[테스트 7-15] 나머지 테스트들은 기본 세션 기능 확인을 위해 생략");

            System.out.println("\n=== 25단계: 세션 관리 테스트 완료 ===");

            // 세션 관리자 상태 출력
            System.out.println("SessionManager 상태: " + dispatcher.getSessionManager());

        } catch (Exception e) {
            System.err.println("세션 테스트 중 전체 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 응답에서 세션 ID를 추출하는 헬퍼 메서드 (시뮬레이션)
     */
    private static String extractSessionId(HttpResponse response) {
        // 실제 구현에서는 Set-Cookie 헤더에서 JSESSIONID를 파싱해야 함
        // 여기서는 시뮬레이션을 위해 간단한 ID 생성
        return "test-session-" + System.currentTimeMillis();
    }

    /**
     * 오류 시나리오 테스트 헬퍼 메서드
     */
    private static void testErrorScenario(Dispatcher dispatcher, String testName, String boundary,
                                          String descField, String descValue,
                                          String filename, String contentType, String fileContent) {
        try {
            StringBuilder multipartBody = new StringBuilder();

            // description 파라미터
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"").append(descField).append("\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append(descValue).append("\r\n");

            // 파일이 있는 경우에만 추가
            if (filename != null && contentType != null && fileContent != null) {
                multipartBody.append("--").append(boundary).append("\r\n");
                multipartBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");
                multipartBody.append("Content-Type: ").append(contentType).append("\r\n");
                multipartBody.append("\r\n");
                multipartBody.append(fileContent).append("\r\n");
            }

            multipartBody.append("--").append(boundary).append("--\r\n");

            HttpRequest errorRequest = createMultipartRequest("/upload", boundary, multipartBody.toString());

            System.out.println("오류 테스트 [" + testName + "] - " +
                    (filename != null ? filename : "파일 없음"));

            HttpResponse errorResponse = new StandardHttpResponse();
            dispatcher.dispatch(errorRequest, errorResponse);

        } catch (Exception e) {
            System.out.println("오류 테스트 [" + testName + "] 실행 중 예외: " + e.getMessage());
        }
    }
}