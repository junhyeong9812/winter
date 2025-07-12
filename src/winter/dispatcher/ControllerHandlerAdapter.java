package winter.dispatcher;

/*Controller 인터페이스를 지원하는 핸들러 어댑터
* 실제로 handle()을 호출하는 역할*/
public class ControllerHandlerAdapter implements HandlerAdapter{
    @Override
    public boolean supports(Object handler){
        return handler instanceof Controller;
    }

    @Override
    public void handle(Object handler){
        ((Controller) handler).handle();
    }//이처럼 안전하게 캐스팅하여 실행

}
