package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 세션 속성을 자동으로 바인딩하는 어노테이션
 *
 * 사용 예시:
 * <pre>
 * {@code
 * @RequestMapping("/user/profile")
 * public ModelAndView showProfile(
 *     @SessionAttribute("username") String username,
 *     @SessionAttribute(value = "userRole", required = false) String role
 * ) {
 *     // 세션에서 "username"과 "userRole" 속성을 자동으로 가져와서 파라미터에 바인딩
 *     // ...
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SessionAttribute {

    /**
     * 세션 속성 이름
     * value()와 동일한 역할을 합니다.
     * @return 세션 속성 이름
     */
    String value() default "";

    /**
     * 세션 속성 이름 (value와 동일)
     * value가 설정되지 않은 경우, 파라미터 이름을 사용합니다.
     * @return 세션 속성 이름
     */
    String name() default "";

    /**
     * 필수 속성 여부
     * true인 경우 세션에 해당 속성이 없으면 예외가 발생합니다.
     * false인 경우 세션에 해당 속성이 없으면 null을 반환합니다.
     * @return 필수 여부 (기본값: true)
     */
    boolean required() default true;
}
