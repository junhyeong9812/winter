package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;
import winter.view.SimpleViewResolver;
import winter.view.View;
import winter.view.ViewResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Dispatcher는 클라이언트 요청을 받아,
* HandlerMapping을 통해 핸들러를 찾고,
* HadlerAdapter를 통해 실행한 후,
* ViewResolver로 뷰를 찾아 렌더링하는 프레임워크의 핵심 흐름을 담당한다.
*/
public class Dispatcher {

    private final HandlerMapping handlerMapping=new HandlerMapping();

    //다양한 HandlerAdapter 지원 가능
    private final List<HandlerAdapter> handlerAdapters = List.of(
            new ControllerHandlerAdapter()
    );

    /*
    * 요청을 처리하는 핵심 메서드
    * 1. HandlerMapping으로 핸들러 조회
    * 2. HandlerAdapter를 통해 핸들러 실행(ModelAndView반환)
    * 3.viewResolver로 View를 찾고, 모델을 렌더링*/
    public void dispatch(HttpRequest request, HttpResponse response){
        String requestPath = request.getPath();
        Object handler = handlerMapping.getHandler(requestPath);

        if(handler == null){
            response.setStatus(404);
            response.setBody("404 Not Found"+requestPath);
            response.send();
            return;
        }

        for(HandlerAdapter adapter: handlerAdapters){
            if(adapter.supports(handler)){
                //1. 핸들러 실행 (request,response 전달)
                ModelAndView mv =adapter.handle(handler,request,response);

                //2. 뷰 이름 ->View 객체로 변환
                ViewResolver viewResolver= new SimpleViewResolver();
                View view=viewResolver.resolveViewName(mv.getViewName());

                //3.모델 전달하여 뷰 렌더링
                view.render(mv.getModel(), response);

                response.send();
                return;
            }
        }

        response.setStatus(500);
        response.setBody("500 Internal Error:Unknown handler type");
        response.send();
    }
}
