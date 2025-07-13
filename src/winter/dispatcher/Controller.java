package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/*요청(HttpRequest),응답(HttpResponse)을 받아 처리하고 modelAndView를
* 반환한느 컨트롤러 인터페이스*/
public interface Controller {
    ModelAndView handle(HttpRequest request, HttpResponse response);
}
