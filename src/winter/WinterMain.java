package winter;

import winter.controller.ProductController;
import winter.dispatcher.Dispatcher;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

public class WinterMain {
    public static void main(String[] args) {
        System.out.println("=== WinterFramework Start ===");

        Dispatcher dispatcher = new Dispatcher();

        // 22단계: 어노테이션 기반 컨트롤러는 CombinedHandlerMapping에서 자동 등록됨
        // ProductController는 이미 등록되어 있음

        // 기존 테스트들
        testExistingFeatures(dispatcher);

        // 21단계: JSON 응답 테스트
        testJsonResponse(dispatcher);

        // 22단계: 어노테이션 기반 MVC 테스트
        testAnnotationBasedMvc(dispatcher);

        // 23단계: 파라미터 바인딩 테스트
        testParameterBinding(dispatcher);

        System.out.println("\n=== WinterFramework Test Complete ===");
    }

    /**
     * 기존 기능들 테스트 (1-21단계)
     */
    private static void testExistingFeatures(Dispatcher dispatcher) {
        System.out.println("\n--- 기존 기능 테스트 ---");

        // /hello 요청
        HttpRequest helloRequest = new HttpRequest("/hello");
        helloRequest.addParameter("name", "winter");
        HttpResponse helloResponse = new HttpResponse();
        dispatcher.dispatch(helloRequest, helloResponse);

        // /api 요청 (21단계 JSON)
        HttpRequest apiRequest = new HttpRequest("/api", "GET");
        HttpResponse apiResponse = new HttpResponse();
        dispatcher.dispatch(apiRequest, apiResponse);
    }

    /**
     * 21단계: JSON 응답 기능 테스트
     */
    private static void testJsonResponse(Dispatcher dispatcher) {
        System.out.println("\n--- 21단계: JSON 응답 테스트 ---");

        // JSON 응답 테스트
        HttpRequest jsonRequest = new HttpRequest("/api", "GET");
        jsonRequest.addHeader("Accept", "application/json");
        HttpResponse jsonResponse = new HttpResponse();
        dispatcher.dispatch(jsonRequest, jsonResponse);
    }

    /**
     * 22단계: 어노테이션 기반 MVC 테스트
     */
    private static void testAnnotationBasedMvc(Dispatcher dispatcher) {
        System.out.println("\n--- 22단계: 어노테이션 기반 MVC 테스트 ---");

        // 테스트 1: GET /products (파라미터 없는 메서드)
        System.out.println("\n[테스트 1] GET /products - 파라미터 없는 어노테이션 메서드");
        HttpRequest productsRequest = new HttpRequest("/products", "GET");
        HttpResponse productsResponse = new HttpResponse();
        dispatcher.dispatch(productsRequest, productsResponse);

        // 테스트 2: GET /product/detail (HttpRequest 파라미터)
        System.out.println("\n[테스트 2] GET /product/detail - HttpRequest 파라미터 메서드");
        HttpRequest detailRequest = new HttpRequest("/product/detail?id=12345&name=Winter Laptop", "GET");
        HttpResponse detailResponse = new HttpResponse();
        dispatcher.dispatch(detailRequest, detailResponse);

        // 테스트 3: POST /products (HttpRequest + HttpResponse 파라미터)
        System.out.println("\n[테스트 3] POST /products - 상품 생성");
        HttpRequest createRequest = new HttpRequest("/products?name=Winter Phone&price=599000", "POST");
        HttpResponse createResponse = new HttpResponse();
        dispatcher.dispatch(createRequest, createResponse);

        // 테스트 4: POST /products (잘못된 데이터)
        System.out.println("\n[테스트 4] POST /products - 유효성 검증 실패");
        HttpRequest invalidRequest = new HttpRequest("/products", "POST"); // name 파라미터 없음
        HttpResponse invalidResponse = new HttpResponse();
        dispatcher.dispatch(invalidRequest, invalidResponse);

        // 테스트 5: 모든 HTTP 메서드 허용 테스트
        System.out.println("\n[테스트 5] PUT /product/info - 모든 메서드 허용");
        HttpRequest infoRequest = new HttpRequest("/product/info", "PUT");
        HttpResponse infoResponse = new HttpResponse();
        dispatcher.dispatch(infoRequest, infoResponse);

        // 테스트 6: 어노테이션 컨트롤러 JSON 응답
        System.out.println("\n[테스트 6] GET /products - JSON 응답");
        HttpRequest productsJsonRequest = new HttpRequest("/products", "GET");
        productsJsonRequest.addHeader("Accept", "application/json");
        HttpResponse productsJsonResponse = new HttpResponse();
        dispatcher.dispatch(productsJsonRequest, productsJsonResponse);

        // 테스트 7: 어노테이션과 레거시 핸들러 우선순위 테스트
        System.out.println("\n[테스트 7] 핸들러 우선순위 테스트 - 기존 /user (레거시)");
        HttpRequest legacyRequest = new HttpRequest("/user?name=Legacy&city=Seoul&zipcode=12345", "GET");
        HttpResponse legacyResponse = new HttpResponse();
        dispatcher.dispatch(legacyRequest, legacyResponse);

        // 테스트 8: 존재하지 않는 어노테이션 경로
        System.out.println("\n[테스트 8] GET /nonexistent - 404 테스트");
        HttpRequest notFoundRequest = new HttpRequest("/nonexistent", "GET");
        HttpResponse notFoundResponse = new HttpResponse();
        dispatcher.dispatch(notFoundRequest, notFoundResponse);
    }

    /**
     * 23단계: 파라미터 바인딩 기능 테스트
     */
    private static void testParameterBinding(Dispatcher dispatcher) {
        System.out.println("\n--- 23단계: 파라미터 바인딩 테스트 ---");

        // 테스트 1: @RequestParam 기본 사용 - 필수 파라미터
        System.out.println("\n[테스트 1] GET /search - @RequestParam 기본 바인딩");
        HttpRequest searchRequest = new HttpRequest("/search?keyword=spring&page=2", "GET");
        HttpResponse searchResponse = new HttpResponse();
        dispatcher.dispatch(searchRequest, searchResponse);

        // 테스트 2: @RequestParam 기본값 사용
        System.out.println("\n[테스트 2] GET /search - 기본값 사용 (page 생략)");
        HttpRequest searchDefaultRequest = new HttpRequest("/search?keyword=java", "GET");
        HttpResponse searchDefaultResponse = new HttpResponse();
        dispatcher.dispatch(searchDefaultRequest, searchDefaultResponse);

        // 테스트 3: @RequestParam 고급 검색 - 여러 파라미터 조합
        System.out.println("\n[테스트 3] GET /search/advanced - 복합 파라미터 바인딩");
        HttpRequest advancedRequest = new HttpRequest("/search/advanced?keyword=spring&category=tech&minPrice=1000&maxPrice=5000&sortBy=date", "GET");
        HttpResponse advancedResponse = new HttpResponse();
        dispatcher.dispatch(advancedRequest, advancedResponse);

        // 테스트 4: @RequestParam 선택적 파라미터 (일부 생략)
        System.out.println("\n[테스트 4] GET /search/advanced - 선택적 파라미터 생략");
        HttpRequest partialRequest = new HttpRequest("/search/advanced?keyword=java&category=programming", "GET");
        HttpResponse partialResponse = new HttpResponse();
        dispatcher.dispatch(partialRequest, partialResponse);

        // 테스트 5: @ModelAttribute 객체 바인딩
        System.out.println("\n[테스트 5] POST /search/form - @ModelAttribute 객체 바인딩");
        HttpRequest formRequest = new HttpRequest("/search/form?keyword=spring boot&category=framework&page=3&size=15&sortBy=relevance&sortOrder=desc", "POST");
        HttpResponse formResponse = new HttpResponse();
        dispatcher.dispatch(formRequest, formResponse);

        // 테스트 6: @ModelAttribute 유효성 검증 실패
        System.out.println("\n[테스트 6] POST /search/form - 유효성 검증 실패 (keyword 없음)");
        HttpRequest invalidFormRequest = new HttpRequest("/search/form?category=tech&page=1", "POST");
        HttpResponse invalidFormResponse = new HttpResponse();
        dispatcher.dispatch(invalidFormRequest, invalidFormResponse);

        // 테스트 7: 혼합 파라미터 (@RequestParam + @ModelAttribute)
        System.out.println("\n[테스트 7] POST /search/mixed - 혼합 파라미터 바인딩");
        HttpRequest mixedRequest = new HttpRequest("/search/mixed?userId=12345&debug=true&keyword=hibernate&category=database&page=2", "POST");
        HttpResponse mixedResponse = new HttpResponse();
        dispatcher.dispatch(mixedRequest, mixedResponse);

        // 테스트 8: 배열 파라미터 (@RequestParam String[])
        System.out.println("\n[테스트 8] GET /search/tags - 배열 파라미터 바인딩");
        HttpRequest tagsRequest = new HttpRequest("/search/tags?tags=java,spring,mvc,hibernate&includeAll=true", "GET");
        HttpResponse tagsResponse = new HttpResponse();
        dispatcher.dispatch(tagsRequest, tagsResponse);

        // 테스트 9: 타입 변환 테스트 (double, boolean)
        System.out.println("\n[테스트 9] GET /search/filter - 다양한 타입 변환");
        HttpRequest filterRequest = new HttpRequest("/search/filter?minRating=4.5&publishedAfter=2023-01-01&isAvailable=true", "GET");
        HttpResponse filterResponse = new HttpResponse();
        dispatcher.dispatch(filterRequest, filterResponse);

        // 테스트 10: 필수 파라미터 누락 에러 테스트
        System.out.println("\n[테스트 10] GET /search - 필수 파라미터 누락 (keyword 없음)");
        HttpRequest missingParamRequest = new HttpRequest("/search?page=1", "GET");
        HttpResponse missingParamResponse = new HttpResponse();
        dispatcher.dispatch(missingParamRequest, missingParamResponse);

        // 테스트 11: JSON 응답과 파라미터 바인딩 조합
        System.out.println("\n[테스트 11] GET /search - JSON 응답 + 파라미터 바인딩");
        HttpRequest jsonSearchRequest = new HttpRequest("/search?keyword=winter&page=5", "GET");
        jsonSearchRequest.addHeader("Accept", "application/json");
        HttpResponse jsonSearchResponse = new HttpResponse();
        dispatcher.dispatch(jsonSearchRequest, jsonSearchResponse);
    }
}