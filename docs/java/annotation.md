# 📘 Java 어노테이션 라이브러리 가이드

## 🎯 어노테이션이란?

**어노테이션(Annotation)**은 Java 5부터 도입된 기능으로, 코드에 **메타데이터(metadata)**를 추가하는 방법입니다. 컴파일러나 런타임에 특별한 처리를 하도록 지시하는 **주석의 확장 형태**입니다.

---

## 🔧 주요 메타 어노테이션

### 1. @Target
**적용 대상을 지정하는 어노테이션**

```java
@Target(ElementType.TYPE)      // 클래스, 인터페이스, 열거형
@Target(ElementType.METHOD)    // 메서드
@Target(ElementType.FIELD)     // 필드
@Target(ElementType.PARAMETER) // 파라미터
@Target(ElementType.CONSTRUCTOR) // 생성자
@Target(ElementType.LOCAL_VARIABLE) // 지역 변수
@Target(ElementType.ANNOTATION_TYPE) // 어노테이션 타입
@Target(ElementType.PACKAGE)   // 패키지

// 여러 타입 지정 가능
@Target({ElementType.TYPE, ElementType.METHOD})
```

### 2. @Retention
**어노테이션 정보의 유지 범위를 지정**

```java
@Retention(RetentionPolicy.SOURCE)   // 소스 코드까지만 (컴파일 후 제거)
@Retention(RetentionPolicy.CLASS)    // 클래스 파일까지 (런타임에는 없음) - 기본값
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 유지 (리플렉션으로 접근 가능)
```

**예시**:
- `@Override` → SOURCE (컴파일러 검사용)
- `@Deprecated` → RUNTIME (실행 중에도 확인 가능)
- `@Controller` → RUNTIME (Spring이 런타임에 스캔)

### 3. @Documented
**JavaDoc에 포함시킬지 결정**

```java
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    // 이 어노테이션은 JavaDoc에 포함됨
}
```

### 4. @Inherited
**상속 가능 여부 결정**

```java
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritableAnnotation {
}

@InheritableAnnotation
class Parent { }

class Child extends Parent { } // Child도 @InheritableAnnotation을 가짐
```

### 5. @Repeatable
**같은 어노테이션을 반복 사용 가능**

```java
@Repeatable(Schedules.class)
@interface Schedule {
    String dayOfWeek();
}

@interface Schedules {
    Schedule[] value();
}

// 사용
@Schedule(dayOfWeek="Monday")
@Schedule(dayOfWeek="Tuesday")
public void doWork() { }
```

---

## 📝 어노테이션 정의 방법

### 기본 구문
```java
public @interface MyAnnotation {
    // 속성들 정의
}
```

### 속성(Element) 정의
```java
public @interface RequestMapping {
    String value();                    // 필수 속성
    String method() default "";        // 기본값이 있는 속성
    String[] headers() default {};     // 배열 타입
    int timeout() default 5000;        // 숫자 타입
    Class<?> targetClass() default Object.class; // 클래스 타입
}
```

### 속성 타입 제한
어노테이션 속성으로 사용 가능한 타입:
- **기본 타입**: `int`, `boolean`, `char`, `byte`, `short`, `long`, `float`, `double`
- **String**
- **Class 타입**
- **enum 타입**
- **다른 어노테이션 타입**
- **위 타입들의 배열**

---

## 🚀 실제 사용 예시

### 1. 간단한 어노테이션
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
}

// 사용
@Test
public void myTestMethod() {
    // 테스트 코드
}
```

### 2. 속성이 있는 어노테이션
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String tableName() default "";
    boolean readOnly() default false;
}

// 사용
@Entity(tableName = "users", readOnly = true)
public class User {
    // ...
}
```

### 3. value 속성 특례
```java
public @interface Component {
    String value() default "";  // value라는 이름의 속성
}

// 사용 - value는 속성명 생략 가능
@Component("userService")        // value="userService"와 동일
@Component(value="userService")  // 명시적 작성
```

---

## 🔍 리플렉션으로 어노테이션 읽기

### 클래스 어노테이션 확인
```java
Class<?> clazz = MyController.class;

// 어노테이션 존재 확인
if (clazz.isAnnotationPresent(Controller.class)) {
    System.out.println("이 클래스는 컨트롤러입니다.");
}

// 어노테이션 정보 가져오기
Controller controller = clazz.getAnnotation(Controller.class);
if (controller != null) {
    String value = controller.value();
    System.out.println("Controller value: " + value);
}

// 모든 어노테이션 가져오기
Annotation[] annotations = clazz.getAnnotations();
for (Annotation annotation : annotations) {
    System.out.println("어노테이션: " + annotation.annotationType().getName());
}
```

### 메서드 어노테이션 확인
```java
Method[] methods = clazz.getDeclaredMethods();
for (Method method : methods) {
    if (method.isAnnotationPresent(RequestMapping.class)) {
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        System.out.println("URL: " + mapping.value());
        System.out.println("HTTP Method: " + mapping.method());
    }
}
```

### 필드 어노테이션 확인
```java
Field[] fields = clazz.getDeclaredFields();
for (Field field : fields) {
    if (field.isAnnotationPresent(Autowired.class)) {
        System.out.println("자동 주입 필드: " + field.getName());
    }
}
```

---

## 🌟 주요 내장 어노테이션

### 컴파일러 관련
```java
@Override           // 메서드 오버라이드 검증
@Deprecated         // 사용 중단 경고
@SuppressWarnings   // 경고 억제
@SafeVarargs        // 가변인수 안전성 보장
@FunctionalInterface // 함수형 인터페이스 표시
```

### 사용 예시
```java
public class Example {
    
    @Override  // 컴파일러가 오버라이드 검증
    public String toString() {
        return "Example";
    }
    
    @Deprecated  // 사용 중단 권고
    public void oldMethod() {
        // 레거시 메서드
    }
    
    @SuppressWarnings("unchecked")  // unchecked 경고 억제
    public void dangerousMethod() {
        List rawList = new ArrayList();
        rawList.add("test");
    }
}
```

---

## 💡 어노테이션 사용 패턴

### 1. 마커 어노테이션 (Marker Annotation)
속성이 없는 단순 표시용
```java
@Test
public void testMethod() { }
```

### 2. 단일값 어노테이션 (Single-Value Annotation)
value 속성만 있는 경우
```java
@Component("userService")
```

### 3. 다중값 어노테이션 (Multi-Value Annotation)
여러 속성을 가지는 경우
```java
@RequestMapping(value="/users", method="GET", headers="Content-Type=application/json")
```

---

## 🔧 실제 프레임워크 예시

### Spring 어노테이션
```java
@Controller                     // 컨트롤러 표시
@Service                       // 서비스 계층 표시
@Repository                    // 데이터 접근 계층 표시
@Component                     // 일반 컴포넌트 표시
@Autowired                     // 의존성 자동 주입
@RequestMapping("/api/users")   // URL 매핑
@GetMapping("/users")          // GET 요청 매핑
@PostMapping("/users")         // POST 요청 매핑
```

### JPA 어노테이션
```java
@Entity                        // 엔티티 클래스 표시
@Table(name="users")           // 테이블 매핑
@Id                           // 기본키 표시
@GeneratedValue               // 자동 생성값
@Column(name="user_name")     // 컬럼 매핑
```

### Validation 어노테이션
```java
@NotNull                      // null 불허
@NotEmpty                     // 빈 값 불허
@Size(min=2, max=30)         // 크기 제한
@Email                        // 이메일 형식 검증
@Pattern(regexp="[0-9]+")    // 정규식 검증
```

---

## 🎯 어노테이션의 장점

1. **코드의 가독성 향상**: 설정과 코드가 함께 위치
2. **컴파일 타임 검증**: 타입 안전성 보장
3. **런타임 처리**: 리플렉션을 통한 동적 처리
4. **재사용성**: 공통 메타데이터를 어노테이션으로 추상화
5. **유지보수성**: XML 설정 파일 대신 코드 내 표현

---

## ⚠️ 주의사항

1. **성능**: 리플렉션 사용으로 인한 성능 오버헤드
2. **복잡성**: 과도한 어노테이션 사용 시 코드 복잡도 증가
3. **디버깅**: 런타임 처리로 인한 디버깅 어려움
4. **의존성**: 어노테이션에 의존적인 코드 구조

---

## 📚 관련 클래스 및 인터페이스

```java
// 주요 패키지
java.lang.annotation.*

// 핵심 클래스/인터페이스
Annotation              // 모든 어노테이션의 슈퍼 인터페이스
ElementType            // @Target에서 사용하는 열거형
RetentionPolicy        // @Retention에서 사용하는 열거형
AnnotatedElement       // 어노테이션을 가질 수 있는 요소들의 인터페이스

// 리플렉션 관련
Class.isAnnotationPresent()
Class.getAnnotation()
Method.getAnnotations()
Field.getDeclaredAnnotations()
```