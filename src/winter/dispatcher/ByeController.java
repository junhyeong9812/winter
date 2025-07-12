package winter.dispatcher;

//Bye 요청을 처리하는 핸들러(컨트롤러 역할)
public class ByeController implements Controller {

    @Override
    public void handle(){

        System.out.println("Goodbye from winter Framework");
    }
}
