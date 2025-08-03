package winter.dispatcher;

import winter.annotation.Controller;
import winter.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 어노테이션 기반 핸들러 매핑을 관리하는 클래스
 *
 * @Controller 어노테이션이 붙은 클래스들을 스캔하고,
 * @RequestMapping 어노테이션이 붙은 메서드들을 찾아서
 * URL과 핸들러 메서드를 매핑합니다.
 *
 * 현재는 수동 등록 방식이지만, 추후 클래스패스 자동 스캔으로 확장 가능합니다.
 */
public class AnnotationHandlerMapping {

    // 등록된 모든 핸들러 메서드 목록
    private final List<HandlerMethod> handlerMethods = new ArrayList<>();

    /**
     * 컨트롤러 클래스를 등록합니다.
     *
     * @Controller 어노테이션이 있는지 확인하고,
     * @RequestMapping이 붙은 모든 메서드를 HandlerMethod로 변환하여 등록합니다.
     *
     * @param controllerClass 등록할 컨트롤러 클래스
     */
    public void registerController(Class<?> controllerClass) {
        // 1. @Controller 어노테이션 확인
        if (!controllerClass.isAnnotationPresent(Controller.class)) {
            throw new IllegalArgumentException(
                    "Class " + controllerClass.getName() + " is not annotated with @Controller");
        }

        try {
            // 2. 컨트롤러 인스턴스 생성 (기본 생성자 사용)
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            // 3. 클래스의 모든 메서드 스캔
            Method[] methods = controllerClass.getDeclaredMethods();

            for (Method method : methods) {
                // 4. @RequestMapping 어노테이션이 있는 메서드만 처리
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);

                    // 5. HandlerMethod 생성 및 등록
                    HandlerMethod handlerMethod = new HandlerMethod(
                            controllerInstance,
                            method,
                            mapping.value(),        // URL 경로
                            mapping.method()        // HTTP 메서드
                    );

                    handlerMethods.add(handlerMethod);

                    // 등록 로그 출력
                    System.out.println("Registered handler: " + handlerMethod);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to register controller: " + controllerClass.getName(), e);
        }
    }

    /**
     * 요청 경로와 HTTP 메서드에 매칭되는 핸들러를 찾습니다.
     *
     * @param path 요청 경로 (예: "/products")
     * @param httpMethod HTTP 메서드 (예: "GET")
     * @return 매칭되는 HandlerMethod, 없으면 null
     */
    public HandlerMethod getHandler(String path, String httpMethod) {
        System.out.println("=== AnnotationHandlerMapping.getHandler() 디버깅 ===");
        System.out.println("요청 경로: '" + path + "'");
        System.out.println("요청 메서드: '" + httpMethod + "'");
        System.out.println("등록된 핸들러 수: " + handlerMethods.size());
        for (HandlerMethod handlerMethod : handlerMethods) {
            if (handlerMethod.matches(path, httpMethod)) {
                return handlerMethod;
            }
        }
        return null; // 매칭되는 핸들러가 없음
    }

    /**
     * 등록된 모든 핸들러 메서드 목록을 반환합니다.
     *
     * @return 핸들러 메서드 리스트 (읽기 전용)
     */
    public List<HandlerMethod> getAllHandlers() {
        return new ArrayList<>(handlerMethods); // 복사본 반환
    }

    /**
     * 등록된 핸들러 수를 반환합니다.
     *
     * @return 핸들러 메서드 개수
     */
    public int getHandlerCount() {
        return handlerMethods.size();
    }
}