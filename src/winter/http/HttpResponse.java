package winter.http;

/*
* 간단하 응답 정보 표현 객체*/
public class HttpResponse {
    private int status = 200;
    private String body = "";
    
    public void setStatus(int status){
        this.status =status;
    }
    
    public void setBody(String body){
        this.body =body;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getBody(){
        return body;
    }
    
    public void send(){
        System.out.println(" HTTP Response ");
        System.out.println("status = " + status);
        System.out.println("body = " + body);
    }
}
