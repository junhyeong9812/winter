package winter.dispatcher;

import winter.view.ModelAndView;

//Hello 요청을 처리하는 핸들러(컨트롤러 역할)
public class HelloController implements Controller {

    @Override
    public ModelAndView handle(){
        ModelAndView mv =new ModelAndView("hello");
        mv.addAttribute("message","hello from Winter Framework!");
        return mv;
    }
}
