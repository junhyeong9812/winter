# 📘 Java 제네릭과 Class<?> 가이드

## 🎯 제네릭이란?

**제네릭(Generics)**은 Java 5부터 도입된 기능으로, **타입을 파라미터화**하여 컴파일 타임에 타입 안전성을 보장하는 기능입니다. 클래스, 인터페이스, 메서드에서 사용할 타입을 **미리 지정하지 않고** 나중에 결정할 수 있게 해줍니다.

---

## 🔧 기본 제네릭 문법

### 1. 기본 형태
```java
// 제네릭 클래스 정의
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// 사용
Box<String> stringBox = new Box<String>();
Box<Integer> intBox = new Box<Integer>();
Box<List<String>> listBox = new Box<List<String>>();  // 중첩 제네릭
```

### 2. 여러 타입 파라미터
```java
public class Pair<T, U> {
    private T first;
    private U second;
    
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
    
    public T getFirst() { return first; }
    public U getSecond() { return second; }
}

// 사용
Pair<String, Integer> nameAge = new Pair<>("John", 25);
Pair<Date, String> dateEvent = new Pair<>(new Date(), "Meeting");
```

### 3. 제네릭 메서드
```java
public class Utility {
    // 제네릭 메서드 정의
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    // 여러 타입 파라미터
    public static <T, U> Pair<T, U> makePair(T first, U second) {
        return new Pair<>(first, second);
    }
}

// 사용
String[] names = {"Alice", "Bob", "Charlie"};
Utility.swap(names, 0, 2);  // 타입 추론으로 <String> 생략 가능
Utility.<String>swap(names, 0, 2);  // 명시적 타입 지정도 가능
```

---

## 🌟 와일드카드 (?)

### 1. 언바운드 와일드카드 (?)
**어떤 타입이든 허용**
```java
List<?> unknownList;        // 어떤 타입의 List든 가능
unknownList = new ArrayList<String>();
unknownList = new ArrayList<Integer>();
unknownList = new ArrayList<Object>();

// 읽기만 가능, 쓰기는 제한적
Object item = unknownList.get(0);  // OK - Object로 읽기
// unknownList.add("hello");      // 컴파일 에러 - 타입을 모르므로 추가 불가
unknownList.add(null);            // OK - null은 어떤 타입에든 추가 가능
```

### 2. 상한 경계 와일드카드 (? extends Type)
**특정 타입이거나 그 하위 타입만 허용**
```java
List<? extends Number> numbers;
numbers = new ArrayList<Integer>();    // OK - Integer는 Number의 하위타입
numbers = new ArrayList<Double>();     // OK - Double도 Number의 하위타입
// numbers = new ArrayList<String>(); // 컴파일 에러 - String은 Number의 하위타입이 아님

// 읽기는 가능, 쓰기는 제한
Number num = numbers.get(0);          // OK - Number로 읽기
// numbers.add(10);                  // 컴파일 에러 - 정확한 타입을 모르므로 추가 불가
```

### 3. 하한 경계 와일드카드 (? super Type)
**특정 타입이거나 그 상위 타입만 허용**
```java
List<? super Integer> numbers;
numbers = new ArrayList<Integer>();    // OK
numbers = new ArrayList<Number>();     // OK - Number는 Integer의 상위타입
numbers = new ArrayList<Object>();     // OK - Object는 Integer의 상위타입
// numbers = new ArrayList<Double>(); // 컴파일 에러 - Double은 Integer의 상위타입이 아님

// 쓰기는 가능, 읽기는 제한적
numbers.add(10);                      // OK - Integer 추가
numbers.add(new Integer(20));         // OK
Object obj = numbers.get(0);          // OK - Object로만 읽기 가능
// Integer num = numbers.get(0);     // 컴파일 에러 - 정확한 타입을 모름
```

---

## 🎯 Class<?> 상세 가이드

### 1. Class<?>의 의미
```java
Class<?>                              // 어떤 타입의 Class 객체든 허용
Class<? extends Object>               // 위와 동일한 의미
Class<Object>                         // Object 타입의 Class 객체만 허용
```

### 2. 실제 사용 예시
```java
// 다양한 타입의 Class 객체를 받을 수 있음
public void processClass(Class<?> clazz) {
    String name = clazz.getName();           // 클래스 이름 출력
    Method[] methods = clazz.getMethods();   // 메서드들 가져오기
    
    // 어떤 타입이든 처리 가능
    System.out.println("Processing class: " + name);
}

// 사용
processClass(String.class);     // OK
processClass(Integer.class);    // OK
processClass(List.class);       // OK
processClass(MyClass.class);    // OK
```

### 3. 구체적인 타입 vs 와일드카드
```java
// 구체적인 타입 지정
Class<String> stringClass = String.class;
Class<Integer> intClass = Integer.class;

// 와일드카드 사용 - 유연함
Class<?> anyClass = String.class;
anyClass = Integer.class;        // OK - 다른 타입으로 재할당 가능
anyClass = List.class;           // OK

// 제한된 와일드카드
Class<? extends Number> numberClass = Integer.class;  // OK
numberClass = Double.class;      // OK
// numberClass = String.class;  // 컴파일 에러
```

### 4. 제네릭 타입 정보 보존
```java
// 런타임에 제네릭 타입 정보는 소거됨 (Type Erasure)
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

Class<?> class1 = stringList.getClass();
Class<?> class2 = intList.getClass();

System.out.println(class1 == class2);  // true - 둘 다 ArrayList.class
System.out.println(class1.getName());  // "java.util.ArrayList"

// Class<?>를 통해서는 제네릭 타입 정보를 알 수 없음
```

---

## 💡 실제 사용 패턴

### 1. 컬렉션에서의 사용
```java
// 다양한 타입의 Class 객체들을 저장
List<Class<?>> classList = new ArrayList<>();
classList.add(String.class);
classList.add(Integer.class);
classList.add(Date.class);

// 각 클래스에 대해 작업 수행
for (Class<?> clazz : classList) {
    System.out.println("Class: " + clazz.getSimpleName());
    
    // 인스턴스 생성 시도
    try {
        Object instance = clazz.getDeclaredConstructor().newInstance();
        System.out.println("Created instance: " + instance);
    } catch (Exception e) {
        System.out.println("Cannot create instance of " + clazz.getSimpleName());
    }
}
```

### 2. 메서드 파라미터 타입 체크
```java
public void analyzeMethod(Method method) {
    Class<?>[] paramTypes = method.getParameterTypes();
    
    for (int i = 0; i < paramTypes.length; i++) {
        Class<?> paramType = paramTypes[i];
        
        System.out.println("Parameter " + i + ": " + paramType.getName());
        
        // 특정 타입인지 확인
        if (paramType.equals(String.class)) {
            System.out.println("  -> String parameter");
        } else if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
            System.out.println("  -> Integer parameter");
        } else if (HttpRequest.class.isAssignableFrom(paramType)) {
            System.out.println("  -> HttpRequest or its subtype");
        }
    }
}
```

### 3. 어노테이션 처리에서의 활용
```java
public void processControllers(List<Class<?>> classes) {
    for (Class<?> clazz : classes) {
        // @Controller 어노테이션이 있는 클래스만 처리
        if (clazz.isAnnotationPresent(Controller.class)) {
            System.out.println("Found controller: " + clazz.getSimpleName());
            
            // 메서드들 검사
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                    System.out.println("  Mapping: " + mapping.value());
                }
            }
        }
    }
}
```

### 4. 팩토리 패턴에서의 활용
```java
public class ObjectFactory {
    private Map<String, Class<?>> classMap = new HashMap<>();
    
    public void registerClass(String name, Class<?> clazz) {
        classMap.put(name, clazz);
    }
    
    public Object createInstance(String name) throws Exception {
        Class<?> clazz = classMap.get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown class: " + name);
        }
        
        return clazz.getDeclaredConstructor().newInstance();
    }
}

// 사용
ObjectFactory factory = new ObjectFactory();
factory.registerClass("string", String.class);
factory.registerClass("list", ArrayList.class);

Object stringObj = factory.createInstance("string");  // String 인스턴스
Object listObj = factory.createInstance("list");      // ArrayList 인스턴스
```

---

## 🔍 타입 체크와 캐스팅

### 1. 타입 체크
```java
public void checkType(Class<?> clazz) {
    // 기본 타입 체크
    if (clazz == String.class) {
        System.out.println("String type");
    }
    
    // 상속 관계 체크
    if (Number.class.isAssignableFrom(clazz)) {
        System.out.println("Number or its subtype");
    }
    
    // 인터페이스 구현 체크
    if (List.class.isAssignableFrom(clazz)) {
        System.out.println("List implementation");
    }
    
    // 어노테이션 체크
    if (clazz.isAnnotationPresent(Deprecated.class)) {
        System.out.println("Deprecated class");
    }
}
```

### 2. 안전한 캐스팅
```java
public <T> T safeCast(Object obj, Class<T> targetType) {
    if (targetType.isInstance(obj)) {
        return targetType.cast(obj);  // 안전한 캐스팅
    } else {
        throw new ClassCastException("Cannot cast " + obj.getClass() + " to " + targetType);
    }
}

// 사용
Object obj = "Hello";
String str = safeCast(obj, String.class);    // OK
// Integer num = safeCast(obj, Integer.class); // ClassCastException
```

---

## ⚠️ 주의사항

### 1. 타입 소거 (Type Erasure)
```java
// 컴파일 후에는 제네릭 타입 정보가 사라짐
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

// 런타임에는 둘 다 단순히 List
System.out.println(stringList.getClass() == intList.getClass());  // true

// 제네릭 타입 정보를 런타임에 얻으려면 다른 방법 필요
```

### 2. 원시 타입 주의
```java
Class<?> intClass = int.class;          // 원시 타입
Class<?> integerClass = Integer.class;  // 래퍼 타입

System.out.println(intClass == integerClass);  // false - 서로 다름

// 래퍼 타입으로 변환
if (intClass.isPrimitive()) {
    // 원시 타입을 래퍼 타입으로 매핑하는 로직 필요
}
```

### 3. 제네릭 배열 제한
```java
// 제네릭 배열 생성 불가
// List<String>[] arrays = new List<String>[10];  // 컴파일 에러

// 대신 와일드카드 사용
List<?>[] arrays = new List<?>[10];  // OK
arrays[0] = new ArrayList<String>();
arrays[1] = new ArrayList<Integer>();
```

---

## 🎯 왜 Class<?>를 사용하는가?

### 1. 유연성
```java
// Class<?>를 사용하지 않는 경우
public void processStringClass(Class<String> clazz) {
    // String 타입만 처리 가능
}

// Class<?>를 사용하는 경우
public void processAnyClass(Class<?> clazz) {
    // 어떤 타입이든 처리 가능
    String name = clazz.getName();
    Method[] methods = clazz.getMethods();
    // ...
}
```

### 2. 재사용성
```java
// 제네릭을 사용하지 않는 경우
public String getClassName(Class clazz) {  // raw type 경고
    return clazz.getName();
}

// Class<?>를 사용하는 경우
public String getClassName(Class<?> clazz) {  // 타입 안전
    return clazz.getName();
}
```

### 3. 타입 안전성
```java
// raw type 사용 (권장하지 않음)
Class rawClass = String.class;
rawClass = Integer.class;  // 경고 없이 할당

// 와일드카드 사용 (권장)
Class<?> wildcardClass = String.class;
wildcardClass = Integer.class;  // 타입 안전하게 할당
```

---

## 📚 관련 메서드 및 활용

### Class<?> 주요 메서드
```java
clazz.getName()                    // 클래스 전체 이름
clazz.getSimpleName()             // 클래스 단순 이름
clazz.isInstance(obj)             // 객체가 이 클래스의 인스턴스인지 확인
clazz.isAssignableFrom(otherClass) // 다른 클래스가 이 클래스에 할당 가능한지 확인
clazz.cast(obj)                   // 안전한 캐스팅
clazz.isPrimitive()               // 원시 타입인지 확인
clazz.isArray()                   // 배열 타입인지 확인
clazz.isInterface()               // 인터페이스인지 확인
clazz.isEnum()                    // 열거형인지 확인
```

### 제네릭 관련 유틸리티
```java
// 타입 토큰 패턴
public class TypeToken<T> {
    private final Class<T> type;
    
    public TypeToken(Class<T> type) {
        this.type = type;
    }
    
    public Class<T> getType() {
        return type;
    }
}

// 사용
TypeToken<List<String>> token = new TypeToken<List<String>>(List.class) {};
```