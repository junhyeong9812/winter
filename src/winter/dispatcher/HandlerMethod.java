package winter.dispatcher;

import java.lang.reflect.Method;

/**
 * 어노테이션 기반 핸들러 메서드 정보를 담는 클래스
 *
 * 기존의 Controller 인터페이스 방식과 달리, 어노테이션 기반에서는
 * 컨트롤러 객체 + 특정 메서드 조합이 하나의 핸들러가 됩니다.
 *
 * 예시:
 * - 컨트롤러: ProductController 인스턴스
 * - 메서드: getProducts() 메서드
 * - 요청: GET /products
 *
 * 이 클래스는 위 정보들을 하나로 묶어서 관리합니다.
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

    @Override
    public String toString() {
        return String.format("HandlerMethod{path='%s', method='%s', handler=%s.%s()}",
                path, httpMethod.isEmpty() ? "ALL" : httpMethod,
                controller.getClass().getSimpleName(), method.getName());
    }
}