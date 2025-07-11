package winter.dispatcher;

import java.util.HashMap;
import java.util.Map;

/*
* Dispatcher는 클라이언트 요청을 받아,
* HandlerMapping을 통해 핸들러를 찾아 실행하는 중심 역할을 수행한다.
*/
public class Dispatcher {

    private final HandlerMapping handlerMapping=new HandlerMapping();

    //요청을 처리하는 메서드
    public void dispatch(String requestPath){
        Object handler = handlerMapping.getHandler(requestPath);
        if(handler == null){
            System.out.println("404 Not Found: " + requestPath);
            return;
        }
        //instanceof는 객체가 어떤 클래스의 인스턴스인 지 확인하는 java키워드
        if(handler instanceof HelloController hello){
            hello.handle();
        }else if (handler instanceof ByeController bye){
            bye.handle();
        }else {
            System.out.println("500 Internal Error: UnKnown handler type");
        }
    }
}
