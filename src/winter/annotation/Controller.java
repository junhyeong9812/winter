package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 클래스를 표시하는 어노테이션
 *
 * Spring의 @Controller와 동일한 역할을 수행합니다.
 * 이 어노테이션이 붙은 클래스는 웹 요청을 처리하는 컨트롤러로 인식됩니다.
 *
 * 사용 예시:
 * @Controller
 * public class ProductController {
 *     @RequestMapping("/products")
 *     public ModelAndView getProducts() {
 *         // ...
 *     }
 * }
 *
 * 특징:
 * - 클래스 레벨에만 적용 가능 (ElementType.TYPE)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - Spring의 스테레오타입 어노테이션과 동일한 패턴
 */
@Target(ElementType.TYPE)        // 클래스에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)  // 런타임에 어노테이션 정보 유지
public @interface Controller {

    /**
     * 컨트롤러의 이름을 지정 (선택사항)
     *
     * @return 컨트롤러 이름 (기본값: 빈 문자열)
     */
    String value() default "";
}