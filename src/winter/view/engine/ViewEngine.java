package winter.view.engine; // 뷰 엔진 관련 패키지 선언

import winter.http.HttpRequest; // HTTP 요청 객체 임포트
import winter.http.HttpResponse; // HTTP 응답 객체 임포트

import java.util.Map; // 모델 데이터를 담기 위한 Map 임포트

/**
 * 뷰 엔진 인터페이스
 * 다양한 템플릿 엔진(Thymeleaf, Mustache, JSP 등)을 통합하기 위한 추상화
 */
public interface ViewEngine { // 뷰 엔진 인터페이스 정의 시작

    /**
     * 뷰 엔진이 지원하는 파일 확장자들 반환
     * @return 지원하는 확장자 배열 (예: {"html", "htm"})
     */
    String[] getSupportedExtensions(); // 지원하는 확장자 목록을 반환하는 메서드 정의

    /**
     * 템플릿을 렌더링하여 HTML 결과 반환
     * @param templatePath 템플릿 파일 경로
     * @param model 렌더링에 사용할 모델 데이터
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 렌더링된 HTML 문자열
     * @throws Exception 렌더링 중 오류 발생 시
     */
    String render(String templatePath, Map<String, Object> model, // 템플릿 렌더링 메서드 정의 시작
                  HttpRequest request, HttpResponse response) throws Exception; // 매개변수와 예외 선언

    /**
     * 뷰 엔진 초기화
     * 엔진별 설정 로드, 캐시 초기화 등
     */
    void initialize(); // 뷰 엔진 초기화 메서드 정의

    /**
     * 뷰 엔진 이름 반환 (로깅/디버깅용)
     * @return 뷰 엔진 이름
     */
    String getEngineName(); // 엔진 이름을 반환하는 메서드 정의

    /**
     * 특정 템플릿 파일을 처리할 수 있는지 확인
     * @param templatePath 템플릿 파일 경로
     * @return 처리 가능 여부
     */
    default boolean canHandle(String templatePath) { // 템플릿 처리 가능 여부를 확인하는 디폴트 메서드 시작
        if (templatePath == null) return false; // 템플릿 경로가 null이면 false 반환

        String[] extensions = getSupportedExtensions(); // 지원하는 확장자 목록 가져오기
        if (extensions == null) return false; // 확장자 목록이 null이면 false 반환

        String lowerPath = templatePath.toLowerCase(); // 템플릿 경로를 소문자로 변환
        for (String ext : extensions) { // 지원하는 확장자들을 순회
            if (lowerPath.endsWith("." + ext.toLowerCase())) { // 템플릿 경로가 해당 확장자로 끝나는지 확인
                return true; // 일치하면 true 반환
            }
        }
        return false; // 일치하는 확장자가 없으면 false 반환
    }

    /**
     * 뷰 엔진의 우선순위 반환
     * 숫자가 낮을수록 높은 우선순위
     * @return 우선순위 (기본값: 100)
     */
    default int getPriority() { // 우선순위를 반환하는 디폴트 메서드
        return 100; // 기본 우선순위 100 반환
    }
}