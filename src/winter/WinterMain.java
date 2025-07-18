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

      // /register - GET 요청
        HttpRequest registerGet = new HttpRequest("/register", "GET");
        HttpResponse registerGetResponse = new HttpResponse();
        dispatcher.dispatch(registerGet, registerGetResponse);

        // /register - POST 요청
        HttpRequest registerPost = new HttpRequest("/register?name=Jun&email=jun@test.com", "POST");
        HttpResponse registerPostResponse = new HttpResponse();
        dispatcher.dispatch(registerPost, registerPostResponse);

        // /register - PUT 요청 (허용되지 않음)
        HttpRequest registerPut = new HttpRequest("/register", "PUT");
        HttpResponse registerPutResponse = new HttpResponse();
        dispatcher.dispatch(registerPut, registerPutResponse);

        // /user 요청 - GET
        HttpRequest userRequest = new HttpRequest("/user?name=Jun&city=Seoul&zipcode=12345", "GET");
        HttpResponse userResponse = new HttpResponse();
        dispatcher.dispatch(userRequest, userResponse);


        // /invalid 요청
        HttpRequest invalidRequest = new HttpRequest("/invalid");
        HttpResponse invalidResponse =new HttpResponse();
        dispatcher.dispatch(invalidRequest,invalidResponse);
    }
}
