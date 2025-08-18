package winter.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 객체 검증 결과를 담는 컨테이너 클래스
 * 검증 오류들을 수집하고 관리하는 역할
 */
public class BindingResult {
    private final List<FieldError> fieldErrors = new ArrayList<>();
    private final Object target;
    private final String objectName;

    public BindingResult(Object target) {
        this.target = target;
        this.objectName = target != null ? target.getClass().getSimpleName() : "unknown";
    }

    public BindingResult(Object target, String objectName) {
        this.target = target;
        this.objectName = objectName;
    }

    /**
     * 필드 오류 추가
     */
    public void addFieldError(String field, Object rejectedValue, String message) {
        fieldErrors.add(new FieldError(field, rejectedValue, message));
    }

    /**
     * 검증 오류가 있는지 확인
     */
    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

    /**
     * 전체 오류 개수
     */
    public int getErrorCount() {
        return fieldErrors.size();
    }

    /**
     * 모든 필드 오류 목록
     */
    public List<FieldError> getFieldErrors() {
        return new ArrayList<>(fieldErrors);
    }

    /**
     * 특정 필드의 오류 목록
     */
    public List<FieldError> getFieldErrors(String field) {
        List<FieldError> result = new ArrayList<>();
        for (FieldError error : fieldErrors) {
            if (error.getField().equals(field)) {
                result.add(error);
            }
        }
        return result;
    }

    /**
     * 특정 필드에 오류가 있는지 확인
     */
    public boolean hasFieldErrors(String field) {
        return !getFieldErrors(field).isEmpty();
    }

    /**
     * 검증 대상 객체
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 객체 이름
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * 필드별 오류 메시지 맵으로 반환 (템플릿에서 사용하기 편함)
     */
    public Map<String, String> getFieldErrorMessages() {
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errorMap.put(error.getField(), error.getMessage());
        }
        return errorMap;
    }

    /**
     * 모든 오류 메시지를 문자열로 결합
     */
    public String getAllErrorMessages() {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : fieldErrors) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(error.getField()).append(": ").append(error.getMessage());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "BindingResult{" +
                "objectName='" + objectName + '\'' +
                ", errorCount=" + getErrorCount() +
                ", hasErrors=" + hasErrors() +
                '}';
    }
}