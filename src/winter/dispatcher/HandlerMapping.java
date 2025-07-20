package winter.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*요청 경로(URL)에 해당하는 핸들러(컨트롤러)를 관리하는 클래스
* Dispatcher는 이 클래스를 통해 요청에 해당하는 핸들러를 찾는다.*/
public class HandlerMapping {

    //URL->핸들러 객체 저장소
    private final Map<String, Object> mapping=new HashMap<>();

    public HandlerMapping(){
        //수동 라우팅 등록(추후 자동 스캔 예정)
        mapping.put("/hello",new HelloController());
        mapping.put("/bye",new ByeController());
        mapping.put("/register",new RegisterController());
        mapping.put("/user", new UserController());
        // 21단계: JSON API 테스트용 경로 추가
        mapping.put("/api", new ApiController());
    }

    /**
     * 요청 경로에 해당하는 핸들러 반환
     *
     * @param path 요청 경로 (예: "/api", "/user")
     * @return 해당하는 Controller 객체, 없으면 null
     */
    public Object getHandler(String path){
        return mapping.get(path);
    }
}
