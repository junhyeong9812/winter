package winter.excption;

import winter.http.HttpRequest;
import winter.http.HttpResponse;

public interface ExceptionResolver {
    /**
     * 발생한 예외를 처리하고, 적절한 응답을 작성한다.
     *
     * @param request  현재 요청 객체
     * @param response 응답 객체
     * @param ex       발생한 예외
     * @return true: 예외를 처리했음, false: 처리하지 않음
     */
    boolean resolveException(HttpRequest request, HttpResponse response, Exception ex);
}
