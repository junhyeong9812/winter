package winter.view;

import winter.http.HttpRequest; // 26챕터 추가: HttpRequest 파라미터 지원을 위한 임포트
import winter.http.HttpResponse;
import winter.util.JsonSerializer;

import java.util.Map;

/**
 * Json형태로 응답을 렌더링하는 View 구현체
 *
 * 기존 InternalResourceView가 HTML 템플릿을 처리한다면,
 * jsonView는 모델 데이터를 JSON형태로 직렬화하여 응답합니다.
 *
 * 주요 특징:
 * - Content-Type를 application/json으로 설정
 * - 모델 데이터를 Json문자로 변환
 * - HTTP 상태 코드 200으로 정상 응답처리
 *
 * 26챕터 수정: render 메서드에 HttpRequest 파라미터 추가
 * */
public class JsonView implements View {

    // 26챕터 수정: HttpRequest request 파라미터 추가
    // 기존 시그니처: render(Map<String, Object> model, HttpResponse response)
    // 새 시그니처: render(Map<String, Object> model, HttpRequest request, HttpResponse response)
    // 뷰 엔진에서 요청 정보를 활용할 수 있도록 HttpRequest 추가
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        // JSON 응답 헤더 설정 - MIME 타입을 application/json으로 설정
        response.setContentType("application/json; charset=UTF-8");

        // HTTP 상태 코드를 200 OK로 설정
        response.setStatus(200);

        try {
            // 모델 데이터를 JSON 문자열로 직렬화
            String jsonBody = JsonSerializer.toJson(model);

            // 직렬화된 JSON을 응답 본문으로 설정
            response.setBody(jsonBody);

            // 로깅: JSON 변환 성공 정보 출력
            System.out.println("JSON 변환 완료: " + model.size() + "개 속성, " +
                    jsonBody.length() + " 문자");

        } catch (Exception e) {
            // JSON 직렬화 실패 시 에러 응답 처리 - 기존 메서드 활용
            handleJsonError(response, e);
        }
    }

    /**
     * JSON 변환 실패 시 에러 응답을 구성하는 메서드 (기존 메서드 보존)
     *
     * 이 메서드는 기존 코드에서 중요한 에러 처리 로직을 담당하므로 유지
     *
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     * */
    private void handleJsonError(HttpResponse response, Exception e) {
        // 500 Internal Server Error로 설정
        response.setStatus(500);
        response.addHeader("Content-Type", "application/json");

        // 에러 정보를 JSON 형태로 구성
        String errorJson = String.format(
                "{\"error\": \"JSON_SERIALIZATION_FAILED\",\"message\": \"%s\" }",
                e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error"
        );

        response.setBody(errorJson);

        // 추가 로깅: 에러 상세 정보 출력
        System.err.println("JSON 직렬화 실패: " + e.getMessage());
    }
}