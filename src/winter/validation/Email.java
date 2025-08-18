package winter.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 필드가 올바른 이메일 형식인지 검증하는 어노테이션
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    /**
     * 검증 실패 시 표시할 메시지
     */
    String message() default "must be a valid email address";
}