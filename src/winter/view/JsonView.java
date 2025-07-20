package winter.view;

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
 * */
public class JsonView implements View {

    /**
     * 모델 데이터를 Json형태로 렌더링하여 HTTP응답에 설정
     *
     * @param model 컨트롤러에서 전달받은 모델 데이터(Map<String,Object> 형태)
     * @param response HTTP 응답 객체
     * */
    @Override
    public void render(Map<String,Object> model, HttpResponse response){
        try{
            //1. 모델 데이터를 JSON 문자열로 변환
            String jsonContent = JsonSerializer.toJson(model);

            //2.HTTP 응답 설정
            //2-1. 정상응답
            response.setStatus(200);
            //2-2. JSON 컨텐츠 타입
            response.addHeader("Content-Type","application/json");
            //2-3 UTF-8 인코딩
            response.addHeader("chatset","UTF-8");
            //2-4 JSON 본문 설정
            response.setBody(jsonContent);

        }catch (Exception e){
            //JSON 직렬화 실패 시 에러 응답 구성
            handleJsonError(response, e);
        }
    }

    /**
     * JSON 변환 실패 시 에러 응답을 구성하는 메서드
     *
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     * */
    private void handleJsonError(HttpResponse response,Exception e){
        //500 Internal Server Error로 설정
        response.setStatus(500);
        response.addHeader("Content-Type","application/json");

        //에러 정보를 JSON 형태로 구성
        String errorJson =String.format(
          "{\"error\": \"JSON_SERIALIZATION_FAILED\",\"message\": \"%s\" }",
          e.getMessage() != null ? e.getMessage().replace("\"","\\\""): "Unkown error"
        );

        response.setBody(errorJson);
    }
}
