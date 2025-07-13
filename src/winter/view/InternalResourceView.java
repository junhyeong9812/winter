package winter.view;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
    public void render(Map<String, Object> model) {
        try {
            // 파일 내용 읽기
            String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

            // 모델 값으로 치환
            if (model != null) {
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    String placeholder = "${" + entry.getKey() + "}";
                    content = content.replace(placeholder, entry.getValue().toString());
                }
            }

            // 결과 출력
            System.out.println("Rendered View: \n" + content);
        } catch (IOException e) {
            System.out.println("❌ View Rendering Failed: " + e.getMessage());
        }
    }

}
