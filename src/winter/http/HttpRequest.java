package winter.http;

import java.util.HashMap;
import java.util.Map;

/*
* 간단한요청 정보 표현 객체.
* 경로 + 쿼리 파라미터 + HTTP 메서드(GET,POST 등)를 포함한다.*/
public class HttpRequest {
  private final String path;
  private final String method;
  private final Map<String,String> parameters= new HashMap<>();

  /*
  * 기본 생성자 (GET 요청 전용)*/
  public HttpRequest(String rawPath){
      this(rawPath, "GET");
  }

    /**
     * 생성자 - 요청 경로 및 HTTP 메서드를 지정
     *
     * @param rawPath "/register?name=jun"
     * @param method "GET", "POST", ...
     */
    public HttpRequest(String rawPath, String method) {
        this.method = method.toUpperCase(); // 대문자로 통일
        String[] parts = rawPath.split("\\?", 2);
        this.path = parts[0];

        if (parts.length > 1) {
            String queryString = parts[1];
            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    parameters.put(kv[0], kv[1]);
                }
            }
        }
    }

    /** 순수 요청 경로 반환 (예: "/register") */
    public String getPath() {
        return path;
    }

    /*HTTP 메서드 반환 (예 : GET,POST)*/
    public String getMethod(){
        return method;
    }

    /** 개별 파라미터 조회 (예: getParameter("name") → "junhyung") */
    public String getParameter(String key) {
        return parameters.get(key);
    }

    /** 전체 파라미터 Map 반환 */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /** 외부에서 수동 파라미터 추가 (테스트 시 유용) */
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

}
