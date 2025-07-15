package winter.view;

import winter.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 전달받은 경로에 있는 HTML 파일을 읽고, 모델 데이터를 ${key} 형태로 치환하여 출력한다.
 */
public class InternalResourceView implements View{

    private final String path;

    //생성자
    public InternalResourceView(String path){
        this.path =path;
    }

    @Override
    public void render(Map<String, Object> model, HttpResponse response) {
        try {
            File file = new File(path);
            // 파일 내용 읽기
            String content = Files.readString(file.toPath());

            // 템플릿 파서 호출 (중첩 속성 지원)
            String rendered = parseTemplate(content, model);

            response.setStatus(200);
            response.setBody(rendered);
        } catch (IOException e) {
            response.setStatus(500);
            response.setBody("View Rendering Failed: " + path);
        }
    }

    // 템플릿 파싱 로직 추가
    private String parseTemplate(String content, Map<String, Object> model) {
        Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_.]+)}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String expression = matcher.group(1); // e.g., user.name
            String[] parts = expression.split("\\.");
            Object value = model.get(parts[0]);

            // 중첩된 속성 접근 (예: user.name)
            for (int i = 1; i < parts.length && value != null; i++) {
                value = getPropertyValue(value, parts[i]);
            }

            matcher.appendReplacement(sb, value != null ? Matcher.quoteReplacement(value.toString()) : "");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    // 리플렉션 기반 getter 호출
    private Object getPropertyValue(Object obj, String propertyName) {
        try {
            String getter = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method method = obj.getClass().getMethod(getter);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

}
