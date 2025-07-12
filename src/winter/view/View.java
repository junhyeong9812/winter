package winter.view;

import java.util.Map;

/*
* View는 모델 데이터를 받아 출력하는 책임을 가진다.*/
public interface View {
    void render(Map<String,Object> model);
}
