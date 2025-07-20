package winter.dispatcher;


import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.model.Address;
import winter.model.User;
import winter.view.ModelAndView;

/**
 * JSON API 응답을 테스트 하기 위한 컨트롤러
 *
 * 동일한 데이터를 HTML 또는 JSON형태로 응답할 수 있음을 보여줍니다.
 * - Accept: text/html → HTML 템플릿 렌더링
 * - Accept: application/json → JSON 응답
 * */
public class ApiController implements Controller{

    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response){
        String method = request.getMethod();

        if("GET".equalsIgnoreCase(method)){
            //샘플 사용자 데이터 생성
            Address address = new Address();
            address.setCity("seoul");
            address.setZipcode("12345");

            User user = new User();
            user.setName("Jun");
            user.setAddress(address);

            //ModelAndView 생성 (동일한 데이터,동일한 뷰명)
            ModelAndView mv =new ModelAndView("api");
            //논리적 뷰명 : "api"
            mv.addAttribute("user",user);
            mv.addAttribute("message","welcom to Winter API!");
            mv.addAttribute("timestamp",System.currentTimeMillis());

            return mv;
        }else {
            //지원하지 않는 HTTP 메서드
            response.setStatus(405);
            response.setBody("Method Not Allowed : "+method);
            return null;
        }
    }
}
