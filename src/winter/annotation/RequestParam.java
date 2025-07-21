package winter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 파라미터에 HTTP 요청 파라미터를 바인딩하는 어노테이션
 *
 * Spring의 @RequestParam과 동일한 역할을 수행합니다.
 * URL 쿼리 파라미터나 폼 데이터를 메서드 파라미터에 자동으로 매핑합니다.
 *
 * 사용 예시:
 * @RequestMapping("/search")
 * public ModelAndView search(@RequestParam("keyword") String keyword,
 *                           @RequestParam(value="page", defaultValue="1") int page) {
 *     // keyword, page 파라미터가 자동으로 바인딩됨
 * }
 *
 * 특징:
 * - 파라미터 레벨에 적용 (ElementType.PARAMETER)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - 파라미터 이름, 필수 여부, 기본값 지정 가능
 */
//파라미터에만 적용 가능
@Target(ElementType.PARAMETER)
//런타임에 어노테이션 정보 유지
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /**
     * 바인딩할 HTTP 요청 파라미터의 이름
     *
     * @return 파라미터 이름 (예:"keyword","page","userId"
     * */
    String value();

    /**
     * 파라미터 필수 여부
     *
     * @return true: 필수 파라미터(없으면 예외 발생)
     *         false: 선택적 파라미터 (없어도 OK)
     *         기본값 : true
     * */
    boolean required() default true;

    /**
     * 파라미터가 없을 때 사용할 기본값
     *
     * @return 기반값 문자열 (빈 문자열이면 기본값 없음)
     * */
    String defaultValue() default "";

}
