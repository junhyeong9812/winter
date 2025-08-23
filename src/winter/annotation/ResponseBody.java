package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 반환값을 HTTP 응답 본문으로 직접 사용함을 나타내는 어노테이션
 *
 * Spring의 @ResponseBody와 동일한 역할을 수행합니다.
 * 이 어노테이션이 붙은 메서드의 반환값은 ViewResolver를 거치지 않고
 * 직접 HTTP 응답 본문으로 사용되며, JSON 형태로 직렬화됩니다.
 *
 * 주요 기능:
 * - 메서드 반환값을 JSON으로 자동 직렬화
 * - ViewResolver와 View 객체를 우회하여 직접 응답 생성
 * - Content-Type을 application/json으로 자동 설정
 * - HTTP 상태 코드를 200 OK로 기본 설정
 *
 * 사용 예시:
 * @Controller
 * public class MixedController {
 *     // 일반 컨트롤러 메서드: HTML 템플릿 렌더링
 *     @RequestMapping("/users/page")
 *     public ModelAndView getUsersPage() {
 *         return new ModelAndView("users", model);
 *     }
 *
 *     // REST API 메서드: JSON 응답
 *     @RequestMapping("/api/users")
 *     @ResponseBody
 *     public List<User> getUsersApi() {
 *         // 반환값이 자동으로 JSON으로 변환되어 응답됨
 *         return userService.getAllUsers();
 *     }
 *
 *     // ResponseEntity 사용: 상태 코드와 헤더 제어
 *     @RequestMapping("/api/user/{id}")
 *     @ResponseBody
 *     public ResponseEntity<User> getUserApi(@RequestParam("id") String id) {
 *         User user = userService.getUser(id);
 *         if (user == null) {
 *             return ResponseEntity.notFound();
 *         }
 *         return ResponseEntity.ok(user);
 *     }
 * }
 *
 * @RestController와의 관계:
 * - @RestController = @Controller + 모든 메서드에 @ResponseBody 적용
 * - @ResponseBody는 개별 메서드에 선택적으로 적용
 * - 하나의 컨트롤러에서 HTML과 JSON 응답을 혼합 사용 가능
 *
 * 지원하는 반환 타입:
 * - 일반 객체: 자동으로 JSON 직렬화 (상태 코드 200)
 * - ResponseEntity: 상태 코드, 헤더, 본문 모두 제어 가능
 * - 컬렉션(List, Map 등): JSON 배열/객체로 직렬화
 * - 문자열: 단순 텍스트 또는 JSON 문자열로 응답
 *
 * 특징:
 * - 메서드 레벨에만 적용 가능 (ElementType.METHOD)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - Winter의 JsonView와 ResponseEntityView가 처리를 담당
 */
@Target(ElementType.METHOD)      // 메서드에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface ResponseBody {

    /**
     * 추가 설정이나 메타데이터 (선택사항)
     *
     * 현재는 사용되지 않지만, 향후 확장을 위해 예약된 속성입니다.
     * 예를 들어, 직렬화 방식이나 Content-Type 설정 등에 사용될 수 있습니다.
     *
     * @return 설정 값 (기본값: 빈 문자열)
     */
    String value() default "";
}