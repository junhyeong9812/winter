package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/* 핸들러 실행 전략을 정의하는 인터페이스
* Dispatcher는 핸들러의 실제 실행을 이 어탭터에게 위임한다.*/
public interface HandlerAdapter {
    /*
    * 해당 핸들러를 지원할 수 있는 어댑터인 지 확인*/
    boolean supports(Object handler);

    /*
    * 핸들러 실행,요청,응답 객체를 함께 전달하며,
    * 실행 결과는 ModelANdView로 반환된다.*/
    ModelAndView handle(Object handler, HttpRequest request, HttpResponse response);
}
