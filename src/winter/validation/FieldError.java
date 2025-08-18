package winter.validation;

/**
 * 필드 검증 오류 정보를 담는 클래스
 */
public class FieldError {
    private final String field;
    private final Object rejectedValue;
    private final String message;

    public FieldError(String field, Object rejectedValue, String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    /**
     * 오류가 발생한 필드명
     */
    public String getField() {
        return field;
    }

    /**
     * 검증에 실패한 값
     */
    public Object getRejectedValue() {
        return rejectedValue;
    }

    /**
     * 오류 메시지
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FieldError{" +
                "field='" + field + '\'' +
                ", rejectedValue=" + rejectedValue +
                ", message='" + message + '\'' +
                '}';
    }
}