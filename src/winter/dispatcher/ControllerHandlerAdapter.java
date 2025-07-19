package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/*
* Controller 인터페이스를 구현한 핸들러를 처리하는 어댑터,
* 실제로 handle(req,res)를 호출하여 modelAndView를 반환한다.*/
public class ControllerHandlerAdapter implements HandlerAdapter{
    @Override
    public boolean supports(Object handler){
        return handler instanceof Controller;
    }

    @Override
    public ModelAndView handle(Object handler, HttpRequest request, HttpResponse response){
//        테스트용 예외
//        throw new IllegalArgumentException("어댑터에서 강제로 발생시킨 예외");
        return ((Controller) handler).handle(request,response);
    }//이처럼 안전하게 캐스팅하여 실행

}
