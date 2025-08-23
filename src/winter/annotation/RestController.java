package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * REST API 컨트롤러를 표시하는 어노테이션
 *
 * Spring의 @RestController와 동일한 역할을 수행합니다.
 * 이 어노테이션이 붙은 클래스는 RESTful 웹 서비스의 컨트롤러로 인식되며,
 * 모든 메서드의 반환값이 자동으로 JSON 형태로 직렬화됩니다.
 *
 * @RestController는 @Controller + @ResponseBody의 조합과 동일합니다:
 * - 클래스가 웹 요청을 처리하는 컨트롤러임을 표시
 * - 모든 메서드의 반환값을 HTTP 응답 본문으로 직접 사용
 * - ViewResolver를 거치지 않고 JSON으로 직접 응답
 *
 * 사용 예시:
 * @RestController
 * public class ApiController {
 *     @RequestMapping("/api/users")
 *     public List<User> getUsers() {
 *         // 반환값이 자동으로 JSON으로 변환되어 응답됨
 *         return userList;
 *     }
 *
 *     @RequestMapping("/api/user/{id}")
 *     public ResponseEntity<User> getUser(@RequestParam("id") String id) {
 *         // ResponseEntity로 상태 코드와 헤더까지 제어 가능
 *         return ResponseEntity.ok(user);
 *     }
 * }
 *
 * 특징:
 * - 클래스 레벨에만 적용 가능 (ElementType.TYPE)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - 기존 @Controller와 구분되는 REST 전용 컨트롤러 표시
 * - JSON 응답에 최적화된 처리 로직 적용
 *
 * @Controller와의 차이점:
 * - @Controller: ModelAndView 반환 → HTML 템플릿 렌더링
 * - @RestController: 객체 직접 반환 → JSON 직렬화 응답
 */
@Target(ElementType.TYPE)        // 클래스에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface RestController {

    /**
     * 컨트롤러의 이름을 지정 (선택사항)
     *
     * 주로 디버깅이나 로깅 목적으로 사용됩니다.
     * 지정하지 않으면 클래스명이 기본값으로 사용됩니다.
     *
     * @return 컨트롤러 이름 (기본값: 빈 문자열)
     */
    String value() default "";
}