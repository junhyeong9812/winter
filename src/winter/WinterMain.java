package winter;

import winter.dispatcher.Dispatcher;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

public class WinterMain {
    public static void main(String[] args){
        System.out.println("WinterFramework Start");

        Dispatcher dispatcher=new Dispatcher();

        // /hello요청
        HttpRequest helloRequest = new HttpRequest("/hello");
        helloRequest.addParameter("name","winter");
        HttpResponse helloResponse =new HttpResponse();
        dispatcher.dispatch(helloRequest,helloResponse);

        // /bye요청
        HttpRequest byeRequest = new HttpRequest("/bye");
        HttpResponse byeResponse = new HttpResponse();
        dispatcher.dispatch(byeRequest,byeResponse);

        // /invalid 요청
        HttpRequest invalidRequest = new HttpRequest("/invalid");
        HttpResponse invalidResponse =new HttpResponse();
        dispatcher.dispatch(invalidRequest,invalidResponse);
    }
}
