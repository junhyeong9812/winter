package winter.view;

import winter.http.HttpResponse;

import java.io.File;
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
    public void render(Map<String, Object> model, HttpResponse response) {
        try {
            File file = new File(path);
            // 파일 내용 읽기
            String content = Files.readString(file.toPath());

            //간단한 placeholder 처리 예시 :${message}-> 값으로 대체
            for(Map.Entry<String,Object> entry : model.entrySet()){
                content = content.replace("${"+entry.getKey()+"}",entry.getValue().toString());
            }
            response.setStatus(200);
            response.setBody(content);
        } catch (IOException e) {
            response.setStatus(500);
            response.setBody("View Rendering Failed: " + path);
        }
    }

}
