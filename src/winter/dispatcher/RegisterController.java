package winter.dispatcher;

import winter.form.UserForm;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.util.ModelAttributeBinder;
import winter.view.ModelAndView;

public class RegisterController implements Controller {
    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();
        if("GET".equals(method)){
            //폼 템플릿만 렌더링하거나 메시지를 보여주는 용도
            ModelAndView mv = new ModelAndView("register");
            mv.addAttribute("user",new UserForm());//빈 폼 전달
            return mv;

        }else if("POST".equals(method)){
            //폼 제출 처리
            UserForm form = ModelAttributeBinder.bind(request, UserForm.class);
            ModelAndView mv = new ModelAndView("register");
            mv.addAttribute("user", form);
            return mv;
        }else {
            // 그 외 메서드는 허용되지 않음
            response.setStatus(405);
            response.setBody("Method Not Allowed: " + method);
            return null;

        }


    }
}
