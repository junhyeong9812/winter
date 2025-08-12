package winter.dispatcher;

import winter.excption.ExceptionResolver;
import winter.excption.SimpleExceptionResolver;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.HttpSession;
import winter.interceptor.*;  // 27단계 추가: 인터셉터 패키지 import
import winter.session.SessionConfig;
import winter.session.SessionManager;
import winter.upload.MultipartParser;
import winter.upload.MultipartRequest;
import winter.view.IntegratedViewResolver;
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
 * 27단계 업데이트: HandlerInterceptor 체인 구조 구현
 * - InterceptorChain을 통한 인터셉터 관리
 * - preHandle → HandlerAdapter → postHandle → ViewResolver → afterCompletion 순서
 * - 예외 발생 시에도 afterCompletion 보장
 * - 다양한 횡단 관심사 처리 (로깅, 인증, 성능 측정, CORS 등)
 *
 * 26단계 업데이트: View Engine Integration 지원
 * - IntegratedViewResolver를 사용하여 다양한 뷰 엔진 통합 지원
 * - SimpleTemplate, Thymeleaf, Mustache, JSP 등 다양한 뷰 엔진 자동 선택
 * - 템플릿 확장자에 따른 적절한 뷰 엔진 매핑
 *
 * 25단계 업데이트: 세션 관리 기능 추가
 * - SessionManager를 통한 세션 생명주기 관리
 * - 세션 쿠키 자동 설정 및 갱신
 * - 세션 기반 상태 관리 지원
 *
 * 24단계 업데이트: Multipart 파일 업로드 지원
 * - multipart/form-data 요청 감지 및 파싱
 * - MultipartRequest로 변환하여 파일 업로드 처리
 * - 기존 모든 기능과 완전 호환
 *
 * 22단계 업데이트: 어노테이션 기반 MVC 지원
 * - CombinedHandlerMapping으로 레거시와 어노테이션 방식 통합
 * - AnnotationHandlerAdapter 추가로 HandlerMethod 실행 지원
 * - 기존 기능들과 완전 호환
 */
public class Dispatcher {

    // 통합 핸들러 매핑 (레거시 + 어노테이션)
    private final CombinedHandlerMapping handlerMapping = new CombinedHandlerMapping();

    // 다양한 HandlerAdapter 지원 (레거시 + 어노테이션)
    private final List<HandlerAdapter> handlerAdapters = List.of(
            new AnnotationHandlerAdapter(),    // 어노테이션 기반 핸들러 어댑터 (우선순위 높음)
            new ControllerHandlerAdapter()     // 레거시 Controller 인터페이스 어댑터
    );

    // 다양한 ExceptionResolver 지원 가능
    private final List<ExceptionResolver> exceptionResolvers = List.of(
            new SimpleExceptionResolver()
    );

    // 27단계 추가: 인터셉터 체인
    private final InterceptorChain interceptorChain = new InterceptorChain();

    // 25단계: 세션 관리자 추가
    private final SessionManager sessionManager;

    // 정적 리소스 기본 경로 설정
    private final String staticBasePath = "src/winter/static";

    /**
     * Dispatcher 생성자
     * 핸들러 등록은 CombinedHandlerMapping에 완전 위임
     * 27단계: 기본 인터셉터들 등록 추가
     * 25단계: 세션 관리자 초기화 추가
     */
    public Dispatcher() {
        // 세션 설정 초기화
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setMaxInactiveInterval(1800); // 30분
        sessionConfig.setCookieName("JSESSIONID");
        sessionConfig.setCookieHttpOnly(true);
        sessionConfig.setCookieSecure(false); // 개발환경에서는 false
        sessionConfig.setCleanupInterval(300); // 5분마다 정리

        this.sessionManager = new SessionManager(sessionConfig);

        // 27단계: 기본 인터셉터들 등록
        setupDefaultInterceptors();

        // 등록된 핸들러 정보 출력 (디버깅용)
        handlerMapping.printRegisteredHandlers();

        System.out.println("SessionManager 초기화 완료: " + sessionManager);
        System.out.println("InterceptorChain 초기화 완료: " + interceptorChain);
    }

    /**
     * 27단계: 기본 인터셉터들을 등록합니다.
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
     * 27단계: 외부에서 추가 인터셉터를 등록할 수 있는 메서드
     *
     * @param interceptor 추가할 인터셉터
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
        System.out.println("사용자 정의 인터셉터 추가: " + interceptor.getClass().getSimpleName());
    }

    /**
     * 외부에서 어노테이션 컨트롤러를 추가로 등록할 수 있는 메서드
     * 테스트나 동적 등록이 필요한 경우에만 사용
     *
     * @param controllerClass @Controller 어노테이션이 붙은 클래스
     */
    public void registerController(Class<?> controllerClass) {
        handlerMapping.registerAnnotationController(controllerClass);
    }

    /**
     * 세션 관리자를 반환합니다.
     *
     * @return SessionManager 인스턴스
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * 인터셉터 체인을 반환합니다.
     *
     * @return InterceptorChain 인스턴스
     */
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    /**
     * 요청을 처리하는 핵심 메서드
     *
     * 27단계 업데이트된 처리 흐름:
     * 0. 세션 처리 (쿠키에서 세션 ID 추출, 세션 생성/조회) (25단계 추가)
     * 1. Multipart 요청 감지 및 파싱 (24단계)
     * 2. HandlerMapping으로 핸들러 조회 (어노테이션 우선, 레거시 대체)
     * 3. 인터셉터 체인의 preHandle 실행 (27단계 추가)
     * 4. HandlerAdapter를 통해 핸들러 실행(ModelAndView반환)
     * 5. 인터셉터 체인의 postHandle 실행 (27단계 추가)
     * 6. IntegratedViewResolver로 View를 찾고, 모델을 렌더링 (26단계 수정)
     * 7. 인터셉터 체인의 afterCompletion 실행 (27단계 추가)
     * 8. 세션 쿠키 설정 (25단계 추가)
     */
    public void dispatch(HttpRequest request, HttpResponse response) {
        Object handler = null;
        Exception dispatchException = null;

        try {
            System.out.println("\n=== 27단계: 인터셉터 체인 요청 처리 시작 ===");
            System.out.println("Method: " + request.getMethod());
            System.out.println("Path: " + request.getPath());
            System.out.println("Content-Type: " + request.getHeader("Content-Type"));

            // 0. 25단계: 세션 처리
            handleSession(request, response);

            // 1. Multipart 요청 감지 및 파싱 (24단계)
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

            // 2. 정적 리소스 처리 우선
            if (requestPath.startsWith("/static/")) {
                handleStaticResource(requestPath, response);
                return;
            }

            // 3. 핸들러 매핑 (어노테이션 우선, 레거시 대체)
            handler = handlerMapping.getHandler(requestPath, requestMethod);

            if (handler == null) {
                System.out.println("핸들러를 찾을 수 없음: " + requestPath);
                response.setStatus(404);
                response.setBody("404 Not Found: " + requestPath);
                response.send();
                return;
            }

            System.out.println("핸들러 발견: " + handler.getClass().getSimpleName());

            // 4. 27단계: 인터셉터 체인의 preHandle 실행
            if (!interceptorChain.applyPreHandle(request, response, handler)) {
                System.out.println("인터셉터 preHandle에서 요청 처리 중단됨");
                response.send();
                return;
            }

            // 5. 적절한 HandlerAdapter 찾기 및 실행
            ModelAndView mv = null;
            for (HandlerAdapter adapter : handlerAdapters) {
                if (adapter.supports(handler)) {
                    System.out.println("사용할 어댑터: " + adapter.getClass().getSimpleName());

                    // 핸들러 실행
                    mv = adapter.handle(handler, request, response);
                    break;
                }
            }

            // 6. 27단계: 인터셉터 체인의 postHandle 실행
            interceptorChain.applyPostHandle(request, response, handler, mv);

            // 7. 뷰 처리 (ModelAndView가 있는 경우)
            if (mv != null) {
                System.out.println("ModelAndView 생성: " + mv.getViewName());

                // 26챕터 수정: IntegratedViewResolver 사용
                IntegratedViewResolver viewResolver = new IntegratedViewResolver();
                viewResolver.setCurrentRequest(request);
                View view = viewResolver.resolveViewName(mv.getViewName());

                System.out.println("뷰 해결: " + view.getClass().getSimpleName());

                // 26챕터 수정: 뷰 렌더링 시 HttpRequest도 함께 전달
                view.render(mv.getModel(), request, response);
            } else {
                System.out.println("ModelAndView가 null - 직접 응답 처리됨");
            }

            response.send();
            System.out.println("=== 27단계: 인터셉터 체인 요청 처리 완료 ===");

        } catch (Exception e) {
            dispatchException = e;

            // 예외 처리에서 Multipart 관련 오류도 처리
            System.err.println("요청 처리 중 오류 발생: " + e.getMessage());
            if (e.getMessage() != null &&
                    (e.getMessage().contains("multipart") || e.getMessage().contains("boundary"))) {
                System.err.println("Multipart 파싱 오류 - Content-Type 확인 필요");
            }

            // ExceptionResolver를 통한 예외 처리
            for (ExceptionResolver resolver : exceptionResolvers) {
                if (resolver.resolveException(request, response, e)) {
                    response.send();
                    return;
                }
            }

            // 기본 에러 응답
            response.setStatus(500);
            response.setBody("Internal Server Error: " + e.getMessage());
            response.send();

            // 디버깅을 위한 상세 에러 출력
            System.err.println("Handler execution failed: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // 8. 27단계: 인터셉터 체인의 afterCompletion 실행 (finally 블록에서 반드시 실행)
            try {
                interceptorChain.triggerAfterCompletion(request, response, handler, dispatchException);
            } catch (Exception afterException) {
                // afterCompletion에서 발생한 예외는 로깅만 하고 전파하지 않음
                System.err.println("afterCompletion 실행 중 예외 발생: " + afterException.getMessage());
                afterException.printStackTrace();
            }
        }
    }

    /**
     * 25단계: 세션 처리 메서드
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
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
     * 요청이 Multipart 요청인지 확인합니다.
     *
     * @param request HTTP 요청
     * @return multipart/form-data 요청이면 true
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
     * Multipart 요청 정보를 로깅합니다.
     *
     * @param request Multipart 요청
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
     * 정적 리소스 처리 (CSS, JS, 이미지 등)
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
     * Dispatcher 종료 시 세션 관리자 정리
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
    }
}