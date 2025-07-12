package winter.view;

import java.util.HashMap;
import java.util.Map;

/*
* Controller가 반환하는 결과 객체.
* - 논리 뷰 이름(viewName)
* - 모델 데이터(Map<String,Object>)를 포함*/
public class ModelAndView {

    private final String viewName;
    private final Map<String, Object> model = new HashMap<>();

    public ModelAndView(String viewName){
        this.viewName =viewName;
    }

     public String getViewName(){
        return viewName;
     }

     public Map<String,Object> getModel(){
        return model;
     }

     public void addAttribute(String key,Object value){
        model.put(key,value);
     }
}
