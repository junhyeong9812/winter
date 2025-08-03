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
 *                           @RequestParam(value="page", defaultValue="1") int page,
 *                           @RequestParam(name="size", required=false) Integer size) {
 *     // keyword, page, size 파라미터가 자동으로 바인딩됨
 * }
 *
 * 파일 업로드 사용 예시:
 * @RequestMapping("/upload")
 * public ModelAndView upload(@RequestParam("file") MultipartFile file,
 *                           @RequestParam("files") MultipartFile[] files) {
 *     // 단일 파일과 다중 파일 모두 지원
 * }
 *
 * 특징:
 * - 파라미터 레벨에 적용 (ElementType.PARAMETER)
 * - 런타임에 리플렉션으로 읽을 수 있음 (RetentionPolicy.RUNTIME)
 * - 파라미터 이름, 필수 여부, 기본값 지정 가능
 * - value()와 name()은 동일한 역할 (별칭 관계)
 */
@Target(ElementType.PARAMETER)              // 파라미터에만 적용 가능
@Retention(RetentionPolicy.RUNTIME)         // 런타임에 어노테이션 정보 유지
public @interface RequestParam {

    /**
     * 바인딩할 HTTP 요청 파라미터의 이름
     *
     * name() 속성의 별칭(alias)입니다.
     * value()와 name() 중 하나만 사용하면 됩니다.
     *
     * @return 파라미터 이름 (예: "keyword", "page", "userId")
     */
    String value() default "";

    /**
     * 바인딩할 HTTP 요청 파라미터의 이름
     *
     * value() 속성의 별칭(alias)입니다.
     * value()와 name() 중 하나만 사용하면 됩니다.
     *
     * Spring Framework와의 호환성을 위해 추가되었습니다.
     *
     * @return 파라미터 이름 (예: "keyword", "page", "userId")
     */
    String name() default "";

    /**
     * 파라미터 필수 여부
     *
     * true: 필수 파라미터
     * - 파라미터가 없으면 IllegalArgumentException 발생
     * - defaultValue가 있으면 예외 발생하지 않음
     *
     * false: 선택적 파라미터
     * - 파라미터가 없어도 null 또는 defaultValue 사용
     * - 기본 타입(int, boolean 등)의 경우 defaultValue 권장
     *
     * @return true: 필수 파라미터, false: 선택적 파라미터
     */
    boolean required() default true;

    /**
     * 파라미터가 없을 때 사용할 기본값
     *
     * 기본값이 설정되면 required=true여도 예외가 발생하지 않습니다.
     *
     * 사용 예시:
     * - @RequestParam(value="page", defaultValue="1") int page
     * - @RequestParam(value="enabled", defaultValue="false") boolean enabled
     * - @RequestParam(value="limit", defaultValue="10") Integer limit
     *
     * 주의사항:
     * - 빈 문자열("")이면 기본값 없음을 의미
     * - 문자열로 지정되므로 TypeConverter에서 적절한 타입으로 변환
     * - null 값을 원하면 defaultValue는 설정하지 말고 required=false 사용
     *
     * @return 기본값 문자열 (빈 문자열이면 기본값 없음)
     */
    String defaultValue() default "";
}