package winter.view;

import winter.http.HttpRequest; // 26챕터 수정: HttpRequest 파라미터 지원을 위한 임포트
import winter.http.HttpResponse;
import winter.view.engine.ViewEngine;

import java.util.Map;

/**
 * 통합된 뷰 구현체
 * 선택된 뷰 엔진을 사용하여 템플릿을 렌더링하는 뷰
 * 26챕터: View Engine Integration의 핵심 뷰 클래스
 */
public class IntegratedView implements View {

    private final ViewEngine engine; // 이 뷰에서 사용할 뷰 엔진 (SimpleTemplate, Thymeleaf, Mustache, JSP 등)
    private final String templatePath; // 렌더링할 템플릿 파일의 전체 경로

    // 생성자: 뷰 엔진과 템플릿 경로를 받아서 초기화
    public IntegratedView(ViewEngine engine, String templatePath) {
        this.engine = engine;
        this.templatePath = templatePath;

        // 생성 정보 로깅 (디버깅용)
        System.out.println("IntegratedView 생성 - 엔진: " + engine.getEngineName() +
                ", 템플릿: " + templatePath);
    }

    // 26챕터 수정: HttpRequest request 파라미터 추가
    // 기존 시그니처: render(Map<String, Object> model, HttpResponse response)
    // 새 시그니처: render(Map<String, Object> model, HttpRequest request, HttpResponse response)
    // 뷰 엔진에서 요청 정보(헤더, 세션 등)를 활용할 수 있도록 HttpRequest 추가
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        try {
            // 렌더링 시작 로깅
            System.out.println("\n=== IntegratedView.render 시작 ===");
            System.out.println("뷰 엔진: " + engine.getEngineName()); // 사용 중인 뷰 엔진 이름
            System.out.println("템플릿 경로: " + templatePath); // 렌더링할 템플릿 파일 경로
            System.out.println("모델 데이터: " + model); // 템플릿에 전달할 모델 데이터

            // 렌더링 성능 측정 시작
            long startTime = System.currentTimeMillis();

            // 26챕터 핵심: 뷰 엔진을 사용하여 템플릿 렌더링
            // ViewEngine.render()는 템플릿 파일을 읽고 모델 데이터로 치환하여 최종 HTML 생성
            String renderedContent = engine.render(templatePath, model, request, response);

            // 렌더링 성능 측정 완료
            long renderTime = System.currentTimeMillis() - startTime;

            // HTTP 응답 헤더 설정
            response.setContentType("text/html; charset=UTF-8"); // HTML 컨텐츠 타입 설정
            response.setStatus(200); // 200 OK 상태 코드 설정

            // 26챕터 수정: Writer를 사용하여 렌더링된 내용을 응답에 작성
            // 기존: response.setBody(renderedContent);
            // 변경: HttpResponse.getWriter()를 사용하여 스트림 방식으로 출력
            response.getWriter().write(renderedContent); // 렌더링 결과를 Writer에 작성
            response.getWriter().flush(); // 버퍼의 내용을 즉시 출력

            // 렌더링 완료 로깅
            System.out.println("렌더링 완료 (" + renderTime + "ms)"); // 소요 시간 출력
            System.out.println("응답 크기: " + renderedContent.length() + " 문자"); // 응답 크기 출력
            System.out.println("=== IntegratedView.render 완료 ===\n");

        } catch (Exception e) {
            // 템플릿 렌더링 중 오류 발생 시 처리
            System.err.println("템플릿 렌더링 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 상세 스택 트레이스 출력

            // 오류 페이지 렌더링
            renderErrorPage(e, response);
        }
    }

    /**
     * 오류 페이지 렌더링
     * 템플릿 렌더링 실패 시 사용자에게 표시할 에러 페이지 생성
     */
    private void renderErrorPage(Exception e, HttpResponse response) {
        try {
            // 사용자 친화적인 에러 HTML 생성
            String errorHtml = createErrorHtml(e);

            // HTTP 응답 설정
            response.setContentType("text/html; charset=UTF-8"); // HTML 컨텐츠 타입
            response.setStatus(500); // 500 Internal Server Error 상태 코드

            // 26챕터 수정: Writer를 사용하여 에러 페이지 출력
            response.getWriter().write(errorHtml); // 에러 HTML을 Writer에 작성
            response.getWriter().flush(); // 버퍼 내용 즉시 출력

        } catch (Exception ex) {
            // 에러 페이지 렌더링 중에도 오류가 발생한 경우
            System.err.println("오류 페이지 렌더링 중 추가 오류 발생: " + ex.getMessage());
        }
    }

    /**
     * 오류 HTML 생성
     * 템플릿 렌더링 실패에 대한 상세 정보를 포함한 HTML 페이지 생성
     */
    private String createErrorHtml(Exception e) {
        StringBuilder html = new StringBuilder();

        // HTML 기본 구조 시작
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>Template Rendering Error</title>\n");

        // CSS 스타일 추가 (사용자 친화적인 디자인)
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 40px; }\n"); // 기본 폰트 및 여백
        html.append("        .error { color: #d32f2f; }\n"); // 에러 텍스트 색상
        html.append("        .details { background: #f5f5f5; padding: 15px; margin: 10px 0; }\n"); // 상세 정보 배경
        html.append("        .code { font-family: monospace; background: #eeeeee; padding: 10px; }\n"); // 코드 블록 스타일
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        // 에러 페이지 헤더
        html.append("    <h1 class=\"error\">🚨 Template Rendering Error</h1>\n");

        // 에러 상세 정보
        html.append("    <div class=\"details\">\n");
        html.append("        <strong>뷰 엔진:</strong> ").append(engine.getEngineName()).append("<br>\n"); // 사용된 뷰 엔진
        html.append("        <strong>템플릿 경로:</strong> ").append(templatePath).append("<br>\n"); // 문제가 발생한 템플릿 경로
        html.append("        <strong>오류 메시지:</strong> ").append(e.getMessage()).append("<br>\n"); // 예외 메시지
        html.append("        <strong>오류 타입:</strong> ").append(e.getClass().getSimpleName()).append("\n"); // 예외 클래스명
        html.append("    </div>\n");

        // 스택 트레이스 (간략히, Winter 패키지만)
        html.append("    <div class=\"code\">\n");
        html.append("        <strong>스택 트레이스:</strong><br>\n");
        for (StackTraceElement element : e.getStackTrace()) {
            // Winter 프레임워크 관련 스택만 표시 (가독성 향상)
            if (element.getClassName().startsWith("winter")) {
                html.append("        ").append(element.toString()).append("<br>\n");
            }
        }
        html.append("    </div>\n");

        // 사용자 액션
        html.append("    <p><a href=\"javascript:history.back()\">← 이전 페이지로 돌아가기</a></p>\n");

        // HTML 종료
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString(); // 완성된 에러 HTML 반환
    }

    /**
     * 뷰 엔진 반환 (디버깅용)
     * 테스트나 디버깅 시 현재 사용 중인 뷰 엔진 확인
     */
    public ViewEngine getViewEngine() {
        return engine;
    }

    /**
     * 템플릿 경로 반환 (디버깅용)
     * 테스트나 디버깅 시 현재 템플릿 파일 경로 확인
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * 뷰 정보 출력 (디버깅용)
     * 현재 IntegratedView의 상태를 콘솔에 출력
     */
    public void printViewInfo() {
        System.out.println("IntegratedView 정보:");
        System.out.println("  - 뷰 엔진: " + engine.getEngineName()); // 뷰 엔진 이름
        System.out.println("  - 템플릿 경로: " + templatePath); // 템플릿 파일 경로
        System.out.println("  - 지원 확장자: " + java.util.Arrays.toString(engine.getSupportedExtensions())); // 지원하는 파일 확장자
        System.out.println("  - 우선순위: " + engine.getPriority()); // 뷰 엔진 우선순위
    }
}