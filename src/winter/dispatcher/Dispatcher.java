package winter.dispatcher;

import winter.excption.ExceptionResolver;
import winter.excption.SimpleExceptionResolver;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatcher는 클라이언트 요청을 받아,
 * HandlerMapping을 통해 핸들러를 찾고,
 * HandlerAdapter를 통해 실행한 후,
 * ViewResolver로 뷰를 찾아 렌더링하는 프레임워크의 핵심 흐름을 담당한다.
 *
 * 21단계 업데이트: JSON 응답 지원
 * - ContentNegotiatingViewResolver 도입
 * - Accept 헤더 기반 View 선택 (HTML vs JSON)
 */
public class Dispatcher {

    private final HandlerMapping handlerMapping=new HandlerMapping();

    //다양한 HandlerAdapter 지원 가능
    private final List<HandlerAdapter> handlerAdapters = List.of(
            new ControllerHandlerAdapter()
    );

    // 다양한 ExceptionResolver 지원 가능
    private final List<ExceptionResolver> exceptionResolvers = List.of(
            new SimpleExceptionResolver()
    );

    //정절 리소스 기본 경로 설정 (src/winter/static)
    private final String staticBasePath= "src/winter/static";

    /*
    * 요청을 처리하는 핵심 메서드
    * 1. HandlerMapping으로 핸들러 조회
    * 2. HandlerAdapter를 통해 핸들러 실행(ModelAndView반환)
    * 3.viewResolver로 View를 찾고, 모델을 렌더링*/
    public void dispatch(HttpRequest request, HttpResponse response) {
        String requestPath = request.getPath();

        //1. 정적 리소스 처리 우선
        if (requestPath.startsWith("/static/")) {
            handleStaticResource(requestPath, response);
            return;
        }

        //2.핸들러 매핑(Controller or RestController)
        Object handler = handlerMapping.getHandler(requestPath);

        if (handler == null) {
            response.setStatus(404);
            response.setBody("404 Not Found" + requestPath);
            response.send();
            return;
        }

        for (HandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                try {
                    //1. 핸들러 실행 (request,response 전달)
                    ModelAndView mv = adapter.handle(handler, request, response);

                    //2. null 대응
                    if (mv == null) {
                        response.send();
                        return;
                    }

                    //3. Content Negotiating ViewResolver 사용
                    // Accept 헤더를 기반으로 HTML 또는 JSON View 선택
                    ContentNegotiatingViewResolver viewResolver =new ContentNegotiatingViewResolver();
                    viewResolver.setCurrentRequest(request);
                    View view = viewResolver.resolveViewName(mv.getViewName());

                    //4.모델 전달하여 뷰 렌더링
                    view.render(mv.getModel(), response);

                    response.send();
                    return;
                } catch (Exception ex) {
                    for (ExceptionResolver resolver : exceptionResolvers) {
                        if (resolver.resolveException(request, response, ex)) {
                            response.send();
                            return;
                        }
                    }
                    // ExceptionResolver가 처리하지 못하면 다시 던짐
                    throw new RuntimeException("Unhandled Exception", ex);
                }
            }

            response.setStatus(500);
            response.setBody("500 Internal Error:Unknown handler type");
            response.send();
        }
    }

    //별도로 분리한 정적 리소스 처리
    private void handleStaticResource(String requestPath,HttpResponse response){
        try {
            //요청 경로에서 static제거 후 실제 파일 시스템 경로 생성
            String relativePath = requestPath.replaceFirst("/static/", "");
            String filePath = staticBasePath + "/" + relativePath;
            String content = Files.readString(Paths.get(filePath));
            response.setStatus(200);
            response.setBody(content);

            //(선택) Content-Type
            if(filePath.endsWith(".css")){
                response.addHeader("Content-Type","text/css");
            }else if (filePath.endsWith(".js")){
                response.addHeader("Content-Type", "application/javascript");
            }else if (filePath.endsWith(".html")){
                response.addHeader("Content-Type", "text/html");
            }

            response.send();

        } catch (IOException e){
            response.setStatus(404);
            response.setBody("Static file not found: "+requestPath);
            response.send();
        }
    }
}
