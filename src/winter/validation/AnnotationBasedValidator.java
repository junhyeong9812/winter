package winter.validation;

import java.lang.reflect.Field;

/**
 * 어노테이션을 기반으로 객체를 검증하는 검증기
 * 리플렉션을 사용하여 필드의 어노테이션을 확인하고 해당 검증을 수행
 */
public class AnnotationBasedValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // 모든 클래스를 지원 (null 제외)
        return clazz != null;
    }

    @Override
    public void validate(Object target, BindingResult bindingResult) {
        if (target == null) {
            return;
        }

        validateFields(target, bindingResult);
    }

    /**
     * 객체의 모든 필드를 검증
     */
    private void validateFields(Object target, BindingResult bindingResult) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(target);
                validateField(field, value, bindingResult);
            } catch (IllegalAccessException e) {
                System.err.println("Failed to access field: " + field.getName());
            }
        }
    }

    /**
     * 개별 필드 검증
     */
    private void validateField(Field field, Object value, BindingResult bindingResult) {
        String fieldName = field.getName();

        // @NotNull 검증
        if (field.isAnnotationPresent(NotNull.class)) {
            validateNotNull(fieldName, value, field.getAnnotation(NotNull.class), bindingResult);
        }

        // @NotEmpty 검증
        if (field.isAnnotationPresent(NotEmpty.class)) {
            validateNotEmpty(fieldName, value, field.getAnnotation(NotEmpty.class), bindingResult);
        }

        // @Size 검증
        if (field.isAnnotationPresent(Size.class)) {
            validateSize(fieldName, value, field.getAnnotation(Size.class), bindingResult);
        }

        // @Email 검증
        if (field.isAnnotationPresent(Email.class)) {
            validateEmail(fieldName, value, field.getAnnotation(Email.class), bindingResult);
        }

        // @Pattern 검증
        if (field.isAnnotationPresent(Pattern.class)) {
            validatePattern(fieldName, value, field.getAnnotation(Pattern.class), bindingResult);
        }
    }

    /**
     * @NotNull 검증 수행
     */
    private void validateNotNull(String fieldName, Object value, NotNull annotation, BindingResult bindingResult) {
        if (value == null) {
            bindingResult.addFieldError(fieldName, value, annotation.message());
        }
    }

    /**
     * @NotEmpty 검증 수행
     */
    private void validateNotEmpty(String fieldName, Object value, NotEmpty annotation, BindingResult bindingResult) {
        if (ValidationUtils.isEmpty(value)) {
            bindingResult.addFieldError(fieldName, value, annotation.message());
        }
    }

    /**
     * @Size 검증 수행
     */
    private void validateSize(String fieldName, Object value, Size annotation, BindingResult bindingResult) {
        // null 값은 @Size에서 허용 (null 체크는 @NotNull이 담당)
        if (value == null) {
            return;
        }

        int size = ValidationUtils.getSize(value);
        int min = annotation.min();
        int max = annotation.max();

        if (!ValidationUtils.isSizeInRange(value, min, max)) {
            String message = ValidationUtils.replacePlaceholders(annotation.message(), min, max);
            bindingResult.addFieldError(fieldName, value, message);
        }
    }

    /**
     * @Email 검증 수행
     */
    private void validateEmail(String fieldName, Object value, Email annotation, BindingResult bindingResult) {
        // null이나 빈 값은 @Email에서 허용 (null 체크는 @NotNull/@NotEmpty가 담당)
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            return;
        }

        if (!(value instanceof String)) {
            bindingResult.addFieldError(fieldName, value, "must be a string");
            return;
        }

        String email = (String) value;
        if (!ValidationUtils.isValidEmail(email)) {
            bindingResult.addFieldError(fieldName, value, annotation.message());
        }
    }

    /**
     * @Pattern 검증 수행
     */
    private void validatePattern(String fieldName, Object value, Pattern annotation, BindingResult bindingResult) {
        // null이나 빈 값은 @Pattern에서 허용 (null 체크는 @NotNull/@NotEmpty가 담당)
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            return;
        }

        if (!(value instanceof String)) {
            bindingResult.addFieldError(fieldName, value, "must be a string");
            return;
        }

        String str = (String) value;
        if (!ValidationUtils.matchesPattern(str, annotation.regexp())) {
            bindingResult.addFieldError(fieldName, value, annotation.message());
        }
    }
}