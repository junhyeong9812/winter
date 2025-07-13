package winter.view;

/*
* prefix+suffix 기반 물리 경로를 가진 View 객체를 반환하는
* Resolver
* */
public class SimpleViewResolver implements ViewResolver{
    private final String prefix="src/winter/templates/";
    private final String suffix=".html";

    @Override
    public View resolveViewName(String viewName){
        String fullPath = prefix + viewName +suffix;
        return new InternalResourceView(fullPath);
    }
}
