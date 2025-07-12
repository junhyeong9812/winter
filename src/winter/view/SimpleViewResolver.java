package winter.view;

/*
* prefix+suffix 기반으로 뷰 이름을 실제 경로로 변환하는 기본 구현체*/
public class SimpleViewResolver implements ViewResolver{
    private final String prefix="/views/";
    private final String suffix=".html";

    @Override
    public String resolveViewName(String viewName){
        return prefix+viewName+suffix;
    }
}
