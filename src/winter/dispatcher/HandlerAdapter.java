package winter.dispatcher;

/*핸들러 실행 전략을 정의하는 인터페이스
* Dispatcher는 이 Adapter에게 핸들러 실행을 위임한다.*/
public interface HandlerAdapter {
    boolean supports(Object handler);
    //이 핸들러는 멀까?
    void handle(Object handler);
}
