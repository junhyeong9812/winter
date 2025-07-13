package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

//Bye 요청을 처리하는 핸들러(컨트롤러 역할)
public class ByeController implements Controller {

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse httpResponse){
        ModelAndView mv = new ModelAndView("bye");
        mv.addAttribute("message","Goodbye from winter Framework");
        return mv;
    }
}
