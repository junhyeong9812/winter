package winter.view;

import winter.http.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InternalResourceView implements View {
    private final String path;

    // 생성자에서 뷰 파일 경로 지정 (ex: src/winter/templates/register.html)
    public InternalResourceView(String path) {
        this.path = path;
    }

    // 모델 데이터를 HTML 템플릿에 바인딩하여 HttpResponse에 출력
    @Override
    public void render(Map<String, Object> model, HttpResponse response) {
        try {
            // HTML 파일 내용 전체 읽기
            String content = Files.readString(Paths.get(path));

            // ${user.name}, ${product.price} 등 중첩 표현식 처리
            content = resolvePlaceholders(content, model);

            // 정상 응답 설정
            response.setStatus(200);
            response.setBody(content);
        } catch (IOException e) {
            // 렌더링 실패 시 응답에 오류 메시지 포함
            response.setStatus(500);
            response.setBody("View Rendering Failed: " + path);
        }
    }

    // 템플릿 내의 ${user.name} 형태를 실제 값으로 치환하는 함수
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

            // 이후 단계는 객체 내부 속성 접근 (Reflection)
            for (int i = 1; i < parts.length && value != null; i++) {
                value = getProperty(value, parts[i]);  // 내부 객체의 getter 호출
            }

            // null일 경우 빈 문자열로 대체
            String replacement = value != null ? value.toString() : "";

            // 치환된 결과를 sb에 누적
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        // 마지막 남은 문자열도 누적 (appendReplacement 이후 필수 호출)
        matcher.appendTail(sb);
        return sb.toString();
    }

    // Reflection을 통해 객체의 속성 값을 추출 (getter 우선)
    private Object getProperty(Object obj, String fieldName) {
        try {
            // 우선적으로 "getXxx" 형태의 getter 메서드 탐색
            String getterName = "get" + capitalize(fieldName);
            Method method = obj.getClass().getMethod(getterName);
            return method.invoke(obj);  // getter 호출
        } catch (Exception e1) {
            try {
                // boolean 타입일 경우 "isXxx" 형태의 메서드도 시도
                String isMethod = "is" + capitalize(fieldName);
                Method method = obj.getClass().getMethod(isMethod);
                return method.invoke(obj);  // isXxx 호출
            } catch (Exception e2) {
                // getter, isXxx 모두 실패하면 null 반환
                return null;
            }
        }
    }

    // 첫 글자를 대문자로 바꿔주는 유틸리티 함수 ("name" → "Name")
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
