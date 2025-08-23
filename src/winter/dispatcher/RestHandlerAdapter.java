package winter.dispatcher;

import winter.annotation.RestController;
import winter.annotation.ResponseBody;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.ResponseEntity;
import winter.view.ModelAndView;
import winter.view.ResponseEntityView;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @RestController와 @ResponseBody 어노테이션을 처리하는 전용 핸들러 어댑터
 *
 * 30챕터에서 추가된 REST API 지원의 핵심 클래스입니다.
 * 기존 AnnotationHandlerAdapter가 ModelAndView를 반환하는 전통적인 MVC 패턴을 처리한다면,
 * RestHandlerAdapter는 JSON 응답에 최적화된 REST API 패턴을 처리합니다.
 *
 * 주요 기능:
 * - @RestController 클래스의 모든 메서드를 REST API로 처리
 * - @ResponseBody가 붙은 개별 메서드를 REST API로 처리
 * - ResponseEntity 반환값을 ResponseEntityView로 처리
 * - 일반 객체 반환값을 JsonView로 자동 변환
 * - 기존 ParameterResolver를 재사용하여 파라미터 바인딩
 *
 * 지원하는 반환 타입:
 * - ResponseEntity<T>: 상태 코드, 헤더, 본문 모두 제어 가능
 * - 일반 객체: 자동으로 JSON 직렬화 (200 OK 상태)
 * - 컬렉션(List, Map 등): JSON 배열/객체로 직렬화
 * - 문자열: 단순 텍스트 또는 JSON 문자열로 응답
 *
 * 처리 흐름:
 * 1. 핸들러가 REST 타입인지 확인 (@RestController 또는 @ResponseBody)
 * 2. 메서드 파라미터 바인딩 (기존 ParameterResolver 사용)
 * 3. 메서드 실행
 * 4. 반환값 타입에 따라 적절한 ModelAndView 생성
 *    - ResponseEntity → ResponseEntityView 사용
 *    - 일반 객체 → JsonView 사용 (별도 구현 필요)
 *
 * AnnotationHandlerAdapter와의 차이점:
 * - AnnotationHandlerAdapter: ModelAndView 반환 → HTML 템플릿 처리
 * - RestHandlerAdapter: 객체 직접 반환 → JSON 응답 처리
 */
public class RestHandlerAdapter implements HandlerAdapter {

    /**
     * 파라미터 해결을 위한 전략 객체
     * 기존 AnnotationHandlerAdapter와 동일한 ParameterResolver 재사용
     */
    private final ParameterResolver parameterResolver = new ParameterResolver();

    /**
     * 일반 객체를 JSON 응답으로 변환할 때 사용할 뷰 이름
     * ContentNegotiatingViewResolver에서 JsonView를 찾기 위해 사용
     */
    private static final String JSON_VIEW_NAME = "jsonResponse";

    /**
     * 이 어댑터가 주어진 핸들러를 지원하는지 확인
     *
     * 지원 조건:
     * 1. HandlerMethod 타입이어야 함
     * 2. 다음 중 하나를 만족해야 함:
     *    - 컨트롤러 클래스에 @RestController 어노테이션이 있음
     *    - 메서드에 @ResponseBody 어노테이션이 있음
     *
     * @param handler 핸들러 객체
     * @return REST 타입 핸들러면 true, 아니면 false
     */
    @Override
    public boolean supports(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false; // HandlerMethod가 아니면 지원하지 않음
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object controller = handlerMethod.getController();
        Method method = handlerMethod.getMethod();

        // 1. 컨트롤러 클래스에 @RestController가 있는지 확인
        boolean isRestController = controller.getClass().isAnnotationPresent(RestController.class);

        // 2. 메서드에 @ResponseBody가 있는지 확인
        boolean hasResponseBody = method.isAnnotationPresent(ResponseBody.class);

        boolean isSupported = isRestController || hasResponseBody;

        System.out.println("RestHandlerAdapter 지원 여부 확인: " +
                controller.getClass().getSimpleName() + "." + method.getName() +
                " → " + (isSupported ? "지원함" : "지원 안함") +
                " (RestController=" + isRestController + ", ResponseBody=" + hasResponseBody + ")");

        return isSupported;
    }

    /**
     * REST 타입 HandlerMethod를 실행하여 ModelAndView를 반환
     *
     * 실행 과정:
     * 1. 파라미터 바인딩 수행
     * 2. 메서드 실행
     * 3. 반환값 타입에 따라 적절한 ModelAndView 생성
     *
     * @param handler HandlerMethod 객체
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 적절한 뷰가 설정된 ModelAndView
     */
    @Override
    public ModelAndView handle(Object handler, HttpRequest request, HttpResponse response) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        try {
            // 컨트롤러 객체와 메서드 추출
            Object controller = handlerMethod.getController();
            Method method = handlerMethod.getMethod();

            System.out.println("=== RestHandlerAdapter 실행 ===");
            System.out.println("Controller: " + controller.getClass().getSimpleName());
            System.out.println("Method: " + method.getName());

            // REST 메서드 실행 및 반환값 처리
            Object result = executeRestMethod(controller, method, request, response);

            // 반환값 타입에 따라 적절한 ModelAndView 생성
            ModelAndView modelAndView = createModelAndViewForRestResponse(result);

            System.out.println("ModelAndView 생성 완료: " + modelAndView.getViewName());
            System.out.println("=============================");

            return modelAndView;

        } catch (Exception e) {
            System.err.println("RestHandlerAdapter 실행 실패: " + e.getMessage());
            throw new RuntimeException("Failed to execute REST handler method: " + handlerMethod, e);
        }
    }

    /**
     * REST 메서드를 실행하고 결과를 반환
     *
     * 기존 AnnotationHandlerAdapter의 파라미터 바인딩 로직을 재사용하되,
     * ModelAndView가 아닌 임의의 객체 반환을 허용합니다.
     *
     * @param controller 컨트롤러 인스턴스
     * @param method 실행할 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 메서드 실행 결과 (ResponseEntity 또는 일반 객체)
     * @throws Exception 메서드 호출 실패 시
     */
    private Object executeRestMethod(Object controller, Method method,
                                     HttpRequest request, HttpResponse response) throws Exception {

        // 메서드의 파라미터 정보 가져오기
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        // 디버깅 정보 출력
        System.out.println("REST 메서드 파라미터 바인딩 시작: " + parameters.length + "개 파라미터");

        // 각 파라미터에 대해 적절한 값 해결
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            // 파라미터 해결 전략 확인 및 출력
            String strategy = parameterResolver.getResolutionStrategy(parameter);
            System.out.println("Parameter " + i + " (" + parameter.getType().getSimpleName() + "): " + strategy);

            try {
                // 실제 파라미터 값 해결
                arguments[i] = parameterResolver.resolveParameter(parameter, request, response);
                System.out.println("  → Resolved value: " + arguments[i]);
            } catch (Exception e) {
                System.out.println("  → Resolution failed: " + e.getMessage());
                throw e;
            }
        }

        System.out.println("파라미터 바인딩 완료, 메서드 실행 중...");

        // 메서드 실행
        Object result = method.invoke(controller, arguments);

        System.out.println("메서드 실행 완료, 반환값 타입: " +
                (result != null ? result.getClass().getSimpleName() : "null"));

        return result;
    }

    /**
     * REST 메서드의 반환값에 따라 적절한 ModelAndView를 생성
     *
     * 반환값 타입별 처리:
     * - ResponseEntity: ResponseEntityView로 처리
     * - 일반 객체: JsonView로 처리 (JSON_VIEW_NAME 사용)
     * - null: 빈 JsonView로 처리
     *
     * @param result REST 메서드의 반환값
     * @return 적절한 뷰가 설정된 ModelAndView
     */
    private ModelAndView createModelAndViewForRestResponse(Object result) {
        if (result instanceof ResponseEntity) {
            // ResponseEntity인 경우: ResponseEntityView로 처리
            System.out.println("ResponseEntity 감지 → ResponseEntityView 사용");
            return createResponseEntityModelAndView((ResponseEntity<?>) result);
        } else {
            // 일반 객체인 경우: JsonView로 처리
            System.out.println("일반 객체 감지 → JsonView 사용");
            return createJsonModelAndView(result);
        }
    }

    /**
     * ResponseEntity를 ResponseEntityView로 처리하기 위한 ModelAndView 생성
     *
     * ResponseEntityView.RESPONSE_ENTITY_KEY를 사용하여 ResponseEntity 객체를
     * 모델에 저장하고, 뷰 이름을 설정하지 않습니다. (ContentNegotiatingViewResolver가
     * ResponseEntity 전용 처리를 위해 ResponseEntityView를 반환할 예정)
     *
     * @param responseEntity ResponseEntity 객체
     * @return ResponseEntity가 포함된 ModelAndView
     */
    private ModelAndView createResponseEntityModelAndView(ResponseEntity<?> responseEntity) {
        // ResponseEntityView에서 사용할 특별한 뷰 이름 설정
        // ContentNegotiatingViewResolver가 이 이름을 인식하여 ResponseEntityView를 반환
        ModelAndView modelAndView = new ModelAndView("responseEntity");

        // ResponseEntity 객체를 모델에 저장
        modelAndView.addAttribute(ResponseEntityView.RESPONSE_ENTITY_KEY, responseEntity);

        System.out.println("ResponseEntity ModelAndView 생성: " +
                "상태=" + responseEntity.getStatusCode() +
                ", 본문=" + (responseEntity.hasBody() ? "있음" : "없음"));

        return modelAndView;
    }

    /**
     * 일반 객체를 JsonView로 처리하기 위한 ModelAndView 생성
     *
     * 일반 객체를 "data" 키로 모델에 저장하고, JSON_VIEW_NAME을 뷰 이름으로 설정합니다.
     * ContentNegotiatingViewResolver가 이 뷰 이름을 인식하여 JsonView를 반환할 예정입니다.
     *
     * @param data JSON으로 직렬화할 데이터 객체
     * @return 데이터가 포함된 ModelAndView
     */
    private ModelAndView createJsonModelAndView(Object data) {
        ModelAndView modelAndView = new ModelAndView(JSON_VIEW_NAME);

        if (data != null) {
            // 데이터를 "data" 키로 모델에 저장
            modelAndView.addAttribute("data", data);
            System.out.println("JSON ModelAndView 생성: 데이터 타입=" + data.getClass().getSimpleName());
        } else {
            // null인 경우 빈 객체 생성
            modelAndView.addAttribute("data", new java.util.HashMap<>());
            System.out.println("JSON ModelAndView 생성: null 데이터 → 빈 객체로 대체");
        }

        return modelAndView;
    }

    /**
     * 디버깅을 위한 HandlerMethod 정보 출력 유틸리티 메서드
     *
     * @param handlerMethod 정보를 출력할 HandlerMethod
     */
    public static void debugHandlerMethod(HandlerMethod handlerMethod) {
        Object controller = handlerMethod.getController();
        Method method = handlerMethod.getMethod();

        System.out.println("=== HandlerMethod Debug Info ===");
        System.out.println("Controller: " + controller.getClass().getName());
        System.out.println("Method: " + method.getName());
        System.out.println("Parameters: " + method.getParameterCount());
        System.out.println("Return Type: " + method.getReturnType().getSimpleName());
        System.out.println("@RestController: " + controller.getClass().isAnnotationPresent(RestController.class));
        System.out.println("@ResponseBody: " + method.isAnnotationPresent(ResponseBody.class));
        System.out.println("===============================");
    }
}