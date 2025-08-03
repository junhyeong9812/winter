package winter;

import winter.controller.ProductController;
import winter.dispatcher.Dispatcher;
import winter.http.HttpRequest;
import winter.http.HttpResponse;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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

        // 24단계: 파일 업로드 테스트
        testFileUpload(dispatcher);

        System.out.println("\n=== WinterFramework Test Complete ===");
    }

    /**
     * Multipart 요청을 생성하는 헬퍼 메서드
     */
    private static HttpRequest createMultipartRequest(String path, String boundary, String multipartBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        headers.put("Content-Length", String.valueOf(multipartBody.length()));

        BufferedReader bodyReader = new BufferedReader(new StringReader(multipartBody));

        return new HttpRequest(path, "POST", headers, new HashMap<>(), bodyReader);
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

    /**
     * 24단계: 파일 업로드 기능 테스트
     */
    private static void testFileUpload(Dispatcher dispatcher) {
        System.out.println("\n--- 24단계: 파일 업로드 테스트 ---");

        // 테스트 1: 파일 업로드 폼 페이지 요청
        System.out.println("\n[테스트 1] GET /upload/form - 파일 업로드 폼 페이지");
        HttpRequest formRequest = new HttpRequest("/upload/form", "GET");
        HttpResponse formResponse = new HttpResponse();
        dispatcher.dispatch(formRequest, formResponse);

        // 테스트 2: 단일 파일 업로드 - 실제 multipart 바이너리 시뮬레이션
        System.out.println("\n[테스트 2] POST /upload - 실제 multipart 데이터 시뮬레이션");
        try {
            String boundary = "----WinterFrameworkBoundary1234567890";

            StringBuilder multipartBody = new StringBuilder();

            // description 파라미터
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"description\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("테스트 PDF 파일 업로드").append("\r\n");

            // file 파라미터 (PDF 파일 시뮬레이션)
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"test-document.pdf\"\r\n");
            multipartBody.append("Content-Type: application/pdf\r\n");
            multipartBody.append("\r\n");
            // PDF 파일의 실제 시작 바이트들 시뮬레이션
            multipartBody.append("%PDF-1.4\n%âãÏÓ\n1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n");
            multipartBody.append("이것은 테스트용 PDF 파일 내용입니다.\n");
            multipartBody.append("Winter Framework 파일 업로드 테스트 - 단일 파일\n");
            multipartBody.append("\r\n");

            multipartBody.append("--").append(boundary).append("--\r\n");

            // ✅ 수정된 부분: createMultipartRequest 헬퍼 메서드 사용
            HttpRequest uploadRequest = createMultipartRequest("/upload", boundary, multipartBody.toString());

            System.out.println("단일 파일 Multipart 요청 생성:");
            System.out.println("- Content-Type: multipart/form-data; boundary=" + boundary);
            System.out.println("- 파일명: test-document.pdf");
            System.out.println("- 파일 크기: " + multipartBody.length() + " bytes");
            System.out.println("- MIME 타입: application/pdf");

            HttpResponse uploadResponse = new HttpResponse();
            dispatcher.dispatch(uploadRequest, uploadResponse);

        } catch (Exception e) {
            System.out.println("단일 파일 업로드 시뮬레이션 오류: " + e.getMessage());
        }

        // 테스트 3: 다중 파일 업로드 - 실제 multipart 바이너리 시뮬레이션
        System.out.println("\n[테스트 3] POST /upload/multiple - 다중 파일 multipart 시뮬레이션");
        try {
            String boundary = "----WinterMultipleBoundary9876543210";

            StringBuilder multipartBody = new StringBuilder();

            // category 파라미터
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"category\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("documents").append("\r\n");

            // 첫 번째 파일 (PDF)
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"files\"; filename=\"doc1.pdf\"\r\n");
            multipartBody.append("Content-Type: application/pdf\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("%PDF-1.4\n%âãÏÓ\n첫 번째 PDF 문서 내용\nWinter Framework 테스트\n").append("\r\n");

            // 두 번째 파일 (이미지)
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"files\"; filename=\"image1.jpg\"\r\n");
            multipartBody.append("Content-Type: image/jpeg\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("ÿØÿàJFIFHHÿÛC테스트 JPEG 이미지 바이너리 데이터 시뮬레이션").append("\r\n");

            // 세 번째 파일 (텍스트)
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"files\"; filename=\"readme.txt\"\r\n");
            multipartBody.append("Content-Type: text/plain\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("=== Winter Framework 파일 업로드 테스트 ===\n");
            multipartBody.append("이 파일은 다중 파일 업로드 테스트용입니다.\n");
            multipartBody.append("업로드 시간: ").append(System.currentTimeMillis()).append("\n");
            multipartBody.append("\r\n");

            multipartBody.append("--").append(boundary).append("--\r\n");

            // ✅ 수정된 부분: createMultipartRequest 헬퍼 메서드 사용
            HttpRequest multipleRequest = createMultipartRequest("/upload/multiple", boundary, multipartBody.toString());

            System.out.println("다중 파일 Multipart 요청 생성:");
            System.out.println("- 파일 개수: 3개 (PDF, JPEG, TXT)");
            System.out.println("- 총 바이너리 크기: " + multipartBody.length() + " bytes");
            System.out.println("- 카테고리: documents");

            HttpResponse multipleResponse = new HttpResponse();
            dispatcher.dispatch(multipleRequest, multipleResponse);

        } catch (Exception e) {
            System.out.println("다중 파일 업로드 시뮬레이션 오류: " + e.getMessage());
        }

        // 테스트 4: 프로필 + 아바타 업로드 - 실제 multipart 바이너리 시뮬레이션
        System.out.println("\n[테스트 4] POST /upload/profile - 프로필 + 아바타 multipart 시뮬레이션");
        try {
            String boundary = "----WinterProfileBoundary5555666677";

            StringBuilder multipartBody = new StringBuilder();

            // 프로필 필드들
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"name\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("이경민").append("\r\n");

            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"email\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("user@winter-framework.com").append("\r\n");

            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"phone\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("010-1234-5678").append("\r\n");

            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"bio\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("Winter Framework 개발자입니다. 파일 업로드 기능을 테스트하고 있습니다.").append("\r\n");

            // 아바타 이미지 파일
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"avatar\"; filename=\"profile-avatar.jpg\"\r\n");
            multipartBody.append("Content-Type: image/jpeg\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("ÿØÿàJFIFHHÿÛC프로필 아바타 JPEG 이미지 바이너리 데이터");
            multipartBody.append("실제로는 여기에 이미지의 바이너리 데이터가 들어갑니다.");
            multipartBody.append("ÿÙ"); // JPEG 종료 마커
            multipartBody.append("\r\n");

            multipartBody.append("--").append(boundary).append("--\r\n");

            // ✅ 수정된 부분: createMultipartRequest 헬퍼 메서드 사용
            HttpRequest profileRequest = createMultipartRequest("/upload/profile", boundary, multipartBody.toString());

            System.out.println("프로필 Multipart 요청 생성:");
            System.out.println("- 사용자명: 이경민");
            System.out.println("- 이메일: user@winter-framework.com");
            System.out.println("- 아바타: profile-avatar.jpg (image/jpeg)");
            System.out.println("- 총 바이너리 크기: " + multipartBody.length() + " bytes");

            HttpResponse profileResponse = new HttpResponse();
            dispatcher.dispatch(profileRequest, profileResponse);

        } catch (Exception e) {
            System.out.println("프로필 업로드 시뮬레이션 오류: " + e.getMessage());
        }

        // 테스트 5: 파일 정보 조회 (AJAX) - JSON 응답
        System.out.println("\n[테스트 5] POST /upload/info - 파일 정보 조회 (JSON)");
        try {
            String boundary = "----WinterInfoBoundary1111222233";

            StringBuilder multipartBody = new StringBuilder();

            // 정보 조회할 파일
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"analysis-data.png\"\r\n");
            multipartBody.append("Content-Type: image/png\r\n");
            multipartBody.append("\r\n");
            multipartBody.append("‰PNGIHDR테스트 PNG 이미지 바이너리 데이터IEND®B`‚");
            multipartBody.append("\r\n");

            multipartBody.append("--").append(boundary).append("--\r\n");

            // ✅ 수정된 부분: createMultipartRequest 헬퍼 메서드 사용
            HttpRequest infoRequest = createMultipartRequest("/upload/info", boundary, multipartBody.toString());
            infoRequest.addHeader("Accept", "application/json"); // JSON 응답 요청

            System.out.println("파일 정보 조회 Multipart 요청:");
            System.out.println("- 파일명: analysis-data.png");
            System.out.println("- MIME 타입: image/png");
            System.out.println("- 응답 형식: JSON");

            HttpResponse infoResponse = new HttpResponse();
            dispatcher.dispatch(infoRequest, infoResponse);

        } catch (Exception e) {
            System.out.println("파일 정보 조회 시뮬레이션 오류: " + e.getMessage());
        }

        // 오류 테스트들
        System.out.println("\n[테스트 6-10] 오류 시나리오 테스트들...");

        // 테스트 6: 파일 없이 업로드
        testErrorScenario(dispatcher, "파일 없음", "----EmptyBoundary123",
                "description", "파일 없이 업로드 테스트", null, null, null);

        // 테스트 7: 허용되지 않은 파일 형식 (.exe)
        testErrorScenario(dispatcher, "허용되지 않은 확장자", "----ExeBoundary456",
                "description", "실행파일 업로드", "malware.exe", "application/x-msdownload",
                "MZ실행파일헤더시뮬레이션");

        // 테스트 8: 파일 크기 초과 (50MB > 10MB 제한)
        testErrorScenario(dispatcher, "파일 크기 초과", "----LargeBoundary789",
                "description", "대용량 파일", "huge-video.mp4", "video/mp4",
                "ftypmp41대용량비디오파일시뮬레이션".repeat(1000)); // 크기 시뮬레이션

        System.out.println("\n[완료] 24단계 파일 업로드 테스트 완료!");
        System.out.println("실제 브라우저 테스트: http://localhost:8080/upload/form");
    }

    /**
     * 오류 시나리오 테스트 헬퍼 메서드
     */
    private static void testErrorScenario(Dispatcher dispatcher, String testName, String boundary,
                                          String descField, String descValue,
                                          String filename, String contentType, String fileContent) {
        try {
            StringBuilder multipartBody = new StringBuilder();

            // description 파라미터
            multipartBody.append("--").append(boundary).append("\r\n");
            multipartBody.append("Content-Disposition: form-data; name=\"").append(descField).append("\"\r\n");
            multipartBody.append("\r\n");
            multipartBody.append(descValue).append("\r\n");

            // 파일이 있는 경우에만 추가
            if (filename != null && contentType != null && fileContent != null) {
                multipartBody.append("--").append(boundary).append("\r\n");
                multipartBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");
                multipartBody.append("Content-Type: ").append(contentType).append("\r\n");
                multipartBody.append("\r\n");
                multipartBody.append(fileContent).append("\r\n");
            }

            multipartBody.append("--").append(boundary).append("--\r\n");

            // ✅ 수정된 부분: createMultipartRequest 헬퍼 메서드 사용
            HttpRequest errorRequest = createMultipartRequest("/upload", boundary, multipartBody.toString());

            System.out.println("오류 테스트 [" + testName + "] - " +
                    (filename != null ? filename : "파일 없음"));

            HttpResponse errorResponse = new HttpResponse();
            dispatcher.dispatch(errorRequest, errorResponse);

        } catch (Exception e) {
            System.out.println("오류 테스트 [" + testName + "] 실행 중 예외: " + e.getMessage());
        }
    }
}