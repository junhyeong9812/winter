package winter.util;

import winter.http.HttpRequest;

import java.lang.reflect.Method;

/**
 * HttpRequest의 파라미터 값을 기반으로 Java 객체의 필드(setter)를 자동으로 채워주는 바인딩 유틸리티
 * 예: request에 name=jun, age=25가 있을 때, User 객체의 setName("jun"), setAge("25")를 자동 호출
 */
public class ModelAttributeBinder {

    /**
     * 요청 파라미터 값을 클래스 객체로 바인딩하여 반환
     *
     * @param request HttpRequest 객체 (요청 파라미터를 담고 있음)
     * @param clazz 바인딩 대상 클래스 타입 (예: User.class)
     * @return 파라미터로부터 값이 세팅된 객체
     * @param <T> 제네릭 타입 (리턴 객체 타입)
     */
    public static <T> T bind(HttpRequest request, Class<T> clazz) {
        try {
            // 전달받은 클래스 타입으로 객체 생성 (기본 생성자 호출)
            T instance = clazz.getDeclaredConstructor().newInstance();

            // 클래스의 public 메서드 중 setter에 해당하는 것만 골라 파라미터 값을 넣어준다
            for (Method method : clazz.getMethods()) {
                if (isSetter(method)) {
                    // 메서드 이름(setName → name 등)을 기반으로 파라미터 이름 추출
                    String paramName = getParamNameFromSetter(method.getName());

                    // 요청에서 해당 파라미터 이름의 값 조회
                    String paramValue = request.getParameter(paramName);

                    // 파라미터가 존재한다면 setter 메서드 호출로 값 세팅
                    if (paramValue != null) {
                        // setter 메서드의 파라미터 타입 가져오기
                        Class<?> paramType = method.getParameterTypes()[0];
                        
                        // TypeConverter를 사용하여 타입 변환
                        Object convertedValue = TypeConverter.convert(paramValue, paramType);
                        
                        method.invoke(instance, convertedValue);
                    }
                }
            }
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Binding failed for " + clazz.getName(), e);
        }
    }

    /**
     * 메서드가 setter인지 판단 (이름이 set으로 시작하고, 파라미터가 1개, 반환값이 void여야 함)
     */
    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set") &&
                method.getParameterCount() == 1 &&
                method.getReturnType().equals(void.class);
    }

    /**
     * setter 메서드 이름으로부터 파라미터 이름을 추출
     * 예: setName → name, setUserId → userId
     */
    private static String getParamNameFromSetter(String setterName) {
        String raw = setterName.substring(3); // 앞의 "set" 제거
        // 첫 글자를 소문자로 변환: "Name" → "name"
        return Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }
}
