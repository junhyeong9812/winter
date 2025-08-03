package winter.dispatcher;

import winter.excption.ExceptionResolver;
import winter.excption.SimpleExceptionResolver;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.upload.MultipartParser;
import winter.upload.MultipartRequest;
import winter.view.ContentNegotiatingViewResolver;
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

    // 정적 리소스 기본 경로 설정
    private final String staticBasePath = "src/winter/static";

    /**
     * Dispatcher 생성자
     * 핸들러 등록은 CombinedHandlerMapping에 완전 위임
     */
    public Dispatcher() {
        // 등록된 핸들러 정보 출력 (디버깅용)
        handlerMapping.printRegisteredHandlers();
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
     * 요청을 처리하는 핵심 메서드
     *
     * 24단계 업데이트된 처리 흐름:
     * 0. Multipart 요청 감지 및 파싱 (24단계 추가)
     * 1. HandlerMapping으로 핸들러 조회 (어노테이션 우선, 레거시 대체)
     * 2. HandlerAdapter를 통해 핸들러 실행(ModelAndView반환)
     * 3. ContentNegotiatingViewResolver로 View를 찾고, 모델을 렌더링
     */
    public void dispatch(HttpRequest request, HttpResponse response) {
        try {
            System.out.println("\n=== 요청 처리 시작 ===");
            System.out.println("Method: " + request.getMethod());
            System.out.println("Path: " + request.getPath());
            System.out.println("Content-Type: " + request.getHeader("Content-Type"));

            // 0. Multipart 요청 감지 및 파싱 (24단계 추가)
            if (isMultipartRequest(request)) {
                System.out.println("Multipart 요청 감지 - 파싱 시작");
                request = MultipartParser.parseRequest(request);
                System.out.println("Multipart 파싱 완료: " + request.getClass().getSimpleName());

                // MultipartRequest인 경우 파일 정보 출력
                if (request instanceof MultipartRequest) {
                    logMultipartInfo((MultipartRequest) request);
                }
            }

            String requestPath = request.getPath();
            String requestMethod = request.getMethod();

            // 1. 정적 리소스 처리 우선
            if (requestPath.startsWith("/static/")) {
                handleStaticResource(requestPath, response);
                return;
            }

            // 2. 핸들러 매핑 (어노테이션 우선, 레거시 대체)
            Object handler = handlerMapping.getHandler(requestPath, requestMethod);

            if (handler == null) {
                System.out.println("핸들러를 찾을 수 없음: " + requestPath);
                response.setStatus(404);
                response.setBody("404 Not Found: " + requestPath);
                response.send();
                return;
            }

            System.out.println("핸들러 발견: " + handler.getClass().getSimpleName());

            // 3. 적절한 HandlerAdapter 찾기 및 실행
            for (HandlerAdapter adapter : handlerAdapters) {
                if (adapter.supports(handler)) {
                    System.out.println("사용할 어댑터: " + adapter.getClass().getSimpleName());

                    // 핸들러 실행
                    ModelAndView mv = adapter.handle(handler, request, response);

                    // null 대응
                    if (mv == null) {
                        System.out.println("ModelAndView가 null - 직접 응답 처리됨");
                        response.send();
                        return;
                    }

                    System.out.println("ModelAndView 생성: " + mv.getViewName());

                    // Content Negotiating ViewResolver 사용
                    ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
                    viewResolver.setCurrentRequest(request);
                    View view = viewResolver.resolveViewName(mv.getViewName());

                    System.out.println("뷰 해결: " + view.getClass().getSimpleName());

                    // 뷰 렌더링
                    view.render(mv.getModel(), response);

                    response.send();
                    System.out.println("=== 요청 처리 완료 ===");
                    return;
                }
            }

            // 적절한 어댑터를 찾지 못한 경우
            System.err.println("적절한 어댑터를 찾을 수 없음: " + handler.getClass().getName());
            response.setStatus(500);
            response.setBody("500 Internal Error: No suitable adapter found for handler type: " +
                    handler.getClass().getName());
            response.send();

        } catch (Exception e) {
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

            // 디버깅을 위한 상세 에러 출력 (기존 방식 유지)
            System.err.println("Handler execution failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unhandled Exception", e);
        }
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
}