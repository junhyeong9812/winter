package winter.dispatcher;

import winter.view.SimpleViewResolver;
import winter.view.View;
import winter.view.ViewResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Dispatcher는 클라이언트 요청을 받아,
* HandlerMapping을 통해 핸들러를 찾아 실행하는 중심 역할을 수행한다.
*/
public class Dispatcher {

    private final HandlerMapping handlerMapping=new HandlerMapping();

    //여러 HandlerAdapter 지원 가능
    private final List<HandlerAdapter> handlerAdapters = List.of(
            new ControllerHandlerAdapter()
    );

    //요청을 처리하는 메서드
    public void dispatch(String requestPath){
        Object handler = handlerMapping.getHandler(requestPath);
        if(handler == null){
            System.out.println("404 Not Found: " + requestPath);
            return;
        }

        for(HandlerAdapter adapter: handlerAdapters){
            if(adapter.supports(handler)){
                adapter.handle(handler);

                //viewResolver 적용
                ViewResolver viewResolver=new SimpleViewResolver();
                View view = viewResolver.resolveViewName("hello");
                System.out.println("뷰 출력 경로:"+view);

                return;
            }
        }


            System.out.println("500 Internal Error: UnKnown handler type");
    }
}
