package winter.dispatcher;

import winter.annotation.ModelAttribute;
import winter.annotation.RequestParam;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.util.ModelAttributeBinder;
import winter.validation.*;
import winter.view.ModelAndView;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 어노테이션 기반 핸들러 메서드를 실행하는 어댑터 (28단계 확장 버전)
 *
 * 기존 AnnotationHandlerAdapter를 확장하여 더 정교한 파라미터 바인딩과 검증 기능을 지원합니다.
 *
 * 23단계 → 28단계 변화:
 * - 기존: @RequestParam, @ModelAttribute를 사용한 파라미터 바인딩
 * - 추가: @Valid 어노테이션을 통한 자동 객체 검증 기능
 * - 추가: BindingResult 파라미터 자동 주입
 * - 추가: 검증 실패 시 ValidationException 발생 (BindingResult 없는 경우)
 *
 * 지원하는 메서드 시그니처 확장:
 * - public ModelAndView method()
 * - public ModelAndView method(HttpRequest request)
 * - public ModelAndView method(HttpRequest request, HttpResponse response)
 * - public ModelAndView method(@RequestParam("name") String name)
 * - public ModelAndView method(@ModelAttribute UserForm form)
 * - public ModelAndView method(@Valid @ModelAttribute UserForm form, BindingResult result)
 * - public ModelAndView method(@RequestParam("id") int id, @Valid @ModelAttribute UserForm form, BindingResult result)
 * - 기타 다양한 조합...
 */
public class AnnotationHandlerAdapter implements HandlerAdapter {

    // 파라미터 해결을 위한 전략 객체
    private final ParameterResolver parameterResolver = new ParameterResolver();

    /**
     * 검증 기능을 수행하는 Validator 인스턴스
     * 28단계에서 추가: 어노테이션 기반 자동 검증을 위해 사용
     * AnnotationBasedValidator는 @NotNull, @NotEmpty, @Size, @Email, @Pattern 등의 어노테이션을 인식하여 검증 수행
     */
    private final Validator validator = new AnnotationBasedValidator();

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

            // 28단계 변경: 검증 기능이 포함된 파라미터 바인딩 메서드 호출
            ModelAndView result = invokeHandlerMethodWithValidation(controller, method, request, response);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke handler method: " + handlerMethod, e);
        }
    }

    /**
     * 28단계 새로운 메서드: 검증 기능을 포함한 파라미터 바인딩 및 메서드 호출
     *
     * 기존 invokeHandlerMethodWithParameterBinding 메서드를 확장하여 @Valid 검증 기능을 추가했습니다.
     *
     * 동작 절차:
     * 1. 메서드 파라미터 분석
     * 2. 각 파라미터에 대해 값 해결 (기존 ParameterResolver 사용)
     * 3. @ModelAttribute + @Valid 조합 감지 시 자동 검증 수행
     * 4. BindingResult 파라미터 자동 주입
     * 5. 검증 실패 시 예외 처리 또는 BindingResult에 오류 저장
     * 6. 메서드 실행
     *
     * @param controller 컨트롤러 인스턴스
     * @param method 실행할 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 메서드 실행 결과
     * @throws Exception 메서드 호출 실패 시
     */
    private ModelAndView invokeHandlerMethodWithValidation(Object controller, Method method,
                                                           HttpRequest request, HttpResponse response) throws Exception {

        // 메서드의 파라미터 정보 가져오기
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        // 디버깅 정보 출력 (28단계 확장)
        System.out.println("=== Parameter Binding & Validation Debug Info ===");
        System.out.println("Method: " + method.getName() + " with " + parameters.length + " parameters");

        /**
         * 28단계 추가: 검증 상태 추적 변수들
         * - currentBindingResult: 현재 생성된 BindingResult (다음 파라미터가 BindingResult 타입인 경우 사용)
         * - validationPerformed: 검증이 수행되었는지 여부
         */
        BindingResult currentBindingResult = null;
        boolean validationPerformed = false;

        // 각 파라미터에 대해 적절한 값 해결
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();

            /**
             * 28단계 추가: BindingResult 타입 파라미터 처리
             *
             * BindingResult는 직전 @Valid @ModelAttribute 파라미터의 검증 결과를 담는 객체입니다.
             * Spring MVC에서와 동일한 방식으로, @Valid 어노테이션이 있는 파라미터 바로 다음에
             * BindingResult 파라미터가 오면 자동으로 검증 결과를 주입합니다.
             */
            if (paramType == BindingResult.class) {
                System.out.println("Parameter " + i + " (BindingResult): Injecting validation result");

                if (currentBindingResult != null) {
                    // 이전 @Valid 파라미터의 검증 결과 주입
                    arguments[i] = currentBindingResult;
                    System.out.println("  → Injected BindingResult with " +
                            currentBindingResult.getErrorCount() + " errors");
                    currentBindingResult = null; // 사용된 BindingResult 초기화
                } else {
                    // BindingResult가 있지만 이전에 @Valid 파라미터가 없는 경우
                    // 빈 BindingResult 생성
                    arguments[i] = new BindingResult(null, "unknown");
                    System.out.println("  → Created empty BindingResult (no preceding @Valid parameter)");
                }
                continue;
            }

            // 기존 파라미터 해결 로직 사용
            String strategy = parameterResolver.getResolutionStrategy(parameter);
            System.out.println("Parameter " + i + " (" + parameter.getType().getSimpleName() + "): " + strategy);

            try {
                // 파라미터 값 해결
                Object resolvedValue = parameterResolver.resolveParameter(parameter, request, response);
                arguments[i] = resolvedValue;
                System.out.println("  → Resolved value: " + resolvedValue);

                /**
                 * 28단계 추가: @Valid + @ModelAttribute 조합 검증 처리
                 *
                 * 검증 수행 조건:
                 * 1. 파라미터에 @Valid 어노테이션이 있어야 함
                 * 2. 파라미터에 @ModelAttribute 어노테이션이 있어야 함
                 * 3. 해결된 값이 null이 아니어야 함
                 *
                 * 검증 절차:
                 * 1. BindingResult 객체 생성 (검증 결과를 담기 위해)
                 * 2. Validator를 사용하여 객체 검증 수행
                 * 3. 다음 파라미터가 BindingResult 타입인지 확인
                 * 4. BindingResult 파라미터가 없고 검증 실패 시 ValidationException 발생
                 */
                if (parameter.isAnnotationPresent(Valid.class) &&
                        parameter.isAnnotationPresent(ModelAttribute.class) &&
                        resolvedValue != null) {

                    System.out.println("  → @Valid annotation detected, performing validation");

                    // ModelAttribute 이름 결정 (어노테이션 값 또는 클래스명 소문자)
                    String modelAttributeName = getModelAttributeName(parameter);

                    // BindingResult 생성 및 검증 수행
                    currentBindingResult = new BindingResult(resolvedValue, modelAttributeName);
                    validator.validate(resolvedValue, currentBindingResult);
                    validationPerformed = true;

                    System.out.println("  → Validation completed with " +
                            currentBindingResult.getErrorCount() + " errors");

                    // 다음 파라미터가 BindingResult 타입인지 확인
                    boolean hasBindingResultParameter = (i + 1 < parameters.length &&
                            parameters[i + 1].getType() == BindingResult.class);

                    if (!hasBindingResultParameter && currentBindingResult.hasErrors()) {
                        /**
                         * BindingResult 파라미터가 없고 검증 오류가 있는 경우 ValidationException 발생
                         *
                         * 이는 Spring MVC의 동작과 동일합니다:
                         * - @Valid 파라미터 다음에 BindingResult가 있으면: 오류를 BindingResult에 저장
                         * - @Valid 파라미터 다음에 BindingResult가 없으면: ValidationException 발생
                         */
                        System.out.println("  → No BindingResult parameter found, throwing ValidationException");
                        throw new ValidationException(currentBindingResult);
                    }

                    if (!hasBindingResultParameter) {
                        // BindingResult 파라미터가 없지만 검증은 성공한 경우
                        currentBindingResult = null;
                    }
                }

            } catch (Exception e) {
                System.out.println("  → Resolution failed: " + e.getMessage());
                throw e;
            }
        }

        System.out.println("Validation performed: " + validationPerformed);
        System.out.println("================================================");

        // 메서드 실행
        Object result = method.invoke(controller, arguments);

        // 반환값이 ModelAndView가 아닌 경우 예외 발생
        if (!(result instanceof ModelAndView)) {
            throw new IllegalArgumentException(
                    "Handler method must return ModelAndView. " +
                            "Method: " + method.getName() + " returned: " +
                            (result != null ? result.getClass().getName() : "null"));
        }

        return (ModelAndView) result;
    }

    /**
     * 28단계 추가: ModelAttribute 이름 추출 유틸리티 메서드
     *
     * @ModelAttribute 어노테이션의 value 값을 확인하고,
     * 값이 없으면 클래스명을 소문자로 변환하여 사용합니다.
     *
     * 예시:
     * - @ModelAttribute("user") UserForm form → "user"
     * - @ModelAttribute UserForm form → "userForm"
     *
     * @param parameter 파라미터 정보
     * @return ModelAttribute 이름
     */
    private String getModelAttributeName(Parameter parameter) {
        ModelAttribute annotation = parameter.getAnnotation(ModelAttribute.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        // 어노테이션 값이 없으면 클래스명을 소문자로 변환
        String className = parameter.getType().getSimpleName();
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * 파라미터 바인딩을 사용하여 메서드 호출 (23단계 기존 방식)
     *
     * 메서드의 파라미터 정보를 분석하고, 각 파라미터에 맞는 값을 해결하여 메서드를 호출합니다.
     * 28단계에서는 검증 기능이 추가된 invokeHandlerMethodWithValidation으로 대체되었습니다.
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