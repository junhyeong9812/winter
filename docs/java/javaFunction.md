# Java 내장 함수 완전 가이드

## 목차
1. [System 클래스의 깊이 있는 이해](#1-system-클래스의-깊이-있는-이해)
2. [Math 클래스 완전 정복](#2-math-클래스-완전-정복)
3. [String 클래스의 모든 것](#3-string-클래스의-모든-것)
4. [Collection Framework 심화](#4-collection-framework-심화)
5. [Java 8+ 새로운 기능들](#5-java-8-새로운-기능들)
6. [날짜/시간 API 완전 가이드](#6-날짜시간-api-완전-가이드)
7. [파일 I/O 심화](#7-파일-io-심화)
8. [스레드와 동시성](#8-스레드와-동시성)
9. [내부 구조와 성능 이해](#9-내부-구조와-성능-이해)

---

## 1. System 클래스의 깊이 있는 이해

### 1.1 System 클래스 구조
System 클래스는 `java.lang` 패키지에 속하며, 모든 메서드가 `static`으로 선언되어 있습니다. JVM과 운영체제 간의 인터페이스 역할을 합니다.

```java
public final class System {
    public static final InputStream in;    // 표준 입력
    public static final PrintStream out;   // 표준 출력
    public static final PrintStream err;   // 표준 에러
}
```

### 1.2 출력 관련 메서드들

#### System.out의 내부 구조
`System.out`은 `PrintStream` 객체입니다. 이 객체는 내부적으로 `BufferedWriter`를 사용하여 성능을 최적화합니다.

```java
// 기본 출력 메서드들
System.out.print("텍스트");           // 줄바꿈 없음
System.out.println("텍스트");         // 줄바꿈 포함
System.out.printf("%d %s", 10, "개"); // 포맷 출력

// 내부 동작 원리
PrintStream out = System.out;
out.write("Hello".getBytes());  // 바이트 레벨 출력
out.flush();                    // 버퍼 강제 비우기
```

#### 고급 출력 기법
```java
// 포맷 지정자들
System.out.printf("정수: %d%n", 42);
System.out.printf("실수: %.2f%n", 3.14159);
System.out.printf("문자열: %10s%n", "hello");  // 10자리 우측 정렬
System.out.printf("문자열: %-10s%n", "hello"); // 10자리 좌측 정렬
System.out.printf("16진수: %x%n", 255);

// 출력 스트림 리다이렉션
PrintStream originalOut = System.out;
try (PrintStream fileOut = new PrintStream("output.txt")) {
    System.setOut(fileOut);
    System.out.println("파일로 출력됨");
    System.setOut(originalOut);  // 원래대로 복구
}
```

### 1.3 시간 관련 메서드들

#### System.currentTimeMillis()
1970년 1월 1일 00:00:00 UTC로부터 경과된 밀리초를 반환합니다.

```java
long startTime = System.currentTimeMillis();
// 작업 수행
Thread.sleep(1000);
long endTime = System.currentTimeMillis();
System.out.println("실행 시간: " + (endTime - startTime) + "ms");
```

#### System.nanoTime()
더 정밀한 시간 측정을 위한 메서드입니다. 절대 시간이 아닌 상대적 시간 차이 측정용입니다.

```java
long startNano = System.nanoTime();
// 정밀한 측정이 필요한 작업
Collections.sort(largeList);
long duration = System.nanoTime() - startNano;
System.out.println("나노초 단위 실행시간: " + duration);
```

### 1.4 시스템 속성과 환경변수

```java
// 시스템 속성 읽기
String javaVersion = System.getProperty("java.version");
String osName = System.getProperty("os.name");
String userHome = System.getProperty("user.home");
String fileSeparator = System.getProperty("file.separator");

// 환경변수 읽기
Map<String, String> envVars = System.getenv();
String path = System.getenv("PATH");
String javaHome = System.getenv("JAVA_HOME");

// 시스템 속성 설정
System.setProperty("myapp.config", "development");
```

---

## 2. Math 클래스 완전 정복

### 2.1 Math 클래스의 내부 구조
Math 클래스의 모든 메서드는 `static` `native` 메서드로 구현되어 있어 JVM이 아닌 운영체제 레벨에서 최적화된 수학 연산을 수행합니다.

```java
public final class Math {
    public static final double E = 2.7182818284590452354;
    public static final double PI = 3.14159265358979323846;
    
    public static native double sin(double a);  // native 메서드
    public static native double cos(double a);
}
```

### 2.2 기본 수학 연산의 깊이 있는 활용

#### 절댓값과 비교 함수들
```java
// 절댓값 - 모든 숫자 타입 지원
int absInt = Math.abs(-42);           // 42
long absLong = Math.abs(-42L);        // 42L
float absFloat = Math.abs(-3.14f);    // 3.14f
double absDouble = Math.abs(-3.14);   // 3.14

// 최대값/최소값 - 오버로딩된 메서드들
int maxInt = Math.max(10, 20);
double maxDouble = Math.max(10.5, 20.3);
float minFloat = Math.min(3.14f, 2.71f);

// 실무 활용 예시: 범위 제한 함수
public static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
}
```

#### 거듭제곱과 루트 함수들
```java
// 거듭제곱
double power = Math.pow(2, 8);        // 256.0
double cube = Math.pow(3, 3);         // 27.0

// 제곱근과 세제곱근
double sqrt = Math.sqrt(16);          // 4.0
double cbrt = Math.cbrt(27);          // 3.0 (세제곱근)

// 지수와 로그
double exp = Math.exp(1);             // e^1 = 2.718...
double log = Math.log(Math.E);        // ln(e) = 1.0
double log10 = Math.log10(100);       // log₁₀(100) = 2.0

// 실무 예시: 복리 계산
public static double compoundInterest(double principal, double rate, int years) {
    return principal * Math.pow(1 + rate, years);
}
```

### 2.3 삼각함수와 각도 변환

```java
// 각도를 라디안으로 변환
double degrees = 90;
double radians = Math.toRadians(degrees);  // π/2
double backToDegrees = Math.toDegrees(radians);  // 90.0

// 삼각함수
double sin90 = Math.sin(Math.toRadians(90));  // 1.0
double cos0 = Math.cos(0);                    // 1.0
double tan45 = Math.tan(Math.toRadians(45));  // 1.0

// 역삼각함수
double asin = Math.asin(1);           // π/2
double acos = Math.acos(0);           // π/2
double atan = Math.atan(1);           // π/4

// 실무 예시: 좌표계 변환
public static class Point2D {
    public static Point2D polarToCartesian(double r, double theta) {
        return new Point2D(
            r * Math.cos(theta),
            r * Math.sin(theta)
        );
    }
}
```

### 2.4 난수 생성의 심화

```java
// 기본 난수 (0.0 이상 1.0 미만)
double random = Math.random();

// 범위 지정 난수 생성 유틸리티
public class RandomUtils {
    // 정수 범위 난수 (min 이상 max 이하)
    public static int randomInt(int min, int max) {
        return (int)(Math.random() * (max - min + 1)) + min;
    }
    
    // 실수 범위 난수
    public static double randomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    
    // 불린 난수 (확률 기반)
    public static boolean randomBoolean(double probability) {
        return Math.random() < probability;
    }
}

// Random 클래스 활용 (더 강력한 난수 생성)
Random rand = new Random();
int gaussianInt = (int)(rand.nextGaussian() * 10 + 50);  // 평균 50, 표준편차 10
```

### 2.5 반올림 함수들의 정밀한 이해

```java
double value = 3.7;

// floor: 바닥 함수 (음의 무한대 방향)
Math.floor(3.7);   // 3.0
Math.floor(-3.7);  // -4.0

// ceil: 천장 함수 (양의 무한대 방향)
Math.ceil(3.2);    // 4.0
Math.ceil(-3.7);   // -3.0

// round: 가장 가까운 정수로 반올림
Math.round(3.7);   // 4
Math.round(3.2);   // 3
Math.round(-3.7);  // -4

// rint: 가장 가까운 짝수로 반올림 (banker's rounding)
Math.rint(2.5);    // 2.0
Math.rint(3.5);    // 4.0

// 소수점 N자리 반올림 유틸리티
public static double roundToDecimalPlaces(double value, int places) {
    double multiplier = Math.pow(10, places);
    return Math.round(value * multiplier) / multiplier;
}
```

---

## 3. String 클래스의 모든 것

### 3.1 String의 내부 구조 이해

Java에서 String은 불변(immutable) 객체입니다. 내부적으로 `char[]` 배열(Java 9 이후는 `byte[]`)을 사용합니다.

```java
public final class String implements Serializable, Comparable<String> {
    private final byte[] value;  // Java 9+에서 char[]에서 byte[]로 변경
    private int hash;            // 해시코드 캐싱
    
    // String Pool 메커니즘
    public native String intern();
}
```

### 3.2 문자열 생성과 메모리 관리

```java
// 다양한 String 생성 방법과 메모리 차이
String literal = "Hello";           // String Pool에 저장
String newString = new String("Hello"); // Heap에 새 객체 생성
String interned = newString.intern();   // String Pool로 이동

// 동일성 vs 동등성
System.out.println(literal == interned);      // true (같은 참조)
System.out.println(literal == newString);     // false (다른 참조)
System.out.println(literal.equals(newString)); // true (내용 동일)
```

### 3.3 문자열 검색과 추출 메서드들

```java
String text = "Java Programming Language";

// 기본 검색 메서드들
int index = text.indexOf("Program");        // 5
int lastIndex = text.lastIndexOf("a");      // 18
boolean contains = text.contains("Java");   // true
boolean startsWith = text.startsWith("Ja"); // true
boolean endsWith = text.endsWith("age");    // true

// 고급 검색 - 여러 위치에서 찾기
public static List<Integer> findAllOccurrences(String text, String pattern) {
    List<Integer> indices = new ArrayList<>();
    int index = text.indexOf(pattern);
    while (index != -1) {
        indices.add(index);
        index = text.indexOf(pattern, index + 1);
    }
    return indices;
}

// 문자열 추출
String sub1 = text.substring(5);        // "Programming Language"
String sub2 = text.substring(5, 16);    // "Programming"
char charAt = text.charAt(0);           // 'J'

// 안전한 문자 접근 (IndexOutOfBounds 방지)
public static char safeCharAt(String str, int index) {
    return (index >= 0 && index < str.length()) ? str.charAt(index) : '\0';
}
```

### 3.4 문자열 변환과 조작

```java
String original = "  Java Programming  ";

// 대소문자 변환
String upper = original.toUpperCase();      // "  JAVA PROGRAMMING  "
String lower = original.toLowerCase();      // "  java programming  "

// 공백 제거
String trimmed = original.trim();           // "Java Programming"
String stripped = original.strip();         // Java 11+ (유니코드 공백도 제거)

// 문자열 치환
String replaced = original.replace(" ", "_");           // "__Java_Programming__"
String replaceFirst = original.replaceFirst("a", "A");  // "  JAva Programming  "
String replaceAll = original.replaceAll("\\s+", " ");  // " Java Programming "

// 정규표현식을 이용한 고급 치환
String text = "Phone: 010-1234-5678";
String masked = text.replaceAll("(\\d{3})-(\\d{4})-(\\d{4})", "$1-****-$3");
// "Phone: 010-****-5678"
```

### 3.5 문자열 분할과 결합

```java
String csv = "apple,banana,cherry,date";

// 기본 분할
String[] fruits = csv.split(",");
String[] limited = csv.split(",", 2);  // 최대 2개로 분할

// 정규표현식 분할
String text = "word1   word2\tword3\nword4";
String[] words = text.split("\\s+");   // 모든 종류의 공백으로 분할

// 문자열 결합
String joined = String.join(", ", fruits);  // "apple, banana, cherry, date"

// StringBuilder vs StringBuffer
StringBuilder sb = new StringBuilder();     // 단일 스레드용 (빠름)
StringBuffer sbuf = new StringBuffer();     // 멀티 스레드용 (동기화됨)

sb.append("Hello")
  .append(" ")
  .append("World")
  .insert(5, " Beautiful")
  .delete(6, 15)
  .reverse();

// StringJoiner (Java 8+)
StringJoiner joiner = new StringJoiner(", ", "[", "]");
joiner.add("A").add("B").add("C");
String result = joiner.toString();  // "[A, B, C]"
```

### 3.6 문자열 비교의 모든 것

```java
String s1 = "Hello";
String s2 = "hello";
String s3 = null;

// 기본 비교
boolean equals = s1.equals(s2);              // false
boolean equalsIgnoreCase = s1.equalsIgnoreCase(s2); // true

// Null-safe 비교
boolean safeEquals = Objects.equals(s1, s3); // false (null 처리됨)

// 사전식 비교
int compare = s1.compareTo(s2);              // 음수 (s1이 사전순으로 앞)
int compareIgnoreCase = s1.compareToIgnoreCase(s2); // 0 (동일)

// 문자열 패턴 매칭
boolean matches = "123".matches("\\d+");     // true (숫자만)
boolean phonePattern = "010-1234-5678".matches("\\d{3}-\\d{4}-\\d{4}"); // true
```

---

## 4. Collection Framework 심화

### 4.1 List 계열의 완전 이해

#### ArrayList의 내부 구조
```java
public class ArrayList<E> implements List<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData;
    private int size;
    
    // 자동 크기 조정 (동적 배열)
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1); // 1.5배 증가
    }
}
```

#### ArrayList 고급 활용
```java
List<String> list = new ArrayList<>();

// 기본 조작
list.add("Apple");
list.add(0, "Banana");              // 특정 위치에 삽입
list.set(0, "Blueberry");           // 특정 위치 값 변경
String removed = list.remove(0);     // 위치로 제거
boolean removed2 = list.remove("Apple"); // 값으로 제거

// 배치 처리
List<String> batch = Arrays.asList("A", "B", "C");
list.addAll(batch);                 // 전체 추가
list.addAll(1, batch);              // 특정 위치부터 추가
list.removeAll(batch);              // 일치하는 모든 요소 제거
list.retainAll(batch);              // 일치하는 요소만 유지

// 고급 검색
int index = list.indexOf("Apple");   // 첫 번째 위치
int lastIndex = list.lastIndexOf("Apple"); // 마지막 위치
List<String> subList = list.subList(1, 3);  // 부분 리스트 (view)

// 성능 최적화 팁
List<String> optimizedList = new ArrayList<>(1000); // 초기 용량 설정
list.trimToSize();                  // 메모리 최적화
```

#### LinkedList vs ArrayList 성능 비교
```java
// LinkedList - 노드 기반 이중 연결 리스트
LinkedList<String> linkedList = new LinkedList<>();
linkedList.addFirst("First");       // O(1)
linkedList.addLast("Last");         // O(1)
linkedList.removeFirst();           // O(1)
linkedList.removeLast();            // O(1)

// 성능 비교 시나리오
// ArrayList: 인덱스 접근 O(1), 중간 삽입/삭제 O(n)
// LinkedList: 인덱스 접근 O(n), 양끝 삽입/삭제 O(1)
```

### 4.2 Map 계열의 심화 활용

#### HashMap의 내부 구조 이해
```java
public class HashMap<K,V> implements Map<K,V> {
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    transient Node<K,V>[] table;  // 해시 테이블
    transient int size;
    
    // 해시 충돌 해결: 체이닝과 트리화 (Java 8+)
    static final int TREEIFY_THRESHOLD = 8;
}
```

#### Map의 고급 기능들
```java
Map<String, Integer> map = new HashMap<>();

// 기본 조작
map.put("apple", 100);
map.put("banana", 200);
Integer value = map.get("apple");            // 100
Integer defaultValue = map.getOrDefault("orange", 0); // 0

// Java 8+ 고급 메서드들
map.putIfAbsent("cherry", 300);              // 키가 없을 때만 추가
map.replace("apple", 150);                   // 키가 있을 때만 교체
map.replace("apple", 150, 175);              // 특정 값일 때만 교체

// 계산 기반 업데이트
map.compute("apple", (key, val) -> val == null ? 1 : val + 1);
map.computeIfAbsent("grape", key -> key.length());
map.computeIfPresent("banana", (key, val) -> val * 2);

// 병합 연산
map.merge("apple", 50, Integer::sum);        // 기존 값과 합계

// 반복 처리
map.forEach((key, value) -> {
    System.out.println(key + ": " + value);
});

// 뷰 컬렉션 활용
Set<String> keys = map.keySet();             // 키 집합
Collection<Integer> values = map.values();   // 값 컬렉션
Set<Map.Entry<String, Integer>> entries = map.entrySet(); // 엔트리 집합
```

#### 특수한 Map 구현체들
```java
// TreeMap - 정렬된 맵
TreeMap<String, Integer> treeMap = new TreeMap<>();
treeMap.put("c", 3);
treeMap.put("a", 1);
treeMap.put("b", 2);
// 자동으로 키 순서로 정렬됨: {a=1, b=2, c=3}

// LinkedHashMap - 삽입 순서 유지
LinkedHashMap<String, Integer> linkedMap = new LinkedHashMap<>();

// ConcurrentHashMap - 스레드 안전
ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
```

### 4.3 Set 계열의 활용

```java
Set<String> hashSet = new HashSet<>();       // 해시 기반, 순서 없음
Set<String> linkedHashSet = new LinkedHashSet<>(); // 삽입 순서 유지
Set<String> treeSet = new TreeSet<>();       // 정렬된 집합

// Set 연산들
Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
Set<String> set2 = new HashSet<>(Arrays.asList("B", "C", "D"));

// 합집합
Set<String> union = new HashSet<>(set1);
union.addAll(set2);  // {A, B, C, D}

// 교집합
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // {B, C}

// 차집합
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);  // {A}

// 부분집합 확인
boolean isSubset = set2.containsAll(set1);
```

---

## 5. Java 8+ 새로운 기능들

### 5.1 Stream API의 완전 정복

Stream은 Java 8에서 도입된 함수형 프로그래밍 스타일의 컬렉션 처리 API입니다.

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");

// 기본 스트림 연산
List<String> filtered = names.stream()
    .filter(name -> name.length() > 3)      // 중간 연산
    .map(String::toUpperCase)               // 중간 연산
    .sorted()                               // 중간 연산
    .collect(Collectors.toList());          // 최종 연산

// 다양한 중간 연산들
names.stream()
    .distinct()                             // 중복 제거
    .skip(2)                               // 처음 2개 건너뛰기
    .limit(3)                              // 최대 3개만
    .peek(System.out::println)             // 디버깅용 중간 처리
    .forEach(System.out::println);
```

#### Stream의 내부 동작 원리
```java
// 지연 평가 (Lazy Evaluation) 예시
Stream<String> stream = names.stream()
    .filter(name -> {
        System.out.println("Filtering: " + name);
        return name.length() > 3;
    })
    .map(name -> {
        System.out.println("Mapping: " + name);
        return name.toUpperCase();
    });

// 여기까지는 아무것도 실행되지 않음!
// 최종 연산이 호출될 때 실행됨
List<String> result = stream.collect(Collectors.toList());
```

### 5.2 고급 Collectors 활용

```java
List<Person> people = Arrays.asList(
    new Person("Alice", 25, "Engineering"),
    new Person("Bob", 30, "Marketing"),
    new Person("Charlie", 35, "Engineering")
);

// 그룹핑
Map<String, List<Person>> byDepartment = people.stream()
    .collect(Collectors.groupingBy(Person::getDepartment));

// 다단계 그룹핑
Map<String, Map<Integer, List<Person>>> byDeptAndAge = people.stream()
    .collect(Collectors.groupingBy(
        Person::getDepartment,
        Collectors.groupingBy(Person::getAge)
    ));

// 집계 연산과 함께 그룹핑
Map<String, Double> avgAgeByDept = people.stream()
    .collect(Collectors.groupingBy(
        Person::getDepartment,
        Collectors.averagingInt(Person::getAge)
    ));

// 커스텀 Collector
Map<String, String> customCollect = people.stream()
    .collect(Collectors.toMap(
        Person::getName,
        person -> person.getDepartment() + ":" + person.getAge(),
        (existing, replacement) -> existing  // 중복 키 처리
    ));
```

### 5.3 Optional 클래스 완전 가이드

Optional은 `null` 값을 안전하게 다루기 위한 컨테이너 클래스입니다.

```java
// Optional 생성
Optional<String> optional1 = Optional.of("Hello");          // null이면 예외
Optional<String> optional2 = Optional.ofNullable(getName()); // null 허용
Optional<String> empty = Optional.empty();                   // 빈 Optional

// 값 확인과 추출
if (optional1.isPresent()) {
    String value = optional1.get();
}

// 함수형 스타일 처리
optional1.ifPresent(System.out::println);                   // 값이 있으면 실행
optional1.ifPresentOrElse(                                  // Java 9+
    System.out::println,
    () -> System.out.println("No value")
);

// 기본값 처리
String result1 = optional2.orElse("Default");               // 즉시 평가
String result2 = optional2.orElseGet(() -> "Default");      // 지연 평가
String result3 = optional2.orElseThrow(() -> new RuntimeException("No value"));

// 변환과 필터링
Optional<Integer> length = optional1
    .filter(s -> s.length() > 3)
    .map(String::length);

Optional<String> upperCase = optional1
    .flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(s.toUpperCase()));
```

### 5.4 Lambda 표현식과 메서드 참조

```java
// Lambda 표현식의 다양한 형태
Runnable runnable = () -> System.out.println("Hello");
Consumer<String> printer = text -> System.out.println(text);
Function<String, Integer> lengthFunc = s -> s.length();
Predicate<String> isEmpty = s -> s.isEmpty();
BinaryOperator<Integer> adder = (a, b) -> a + b;

// 메서드 참조 종류
Consumer<String> methodRef1 = System.out::println;          // 정적 메서드 참조
Function<String, Integer> methodRef2 = String::length;      // 인스턴스 메서드 참조
Supplier<List<String>> methodRef3 = ArrayList::new;         // 생성자 참조

// 고급 람다 활용
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 복잡한 조건 조합
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> evenAndPositive = isEven.and(isPositive);

List<Integer> filtered = numbers.stream()
    .filter(evenAndPositive)
    .collect(Collectors.toList());
```

### 5.5 병렬 스트림 (Parallel Streams)

```java
List<Integer> largeList = IntStream.range(0, 1_000_000)
    .boxed()
    .collect(Collectors.toList());

// 순차 처리
long sequential = largeList.stream()
    .mapToInt(i -> i * i)
    .sum();

// 병렬 처리
long parallel = largeList.parallelStream()
    .mapToInt(i -> i * i)
    .sum();

// 병렬 처리 주의사항
// 1. 작은 데이터셋에서는 오히려 느릴 수 있음
// 2. 스레드 안전하지 않은 연산 주의
// 3. 순서에 의존하는 연산 주의
```

---

## 6. 날짜/시간 API 완전 가이드

### 6.1 Java 8 Time API 구조

Java 8에서 완전히 새롭게 설계된 `java.time` 패키지는 불변 객체와 스레드 안전성을 보장합니다.

```java
// 주요 클래스들
LocalDate date = LocalDate.now();           // 날짜만
LocalTime time = LocalTime.now();           // 시간만
LocalDateTime dateTime = LocalDateTime.now(); // 날짜 + 시간
ZonedDateTime zonedDateTime = ZonedDateTime.now(); // 시간대 포함
Instant instant = Instant.now();            // UTC 기준 타임스탬프
```

### 6.2 날짜 생성과 파싱

```java
// 다양한 생성 방법
LocalDate specificDate = LocalDate.of(2025, 8, 4);
LocalDate parsed = LocalDate.parse("2025-08-04");
LocalDate formatted = LocalDate.parse("04/08/2025", 
    DateTimeFormatter.ofPattern("dd/MM/yyyy"));

// 현재 시간 기준 생성
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);
LocalDate lastWeek = today.minusWeeks(1);
LocalDate firstDayOfMonth = today.withDayOfMonth(1);
LocalDate firstDayOfYear = today.withDayOfYear(1);

// 상대적 날짜 계산
LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
LocalDate firstMondayOfMonth = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
```

### 6.3 시간 계산과 비교

```java
LocalDate start = LocalDate.of(2025, 1, 1);
LocalDate end = LocalDate.of(2025, 12, 31);

// 기간 계산
Period period = Period.between(start, end);
System.out.println(period.getYears());      // 0
System.out.println(period.getMonths());     // 11
System.out.println(period.getDays());       // 30

// 정확한 일수 계산
long daysBetween = ChronoUnit.DAYS.between(start, end);
long weeksBetween = ChronoUnit.WEEKS.between(start, end);
long monthsBetween = ChronoUnit.MONTHS.between(start, end);

// 시간 계산
LocalTime startTime = LocalTime.of(9, 30);
LocalTime endTime = LocalTime.of(17, 45);
Duration duration = Duration.between(startTime, endTime);
long hours = duration.toHours();            // 8
long minutes = duration.toMinutes();        // 495

// 날짜 비교
boolean isBefore = start.isBefore(end);      // true
boolean isAfter = start.isAfter(end);        // false
boolean isEqual = start.isEqual(end);        // false
```

### 6.4 포맷팅과 파싱

```java
LocalDateTime now = LocalDateTime.now();

// 미리 정의된 포맷터
String iso = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
String basic = now.format(DateTimeFormatter.BASIC_ISO_DATE);

// 커스텀 포맷터
DateTimeFormatter custom = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
String formatted = now.format(custom);

// 로케일 기반 포맷팅
DateTimeFormatter korean = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN);
DateTimeFormatter english = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

// 파싱 시 오류 처리
try {
    LocalDate parsed = LocalDate.parse("2025-13-01");  // 잘못된 월
} catch (DateTimeParseException e) {
    System.out.println("파싱 오류: " + e.getMessage());
}
```

### 6.5 시간대 처리

```java
// 시간대 정보
ZoneId seoulZone = ZoneId.of("Asia/Seoul");
ZoneId utcZone = ZoneId.of("UTC");
ZoneId systemZone = ZoneId.systemDefault();

// 시간대별 시간 생성
ZonedDateTime seoulTime = ZonedDateTime.now(seoulZone);
ZonedDateTime utcTime = ZonedDateTime.now(utcZone);

// 시간대 변환
ZonedDateTime convertedTime = seoulTime.withZoneSameInstant(utcZone);

// Instant와 ZonedDateTime 변환
Instant instant = Instant.now();
ZonedDateTime fromInstant = instant.atZone(seoulZone);
Instant backToInstant = seoulTime.toInstant();

// 실무 예시: 전 세계 시간 표시
public static void showWorldTimes() {
    Instant now = Instant.now();
    String[] timeZones = {"America/New_York", "Europe/London", "Asia/Tokyo", "Asia/Seoul"};
    
    for (String tz : timeZones) {
        ZonedDateTime local = now.atZone(ZoneId.of(tz));
        System.out.printf("%s: %s%n", tz, local.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
```

---

## 7. 파일 I/O 심화

### 7.1 NIO.2 (New I/O) API

Java 7에서 도입된 `java.nio.file` 패키지는 기존 File 클래스의 한계를 극복합니다.

```java
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

// Path 객체 생성
Path path = Paths.get("data", "sample.txt");
Path absolutePath = path.toAbsolutePath();
Path parentPath = path.getParent();
Path fileName = path.getFileName();

// 파일 존재 확인과 속성
boolean exists = Files.exists(path);
boolean isDirectory = Files.isDirectory(path);
boolean isReadable = Files.isReadable(path);
boolean isWritable = Files.isWritable(path);
long fileSize = Files.size(path);
```

### 7.2 파일 읽기와 쓰기

```java
// 전체 파일을 한 번에 읽기
List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
String content = Files.readString(path);  // Java 11+
byte[] bytes = Files.readAllBytes(path);

// 대용량 파일을 스트림으로 읽기
try (Stream<String> lineStream = Files.lines(path)) {
    lineStream
        .filter(line -> line.contains("Java"))
        .forEach(System.out::println);
}

// 파일 쓰기
List<String> outputLines = Arrays.asList("Line 1", "Line 2", "Line 3");
Files.write(path, outputLines, StandardCharsets.UTF_8);
Files.writeString(path, "Single line content");  // Java 11+

// 추가 모드로 쓰기
Files.write(path, "추가 내용".getBytes(), StandardOpenOption.APPEND);
```

### 7.3 디렉토리 처리와 파일 탐색

```java
// 디렉토리 생성
Path newDir = Paths.get("new_directory");
Files.createDirectory(newDir);                    // 단일 디렉토리
Files.createDirectories(Paths.get("a/b/c"));     // 중간 디렉토리도 함께 생성

// 디렉토리 내용 나열
try (DirectoryStream<Path> stream = Files.newDirectoryStream(newDir)) {
    for (Path entry : stream) {
        System.out.println(entry.getFileName());
    }
}

// 재귀적 파일 탐색
try (Stream<Path> paths = Files.walk(Paths.get("."))) {
    paths
        .filter(Files::isRegularFile)
        .filter(path -> path.toString().endsWith(".java"))
        .forEach(System.out::println);
}

// 파일 찾기
try (Stream<Path> paths = Files.find(Paths.get("."), 
                                    Integer.MAX_VALUE,
                                    (path, attrs) -> attrs.isRegularFile() 
                                                  && path.toString().endsWith(".txt"))) {
    paths.forEach(System.out::println);
}
```

### 7.4 고급 I/O 작업

```java
// 파일 복사와 이동
Path source = Paths.get("source.txt");
Path target = Paths.get("target.txt");

Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.move(source, Paths.get("moved.txt"), StandardCopyOption.REPLACE_EXISTING);

// 심볼릭 링크
Path link = Paths.get("link.txt");
Files.createSymbolicLink(link, target);

// 파일 속성 읽기
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
System.out.println("생성 시간: " + attrs.creationTime());
System.out.println("마지막 수정: " + attrs.lastModifiedTime());
System.out.println("파일 크기: " + attrs.size());

// 파일 속성 수정
FileTime newTime = FileTime.fromMillis(System.currentTimeMillis());
Files.setLastModifiedTime(path, newTime);
```

---

## 8. 스레드와 동시성

### 8.1 Thread 클래스와 Runnable

```java
// Thread 생성 방법들
Thread thread1 = new Thread(() -> {
    System.out.println("Lambda로 생성된 스레드");
});

Thread thread2 = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("익명 클래스로 생성된 스레드");
    }
});

// Thread 생명주기 관리
thread1.start();                    // 스레드 시작
thread1.join();                     // 스레드 완료 대기
thread1.interrupt();                // 스레드 중단 요청
boolean isAlive = thread1.isAlive(); // 실행 중인지 확인

// 스레드 우선순위
thread1.setPriority(Thread.MAX_PRIORITY);
int priority = thread1.getPriority();
```

### 8.2 ExecutorService와 스레드 풀

```java
// 다양한 스레드 풀 생성
ExecutorService fixedPool = Executors.newFixedThreadPool(4);
ExecutorService cachedPool = Executors.newCachedThreadPool();
ExecutorService singlePool = Executors.newSingleThreadExecutor();
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

// 작업 제출과 결과 처리
Future<String> future = fixedPool.submit(() -> {
    Thread.sleep(1000);
    return "작업 완료";
});

try {
    String result = future.get(2, TimeUnit.SECONDS);  // 타임아웃 설정
    System.out.println(result);
} catch (TimeoutException e) {
    future.cancel(true);  // 작업 취소
}

// 여러 작업 동시 실행
List<Callable<Integer>> tasks = Arrays.asList(
    () -> { Thread.sleep(1000); return 1; },
    () -> { Thread.sleep(2000); return 2; },
    () -> { Thread.sleep(1500); return 3; }
);

List<Future<Integer>> futures = fixedPool.invokeAll(tasks);
Integer firstResult = fixedPool.invokeAny(tasks);  // 가장 빨리 완료된 결과

// 예약 작업
scheduledPool.schedule(() -> System.out.println("1초 후 실행"), 1, TimeUnit.SECONDS);
scheduledPool.scheduleAtFixedRate(() -> System.out.println("주기적 실행"), 
                                  0, 5, TimeUnit.SECONDS);
```

### 8.3 동기화와 락

```java
// synchronized 블록
private final Object lock = new Object();
public void synchronizedMethod() {
    synchronized(lock) {
        // 동기화된 코드 블록
    }
}

// ReentrantLock 활용
private final ReentrantLock lock = new ReentrantLock();
public void lockMethod() {
    lock.lock();
    try {
        // 임계 영역
    } finally {
        lock.unlock();
    }
}

// ReadWriteLock으로 성능 최적화
private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
private final Lock readLock = rwLock.readLock();
private final Lock writeLock = rwLock.writeLock();

public String read() {
    readLock.lock();
    try {
        return data;  // 여러 스레드가 동시에 읽기 가능
    } finally {
        readLock.unlock();
    }
}
```

---

## 9. 내부 구조와 성능 이해

### 9.1 String의 메모리 최적화

```java
// String Pool 동작 원리
String s1 = "Hello";                // Heap의 String Pool 영역
String s2 = "Hello";                // 같은 객체 참조
String s3 = new String("Hello");    // Heap의 일반 영역에 새 객체
String s4 = s3.intern();            // String Pool로 이동 (s1과 같은 참조)

// StringBuilder vs StringBuffer 성능
public static String concatenateStrings(List<String> strings) {
    StringBuilder sb = new StringBuilder(strings.size() * 16); // 적절한 초기 용량
    for (String str : strings) {
        sb.append(str);
    }
    return sb.toString();
}

// String 연산 시 메모리 누수 방지
// 잘못된 예시
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "a";  // 매번 새 String 객체 생성 (O(n²) 복잡도)
}

// 올바른 예시
StringBuilder sb = new StringBuilder(1000);
for (int i = 0; i < 1000; i++) {
    sb.append("a");  // O(n) 복잡도
}
String result = sb.toString();
```

### 9.2 Collection의 성능 특성

```java
// ArrayList vs LinkedList 성능 분석
public class PerformanceComparison {
    public static void arrayListVsLinkedList() {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        
        // 순차 추가: ArrayList 우세 (동적 배열 확장)
        // 중간 삽입: LinkedList 우세 (노드 연결만 변경)
        // 인덱스 접근: ArrayList 압도적 우세 (O(1) vs O(n))
        // 메모리 사용량: ArrayList 우세 (연속 메모리)
    }
    
    // HashMap 로드팩터와 성능
    public static void hashMapOptimization() {
        // 초기 용량 설정으로 리해싱 방지
        Map<String, Integer> map = new HashMap<>(1000, 0.75f);
        
        // 해시 충돌 최소화를 위한 좋은 hashCode 구현 필요
    }
}
```

### 9.3 가비지 컬렉션과 메모리 관리

```java
// 메모리 사용량 모니터링
MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

System.out.println("사용된 메모리: " + heapUsage.getUsed() / 1024 / 1024 + " MB");
System.out.println("최대 메모리: " + heapUsage.getMax() / 1024 / 1024 + " MB");

// 메모리 효율적인 코드 작성
public class MemoryEfficient {
    // 대용량 데이터 처리 시 스트림 활용
    public static long processLargeFile(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                .filter(line -> line.contains("ERROR"))
                .count();  // 전체 파일을 메모리에 로드하지 않음
        } catch (IOException e) {
            return 0;
        }
    }
    
    // 객체 재사용으로 GC 압박 감소
    private static final StringBuilder REUSABLE_BUILDER = new StringBuilder();
    
    public static synchronized String buildString(String... parts) {
        REUSABLE_BUILDER.setLength(0);  // 기존 내용 제거
        for (String part : parts) {
            REUSABLE_BUILDER.append(part);
        }
        return REUSABLE_BUILDER.toString();
    }
}
```

## 10. 고급 유틸리티 클래스들

### 10.1 Objects 클래스 (Java 7+)

```java
// Null-safe 연산들
String a = "Hello";
String b = null;

boolean equals = Objects.equals(a, b);           // false (null 안전)
int hashCode = Objects.hash(a, b);               // 복합 해시코드
String string = Objects.toString(b, "default"); // null일 때 기본값

// 유효성 검사
String validated = Objects.requireNonNull(a, "a는 null일 수 없습니다");
Objects.requireNonNull(a);  // null이면 NullPointerException

// 비교
int comparison = Objects.compare(a, b, String.CASE_INSENSITIVE_ORDER);
```

### 10.2 Arrays 클래스 고급 활용

```java
int[] numbers = {3, 1, 4, 1, 5, 9, 2, 6};

// 정렬과 검색
Arrays.sort(numbers);                           // 오름차순 정렬
int index = Arrays.binarySearch(numbers, 5);    // 이진 검색 (정렬된 배열 필요)

// 배열 비교
int[] other = {1, 1, 2, 3, 4, 5, 6, 9};
boolean equal = Arrays.equals(numbers, other);  // 내용 비교
int comparison = Arrays.compare(numbers, other); // 사전식 비교

// 배열 복사
int[] copied = Arrays.copyOf(numbers, 10);      // 크기 10으로 복사 (부족하면 0 채움)
int[] range = Arrays.copyOfRange(numbers, 2, 6); // 부분 복사

// 배열 채우기
int[] filled = new int[10];
Arrays.fill(filled, 42);                        // 모든 요소를 42로
Arrays.fill(filled, 2, 5, 99);                  // 인덱스 2~4만 99로

// 다차원 배열
int[][] matrix = {{1, 2}, {3, 4}};
String deepString = Arrays.deepToString(matrix); // "[[1, 2], [3, 4]]"
boolean deepEqual = Arrays.deepEquals(matrix, matrix);
```

### 10.3 Collections 클래스 완전 활용

```java
List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));

// 정렬과 셔플
Collections.sort(list);                         // 오름차순
Collections.sort(list, Collections.reverseOrder()); // 내림차순
Collections.shuffle(list);                      // 무작위 섞기
Collections.reverse(list);                      // 순서 뒤집기

// 검색과 치환
int index = Collections.binarySearch(list, 4);  // 이진 검색
int frequency = Collections.frequency(list, 1); // 요소 빈도
Collections.replaceAll(list, 1, 10);           // 모든 1을 10으로 교체

// 극값 찾기
Integer min = Collections.min(list);
Integer max = Collections.max(list);
Integer maxCustom = Collections.max(list, (a, b) -> Integer.compare(a % 10, b % 10));

// 회전과 스왑
Collections.rotate(list, 2);                   // 오른쪽으로 2칸 회전
Collections.swap(list, 0, list.size() - 1);   // 첫 번째와 마지막 요소 교환

// 불변 컬렉션 생성
List<String> immutableList = Collections.unmodifiableList(Arrays.asList("A", "B", "C"));
Set<String> singletonSet = Collections.singleton("Only");
Map<String, Integer> singletonMap = Collections.singletonMap("key", 42);

// 동기화된 컬렉션
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
```

## 11. 정규표현식 (Pattern & Matcher)

### 11.1 Pattern 클래스 심화

```java
// Pattern 컴파일과 재사용
Pattern emailPattern = Pattern.compile(
    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
    Pattern.CASE_INSENSITIVE
);

// 매칭 확인
boolean isValidEmail = emailPattern.matcher("user@example.com").matches();

// 그룹 매칭
Pattern phonePattern = Pattern.compile("(\\d{3})-(\\d{4})-(\\d{4})");
Matcher phoneMatcher = phonePattern.matcher("010-1234-5678");

if (phoneMatcher.matches()) {
    String areaCode = phoneMatcher.group(1);    // "010"
    String firstPart = phoneMatcher.group(2);   // "1234"
    String secondPart = phoneMatcher.group(3);  // "5678"
}

// 모든 매칭 찾기
String text = "이메일: user1@test.com, user2@example.org";
Matcher emailMatcher = emailPattern.matcher(text);
while (emailMatcher.find()) {
    System.out.println("찾은 이메일: " + emailMatcher.group());
    System.out.println("위치: " + emailMatcher.start() + "-" + emailMatcher.end());
}

// 치환 작업
String masked = phoneMatcher.replaceAll("$1-****-$3");  // 010-****-5678
String allReplaced = emailMatcher.replaceAll("[이메일]"); // 모든 이메일을 [이메일]로 치환
```

## 12. Java 9+ 추가 기능들

### 12.1 모듈 시스템 (Java 9)

```java
// module-info.java 예시
module com.example.myapp {
    requires java.base;          // 암시적으로 항상 포함
    requires java.logging;       // 로깅 모듈 의존성
    exports com.example.api;     // 외부에 노출할 패키지
    provides com.example.Service with com.example.ServiceImpl; // 서비스 제공
}
```

### 12.2 컬렉션 팩토리 메서드 (Java 9)

```java
// 불변 컬렉션 생성
List<String> immutableList = List.of("A", "B", "C");
Set<Integer> immutableSet = Set.of(1, 2, 3);
Map<String, Integer> immutableMap = Map.of(
    "apple", 1,
    "banana", 2,
    "cherry", 3
);

// 주의: null 값 불허, 중복 키 불허 (Set, Map)
// 수정 시도 시 UnsupportedOperationException 발생
```

### 12.3 var 키워드 (Java 10)

```java
// 지역 변수 타입 추론
var message = "Hello World";              // String 타입으로 추론
var numbers = Arrays.asList(1, 2, 3);     // List<Integer>로 추론
var map = new HashMap<String, Integer>(); // HashMap<String, Integer>로 추론

// 제한사항
// var x;                    // 컴파일 오류: 초기화 필요
// var x = null;             // 컴파일 오류: 타입 추론 불가
// private var field;        // 컴파일 오류: 지역 변수만 가능
```

### 12.4 Switch 표현식 (Java 12-14)

```java
// 기존 switch 문
String dayType;
switch (dayOfWeek) {
    case MONDAY:
    case TUESDAY:
    case WEDNESDAY:
    case THURSDAY:
    case FRIDAY:
        dayType = "Weekday";
        break;
    case SATURDAY:
    case SUNDAY:
        dayType = "Weekend";
        break;
    default:
        dayType = "Unknown";
}

// 새로운 switch 표현식 (Java 14+)
String dayType = switch (dayOfWeek) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
    case SATURDAY, SUNDAY -> "Weekend";
    default -> "Unknown";
};

// yield를 사용한 복잡한 로직
String result = switch (score) {
    case 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 -> "A";
    case 80, 81, 82, 83, 84, 85, 86, 87, 88, 89 -> "B";
    default -> {
        String grade = score >= 70 ? "C" : "F";
        yield "Grade: " + grade;
    }
};
```

### 12.5 Record 클래스 (Java 14+)

```java
// 전통적인 데이터 클래스
public class PersonOld {
    private final String name;
    private final int age;
    
    public PersonOld(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // getter, equals, hashCode, toString 메서드들...
}

// Record로 간단하게
public record Person(String name, int age) {
    // 자동으로 생성됨:
    // - 생성자
    // - getter 메서드 (name(), age())
    // - equals(), hashCode(), toString()
    
    // 커스텀 검증
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 음수일 수 없습니다");
        }
    }
    
    // 추가 메서드 정의 가능
    public boolean isAdult() {
        return age >= 18;
    }
}

// 사용 예시
Person person = new Person("Alice", 25);
System.out.println(person.name());          // "Alice"
System.out.println(person.age());           // 25
System.out.println(person.isAdult());       // true
```

### 12.6 텍스트 블록 (Java 15+)

```java
// 기존 방식
String json = "{\n" +
              "  \"name\": \"John\",\n" +
              "  \"age\": 30,\n" +
              "  \"city\": \"Seoul\"\n" +
              "}";

// 텍스트 블록 방식
String json = """
    {
      "name": "John",
      "age": 30,
      "city": "Seoul"
    }
    """;

// SQL 쿼리 예시
String sql = """
    SELECT u.name, u.email, p.title
    FROM users u
    JOIN posts p ON u.id = p.user_id
    WHERE u.active = true
    ORDER BY u.name
    """;

// 인덴테이션 처리
String formatted = """
    Line 1
        Line 2 (indented)
    Line 3
    """.stripIndent();  // 공통 인덴테이션 제거
```

### 12.7 Pattern Matching (Java 17+)

```java
// instanceof 패턴 매칭
public static String processObject(Object obj) {
    if (obj instanceof String s) {
        return "String length: " + s.length();  // 자동 캐스팅
    } else if (obj instanceof Integer i) {
        return "Integer value: " + i;
    } else if (obj instanceof List<?> list && !list.isEmpty()) {
        return "List size: " + list.size();
    }
    return "Unknown type";
}

// Switch에서 패턴 매칭 (Preview in Java 17+)
public static String describe(Object obj) {
    return switch (obj) {
        case null -> "null value";
        case String s when s.isEmpty() -> "empty string";
        case String s -> "string of length " + s.length();
        case Integer i when i > 0 -> "positive integer: " + i;
        case Integer i -> "non-positive integer: " + i;
        case List<?> list -> "list of size " + list.size();
        default -> "unknown object";
    };
}
```

## 13. 네트워킹과 HTTP 클라이언트

### 13.1 HTTP 클라이언트 (Java 11+)

```java
import java.net.http.*;
import java.net.URI;
import java.time.Duration;

// HTTP 클라이언트 생성
HttpClient client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();

// GET 요청
HttpRequest getRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Accept", "application/json")
    .timeout(Duration.ofSeconds(30))
    .GET()
    .build();

// 동기 요청
HttpResponse<String> response = client.send(getRequest, 
    HttpResponse.BodyHandlers.ofString());

System.out.println("Status: " + response.statusCode());
System.out.println("Body: " + response.body());

// 비동기 요청
CompletableFuture<HttpResponse<String>> futureResponse = 
    client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString());

futureResponse.thenAccept(resp -> {
    System.out.println("Async response: " + resp.body());
});

// POST 요청
String jsonBody = """
    {
        "name": "John Doe",
        "email": "john@example.com"
    }
    """;

HttpRequest postRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    .build();
```

### 13.2 URL과 URI 처리

```java
import java.net.*;

// URL 파싱과 조작
URL url = new URL("https://example.com:8080/path/to/resource?param1=value1&param2=value2#section");

String protocol = url.getProtocol();    // "https"
String host = url.getHost();            // "example.com"
int port = url.getPort();               // 8080
String path = url.getPath();            // "/path/to/resource"
String query = url.getQuery();          // "param1=value1&param2=value2"
String fragment = url.getRef();         // "section"

// URI 빌딩 (Java 11+)
URI uri = URI.create("https://api.example.com")
    .resolve("/v1/users")
    .resolve("?page=1&size=10");

// URL 인코딩/디코딩
String encoded = URLEncoder.encode("Hello World!", StandardCharsets.UTF_8);
String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
```

## 14. 리플렉션 (Reflection) API

### 14.1 클래스 정보 조회

```java
Class<?> clazz = String.class;
// 또는 Class.forName("java.lang.String");
// 또는 "hello".getClass();

// 클래스 정보
String className = clazz.getName();              // "java.lang.String"
String simpleName = clazz.getSimpleName();      // "String"
Package pkg = clazz.getPackage();               // java.lang 패키지
Class<?> superClass = clazz.getSuperclass();    // Object.class

// 구현된 인터페이스들
Class<?>[] interfaces = clazz.getInterfaces();
for (Class<?> iface : interfaces) {
    System.out.println("Interface: " + iface.getName());
}

// 어노테이션 확인
boolean isDeprecated = clazz.isAnnotationPresent(Deprecated.class);
Annotation[] annotations = clazz.getAnnotations();
```

### 14.2 필드와 메서드 조작

```java
// 필드 정보
Field[] fields = clazz.getDeclaredFields();
for (Field field : fields) {
    System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
    
    // private 필드 접근
    field.setAccessible(true);
    Object value = field.get(someObject);
    field.set(someObject, newValue);
}

// 메서드 정보
Method[] methods = clazz.getDeclaredMethods();
for (Method method : methods) {
    System.out.println("Method: " + method.getName());
    System.out.println("Return type: " + method.getReturnType());
    System.out.println("Parameters: " + Arrays.toString(method.getParameterTypes()));
}

// 메서드 호출
Method lengthMethod = String.class.getMethod("length");
Integer length = (Integer) lengthMethod.invoke("Hello");  // 5

// 생성자 사용
Constructor<?> constructor = String.class.getConstructor(String.class);
String newString = (String) constructor.newInstance("Hello");
```

### 14.3 동적 프록시

```java
interface Calculator {
    int add(int a, int b);
    int multiply(int a, int b);
}

// 프록시 생성
Calculator proxy = (Calculator) Proxy.newProxyInstance(
    Calculator.class.getClassLoader(),
    new Class<?>[]{Calculator.class},
    (proxyObj, method, args) -> {
        System.out.println("메서드 호출: " + method.getName());
        
        // 실제 로직
        if ("add".equals(method.getName())) {
            return (Integer) args[0] + (Integer) args[1];
        } else if ("multiply".equals(method.getName())) {
            return (Integer) args[0] * (Integer) args[1];
        }
        return null;
    }
);

int result = proxy.add(5, 3);  // "메서드 호출: add" 출력 후 8 반환
```

## 15. 동시성 유틸리티 고급

### 15.1 CompletableFuture 완전 가이드

```java
// 비동기 작업 생성
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(1000); } catch (InterruptedException e) {}
    return "결과 1";
});

CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    try { Thread.sleep(1500); } catch (InterruptedException e) {}
    return "결과 2";
});

// 조합과 변환
CompletableFuture<String> combined = future1
    .thenCompose(result1 -> 
        CompletableFuture.supplyAsync(() -> result1 + " + 추가처리"))
    .thenCombine(future2, (result1, result2) -> result1 + " & " + result2);

// 예외 처리
CompletableFuture<String> handled = CompletableFuture.supplyAsync(() -> {
    if (Math.random() > 0.5) {
        throw new RuntimeException("오류 발생");
    }
    return "성공";
}).exceptionally(throwable -> {
    System.out.println("예외 처리: " + throwable.getMessage());
    return "기본값";
}).handle((result, throwable) -> {
    if (throwable != null) {
        return "오류로 인한 기본값";
    }
    return result.toUpperCase();
});

// 여러 작업 조합
CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2);
CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
```

### 15.2 CountDownLatch와 CyclicBarrier

```java
// CountDownLatch - 일회성 동기화
CountDownLatch latch = new CountDownLatch(3);

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        // 작업 수행
        System.out.println("작업 완료");
        latch.countDown();  // 카운터 감소
    }).start();
}

latch.await();  // 모든 작업 완료까지 대기
System.out.println("모든 작업 완료");

// CyclicBarrier - 재사용 가능한 동기화
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("모든 스레드가 도착했습니다!");
});

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        try {
            // 작업 수행
            barrier.await();  // 다른 스레드들 대기
            // 모든 스레드가 도착한 후 계속 진행
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

### 15.3 Semaphore와 BlockingQueue

```java
// Semaphore - 리소스 접근 제한
Semaphore semaphore = new Semaphore(2);  // 최대 2개 스레드만 동시 접근

public void limitedResource() {
    try {
        semaphore.acquire();  // 허가 획득
        // 제한된 리소스 사용
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        semaphore.release();  // 허가 반납
    }
}

// BlockingQueue - 생산자-소비자 패턴
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

// 생산자
CompletableFuture.runAsync(() -> {
    for (int i = 0; i < 100; i++) {
        try {
            queue.put("Item " + i);  // 큐가 가득 차면 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});

// 소비자
CompletableFuture.runAsync(() -> {
    while (true) {
        try {
            String item = queue.take();  // 큐가 비어있으면 대기
            System.out.println("처리: " + item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});
```

## 16. 메모리 관리와 성능 최적화

### 16.1 WeakReference와 SoftReference

```java
import java.lang.ref.*;

// Strong Reference (일반 참조)
String strongRef = new String("Strong");

// Weak Reference - GC 대상이 되면 즉시 정리
WeakReference<String> weakRef = new WeakReference<>(new String("Weak"));
String retrieved = weakRef.get();  // null일 수 있음

// Soft Reference - 메모리 부족할 때만 정리
SoftReference<byte[]> softRef = new SoftReference<>(new byte[1024 * 1024]);
byte[] data = softRef.get();  // 메모리 여유 있으면 유지

// 캐시 구현 예시
public class SimpleCache<K, V> {
    private final Map<K, SoftReference<V>> cache = new ConcurrentHashMap<>();
    
    public V get(K key) {
        SoftReference<V> ref = cache.get(key);
        if (ref != null) {
            V value = ref.get();
            if (value != null) {
                return value;
            } else {
                cache.remove(key);  // GC된 항목 제거
            }
        }
        return null;
    }
    
    public void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }
}
```

### 16.2 메모리 모니터링

```java
import java.lang.management.*;

public class MemoryMonitor {
    public static void printMemoryInfo() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        // Heap 메모리 정보
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        System.out.println("=== Heap Memory ===");
        System.out.println("Init: " + formatBytes(heapUsage.getInit()));
        System.out.println("Used: " + formatBytes(heapUsage.getUsed()));
        System.out.println("Committed: " + formatBytes(heapUsage.getCommitted()));
        System.out.println("Max: " + formatBytes(heapUsage.getMax()));
        
        // Non-Heap 메모리 정보 (Method Area, Code Cache 등)
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        System.out.println("=== Non-Heap Memory ===");
        System.out.println("Used: " + formatBytes(nonHeapUsage.getUsed()));
        
        // GC 정보
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("GC Name: " + gcBean.getName());
            System.out.println("Collection Count: " + gcBean.getCollectionCount());
            System.out.println("Collection Time: " + gcBean.getCollectionTime() + "ms");
        }
    }
    
    private static String formatBytes(long bytes) {
        return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
    }
}
```

## 17. 함수형 인터페이스 완전 가이드

### 17.1 기본 함수형 인터페이스들

```java
import java.util.function.*;

// Predicate<T> - 조건 검사
Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isLong = s -> s.length() > 10;
Predicate<String> combined = isEmpty.or(isLong);

// Function<T, R> - 변환
Function<String, Integer> stringToLength = String::length;
Function<Integer, String> intToString = Object::toString;
Function<String, String> composite = stringToLength.andThen(intToString);

// Consumer<T> - 소비 (반환값 없음)
Consumer<String> printer = System.out::println;
Consumer<String> logger = s -> System.err.println("LOG: " + s);
Consumer<String> both = printer.andThen(logger);

// Supplier<T> - 공급 (매개변수 없음)
Supplier<String> randomString = () -> "Random-" + Math.random();
Supplier<List<String>> listSupplier = ArrayList::new;

// BiFunction<T, U, R> - 두 매개변수
BiFunction<String, String, String> concatenator = (a, b) -> a + b;
BiFunction<Integer, Integer, Integer> adder = Integer::sum;

// UnaryOperator<T> - 같은 타입 변환
UnaryOperator<String> toUpper = String::toUpperCase;
UnaryOperator<Integer> square = x -> x * x;

// BinaryOperator<T> - 같은 타입 두 매개변수
BinaryOperator<String> stringJoiner = (a, b) -> a + ", " + b;
BinaryOperator<Integer> max = Integer::max;
```

### 17.2 특수 함수형 인터페이스들

```java
// 기본 타입 특화 인터페이스들 (박싱/언박싱 오버헤드 제거)
IntPredicate isEven = x -> x % 2 == 0;
IntFunction<String> intToString = Integer::toString;
ToIntFunction<String> stringToInt = Integer::parseInt;

IntSupplier randomInt = () -> (int)(Math.random() * 100);
IntConsumer printer = System.out::println;

IntUnaryOperator doubler = x -> x * 2;
IntBinaryOperator adder = Integer::sum;

// 스트림에서 활용
IntStream.range(1, 10)
    .filter(isEven)
    .map(doubler)
    .forEach(printer);

// 커스텀 함수형 인터페이스
@FunctionalInterface
interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
    
    // 기본 메서드 추가 가능
    default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
        return (t, u, v) -> after.apply(apply(t, u, v));
    }
}

TriFunction<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;
Integer result = sum3.apply(1, 2, 3);  // 6
```

## 18. 고급 스트림 패턴들

### 18.1 복잡한 데이터 변환

```java
// 중첩 컬렉션 평면화
List<List<String>> nestedList = Arrays.asList(
    Arrays.asList("A", "B"),
    Arrays.asList("C", "D", "E"),
    Arrays.asList("F")
);

List<String> flattened = nestedList.stream()
    .flatMap(Collection::stream)
    .collect(Collectors.toList());  // [A, B, C, D, E, F]

// 파일에서 단어 추출하고 빈도 계산
Map<String, Long> wordFrequency = Files.lines(path)
    .flatMap(line -> Arrays.stream(line.toLowerCase().split("\\s+")))
    .filter(word -> !word.isEmpty())
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));

// 상위 N개 추출
List<Map.Entry<String, Long>> topWords = wordFrequency.entrySet().stream()
    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
    .limit(10)
    .collect(Collectors.toList());
```

### 18.2 커스텀 Collector 작성

```java
// 표준편차 계산 Collector
public static Collector<Double, ?, Double> standardDeviation() {
    return Collector.of(
        () -> new double[3],  // [count, sum, sumOfSquares]
        (acc, value) -> {
            acc[0]++;                    // count
            acc[1] += value;             // sum
            acc[2] += value * value;     // sum of squares
        },
        (acc1, acc2) -> {
            acc1[0] += acc2[0];
            acc1[1] += acc2[1];
            acc1[2] += acc2[2];
            return acc1;
        },
        acc -> {
            double count = acc[0];
            double mean = acc[1] / count;
            double variance = (acc[2] / count) - (mean * mean);
            return Math.sqrt(variance);
        }
    );
}

// 사용 예시
List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
Double stdDev = values.stream().collect(standardDeviation());
```

### 18.3 병렬 처리 최적화

```java
public class ParallelOptimization {
    // CPU 집약적 작업에 적합
    public static List<Integer> parallelCpuIntensive(List<Integer> numbers) {
        return numbers.parallelStream()
            .map(n -> {
                // 복잡한 계산 시뮬레이션
                int result = n;
                for (int i = 0; i < 1000; i++) {
                    result = (result * 31 + 1) % 1000000;
                }
                return result;
            })
            .collect(Collectors.toList());
    }
    
    // I/O 집약적 작업은 일반적으로 부적합
    public static void ioIntensiveExample() {
        List<String> urls = Arrays.asList(
            "http://example1.com",
            "http://example2.com",
            "http://example3.com"
        );
        
        // 이런 경우는 CompletableFuture가 더 적합
        List<CompletableFuture<String>> futures = urls.stream()
            .map(url -> CompletableFuture.supplyAsync(() -> fetchData(url)))
            .collect(Collectors.toList());
        
        List<String> results = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }
    
    private static String fetchData(String url) {
        // HTTP 요청 시뮬레이션
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        return "Data from " + url;
    }
}
```

## 19. 보안과 암호화

### 19.1 MessageDigest를 이용한 해싱

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {
    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        // 바이트를 16진수 문자열로 변환
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    // 솔트를 이용한 안전한 해싱
    public static String hashWithSalt(String password, String salt) throws NoSuchAlgorithmException {
        return sha256(password + salt);
    }
    
    // 다양한 해시 알고리즘
    public static void demonstrateHashAlgorithms() throws NoSuchAlgorithmException {
        String input = "Hello World";
        
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        
        System.out.println("MD5: " + bytesToHex(md5.digest(input.getBytes())));
        System.out.println("SHA-1: " + bytesToHex(sha1.digest(input.getBytes())));
        System.out.println("SHA-256: " + bytesToHex(sha256.digest(input.getBytes())));
        System.out.println("SHA-512: " + bytesToHex(sha512.digest(input.getBytes())));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

### 19.2 Base64 인코딩/디코딩

```java
import java.util.Base64;

public class Base64Utils {
    public static void demonstrateBase64() {
        String original = "Hello, World! 한글도 포함된 텍스트";
        byte[] originalBytes = original.getBytes(StandardCharsets.UTF_8);
        
        // 기본 Base64 인코딩
        String encoded = Base64.getEncoder().encodeToString(originalBytes);
        System.out.println("Encoded: " + encoded);
        
        // 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("Decoded: " + decoded);
        
        // URL-safe Base64 (URL에 안전한 문자만 사용)
        String urlSafeEncoded = Base64.getUrlEncoder().encodeToString(originalBytes);
        byte[] urlSafeDecoded = Base64.getUrlDecoder().decode(urlSafeEncoded);
        
        // MIME Base64 (76자마다 줄바꿈)
        String mimeEncoded = Base64.getMimeEncoder().encodeToString(originalBytes);
        byte[] mimeDecoded = Base64.getMimeDecoder().decode(mimeEncoded);
    }
}
```

## 20. 고급 제네릭과 타입 안전성

### 20.1 와일드카드와 경계

```java
// 상한 경계 와일드카드 (Upper Bound)
public static double sumOfNumbers(List<? extends Number> numbers) {
    double sum = 0;
    for (Number num : numbers) {
        sum += num.doubleValue();  // 읽기만 가능
    }
    return sum;
}

// 하한 경계 와일드카드 (Lower Bound)
public static void addNumbers(List<? super Integer> numbers) {
    numbers.add(42);        // 쓰기 가능
    numbers.add(100);       // Integer나 그 하위 타입만 추가 가능
    // Integer value = numbers.get(0);  // 컴파일 오류! 읽기 불가
}

// PECS 원칙: Producer Extends, Consumer Super
public static <T> void copy(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {
        destination.add(item);
    }
}

// 타입 소거와 런타임 정보
public static void typeErasureExample() {
    List<String> stringList = new ArrayList<>();
    List<Integer> intList = new ArrayList<>();
    
    // 런타임에는 모두 List 타입
    System.out.println(stringList.getClass() == intList.getClass());  // true
    
    // 제네릭 정보는 컴파일 타임에만 존재
    Class<?> clazz = stringList.getClass();
    Type[] genericTypes = clazz.getGenericInterfaces();
}
```

### 20.2 타입 토큰 패턴

```java
// 타입 안전한 맵 구현
public class TypeSafeMap {
    private final Map<Class<?>, Object> map = new HashMap<>();
    
    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) map.get(type);
    }
}

// 사용 예시
TypeSafeMap safeMap = new TypeSafeMap();
safeMap.put(String.class, "Hello");
safeMap.put(Integer.class, 42);

String str = safeMap.get(String.class);    // 자동 캐스팅, 타입 안전
Integer num = safeMap.get(Integer.class);  // 자동 캐스팅, 타입 안전
```

## 21. 어노테이션과 메타프로그래밍

### 21.1 기본 어노테이션들

```java
// 메서드 오버라이드 검증
@Override
public String toString() {
    return "Custom toString";
}

// 경고 억제
@SuppressWarnings({"unchecked", "deprecation"})
public void legacyCode() {
    List rawList = new ArrayList();  // 경고 억제됨
}

// 사용 중단 표시
@Deprecated(since = "1.5", forRemoval = true)
public void oldMethod() {
    // 더 이상 사용하지 않는 메서드
}

// 함수형 인터페이스 명시
@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}
```

### 21.2 커스텀 어노테이션

```java
// 어노테이션 정의
@Retention(RetentionPolicy.RUNTIME)  // 런타임까지 유지
@Target({ElementType.METHOD, ElementType.TYPE})  // 메서드와 클래스에 적용
public @interface Benchmark {
    String value() default "";
    int iterations() default 1;
}

// 어노테이션 사용
@Benchmark(value = "성능 테스트", iterations = 1000)
public void performanceMethod() {
    // 성능 측정 대상 메서드
}

// 리플렉션으로 어노테이션 처리
public static void processBenchmarks(Object obj) {
    Class<?> clazz = obj.getClass();
    
    for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(Benchmark.class)) {
            Benchmark benchmark = method.getAnnotation(Benchmark.class);
            
            System.out.println("벤치마크: " + benchmark.value());
            
            // 메서드 실행 시간 측정
            long startTime = System.nanoTime();
            for (int i = 0; i < benchmark.iterations(); i++) {
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.nanoTime();
            
            System.out.println("평균 실행 시간: " + 
                (endTime - startTime) / benchmark.iterations() + " ns");
        }
    }
}
```

## 22. 로깅과 디버깅

### 22.1 java.util.logging 활용

```java
import java.util.logging.*;

public class LoggingExample {
    private static final Logger LOGGER = Logger.getLogger(LoggingExample.class.getName());
    
    static {
        // 로깅 설정
        LOGGER.setLevel(Level.ALL);
        
        // 콘솔 핸들러
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        
        // 파일 핸들러
        try {
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (Exception e) {
            LOGGER.severe("파일 핸들러 설정 실패: " + e.getMessage());
        }
        
        LOGGER.addHandler(consoleHandler);
    }
    
    public void demonstrateLogging() {
        LOGGER.severe("심각한 오류");
        LOGGER.warning("경고 메시지");
        LOGGER.info("정보 메시지");
        LOGGER.config("설정 정보");
        LOGGER.fine("상세 정보");
        LOGGER.finer("더 상세한 정보");
        LOGGER.finest("가장 상세한 정보");
        
        // 매개변수가 있는 로깅
        LOGGER.log(Level.INFO, "사용자 {0}이 {1}에 로그인했습니다", 
                  new Object[]{"홍길동", new Date()});
        
        // 예외와 함께 로깅
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            LOGGER.log(Level.SEVERE, "계산 오류 발생", e);
        }
    }
}
```

### 22.2 성능 프로파일링

```java
public class PerformanceProfiler {
    private static final Map<String, Long> timings = new ConcurrentHashMap<>();
    
    // 메서드 실행 시간 측정
    public static <T> T timeMethod(String methodName, Supplier<T> method) {
        long start = System.nanoTime();
        try {
            return method.get();
        } finally {
            long duration = System.nanoTime() - start;
            timings.merge(methodName, duration, Long::sum);
            System.out.printf("%s: %.2f ms%n", methodName, duration / 1_000_000.0);
        }
    }
    
    // 메모리 사용량 측정
    public static void measureMemoryUsage(String operation, Runnable task) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();  // 가비지 컬렉션 수행
        
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        task.run();
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.printf("%s 메모리 사용량: %d bytes%n", 
                         operation, afterMemory - beforeMemory);
    }
    
    // 통계 출력
    public static void printStatistics() {
        System.out.println("=== 성능 통계 ===");
        timings.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("%s: %.2f ms%n", 
                                entry.getKey(), 
                                entry.getValue() / 1_000_000.0));
    }
}
```

## 23. 고급 I/O와 NIO

### 23.1 Non-blocking I/O

```java
import java.nio.*;
import java.nio.channels.*;

public class NIOExample {
    public static void readFileWithNIO(String fileName) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            
            // 버퍼 할당
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            
            while (channel.read(buffer) != -1) {
                buffer.flip();  // 읽기 모드로 전환
                
                // 데이터 처리
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                
                buffer.clear();  // 쓰기 모드로 전환
            }
        }
    }
    
    public static void copyFileWithNIO(String source, String destination) throws Exception {
        try (FileChannel sourceChannel = FileChannel.open(Paths.get(source), StandardOpenOption.READ);
             FileChannel destChannel = FileChannel.open(Paths.get(destination), 
                                                       StandardOpenOption.CREATE, 
                                                       StandardOpenOption.WRITE)) {
            
            // 효율적인 파일 복사
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }
}
```

### 23.2 메모리 매핑된 파일

```java
public class MemoryMappedFile {
    public static void processLargeFileWithMapping(String fileName) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            
            // 파일을 메모리에 매핑
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY, 0, fileSize);
            
            // 직접 메모리 접근으로 빠른 처리
            int lineCount = 0;
            while (buffer.hasRemaining()) {
                if (buffer.get() == '\n') {
                    lineCount++;
                }
            }
            
            System.out.println("라인 수: " + lineCount);
        }
    }
}
```

## 24. 국제화와 지역화 (i18n/l10n)

### 24.1 Locale과 ResourceBundle

```java
import java.util.*;

public class InternationalizationExample {
    public static void demonstrateLocalization() {
        // 다양한 Locale 생성
        Locale korean = new Locale("ko", "KR");
        Locale english = Locale.ENGLISH;
        Locale japanese = Locale.JAPANESE;
        
        // 시스템 기본 로케일
        Locale defaultLocale = Locale.getDefault();
        
        // 리소스 번들 사용
        ResourceBundle koreanBundle = ResourceBundle.getBundle("messages", korean);
        ResourceBundle englishBundle = ResourceBundle.getBundle("messages", english);
        
        System.out.println("Korean: " + koreanBundle.getString("greeting"));
        System.out.println("English: " + englishBundle.getString("greeting"));
        
        // 숫자 포맷팅
        NumberFormat koreanFormat = NumberFormat.getInstance(korean);
        NumberFormat englishFormat = NumberFormat.getInstance(english);
        
        double number = 1234567.89;
        System.out.println("Korean: " + koreanFormat.format(number));    // 1,234,567.89
        System.out.println("English: " + englishFormat.format(number));  // 1,234,567.89
        
        // 통화 포맷팅
        NumberFormat koreanCurrency = NumberFormat.getCurrencyInstance(korean);
        NumberFormat englishCurrency = NumberFormat.getCurrencyInstance(Locale.US);
        
        System.out.println("Korean: " + koreanCurrency.format(number));   // ₩1,234,568
        System.out.println("US: " + englishCurrency.format(number));      // $1,234,567.89
        
        // 날짜 포맷팅
        DateFormat koreanDate = DateFormat.getDateInstance(DateFormat.FULL, korean);
        DateFormat englishDate = DateFormat.getDateInstance(DateFormat.FULL, english);
        
        Date now = new Date();
        System.out.println("Korean: " + koreanDate.format(now));
        System.out.println("English: " + englishDate.format(now));
    }
}

// messages_ko_KR.properties
// greeting=안녕하세요
// farewell=안녕히 가세요

// messages_en_US.properties  
// greeting=Hello
// farewell=Goodbye
```

### 24.2 MessageFormat을 이용한 고급 메시지 포맷팅

```java
import java.text.MessageFormat;

public class AdvancedFormatting {
    public static void demonstrateMessageFormat() {
        // 기본 메시지 포맷팅
        String pattern = "사용자 {0}님이 {1,date,short}에 {2,number,integer}개의 파일을 업로드했습니다.";
        Object[] arguments = {"홍길동", new Date(), 5};
        String message = MessageFormat.format(pattern, arguments);
        
        // 조건부 메시지 (ChoiceFormat)
        String choicePattern = "파일이 {0,choice,0#없습니다|1#1개 있습니다|1<{0,number,integer}개 있습니다}.";
        
        for (int i = 0; i <= 3; i++) {
            String result = MessageFormat.format(choicePattern, i);
            System.out.println("파일 " + i + "개: " + result);
        }
        
        // 복수형 처리
        MessageFormat pluralFormat = new MessageFormat(
            "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files} in the folder.",
            Locale.ENGLISH
        );
        
        for (int i = 0; i <= 3; i++) {
            System.out.println(pluralFormat.format(new Object[]{i}));
        }
    }
}
```

## 25. JVM 모니터링과 관리

### 25.1 MXBean을 이용한 JVM 모니터링

```java
import java.lang.management.*;

public class JVMMonitoring {
    public static void printSystemInfo() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        // JVM 정보
        System.out.println("=== JVM 정보 ===");
        System.out.println("JVM 이름: " + runtimeBean.getVmName());
        System.out.println("JVM 버전: " + runtimeBean.getVmVersion());
        System.out.println("JVM 업타임: " + runtimeBean.getUptime() + "ms");
        System.out.println("클래스패스: " + runtimeBean.getClassPath());
        
        // 운영체제 정보
        System.out.println("\n=== OS 정보 ===");
        System.out.println("OS 이름: " + osBean.getName());
        System.out.println("OS 버전: " + osBean.getVersion());
        System.out.println("프로세서 수: " + osBean.getAvailableProcessors());
        System.out.println("시스템 부하: " + osBean.getSystemLoadAverage());
        
        // 스레드 정보
        System.out.println("\n=== 스레드 정보 ===");
        System.out.println("활성 스레드 수: " + threadBean.getThreadCount());
        System.out.println("데몬 스레드 수: " + threadBean.getDaemonThreadCount());
        System.out.println("최대 스레드 수: " + threadBean.getPeakThreadCount());
        System.out.println("총 시작된 스레드 수: " + threadBean.getTotalStartedThreadCount());
        
        // 데드락 감지
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        if (deadlockedThreads != null) {
            System.out.println("데드락 감지된 스레드: " + Arrays.toString(deadlockedThreads));
        }
    }
    
    public static void monitorGarbageCollection() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        
        System.out.println("=== GC 정보 ===");
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("GC 이름: " + gcBean.getName());
            System.out.println("수집 횟수: " + gcBean.getCollectionCount());
            System.out.println("수집 시간: " + gcBean.getCollectionTime() + "ms");
            System.out.println("메모리 풀: " + Arrays.toString(gcBean.getMemoryPoolNames()));
        }
    }
}
```

## 26. 고급 동시성 패턴

### 26.1 Fork/Join Framework

```java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

// 병렬 계산을 위한 Fork/Join 태스크
public class ParallelSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;
    private final int[] array;
    private final int start, end;
    
    public ParallelSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // 작은 작업은 직접 계산
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // 큰 작업은 분할
            int mid = (start + end) / 2;
            ParallelSum leftTask = new ParallelSum(array, start, mid);
            ParallelSum rightTask = new ParallelSum(array, mid, end);
            
            leftTask.fork();  // 비동기 실행
            long rightResult = rightTask.compute();  // 현재 스레드에서 실행
            long leftResult = leftTask.join();       // 결과 대기
            
            return leftResult + rightResult;
        }
    }
    
    public static void demonstrateForkJoin() {
        int[] largeArray = new int[10_000_000];
        Arrays.fill(largeArray, 1);  // 모든 원소를 1로 초기화
        
        ForkJoinPool pool = new ForkJoinPool();
        ParallelSum task = new ParallelSum(largeArray, 0, largeArray.length);
        
        long start = System.currentTimeMillis();
        Long result = pool.invoke(task);
        long end = System.currentTimeMillis();
        
        System.out.println("결과: " + result);
        System.out.println("실행 시간: " + (end - start) + "ms");
        
        pool.shutdown();
    }
}
```

### 26.2 StampedLock (Java 8+)

```java
import java.util.concurrent.locks.StampedLock;

public class OptimizedReadWriteLock {
    private final StampedLock lock = new StampedLock();
    private double x, y;
    
    // 낙관적 읽기 (Optimistic Read)
    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();  // 낙관적 읽기 시도
        double currentX = x, currentY = y;      // 변수 복사
        
        if (!lock.validate(stamp)) {  // 읽기 도중 쓰기가 발생했는지 확인
            // 낙관적 읽기 실패, 읽기 락 획득
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
    
    // 쓰기 락
    public void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    // 읽기 락을 쓰기 락으로 업그레이드
    public void moveIfAtOrigin(double newX, double newY) {
        long stamp = lock.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                long writeStamp = lock.tryConvertToWriteLock(stamp);
                if (writeStamp != 0L) {
                    // 업그레이드 성공
                    stamp = writeStamp;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    // 업그레이드 실패, 다시 시도
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }
}
```

## 27. 함수형 프로그래밍 고급 패턴

### 27.1 Monad 패턴 구현

```java
// Maybe 모나드 (Optional과 유사하지만 더 함수형)
public class Maybe<T> {
    private final T value;
    
    private Maybe(T value) {
        this.value = value;
    }
    
    public static <T> Maybe<T> of(T value) {
        return new Maybe<>(value);
    }
    
    public static <T> Maybe<T> nothing() {
        return new Maybe<>(null);
    }
    
    public <U> Maybe<U> map(Function<T, U> mapper) {
        return value == null ? nothing() : of(mapper.apply(value));
    }
    
    public <U> Maybe<U> flatMap(Function<T, Maybe<U>> mapper) {
        return value == null ? nothing() : mapper.apply(value);
    }
    
    public T orElse(T defaultValue) {
        return value == null ? defaultValue : value;
    }
    
    // 사용 예시
    public static void demonstrateMaybe() {
        Maybe<String> maybe = Maybe.of("Hello")
            .map(String::toUpperCase)
            .flatMap(s -> Maybe.of(s + " World"))
            .map(s -> s + "!");
        
        String result = maybe.orElse("Nothing");
        System.out.println(result);  // "HELLO WORLD!"
    }
}
```

### 27.2 커링(Currying)과 부분 적용

```java
// 커링 함수 구현
public class CurryingExample {
    // 3개 매개변수를 받는 함수를 1개씩 받는 함수들로 변환
    public static Function<Integer, Function<Integer, Function<Integer, Integer>>> 
            curriedAdd = a -> b -> c -> a + b + c;
    
    // 부분 적용 함수
    public static Function<Integer, Function<Integer, Integer>> add10 = curriedAdd.apply(10);
    public static Function<Integer, Integer> add10And20 = add10.apply(20);
    
    public static void demonstrateCurrying() {
        // 단계별 적용
        int result1 = curriedAdd.apply(1).apply(2).apply(3);  // 6
        
        // 부분 적용 활용
        int result2 = add10And20.apply(5);  // 35 (10 + 20 + 5)
        
        // 함수 조합
        Function<String, Integer> stringLength = String::length;
        Function<Integer, String> intToString = Object::toString;
        Function<String, String> lengthToString = stringLength.andThen(intToString);
        
        String result3 = lengthToString.apply("Hello");  // "5"
    }
}
```

## 28. 실무에서 자주 사용하는 유틸리티 패턴들

### 28.1 빌더 패턴의 고급 구현

```java
public class Person {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    private final List<String> hobbies;
    
    private Person(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.email = builder.email;
        this.hobbies = List.copyOf(builder.hobbies);  // 불변 복사
    }
    
    public static class Builder {
        private String firstName;
        private String lastName;
        private int age;
        private String email;
        private List<String> hobbies = new ArrayList<>();
        
        public Builder firstName(String firstName) {
            this.firstName = Objects.requireNonNull(firstName);
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = Objects.requireNonNull(lastName);
            return this;
        }
        
        public Builder age(int age) {
            if (age < 0) throw new IllegalArgumentException("나이는 음수일 수 없습니다");
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            if (!email.contains("@")) throw new IllegalArgumentException("유효하지 않은 이메일");
            this.email = email;
            return this;
        }
        
        public Builder addHobby(String hobby) {
            this.hobbies.add(Objects.requireNonNull(hobby));
            return this;
        }
        
        public Builder hobbies(Collection<String> hobbies) {
            this.hobbies.clear();
            this.hobbies.addAll(hobbies);
            return this;
        }
        
        public Person build() {
            Objects.requireNonNull(firstName, "이름은 필수입니다");
            Objects.requireNonNull(lastName, "성은 필수입니다");
            return new Person(this);
        }
    }
    
    // 사용 예시
    public static void demonstrateBuilder() {
        Person person = new Person.Builder()
            .firstName("길동")
            .lastName("홍")
            .age(30)
            .email("hong@example.com")
            .addHobby("독서")
            .addHobby("영화감상")
            .build();
    }
}
```

### 28.2 팩토리 메서드 패턴

```java
// 다양한 타입의 Logger 생성
public abstract class Logger {
    public abstract void log(String message);
    
    // 팩토리 메서드
    public static Logger console() {
        return new ConsoleLogger();
    }
    
    public static Logger file(String fileName) {
        return new FileLogger(fileName);
    }
    
    public static Logger composite(Logger... loggers) {
        return new CompositeLogger(Arrays.asList(loggers));
    }
    
    // 구현 클래스들
    private static class ConsoleLogger extends Logger {
        @Override
        public void log(String message) {
            System.out.println("[CONSOLE] " + message);
        }
    }
    
    private static class FileLogger extends Logger {
        private final String fileName;
        
        FileLogger(String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public void log(String message) {
            try {
                Files.write(Paths.get(fileName), 
                           (LocalDateTime.now() + ": " + message + "\n").getBytes(),
                           StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.err.println("파일 로깅 실패: " + e.getMessage());
            }
        }
    }
    
    private static class CompositeLogger extends Logger {
        private final List<Logger> loggers;
        
        CompositeLogger(List<Logger> loggers) {
            this.loggers = new ArrayList<>(loggers);
        }
        
        @Override
        public void log(String message) {
            loggers.forEach(logger -> logger.log(message));
        }
    }
}
```

### 28.3 캐시 구현 패턴

```java
public class Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long maxAge;
    private final int maxSize;
    
    public Cache(long maxAgeMillis, int maxSize) {
        this.maxAge = maxAgeMillis;
        this.maxSize = maxSize;
    }
    
    public V get(K key, Supplier<V> valueSupplier) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null || entry.isExpired()) {
            // 캐시 미스 또는 만료
            V value = valueSupplier.get();
            cache.put(key, new CacheEntry<>(value));
            
            // 크기 제한 확인
            if (cache.size() > maxSize) {
                evictOldest();
            }
            
            return value;
        }
        
        return entry.getValue();
    }
    
    private void evictOldest() {
        K oldestKey = cache.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }
    
    private class CacheEntry<T> implements Comparable<CacheEntry<T>> {
        private final T value;
        private final long timestamp;
        
        CacheEntry(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > maxAge;
        }
        
        T getValue() { return value; }
        
        @Override
        public int compareTo(CacheEntry<T> other) {
            return Long.compare(this.timestamp, other.timestamp);
        }
    }
}
```

## 29. 성능 최적화 실무 패턴

### 29.1 객체 풀링

```java
// 비용이 큰 객체의 재사용
public class ObjectPool<T> {
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final Supplier<T> factory;
    private final Consumer<T> resetFunction;
    private final int maxSize;
    private final AtomicInteger currentSize = new AtomicInteger(0);
    
    public ObjectPool(Supplier<T> factory, Consumer<T> resetFunction, int maxSize) {
        this.factory = factory;
        this.resetFunction = resetFunction;
        this.maxSize = maxSize;
    }
    
    public T acquire() {
        T object = pool.poll();
        if (object == null) {
            object = factory.get();
            currentSize.incrementAndGet();
        }
        return object;
    }
    
    public void release(T object) {
        if (object != null && currentSize.get() <= maxSize) {
            resetFunction.accept(object);
            pool.offer(object);
        }
    }
    
    // StringBuilder 풀 예시
    public static final ObjectPool<StringBuilder> STRING_BUILDER_POOL = 
        new ObjectPool<>(
            () -> new StringBuilder(256),  // 초기 용량 설정
            sb -> sb.setLength(0),         // 리셋 함수
            10                             // 최대 10개 유지
        );
    
    public static String buildString(String... parts) {
        StringBuilder sb = STRING_BUILDER_POOL.acquire();
        try {
            for (String part : parts) {
                sb.append(part);
            }
            return sb.toString();
        } finally {
            STRING_BUILDER_POOL.release(sb);
        }
    }
}
```

### 29.2 지연 초기화 패턴

```java
public class LazyInitialization {
    // 단순 지연 초기화 (스레드 안전하지 않음)
    private ExpensiveObject expensive;
    
    public ExpensiveObject getExpensive() {
        if (expensive == null) {
            expensive = new ExpensiveObject();
        }
        return expensive;
    }
    
    // 스레드 안전한 지연 초기화
    private volatile ExpensiveObject safeExpensive;
    
    public ExpensiveObject getSafeExpensive() {
        ExpensiveObject result = safeExpensive;  // 로컬 변수로 복사 (성능 최적화)
        if (result == null) {
            synchronized (this) {
                result = safeExpensive;
                if (result == null) {  // Double-checked locking
                    safeExpensive = result = new ExpensiveObject();
                }
            }
        }
        return result;
    }
    
    // Holder 패턴을 이용한 지연 초기화 (가장 효율적)
    private static class ExpensiveHolder {
        static final ExpensiveObject INSTANCE = new ExpensiveObject();
    }
    
    public static ExpensiveObject getInstance() {
        return ExpensiveHolder.INSTANCE;  // 클래스 로딩 시점에 초기화
    }
    
    // Supplier를 이용한 함수형 지연 초기화
    private final Supplier<ExpensiveObject> lazySupplier = 
        Suppliers.memoize(ExpensiveObject::new);  // Guava 라이브러리 필요
    
    // Java 표준만으로 구현
    private final Supplier<ExpensiveObject> memoizedSupplier = new Supplier<ExpensiveObject>() {
        private volatile ExpensiveObject instance;
        
        @Override
        public ExpensiveObject get() {
            ExpensiveObject result = instance;
            if (result == null) {
                synchronized (this) {
                    result = instance;
                    if (result == null) {
                        instance = result = new ExpensiveObject();
                    }
                }
            }
            return result;
        }
    };
}

class ExpensiveObject {
    // 생성 비용이 큰 객체 시뮬레이션
    public ExpensiveObject() {
        try {
            Thread.sleep(100);  // 초기화에 시간이 오래 걸리는 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## 30. 고급 예외 처리 패턴

### 30.1 Result 패턴 구현

```java
// 예외 대신 결과 객체를 반환하는 패턴
public abstract class Result<T, E extends Exception> {
    public abstract boolean isSuccess();
    public abstract boolean isFailure();
    public abstract T getValue() throws E;
    public abstract E getError();
    
    public static <T, E extends Exception> Result<T, E> success(T value) {
        return new Success<>(value);
    }
    
    public static <T, E extends Exception> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }
    
    // 함수형 메서드들
    public abstract <U> Result<U, E> map(Function<T, U> mapper);
    public abstract <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper);
    public abstract Result<T, E> filter(Predicate<T> predicate, Supplier<E> errorSupplier);
    
    // 구현 클래스들
    private static class Success<T, E extends Exception> extends Result<T, E> {
        private final T value;
        
        Success(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isSuccess() { return true; }
        
        @Override
        public boolean isFailure() { return false; }
        
        @Override
        public T getValue() { return value; }
        
        @Override
        public E getError() { return null; }
        
        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            try {
                return success(mapper.apply(value));
            } catch (Exception e) {
                @SuppressWarnings("unchecked")
                E castedException = (E) e;
                return failure(castedException);
            }
        }
        
        @Override
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return mapper.apply(value);
        }
        
        @Override
        public Result<T, E> filter(Predicate<T> predicate, Supplier<E> errorSupplier) {
            return predicate.test(value) ? this : failure(errorSupplier.get());
        }
    }
    
    private static class Failure<T, E extends Exception> extends Result<T, E> {
        private final E error;
        
        Failure(E error) {
            this.error = error;
        }
        
        @Override
        public boolean isSuccess() { return false; }
        
        @Override
        public boolean isFailure() { return true; }
        
        @Override
        public T getValue() throws E { throw error; }
        
        @Override
        public E getError() { return error; }
        
        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return (Result<U, E>) this;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return (Result<U, E>) this;
        }
        
        @Override
        public Result<T, E> filter(Predicate<T> predicate, Supplier<E> errorSupplier) {
            return this;
        }
    }
    
    // 사용 예시
    public static Result<Integer, NumberFormatException> parseInteger(String str) {
        try {
            return success(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return failure(e);
        }
    }
    
    public static void demonstrateResult() {
        Result<Integer, NumberFormatException> result = parseInteger("123")
            .map(x -> x * 2)
            .filter(x -> x > 100, () -> new NumberFormatException("값이 너무 작습니다"));
        
        if (result.isSuccess()) {
            System.out.println("결과: " + result.getValue());
        } else {
            System.out.println("오류: " + result.getError().getMessage());
        }
    }
}
```

### 30.2 예외 체이닝과 컨텍스트 정보

```java
public class AdvancedExceptionHandling {
    // 커스텀 예외 클래스
    public static class BusinessException extends Exception {
        private final String errorCode;
        private final Map<String, Object> context;
        
        public BusinessException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
            this.context = new HashMap<>();
        }
        
        public BusinessException(String message, String errorCode, Throwable cause) {
            super(message, cause);
            this.errorCode = errorCode;
            this.context = new HashMap<>();
        }
        
        public BusinessException addContext(String key, Object value) {
            context.put(key, value);
            return this;
        }
        
        public String getErrorCode() { return errorCode; }
        public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append("\n에러 코드: ").append(errorCode);
            if (!context.isEmpty()) {
                sb.append("\n컨텍스트: ").append(context);
            }
            return sb.toString();
        }
    }
    
    // 예외 체이닝 예시
    public static void processUserData(String userId) throws BusinessException {
        try {
            validateUser(userId);
            loadUserData(userId);
            processData(userId);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("사용자 데이터 처리 실패", "USER_PROCESSING_ERROR", e)
                .addContext("userId", userId)
                .addContext("timestamp", System.currentTimeMillis());
        }
    }
    
    private static void validateUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID가 비어있습니다");
        }
    }
    
    private static void loadUserData(String userId) {
        // 데이터 로딩 로직
    }
    
    private static void processData(String userId) {
        // 데이터 처리 로직
    }
    
    // 리소스 관리를 위한 try-with-resources 고급 활용
    public static void advancedResourceManagement() {
        // 여러 리소스 동시 관리
        try (FileInputStream fis = new FileInputStream("input.txt");
             FileOutputStream fos = new FileOutputStream("output.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
             PrintWriter writer = new PrintWriter(fos)) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line.toUpperCase());
            }
            
        } catch (IOException e) {
            System.err.println("파일 처리 중 오류: " + e.getMessage());
        }
        
        // 커스텀 AutoCloseable 리소스
        try (CustomResource resource = new CustomResource()) {
            resource.doSomething();
        }  // 자동으로 close() 호출
    }
    
    static class CustomResource implements AutoCloseable {
        public void doSomething() {
            System.out.println("리소스 사용 중");
        }
        
        @Override
        public void close() {
            System.out.println("리소스 해제됨");
        }
    }
}
```

## 31. 대용량 데이터 처리 패턴

### 31.1 스트림을 이용한 메모리 효율적 처리

```java
public class BigDataProcessing {
    // 대용량 파일을 청크 단위로 처리
    public static void processLargeFile(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            Map<String, Long> wordCount = lines
                .filter(line -> !line.trim().isEmpty())
                .flatMap(line -> Arrays.stream(line.toLowerCase().split("\\s+")))
                .filter(word -> word.length() > 3)
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.counting()
                ));
            
            // 상위 100개 단어만 메모리에 유지
            List<Map.Entry<String, Long>> top100 = wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(100)
                .collect(Collectors.toList());
            
            top100.forEach(entry -> 
                System.out.println(entry.getKey() + ": " + entry.getValue()));
        }
    }
    
    // 배치 처리 패턴
    public static <T> void processBatches(Stream<T> stream, int batchSize, Consumer<List<T>> processor) {
        List<T> batch = new ArrayList<>(batchSize);
        
        stream.forEach(item -> {
            batch.add(item);
            if (batch.size() >= batchSize) {
                processor.accept(new ArrayList<>(batch));  // 복사본 전달
                batch.clear();
            }
        });
        
        // 마지막 배치 처리
        if (!batch.isEmpty()) {
            processor.accept(batch);
        }
    }
    
    // 병렬 배치 처리
    public static void parallelBatchProcessing() {
        List<Integer> largeDataset = IntStream.range(0, 1_000_000)
            .boxed()
            .collect(Collectors.toList());
        
        // 1000개씩 배치로 나누어 병렬 처리
        largeDataset.parallelStream()
            .collect(Collectors.groupingBy(i -> i / 1000))  // 배치로 그룹핑
            .values()
            .parallelStream()
            .forEach(batch -> {
                // 각 배치를 병렬로 처리
                long sum = batch.stream().mapToLong(Integer::longValue).sum();
                System.out.println("배치 합계: " + sum);
            });
    }
}
```

### 31.2 메모리 매핑을 이용한 고성능 I/O

```java
public class HighPerformanceIO {
    // 대용량 파일 분석
    public static void analyzeLargeFile(String fileName) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            long chunkSize = 64 * 1024 * 1024;  // 64MB 청크
            
            for (long position = 0; position < fileSize; position += chunkSize) {
                long size = Math.min(chunkSize, fileSize - position);
                
                MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY, position, size);
                
                // 청크 단위로 처리
                processChunk(buffer);
                
                // 명시적 해제 (JVM 버전에 따라 다름)
                if (buffer instanceof sun.nio.ch.DirectBuffer) {
                    ((sun.nio.ch.DirectBuffer) buffer).cleaner().clean();
                }
            }
        }
    }
    
    private static void processChunk(ByteBuffer buffer) {
        int lineCount = 0;
        while (buffer.hasRemaining()) {
            if (buffer.get() == '\n') {
                lineCount++;
            }
        }
        System.out.println("청크의 라인 수: " + lineCount);
    }
    
    // 고성능 파일 쓰기
    public static void writeDataEfficiently(String fileName, List<String> data) throws IOException {
        try (FileChannel channel = FileChannel.open(
                Paths.get(fileName), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // 버퍼 풀을 사용한 효율적 쓰기
            ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
            
            for (String line : data) {
                byte[] bytes = (line + "\n").getBytes(StandardCharsets.UTF_8);
                
                if (buffer.remaining() < bytes.length) {
                    buffer.flip();
                    channel.write(buffer);
                    buffer.clear();
                }
                
                buffer.put(bytes);
            }
            
            // 남은 데이터 쓰기
            if (buffer.position() > 0) {
                buffer.flip();
                channel.write(buffer);
            }
        }
    }
}
```

## 32. 디자인 패턴 구현

### 32.1 Observer 패턴 (이벤트 시스템)

```java
// 함수형 스타일의 Observer 패턴
public class EventSystem<T> {
    private final List<Consumer<T>> observers = new CopyOnWriteArrayList<>();
    
    public void subscribe(Consumer<T> observer) {
        observers.add(observer);
    }
    
    public void unsubscribe(Consumer<T> observer) {
        observers.remove(observer);
    }
    
    public void publish(T event) {
        observers.forEach(observer -> {
            try {
                observer.accept(event);
            } catch (Exception e) {
                System.err.println("Observer에서 예외 발생: " + e.getMessage());
            }
        });
    }
    
    // 비동기 이벤트 처리
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public void publishAsync(T event) {
        observers.forEach(observer -> 
            executor.submit(() -> {
                try {
                    observer.accept(event);
                } catch (Exception e) {
                    System.err.println("비동기 Observer에서 예외 발생: " + e.getMessage());
                }
            })
        );
    }
    
    // 필터링된 구독
    public void subscribeFiltered(Predicate<T> filter, Consumer<T> observer) {
        subscribe(event -> {
            if (filter.test(event)) {
                observer.accept(event);
            }
        });
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}

// 사용 예시
class UserEvent {
    private final String userId;
    private final String action;
    private final LocalDateTime timestamp;
    
    public UserEvent(String userId, String action) {
        this.userId = userId;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }
    
    // getters...
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

public class EventSystemDemo {
    public static void demonstrate() {
        EventSystem<UserEvent> eventSystem = new EventSystem<>();
        
        // 로깅 Observer
        eventSystem.subscribe(event -> 
            System.out.println("LOG: " + event.getUserId() + " " + event.getAction()));
        
        // 보안 모니터링 Observer (로그인 이벤트만)
        eventSystem.subscribeFiltered(
            event -> "LOGIN".equals(event.getAction()),
            event -> System.out.println("SECURITY: 로그인 감지 - " + event.getUserId())
        );
        
        // 이벤트 발생
        eventSystem.publish(new UserEvent("user123", "LOGIN"));
        eventSystem.publish(new UserEvent("user456", "LOGOUT"));
        
        eventSystem.shutdown();
    }
}
```

### 32.2 Command 패턴과 Undo/Redo

```java
// Command 인터페이스
interface Command {
    void execute();
    void undo();
    String getDescription();
}

// 텍스트 에디터 예시
class TextEditor {
    private StringBuilder content = new StringBuilder();
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();  // 새 명령 실행 시 redo 스택 초기화
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
    
    // 구체적인 Command 구현들
    public class InsertCommand implements Command {
        private final int position;
        private final String text;
        
        public InsertCommand(int position, String text) {
            this.position = position;
            this.text = text;
        }
        
        @Override
        public void execute() {
            content.insert(position, text);
        }
        
        @Override
        public void undo() {
            content.delete(position, position + text.length());
        }
        
        @Override
        public String getDescription() {
            return "Insert '" + text + "' at " + position;
        }
    }
    
    public class DeleteCommand implements Command {
        private final int position;
        private final int length;
        private String deletedText;
        
        public DeleteCommand(int position, int length) {
            this.position = position;
            this.length = length;
        }
        
        @Override
        public void execute() {
            deletedText = content.substring(position, position + length);
            content.delete(position, position + length);
        }
        
        @Override
        public void undo() {
            content.insert(position, deletedText);
        }
        
        @Override
        public String getDescription() {
            return "Delete " + length + " characters at " + position;
        }
    }
    
    // 매크로 명령 (복합 명령)
    public class MacroCommand implements Command {
        private final List<Command> commands;
        private final String description;
        
        public MacroCommand(String description, Command... commands) {
            this.description = description;
            this.commands = Arrays.asList(commands);
        }
        
        @Override
        public void execute() {
            commands.forEach(Command::execute);
        }
        
        @Override
        public void undo() {
            // 역순으로 undo 실행
            for (int i = commands.size() - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
        }
        
        @Override
        public String getDescription() {
            return description;
        }
    }
    
    public String getContent() {
        return content.toString();
    }
}
```

## 33. 반응형 프로그래밍 기초

### 33.1 Publisher-Subscriber 패턴

```java
import java.util.concurrent.Flow.*;

// 커스텀 Publisher 구현
public class NumberPublisher implements Publisher<Integer> {
    private final int max;
    
    public NumberPublisher(int max) {
        this.max = max;
    }
    
    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new NumberSubscription(subscriber, max));
    }
    
    private static class NumberSubscription implements Subscription {
        private final Subscriber<? super Integer> subscriber;
        private final int max;
        private int current = 0;
        private boolean cancelled = false;
        
        NumberSubscription(Subscriber<? super Integer> subscriber, int max) {
            this.subscriber = subscriber;
            this.max = max;
        }
        
        @Override
        public void request(long n) {
            if (cancelled) return;
            
            for (long i = 0; i < n && current < max && !cancelled; i++) {
                subscriber.onNext(current++);
            }
            
            if (current >= max && !cancelled) {
                subscriber.onComplete();
            }
        }
        
        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}

// Subscriber 구현
public class NumberSubscriber implements Subscriber<Integer> {
    private Subscription subscription;
    private final int batchSize;
    
    public NumberSubscriber(int batchSize) {
        this.batchSize = batchSize;
    }
    
    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(batchSize);  // 첫 배치 요청
    }
    
    @Override
    public void onNext(Integer item) {
        System.out.println("받은 숫자: " + item);
        
        // 처리 후 다음 배치 요청
        if (item % batchSize == batchSize - 1) {
            subscription.request(batchSize);
        }
    }
    
    @Override
    public void onError(Throwable throwable) {
        System.err.println("오류 발생: " + throwable.getMessage());
    }
    
    @Override
    public void onComplete() {
        System.out.println("스트림 완료");
    }
}

// 사용 예시
public class ReactiveDemo {
    public static void demonstrate() {
        NumberPublisher publisher = new NumberPublisher(100);
        NumberSubscriber subscriber = new NumberSubscriber(10);
        
        publisher.subscribe(subscriber);
        
        // 비동기 처리를 위해 잠시 대기
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## 34. 성능 측정과 벤치마킹

### 34.1 JMH (Java Microbenchmark Harness) 스타일 벤치마킹

```java
// 수동 벤치마킹 유틸리티
public class ManualBenchmark {
    private final String name;
    private final int warmupIterations;
    private final int measurementIterations;
    
    public ManualBenchmark(String name, int warmupIterations, int measurementIterations) {
        this.name = name;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
    }
    
    public void benchmark(Runnable operation) {
        System.out.println("벤치마크: " + name);
        
        // Warmup (JIT 컴파일러 최적화를 위해)
        System.out.println("워밍업 중...");
        for (int i = 0; i < warmupIterations; i++) {
            operation.run();
        }
        
        // 실제 측정
        System.out.println("측정 중...");
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        
        for (int i = 0; i < measurementIterations; i++) {
            long start = System.nanoTime();
            operation.run();
            long end = System.nanoTime();
            
            long duration = end - start;
            totalTime += duration;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
        }
        
        double avgTime = (double) totalTime / measurementIterations;
        
        System.out.printf("평균: %.2f ms%n", avgTime / 1_000_000);
        System.out.printf("최소: %.2f ms%n", minTime / 1_000_000.0);
        System.out.printf("최대: %.2f ms%n", maxTime / 1_000_000.0);
        System.out.println("---");
    }
    
    // 비교 벤치마크
    public static void compareMethods() {
        List<String> data = IntStream.range(0, 10000)
            .mapToObj(i -> "String " + i)
            .collect(Collectors.toList());
        
        ManualBenchmark benchmark = new ManualBenchmark("String Concatenation", 1000, 5000);
        
        // StringBuilder 방식
        benchmark.benchmark(() -> {
            StringBuilder sb = new StringBuilder();
            for (String s : data) {
                sb.append(s);
            }
            sb.toString();
        });
        
        // Stream 방식
        benchmark.benchmark(() -> {
            data.stream().collect(Collectors.joining());
        });
        
        // String.join 방식
        benchmark.benchmark(() -> {
            String.join("", data);
        });
    }
}
```

### 34.2 메모리 프로파일링

```java
public class MemoryProfiler {
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    public static class MemorySnapshot {
        private final long heapUsed;
        private final long heapMax;
        private final long nonHeapUsed;
        private final long timestamp;
        
        public MemorySnapshot() {
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            
            this.heapUsed = heapUsage.getUsed();
            this.heapMax = heapUsage.getMax();
            this.nonHeapUsed = nonHeapUsage.getUsed();
            this.timestamp = System.currentTimeMillis();
        }
        
        public long getHeapUsedMB() { return heapUsed / 1024 / 1024; }
        public long getHeapMaxMB() { return heapMax / 1024 / 1024; }
        public long getNonHeapUsedMB() { return nonHeapUsed / 1024 / 1024; }
        public double getHeapUsagePercent() { return (double) heapUsed / heapMax * 100; }
    }
    
    public static <T> T profileMemory(String operation, Supplier<T> task) {
        // 가비지 컬렉션 수행
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        
        MemorySnapshot before = new MemorySnapshot();
        T result = task.get();
        MemorySnapshot after = new MemorySnapshot();
        
        long memoryIncrease = after.getHeapUsedMB() - before.getHeapUsedMB();
        
        System.out.printf("=== %s 메모리 프로파일 ===%n", operation);
        System.out.printf("이전 힙 사용량: %d MB%n", before.getHeapUsedMB());
        System.out.printf("이후 힙 사용량: %d MB%n", after.getHeapUsedMB());
        System.out.printf("메모리 증가량: %d MB%n", memoryIncrease);
        System.out.printf("힙 사용률: %.1f%%%n", after.getHeapUsagePercent());
        System.out.println();
        
        return result;
    }
    
    // 메모리 누수 감지
    public static void detectMemoryLeaks() {
        List<MemorySnapshot> snapshots = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            // 메모리를 사용하는 작업 시뮬레이션
            List<byte[]> memoryHog = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                memoryHog.add(new byte[1024]);
            }
            
            System.gc();
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            snapshots.add(new MemorySnapshot());
            memoryHog.clear();  // 참조 해제
        }
        
        // 메모리 사용량 트렌드 분석
        System.out.println("메모리 사용량 트렌드:");
        for (int i = 0; i < snapshots.size(); i++) {
            MemorySnapshot snapshot = snapshots.get(i);
            System.out.printf("반복 %d: %d MB (%.1f%%)%n", 
                            i, snapshot.getHeapUsedMB(), snapshot.getHeapUsagePercent());
        }
    }
}
```

## 35. 고급 문자열 처리

### 35.1 고성능 문자열 알고리즘

```java
public class StringAlgorithms {
    // KMP 알고리즘을 이용한 문자열 검색
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        int[] lps = computeLPSArray(pattern);
        
        int textIndex = 0, patternIndex = 0;
        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
            }
            
            if (patternIndex == pattern.length()) {
                occurrences.add(textIndex - patternIndex);
                patternIndex = lps[patternIndex - 1];
            } else if (textIndex < text.length() && 
                      text.charAt(textIndex) != pattern.charAt(patternIndex)) {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }
        
        return occurrences;
    }
    
    private static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    // 레벤슈타인 거리 (편집 거리)
    public static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i-1][j] + 1,      // 삭제
                    dp[i][j-1] + 1),     // 삽입
                    dp[i-1][j-1] + cost  // 치환
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    // 문자열 유사도 계산
    public static double similarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;
        
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLength;
    }
}
```

### 35.2 고급 텍스트 처리

```java
public class AdvancedTextProcessing {
    // 단어 빈도 분석
    public static Map<String, Long> analyzeWordFrequency(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
            .filter(word -> !word.isEmpty())
            .filter(word -> word.length() > 2)  // 2글자 이하 제외
            .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new,  // 순서 유지
                Collectors.counting()
            ));
    }
    
    // 텍스트 요약 (간단한 추출적 요약)
    public static List<String> extractiveSummary(String text, int sentenceCount) {
        String[] sentences = text.split("[.!?]+");
        Map<String, Long> wordFreq = analyzeWordFrequency(text);
        
        return Arrays.stream(sentences)
            .filter(sentence -> sentence.trim().length() > 10)
            .collect(Collectors.toMap(
                Function.identity(),
                sentence -> calculateSentenceScore(sentence, wordFreq)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(sentenceCount)
            .map(Map.Entry::getKey)
            .map(String::trim)
            .collect(Collectors.toList());
    }
    
    private static Long calculateSentenceScore(String sentence, Map<String, Long> wordFreq) {
        return Arrays.stream(sentence.toLowerCase().split("\\W+"))
            .filter(word -> !word.isEmpty())
            .mapToLong(word -> wordFreq.getOrDefault(word, 0L))
            .sum();
    }
    
    // 텍스트 정규화
    public static String normalizeText(String text) {
        return text
            .toLowerCase()                           // 소문자 변환
            .replaceAll("[^a-z0-9\\s]", "")         // 특수문자 제거
            .replaceAll("\\s+", " ")                // 연속 공백을 하나로
            .trim();                                // 앞뒤 공백 제거
    }
    
    // 카멜케이스/스네이크케이스 변환
    public static String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    public static String snakeToCamel(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                result.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            }
        }
        
        return result.toString();
    }
}
```

## 36. 실무 통합 예제

### 36.1 로그 분석 시스템

```java
// 로그 엔트리 클래스 (Record 사용)
public record LogEntry(
    LocalDateTime timestamp,
    String level,
    String logger,
    String message,
    String thread
) {
    public static LogEntry parse(String logLine) {
        // 로그 형식: "2025-08-04 14:30:15.123 [INFO ] [main] com.example.App - 애플리케이션 시작"
        Pattern pattern = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) " +
            "\\[(\\w+)\\s*\\] " +
            "\\[(\\w+)\\] " +
            "(\\S+) - (.+)$"
        );
        
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.matches()) {
            LocalDateTime timestamp = LocalDateTime.parse(
                matcher.group(1), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            );
            
            return new LogEntry(
                timestamp,
                matcher.group(2).trim(),
                matcher.group(4),
                matcher.group(5),
                matcher.group(3)
            );
        }
        
        throw new IllegalArgumentException("잘못된 로그 형식: " + logLine);
    }
}

// 로그 분석기
public class LogAnalyzer {
    private final List<LogEntry> logs;
    
    public LogAnalyzer(Path logFile) throws IOException {
        this.logs = Files.lines(logFile)
            .filter(line -> !line.trim().isEmpty())
            .map(LogEntry::parse)
            .collect(Collectors.toList());
    }
    
    // 시간대별 로그 분포
    public Map<String, Long> getHourlyDistribution() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                log -> log.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH")),
                TreeMap::new,
                Collectors.counting()
            ));
    }
    
    // 로그 레벨별 통계
    public Map<String, Long> getLevelStatistics() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::level,
                Collectors.counting()
            ));
    }
    
    // 에러 패턴 분석
    public List<String> findErrorPatterns() {
        return logs.stream()
            .filter(log -> "ERROR".equals(log.level()))
            .map(LogEntry::message)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .filter(entry -> entry.getValue() > 1)  // 2회 이상 발생
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    // 성능 이슈 감지 (특정 시간 내 에러 급증)
    public List<LocalDateTime> detectPerformanceIssues(Duration timeWindow, int errorThreshold) {
        return logs.stream()
            .filter(log -> "ERROR".equals(log.level()))
            .collect(Collectors.groupingBy(
                log -> log.timestamp().truncatedTo(ChronoUnit.MINUTES),
                TreeMap::new,
                Collectors.counting()
            ))
            .entrySet().stream()
            .filter(entry -> entry.getValue() > errorThreshold)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    // 리포트 생성
    public void generateReport() {
        System.out.println("=== 로그 분석 리포트 ===");
        System.out.println("총 로그 엔트리: " + logs.size());
        
        System.out.println("\n레벨별 통계:");
        getLevelStatistics().forEach((level, count) -> 
            System.out.printf("  %s: %d개%n", level, count));
        
        System.out.println("\n시간대별 분포 (상위 10개):");
        getHourlyDistribution().entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> 
                System.out.printf("  %s: %d개%n", entry.getKey(), entry.getValue()));
        
        List<String> errorPatterns = findErrorPatterns();
        if (!errorPatterns.isEmpty()) {
            System.out.println("\n반복되는 에러 패턴:");
            errorPatterns.stream()
                .limit(5)
                .forEach(pattern -> System.out.println("  " + pattern));
        }
    }
}
```

### 36.2 데이터 처리 파이프라인

```java
// 함수형 파이프라인 구현
public class DataPipeline<T> {
    private final List<Function<Stream<T>, Stream<T>>> stages = new ArrayList<>();
    
    public DataPipeline<T> filter(Predicate<T> predicate) {
        stages.add(stream -> stream.filter(predicate));
        return this;
    }
    
    public <R> DataPipeline<R> map(Function<T, R> mapper) {
        @SuppressWarnings("unchecked")
        DataPipeline<R> newPipeline = (DataPipeline<R>) this;
        newPipeline.stages.add(stream -> stream.map(mapper));
        return newPipeline;
    }
    
    public DataPipeline<T> distinct() {
        stages.add(Stream::distinct);
        return this;
    }
    
    public DataPipeline<T> sorted(Comparator<T> comparator) {
        stages.add(stream -> stream.sorted(comparator));
        return this;
    }
    
    public DataPipeline<T> limit(long maxSize) {
        stages.add(stream -> stream.limit(maxSize));
        return this;
    }
    
    public DataPipeline<T> parallel() {
        stages.add(Stream::parallel);
        return this;
    }
    
    // 파이프라인 실행
    public Stream<T> execute(Stream<T> input) {
        Stream<T> result = input;
        for (Function<Stream<T>, Stream<T>> stage : stages) {
            result = stage.apply(result);
        }
        return result;
    }
    
    // 결과 수집
    public <R> R collect(Stream<T> input, Collector<T, ?, R> collector) {
        return execute(input).collect(collector);
    }
    
    // 사용 예시: 판매 데이터 분석
    public static void salesDataAnalysis() {
        List<SaleRecord> sales = generateSampleSales();
        
        DataPipeline<SaleRecord> pipeline = new DataPipeline<SaleRecord>()
            .filter(sale -> sale.amount() > 1000)           // 고액 매출만
            .filter(sale -> sale.date().isAfter(LocalDate.now().minusMonths(1))) // 최근 1개월
            .sorted(Comparator.comparing(SaleRecord::amount).reversed())          // 금액 내림차순
            .limit(100);                                    // 상위 100건
        
        List<SaleRecord> topSales = pipeline.collect(
            sales.stream(),
            Collectors.toList()
        );
        
        // 분석 결과 출력
        double totalAmount = topSales.stream()
            .mapToDouble(SaleRecord::amount)
            .sum();
            
        System.out.println("상위 100건 매출 합계: $" + String.format("%.2f", totalAmount));
        
        Map<String, Double> salesByProduct = topSales.stream()
            .collect(Collectors.groupingBy(
                SaleRecord::product,
                Collectors.summingDouble(SaleRecord::amount)
            ));
        
        System.out.println("제품별 매출:");
        salesByProduct.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> 
                System.out.printf("  %s: $%.2f%n", entry.getKey(), entry.getValue()));
    }
    
    private static List<SaleRecord> generateSampleSales() {
        Random random = new Random();
        List<String> products = Arrays.asList("노트북", "마우스", "키보드", "모니터", "스피커");
        
        return IntStream.range(0, 10000)
            .mapToObj(i -> new SaleRecord(
                "SALE" + i,
                products.get(random.nextInt(products.size())),
                500 + random.nextDouble() * 3000,  // $500-$3500
                LocalDate.now().minusDays(random.nextInt(90))  // 최근 90일
            ))
            .collect(Collectors.toList());
    }
}

record SaleRecord(String id, String product, double amount, LocalDate date) {}
```

## 37. 정리 및 실무 활용 가이드

### 37.1 성능 최적화 체크리스트

```java
public class PerformanceChecklist {
    // 1. 컬렉션 선택 가이드
    public static void collectionSelectionGuide() {
        /*
        List 선택:
        - 순차 접근 위주 → ArrayList
        - 빈번한 삽입/삭제 (중간) → LinkedList
        - 스레드 안전 필요 → Vector 또는 Collections.synchronizedList()
        
        Set 선택:
        - 빠른 검색 → HashSet
        - 정렬된 순서 → TreeSet
        - 삽입 순서 유지 → LinkedHashSet
        
        Map 선택:
        - 일반적 용도 → HashMap
        - 정렬된 키 → TreeMap
        - 삽입 순서 유지 → LinkedHashMap
        - 스레드 안전 → ConcurrentHashMap
        */
    }
    
    // 2. 문자열 처리 최적화
    public static void stringOptimization() {
        // 좋은 예시
        StringBuilder sb = new StringBuilder(예상크기);
        for (String part : parts) {
            sb.append(part);
        }
        String result = sb.toString();
        
        // 나쁜 예시
        String result = "";
        for (String part : parts) {
            result += part;  // 매번 새 객체 생성
        }
    }
    
    // 3. 스트림 vs 반복문 선택 가이드
    public static void streamVsLoop() {
        /*
        스트림 사용하기 좋은 경우:
        - 복잡한 데이터 변환
        - 함수형 스타일 선호
        - 병렬 처리 필요
        - 가독성이 중요한 경우
        
        반복문 사용하기 좋은 경우:
        - 단순한 반복
        - 성능이 극도로 중요한 경우
        - 중간에 break/continue 필요
        - 외부 변수 수정 필요
        */
    }
}
```

### 37.2 실무 코딩 패턴 모음

```java
public class PracticalPatterns {
    // 1. Null 안전 체인 호출
    public static String safeGetUserEmail(User user) {
        return Optional.ofNullable(user)
            .map(User::getProfile)
            .map(Profile::getEmail)
            .orElse("unknown@example.com");
    }
    
    // 2. 설정 관리 패턴
    public static class ConfigManager {
        private final Properties properties = new Properties();
        
        public ConfigManager(String configFile) {
            try (InputStream is = getClass().getResourceAsStream(configFile)) {
                properties.load(is);
            } catch (IOException e) {
                throw new RuntimeException("설정 파일 로드 실패", e);
            }
        }
        
        public String getString(String key, String defaultValue) {
            return properties.getProperty(key, defaultValue);
        }
        
        public int getInt(String key, int defaultValue) {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        }
        
        public boolean getBoolean(String key, boolean defaultValue) {
            String value = properties.getProperty(key);
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        }
    }
    
    // 3. 재시도 패턴
    public static <T> T retry(Supplier<T> operation, int maxAttempts, Duration delay) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    System.out.println("시도 " + attempt + " 실패, " + delay.toMillis() + "ms 후 재시도");
                    try {
                        Thread.sleep(delay.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("재시도 중 인터럽트 발생", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("모든 재시도 실패", lastException);
    }
    
    // 4. 회로차단기 패턴 (Circuit Breaker)
    public static class CircuitBreaker<T> {
        private enum State { CLOSED, OPEN, HALF_OPEN }
        
        private volatile State state = State.CLOSED;
        private volatile int failureCount = 0;
        private volatile long lastFailureTime = 0;
        
        private final int failureThreshold;
        private final Duration timeout;
        private final Supplier<T> fallback;
        
        public CircuitBreaker(int failureThreshold, Duration timeout, Supplier<T> fallback) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
            this.fallback = fallback;
        }
        
        public T execute(Supplier<T> operation) {
            if (state == State.OPEN && 
                System.currentTimeMillis() - lastFailureTime < timeout.toMillis()) {
                return fallback.get();
            }
            
            try {
                T result = operation.get();
                onSuccess();
                return result;
            } catch (Exception e) {
                onFailure();
                return fallback.get();
            }
        }
        
        private synchronized void onSuccess() {
            failureCount = 0;
            state = State.CLOSED;
        }
        
        private synchronized void onFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            
            if (failureCount >= failureThreshold) {
                state = State.OPEN;
                System.out.println("회로차단기 OPEN 상태로 전환");
            }
        }
    }
    
    // 5. 캐시된 계산 결과
    public static class MemoizedFunction<T, R> implements Function<T, R> {
        private final Function<T, R> function;
        private final Map<T, R> cache = new ConcurrentHashMap<>();
        
        public MemoizedFunction(Function<T, R> function) {
            this.function = function;
        }
        
        @Override
        public R apply(T input) {
            return cache.computeIfAbsent(input, function);
        }
        
        public void clearCache() {
            cache.clear();
        }
        
        public int getCacheSize() {
            return cache.size();
        }
    }
}

// 더미 클래스들
class User {
    public Profile getProfile() { return new Profile(); }
}

class Profile {
    public String getEmail() { return "user@example.com"; }
}
```

### 37.3 테스트 유틸리티

```java
public class TestUtils {
    // 성능 테스트 헬퍼
    public static void performanceTest(String testName, Runnable test, int iterations) {
        System.out.println("=== " + testName + " ===");
        
        // 워밍업
        for (int i = 0; i < Math.min(iterations / 10, 100); i++) {
            test.run();
        }
        
        // 측정
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            test.run();
        }
        long end = System.nanoTime();
        
        double avgTime = (end - start) / 1_000_000.0 / iterations;
        System.out.printf("평균 실행 시간: %.3f ms%n", avgTime);
        System.out.printf("총 실행 시간: %.3f ms%n", (end - start) / 1_000_000.0);
        System.out.println();
    }
    
    // 메모리 테스트 헬퍼
    public static void memoryTest(String testName, Supplier<Object> test) {
        System.out.println("=== " + testName + " 메모리 테스트 ===");
        
        System.gc();
        long beforeMemory = getUsedMemory();
        
        Object result = test.get();
        
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;
        
        System.out.printf("사용된 메모리: %.2f MB%n", memoryUsed / 1024.0 / 1024.0);
        
        // 메모리 누수 방지를 위해 결과 참조 유지
        System.out.println("결과 객체 타입: " + result.getClass().getSimpleName());
        System.out.println();
    }
    
    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    // 동시성 테스트 헬퍼
    public static void concurrencyTest(String testName, Runnable test, int threadCount) {
        System.out.println("=== " + testName + " 동시성 테스트 ===");
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();  // 모든 스레드가 동시 시작
                    test.run();
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        
        long start = System.currentTimeMillis();
        startLatch.countDown();  // 모든 스레드 시작
        
        try {
            endLatch.await();  // 모든 스레드 완료 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long end = System.currentTimeMillis();
        
        System.out.printf("실행 시간: %d ms%n", end - start);
        System.out.printf("스레드 수: %d%n", threadCount);
        System.out.printf("예외 발생 수: %d%n", exceptions.size());
        
        if (!exceptions.isEmpty()) {
            System.out.println("발생한 예외들:");
            exceptions.forEach(e -> System.out.println("  " + e.getMessage()));
        }
        System.out.println();
    }
}
```

## 38. 마무리: 실무 적용 가이드

### 38.1 코드 품질 향상을 위한 함수 활용법

1. **불변성 선호**: `List.of()`, `Map.of()`, `Collections.unmodifiableXxx()` 활용
2. **Null 안전성**: `Optional`, `Objects.requireNonNull()`, `Objects.equals()` 활용
3. **함수형 스타일**: Stream API, Lambda, Method Reference 적극 활용
4. **예외 처리**: try-with-resources, Result 패턴, 체이닝된 예외 활용
5. **성능 고려**: 적절한 컬렉션 선택, StringBuilder 사용, 병렬 스트림 신중히 활용

### 38.2 학습 로드맵

1. **기초 단계**: System, Math, String, 기본 컬렉션
2. **중급 단계**: Stream API, Optional, 람다 표현식, 날짜/시간 API
3. **고급 단계**: 동시성, NIO, 리플렉션, 어노테이션
4. **실무 단계**: 디자인 패턴, 성능 최적화, 대용량 데이터 처리

### 38.3 지속적인 학습을 위한 팁

- **공식 문서 읽기**: Oracle Java Documentation을 정기적으로 확인
- **소스 코드 분석**: JDK 소스 코드를 직접 읽어보며 내부 구현 이해
- **성능 측정**: 실제 프로젝트에서 성능을 측정하고 최적화 경험 쌓기
- **새 기능 추적**: 각 Java 버전별 새로운 기능들을 지속적으로 학습

이 가이드는 Java의 핵심 내장 함수들을 체계적으로 정리한 것입니다. 실무에서는 이러한 기본기를 바탕으로 더 복잡한 프레임워크와 라이브러리를 효과적으로 활용할 수 있습니다. 각 함수의 내부 동작 원리를 이해하고, 적절한 상황에서 올바른 함수를 선택하는 것이 고품질 코드 작성의 핵심입니다.