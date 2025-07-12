package winter.dispatcher;

import winter.view.ModelAndView;

//Bye 요청을 처리하는 핸들러(컨트롤러 역할)
public class ByeController implements Controller {

    @Override
    public ModelAndView handle(){
        ModelAndView mv = new ModelAndView("bye");
        mv.addAttribute("message","Goodbye from winter Framework");
        return mv;
    }
}
