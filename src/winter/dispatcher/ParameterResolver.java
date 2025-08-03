package winter.dispatcher;

import winter.annotation.ModelAttribute;
import winter.annotation.RequestParam;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.upload.MultipartFile;
import winter.upload.MultipartRequest;
import winter.util.ModelAttributeBinder;
import winter.util.TypeConverter;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 메서드 파라미터를 해결(resolve)하는 전략 클래스
 *
 * 메서드의 각 파라미터에 대해 적절한 값을 찾아서 반환하는 역할을 담당합니다.
 * 파라미터 타입과 어노테이션에 따라 다른 처리 방식을 사용합니다.
 *
 * 지원하는 파라미터 타입:
 * 1. @RequestParam이 붙은 파라미터 → 개별 요청 파라미터 바인딩
 * 2. @ModelAttribute가 붙은 파라미터 → 객체 바인딩
 * 3. HttpRequest 타입 → 요청 객체 전달
 * 4. HttpResponse 타입 → 응답 객체 전달
 * 5. 일반 객체 타입 → @ModelAttribute 어노테이션이 없어도 객체 바인딩
 */
public class ParameterResolver {

    /**
     * 메서드 파라미터를 해결하여 실제 값을 반환
     *
     * @param parameter 해결할 파라미터 정보 (java.lang.reflect.Parameter)
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 파라미터에 전달할 실제 값
     * @throws Exception 파라미터 해결 실패 시
     */
    public Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response) throws Exception{
        Class<?> paramType = parameter.getType();
        
        // 1. HttpRequest 타입 처리
        if(paramType.equals(HttpRequest.class)){
            return request;
        }

        // 2. HttpResponse 타입 처리
        if(paramType.equals(HttpResponse.class)){
            return response;
        }

        // 3. MultipartFile 타입 처리 (새로 추가)
        if (paramType.equals(MultipartFile.class)) {
            return resolveMultipartFile(parameter, request);
        }

        // 4. MultipartFile[] 배열 타입 처리 (새로 추가)
        if (paramType.equals(MultipartFile[].class)) {
            return resolveMultipartFileArray(parameter, request);
        }

        // 5. @RequestParam 어노테이션 처리
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if(requestParam != null){
            return resolveRequestParam(requestParam, paramType,request);
        }

        // 4. @ModelAttribute 어노테이션 처리 (명시적 처리)
        ModelAttribute modelAttribute = parameter.getAnnotation(ModelAttribute.class);
        if(modelAttribute != null){
            return resolveModelAttribute(paramType,request);
        }

        //5. 일반 객체 타입 처리 (암묵적 @ModelAttribute)
        //기본 타입이 아니고, HTTP 요청/응답 타입도 아니면 객체 바인딩
        if(!isSimpleType(paramType)){
            return resolveModelAttribute(paramType,request);
        }

        // 6. 지원하지 앟는 파라미터 타입
        throw new IllegalArgumentException(
                "Unsupported parameter type: " + paramType.getName() + ". " +
                "Use @RequestParam for simple types or @ModelAttribute for objects."
        );
    }
    /**
     * @RequestParam 어노테이션이 붙은 파라미터 처리
     *
     * @param requestParam @RequestParam 어노테이션
     * @param paramType 파라미터 타입
     * @param request HTTP 요청
     * @return 변환된 파라미터 값
     */
    private Object resolveRequestParam(RequestParam requestParam,Class<?> paramType,HttpRequest request){
        String paramName = requestParam.value();
        boolean required = requestParam.required();
        String defaultValue = requestParam.defaultValue();

        //요청에서 파라미터 값 가져오기
        String paramValue = request.getParameter(paramName);

        //필수 파라미터가 없는 경우
        if (paramValue ==null && required && defaultValue.isEmpty()) {
            throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing");
        }

        //타입 변환
        try{
            if(defaultValue.isEmpty()){
                return TypeConverter.convert(paramValue,paramType);
            }else {
                return TypeConverter.convertWithDefault(paramValue,paramType,defaultValue);
            }
        }catch (Exception e){
            throw new IllegalArgumentException(
                    String.format("Failed to convert parameter '%s' with value '%s' to type %s: %s",
                            paramName, paramValue, paramType.getSimpleName(), e.getMessage()), e);
        }
    }

    /**
     * @ModelAttribute 어노테이션이 붙은 파라미터 또는 일반 객체 타입 처리
     *
     * @param paramType 파라미터 타입 (바인딩할 객체의 클래스)
     * @param request HTTP 요청
     * @return 바인딩된 객체
     */
    private Object resolveModelAttribute(Class<?> paramType,HttpRequest request){
        try{
            // 기본 ModelAttributeBinder를 사용하여 객체 바인딩
            return ModelAttributeBinder.bind(request,paramType);
        }catch (Exception e){
            throw new RuntimeException(
                    "Failed to bind request parameters to " + paramType.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * 단순 타입인지 판단
     * 단순 타입은 @RequestParam으로 처리되어야 하고,
     * 복합 타입은 @ModelAttribute로 처리되어야 합니다.
     *
     * @param type 확인할 타입
     * @return 단순 타입 여부
     */
    private boolean isSimpleType(Class<?> type){
        return type.isPrimitive() ||                    // int, boolean, double등
               type.equals(String.class) ||             // String
               Number.class.isAssignableFrom(type) ||   // Integer, Long, Double 등
               type.equals(Boolean.class) ||            // Boolean
               type.isArray() ||                        // 배열
               type.isEnum() ||                         // 열거형
               TypeConverter.isSupported(type);         // TypeConverter에서 지원하는 타입
    }

    /**
     * 파라미터가 어떤 방식으로 해결될지 미리 확인 (디버깅용)
     *
     * @param parameter 확인할 파라미터
     * @return 해결 방식 설명
     */
    public String getResolutionStrategy(Parameter parameter){
        Class<?> paramType = parameter.getType();

        if(paramType.equals(HttpRequest.class)){
            return "HttpRequest injection";
        }
        if(paramType.equals(HttpResponse.class)){
            return "HttpResponse injection";
        }
        // MultipartFile 처리 전략
        if (paramType.equals(MultipartFile.class)) {
            RequestParam rp = parameter.getAnnotation(RequestParam.class);
            String paramName = rp != null ? rp.value() : parameter.getName();
            return String.format("MultipartFile binding for parameter '%s'", paramName);
        }
        if (paramType.equals(MultipartFile[].class)) {
            RequestParam rp = parameter.getAnnotation(RequestParam.class);
            String paramName = rp != null ? rp.value() : parameter.getName();
            return String.format("MultipartFile[] array binding for parameter '%s'", paramName);
        }

        // 일반 파라미터 처리 전략
        if (parameter.getAnnotation(RequestParam.class) != null) {
            RequestParam rp = parameter.getAnnotation(RequestParam.class);
            return String.format("@RequestParam('%s', required=%s, defaultValue='%s')",
                    rp.value(), rp.required(), rp.defaultValue());
        }
        if (parameter.getAnnotation(ModelAttribute.class) != null) {
            return "@ModelAttribute object binding to " + paramType.getSimpleName();
        }
        if (!isSimpleType(paramType)) {
            return "Implicit @ModelAttribute object binding to " + paramType.getSimpleName();
        }

        return "Unsupported parameter type: " + paramType.getName();
    }

    /**
     * MultipartFile 타입 파라미터 처리
     *
     * @param parameter 파라미터 정보
     * @param request HTTP 요청
     * @return 업로드된 파일 또는 null
     * */
    private MultipartFile resolveMultipartFile(Parameter parameter,HttpRequest request){
        if(!(request instanceof MultipartRequest)) {
            throw new IllegalArgumentException(
                    "MultipartFile parameter requires multipart/form-data request");
        }

        MultipartRequest multipartRequest = (MultipartRequest) request;

        //@Reequest 어노테이션에서 파라미터명 추출
        String paramName = getParameterName(parameter);

        MultipartFile file = multipartRequest.getFile(paramName);

        //required 속성 확인
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam != null && requestParam.required() && (file == null || file.isEmpty())){
            throw new IllegalArgumentException("Required file parameter '" + paramName + "' is missing or empty");
        }
        
        return file;
    }

    /**
     * MultipartFile[] 배열 타입 파라미터 처리
     *
     * @param parameter 파라미터 정보
     * @param request HTTP 요청
     * @return 업로드된 파일 배열
     */
    private MultipartFile[] resolveMultipartFileArray(Parameter parameter, HttpRequest request) {
        if (!(request instanceof MultipartRequest)) {
            throw new IllegalArgumentException(
                    "MultipartFile[] parameter requires multipart/form-data request");
        }

        MultipartRequest multipartRequest = (MultipartRequest) request;

        // @RequestParam 어노테이션에서 파라미터명 추출
        String paramName = getParameterName(parameter);

        List<MultipartFile> files = multipartRequest.getFiles(paramName);

        // required 속성 확인
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam != null && requestParam.required() && (files == null || files.isEmpty())) {
            throw new IllegalArgumentException("Required file parameter '" + paramName + "' is missing or empty");
        }

        return files != null ? files.toArray(new MultipartFile[0]) : new MultipartFile[0];
    }

    /**
     * 파라미터명을 추출합니다.
     * @RequestParam 어노테이션의 value 또는 name 속성을 우선 사용하고,
     * 없으면 파라미터의 실제 이름을 사용합니다.
     *
     * @param parameter 파라미터 정보
     * @return 파라미터명
     * */
    private String getParameterName(Parameter parameter){
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if(requestParam != null){
            String paramName = requestParam.value();
            if(paramName.isEmpty()){
                paramName = requestParam.name();
            }
            if ( !paramName.isEmpty()){
                return paramName;
            }
        }
        //파라미터의 실제 이름 사용 (컴파일 시 - parameters 옵션 필요)
        return parameter.getName();
    }


}
