package winter.view;

/*
*
* 논리 뷰 이름을 받아 View객체를 반환하는 전략 인터페이스
*
* */
public interface ViewResolver {
    View resolveViewName(String viewName);
}
