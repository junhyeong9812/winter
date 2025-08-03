package winter.dispatcher;

import winter.controller.FileUploadController;
import winter.controller.ProductController;
import winter.controller.SearchController;

/**
 * 레거시 Controller 인터페이스와 어노테이션 기반 핸들러를 통합하여 관리하는 클래스
 *
 * 이 클래스는 두 가지 핸들러 방식을 동시에 지원합니다:
 * 1. 기존 방식: Controller 인터페이스 구현 (URL → Controller 객체)
 * 2. 새로운 방식: @Controller + @RequestMapping 어노테이션 (URL + HTTP Method → HandlerMethod)
 *
 * 핸들러 검색 우선순위:
 * 1. 어노테이션 기반 핸들러 우선 검색
 * 2. 어노테이션 핸들러가 없으면 레거시 핸들러 검색
 *
 * 모든 컨트롤러 등록을 이 클래스에서 중앙 관리합니다.
 */
public class CombinedHandlerMapping {

    private final HandlerMapping legacyHandlerMapping;              // 기존 Controller 인터페이스 매핑
    private final AnnotationHandlerMapping annotationHandlerMapping; // 어노테이션 기반 매핑

    /**
     * CombinedHandlerMapping 생성자
     * 모든 컨트롤러 등록을 여기서 처리
     */
    public CombinedHandlerMapping() {
        this.legacyHandlerMapping = new HandlerMapping();
        this.annotationHandlerMapping = new AnnotationHandlerMapping();

        // 모든 컨트롤러 등록을 중앙에서 관리
        registerAllControllers();
    }

    /**
     * 모든 컨트롤러를 등록하는 메서드
     * 레거시 컨트롤러는 HandlerMapping 생성자에서 자동 등록되고,
     * 어노테이션 컨트롤러는 여기서 등록합니다.
     */
    private void registerAllControllers() {
        try {
            // 22단계: 어노테이션 기반 컨트롤러들 등록
            annotationHandlerMapping.registerController(ProductController.class);

            // 23단계: 파라미터 바인딩 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(SearchController.class);

            // 24단계: 파일 업로드 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(FileUploadController.class);

            // 추후 추가될 어노테이션 컨트롤러들을 여기에 등록
            // annotationHandlerMapping.registerController(UserApiController.class);
            // annotationHandlerMapping.registerController(OrderController.class);

        } catch (Exception e) {
            System.err.println("Failed to register annotation controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 외부에서 어노테이션 컨트롤러를 추가로 등록할 수 있는 메서드
     * 테스트나 동적 등록이 필요한 경우 사용
     *
     * @param controllerClass @Controller 어노테이션이 붙은 클래스
     */
    public void registerAnnotationController(Class<?> controllerClass) {
        annotationHandlerMapping.registerController(controllerClass);
    }

    /**
     * 요청에 적합한 핸들러를 찾습니다.
     *
     * 검색 순서:
     * 1. 어노테이션 기반 핸들러 검색 (경로 + HTTP 메서드 모두 매치)
     * 2. 레거시 핸들러 검색 (경로만 매치)
     *
     * @param path 요청 경로
     * @param httpMethod HTTP 메서드
     * @return 매칭되는 핸들러 (HandlerMethod 또는 Controller 구현체), 없으면 null
     */
    public Object getHandler(String path, String httpMethod) {
        // 1. 어노테이션 기반 핸들러 우선 검색
        HandlerMethod annotationHandler = annotationHandlerMapping.getHandler(path, httpMethod);
        if (annotationHandler != null) {
            return annotationHandler;
        }

        // 2. 어노테이션 핸들러가 없으면 레거시 핸들러 검색
        Object legacyHandler = legacyHandlerMapping.getHandler(path);
        if (legacyHandler != null) {
            return legacyHandler;
        }

        // 3. 둘 다 없으면 null 반환
        return null;
    }

    /**
     * 레거시 핸들러 매핑 객체를 반환합니다.
     * 기존 코드와의 호환성을 위해 제공됩니다.
     *
     * @return HandlerMapping 객체
     */
    public HandlerMapping getLegacyHandlerMapping() {
        return legacyHandlerMapping;
    }

    /**
     * 어노테이션 핸들러 매핑 객체를 반환합니다.
     *
     * @return AnnotationHandlerMapping 객체
     */
    public AnnotationHandlerMapping getAnnotationHandlerMapping() {
        return annotationHandlerMapping;
    }

    /**
     * 현재 등록된 핸들러 정보를 출력합니다. (디버깅용)
     */
    public void printRegisteredHandlers() {
        System.out.println("=== Registered Handlers ===");

        System.out.println("\n[Annotation-based Handlers]");
        for (HandlerMethod handler : annotationHandlerMapping.getAllHandlers()) {
            System.out.println("  " + handler);
        }

        System.out.println("\n[Legacy Handlers]");
        System.out.println("  /hello → HelloController");
        System.out.println("  /bye → ByeController");
        System.out.println("  /register → RegisterController");
        System.out.println("  /user → UserController");
        System.out.println("  /api → ApiController");

        System.out.println("==========================");
    }
}