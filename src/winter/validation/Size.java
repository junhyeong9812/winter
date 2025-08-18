package winter.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 필드의 크기(길이)를 검증하는 어노테이션
 * String의 길이, Collection의 크기, Array의 길이 등을 검증
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    /**
     * 최소 크기 (포함)
     */
    int min() default 0;

    /**
     * 최대 크기 (포함)
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 검증 실패 시 표시할 메시지
     */
    String message() default "size must be between {min} and {max}";
}