package winter.util;

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
        //기본 타입 및 레퍼 타입 변환기 등록
        CONVERTERS.put(String.class,value -> value);

        //정수 타입
        CONVERTERS.put(int.class,Integer::parseInt);
        CONVERTERS.put(Integer.class, value -> value.isEmpty() ? null : Integer.parseInt(value));

        //긴 정수 타입
        CONVERTERS.put(long.class, Long::parseLong);
        CONVERTERS.put(Long.class, value -> value.isEmpty() ? null : Long.parseLong(value));

        //실수 타입
        CONVERTERS.put(double.class, Double::parseDouble);
        CONVERTERS.put(Double.class, value -> value.isEmpty() ? null :Double.parseDouble(value));

        //부울 타입
        CONVERTERS.put(boolean.class, Boolean::parseBoolean);
        CONVERTERS.put(Boolean.class, value -> value.isEmpty() ? null : Boolean.parseBoolean(value));

        //날짜 타입
        CONVERTERS.put(LocalDate.class, value ->{
            if(value == null || value.isEmpty()) return null;
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
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
    public static <T> T convert(String value,Class<T> targetType){
        if(value == null){
            return null;
        }

        //배열 타입 처리
        if(targetType.isArray()){
            return (T) convertArray(value,targetType);
        }

        //등록된 변환기 찾기
        Function<String,Object> converter = CONVERTERS.get(targetType);
        if(converter ==null){
            throw new IllegalArgumentException("Unsupported type for conversion: "+ targetType.getName());
        }

        try {
            return (T) converter.apply(value);
        }catch (Exception e){
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

    }



}
