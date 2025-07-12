package winter.view;

import java.util.Map;

public class InternalResourceView implements View{

    private final String path;

    //생성자
    public InternalResourceView(String path){
        this.path =path;
    }

    @Override
    public void render(Map<String,Object> model){
        System.out.println("View Rendering:"+ path);
        if(model != null && !model.isEmpty()){
            System.out.println("Model Data:");
            model.forEach((k,v)-> System.out.println(" " + k +"="+ v));

        }
    }
    /*현재는 단순 로그만 출력하지만 이후 jsp포워딩,템플릿 처리등으로 확장
    * */

}
