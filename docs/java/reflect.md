# 📘 Java 리플렉션 라이브러리 가이드

## 🎯 리플렉션이란?

**리플렉션(Reflection)**은 실행 중인 Java 프로그램이 **자기 자신을 검사하고 조작**할 수 있는 기능입니다. 클래스의 구조, 메서드, 필드 등을 **런타임에 동적으로** 접근하고 조작할 수 있습니다.

---

## 🔧 주요 클래스 및 인터페이스

### 1. Class<?> 클래스
**모든 클래스와 인터페이스의 메타데이터를 담고 있는 클래스**

```java
// Class 객체 얻는 방법
Class<?> clazz1 = String.class;                    // 리터럴 사용
Class<?> clazz2 = "Hello".getClass();              // 객체에서 얻기
Class<?> clazz3 = Class.forName("java.lang.String"); // 이름으로 찾기

// 기본 정보 얻기
String className = clazz1.getName();           // "java.lang.String"
String simpleName = clazz1.getSimpleName();    // "String"
Package pkg = clazz1.getPackage();             // java.lang 패키지
```

### 2. Method 클래스
**메서드에 대한 정보와 실행 기능을 제공**

```java
Class<?> clazz = String.class;

// 모든 public 메서드 가져오기
Method[] publicMethods = clazz.getMethods();

// 선언된 모든 메서드 가져오기 (private 포함)
Method[] allMethods = clazz.getDeclaredMethods();

// 특정 메서드 찾기
Method lengthMethod = clazz.getMethod("length");                    // 파라미터 없는 메서드
Method subMethod = clazz.getMethod("substring", int.class, int.class); // 파라미터 있는 메서드

// 메서드 정보 확인
String methodName = lengthMethod.getName();                    // "length"
Class<?> returnType = lengthMethod.getReturnType();            // int.class
Class<?>[] paramTypes = subMethod.getParameterTypes();         // [int.class, int.class]
int modifiers = lengthMethod.getModifiers();                   // public, static 등
```

### 3. Field 클래스
**필드(변수)에 대한 정보와 접근 기능을 제공**

```java
Class<?> clazz = MyClass.class;

// 모든 public 필드 가져오기
Field[] publicFields = clazz.getFields();

// 선언된 모든 필드 가져오기 (private 포함)
Field[] allFields = clazz.getDeclaredFields();

// 특정 필드 찾기
Field nameField = clazz.getDeclaredField("name");

// 필드 정보 확인
String fieldName = nameField.getName();        // "name"
Class<?> fieldType = nameField.getType();      // String.class
int modifiers = nameField.getModifiers();      // private, final 등
```

### 4. Constructor 클래스
**생성자에 대한 정보와 실행 기능을 제공**

```java
Class<?> clazz = String.class;

// 모든 public 생성자 가져오기
Constructor<?>[] constructors = clazz.getConstructors();

// 특정 생성자 찾기
Constructor<?> constructor = clazz.getConstructor(String.class); // String(String) 생성자

// 생성자 정보 확인
Class<?>[] paramTypes = constructor.getParameterTypes(); // [String.class]
```

---

## 🚀 Method.invoke() 상세 가이드

### 기본 사용법
```java
Method method = ...;  // 메서드 객체
Object result = method.invoke(instance, arguments...);
```

### 파라미터 설명
- **첫 번째 파라미터**: 메서드를 호출할 **객체 인스턴스**
    - static 메서드인 경우 `null` 전달
- **나머지 파라미터**: 메서드에 전달할 **인수들**
    - 메서드 파라미터 순서와 동일하게 전달

### 실제 예시

#### 1. 파라미터 없는 메서드 호출
```java
String str = "Hello World";
Method lengthMethod = String.class.getMethod("length");

// str.length() 호출과 동일
int length = (int) lengthMethod.invoke(str);  // 11
System.out.println("Length: " + length);
```

#### 2. 파라미터 있는 메서드 호출
```java
String str = "Hello World";
Method subMethod = String.class.getMethod("substring", int.class, int.class);

// str.substring(0, 5) 호출과 동일
String result = (String) subMethod.invoke(str, 0, 5);  // "Hello"
System.out.println("Substring: " + result);
```

#### 3. static 메서드 호출
```java
Method parseIntMethod = Integer.class.getMethod("parseInt", String.class);

// Integer.parseInt("123") 호출과 동일
int number = (int) parseIntMethod.invoke(null, "123");  // 123
System.out.println("Parsed: " + number);
```

#### 4. void 메서드 호출
```java
List<String> list = new ArrayList<>();
Method addMethod = List.class.getMethod("add", Object.class);

// list.add("Hello") 호출과 동일
addMethod.invoke(list, "Hello");  // 반환값은 Boolean이지만 무시 가능
System.out.println("List: " + list);  // [Hello]
```

#### 5. 예외 처리
```java
try {
    Method method = String.class.getMethod("charAt", int.class);
    char ch = (char) method.invoke("Hello", 10);  // StringIndexOutOfBoundsException 발생
} catch (InvocationTargetException e) {
    // 메서드 실행 중 발생한 예외는 InvocationTargetException으로 래핑됨
    Throwable cause = e.getCause();  // 실제 예외 (StringIndexOutOfBoundsException)
    System.out.println("실제 예외: " + cause.getClass().getSimpleName());
}
```

---

## 💡 리플렉션 활용 패턴

### 1. 동적 객체 생성
```java
// 클래스 이름으로 객체 생성
String className = "java.util.ArrayList";
Class<?> clazz = Class.forName(className);
Object instance = clazz.getDeclaredConstructor().newInstance();

// 파라미터가 있는 생성자 사용
Constructor<?> constructor = String.class.getConstructor(String.class);
String str = (String) constructor.newInstance("Hello");
```

### 2. 어노테이션 기반 처리
```java
Class<?> clazz = MyController.class;

// @Controller 어노테이션이 있는지 확인
if (clazz.isAnnotationPresent(Controller.class)) {
    Controller controller = clazz.getAnnotation(Controller.class);
    String value = controller.value();
    
    // @RequestMapping이 있는 메서드들 찾기
    for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            String url = mapping.value();
            String httpMethod = mapping.method();
            
            // 메서드 실행
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            ModelAndView result = (ModelAndView) method.invoke(controllerInstance, request, response);
        }
    }
}
```

### 3. 필드 접근 및 수정
```java
public class Person {
    private String name;
    private int age;
    
    // getter/setter 생략
}

// private 필드에 접근
Person person = new Person();
Field nameField = Person.class.getDeclaredField("name");
nameField.setAccessible(true);  // private 필드 접근 허용

// 필드 값 설정
nameField.set(person, "John");

// 필드 값 읽기
String name = (String) nameField.get(person);
System.out.println("Name: " + name);  // "John"
```

### 4. 메서드 파라미터 타입 분석
```java
Method method = MyController.class.getMethod("handleRequest", HttpRequest.class, HttpResponse.class);

Class<?>[] paramTypes = method.getParameterTypes();
for (int i = 0; i < paramTypes.length; i++) {
    Class<?> paramType = paramTypes[i];
    System.out.println("Parameter " + i + ": " + paramType.getName());
    
    if (paramType.equals(HttpRequest.class)) {
        // HttpRequest 타입 파라미터 처리
    } else if (paramType.equals(HttpResponse.class)) {
        // HttpResponse 타입 파라미터 처리
    }
}
```

---

## 🔍 실제 프레임워크에서의 사용

### 1. Spring Framework
```java
// Spring이 @Autowired를 처리하는 방식
Field[] fields = clazz.getDeclaredFields();
for (Field field : fields) {
    if (field.isAnnotationPresent(Autowired.class)) {
        field.setAccessible(true);
        Object dependency = getBeanFromContainer(field.getType());
        field.set(instance, dependency);  // 의존성 주입
    }
}
```

### 2. JPA (Hibernate)
```java
// JPA가 @Entity를 처리하는 방식
if (clazz.isAnnotationPresent(Entity.class)) {
    Entity entity = clazz.getAnnotation(Entity.class);
    String tableName = entity.name();
    
    // @Column 어노테이션이 있는 필드들 찾기
    for (Field field : clazz.getDeclaredFields()) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            String columnName = column.name();
            // SQL 매핑 정보 생성
        }
    }
}
```

### 3. JSON 직렬화 (Jackson, Gson)
```java
// 객체를 JSON으로 변환하는 방식
Object obj = new User("John", 25);
Class<?> clazz = obj.getClass();

StringBuilder json = new StringBuilder("{");
Field[] fields = clazz.getDeclaredFields();

for (Field field : fields) {
    field.setAccessible(true);
    String fieldName = field.getName();
    Object fieldValue = field.get(obj);
    
    json.append("\"").append(fieldName).append("\":")
        .append("\"").append(fieldValue).append("\",");
}
json.append("}");
```

---

## ⚠️ 주의사항 및 제한사항

### 1. 성능 오버헤드
```java
// 느린 방식 (매번 리플렉션)
Method method = clazz.getMethod("getValue");  // 느림
Object result = method.invoke(instance);      // 느림

// 빠른 방식 (리플렉션 결과 캐싱)
private static final Method CACHED_METHOD = clazz.getMethod("getValue");
Object result = CACHED_METHOD.invoke(instance);  // 상대적으로 빠름
```

### 2. 보안 제한
```java
// SecurityManager가 있는 환경에서는 제한될 수 있음
Field privateField = clazz.getDeclaredField("privateData");
privateField.setAccessible(true);  // SecurityException 발생 가능
```

### 3. 타입 안전성 부족
```java
// 컴파일 타임에 타입 체크가 안됨
Method method = clazz.getMethod("wrongMethod");  // 런타임에 NoSuchMethodException
Object result = method.invoke(instance, "wrong type");  // 런타임에 IllegalArgumentException
```

### 4. 예외 처리
```java
try {
    Method method = clazz.getMethod("methodName");
    Object result = method.invoke(instance);
} catch (NoSuchMethodException e) {
    // 메서드를 찾을 수 없음
} catch (IllegalAccessException e) {
    // 접근 권한 없음
} catch (InvocationTargetException e) {
    // 메서드 실행 중 예외 발생
    Throwable actualException = e.getCause();
}
```

---

## 🎯 리플렉션 사용 시기

### ✅ 사용하기 좋은 경우
- **프레임워크 개발**: Spring, Hibernate 등
- **라이브러리 개발**: JSON 파서, ORM 등
- **플러그인 시스템**: 동적 클래스 로딩
- **어노테이션 기반 처리**: 설정, 검증 등
- **테스트 코드**: private 메서드/필드 테스트

### ❌ 피해야 하는 경우
- **일반적인 비즈니스 로직**: 직접 메서드 호출이 더 좋음
- **성능이 중요한 코드**: 오버헤드가 큼
- **간단한 작업**: 리플렉션이 과한 경우

---

## 📚 주요 메서드 요약

### Class 클래스
```java
Class.forName(String className)          // 클래스 이름으로 Class 객체 얻기
clazz.getName()                          // 클래스 풀네임
clazz.getSimpleName()                    // 클래스 단순명
clazz.newInstance()                      // 기본 생성자로 인스턴스 생성 (deprecated)
clazz.getDeclaredConstructor().newInstance() // 권장되는 인스턴스 생성
clazz.isAnnotationPresent(Annotation.class) // 어노테이션 존재 확인
clazz.getAnnotation(Annotation.class)    // 어노테이션 객체 얻기
```

### Method 클래스
```java
method.invoke(instance, args...)         // 메서드 실행
method.getName()                         // 메서드 이름
method.getReturnType()                   // 반환 타입
method.getParameterTypes()               // 파라미터 타입들
method.getModifiers()                    // 수식어 (public, static 등)
method.setAccessible(true)               // 접근 권한 무시
```

### Field 클래스
```java
field.get(instance)                      // 필드 값 읽기
field.set(instance, value)               // 필드 값 설정
field.getName()                          // 필드 이름
field.getType()                          // 필드 타입
field.setAccessible(true)                // 접근 권한 무시
```