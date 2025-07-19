package winter.http;

import java.util.HashMap;
import java.util.Map;

/*
* 간단하 응답 정보 표현 객체*/
public class HttpResponse {
    private int status = 200;
    private String body = "";
    private final Map<String,String> headers = new HashMap<>();
    
    public void setStatus(int status){
        this.status =status;
    }
    
    public void setBody(String body){
        this.body =body;
    }

    public void addHeader(String key,String value){
        headers.put(key,value);
    }

    public int getStatus() {
        return status;
    }
    
    public String getBody(){
        return body;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public void send(){
        System.out.println(" HTTP Response ");
        System.out.println("status = " + status);
        for (Map.Entry<String,String> entry : headers.entrySet()){
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("body = " + body);
    }
}
