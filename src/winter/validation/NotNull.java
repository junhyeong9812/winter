package winter.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 필드가 null이 아님을 검증하는 어노테이션
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    /**
     * 검증 실패 시 표시할 메시지
     */
    String message() default "must not be null";
}