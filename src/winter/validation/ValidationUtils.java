package winter.validation;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 검증에 사용되는 유틸리티 메서드들
 */
public class ValidationUtils {

    // 이메일 검증용 정규식 패턴
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * 값이 비어있는지 확인
     * null, 빈 문자열, 빈 컬렉션, 빈 배열을 비어있다고 판단
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }

        return false;
    }

    /**
     * 값이 null이 아니고 비어있지 않은지 확인
     */
    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    /**
     * 이메일 형식이 올바른지 확인
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email.trim()).matches();
    }

    /**
     * 객체의 크기(길이) 반환
     */
    public static int getSize(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof String) {
            return ((String) value).length();
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).size();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }

        // 기타 타입은 문자열로 변환 후 길이 반환
        return value.toString().length();
    }

    /**
     * 문자열이 정규식 패턴과 일치하는지 확인
     */
    public static boolean matchesPattern(String value, String pattern) {
        if (value == null || pattern == null) {
            return false;
        }

        try {
            return Pattern.matches(pattern, value);
        } catch (Exception e) {
            // 잘못된 정규식 패턴인 경우 false 반환
            return false;
        }
    }

    /**
     * 숫자 범위 확인 (최소값, 최대값 포함)
     */
    public static boolean isInRange(Number value, Number min, Number max) {
        if (value == null) {
            return false;
        }

        double val = value.doubleValue();
        double minVal = min != null ? min.doubleValue() : Double.MIN_VALUE;
        double maxVal = max != null ? max.doubleValue() : Double.MAX_VALUE;

        return val >= minVal && val <= maxVal;
    }

    /**
     * 크기가 지정된 범위 내에 있는지 확인
     */
    public static boolean isSizeInRange(Object value, int min, int max) {
        int size = getSize(value);
        return size >= min && size <= max;
    }

    /**
     * 에러 메시지에서 플레이스홀더 치환
     * 예: "size must be between {min} and {max}" -> "size must be between 2 and 10"
     */
    public static String replacePlaceholders(String message, Object... values) {
        if (message == null) {
            return "";
        }

        String result = message;

        // {min}, {max} 등의 플레이스홀더 치환
        if (values.length >= 2) {
            result = result.replace("{min}", String.valueOf(values[0]));
            result = result.replace("{max}", String.valueOf(values[1]));
        }

        return result;
    }
}