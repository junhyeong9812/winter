package winter.dispatcher;

import winter.excption.ExceptionResolver;
import winter.excption.SimpleExceptionResolver;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.HttpSession;
import winter.http.StandardHttpResponse;  // 29단계 추가: 구체 구현체 import
import winter.interceptor.*;
import winter.session.SessionConfig;
import winter.session.SessionManager;
import winter.upload.MultipartParser;
import winter.upload.MultipartRequest;
import winter.view.ContentNegotiatingViewResolver; // 30챕터: IntegratedViewResolver 대신 사용
import winter.view.ModelAndView;
import winter.view.View;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Dispatcher는 클라이언트 요청을 받아,
 * HandlerMapping을 통해 핸들러를 찾고,
 * HandlerAdapter를 통해 실행한 후,
 * ViewResolver로 뷰를 찾아 렌더링하는 프레임워크의 핵심 흐름을 담당한다.
 *
 * ===== 30챕터 업데이트: REST API 완전 지원 =====
 * - RestHandlerAdapter 추가로 @RestController와 @ResponseBody 완전 지원
 * - ContentNegotiatingViewResolver로 ResponseEntity 자동 처리
 * - JSON 응답과 HTML 템플릿을 동시에 지원하는 하이브리드 아키텍처
 * - 기존 29단계의 모든 기능 완전 유지
 *
 * ===== 29단계 기능들 완전 유지 =====
 * - HttpResponse를 인터페이스로 사용하여 유연성 증대
 * - AnnotationHandlerAdapter에서 ResponseEntity 처리 지원
 * - 기존 27단계의 모든 기능 완전 유지
 *
 * ===== 27단계 기능들 완전 유지 =====
 * - InterceptorChain을 통한 인터셉터 관리
 * - preHandle → HandlerAdapter → postHandle → ViewResolver → afterCompletion 순서
 * - 예외 발생 시에도 afterCompletion 보장
 * - 다양한 횡단 관심사 처리 (로깅, 인증, 성능 측정, CORS 등)
 *
 * ===== 26단계 기능들 완전 유지 =====
 * - 다양한 뷰 엔진 통합 지원 (30챕터: ContentNegotiatingViewResolver로 통합)
 * - SimpleTemplate, Thymeleaf, Mustache, JSP 등 다양한 뷰 엔진 자동 선택
 * - 템플릿 확장자에 따른 적절한 뷰 엔진 매핑
 *
 * ===== 25단계 기능들 완전 유지 =====
 * - SessionManager를 통한 세션 생명주기 관리
 * - 세션 쿠키 자동 설정 및 갱신
 * - 세션 기반 상태 관리 지원
 *
 * ===== 24단계 기능들 완전 유지 =====
 * - multipart/form-data 요청 감지 및 파싱
 * - MultipartRequest로 변환하여 파일 업로드 처리
 * - 기존 모든 기능과 완전 호환
 *
 * ===== 22단계 기능들 완전 유지 =====
 * - CombinedHandlerMapping으로 레거시와 어노테이션 방식 통합
 * - 기존 기능들과 완전 호환 (30챕터: @RestController 추가 지원)
 */
public class Dispatcher {

    // ===== 30챕터 기존 필드들 완전 유지 및 확장 =====

    // 통합 핸들러 매핑 (레거시 + MVC + REST)
    private final CombinedHandlerMapping handlerMapping = new CombinedHandlerMapping();

    // 30챕터 업데이트: RestHandlerAdapter 추가로 완전한 REST 지원
    // 핸들러 어댑터 우선순위: REST → MVC → Legacy
    private final List<HandlerAdapter> handlerAdapters = List.of(
            new RestHandlerAdapter(),         // 30챕터 신규: @RestController와 @ResponseBody 처리 (최우선)
            new AnnotationHandlerAdapter(),   // 29단계: 기존 @Controller 처리 (ModelAndView)
            new ControllerHandlerAdapter()    // 레거시 Controller 인터페이스 어댑터 (변경 없음)
    );

    // 다양한 ExceptionResolver 지원 가능 (27단계와 완전 동일)
    private final List<ExceptionResolver> exceptionResolvers = List.of(
            new SimpleExceptionResolver()
    );

    // 27단계: 인터셉터 체인 (완전 유지)
    private final InterceptorChain interceptorChain = new InterceptorChain();

    // 25단계: 세션 관리자 (완전 유지)
    private final SessionManager sessionManager;

    // 정적 리소스 기본 경로 설정 (완전 유지)
    private final String staticBasePath = "src/winter/static";

    /**
     * 30챕터 업데이트: Dispatcher 생성자
     * 핸들러 등록은 CombinedHandlerMapping에 완전 위임
     * 기본 인터셉터들 등록 및 세션 관리자 초기화
     * REST API 지원 활성화
     */
    public Dispatcher() {
        // 세션 설정 초기화 (25단계와 완전 동일)
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setMaxInactiveInterval(1800); // 30분
        sessionConfig.setCookieName("JSESSIONID");
        sessionConfig.setCookieHttpOnly(true);
        sessionConfig.setCookieSecure(false); // 개발환경에서는 false
        sessionConfig.setCleanupInterval(300); // 5분마다 정리

        this.sessionManager = new SessionManager(sessionConfig);

        // 27단계: 기본 인터셉터들 등록 (완전 유지)
        setupDefaultInterceptors();

        // 등록된 핸들러 정보 출력 (디버깅용)
        handlerMapping.printRegisteredHandlers();

        System.out.println("SessionManager 초기화 완료: " + sessionManager);
        System.out.println("InterceptorChain 초기화 완료: " + interceptorChain);

        // 30챕터: REST API 지원 상태 출력
        System.out.println("\n=== 30챕터: Winter 프레임워크 완전 초기화 ===");
        System.out.println("✅ REST API 지원 활성화");
        System.out.println("✅ @RestController 완전 지원");
        System.out.println("✅ ResponseEntity 완전 지원");
        System.out.println("✅ JSON 자동 직렬화 지원");
        System.out.println("✅ Content Negotiation 지원");
        System.out.println("✅ 기존 MVC 패턴 완전 유지");
        System.out.println("등록된 핸들러 어댑터: " + handlerAdapters.size() + "개");
        System.out.println("  1. RestHandlerAdapter (REST API 전용)");
        System.out.println("  2. AnnotationHandlerAdapter (MVC 전용)");
        System.out.println("  3. ControllerHandlerAdapter (레거시 전용)");
        System.out.println("============================================\n");
    }

    // ===== 27단계 기존 메서드들 완전 유지 =====

    /**
     * 기본 인터셉터들을 등록합니다. (27단계와 완전 동일)
     * 인터셉터 등록 순서가 실행 순서를 결정합니다.
     */
    private void setupDefaultInterceptors() {
        // 1. CORS 인터셉터 (가장 먼저 실행되어야 함)
        interceptorChain.addInterceptor(new CorsInterceptor());

        // 2. 로깅 인터셉터 (모든 요청을 로깅)
        interceptorChain.addInterceptor(new LoggingInterceptor());

        // 3. 성능 측정 인터셉터
        interceptorChain.addInterceptor(new PerformanceInterceptor());

        // 4. 인증 인터셉터 (보안이 필요한 경우)
        interceptorChain.addInterceptor(new AuthenticationInterceptor());

        System.out.println("=== 기본 인터셉터 등록 완료 ===");
        System.out.println("등록된 인터셉터 수: " + interceptorChain.size());
        System.out.println("실행 순서: " + interceptorChain.toString());
    }

    /**
     * 외부에서 추가 인터셉터를 등록할 수 있는 메서드 (27단계와 완전 동일)
     *
     * @param interceptor 추가할 인터셉터
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
        System.out.println("사용자 정의 인터셉터 추가: " + interceptor.getClass().getSimpleName());
    }

    /**
     * 30챕터 업데이트: 외부에서 어노테이션 컨트롤러를 추가로 등록할 수 있는 메서드
     * @Controller와 @RestController 모두 지원
     * 테스트나 동적 등록이 필요한 경우에만 사용
     *
     * @param controllerClass @Controller 또는 @RestController 어노테이션이 붙은 클래스
     */
    public void registerController(Class<?> controllerClass) {
        handlerMapping.registerAnnotationController(controllerClass);
    }

    /**
     * 세션 관리자를 반환합니다. (25단계와 완전 동일)
     *
     * @return SessionManager 인스턴스
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * 인터셉터 체인을 반환합니다. (27단계와 완전 동일)
     *
     * @return InterceptorChain 인스턴스
     */
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    // ===== 29단계 수정: HttpResponse 팩토리 메서드 추가 =====

    /**
     * 29단계 신규: HttpResponse 인스턴스를 생성합니다.
     * 테스트 환경에서는 Mock 객체로 교체 가능하도록 팩토리 패턴 적용
     *
     * @return HttpResponse 구현체
     */
    protected HttpResponse createHttpResponse() {
        return new StandardHttpResponse();
    }

    // ===== 30챕터: 요청 처리 메서드 업데이트 =====

    /**
     * 30챕터 업데이트: 요청을 처리하는 핵심 메서드
     * REST API와 전통적인 MVC를 모두 지원하는 하이브리드 아키텍처
     *
     * ===== 30챕터 처리 흐름 =====
     * 0. 세션 처리 (쿠키에서 세션 ID 추출, 세션 생성/조회) (25단계)
     * 1. Multipart 요청 감지 및 파싱 (24단계)
     * 2. HandlerMapping으로 핸들러 조회 (어노테이션 우선, 레거시 대체)
     * 3. 인터셉터 체인의 preHandle 실행 (27단계)
     * 4. HandlerAdapter를 통해 핸들러 실행 - 30챕터: REST/MVC 자동 판별
     *    - RestHandlerAdapter: @RestController, @ResponseBody 처리 → JSON 응답
     *    - AnnotationHandlerAdapter: @Controller 처리 → HTML 템플릿
     *    - ControllerHandlerAdapter: 레거시 Controller 처리
     * 5. 인터셉터 체인의 postHandle 실행 (27단계)
     * 6. ContentNegotiatingViewResolver로 View를 찾고 렌더링 - 30챕터: REST/MVC 통합
     *    - ResponseEntity → ResponseEntityView (JSON + 상태 코드 + 헤더)
     *    - JsonView → JSON 직렬화
     *    - HTML Templates → 기존 템플릿 엔진들
     * 7. 인터셉터 체인의 afterCompletion 실행 (27단계)
     * 8. 세션 쿠키 설정 (25단계)
     *
     * ===== 30챕터 핵심 변경사항 =====
     * - RestHandlerAdapter를 최우선으로 배치하여 REST API 최적화
     * - ContentNegotiatingViewResolver로 통합된 뷰 해결
     * - ResponseEntity 완전 지원으로 HTTP 제어 강화
     * - JSON과 HTML을 동시에 지원하는 하이브리드 구조
     */
    public void dispatch(HttpRequest request, HttpResponse response) {
        Object handler = null;
        Exception dispatchException = null;

        try {
            System.out.println("\n=== 30챕터: REST + MVC 하이브리드 요청 처리 시작 ===");
            System.out.println("Method: " + request.getMethod());
            System.out.println("Path: " + request.getPath());
            System.out.println("Content-Type: " + request.getHeader("Content-Type"));
            System.out.println("Accept: " + request.getHeader("Accept"));

            // 0. 25단계: 세션 처리 (완전 동일)
            handleSession(request, response);

            // 1. Multipart 요청 감지 및 파싱 (24단계, 완전 동일)
            if (isMultipartRequest(request)) {
                System.out.println("Multipart 요청 감지 - 파싱 시작");
                request = MultipartParser.parseRequest(request);
                System.out.println("Multipart 파싱 완료: " + request.getClass().getSimpleName());

                // MultipartRequest인 경우 파일 정보 출력
                if (request instanceof MultipartRequest) {
                    logMultipartInfo((MultipartRequest) request);
                }

                // Multipart 요청의 경우 세션을 다시 설정
                handleSession(request, response);
            }

            String requestPath = request.getPath();
            String requestMethod = request.getMethod();

            // 2. 정적 리소스 처리 우선 (완전 동일)
            if (requestPath.startsWith("/static/")) {
                handleStaticResource(requestPath, response);
                return;
            }

            // 3. 핸들러 매핑 (어노테이션 우선, 레거시 대체) (30챕터: REST/MVC 모두 포함)
            handler = handlerMapping.getHandler(requestPath, requestMethod);

            if (handler == null) {
                System.out.println("핸들러를 찾을 수 없음: " + requestPath);
                response.setStatus(404);
                response.setBody("404 Not Found: " + requestPath);
                response.send();
                return;
            }

            System.out.println("핸들러 발견: " + handler.getClass().getSimpleName());

            // 4. 27단계: 인터셉터 체인의 preHandle 실행 (완전 동일)
            if (!interceptorChain.applyPreHandle(request, response, handler)) {
                System.out.println("인터셉터 preHandle에서 요청 처리 중단됨");
                response.send();
                return;
            }

            // 5. 30챕터: 적절한 HandlerAdapter 찾기 및 실행 (REST 우선 처리)
            ModelAndView mv = null;
            for (HandlerAdapter adapter : handlerAdapters) {
                if (adapter.supports(handler)) {
                    System.out.println("사용할 어댑터: " + adapter.getClass().getSimpleName());

                    // 30챕터: 어댑터별 처리 방식 로깅
                    if (adapter instanceof RestHandlerAdapter) {
                        System.out.println("30챕터: REST API 처리 - JSON 응답 또는 ResponseEntity");
                    } else if (adapter instanceof AnnotationHandlerAdapter) {
                        System.out.println("MVC 패턴 처리 - ModelAndView 반환");
                    } else {
                        System.out.println("레거시 Controller 처리");
                    }

                    // 핸들러 실행 (30챕터: REST/MVC 자동 판별 처리)
                    mv = adapter.handle(handler, request, response);
                    break;
                }
            }

            // 6. 27단계: 인터셉터 체인의 postHandle 실행 (완전 동일)
            interceptorChain.applyPostHandle(request, response, handler, mv);

            // 7. 30챕터: 뷰 처리 (ContentNegotiatingViewResolver 사용)
            if (mv != null) {
                System.out.println("ModelAndView 생성: " + mv.getViewName());

                // 30챕터: ContentNegotiatingViewResolver 사용 (ResponseEntity + JSON + HTML 통합)
                ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
                viewResolver.setCurrentRequest(request);
                View view = viewResolver.resolveViewName(mv.getViewName());

                System.out.println("뷰 해결: " + view.getClass().getSimpleName());

                // 30챕터: 뷰 렌더링 (REST와 MVC 모두 지원)
                view.render(mv.getModel(), request, response);

                System.out.println("뷰 렌더링 완료 - " + view.getClass().getSimpleName());
            } else {
                System.out.println("ModelAndView가 null - 직접 응답 처리됨 (REST API 직접 응답 등)");
            }

            response.send();
            System.out.println("=== 30챕터: REST + MVC 하이브리드 요청 처리 완료 ===\n");

        } catch (Exception e) {
            dispatchException = e;

            // 예외 처리에서 Multipart 관련 오류도 처리 (24단계와 완전 동일)
            System.err.println("요청 처리 중 오류 발생: " + e.getMessage());
            if (e.getMessage() != null &&
                    (e.getMessage().contains("multipart") || e.getMessage().contains("boundary"))) {
                System.err.println("Multipart 파싱 오류 - Content-Type 확인 필요");
            }

            // ExceptionResolver를 통한 예외 처리 (완전 동일)
            for (ExceptionResolver resolver : exceptionResolvers) {
                if (resolver.resolveException(request, response, e)) {
                    response.send();
                    return;
                }
            }

            // 기본 에러 응답 (완전 동일)
            response.setStatus(500);
            response.setBody("Internal Server Error: " + e.getMessage());
            response.send();

            // 디버깅을 위한 상세 에러 출력 (완전 동일)
            System.err.println("Handler execution failed: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // 8. 27단계: 인터셉터 체인의 afterCompletion 실행 (완전 동일)
            try {
                interceptorChain.triggerAfterCompletion(request, response, handler, dispatchException);
            } catch (Exception afterException) {
                // afterCompletion에서 발생한 예외는 로깅만 하고 전파하지 않음
                System.err.println("afterCompletion 실행 중 예외 발생: " + afterException.getMessage());
                afterException.printStackTrace();
            }
        }
    }

    // ===== 이하 모든 메서드들 27단계와 완전 동일 (변경 없음) =====

    /**
     * 25단계: 세션 처리 메서드 (완전 동일)
     */
    private void handleSession(HttpRequest request, HttpResponse response) {
        // 요청에서 세션 ID 추출
        String requestedSessionId = request.getRequestedSessionId();
        HttpSession session = null;

        if (requestedSessionId != null) {
            // 기존 세션 조회
            session = sessionManager.getSession(requestedSessionId);
            if (session != null) {
                System.out.println("기존 세션 발견: " + session.getId());
            } else {
                System.out.println("요청된 세션 ID가 무효함: " + requestedSessionId);
            }
        }

        // 세션이 없으면 새로 생성
        if (session == null) {
            session = sessionManager.createSession();
            System.out.println("새 세션 생성: " + session.getId());

            // 세션 쿠키 설정
            SessionConfig config = sessionManager.getConfig();
            response.setSessionCookie(
                    session.getId(),
                    config.getMaxInactiveInterval(),
                    config.isCookieSecure(),
                    config.isCookieHttpOnly()
            );
        }

        // 요청에 세션 설정
        request.setSession(session);

        System.out.println("세션 처리 완료 - ID: " + session.getId() +
                ", 새 세션: " + session.isNew() +
                ", 속성 수: " + session.getAttributeNames().asIterator().hasNext());
    }

    /**
     * 요청이 Multipart 요청인지 확인합니다. (24단계와 완전 동일)
     */
    private boolean isMultipartRequest(HttpRequest request) {
        // POST 메서드이면서 Content-Type이 multipart/form-data인 경우
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String contentType = request.getHeader("Content-Type");
        return contentType != null &&
                contentType.toLowerCase().startsWith("multipart/form-data");
    }

    /**
     * Multipart 요청 정보를 로깅합니다. (24단계와 완전 동일)
     */
    private void logMultipartInfo(MultipartRequest request) {
        System.out.println("=== Multipart 요청 정보 ===");
        System.out.println("총 파일 수: " + request.getFileCount());
        System.out.println("총 파일 크기: " + request.getTotalFileSize() + " bytes");

        for (String fieldName : request.getFileNames()) {
            System.out.println("필드 '" + fieldName + "': " + request.getFileCount(fieldName) + "개 파일");
            request.getFiles(fieldName).forEach(file -> {
                System.out.println("  - " + file.getOriginalFilename() +
                        " (" + file.getSize() + " bytes, " + file.getContentType() + ")");
            });
        }
        System.out.println("=======================");
    }

    /**
     * 정적 리소스 처리 (CSS, JS, 이미지 등) (완전 동일)
     */
    private void handleStaticResource(String requestPath, HttpResponse response) {
        try {
            String relativePath = requestPath.replaceFirst("/static/", "");
            String filePath = staticBasePath + "/" + relativePath;
            String content = Files.readString(Paths.get(filePath));
            response.setStatus(200);
            response.setBody(content);

            // Content-Type 설정
            if (filePath.endsWith(".css")) {
                response.addHeader("Content-Type", "text/css");
            } else if (filePath.endsWith(".js")) {
                response.addHeader("Content-Type", "application/javascript");
            } else if (filePath.endsWith(".html")) {
                response.addHeader("Content-Type", "text/html");
            }

            System.out.println("정적 리소스 제공: " + requestPath);
            response.send();

        } catch (IOException e) {
            System.err.println("정적 리소스 찾을 수 없음: " + requestPath);
            response.setStatus(404);
            response.setBody("Static file not found: " + requestPath);
            response.send();
        }
    }

    /**
     * 30챕터 추가: 등록된 REST API 엔드포인트 목록 조회
     * 운영 모니터링이나 API 문서 생성 목적
     *
     * @return REST API 엔드포인트 목록
     */
    public java.util.List<String> getRestApiEndpoints() {
        return handlerMapping.getRestApiEndpoints();
    }

    /**
     * 30챕터 추가: 등록된 MVC 페이지 목록 조회
     * 사이트맵 생성이나 운영 모니터링 목적
     *
     * @return MVC 페이지 엔드포인트 목록
     */
    public java.util.List<String> getMvcPageEndpoints() {
        return handlerMapping.getMvcPageEndpoints();
    }

    /**
     * 30챕터 추가: 프레임워크 상태 정보 반환
     * 운영 모니터링이나 헬스 체크 목적
     *
     * @return 프레임워크 상태 정보
     */
    public java.util.Map<String, Object> getFrameworkStatus() {
        java.util.Map<String, Object> status = new java.util.HashMap<>();

        // 기본 정보
        status.put("version", "30챕터 - REST API 완전 지원");
        status.put("handlerAdapters", handlerAdapters.size());
        status.put("interceptors", interceptorChain.size());
        status.put("exceptionResolvers", exceptionResolvers.size());

        // 핸들러 통계
        java.util.Map<String, Integer> handlerStats = handlerMapping.getAnnotationHandlerMapping().getControllerStats();
        status.put("mvcHandlers", handlerStats.get("mvc"));
        status.put("restHandlers", handlerStats.get("rest"));
        status.put("totalHandlers", handlerStats.get("total"));

        // 세션 정보
        status.put("sessionManagerActive", sessionManager != null);
        if (sessionManager != null) {
            status.put("sessionConfig", sessionManager.getConfig().toString());
        }

        // 지원 기능
        status.put("features", java.util.List.of(
                "REST API (@RestController)",
                "MVC Pattern (@Controller)",
                "ResponseEntity Support",
                "JSON Serialization",
                "Content Negotiation",
                "File Upload (Multipart)",
                "Session Management",
                "Interceptor Chain",
                "Exception Handling",
                "Static Resource Serving",
                "Multiple View Engines"
        ));

        return status;
    }

    /**
     * 30챕터 업데이트: Dispatcher 종료 시 정리 작업
     */
    public void shutdown() {
        if (sessionManager != null) {
            sessionManager.shutdown();
            System.out.println("SessionManager 종료 완료");
        }

        // 27단계: 인터셉터 체인 정리
        if (interceptorChain != null) {
            interceptorChain.clear();
            System.out.println("InterceptorChain 정리 완료");
        }

        System.out.println("30챕터: Winter 프레임워크 종료");
        System.out.println("  ✓ REST API 지원 비활성화");
        System.out.println("  ✓ MVC 패턴 지원 비활성화");
        System.out.println("  ✓ 모든 리소스 정리 완료");
    }
}