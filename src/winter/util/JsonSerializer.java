package winter.util;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 간단한 JSON 직렬화 유틸리티 클래스
 * 객체를 JSON 문자열로 변환하는 기능을 제공합니다.
 *
 * 지원하는 타입 :
 * - String, Number, Boolean (기본 타입)
 * - Map<String, Object>(JSON 객체 형태)
 * - 일반 JAVA 객체 (Reflection을 통한 getter 호출)
 * */
public class JsonSerializer {
    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param obj 변환할 객체
     * @param JSON 형태의 문자열
     * */
    public static String toJson(Object obj){
        if (obj == null){
            return "null";
        }

        // 기본 타입 처리
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj)+ "\"";
        }

        if(obj instanceof Number || obj instanceof Boolean){
            return obj.toString();
        }

        //Map 단일 처리 (JSON 객체)
        if (obj instanceof Map<?,?>){
            return mapToJson((Map<?,?>) obj);
        }

        //일반 객체 처리 (Reflection 사용)
        return objectToJson(obj);
    }
    /**
     * Map을 JSON 객체 문자열로 변환
     * 예 :{"key1": "value1","key2":123}*/
    private static String mapToJson(Map<?,?> map){
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for(Map.Entry<?,?> entry : map.entrySet()){
            if(!first){
                sb.append(",");
            }
            first = false;

            //키는 항상 문자열로 처리
            sb.append("\"").append(entry.getKey().toString()).append("\":");
            //값은 재귀적으로 json 변환
            sb.append(toJson(entry.getValue()));
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * 일반 Java객체를 JSON으로 변환
     * Reflection을 사용하여 getter 메서드를 호출하고결과를
     * JSON형태로 구성*/
    private static String objectToJson(Object obj){
        StringBuilder sb= new StringBuilder("{");
        boolean first = true;
        
        //클래스의 모든 public 메서드를 조회
        for (Method method : obj.getClass().getMethods()){
            // getter 메서드만 선별 (get으로 시작하고 파라미터가 없는 메서드)
            if(isGetter(method)){
                try {
                    //getter 이름에서 필드명 추출 (getName -> name)
                    String fieldName = getFieldNameFromGetter(method.getName());
                    //getter 호출하여 값 얻기
                    Object value = method.invoke(obj);

                    if (!first){
                        sb.append(",");
                    }
                    first =false;

                    //JSON 형태로 추가 : "fieldName" : value
                    sb.append("\"").append(fieldName).append("\" :");
                    //재귀적으로 값 직렬화
                    sb.append(toJson(value));
                }catch (Exception e){
                    //getter 호출 실패 시 해당 필드는 무시
                    System.err.println("Failed to serialize field from method: " + method.getName());
                }
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * 메서드가 getter인지 판단
     * - get으로 시작
     * - 파라미터가 없음
     * - 반환 타입이 void가 아님
     * - getClass() 메서드는 제외 (무한 재귀 방지)
     * */
    private static boolean isGetter(Method method){
        return method.getName().startsWith("get") &&
               method.getParameterCount() == 0 &&
               !method.getReturnType().equals(void.class) &&
               !method.getName().equals("getClass"); //getClass() 제외
    }

    /**
     * getter 메서드 이름에서 필드명 추출
     * 예 : getName ->name, getUserId -> userId
     * */
    private static String getFieldNameFromGetter(String getterName){
        String raw = getterName.substring(3); // "get" 제거
        //첫 글자를 소문자로 변환
        return Character.toLowerCase(raw.charAt(0))+raw.substring(1);
    }

    /**
     * JSON 문자열에서 특수 문자 이스케이프 처리
     * 쌍따옴표, 백슬래시, 줄바꿈 등을 JSON 구격에 맞게 변환
     * */
    private static String escapeJson(String str){
        return str.replace("\\", "\\\\")    // 백슬래시
                  .replace("\"", "\\\"")    // 쌍따옴표
                  .replace("\n", "\\n")     // 줄바꿈
                  .replace("\r", "\\r")     // 캐리지 리턴
                  .replace("\t", "\\t");    // 탭
    }
    

}
