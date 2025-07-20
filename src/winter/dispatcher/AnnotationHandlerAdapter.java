package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.lang.reflect.Method;

/**
 * 어노테이션 기반 핸들러 메서드를 실행하는 어댑터
 *
 * 기존 ControllerHandlerAdapter가 Controller 인터페이스를 처리한다면,
 * AnnotationHandlerAdapter는 HandlerMethod 객체를 처리합니다.
 *
 * 처리 과정:
 * 1. HandlerMethod에서 컨트롤러 객체와 메서드 추출
 * 2. 리플렉션을 사용하여 메서드 호출
 * 3. 메서드 실행 결과를 ModelAndView로 반환
 *
 * 지원하는 메서드 시그니처:
 * - public ModelAndView methodName()
 * - public ModelAndView methodName(HttpRequest request)
 * - public ModelAndView methodName(HttpRequest request, HttpResponse response)
 */
public class AnnotationHandlerAdapter implements HandlerAdapter {

    /**
     * 이 어댑터가 주어진 핸들러를 지원하는지 확인
     *
     * @param handler 핸들러 객체
     * @return HandlerMethod 타입이면 true
     */
    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    /**
     * HandlerMethod를 실행하여 ModelAndView를 반환
     *
     * @param handler HandlerMethod 객체
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 메서드 실행 결과 (ModelAndView)
     */
    @Override
    public ModelAndView handle(Object handler, HttpRequest request, HttpResponse response) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        try {
            // 컨트롤러 객체와 메서드 추출
            Object controller = handlerMethod.getController();
            Method method = handlerMethod.getMethod();

            // 메서드 파라미터 수에 따른 호출 방식 결정
            ModelAndView result = invokeHandlerMethod(controller, method, request, response);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke handler method: " + handlerMethod, e);
        }
    }

    /**
     * 메서드 파라미터에 따라 적절한 방식으로 메서드 호출
     *
     * @param controller 컨트롤러 인스턴스
     * @param method 실행할 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 메서드 실행 결과
     * @throws Exception 메서드 호출 실패 시
     */
    private ModelAndView invokeHandlerMethod(Object controller, Method method,
                                             HttpRequest request, HttpResponse response) throws Exception {

        Class<?>[] paramTypes = method.getParameterTypes();
        Object result;

        // 파라미터 수에 따른 메서드 호출
        switch (paramTypes.length) {
            case 0:
                // 파라미터 없는 메서드: methodName()
                result = method.invoke(controller);
                break;

            case 1:
                // 파라미터 1개인 메서드: methodName(HttpRequest request)
                if (paramTypes[0].equals(HttpRequest.class)) {
                    result = method.invoke(controller, request);
                } else {
                    throw new IllegalArgumentException(
                            "Unsupported single parameter type: " + paramTypes[0].getName() +
                                    ". Only HttpRequest is supported.");
                }
                break;

            case 2:
                // 파라미터 2개인 메서드: methodName(HttpRequest request, HttpResponse response)
                if (paramTypes[0].equals(HttpRequest.class) && paramTypes[1].equals(HttpResponse.class)) {
                    result = method.invoke(controller, request, response);
                } else {
                    throw new IllegalArgumentException(
                            "Unsupported parameter types for 2-parameter method. " +
                                    "Expected: (HttpRequest, HttpResponse)");
                }
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported method signature. Handler methods can have 0, 1, or 2 parameters only.");
        }

        // 반환값이 ModelAndView가 아닌 경우 예외 발생
        if (!(result instanceof ModelAndView)) {
            throw new IllegalArgumentException(
                    "Handler method must return ModelAndView. " +
                            "Method: " + method.getName() + " returned: " +
                            (result != null ? result.getClass().getName() : "null"));
        }

        return (ModelAndView) result;
    }
}