package winter.dispatcher;

import winter.annotation.Controller;
import winter.annotation.RestController;
import winter.annotation.ResponseBody;

import java.lang.reflect.Method;

/**
 * 어노테이션 기반 핸들러 메서드 정보를 담는 클래스
 *
 * 30챕터 업데이트: REST API 지원 추가
 * 기존의 Controller 인터페이스 방식과 달리, 어노테이션 기반에서는
 * 컨트롤러 객체 + 특정 메서드 조합이 하나의 핸들러가 됩니다.
 *
 * 지원하는 컨트롤러 타입:
 * - @Controller: 전통적인 MVC (ModelAndView → HTML)
 * - @RestController: REST API (Object → JSON)
 * - @Controller + @ResponseBody: 메서드별 JSON 응답
 *
 * 예시:
 * - 컨트롤러: ProductController 인스턴스 (@Controller)
 * - 메서드: getProducts() 메서드
 * - 요청: GET /products → HTML 템플릿 렌더링
 *
 * - 컨트롤러: ApiController 인스턴스 (@RestController)
 * - 메서드: getUsers() 메서드
 * - 요청: GET /api/users → JSON 응답
 *
 * 이 클래스는 위 정보들을 하나로 묶어서 관리하고,
 * 핸들러 타입 판별 기능을 제공합니다.
 */
public class HandlerMethod {

    private final Object controller;    // 컨트롤러 인스턴스
    private final Method method;        // 실행할 메서드
    private final String path;          // 매핑된 URL 경로
    private final String httpMethod;    // 허용된 HTTP 메서드

    /**
     * HandlerMethod 생성자
     *
     * @param controller 컨트롤러 객체 인스턴스
     * @param method 실행할 메서드 (리플렉션 Method 객체)
     * @param path 매핑된 URL 경로
     * @param httpMethod 허용된 HTTP 메서드 (빈 문자열이면 모든 메서드 허용)
     */
    public HandlerMethod(Object controller, Method method, String path, String httpMethod) {
        this.controller = controller;
        this.method = method;
        this.path = path;
        this.httpMethod = httpMethod;
    }

    /**
     * 컨트롤러 인스턴스 반환
     *
     * @return 컨트롤러 객체
     */
    public Object getController() {
        return controller;
    }

    /**
     * 실행할 메서드 반환
     *
     * @return Method 객체
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 매핑된 URL 경로 반환
     *
     * @return URL 경로
     */
    public String getPath() {
        return path;
    }

    /**
     * 30챕터 추가: getPath()의 별칭 메서드
     * AnnotationHandlerMapping에서 사용하는 메서드명과 통일
     *
     * @return URL 패턴 (getPath()와 동일)
     */
    public String getUrlPattern() {
        return path;
    }

    /**
     * 허용된 HTTP 메서드 반환
     *
     * @return HTTP 메서드 (빈 문자열이면 모든 메서드 허용)
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * 현재 HTTP 요청이 이 핸들러와 매치되는지 확인
     *
     * @param requestPath 요청 경로
     * @param requestMethod 요청 HTTP 메서드
     * @return 매치 여부
     */
    public boolean matches(String requestPath, String requestMethod) {
        // 1. 경로 매치 확인
        if (!this.path.equals(requestPath)) {
            return false;
        }

        // 2. HTTP 메서드 매치 확인
        // httpMethod가 빈 문자열이면 모든 메서드 허용
        if (this.httpMethod.isEmpty()) {
            return true;
        }

        return this.httpMethod.equalsIgnoreCase(requestMethod);
    }

    /**
     * 30챕터 추가: 이 핸들러가 REST 타입인지 확인
     *
     * REST 타입 판별 조건:
     * 1. 컨트롤러 클래스에 @RestController 어노테이션이 있음
     * 2. 또는 메서드에 @ResponseBody 어노테이션이 있음
     *
     * @return REST 핸들러면 true, MVC 핸들러면 false
     */
    public boolean isRestHandler() {
        // 1. 컨트롤러 클래스에 @RestController가 있는지 확인
        boolean isRestController = controller.getClass().isAnnotationPresent(RestController.class);

        // 2. 메서드에 @ResponseBody가 있는지 확인
        boolean hasResponseBody = method.isAnnotationPresent(ResponseBody.class);

        return isRestController || hasResponseBody;
    }

    /**
     * 30챕터 추가: 이 핸들러가 MVC 타입인지 확인
     *
     * MVC 타입 판별 조건:
     * 1. 컨트롤러 클래스에 @Controller 어노테이션이 있음
     * 2. 그리고 REST 타입이 아님 (@RestController가 아니고 @ResponseBody도 없음)
     *
     * @return MVC 핸들러면 true, REST 핸들러면 false
     */
    public boolean isMvcHandler() {
        // 1. 컨트롤러 클래스에 @Controller가 있는지 확인
        boolean isController = controller.getClass().isAnnotationPresent(Controller.class);

        // 2. REST 타입이 아닌지 확인
        boolean isNotRest = !isRestHandler();

        return isController && isNotRest;
    }

    /**
     * 30챕터 추가: 핸들러의 타입을 문자열로 반환
     *
     * @return "REST", "MVC", 또는 "UNKNOWN"
     */
    public String getHandlerType() {
        if (isRestHandler()) {
            return "REST";
        } else if (isMvcHandler()) {
            return "MVC";
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * 30챕터 추가: 컨트롤러 클래스 이름 반환 (디버깅용)
     *
     * @return 컨트롤러 클래스의 단순 이름
     */
    public String getControllerName() {
        return controller.getClass().getSimpleName();
    }

    /**
     * 30챕터 추가: 메서드 이름 반환 (디버깅용)
     *
     * @return 메서드 이름
     */
    public String getMethodName() {
        return method.getName();
    }

    /**
     * 30챕터 추가: 핸들러의 완전한 시그니처 반환
     *
     * @return "ControllerName.methodName"
     */
    public String getHandlerSignature() {
        return getControllerName() + "." + getMethodName();
    }

    /**
     * 30챕터 추가: HTTP 메서드 표시 문자열 반환
     *
     * @return HTTP 메서드 (비어있으면 "ALL")
     */
    public String getHttpMethodDisplay() {
        return httpMethod.isEmpty() ? "ALL" : httpMethod;
    }

    /**
     * 30챕터 추가: 핸들러 정보를 상세하게 반환 (디버깅용)
     *
     * @return 상세한 핸들러 정보
     */
    public String toDetailedString() {
        return String.format("[%s] %s %s → %s (%s)",
                getHandlerType(),
                getHttpMethodDisplay(),
                getUrlPattern(),
                getHandlerSignature(),
                getControllerName());
    }

    /**
     * 기존 toString() 메서드 (30챕터: REST/MVC 구분 추가)
     */
    @Override
    public String toString() {
        return String.format("HandlerMethod{path='%s', method='%s', handler=%s.%s(), type=%s}",
                path,
                httpMethod.isEmpty() ? "ALL" : httpMethod,
                controller.getClass().getSimpleName(),
                method.getName(),
                getHandlerType());
    }

    /**
     * 30챕터 추가: 두 HandlerMethod가 같은지 비교
     * 경로, HTTP 메서드, 컨트롤러 클래스, 메서드 이름을 모두 비교
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        HandlerMethod that = (HandlerMethod) obj;

        return path.equals(that.path) &&
                httpMethod.equals(that.httpMethod) &&
                controller.getClass().equals(that.controller.getClass()) &&
                method.getName().equals(that.method.getName());
    }

    /**
     * 30챕터 추가: equals()와 함께 구현하는 hashCode()
     */
    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + httpMethod.hashCode();
        result = 31 * result + controller.getClass().hashCode();
        result = 31 * result + method.getName().hashCode();
        return result;
    }
}