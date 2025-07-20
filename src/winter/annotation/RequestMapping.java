package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 요청 매핑을 정의하는 어노테이션
 *
 * Spring의 @RequestMapping과 동일한 역할을 수행합니다.
 * 메서드 레벨에서 URL 경로와 HTTP 메서드를 매핑합니다.
 *
 * 사용 예시:
 * @RequestMapping(value = "/products", method = "GET")
 * public ModelAndView getProducts() { ... }
 *
 * @RequestMapping("/products")  // method 생략 시 모든 HTTP 메서드 허용
 * public ModelAndView handleProducts() { ... }
 *
 * 특징:
 * - 메서드 레벨에 적용 (ElementType.METHOD)
 * - URL 경로와 HTTP 메서드를 동시에 지정
 * - method 생략 시 모든 HTTP 메서드를 허용
 */
@Target(ElementType.METHOD)      // 메서드에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface RequestMapping {

    /**
     * 매핑할 URL 경로
     *
     * @return URL 경로 (예: "/products", "/api/users")
     */
    String value();

    /**
     * 허용할 HTTP 메서드
     *
     * @return HTTP 메서드 문자열 (예: "GET", "POST", "PUT", "DELETE")
     *         기본값: 빈 문자열 (모든 메서드 허용)
     */
    String method() default "";
}