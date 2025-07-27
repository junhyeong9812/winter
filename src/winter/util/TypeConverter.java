package winter.util;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 문자열을 다양한 타입으로 변환하는 유틸리티 클래스
 *
 * HTTP 요청 파라미터는 모두 문자열로 전달되므로, 메서드 파라미터의
 * 실제 타입에 맞게 변환해주는 역할을 담당합니다.
 *
 * 지원하는 타입:
 * - 기본 타입: int, long, double, boolean
 * - 래퍼 타입: Integer, Long, Double, Boolean
 * - 문자열: String
 * - 날짜: LocalDate, LocalDateTime
 * - 배열: String[]
 */
public class TypeConverter {

    // 타입별 변환 함수를 저장하는 맵
    private static final Map<Class<?>, Function<String, Object>> CONVERTERS = new HashMap<>();

    static {
        // 기본 타입 및 래퍼 타입 변환기 등록
        CONVERTERS.put(String.class, value -> value);  // 문자열은 그대로

        // 정수 타입
        CONVERTERS.put(int.class, Integer::parseInt);
        CONVERTERS.put(Integer.class, value -> value.isEmpty() ? null : Integer.parseInt(value));

        // 긴 정수 타입
        CONVERTERS.put(long.class, Long::parseLong);
        CONVERTERS.put(Long.class, value -> value.isEmpty() ? null : Long.parseLong(value));

        // 실수 타입
        CONVERTERS.put(double.class, Double::parseDouble);
        CONVERTERS.put(Double.class, value -> value.isEmpty() ? null : Double.parseDouble(value));

        // 부울 타입
        CONVERTERS.put(boolean.class, Boolean::parseBoolean);
        CONVERTERS.put(Boolean.class, value -> value.isEmpty() ? null : Boolean.parseBoolean(value));

        // 날짜 타입
        CONVERTERS.put(LocalDate.class, value -> {
            if (value == null || value.isEmpty()) return null;
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);  // yyyy-MM-dd
        });

        CONVERTERS.put(LocalDateTime.class, value -> {
            if (value == null || value.isEmpty()) return null;
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // yyyy-MM-ddTHH:mm:ss
        });
    }

    /**
     * 문자열 값을 지정된 타입으로 변환
     *
     * @param value 변환할 문자열 값
     * @param targetType 변환할 목표 타입
     * @return 변환된 객체
     * @throws IllegalArgumentException 지원하지 않는 타입이거나 변환 실패 시
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> targetType) {
        if (value == null) {
            return null;
        }

        // 배열 타입 처리
        if (targetType.isArray()) {
            return (T) convertArray(value, targetType);
        }

        // 등록된 변환기 찾기
        Function<String, Object> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            throw new IllegalArgumentException("Unsupported type for conversion: " + targetType.getName());
        }

        try {
            return (T) converter.apply(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Cannot convert '%s' to type %s: %s",
                            value, targetType.getSimpleName(), e.getMessage()), e);
        }
    }

    /**
     * 문자열을 배열로 변환
     * 쉼표로 구분된 값들을 배열로 변환합니다.
     *
     * @param value 변환할 문자열 (예: "apple,banana,cherry")
     * @param arrayType 배열 타입 (예: String[].class)
     * @return 변환된 배열
     */
    private static Object convertArray(String value, Class<?> arrayType) {
        Class<?> componentType = arrayType.getComponentType();

        // 빈 값이면 빈 배열 반환
        if (value == null || value.trim().isEmpty()) {
            return java.lang.reflect.Array.newInstance(componentType, 0);
        }

        // 쉼표로 분리
        String[] parts = value.split(",");
        Object array = java.lang.reflect.Array.newInstance(componentType, parts.length);

        // 각 요소를 해당 타입으로 변환
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            Object convertedValue = convert(part, componentType);
            java.lang.reflect.Array.set(array, i, convertedValue);
        }

        return array;
    }

    /**
     * 지원하는 타입인지 확인
     *
     * @param type 확인할 타입
     * @return 지원 여부
     */
    public static boolean isSupported(Class<?> type) {
        return CONVERTERS.containsKey(type) || type.isArray();
    }

    /**
     * 기본값이 있는 변환
     * 변환 실패 시 기본값을 반환합니다.
     *
     * @param value 변환할 문자열
     * @param targetType 목표 타입
     * @param defaultValue 기본값
     * @return 변환된 값 또는 기본값
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertWithDefault(String value, Class<T> targetType, String defaultValue) {
        try {
            // 값이 없으면 기본값 사용
            String valueToConvert = (value == null || value.isEmpty()) ? defaultValue : value;

            if (valueToConvert == null || valueToConvert.isEmpty()) {
                // 기본값도 없으면 null 반환 (nullable 타입인 경우)
                if (targetType.isPrimitive()) {
                    // 기본 타입인 경우 기본값 반환
                    return getDefaultForPrimitive(targetType);
                }
                return null;
            }

            return convert(valueToConvert, targetType);
        } catch (Exception e) {
            // 변환 실패 시 기본값으로 재시도
            if (!defaultValue.isEmpty()) {
                try {
                    return convert(defaultValue, targetType);
                } catch (Exception ex) {
                    // 기본값 변환도 실패하면 예외 발생
                    throw new IllegalArgumentException("Cannot convert default value: " + defaultValue, ex);
                }
            }
            throw e;
        }
    }

    /**
     * 기본 타입의 기본값 반환
     *
     * @param primitiveType 기본 타입
     * @return 기본값
     */
    @SuppressWarnings("unchecked")
    private static <T> T getDefaultForPrimitive(Class<T> primitiveType) {
        if (primitiveType == int.class) return (T) Integer.valueOf(0);
        if (primitiveType == long.class) return (T) Long.valueOf(0L);
        if (primitiveType == double.class) return (T) Double.valueOf(0.0);
        if (primitiveType == boolean.class) return (T) Boolean.valueOf(false);

        throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType);
    }
}