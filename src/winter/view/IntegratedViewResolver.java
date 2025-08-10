package winter.view;

import winter.view.engine.*;

import java.io.File;
import java.util.Arrays;

/**
 * 통합 ViewResolver
 * 여러 뷰 엔진을 통합하여 적절한 뷰를 선택하는 ViewResolver
 */
public class IntegratedViewResolver implements ViewResolver {

    private ViewEngineRegistry engineRegistry;
    private String templatePrefix = "src/winter/templates/";
    private ContentNegotiatingViewResolver contentNegotiatingViewResolver;

    public IntegratedViewResolver() {
        initializeEngineRegistry();
        initializeContentNegotiatingViewResolver();
    }

    /**
     * 뷰 엔진 레지스트리 초기화
     */
    private void initializeEngineRegistry() {
        engineRegistry = new ViewEngineRegistry();

        // 내장 뷰 엔진들 등록
        engineRegistry.registerEngine(new SimpleTemplateEngine());
        engineRegistry.registerEngine(new MockThymeleafEngine());
        engineRegistry.registerEngine(new MockMustacheEngine());
        engineRegistry.registerEngine(new MockJspEngine());

        // 기본 엔진 설정 (SimpleTemplateEngine)
        engineRegistry.setDefaultEngine(new SimpleTemplateEngine());

        // 레지스트리 정보 출력
        engineRegistry.printRegistryInfo();
    }

    /**
     * Content Negotiating ViewResolver 초기화
     */
    private void initializeContentNegotiatingViewResolver() {
        contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
    }

    /**
     * 현재 요청 설정 (Content Negotiation용)
     */
    public void setCurrentRequest(winter.http.HttpRequest request) {
        contentNegotiatingViewResolver.setCurrentRequest(request);
    }

    @Override
    public View resolveViewName(String viewName) {
        System.out.println("\n=== IntegratedViewResolver.resolveViewName ===");
        System.out.println("요청된 뷰명: " + viewName);

        // 1. JSON 뷰 우선 확인 (기존 로직)
        View jsonView = contentNegotiatingViewResolver.resolveViewName(viewName);
        if (jsonView instanceof JsonView) {
            System.out.println("JSON 뷰 선택됨");
            return jsonView;
        }

        // 2. 템플릿 기반 뷰 처리
        return resolveTemplateView(viewName);
    }

    /**
     * 템플릿 기반 뷰 해결
     */
    private View resolveTemplateView(String viewName) {
        // 템플릿 파일 후보들 검색
        String[] templateCandidates = findTemplateFiles(viewName);

        if (templateCandidates.length == 0) {
            System.out.println("템플릿 파일을 찾을 수 없음: " + viewName);
            return createErrorView(viewName);
        }

        // 우선순위에 따라 첫 번째 템플릿 선택
        String selectedTemplate = templateCandidates[0];
        System.out.println("선택된 템플릿: " + selectedTemplate);

        // 해당 템플릿에 맞는 뷰 엔진 선택
        ViewEngine engine = engineRegistry.getEngineForTemplate(selectedTemplate);

        if (engine == null) {
            System.out.println("적절한 뷰 엔진을 찾을 수 없음: " + selectedTemplate);
            return createErrorView(viewName);
        }

        System.out.println("선택된 뷰 엔진: " + engine.getEngineName());

        // IntegratedView 생성
        return new IntegratedView(engine, selectedTemplate);
    }

    /**
     * 뷰명에 해당하는 템플릿 파일들 검색
     */
    private String[] findTemplateFiles(String viewName) {
        File templateDir = new File(templatePrefix);
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            System.out.println("템플릿 디렉토리가 존재하지 않음: " + templatePrefix);
            return new String[0];
        }

        // 지원하는 모든 확장자로 파일 검색
        String[] allExtensions = getAllSupportedExtensions();

        return Arrays.stream(allExtensions)
                .map(ext -> templatePrefix + viewName + "." + ext)
                .filter(path -> new File(path).exists())
                .sorted(this::compareByPriority)
                .toArray(String[]::new);
    }

    /**
     * 등록된 모든 뷰 엔진의 지원 확장자 수집
     */
    private String[] getAllSupportedExtensions() {
        return engineRegistry.getAllEngines().stream()
                .flatMap(engine -> Arrays.stream(engine.getSupportedExtensions()))
                .distinct()
                .toArray(String[]::new);
    }

    /**
     * 템플릿 파일 우선순위 비교
     * 뷰 엔진의 우선순위에 따라 정렬
     */
    private int compareByPriority(String template1, String template2) {
        ViewEngine engine1 = engineRegistry.getEngineForTemplate(template1);
        ViewEngine engine2 = engineRegistry.getEngineForTemplate(template2);

        int priority1 = engine1 != null ? engine1.getPriority() : Integer.MAX_VALUE;
        int priority2 = engine2 != null ? engine2.getPriority() : Integer.MAX_VALUE;

        return Integer.compare(priority1, priority2);
    }

    /**
     * 에러 뷰 생성
     */
    private View createErrorView(String viewName) {
        // 간단한 에러 HTML 생성
        String errorHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>View Not Found</title>
                </head>
                <body>
                    <h1>404 - View Not Found</h1>
                    <p>요청한 뷰를 찾을 수 없습니다: <strong>%s</strong></p>
                    <p>템플릿 디렉토리: %s</p>
                    <p>지원하는 확장자: %s</p>
                </body>
                </html>
                """.formatted(viewName, templatePrefix, Arrays.toString(getAllSupportedExtensions()));

        return new StaticView(errorHtml);
    }

    /**
     * 뷰 엔진 레지스트리 반환 (테스트용)
     */
    public ViewEngineRegistry getEngineRegistry() {
        return engineRegistry;
    }

    /**
     * 템플릿 접두사 설정
     */
    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
        if (!this.templatePrefix.endsWith("/")) {
            this.templatePrefix += "/";
        }
        System.out.println("템플릿 접두사 설정: " + this.templatePrefix);
    }

    /**
     * 뷰 엔진 추가 등록
     */
    public void addViewEngine(ViewEngine engine) {
        engineRegistry.registerEngine(engine);
    }

    /**
     * 정적 HTML을 반환하는 간단한 뷰
     */
    private static class StaticView implements View {
        private final String html;

        public StaticView(String html) {
            this.html = html;
        }

        @Override
        public void render(java.util.Map<String, Object> model,
                           winter.http.HttpRequest request,
                           winter.http.HttpResponse response) {
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(404);
            response.getWriter().write(html);
            response.getWriter().flush();
        }
    }
}