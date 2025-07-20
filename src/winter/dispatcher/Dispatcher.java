package winter.dispatcher;

import winter.excption.ExceptionResolver;
import winter.excption.SimpleExceptionResolver;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
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
     * 1. HandlerMapping으로 핸들러 조회 (어노테이션 우선, 레거시 대체)
     * 2. HandlerAdapter를 통해 핸들러 실행(ModelAndView반환)
     * 3. ContentNegotiatingViewResolver로 View를 찾고, 모델을 렌더링
     */
    public void dispatch(HttpRequest request, HttpResponse response) {
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
            response.setStatus(404);
            response.setBody("404 Not Found: " + requestPath);
            response.send();
            return;
        }

        // 3. 적절한 HandlerAdapter 찾기 및 실행
        for (HandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                try {
                    // 핸들러 실행
                    ModelAndView mv = adapter.handle(handler, request, response);

                    // null 대응
                    if (mv == null) {
                        response.send();
                        return;
                    }

                    // Content Negotiating ViewResolver 사용
                    ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
                    viewResolver.setCurrentRequest(request);
                    View view = viewResolver.resolveViewName(mv.getViewName());

                    // 뷰 렌더링
                    view.render(mv.getModel(), response);

                    response.send();
                    return;

                } catch (Exception ex) {
                    // 디버깅을 위한 상세 에러 출력
                    System.err.println("Handler execution failed: " + ex.getMessage());
                    ex.printStackTrace();

                    // 예외 처리
                    for (ExceptionResolver resolver : exceptionResolvers) {
                        if (resolver.resolveException(request, response, ex)) {
                            response.send();
                            return;
                        }
                    }
                    throw new RuntimeException("Unhandled Exception", ex);
                }
            }
        }

        response.setStatus(500);
        response.setBody("500 Internal Error: No suitable adapter found for handler type: " +
                handler.getClass().getName());
        response.send();
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

            response.send();

        } catch (IOException e) {
            response.setStatus(404);
            response.setBody("Static file not found: " + requestPath);
            response.send();
        }
    }
}