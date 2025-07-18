package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.model.Address;
import winter.model.User;
import winter.view.ModelAndView;

public class UserController implements Controller {

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            String name = request.getParameter("name");
            String city = request.getParameter("city");
            String zipcode = request.getParameter("zipcode");

            Address address = new Address();
            address.setCity(city);
            address.setZipcode(zipcode);

            User user = new User();
            user.setName(name);
            user.setAddress(address);

            ModelAndView mv = new ModelAndView("user");
            mv.addAttribute("user", user);
            return mv;

        } else {
            response.setStatus(405);
            response.setBody("Method Not Allowed: " + method);
            return null;
        }
    }
}
