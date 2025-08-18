package winter.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 필드가 지정된 정규식 패턴과 일치하는지 검증하는 어노테이션
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    /**
     * 검증에 사용할 정규식 패턴
     */
    String regexp();

    /**
     * 검증 실패 시 표시할 메시지
     */
    String message() default "must match the pattern";
}