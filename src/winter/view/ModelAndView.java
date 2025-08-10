package winter.view;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller가 반환하는 결과 객체.
 * - 논리 뷰 이름(viewName)
 * - 모델 데이터(Map<String,Object>)를 포함
 *
 * 26챕터 수정: 모델 데이터를 받는 생성자 추가
 */
public class ModelAndView {

    private final String viewName; // 논리 뷰 이름 (예: "view-simple")
    private final Map<String, Object> model = new HashMap<>(); // 모델 데이터 저장

    /**
     * 기본 생성자: 뷰 이름만 지정 (기존 방식)
     * 모델 데이터는 나중에 addAttribute()로 추가
     *
     * 1-25챕터에서 사용하던 전통적인 방식 유지:
     * ModelAndView mv = new ModelAndView("view");
     * mv.addAttribute("key", value);
     *
     * @param viewName 논리 뷰 이름
     */
    public ModelAndView(String viewName) {
        this.viewName = viewName; // 뷰 이름 설정
        System.out.println("ModelAndView 생성 (기존 방식): 뷰=" + viewName); // 기존 방식 사용 로깅
    }

    /**
     * 26챕터 추가: 뷰 이름과 모델 데이터를 함께 받는 생성자
     * ViewEngineController에서 new ModelAndView("view-simple", model) 형태로 사용
     *
     * 기존 방식과 새로운 방식을 모두 지원:
     * - 기존: new ModelAndView("view").addAttribute("key", value)
     * - 새로운: new ModelAndView("view", model)
     *
     * @param viewName 논리 뷰 이름
     * @param model 템플릿에 전달할 모델 데이터
     */
    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName; // 뷰 이름 설정
        if (model != null) { // 모델이 null이 아닌 경우에만
            this.model.putAll(model); // 전달받은 모델 데이터를 내부 맵에 복사
            System.out.println("ModelAndView 생성 (새로운 방식): 뷰=" + viewName +
                    ", 모델 속성=" + model.size() + "개"); // 새 방식 사용 로깅
        }
    }

    /**
     * 뷰 이름 반환
     *
     * @return 논리 뷰 이름
     */
    public String getViewName() {
        return viewName; // 뷰 이름 반환
    }

    /**
     * 모델 데이터 맵 반환
     *
     * @return 모델 데이터가 담긴 Map
     */
    public Map<String, Object> getModel() {
        return model; // 모델 맵 반환
    }

    /**
     * 모델에 단일 속성 추가
     * 기존 1-25챕터 방식과의 호환성을 위해 유지
     *
     * 사용 예:
     * mv.addAttribute("title", "제목");
     * mv.addAttribute("user", userObject);
     *
     * @param key 속성 키
     * @param value 속성 값
     */
    public void addAttribute(String key, Object value) {
        model.put(key, value); // 모델 맵에 키-값 추가
        System.out.println("ModelAndView 속성 추가: " + key + " = " + value); // 속성 추가 로깅
    }

    /**
     * 모델에 여러 속성을 한 번에 추가
     * 26챕터 추가: 편의 메서드
     *
     * @param attributes 추가할 속성들이 담긴 Map
     */
    public void addAllAttributes(Map<String, Object> attributes) {
        if (attributes != null) { // null 체크
            this.model.putAll(attributes); // 모든 속성을 모델에 추가
        }
    }

    /**
     * 모델에서 특정 속성 값 조회
     * 26챕터 추가: 편의 메서드
     *
     * @param key 속성 키
     * @return 속성 값, 없으면 null
     */
    public Object getAttribute(String key) {
        return model.get(key); // 키에 해당하는 값 반환
    }

    /**
     * 모델에 특정 속성이 있는지 확인
     * 26챕터 추가: 편의 메서드
     *
     * @param key 속성 키
     * @return 속성이 존재하면 true
     */
    public boolean hasAttribute(String key) {
        return model.containsKey(key); // 키 존재 여부 반환
    }

    /**
     * 모델 크기 반환
     * 26챕터 추가: 디버깅용
     *
     * @return 모델에 저장된 속성 개수
     */
    public int getModelSize() {
        return model.size(); // 모델 맵 크기 반환
    }

    /**
     * 디버깅용 문자열 표현
     *
     * @return ModelAndView 정보를 담은 문자열
     */
    @Override
    public String toString() {
        return "ModelAndView{" +
                "viewName='" + viewName + '\'' + // 뷰 이름
                ", modelSize=" + model.size() + // 모델 크기
                ", model=" + model + // 모델 내용
                '}';
    }
}