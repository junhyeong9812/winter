package winter.dispatcher;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 어노테이션 기반 핸들러 메서드를 실행하는 어댑터 (23단계 확장 버전)
 *
 * 기존 AnnotationHandlerAdapter를 확장하여 더 정교한 파라미터 바인딩을 지원합니다.
 *
 * 22단계 → 23단계 변화:
 * - 기존: 0개, 1개(HttpRequest), 2개(HttpRequest+HttpResponse) 시그니처만 지원
 * - 확장: @RequestParam, @ModelAttribute를 사용한 임의 개수의 파라미터 지원
 *
 * 지원하는 메서드 시그니처 확장:
 * - public ModelAndView method()
 * - public ModelAndView method(HttpRequest request)
 * - public ModelAndView method(HttpRequest request, HttpResponse response)
 * - public ModelAndView method(@RequestParam("name") String name)
 * - public ModelAndView method(@ModelAttribute UserForm form)
 * - public ModelAndView method(@RequestParam("id") int id, @ModelAttribute UserForm form, HttpResponse response)
 * - 기타 다양한 조합...
 */
public class AnnotationHandlerAdapter implements HandlerAdapter {

    //파라미터 해결을 위한 전략 객체
    private final ParameterResolver parameterResolver = new ParameterResolver();

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
            ModelAndView result = invokeHandlerMethodWithParameterBinding(controller, method, request, response);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke handler method: " + handlerMethod, e);
        }
    }

    /**
     * 파라미터 바인딩을 사용하여 메서드 호출 (23단계 새로운 방식)
     *
     * 메서드의 파라미터 정보를 분석하고, 각 파라미터에 맞는 값을 해결하여 메서드를 호출합니다.
     *
     * @param controller 컨트롤러 인스턴스
     * @param method 실행할 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 메서드 실행 결과
     * @throws Exception 메서드 호출 실패 시
     */
    private ModelAndView invokeHandlerMethodWithParameterBinding(Object controller, Method method, HttpRequest request, HttpResponse response) throws Exception {

        //메서드의 파라미터 정보 가져오기
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        //디버깅 정보 출력
        System.out.println("=== Parameter Binding Debug Info ===");
        System.out.println("Method: " + method.getName() + " with " + parameters.length + " parameters");

        //각 파라미터에 대해 적절한 값 해결
        for(int i =0; i < parameters.length; i++){
            Parameter parameter = parameters[i];

            //파라미터 해결 전략 확인 및 출력
            String strategy = parameterResolver.getResolutionStrategy(parameter);
            System.out.println("Parameter " + i + " (" + parameter.getType().getSimpleName() + "): " + strategy);
            
            //실제 파라미터 값 해결
            try{
                arguments[i] = parameterResolver.resolveParameter(parameter, request, response);
                System.out.println("  → Resolved value: " + arguments[i]);
            }catch (Exception e){
                System.out.println("  → Resolution failed: " + e.getMessage());
                throw e;
            }
        }
        System.out.println("=====================================");

        //메서드 실행
        Object result = method.invoke(controller, arguments);

        //반환값이 ModelAndView가 아닌 경우 예외 발생
        if(!(result instanceof ModelAndView)){
            throw new IllegalArgumentException(
                    "Handler method must return ModelAndView. " +
                    "Method: " + method.getName() + " returned: " +
                    (result != null ? result.getClass().getName() : "null"));
        }

        return (ModelAndView) result;
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