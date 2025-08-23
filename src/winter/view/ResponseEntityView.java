package winter.view;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.ResponseEntity;
import winter.util.JsonSerializer;

import java.util.Map;

/**
 * ResponseEntity 객체를 처리하는 전용 View 구현체
 *
 * 30챕터에서 추가된 REST API 지원을 위한 핵심 클래스입니다.
 * ResponseEntity는 HTTP 상태 코드, 헤더, 본문을 모두 제어할 수 있는
 * Spring의 ResponseEntity와 동일한 기능을 제공합니다.
 *
 * 주요 기능:
 * - ResponseEntity의 상태 코드를 HTTP 응답에 설정
 * - ResponseEntity의 헤더를 HTTP 응답에 추가
 * - ResponseEntity의 본문을 JSON으로 직렬화하여 응답
 * - 본문이 없는 경우(예: 204 No Content) 적절히 처리
 *
 * 처리 흐름:
 * 1. 모델에서 ResponseEntity 객체 추출
 * 2. 상태 코드 설정
 * 3. 헤더 설정
 * 4. 본문 JSON 직렬화 및 응답
 *
 * 사용 예시:
 * @RestController
 * public class ApiController {
 *     @RequestMapping("/api/users")
 *     public ResponseEntity<List<User>> getUsers() {
 *         return ResponseEntity.ok(users)
 *                 .withHeader("X-Total-Count", "10")
 *                 .withHeader("Cache-Control", "max-age=300");
 *     }
 *
 *     @RequestMapping("/api/user/{id}")
 *     public ResponseEntity<User> getUser(@RequestParam("id") String id) {
 *         User user = findUser(id);
 *         if (user == null) {
 *             return ResponseEntity.notFound(); // 404 응답
 *         }
 *         return ResponseEntity.ok(user); // 200 응답 + JSON 본문
 *     }
 * }
 */
public class ResponseEntityView implements View {

    /**
     * ResponseEntity 객체의 모델 키
     * RestHandlerAdapter에서 ResponseEntity를 모델에 저장할 때 사용하는 키
     */
    public static final String RESPONSE_ENTITY_KEY = "responseEntity";

    /**
     * ResponseEntity 객체를 HTTP 응답으로 렌더링
     *
     * @param model 모델 데이터 (ResponseEntity 객체 포함)
     * @param request HTTP 요청 객체 (현재 미사용, 향후 확장 가능)
     * @param response HTTP 응답 객체
     */
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        // 모델에서 ResponseEntity 객체 추출
        Object responseEntityObj = model.get(RESPONSE_ENTITY_KEY);

        if (responseEntityObj == null) {
            // ResponseEntity가 없는 경우 에러 처리
            handleMissingResponseEntity(response);
            return;
        }

        if (!(responseEntityObj instanceof ResponseEntity)) {
            // ResponseEntity 타입이 아닌 경우 에러 처리
            handleInvalidResponseEntity(response, responseEntityObj);
            return;
        }

        @SuppressWarnings("unchecked")
        ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) responseEntityObj;

        try {
            // ResponseEntity 내용을 HTTP 응답으로 변환
            renderResponseEntity(responseEntity, response);

            System.out.println("ResponseEntity 렌더링 완료: " +
                    "상태=" + responseEntity.getStatusCode() +
                    ", 헤더=" + responseEntity.getHeaders().size() + "개" +
                    ", 본문=" + (responseEntity.hasBody() ? "있음" : "없음"));

        } catch (Exception e) {
            // 렌더링 실패 시 에러 응답 처리
            handleRenderingError(response, e);
        }
    }

    /**
     * ResponseEntity 객체를 HTTP 응답으로 변환하는 핵심 메서드
     *
     * @param responseEntity 변환할 ResponseEntity 객체
     * @param response HTTP 응답 객체
     */
    private void renderResponseEntity(ResponseEntity<Object> responseEntity, HttpResponse response) {
        // 1. HTTP 상태 코드 설정
        response.setStatus(responseEntity.getStatusCode().getCode());
        System.out.println("HTTP 상태 코드 설정: " + responseEntity.getStatusCode());

        // 2. HTTP 헤더 설정
        Map<String, String> headers = responseEntity.getHeaders();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.addHeader(header.getKey(), header.getValue());
            System.out.println("HTTP 헤더 추가: " + header.getKey() + " = " + header.getValue());
        }

        // 3. Content-Type 헤더가 없는 경우 JSON으로 기본 설정
        if (!headers.containsKey("Content-Type") && responseEntity.hasBody()) {
            response.setContentType("application/json; charset=UTF-8");
            System.out.println("기본 Content-Type 설정: application/json");
        }

        // 4. 응답 본문 처리
        if (responseEntity.hasBody()) {
            // 본문이 있는 경우 JSON 직렬화
            renderResponseBody(responseEntity.getBody(), response);
        } else {
            // 본문이 없는 경우 (예: 204 No Content, 404 Not Found 등)
            System.out.println("응답 본문 없음 - 상태 코드만 응답");
        }
    }

    /**
     * ResponseEntity의 본문을 JSON으로 직렬화하여 응답에 설정
     *
     * @param body 직렬화할 본문 객체
     * @param response HTTP 응답 객체
     */
    private void renderResponseBody(Object body, HttpResponse response) {
        try {
            if (body instanceof String) {
                // 문자열인 경우 그대로 사용 (이미 JSON 문자열일 수 있음)
                response.setBody((String) body);
                System.out.println("문자열 본문 설정 완료: " + ((String) body).length() + " 문자");
            } else {
                // 객체인 경우 JSON으로 직렬화
                String jsonBody = JsonSerializer.toJson(body);
                response.setBody(jsonBody);
                System.out.println("객체 JSON 직렬화 완료: " + jsonBody.length() + " 문자");
            }
        } catch (Exception e) {
            // JSON 직렬화 실패 시 에러 응답 처리
            handleJsonSerializationError(response, e, body);
        }
    }

    /**
     * ResponseEntity가 모델에 없는 경우 에러 처리
     *
     * @param response HTTP 응답 객체
     */
    private void handleMissingResponseEntity(HttpResponse response) {
        response.setStatus(500);
        response.setContentType("application/json; charset=UTF-8");

        String errorJson = "{\"error\": \"MISSING_RESPONSE_ENTITY\", " +
                "\"message\": \"ResponseEntity not found in model\"}";
        response.setBody(errorJson);

        System.err.println("ResponseEntity 렌더링 오류: 모델에 ResponseEntity 없음");
    }

    /**
     * ResponseEntity 타입이 올바르지 않은 경우 에러 처리
     *
     * @param response HTTP 응답 객체
     * @param invalidObject 잘못된 타입의 객체
     */
    private void handleInvalidResponseEntity(HttpResponse response, Object invalidObject) {
        response.setStatus(500);
        response.setContentType("application/json; charset=UTF-8");

        String errorJson = String.format(
                "{\"error\": \"INVALID_RESPONSE_ENTITY_TYPE\", " +
                        "\"message\": \"Expected ResponseEntity but got %s\"}",
                invalidObject.getClass().getSimpleName()
        );
        response.setBody(errorJson);

        System.err.println("ResponseEntity 렌더링 오류: 잘못된 타입 - " +
                invalidObject.getClass().getName());
    }

    /**
     * ResponseEntity 렌더링 중 일반적인 오류 처리
     *
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     */
    private void handleRenderingError(HttpResponse response, Exception e) {
        response.setStatus(500);
        response.setContentType("application/json; charset=UTF-8");

        String errorJson = String.format(
                "{\"error\": \"RESPONSE_ENTITY_RENDERING_FAILED\", " +
                        "\"message\": \"%s\"}",
                e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error"
        );
        response.setBody(errorJson);

        System.err.println("ResponseEntity 렌더링 실패: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * JSON 직렬화 실패 시 에러 처리
     *
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     * @param originalBody 직렬화에 실패한 원본 객체
     */
    private void handleJsonSerializationError(HttpResponse response, Exception e, Object originalBody) {
        response.setStatus(500);
        response.setContentType("application/json; charset=UTF-8");

        String errorJson = String.format(
                "{\"error\": \"JSON_SERIALIZATION_FAILED\", " +
                        "\"message\": \"Failed to serialize %s: %s\"}",
                originalBody.getClass().getSimpleName(),
                e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error"
        );
        response.setBody(errorJson);

        System.err.println("JSON 직렬화 실패: " + originalBody.getClass().getName() +
                " - " + e.getMessage());
    }

    /**
     * 디버깅을 위한 ResponseEntity 정보 출력 유틸리티 메서드
     *
     * @param responseEntity 정보를 출력할 ResponseEntity
     */
    public static void debugResponseEntity(ResponseEntity<?> responseEntity) {
        System.out.println("=== ResponseEntity Debug Info ===");
        System.out.println("Status: " + responseEntity.getStatusCode());
        System.out.println("Headers: " + responseEntity.getHeaders());
        System.out.println("Has Body: " + responseEntity.hasBody());
        if (responseEntity.hasBody()) {
            System.out.println("Body Type: " + responseEntity.getBody().getClass().getName());
            System.out.println("Body Content: " + responseEntity.getBody());
        }
        System.out.println("================================");
    }
}