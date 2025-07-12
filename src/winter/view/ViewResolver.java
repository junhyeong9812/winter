package winter.view;

/*
* Dispatcher가 반환한 논리 뷰 이름(String)을 실제 물리 경로로 변환하는
* 전략 인터페이스*/
public interface ViewResolver {
    String resolveViewName(String viewName);
}
