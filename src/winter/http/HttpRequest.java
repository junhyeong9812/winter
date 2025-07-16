package winter.http;

import java.util.HashMap;
import java.util.Map;

/*
* 간단한 요청 정보 표현 객체
* 요청 경로와 쿼리 파라미터를 포함하며,
* Dispatcher가 요청 경로를 넘길 때 ?name=junhyeong 같은 형태도 파싱 가능*/
public class HttpRequest {
  private final String path;
  private final Map<String,String> parameters= new HashMap<>();

  /*
  * 생성자 -  요청 경로 문자열을 받아 내부적으로 쿼리 파라미터를 자동 파싱함.
  * 예: "/register?name=junhyeong&email=test@test.com"
  *
  * 1. '?'를 기준으로 경로(path)와 쿼리스트링(query String)을 분리
  * 2. query string이 존재하면 key=value 형태로 분리하여 파라미터 맵에 저장*/
  public HttpRequest(String rawPath) {
      String[] parts = rawPath.split("\\?", 2); // 경로와 쿼리스트링 분리
      this.path = parts[0]; // 앞부분은 순수 요청 경로

      if (parts.length > 1) {
          String queryString = parts[1]; // 뒷부분은 name=junhyung&email=test
          String[] pairs = queryString.split("&");

          for (String pair : pairs) {
              String[] kv = pair.split("=", 2); // key=value 분리
              if (kv.length == 2) {
                  parameters.put(kv[0], kv[1]); // 파라미터 맵에 저장
              }
          }
      }
  }

    /** 순수 요청 경로 반환 (예: "/register") */
    public String getPath() {
        return path;
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
