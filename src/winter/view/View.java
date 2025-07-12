package winter.view;

/*
* 실제 렌더링 책임을 가지는 view 엔터페이스*/
public interface View {
    void render();
    /*현재는 render에 인자가 없지만
    * 추후 모델 정보나 요청/응답 객체 전달이 가능하도록 확장 예정
    * */
}
