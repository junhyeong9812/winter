package winter.controller;

import winter.annotation.Controller; // @Controller 어노테이션
import winter.annotation.RequestMapping; // @RequestMapping 어노테이션
import winter.http.HttpRequest; // HTTP 요청 객체
import winter.http.HttpResponse; // HTTP 응답 객체
import winter.model.User; // 사용자 모델
import winter.model.Address; // 주소 모델
import winter.view.ModelAndView; // 모델과 뷰를 담는 객체

import java.util.Arrays; // 배열 유틸리티
import java.util.HashMap; // 해시맵
import java.util.List; // 리스트
import java.util.Map; // 맵 인터페이스

/**
 * 뷰 엔진 테스트를 위한 컨트롤러
 * 26챕터: View Engine Integration 테스트
 */
@Controller // 이 클래스가 컨트롤러임을 표시하는 어노테이션
public class ViewEngineController {

    /**
     * 테스트 1: 기본 HTML 템플릿 (SimpleTemplateEngine)
     * URL: /view/simple
     */
    @RequestMapping("/view/simple") // 요청 매핑 어노테이션
    public ModelAndView viewSimple(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewSimple 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("title", "Simple Template Engine 테스트"); // 제목 추가
        model.put("message", "Winter Framework의 기본 템플릿 엔진입니다."); // 메시지 추가
        model.put("currentTime", java.time.LocalDateTime.now().toString()); // 현재 시간 추가

        // 간단한 User 객체 생성 - 기존 User 모델 그대로 사용
        User user = new User(); // 기본 생성자 사용
        user.setName("김겨울"); // 이름만 설정 (기존 User 모델에 있는 필드)

        // Address 객체 생성
        Address address = new Address(); // 주소 객체 생성
        address.setCity("서울시"); // 도시 설정
        address.setZipcode("12345"); // 우편번호 설정
        user.setAddress(address); // 사용자에 주소 설정

        model.put("user", user); // 모델에 사용자 추가

        // 추가 데이터는 별도로 설정 (User 모델 수정 없이)
        model.put("userAge", 25); // 나이를 별도 필드로
        model.put("userEmail", "winter@example.com"); // 이메일을 별도 필드로

        return new ModelAndView("view-simple", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 2: Thymeleaf 템플릿
     * URL: /view/thymeleaf
     */
    @RequestMapping("/view/thymeleaf") // 요청 매핑
    public ModelAndView viewThymeleaf(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewThymeleaf 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("title", "Thymeleaf Engine 테스트"); // 제목
        model.put("description", "Mock Thymeleaf 엔진으로 렌더링됩니다."); // 설명
        model.put("showDetails", true); // 상세 표시 여부
        model.put("isVip", false); // VIP 여부

        // 사용자 목록 생성 - 간단한 방식으로
        List<Map<String, Object>> users = Arrays.asList( // Map으로 사용자 데이터 생성
                createUserMap("김겨울", 25, "winter@example.com"), // 사용자 1
                createUserMap("이봄", 30, "spring@example.com"), // 사용자 2
                createUserMap("박여름", 28, "summer@example.com"), // 사용자 3
                createUserMap("최가을", 35, "autumn@example.com") // 사용자 4
        );
        model.put("users", users); // 모델에 사용자 리스트 추가
        model.put("totalUsers", users.size()); // 총 사용자 수 추가

        return new ModelAndView("view-thymeleaf", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 3: Mustache 템플릿
     * URL: /view/mustache
     */
    @RequestMapping("/view/mustache") // 요청 매핑
    public ModelAndView viewMustache(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewMustache 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("title", "Mustache Engine 테스트"); // 제목
        model.put("framework", "Winter Framework"); // 프레임워크 이름
        model.put("version", "1.0.0"); // 버전

        // 섹션 테스트용 데이터
        model.put("hasFeatures", true); // 기능 존재 여부
        model.put("isEmpty", false); // 빈 상태 여부

        // 기능 리스트
        List<Map<String, Object>> features = Arrays.asList( // 기능 리스트 생성
                Map.of("name", "MVC Pattern", "description", "Model-View-Controller 패턴 지원"), // 기능 1
                Map.of("name", "View Engines", "description", "다양한 뷰 엔진 통합"), // 기능 2
                Map.of("name", "Annotation Based", "description", "어노테이션 기반 설정"), // 기능 3
                Map.of("name", "Dependency Injection", "description", "의존성 주입 지원") // 기능 4
        );
        model.put("features", features); // 모델에 기능 리스트 추가

        // 빈 배열과 숫자 배열 테스트
        model.put("emptyList", Arrays.asList()); // 빈 리스트
        model.put("numbers", Arrays.asList(1, 2, 3, 4, 5)); // 숫자 리스트

        // 현재 사용자 정보 - Map으로 간단하게
        Map<String, Object> currentUser = createUserMap("관리자", 30, "admin@winter.com"); // 현재 사용자
        model.put("currentUser", currentUser); // 모델에 현재 사용자 추가

        return new ModelAndView("view-mustache", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 4: JSP 템플릿
     * URL: /view/jsp
     */
    @RequestMapping("/view/jsp") // 요청 매핑
    public ModelAndView viewJsp(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewJsp 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("pageTitle", "JSP Engine 테스트"); // 페이지 제목
        model.put("welcomeMessage", "Mock JSP 엔진에 오신 것을 환영합니다!"); // 환영 메시지

        // JSP 내장 객체 시뮬레이션
        model.put("serverInfo", "Winter Framework Mock JSP Engine"); // 서버 정보
        model.put("contextPath", "/winter"); // 컨텍스트 경로

        // 관리자 정보 - Map으로 간단하게
        Map<String, Object> admin = createUserMap("시스템 관리자", 40, "sysadmin@winter.com"); // 관리자
        model.put("admin", admin); // 모델에 관리자 추가

        // 수학 연산 테스트용 데이터
        model.put("itemCount", 15); // 아이템 개수
        model.put("pricePerItem", 1000); // 아이템당 가격
        model.put("totalPrice", 15 * 1000); // 총 가격 (계산된 값)

        return new ModelAndView("view-jsp", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 5: 다중 뷰 엔진 우선순위
     * URL: /view/priority
     */
    @RequestMapping("/view/priority") // 요청 매핑
    public ModelAndView viewPriority(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewPriority 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("testType", "우선순위 테스트"); // 테스트 유형
        model.put("description", "동일한 뷰명으로 여러 템플릿 파일이 있을 때 어떤 엔진이 선택되는지 확인"); // 설명

        return new ModelAndView("priority-test", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 6: 존재하지 않는 템플릿 처리
     * URL: /view/nonexistent
     */
    @RequestMapping("/view/nonexistent") // 요청 매핑
    public ModelAndView viewNonexistent(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewNonexistent 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("errorType", "템플릿 없음"); // 에러 유형

        return new ModelAndView("this-template-does-not-exist", model); // 존재하지 않는 뷰 반환
    }

    /**
     * 테스트 7: 뷰 엔진 성능 비교
     * URL: /view/performance
     */
    @RequestMapping("/view/performance") // 요청 매핑
    public ModelAndView viewPerformance(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewPerformance 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("title", "뷰 엔진 성능 테스트"); // 제목

        // 대량의 사용자 데이터 생성 (간단한 Map 방식)
        List<Map<String, Object>> largeUserList = generateLargeUserMapList(100); // 100명의 사용자 생성
        model.put("users", largeUserList); // 모델에 사용자 리스트 추가
        model.put("userCount", largeUserList.size()); // 사용자 수 추가

        return new ModelAndView("performance-test", model); // 뷰와 모델 반환
    }

    /**
     * 테스트 8: 뷰 엔진 정보 조회
     * URL: /view/info
     */
    @RequestMapping("/view/info") // 요청 매핑
    public ModelAndView viewEngineInfo(HttpRequest request, HttpResponse response) {
        System.out.println("[ViewEngineController] viewEngineInfo 호출"); // 로깅

        Map<String, Object> model = new HashMap<>(); // 모델 데이터 생성
        model.put("title", "뷰 엔진 정보"); // 제목
        model.put("description", "현재 등록된 뷰 엔진들의 정보를 표시합니다."); // 설명

        return new ModelAndView("engine-info", model); // 뷰와 모델 반환
    }

    // === 헬퍼 메서드들 ===

    /**
     * 사용자 정보를 Map으로 생성하는 헬퍼 메서드
     * User 모델을 수정하지 않고도 필요한 데이터를 템플릿에 전달
     *
     * @param name 사용자 이름
     * @param age 사용자 나이
     * @param email 사용자 이메일
     * @return 사용자 정보가 담긴 Map
     */
    private Map<String, Object> createUserMap(String name, int age, String email) {
        Map<String, Object> user = new HashMap<>(); // 새 Map 생성
        user.put("name", name); // 이름 추가
        user.put("age", age); // 나이 추가
        user.put("email", email); // 이메일 추가
        user.put("city", "서울시"); // 도시 추가 (간단하게)
        user.put("zipcode", "12345"); // 우편번호 추가 (간단하게)
        return user; // 완성된 Map 반환
    }

    /**
     * 대량의 사용자 데이터를 Map 리스트로 생성 (성능 테스트용)
     *
     * @param count 생성할 사용자 수
     * @return 사용자 Map 리스트
     */
    private List<Map<String, Object>> generateLargeUserMapList(int count) {
        return java.util.stream.IntStream.range(1, count + 1) // 1부터 count까지 스트림 생성
                .mapToObj(i -> createUserMap( // 각 숫자를 사용자 Map으로 변환
                        "사용자" + i, // 이름
                        20 + (i % 50), // 나이 (20~69 사이)
                        "user" + i + "@test.com" // 이메일
                ))
                .collect(java.util.stream.Collectors.toList()); // 리스트로 수집
    }

    /**
     * 기존 User 객체를 사용하는 헬퍼 메서드 (필요시에만 사용)
     * 기존 User 모델과의 호환성을 위해 유지
     *
     * @param name 사용자 이름
     * @return 생성된 User 객체 (name과 address만 설정)
     */
    private User createSimpleUser(String name) {
        User user = new User(); // 기본 생성자 사용
        user.setName(name); // 이름만 설정

        Address address = new Address(); // 주소 객체 생성
        address.setCity("서울시"); // 도시 설정
        address.setZipcode("12345"); // 우편번호 설정
        user.setAddress(address); // 사용자에 주소 설정

        return user; // 완성된 User 객체 반환
    }
}