package winter.dispatcher;

import winter.form.UserForm;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.util.ModelAttributeBinder;
import winter.view.ModelAndView;

public class RegisterController implements Controller {
    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response) {
        UserForm form = ModelAttributeBinder.bind(request, UserForm.class);

        ModelAndView mv = new ModelAndView("register");
        mv.addAttribute("user", form);
        return mv;
    }
}
