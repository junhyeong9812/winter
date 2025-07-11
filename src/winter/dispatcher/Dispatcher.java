package winter.dispatcher;


/*Dispatcher는 클라이언트 요청을 받아서 해당 핸들러(컨트롤러)로 전달하는
 * 핵심 클래스
 * 실제 요청 URL에 따라 어떤 메서드를 실행할 지 라우팅 책임을 가진다.*/

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    //요청 URL과 컨트롤러(핸들러) 객체를 매핑하는 역할
    private final Map<String,Object> handlerMapping=new HashMap<>();

    public Dispatcher(){
        //임시 라우팅 등록(핸들러 직접 연결)
        //나중엔 자동 등록 방식으로 리팩토링 예정
        handlerMapping.put("/hello",new HelloController());
        handlerMapping.put("/bye",new ByeController());
    }

    //요청을 처리하는 메서드
    public void dispatch(String requestPath){
        Object handler = handlerMapping.get(requestPath);
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
