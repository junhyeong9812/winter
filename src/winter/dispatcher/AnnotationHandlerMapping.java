package winter.dispatcher;

import winter.annotation.Controller;
import winter.annotation.RestController;
import winter.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 어노테이션 기반 핸들러 매핑을 관리하는 클래스
 *
 * 30챕터 업데이트: @RestController 지원 추가
 * - @Controller 어노테이션이 붙은 클래스들을 스캔 (기존 기능)
 * - @RestController 어노테이션이 붙은 클래스들을 스캔 (신규 추가)
 * - @RequestMapping 어노테이션이 붙은 메서드들을 찾아서 URL과 핸들러 메서드 매핑
 *
 * 지원하는 컨트롤러 타입:
 * - @Controller: 전통적인 MVC 컨트롤러 (ModelAndView 반환)
 * - @RestController: REST API 컨트롤러 (JSON 응답)
 *
 * 처리 흐름:
 * 1. 컨트롤러 클래스 등록 시 @Controller 또는 @RestController 확인
 * 2. @RequestMapping이 붙은 모든 메서드를 HandlerMethod로 변환
 * 3. 요청 시 경로와 HTTP 메서드로 적절한 핸들러 조회
 * 4. HandlerAdapter가 컨트롤러 타입에 따라 적절히 처리
 *
 * 현재는 수동 등록 방식이지만, 추후 클래스패스 자동 스캔으로 확장 가능합니다.
 */
public class AnnotationHandlerMapping {

    // 등록된 모든 핸들러 메서드 목록
    private final List<HandlerMethod> handlerMethods = new ArrayList<>();

    /**
     * 30챕터 업데이트: @Controller 또는 @RestController 클래스를 등록합니다.
     *
     * 지원하는 어노테이션:
     * - @Controller: 전통적인 MVC 컨트롤러
     * - @RestController: REST API 컨트롤러 (30챕터 신규)
     *
     * @RequestMapping이 붙은 모든 메서드를 HandlerMethod로 변환하여 등록합니다.
     *
     * @param controllerClass 등록할 컨트롤러 클래스
     */
    public void registerController(Class<?> controllerClass) {
        // 1. @Controller 또는 @RestController 어노테이션 확인
        boolean isController = controllerClass.isAnnotationPresent(Controller.class);
        boolean isRestController = controllerClass.isAnnotationPresent(RestController.class);

        if (!isController && !isRestController) {
            throw new IllegalArgumentException(
                    "Class " + controllerClass.getName() +
                            " is not annotated with @Controller or @RestController");
        }

        // 컨트롤러 타입 로깅
        String controllerType = isRestController ? "@RestController" : "@Controller";
        System.out.println("=== " + controllerType + " 등록 시작 ===");
        System.out.println("클래스: " + controllerClass.getSimpleName());

        try {
            // 2. 컨트롤러 인스턴스 생성 (기본 생성자 사용)
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

            // 3. 클래스의 모든 메서드 스캔
            Method[] methods = controllerClass.getDeclaredMethods();
            int registeredMethodCount = 0;

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
                    registeredMethodCount++;

                    // 등록 로그 출력 (30챕터: REST 정보 포함)
                    System.out.println("핸들러 등록: " +
                            controllerType + " " +
                            controllerClass.getSimpleName() + "." + method.getName() +
                            " → " + mapping.value() +
                            (mapping.method().isEmpty() ? " [ALL]" : " [" + mapping.method() + "]"));
                }
            }

            System.out.println(controllerType + " 등록 완료: " +
                    registeredMethodCount + "개 메서드");
            System.out.println("===================================");

        } catch (Exception e) {
            throw new RuntimeException("Failed to register controller: " + controllerClass.getName(), e);
        }
    }

    /**
     * 요청 경로와 HTTP 메서드에 매칭되는 핸들러를 찾습니다.
     *
     * @param path 요청 경로 (예: "/products", "/api/users")
     * @param httpMethod HTTP 메서드 (예: "GET", "POST")
     * @return 매칭되는 HandlerMethod, 없으면 null
     */
    public HandlerMethod getHandler(String path, String httpMethod) {
        System.out.println("=== HandlerMapping 조회 ===");
        System.out.println("요청 경로: '" + path + "'");
        System.out.println("요청 메서드: '" + httpMethod + "'");
        System.out.println("등록된 핸들러 수: " + handlerMethods.size());

        // 30챕터: 조회 로직은 동일하지만 REST/MVC 구분 로깅 추가
        for (HandlerMethod handlerMethod : handlerMethods) {
            if (handlerMethod.matches(path, httpMethod)) {
                // 30챕터: 매칭된 핸들러가 REST 타입인지 확인
                Object controller = handlerMethod.getController();
                boolean isRestController = controller.getClass().isAnnotationPresent(RestController.class);
                String controllerType = isRestController ? "RestController" : "Controller";

                System.out.println("핸들러 매칭: " + controllerType + " " +
                        controller.getClass().getSimpleName() + "." +
                        handlerMethod.getMethod().getName());
                System.out.println("=========================");

                return handlerMethod;
            }
        }

        System.out.println("매칭되는 핸들러 없음");
        System.out.println("=========================");
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

    /**
     * 30챕터 추가: 등록된 핸들러 정보를 출력합니다.
     * 디버깅 및 운영 모니터링 목적
     */
    public void printRegisteredHandlers() {
        System.out.println("\n=== 등록된 핸들러 목록 ===");
        System.out.println("총 핸들러 수: " + handlerMethods.size());

        int controllerCount = 0;
        int restControllerCount = 0;

        for (HandlerMethod handlerMethod : handlerMethods) {
            Object controller = handlerMethod.getController();
            boolean isRestController = controller.getClass().isAnnotationPresent(RestController.class);

            if (isRestController) {
                restControllerCount++;
            } else {
                controllerCount++;
            }

            String controllerType = isRestController ? "[REST]" : "[MVC ]";
            System.out.println(controllerType + " " +
                    handlerMethod.getUrlPattern() +
                    " " + (handlerMethod.getHttpMethod().isEmpty() ? "[ALL]" : "[" + handlerMethod.getHttpMethod() + "]") +
                    " → " + controller.getClass().getSimpleName() + "." +
                    handlerMethod.getMethod().getName());
        }

        System.out.println("MVC 컨트롤러: " + controllerCount + "개");
        System.out.println("REST 컨트롤러: " + restControllerCount + "개");
        System.out.println("========================\n");
    }

    /**
     * 30챕터 추가: 자동 스캔 지원을 위한 기반 메서드
     * 추후 클래스패스 스캔 기능 구현 시 사용 예정
     *
     * @param packageName 스캔할 패키지명
     */
    public void scanPackage(String packageName) {
        // TODO: 클래스패스 스캔 기능 구현 예정
        // 현재는 수동 등록만 지원
        System.out.println("자동 스캔 기능은 아직 구현되지 않았습니다: " + packageName);
        System.out.println("현재는 수동 등록만 지원됩니다.");
    }

    /**
     * 30챕터 추가: 컨트롤러 타입별 통계 조회
     *
     * @return {mvc: count, rest: count} 형태의 통계
     */
    public java.util.Map<String, Integer> getControllerStats() {
        int mvcCount = 0;
        int restCount = 0;

        for (HandlerMethod handlerMethod : handlerMethods) {
            Object controller = handlerMethod.getController();
            boolean isRestController = controller.getClass().isAnnotationPresent(RestController.class);

            if (isRestController) {
                restCount++;
            } else {
                mvcCount++;
            }
        }

        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("mvc", mvcCount);
        stats.put("rest", restCount);
        stats.put("total", mvcCount + restCount);

        return stats;
    }
}