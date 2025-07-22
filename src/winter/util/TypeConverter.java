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

}
