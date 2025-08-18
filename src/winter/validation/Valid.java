package winter.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 객체의 유효성 검증을 수행하도록 지시하는 어노테이션
 * 컨트롤러 메서드의 파라미터에 사용하여 자동 검증을 활성화
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}