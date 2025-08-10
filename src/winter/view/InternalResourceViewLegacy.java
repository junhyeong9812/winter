package winter.view;

import winter.http.HttpRequest; // 26챕터 추가: HttpRequest 파라미터 지원을 위한 임포트
import winter.http.HttpResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 구 버전 JAVA환경을 고려한 View 렌더링 클래스
 * (BufferedReader 사용)
 *
 * 26단계 수정: render 메서드에 HttpRequest 파라미터 추가하되 기존 레거시 로직 보존
 */
public class InternalResourceViewLegacy implements View {
    private final String path; // 템플릿 파일 경로

    // 생성자: 템플릿 파일 경로 설정
    public InternalResourceViewLegacy(String path) {
        this.path = path;
    }

    // 26챕터 수정: HttpRequest request 파라미터 추가
    // 기존 시그니처: render(Map<String, Object> model, HttpResponse response)
    // 새 시그니처: render(Map<String, Object> model, HttpRequest request, HttpResponse response)
    // 레거시 방식의 파일 읽기와 템플릿 처리는 그대로 유지
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        StringBuilder content = new StringBuilder(); // 파일 내용을 저장할 StringBuilder

        try (
                // 레거시 방식: BufferedReader를 사용한 파일 읽기 (구 버전 Java 호환)
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))
        ) {
            String line; // 한 줄씩 읽기 위한 변수
            // 파일의 모든 줄을 읽어서 content에 추가
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator()); // 줄바꿈 문자 추가
            }

            // 모델 치환 (레거시 방식: 간단한 문자열 replace 사용)
            String rendered = content.toString(); // StringBuilder를 String으로 변환
            for (Map.Entry<String, Object> entry : model.entrySet()) { // 모든 모델 데이터 순회
                // ${key} 형태를 실제 값으로 치환
                rendered = rendered.replace("${" + entry.getKey() + "}", entry.getValue().toString());

                // 로깅: 치환 정보 출력 (레거시 방식 표시)
                System.out.println("  [Legacy] 치환: ${" + entry.getKey() + "} -> " + entry.getValue());
            }

            // 정상 응답 설정
            response.setStatus(200); // HTTP 200 OK
            response.setBody(rendered); // 렌더링된 내용을 응답 본문으로 설정

            // 로깅: 레거시 렌더링 완료 정보
            System.out.println("InternalResourceViewLegacy 렌더링 완료: " + path +
                    " (" + rendered.length() + " 문자, BufferedReader 사용)");

        } catch (IOException e) {
            // 파일 읽기 실패 시 에러 처리 (레거시 방식)
            response.setStatus(500); // HTTP 500 Internal Server Error
            response.setBody("View Rendering Failed (Legacy): " + path); // 에러 메시지

            // 추가 로깅: 레거시 에러 정보
            System.err.println("레거시 템플릿 파일 읽기 실패: " + path + " - " + e.getMessage());
        }
    }

    /**
     * 템플릿 경로를 반환하는 getter 메서드 (기존 메서드 보존)
     *
     * @return 템플릿 파일 경로
     */
    public String getPath() {
        return path;
    }
}