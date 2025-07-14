package winter.view;

import winter.http.HttpResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
* 구 버전 JAVA환경을 고려한 View 렌더링 클래스
* (BufferedReader 사용)*/
public class InternalResourceViewLegacy implements View{
    private final String path;

    public InternalResourceViewLegacy(String path){
        this.path = path;
    }

    @Override
    public void render(Map<String, Object> model, HttpResponse response) {
        StringBuilder content = new StringBuilder();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

            // 모델 치환
            String rendered = content.toString();
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                rendered = rendered.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }

            response.setStatus(200);
            response.setBody(rendered);
        } catch (IOException e) {
            response.setStatus(500);
            response.setBody("View Rendering Failed (Legacy): " + path);
        }
    }

}
