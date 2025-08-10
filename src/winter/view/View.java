package winter.view;

import winter.http.HttpRequest; // HTTP 요청 객체 임포트 추가 (26챕터 수정)
import winter.http.HttpResponse;

import java.util.Map;

/*
 * View는 모델 데이터를 받아 출력하는 책임을 가진다.
 * 26챕터 수정: HttpRequest 파라미터 추가로 뷰 엔진이 요청 정보를 활용할 수 있도록 함
 */
public interface View {
    // 26챕터 수정: HttpRequest request 파라미터 추가
    // 기존: void render(Map<String,Object> model, HttpResponse response);
    // 변경: IntegratedView와 ViewEngine에서 요청 정보가 필요하기 때문에 HttpRequest 추가
    void render(Map<String,Object> model, HttpRequest request, HttpResponse response);
}