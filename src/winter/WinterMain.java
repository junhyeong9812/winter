package winter;

import winter.dispatcher.Dispatcher;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

public class WinterMain {
    public static void main(String[] args) {
        System.out.println("=== WinterFramework Start ===");

        Dispatcher dispatcher = new Dispatcher();

        // 기존 테스트들
        testExistingFeatures(dispatcher);

        // 21단계: JSON 응답 테스트
        testJsonResponse(dispatcher);

        System.out.println("\n=== WinterFramework Test Complete ===");
    }

    /**
     * 기존 기능들 테스트 (1-20단계)
     */
    private static void testExistingFeatures(Dispatcher dispatcher) {
        System.out.println("\n--- 기존 기능 테스트 ---");

        // /hello 요청
        HttpRequest helloRequest = new HttpRequest("/hello");
        helloRequest.addParameter("name", "winter");
        HttpResponse helloResponse = new HttpResponse();
        dispatcher.dispatch(helloRequest, helloResponse);

        // /bye 요청
        HttpRequest byeRequest = new HttpRequest("/bye");
        HttpResponse byeResponse = new HttpResponse();
        dispatcher.dispatch(byeRequest, byeResponse);

        // /register - GET 요청
        HttpRequest registerGet = new HttpRequest("/register", "GET");
        HttpResponse registerGetResponse = new HttpResponse();
        dispatcher.dispatch(registerGet, registerGetResponse);

        // /register - POST 요청
        HttpRequest registerPost = new HttpRequest("/register?name=Jun&email=jun@test.com", "POST");
        HttpResponse registerPostResponse = new HttpResponse();
        dispatcher.dispatch(registerPost, registerPostResponse);

        // /user 요청 - GET
        HttpRequest userRequest = new HttpRequest("/user?name=Jun&city=Seoul&zipcode=12345", "GET");
        HttpResponse userResponse = new HttpResponse();
        dispatcher.dispatch(userRequest, userResponse);

        // 정적파일 요청
        HttpRequest staticRequest = new HttpRequest("/static/style.css");
        HttpResponse staticResponse = new HttpResponse();
        dispatcher.dispatch(staticRequest, staticResponse);
    }

    /**
     * 21단계: JSON 응답 기능 테스트
     */
    private static void testJsonResponse(Dispatcher dispatcher) {
        System.out.println("\n--- 21단계: JSON 응답 테스트 ---");

        // 테스트 1: HTML 응답 (기본 Accept 헤더)
        System.out.println("\n[테스트 1] /api - HTML 응답");
        HttpRequest htmlRequest = new HttpRequest("/api", "GET");
        // Accept 헤더를 설정하지 않으면 기본적으로 HTML로 응답
        HttpResponse htmlResponse = new HttpResponse();
        dispatcher.dispatch(htmlRequest, htmlResponse);

        // 테스트 2: JSON 응답 (Accept: application/json)
        System.out.println("\n[테스트 2] /api - JSON 응답");
        HttpRequest jsonRequest = new HttpRequest("/api", "GET");
        jsonRequest.addHeader("Accept", "application/json"); // JSON 요청 헤더
        HttpResponse jsonResponse = new HttpResponse();
        dispatcher.dispatch(jsonRequest, jsonResponse);

        // 테스트 3: 복합 Accept 헤더 (JSON 우선)
        System.out.println("\n[테스트 3] /api - 복합 Accept 헤더 (JSON 포함)");
        HttpRequest mixedRequest = new HttpRequest("/api", "GET");
        mixedRequest.addHeader("Accept", "text/html, application/json;q=0.9, */*;q=0.8");
        HttpResponse mixedResponse = new HttpResponse();
        dispatcher.dispatch(mixedRequest, mixedResponse);

        // 테스트 4: 기존 컨트롤러에 JSON 요청
        System.out.println("\n[테스트 4] /user - JSON 응답 테스트");
        HttpRequest userJsonRequest = new HttpRequest("/user?name=Winter&city=Busan&zipcode=67890", "GET");
        userJsonRequest.addHeader("Accept", "application/json");
        HttpResponse userJsonResponse = new HttpResponse();
        dispatcher.dispatch(userJsonRequest, userJsonResponse);

        // 테스트 5: 에러 상황 테스트
        System.out.println("\n[테스트 5] 존재하지 않는 경로 - JSON 요청");
        HttpRequest invalidJsonRequest = new HttpRequest("/invalid", "GET");
        invalidJsonRequest.addHeader("Accept", "application/json");
        HttpResponse invalidJsonResponse = new HttpResponse();
        dispatcher.dispatch(invalidJsonRequest, invalidJsonResponse);
    }
}