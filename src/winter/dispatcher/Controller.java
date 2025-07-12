package winter.dispatcher;

/*모든 컨트롤러는 이 인터페이스를 구현해야 한다.
* Dispatcher는 Controller인터페이스만 보고
* handle()을 호출한다.*/
public interface Controller {
    void handle();
}
