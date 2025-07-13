package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

//Hello 요청을 처리하는 핸들러(컨트롤러 역할)
public class HelloController implements Controller {

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response){
        String name = request.getParameter("name");
        if(name ==null) name ="Winter";

        ModelAndView mv =new ModelAndView("hello");
        mv.addAttribute("message","hello!"+name+"!");
        return mv;
    }
}
