package winter.controller;

import winter.annotation.Controller;
import winter.annotation.RequestMapping;
import winter.annotation.RequestParam;
import winter.http.HttpStatus;
import winter.http.ResponseEntity;
import winter.model.User;
import winter.model.Address;

import java.util.*;

/**
 * ResponseEntity를 활용한 REST API 컨트롤러 예제
 * HTTP 상태 코드와 헤더를 명시적으로 제어하는 방법을 보여줍니다.
 */
@Controller
public class ResponseEntityController {

    // 가상의 사용자 저장소
    private final Map<Long, User> userStore = new HashMap<>();
    private long userIdCounter = 1L;

    public ResponseEntityController() {
        // 초기 데이터 세팅
        User user1 = new User();
        user1.setName("john");
        user1.setAddress(new Address("123 Main St", "Seoul", "12345"));

        User user2 = new User();
        user2.setName("jane");
        user2.setAddress(new Address("456 Oak Ave", "Busan", "67890"));

        userStore.put(1L, user1);
        userStore.put(2L, user2);
        userIdCounter = 3L;
    }

    /**
     * 모든 사용자 조회
     * 200 OK 응답
     */
    @RequestMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>(userStore.values());

        if (users.isEmpty()) {
            return ResponseEntity.ok(users)
                    .withHeader("X-Total-Count", "0");
        }

        return ResponseEntity.ok(users)
                .withHeader("X-Total-Count", String.valueOf(users.size()))
                .withHeader("Cache-Control", "max-age=300");
    }

    /**
     * 특정 사용자 조회
     * 200 OK 또는 404 Not Found 응답
     */
    @RequestMapping("/api/users/{id}")
    public ResponseEntity<User> getUserById(@RequestParam("id") String id) {
        try {
            Long userId = Long.parseLong(id);
            User user = userStore.get(userId);

            if (user == null) {
                return ResponseEntity.notFound();
            }

            return ResponseEntity.ok(user)
                    .withHeader("Last-Modified", new Date().toString());

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest();
        }
    }

    /**
     * 새 사용자 생성
     * 201 Created 응답
     */
    @RequestMapping("/api/users/create")
    public ResponseEntity<User> createUser(@RequestParam("name") String name,
                                           @RequestParam("street") String street,
                                           @RequestParam("city") String city,
                                           @RequestParam("zipcode") String zipcode) {

        // 입력 검증
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.<User>badRequest(null)
                    .withHeader("X-Error-Code", "INVALID_NAME");
        }

        if (street == null || street.trim().isEmpty()) {
            return ResponseEntity.<User>badRequest(null)
                    .withHeader("X-Error-Code", "INVALID_STREET");
        }

        if (city == null || city.trim().isEmpty()) {
            return ResponseEntity.<User>badRequest(null)
                    .withHeader("X-Error-Code", "INVALID_CITY");
        }

        // 이름 중복 체크
        boolean nameExists = userStore.values().stream()
                .anyMatch(user -> user.getName().equals(name));

        if (nameExists) {
            return ResponseEntity.<User>conflict(null)
                    .withHeader("X-Error-Code", "NAME_ALREADY_EXISTS");
        }

        // 사용자 생성
        User newUser = new User();
        newUser.setName(name);
        newUser.setAddress(new Address(street, city, zipcode != null ? zipcode : "00000"));

        Long newUserId = userIdCounter++;
        userStore.put(newUserId, newUser);

        return ResponseEntity.created(newUser)
                .withHeader("Location", "/api/users/" + newUserId)
                .withHeader("X-Created-Id", String.valueOf(newUserId));
    }

    /**
     * 사용자 정보 수정
     * 200 OK, 404 Not Found, 또는 400 Bad Request 응답
     */
    @RequestMapping("/api/users/{id}/update")
    public ResponseEntity<User> updateUser(@RequestParam("id") String id,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "street", required = false) String street,
                                           @RequestParam(value = "city", required = false) String city,
                                           @RequestParam(value = "zipcode", required = false) String zipcode) {

        try {
            Long userId = Long.parseLong(id);
            User existingUser = userStore.get(userId);

            if (existingUser == null) {
                return ResponseEntity.<User>notFound()
                        .withHeader("X-Error-Code", "USER_NOT_FOUND");
            }

            // 입력 검증 및 업데이트
            if (name != null && !name.trim().isEmpty()) {
                // 다른 사용자가 이미 사용 중인 이름인지 확인
                boolean nameExistsForOther = userStore.entrySet().stream()
                        .anyMatch(entry -> !entry.getKey().equals(userId) &&
                                entry.getValue().getName().equals(name));

                if (nameExistsForOther) {
                    return ResponseEntity.<User>conflict(null)
                            .withHeader("X-Error-Code", "NAME_ALREADY_EXISTS");
                }

                existingUser.setName(name);
            }

            // 주소 업데이트
            Address currentAddress = existingUser.getAddress();
            if (currentAddress == null) {
                currentAddress = new Address("", "", "");
                existingUser.setAddress(currentAddress);
            }

            if (street != null && !street.trim().isEmpty()) {
                currentAddress.setStreet(street);
            }

            if (city != null && !city.trim().isEmpty()) {
                currentAddress.setCity(city);
            }

            if (zipcode != null && !zipcode.trim().isEmpty()) {
                currentAddress.setZipcode(zipcode);
            }

            return ResponseEntity.ok(existingUser)
                    .withHeader("Last-Modified", new Date().toString())
                    .withHeader("X-Updated-Id", String.valueOf(userId));

        } catch (NumberFormatException e) {
            return ResponseEntity.<User>badRequest(null)
                    .withHeader("X-Error-Code", "INVALID_ID_FORMAT");
        }
    }

    /**
     * 사용자 삭제
     * 204 No Content 또는 404 Not Found 응답
     */
    @RequestMapping("/api/users/{id}/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam("id") String id) {
        try {
            Long userId = Long.parseLong(id);
            User removedUser = userStore.remove(userId);

            if (removedUser == null) {
                return ResponseEntity.notFound()
                        .withHeader("X-Error-Code", "USER_NOT_FOUND");
            }

            return ResponseEntity.noContent()
                    .withHeader("X-Deleted-Id", String.valueOf(userId));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .withHeader("X-Error-Code", "INVALID_ID_FORMAT");
        }
    }

    /**
     * 사용자 검색
     * 검색 결과에 따라 다른 응답
     */
    @RequestMapping("/api/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.<List<User>>badRequest(null)
                    .withHeader("X-Error-Code", "QUERY_TOO_SHORT");
        }

        String lowerQuery = query.toLowerCase().trim();
        List<User> searchResults = userStore.values().stream()
                .filter(user -> {
                    boolean nameMatch = user.getName().toLowerCase().contains(lowerQuery);
                    boolean addressMatch = user.getAddress() != null &&
                            (user.getAddress().getCity().toLowerCase().contains(lowerQuery) ||
                                    user.getAddress().getStreet().toLowerCase().contains(lowerQuery));
                    return nameMatch || addressMatch;
                })
                .toList();

        if (searchResults.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>())
                    .withHeader("X-Search-Results", "0")
                    .withHeader("X-Search-Query", query);
        }

        return ResponseEntity.ok(searchResults)
                .withHeader("X-Search-Results", String.valueOf(searchResults.size()))
                .withHeader("X-Search-Query", query)
                .withHeader("Cache-Control", "max-age=60");
    }

    /**
     * 서버 상태 확인
     * 헬스 체크 엔드포인트
     */
    @RequestMapping("/api/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", new Date());
        health.put("userCount", userStore.size());
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health)
                .withHeader("Cache-Control", "no-cache")
                .withHeader("X-Health-Check", "OK");
    }

    /**
     * 사용자 통계
     * 사용자 도시별 분포 통계
     */
    @RequestMapping("/api/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Integer> cityStats = new HashMap<>();

        for (User user : userStore.values()) {
            if (user.getAddress() != null && user.getAddress().getCity() != null) {
                String city = user.getAddress().getCity();
                cityStats.put(city, cityStats.getOrDefault(city, 0) + 1);
            }
        }

        stats.put("totalUsers", userStore.size());
        stats.put("cityDistribution", cityStats);
        stats.put("timestamp", new Date());

        return ResponseEntity.ok(stats)
                .withHeader("X-Stats-Type", "USER_DISTRIBUTION")
                .withHeader("Cache-Control", "max-age=120");
    }

    /**
     * 오류 테스트 엔드포인트
     * 다양한 HTTP 상태 코드 테스트
     */
    @RequestMapping("/api/error-test")
    public ResponseEntity<Map<String, String>> errorTest(@RequestParam("type") String errorType) {
        Map<String, String> errorResponse = new HashMap<>();

        switch (errorType) {
            case "400":
                errorResponse.put("error", "Bad Request Test");
                return ResponseEntity.badRequest(errorResponse)
                        .withHeader("X-Error-Type", "CLIENT_ERROR");

            case "401":
                errorResponse.put("error", "Unauthorized Test");
                return ResponseEntity.withStatus(HttpStatus.UNAUTHORIZED, errorResponse)
                        .withHeader("X-Error-Type", "AUTH_ERROR");

            case "403":
                errorResponse.put("error", "Forbidden Test");
                return ResponseEntity.withStatus(HttpStatus.FORBIDDEN, errorResponse)
                        .withHeader("X-Error-Type", "PERMISSION_ERROR");

            case "500":
                errorResponse.put("error", "Internal Server Error Test");
                return ResponseEntity.withStatus(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse)
                        .withHeader("X-Error-Type", "SERVER_ERROR");

            default:
                errorResponse.put("error", "Unknown error type");
                return ResponseEntity.badRequest(errorResponse)
                        .withHeader("X-Error-Type", "INVALID_PARAMETER");
        }
    }
}

