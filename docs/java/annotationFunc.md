# 📘 어노테이션 함수와 Map 구조 설명

## 🎯 어노테이션의 함수들 (속성들)

### 1. value() 함수
```java
@RequestParam("keyword")  // value = "keyword"
@RequestParam(value = "keyword")  // 명시적 작성
```

**역할**:
- 어노테이션의 **주요 값**을 지정하는 특별한 속성
- 어노테이션에서 가장 중요한 정보를 담는 **기본 속성**

**특징**:
- `value`라는 이름의 속성은 **이름을 생략**할 수 있음
- `@RequestParam("keyword")`와 `@RequestParam(value="keyword")`는 **완전히 동일**
- 다른 속성과 함께 사용할 때는 반드시 **명시적으로 작성**해야 함

**실제 예시**:
```java
// value만 사용 - 이름 생략 가능
@RequestParam("username")
@Component("userService")
@RequestMapping("/users")

// value와 다른 속성 함께 사용 - 반드시 명시적 작성
@RequestParam(value = "page", defaultValue = "1")
@RequestMapping(value = "/users", method = "GET")
```

### 2. required() 함수
```java
@RequestParam(value = "page", required = false)  // 선택적 파라미터
@RequestParam(value = "name", required = true)   // 필수 파라미터 (기본값)
```

**역할**:
- 해당 파라미터가 **필수인지 선택적인지** 결정
- HTTP 요청에 파라미터가 없을 때의 **동작을 제어**

**동작 방식**:
- `required = true` (기본값): 파라미터가 없으면 **예외 발생**
- `required = false`: 파라미터가 없어도 **정상 처리** (null 전달)

**실제 예시**:
```java
public ModelAndView search(
    @RequestParam("keyword") String keyword,           // required=true (기본값) - 필수
    @RequestParam(value="page", required=false) Integer page  // 선택적 - 없어도 OK
) {
    // keyword가 없으면 예외 발생
    // page가 없으면 null이 전달됨
}
```

### 3. defaultValue() 함수
```java
@RequestParam(value = "page", defaultValue = "1")    // 기본값 "1"
@RequestParam(value = "size", defaultValue = "20")   // 기본값 "20"
```

**역할**:
- 파라미터가 없을 때 사용할 **기본값 지정**
- `required = false`와 함께 사용하여 **안전한 기본값** 제공

**동작 방식**:
- 파라미터가 없거나 빈 문자열일 때 **defaultValue 사용**
- defaultValue가 있으면 **자동으로 required = false**가 됨
- 문자열로 지정하고, **타입 변환기가 적절한 타입으로 변환**

**실제 예시**:
```java
public ModelAndView search(
    @RequestParam("keyword") String keyword,                    // 필수
    @RequestParam(value="page", defaultValue="1") int page,     // 기본값 1
    @RequestParam(value="size", defaultValue="20") int size     // 기본값 20
) {
    // /search?keyword=spring → page=1, size=20 (기본값 사용)
    // /search?keyword=spring&page=3 → page=3, size=20
    // /search?keyword=spring&page=3&size=50 → page=3, size=50
}
```

---

## 🗂 Map 구조 설명

### Map<Class<?>, Function<String, Object>> 분석

```java
private static final Map<Class<?>, Function<String, Object>> CONVERTERS = new HashMap<>();
```

### 구조 분해:
- **Key**: `Class<?>` → **타입 정보** (String.class, Integer.class, Boolean.class 등)
- **Value**: `Function<String, Object>` → **변환 함수** (String을 받아서 Object를 반환)

### 실제 저장 내용:
```java
// 이런 식으로 저장됨:
CONVERTERS.put(String.class, value -> value);              // String은 그대로
CONVERTERS.put(Integer.class, Integer::parseInt);          // String → Integer 변환
CONVERTERS.put(Boolean.class, Boolean::parseBoolean);      // String → Boolean 변환
CONVERTERS.put(Double.class, Double::parseDouble);         // String → Double 변환
```

### 동작 원리:
```java
// 1. 변환이 필요할 때
String inputValue = "123";
Class<?> targetType = Integer.class;

// 2. Map에서 변환 함수 찾기
Function<String, Object> converter = CONVERTERS.get(targetType);
// converter = Integer::parseInt 함수가 반환됨

// 3. 변환 함수 실행
Object result = converter.apply(inputValue);
// result = 123 (Integer 객체)
```

### 왜 이런 구조를 사용하는가?

#### 1. **전략 패턴 (Strategy Pattern)**
```java
// 타입마다 다른 변환 전략을 사용
if (targetType == Integer.class) {
    return Integer.parseInt(value);     // 정수 변환 전략
} else if (targetType == Boolean.class) {
    return Boolean.parseBoolean(value); // 불린 변환 전략
} else if (targetType == Double.class) {
    return Double.parseDouble(value);   // 실수 변환 전략
}

// 위 코드를 Map으로 깔끔하게 대체:
Function<String, Object> converter = CONVERTERS.get(targetType);
return converter.apply(value);
```

#### 2. **확장성**
```java
// 새로운 타입 추가가 쉬움
CONVERTERS.put(LocalDate.class, value -> LocalDate.parse(value));
CONVERTERS.put(BigDecimal.class, BigDecimal::new);
CONVERTERS.put(URL.class, URL::new);

// if-else 체인을 늘릴 필요 없음!
```

#### 3. **성능**
```java
// Map 조회는 O(1) - 매우 빠름
Function<String, Object> converter = CONVERTERS.get(targetType);  // 빠른 조회

// vs if-else 체인은 O(n) - 타입이 많아질수록 느려짐
if (type == String.class) { ... }
else if (type == Integer.class) { ... }  
else if (type == Boolean.class) { ... }
// ... 20개, 30개 계속...
```

### Function<String, Object> 설명

#### Function 인터페이스:
```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);  // T 타입을 받아서 R 타입을 반환
}
```

#### 우리 경우:
```java
Function<String, Object>
//       ↑      ↑
//    입력타입  출력타입

// 의미: String을 받아서 Object를 반환하는 함수
```

#### 실제 사용 예시:
```java
// 람다식으로 함수 정의
Function<String, Object> intConverter = value -> Integer.parseInt(value);

// 메서드 참조로 함수 정의  
Function<String, Object> boolConverter = Boolean::parseBoolean;

// 함수 실행
Object intResult = intConverter.apply("123");    // 123 (Integer)
Object boolResult = boolConverter.apply("true"); // true (Boolean)
```

---

## 🔄 전체 흐름 예시

### 1. 사용자 요청:
```
GET /search?keyword=spring&page=3&active=true
```

### 2. 메서드 정의:
```java
public ModelAndView search(
    @RequestParam("keyword") String keyword,
    @RequestParam(value="page", defaultValue="1") int page,
    @RequestParam(value="active", defaultValue="false") boolean active
) { ... }
```

### 3. 파라미터 처리 과정:

#### keyword 파라미터:
```java
// 1. @RequestParam("keyword") 분석
String paramName = "keyword";
String paramValue = request.getParameter("keyword");  // "spring"
Class<?> targetType = String.class;

// 2. 타입 변환
Function<String, Object> converter = CONVERTERS.get(String.class);
// converter = value -> value (그대로 반환)
Object result = converter.apply("spring");  // "spring"
```

#### page 파라미터:
```java
// 1. @RequestParam(value="page", defaultValue="1") 분석
String paramName = "page";
String paramValue = request.getParameter("page");  // "3"
Class<?> targetType = int.class;
String defaultValue = "1";

// 2. 타입 변환
Function<String, Object> converter = CONVERTERS.get(int.class);
// converter = Integer::parseInt
Object result = converter.apply("3");  // 3 (int)
```

#### active 파라미터:
```java
// 1. @RequestParam(value="active", defaultValue="false") 분석
String paramName = "active";
String paramValue = request.getParameter("active");  // "true"
Class<?> targetType = boolean.class;
String defaultValue = "false";

// 2. 타입 변환
Function<String, Object> converter = CONVERTERS.get(boolean.class);
// converter = Boolean::parseBoolean
Object result = converter.apply("true");  // true (boolean)
```

### 4. 최종 메서드 호출:
```java
search("spring", 3, true);
```

이렇게 Map과 Function을 조합하여 **깔끔하고 확장 가능한 타입 변환 시스템**을 구현한 것입니다! 🎯