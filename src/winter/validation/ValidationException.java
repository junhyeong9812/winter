package winter.validation;

/**
 * 검증 실패 시 발생하는 예외
 * BindingResult 정보를 포함하여 상세한 오류 정보를 제공
 */
public class ValidationException extends RuntimeException {
    private final BindingResult bindingResult;

    public ValidationException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public ValidationException(BindingResult bindingResult) {
        super("Validation failed: " + bindingResult.getAllErrorMessages());
        this.bindingResult = bindingResult;
    }

    /**
     * 검증 결과 정보
     */
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    /**
     * 검증 오류가 있는지 확인
     */
    public boolean hasErrors() {
        return bindingResult != null && bindingResult.hasErrors();
    }

    /**
     * 오류 개수
     */
    public int getErrorCount() {
        return bindingResult != null ? bindingResult.getErrorCount() : 0;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", errorCount=" + getErrorCount() +
                ", bindingResult=" + bindingResult +
                '}';
    }
}