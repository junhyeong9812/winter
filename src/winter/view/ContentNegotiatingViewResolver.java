package winter.view;

import winter.http.HttpRequest;

/**
 * HTTP Accept 헤더를 기반으로 적절한 View를 선택하는 ViewResolver
 *
 * Content Negotiation (컨텐츠 협상):
 * - Accept : application/json -> JsonView 반환
 * - Accept : text/html 또는 기타 -> HTML 템플릿 View 반환
 *
 * 이는 같은 컨트롤러 메서드가 요청 헤더에 따라
 * HTML 페이지 또는 JSON API 응답을 선택적으로 제공할 수 있게 합니다.
 * */
public class ContentNegotiatingViewResolver implements ViewResolver {
    //HTML 처리를  위한 기존 ViewResolver
    private final SimpleViewResolver htmlViewResolver = new SimpleViewResolver();

    // 현재 요청 정보 (Accept 헤더 확인용)
    private HttpRequest currentRequest;

    /**
     * 요청 정보를 설정하는 메서드
     * Dispatcher에서 ViewResolver 호출 전에 설정해 한다.
     * @param request 현재 HTTP 요청 객체
     * */
    public void setCurrentRequest(HttpRequest request){
        this.currentRequest =request;
    }
    
    /**
     * Accept 헤더를 기반으로 적저랗ㄴ View 반환
     * @param viewName 논리적 뷰 이름 (예:"user","product")
     * @return Accept 헤더에 맞는 View 객체
     * */
    @Override
    public View resolveViewName(String viewName){
        //1. 현재 요청의 Accept 헤더 확인
        String acceptHeader = getAcceptHeader();

        //2. Accept 헤더에 따른 View 선택
        if(isJsonReqeust(acceptHeader)){
            //JSON 요청 : JsonView 반환
            return new JsonView();
        }else {
            //HTML 요청 : 기존 HTML 템플릿 View 반환
            return htmlViewResolver.resolveViewName(viewName);
        }
    }

    /**
     * 핸재 요청의 Accept 헤더 값을 가져오는 메서드
     *
     * @return Accept 헤더 갑슬 가져오는 메서드
     * */
    private String getAcceptHeader(){
        if(currentRequest ==null){
            return "*/*"; //기본값
        }
        return currentRequest.getHeader("Accept");
    }

    /**
     * Accept 헤더가 JSON요청인 지 판단
     *
     * @param acceptHeader Accept 헤더 값
     * @return JSON 요청 여부
     * */
    private boolean isJsonReqeust(String acceptHeader){
        if(acceptHeader ==null){
            return false;
        }

        //Accept 헤더에 application/json이 포함되는 지 확인
        //예 : "application/json","application/json,text/html", "text/html, application/json;q=0.8"
        return acceptHeader.toLowerCase().contains("application/json");
    }

}
