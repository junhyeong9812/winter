package winter.dispatcher;

import winter.controller.*;

/**
 * 레거시 Controller 인터페이스와 어노테이션 기반 핸들러를 통합하여 관리하는 클래스
 *
 * 30챕터 업데이트: @RestController 지원 추가
 * 이 클래스는 세 가지 핸들러 방식을 동시에 지원합니다:
 * 1. 기존 방식: Controller 인터페이스 구현 (URL → Controller 객체)
 * 2. MVC 방식: @Controller + @RequestMapping 어노테이션 (URL + HTTP Method → HandlerMethod)
 * 3. REST 방식: @RestController + @RequestMapping 어노테이션 (URL + HTTP Method → JSON 응답)
 *
 * 핸들러 검색 우선순위:
 * 1. 어노테이션 기반 핸들러 우선 검색 (@Controller, @RestController 모두 포함)
 * 2. 어노테이션 핸들러가 없으면 레거시 핸들러 검색
 *
 * REST API 지원:
 * - @RestController: 모든 메서드가 JSON 응답 (RestHandlerAdapter 처리)
 * - @ResponseBody: 개별 메서드만 JSON 응답 (RestHandlerAdapter 처리)
 * - ResponseEntity: HTTP 상태 코드와 헤더까지 제어 가능
 *
 * 모든 컨트롤러 등록을 이 클래스에서 중앙 관리합니다.
 */
public class CombinedHandlerMapping {

    private final HandlerMapping legacyHandlerMapping;              // 기존 Controller 인터페이스 매핑
    private final AnnotationHandlerMapping annotationHandlerMapping; // 어노테이션 기반 매핑 (MVC + REST)

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
     * 30챕터 업데이트: 모든 컨트롤러를 등록하는 메서드
     * 레거시, MVC, REST 컨트롤러를 모두 등록합니다.
     */
    private void registerAllControllers() {
        try {
            System.out.println("=== 30챕터: 모든 컨트롤러 등록 시작 ===");

            // === MVC 컨트롤러 등록 (@Controller) ===
            System.out.println("\n[MVC 컨트롤러 등록]");

            // 22단계: 어노테이션 기반 컨트롤러들 등록
            annotationHandlerMapping.registerController(ProductController.class);

            // 23단계: 파라미터 바인딩 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(SearchController.class);

            // 24단계: 파일 업로드 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(FileUploadController.class);

            // 25단계: 세션 관리 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(SessionController.class);

            // 26단계: View Engine Integration 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(ViewEngineController.class);

            // 28단계: 검증 테스트 컨트롤러 등록
            annotationHandlerMapping.registerController(ValidationController.class);

            // 27단계: 인터셉터 테스트 컨트롤러 등록 (있다면)
            try {
                annotationHandlerMapping.registerController(InterceptorTestController.class);
            } catch (Exception e) {
                System.out.println("InterceptorTestController 등록 실패 (선택사항): " + e.getMessage());
            }

            // === REST API 컨트롤러 등록 (@RestController) ===
            System.out.println("\n[REST API 컨트롤러 등록]");

            // 30단계: ResponseEntity 기반 REST API 컨트롤러 등록
            try {
                // ResponseEntityController가 @RestController로 변경되었다고 가정
                // 만약 여전히 @Controller라면 기존대로 MVC 컨트롤러로 처리
                annotationHandlerMapping.registerController(ResponseEntityController.class);
                System.out.println("ResponseEntityController 등록 완료 (REST API)");
            } catch (Exception e) {
                System.err.println("ResponseEntityController 등록 실패: " + e.getMessage());
            }

            // 추후 추가될 REST 컨트롤러들을 여기에 등록
            // annotationHandlerMapping.registerController(UserApiController.class);
            // annotationHandlerMapping.registerController(ProductApiController.class);
            // annotationHandlerMapping.registerController(OrderApiController.class);

            // === 등록 결과 요약 ===
            System.out.println("\n=== 컨트롤러 등록 완료 ===");

            // 30챕터: 컨트롤러 타입별 통계 출력
            java.util.Map<String, Integer> stats = annotationHandlerMapping.getControllerStats();
            System.out.println("MVC 컨트롤러: " + stats.get("mvc") + "개 메서드");
            System.out.println("REST 컨트롤러: " + stats.get("rest") + "개 메서드");
            System.out.println("총 어노테이션 핸들러: " + stats.get("total") + "개 메서드");
            System.out.println("레거시 핸들러: 5개 (HelloController, ByeController, RegisterController, UserController, ApiController)");

            System.out.println("\n등록된 주요 기능:");
            System.out.println("  ✓ 전통적인 MVC 패턴 (@Controller → HTML 템플릿)");
            System.out.println("  ✓ REST API 패턴 (@RestController → JSON 응답)");
            System.out.println("  ✓ ResponseEntity 지원 (상태 코드 + 헤더 제어)");
            System.out.println("  ✓ Content Negotiation (Accept 헤더 기반)");
            System.out.println("  ✓ 파라미터 바인딩 (@RequestParam, @ModelAttribute)");
            System.out.println("  ✓ 파일 업로드 (Multipart)");
            System.out.println("  ✓ 세션 관리");
            System.out.println("  ✓ 검증 기능 (@Valid)");
            System.out.println("  ✓ 다양한 뷰 엔진 (HTML, Thymeleaf, Mustache, JSP)");
            System.out.println("===============================\n");

        } catch (Exception e) {
            System.err.println("Failed to register annotation controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 외부에서 어노테이션 컨트롤러를 추가로 등록할 수 있는 메서드
     * 테스트나 동적 등록이 필요한 경우 사용
     *
     * 30챕터: @Controller와 @RestController 모두 지원
     *
     * @param controllerClass @Controller 또는 @RestController 어노테이션이 붙은 클래스
     */
    public void registerAnnotationController(Class<?> controllerClass) {
        annotationHandlerMapping.registerController(controllerClass);
    }

    /**
     * 요청에 적합한 핸들러를 찾습니다.
     *
     * 30챕터: 검색 순서는 동일하지만, REST 컨트롤러도 포함하여 검색
     * 1. 어노테이션 기반 핸들러 검색 (경로 + HTTP 메서드 모두 매치)
     *    - @Controller 기반 MVC 핸들러
     *    - @RestController 기반 REST 핸들러
     * 2. 레거시 핸들러 검색 (경로만 매치)
     *
     * @param path 요청 경로
     * @param httpMethod HTTP 메서드
     * @return 매칭되는 핸들러 (HandlerMethod 또는 Controller 구현체), 없으면 null
     */
    public Object getHandler(String path, String httpMethod) {
        // 1. 어노테이션 기반 핸들러 우선 검색 (MVC + REST 모두 포함)
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
     * @return AnnotationHandlerMapping 객체 (MVC + REST 모두 포함)
     */
    public AnnotationHandlerMapping getAnnotationHandlerMapping() {
        return annotationHandlerMapping;
    }

    /**
     * 30챕터 업데이트: 현재 등록된 핸들러 정보를 출력합니다. (디버깅용)
     * MVC와 REST 컨트롤러를 구분하여 표시합니다.
     */
    public void printRegisteredHandlers() {
        System.out.println("\n=== 30챕터: 등록된 모든 핸들러 ===");

        // 어노테이션 기반 핸들러 출력 (MVC + REST 구분)
        annotationHandlerMapping.printRegisteredHandlers();

        // 레거시 핸들러 출력
        System.out.println("[Legacy Handlers - 기존 Controller 인터페이스]");
        System.out.println("  /hello → HelloController");
        System.out.println("  /bye → ByeController");
        System.out.println("  /register → RegisterController");
        System.out.println("  /user → UserController");
        System.out.println("  /api → ApiController");

        System.out.println("\n=== 핸들러 어댑터 지원 현황 ===");
        System.out.println("  ✓ RestHandlerAdapter: @RestController, @ResponseBody 처리");
        System.out.println("  ✓ AnnotationHandlerAdapter: @Controller 처리 (ModelAndView)");
        System.out.println("  ✓ ControllerHandlerAdapter: 레거시 Controller 인터페이스 처리");
        System.out.println("==============================\n");
    }

    /**
     * 30챕터 추가: REST API 엔드포인트 목록 조회
     * API 문서 생성이나 운영 모니터링 목적
     *
     * @return REST API 엔드포인트 목록
     */
    public java.util.List<String> getRestApiEndpoints() {
        java.util.List<String> endpoints = new java.util.ArrayList<>();

        for (HandlerMethod handler : annotationHandlerMapping.getAllHandlers()) {
            Object controller = handler.getController();
            boolean isRestController = controller.getClass().isAnnotationPresent(
                    winter.annotation.RestController.class);

            if (isRestController) {
                String endpoint = handler.getHttpMethod() + " " + handler.getUrlPattern();
                endpoints.add(endpoint);
            }
        }

        return endpoints;
    }

    /**
     * 30챕터 추가: MVC 페이지 목록 조회
     * 사이트맵 생성이나 운영 모니터링 목적
     *
     * @return MVC 페이지 목록
     */
    public java.util.List<String> getMvcPageEndpoints() {
        java.util.List<String> endpoints = new java.util.ArrayList<>();

        for (HandlerMethod handler : annotationHandlerMapping.getAllHandlers()) {
            Object controller = handler.getController();
            boolean isController = controller.getClass().isAnnotationPresent(
                    winter.annotation.Controller.class);
            boolean isRestController = controller.getClass().isAnnotationPresent(
                    winter.annotation.RestController.class);

            if (isController && !isRestController) {
                String endpoint = handler.getHttpMethod() + " " + handler.getUrlPattern();
                endpoints.add(endpoint);
            }
        }

        // 레거시 핸들러도 추가
        endpoints.add("GET /hello");
        endpoints.add("GET /bye");
        endpoints.add("GET /register");
        endpoints.add("GET /user");
        endpoints.add("GET /api");

        return endpoints;
    }
}