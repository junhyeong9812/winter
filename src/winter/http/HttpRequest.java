package winter.http;

import java.util.HashMap;
import java.util.Map;

/*간단한 요청 정보 표현 객체
* */
public class HttpRequest {
  private final String path;
  private final Map<String,String> parameters= new HashMap<>();

  public HttpRequest(String path){
      this.path =path;
  }

  public String getPath(){
      return path;
  }

  public void addParameter(String key,String value){
      parameters.put(key,value);
  }

  public String getParameter(String key){
      return parameters.get(key);
  }

  public Map<String,String> getParameters(){
      return parameters;
  }

}
