package winter.excption;

import winter.http.HttpRequest;
import winter.http.HttpResponse;

public class SimpleExceptionResolver implements ExceptionResolver{
    @Override
    public boolean resolveException(HttpRequest request, HttpResponse response,Exception ex){
        response.setStatus(500);
        response.addHeader("Content-Type","text/plain");
        String jsonBody = "{\n" +
                "  \"status\": 500,\n" +
                "  \"message\": \"" + ex.getMessage() + "\"\n" +
                "}";
        response.setBody(jsonBody);
        return true;
    }
}
