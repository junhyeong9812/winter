# 23단계: 파라미터 바인딩 (Parameter Binding)

## 📋 단계 개요

23단계에서는 어노테이션 기반 컨트롤러에서 **`@RequestParam`**과 **`@ModelAttribute`**를 통한 정교한 파라미터 바인딩 기능을 구현합니다. HTTP 요청 파라미터를 다양한 타입의 메서드 파라미터로 자동 변환하여 바인딩하는 시스템을 완성합니다.

## 🎯 주요 목표

- **@RequestParam** 어노테이션을 통한 개별 파라미터 바인딩
- **@ModelAttribute** 어노테이션을 통한 객체 파라미터 바인딩  
- **TypeConverter**를 통한 다양한 타입 변환 지원
- 필수/선택적 파라미터, 기본값 처리
- 배열 파라미터 바인딩 지원

## 🔧 주요 구현 내용

### 1. 파라미터 바인딩 어노테이션

#### @RequestParam 어노테이션
```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";           // 파라미터 이름
    String name() default "";            // value()의 별칭
    boolean required() default true;     // 필수 여부
    String defaultValue() default "";    // 기본값
}
```

#### @ModelAttribute 어노테이션
```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelAttribute {
    String value() default "";   // 모델 이름 (선택적)
}
```

### 2. 타입 변환기 (TypeConverter)

HTTP 요청 파라미터(String)를 다양한 타입으로 변환하는 유틸리티:

```java
public class TypeConverter {
    // 지원하는 타입들
    - 기본 타입: int, long, double, boolean
    - 래퍼 타입: Integer, Long, Double, Boolean  
    - 문자열: String
    - 날짜: LocalDate, LocalDateTime
    - 배열: String[], Integer[] 등
    
    public static <T> T convert(String value, Class<T> targetType)
    public static <T> T convertWithDefault(String value, Class<T> targetType, String defaultValue)
}
```

### 3. 파라미터 리졸버 (ParameterResolver)

메서드 파라미터 타입에 따라 적절한 값을 주입하는 전략 패턴:

```java
public class ParameterResolver {
    public Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response)
    
    // 처리 전략
    - HttpRequest/HttpResponse 주입
    - @RequestParam 개별 파라미터 바인딩  
    - @ModelAttribute 객체 바인딩
}
```

### 4. 모델 속성 바인더 (ModelAttributeBinder)

HTTP 요청 파라미터를 Java 객체로 자동 바인딩:

```java
public class ModelAttributeBinder {
    public static <T> T bind(HttpRequest request, Class<T> clazz)
    
    // 바인딩 과정
    1. 객체 생성 (기본 생성자 호출)
    2. setter 메서드 탐지
    3. 파라미터 이름 매칭 (setName → name)
    4. 타입 변환 후 setter 호출
}
```

## 📝 사용 예시

### 1. @RequestParam 기본 사용

```java
@RequestMapping(value = "/search", method = "GET")
public ModelAndView search(
    @RequestParam("keyword") String keyword,
    @RequestParam(value = "page", defaultValue = "1") int page
) {
    // keyword는 필수, page는 기본값 1
}
```

### 2. @RequestParam 고급 사용

```java
@RequestMapping(value = "/search/advanced", method = "GET") 
public ModelAndView advancedSearch(
    @RequestParam("keyword") String keyword,
    @RequestParam(value = "category", defaultValue = "all") String category,
    @RequestParam(value = "minPrice", required = false) Integer minPrice,
    @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
    @RequestParam(value = "sortBy", defaultValue = "relevance") String sortBy
) {
    // 필수/선택적 파라미터 조합
}
```

### 3. @ModelAttribute 객체 바인딩

```java
@RequestMapping(value = "/search/form", method = "POST")
public ModelAndView searchForm(
    @ModelAttribute SearchForm form,
    HttpResponse response
) {
    // SearchForm 객체로 자동 바인딩
    if (!form.isValid()) {
        // 유효성 검증 실패 처리
    }
}
```

### 4. 배열 파라미터 바인딩

```java
@RequestMapping(value = "/search/tags", method = "GET")
public ModelAndView searchByTags(
    @RequestParam("tags") String[] tags,
    @RequestParam(value = "includeAll", defaultValue = "false") boolean includeAll
) {
    // tags=java,spring,mvc → String[] {"java", "spring", "mvc"}
}
```

## 🛠 기술적 특징

### 1. 타입 안전성
- 컴파일 타임에 타입 체크
- 런타임 타입 변환 오류 처리
- 기본값과 null 처리

### 2. 유연한 파라미터 처리  
- 필수/선택적 파라미터
- 기본값 지원
- 배열 파라미터 자동 분할

### 3. 객체 바인딩
- setter 기반 자동 바인딩
- 중첩 객체 지원 가능
- 유효성 검증 통합

### 4. 확장성
- 새로운 타입 변환기 추가 가능
- 커스텀 바인딩 로직 확장
- 어노테이션 속성 확장

## 📊 테스트 시나리오

WinterMain에서 11가지 파라미터 바인딩 시나리오를 테스트:

1. **기본 @RequestParam** - 필수 파라미터 바인딩
2. **기본값 사용** - defaultValue 속성 테스트  
3. **복합 파라미터** - 여러 @RequestParam 조합
4. **선택적 파라미터** - required=false 테스트
5. **@ModelAttribute** - 객체 바인딩 테스트
6. **유효성 검증** - 바인딩 실패 처리
7. **혼합 파라미터** - @RequestParam + @ModelAttribute
8. **배열 파라미터** - String[] 바인딩
9. **타입 변환** - double, boolean 변환
10. **필수 파라미터 누락** - 오류 처리
11. **JSON 응답** - 파라미터 바인딩 + JSON 출력

## 🔗 연관된 컴포넌트

- **AnnotationHandlerAdapter**: 파라미터 바인딩 통합
- **ParameterResolver**: 파라미터 해결 전략
- **TypeConverter**: 타입 변환 엔진
- **ModelAttributeBinder**: 객체 바인딩 엔진
- **SearchController**: 테스트용 컨트롤러
- **SearchForm**: 바인딩 대상 DTO

## 🎉 23단계 완성 효과

- **생산성 향상**: 수동 파라미터 추출 → 자동 바인딩
- **타입 안전성**: 컴파일 타임 타입 체크
- **코드 간소화**: 보일러플레이트 코드 제거
- **유지보수성**: 어노테이션 기반 명시적 설정
- **확장성**: 새로운 타입과 바인딩 전략 추가 용이

23단계를 통해 Winter Framework는 Spring MVC와 유사한 강력하고 유연한 파라미터 바인딩 시스템을 갖추게 되었습니다!

## 🚀 다음 단계 예고

24단계에서는 **Validation Framework**를 구현하여 바인딩된 객체의 유효성 검증을 자동화할 예정입니다.