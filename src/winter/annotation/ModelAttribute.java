package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 파라미터에 HTTP 요청 파라미터들을 객체로 바인딩하는 어노테이션
 *
 * Spring의 @ModelAttribute와 동일한 역할을 수행합니다.
 * 여러 요청 파라미터를 하나의 객체로 매핑하여 메서드 파라미터로 전달합니다.
 *
 * 사용 예시:
 * public class UserForm {
 *     private String name;
 *     private int age;
 *     private String email;
 *     // getter/setter...
 * }
 *
 * @RequestMapping("/users")
 * public ModelAndView createUser(@ModelAttribute UserForm userForm) {
 *     // name=John&age=25&email=john@test.com → UserForm 객체로 자동 바인딩
 * }
 *
 * 특징:
 * - 파라미터 레벨에 적용 (ElementType.PARAMETER)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - 기존 ModelAttributeBinder 유틸리티를 재사용
 * - 객체의 setter 메서드를 통한 자동 바인딩
 */
// 파라미터에만 적용 가능
@Target(ElementType.PARAMETER)
// 런타임에 어노테이션 정보 유지
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelAttribute {

    /**
     * 모델 속성의 이름 (선택사항)
     *
     * 주로 뷰에서 참조할 때 사용되는 이름입니다.
     * 지정하지 않으면 클래스 이름의 첫 글자를 소문자로 한 이름을 사용합니다.
     *
     * @return 모델 속성 이름 (기본값: 빈 문자열)
     */
    String value() default "";
}
