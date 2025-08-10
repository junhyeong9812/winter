package winter.view;

import winter.http.HttpRequest; // 26챕터 추가: HttpRequest 파라미터 지원을 위한 임포트
import winter.http.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Method; // 기존 핵심 기능: Reflection을 통한 getter 호출
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML 템플릿 파일을 읽어서 모델 데이터를 치환하여 렌더링하는 뷰
 * 11-13단계에서 구현된 템플릿 엔진 기능
 * 26단계 수정: render 메서드에 HttpRequest 파라미터 추가하되 기존 핵심 로직 보존
 */
public class InternalResourceView implements View {
    private final String path; // 템플릿 파일의 경로를 저장하는 필드

    // 생성자에서 뷰 파일 경로 지정 (ex: src/winter/templates/register.html)
    public InternalResourceView(String path) {
        this.path = path;
    }

    // 26챕터 수정: HttpRequest request 파라미터 추가
    // 기존 시그니처: render(Map<String, Object> model, HttpResponse response)
    // 새 시그니처: render(Map<String, Object> model, HttpRequest request, HttpResponse response)
    // 뷰 엔진에서 요청 정보를 활용할 수 있도록 HttpRequest 추가하되 기존 로직은 그대로 유지
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        try {
            // HTML 파일 내용 전체 읽기 (기존 로직 유지)
            String content = Files.readString(Paths.get(path));

            // ${user.name}, ${product.price} 등 중첩 표현식 처리 (기존 핵심 메서드 활용)
            content = resolvePlaceholders(content, model);

            // 정상 응답 설정 (기존 로직 유지)
            response.setStatus(200);
            response.setBody(content);

            // 로깅: 렌더링 완료 정보 출력
            System.out.println("InternalResourceView 렌더링 완료: " + path +
                    " (" + content.length() + " 문자)");

        } catch (IOException e) {
            // 렌더링 실패 시 응답에 오류 메시지 포함 (기존 에러 처리 로직 유지)
            response.setStatus(500);
            response.setBody("View Rendering Failed: " + path);

            // 추가 로깅: 에러 상세 정보 출력
            System.err.println("템플릿 파일 읽기 실패: " + path + " - " + e.getMessage());
        }
    }

    /**
     * 템플릿 내의 ${user.name} 형태를 실제 값으로 치환하는 함수 (기존 핵심 메서드 보존)
     *
     * 이 메서드는 Winter Framework의 핵심 템플릿 처리 로직이므로 반드시 유지해야 함
     * - 정규식을 통한 플레이스홀더 탐지
     * - 점 표기법(user.name) 지원
     * - Reflection을 통한 중첩 객체 접근
     */
    private String resolvePlaceholders(String content, Map<String, Object> model) {
        // 정규식 패턴: ${xxx.yyy.zzz} 형태의 문자열을 찾음
        Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_.]+)}");
        Matcher matcher = pattern.matcher(content);

        // 결과 문자열을 누적하기 위한 StringBuffer
        StringBuffer sb = new StringBuffer();

        // 모든 ${...} 표현식을 순회하면서 치환
        while (matcher.find()) {
            String expression = matcher.group(1);  // ex) user.name
            String[] parts = expression.split("\\.");  // "user.name" → ["user", "name"]

            // 첫 번째 키는 model의 key로 조회
            Object value = model.get(parts[0]);

            // 이후 단계는 객체 내부 속성 접근 (Reflection) - 기존 핵심 로직
            for (int i = 1; i < parts.length && value != null; i++) {
                value = getProperty(value, parts[i]);  // 내부 객체의 getter 호출
            }

            // null일 경우 빈 문자열로 대체
            String replacement = value != null ? value.toString() : "";

            // 치환된 결과를 sb에 누적
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));

            // 로깅: 각 치환 정보 출력 (디버깅용)
            System.out.println("  플레이스홀더 치환: ${" + expression + "} -> " +
                    (value != null ? "'" + value.toString() + "'" : "null"));
        }

        // 마지막 남은 문자열도 누적 (appendReplacement 이후 필수 호출)
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Reflection을 통해 객체의 속성 값을 추출 (getter 우선) - 기존 핵심 메서드 보존
     *
     * 이 메서드는 Winter Framework의 중요한 기능으로 점 표기법(user.name) 지원의 핵심
     * - getter 메서드 우선 호출 (getXxx)
     * - boolean 타입의 경우 isXxx 메서드 지원
     * - 메서드가 없으면 null 반환
     */
    private Object getProperty(Object obj, String fieldName) {
        try {
            // 우선적으로 "getXxx" 형태의 getter 메서드 탐색
            String getterName = "get" + capitalize(fieldName);
            Method method = obj.getClass().getMethod(getterName);
            Object result = method.invoke(obj);  // getter 호출

            // 로깅: getter 호출 성공 정보
            System.out.println("    getter 호출 성공: " + obj.getClass().getSimpleName() +
                    "." + getterName + "() -> " + result);
            return result;

        } catch (Exception e1) {
            try {
                // boolean 타입일 경우 "isXxx" 형태의 메서드도 시도
                String isMethod = "is" + capitalize(fieldName);
                Method method = obj.getClass().getMethod(isMethod);
                Object result = method.invoke(obj);  // isXxx 호출

                // 로깅: isXxx 호출 성공 정보
                System.out.println("    boolean getter 호출 성공: " + obj.getClass().getSimpleName() +
                        "." + isMethod + "() -> " + result);
                return result;

            } catch (Exception e2) {
                // getter, isXxx 모두 실패하면 null 반환
                System.err.println("    getter 호출 실패: " + obj.getClass().getSimpleName() +
                        "." + fieldName + " (getXxx/isXxx 메서드 없음)");
                return null;
            }
        }
    }

    /**
     * 첫 글자를 대문자로 바꿔주는 유틸리티 함수 ("name" → "Name") - 기존 유틸리티 메서드 보존
     *
     * 이 메서드는 getter 메서드명 생성에 필수적인 유틸리티 함수
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s; // null이나 빈 문자열 처리
        return Character.toUpperCase(s.charAt(0)) + s.substring(1); // 첫 글자 대문자화
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