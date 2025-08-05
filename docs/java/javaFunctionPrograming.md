# Java 함수형 프로그래밍 완벽 가이드

## 📖 목차
1. [함수형 프로그래밍 기초 개념](#1-함수형-프로그래밍-기초-개념)
2. [함수형 인터페이스 완전 정복](#2-함수형-인터페이스-완전-정복)
3. [Lambda 표현식 마스터하기](#3-lambda-표현식-마스터하기)
4. [Stream API 심화 학습](#4-stream-api-심화-학습)
5. [Collector 고급 활용법](#5-collector-고급-활용법)
6. [Optional과 null-safe 프로그래밍](#6-optional과-null-safe-프로그래밍)
7. [병렬 프로그래밍과 성능 최적화](#7-병렬-프로그래밍과-성능-최적화)
8. [실무 패턴과 베스트 프랙티스](#8-실무-패턴과-베스트-프랙티스)

---

## 1. 함수형 프로그래밍 기초 개념

### 1.1 명령형 vs 선언형 프로그래밍

**기존 명령형 방식 (Java 7 이전)**
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
List<String> result = new ArrayList<>();

// 어떻게(How) 처리할지 명시
for (String name : names) {
    if (name.length() > 3) {
        result.add(name.toUpperCase());
    }
}
Collections.sort(result);
```

**함수형 선언형 방식 (Java 8+)**
```java
List<String> result = names.stream()
    .filter(name -> name.length() > 3)  // 무엇을(What) 원하는지 명시
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.toList());
```

### 1.2 함수형 프로그래밍의 핵심 원칙

**불변성 (Immutability)**
- 원본 데이터를 변경하지 않고 새로운 결과를 생성
- 부작용(Side Effect) 최소화로 안전한 코드 작성

**순수 함수 (Pure Function)**
- 동일한 입력에 대해 항상 동일한 출력
- 외부 상태에 의존하지 않음

**고차 함수 (Higher-Order Function)**
- 함수를 매개변수로 받거나 함수를 반환하는 함수
- Stream API의 `map`, `filter` 등이 대표적인 예

---

## 2. 함수형 인터페이스 완전 정복

### 2.1 Function 계열 - 변환과 매핑

**Function<T, R>: 단일 입력 → 출력**
```java
// 기본 사용법
Function<String, Integer> stringLength = s -> s.length();
Function<String, Integer> stringLengthRef = String::length; // 메서드 참조

// 실무 예제: DTO 변환
Function<User, UserDto> userToDto = user -> UserDto.builder()
    .id(user.getId())
    .name(user.getName())
    .email(user.getEmail())
    .build();

// 체인으로 연결 (compose, andThen)
Function<String, Integer> parseAndDouble = Integer::parseInt
    .andThen(x -> x * 2);

Function<String, String> trimAndUpper = String::trim
    .andThen(String::toUpperCase);
```

**BiFunction<T, U, R>: 두 입력 → 출력**
```java
// 계산기 함수
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
BiFunction<Integer, Integer, Integer> multiply = Integer::sum; // 잘못된 예
BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;

// 실무 예제: 두 객체 병합
BiFunction<User, Address, UserWithAddress> mergeUserAddress = 
    (user, address) -> new UserWithAddress(user, address);

// 문자열 포맷팅
BiFunction<String, Object[], String> formatter = String::format;
String result = formatter.apply("Hello %s, age: %d", new Object[]{"John", 25});
```

### 2.2 Predicate 계열 - 조건 검사와 필터링

**Predicate<T>: 조건 검사 → boolean**
```java
// 기본 사용법
Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate(); // 논리 반전

Predicate<Integer> isPositive = x -> x > 0;
Predicate<Integer> isEven = x -> x % 2 == 0;

// 조건 조합 (and, or, negate)
Predicate<Integer> isPositiveEven = isPositive.and(isEven);
Predicate<Integer> isPositiveOrEven = isPositive.or(isEven);

// 실무 예제: 사용자 검증
Predicate<User> isAdult = user -> user.getAge() >= 18;
Predicate<User> isActive = user -> user.getStatus() == UserStatus.ACTIVE;
Predicate<User> isValidUser = isAdult.and(isActive);

// 컬렉션 필터링에 활용
List<User> validUsers = users.stream()
    .filter(isValidUser)
    .collect(Collectors.toList());
```

**BiPredicate<T, U>: 두 값 조건 비교**
```java
// 두 문자열 길이 비교
BiPredicate<String, String> isSameLength = 
    (s1, s2) -> s1.length() == s2.length();

// 날짜 범위 체크
BiPredicate<LocalDate, LocalDate> isWithinRange = 
    (date, endDate) -> date.isBefore(endDate) || date.isEqual(endDate);

// 실무 예제: 권한 체크
BiPredicate<User, Resource> hasPermission = 
    (user, resource) -> user.getRole().getPermissions().contains(resource.getRequiredPermission());
```

### 2.3 Consumer 계열 - 소비와 부작용 처리

**Consumer<T>: 입력 받고 반환 없음**
```java
// 기본 출력
Consumer<String> print = System.out::println;
Consumer<String> printWithPrefix = s -> System.out.println("LOG: " + s);

// 체인으로 연결
Consumer<String> printAndLog = print.andThen(printWithPrefix);

// 실무 예제: 로깅과 모니터링
Consumer<User> auditUserAction = user -> {
    auditService.log("User action", user.getId());
    metricsService.increment("user.action.count");
};

// 리스트 처리
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(print); // 각 요소에 Consumer 적용
```

**BiConsumer<T, U>: 두 개 입력 소비**
```java
// Map 순회
Map<String, Integer> scores = Map.of("Alice", 95, "Bob", 87, "Charlie", 92);
BiConsumer<String, Integer> printScore = 
    (name, score) -> System.out.printf("%s: %d점%n", name, score);

scores.forEach(printScore);

// 실무 예제: 캐시 업데이트
BiConsumer<String, Object> updateCache = 
    (key, value) -> cacheManager.put(key, value, Duration.ofMinutes(30));
```

### 2.4 Supplier 계열 - 생성과 공급

**Supplier<T>: 매개변수 없이 값 생성**
```java
// 기본 사용법
Supplier<Double> randomValue = Math::random;
Supplier<LocalDateTime> currentTime = LocalDateTime::now;

// 객체 생성 팩토리
Supplier<User> defaultUser = () -> User.builder()
    .name("Guest")
    .status(UserStatus.INACTIVE)
    .createdAt(LocalDateTime.now())
    .build();

// 지연 초기화 (Lazy Initialization)
Supplier<ExpensiveResource> lazyResource = () -> new ExpensiveResource();

// Optional과 함께 사용
Optional<String> optional = Optional.empty();
String result = optional.orElseGet(() -> "기본값");

// 실무 예제: 설정값 공급
Supplier<DatabaseConfig> configSupplier = () -> DatabaseConfig.builder()
    .url(System.getenv("DB_URL"))
    .username(System.getenv("DB_USERNAME"))
    .password(System.getenv("DB_PASSWORD"))
    .build();
```

---

## 3. Lambda 표현식 마스터하기

### 3.1 Lambda 문법 완전 정복

**기본 문법 패턴**
```java
// 매개변수 없음
() -> "Hello World"
() -> { return "Hello World"; }

// 매개변수 1개 (괄호 생략 가능)
x -> x * 2
(x) -> x * 2

// 매개변수 2개 이상 (괄호 필수)
(x, y) -> x + y
(x, y) -> {
    int sum = x + y;
    return sum;
}

// 타입 명시 (보통 생략)
(Integer x, Integer y) -> x + y
```

**메서드 참조 (Method Reference) 활용**
```java
// 정적 메서드 참조
Function<String, Integer> parseInt = Integer::parseInt;

// 인스턴스 메서드 참조
Function<String, String> toUpper = String::toUpperCase;

// 특정 객체의 메서드 참조
String prefix = "Hello ";
Function<String, String> addPrefix = prefix::concat;

// 생성자 참조
Supplier<List<String>> listSupplier = ArrayList::new;
Function<String, User> userCreator = User::new;

// 배열 생성자 참조
Function<Integer, String[]> arrayCreator = String[]::new;
```

### 3.2 변수 캡처와 클로저

**Effectively Final 규칙**
```java
// 올바른 예: effectively final 변수
int multiplier = 2;
Function<Integer, Integer> multiply = x -> x * multiplier;

// 잘못된 예: 변경되는 변수 (컴파일 에러)
int counter = 0;
// counter++; // 이 줄이 있으면 아래 람다에서 컴파일 에러
Supplier<Integer> getCounter = () -> counter;

// 해결책: 참조 타입 사용
AtomicInteger atomicCounter = new AtomicInteger(0);
Supplier<Integer> getAtomicCounter = () -> atomicCounter.get();
Consumer<Integer> incrementCounter = x -> atomicCounter.addAndGet(x);
```

---

## 4. Stream API 심화 학습

### 4.1 Stream 생성 방법들

**다양한 Stream 생성**
```java
// 컬렉션에서 생성
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// 배열에서 생성
String[] array = {"x", "y", "z"};
Stream<String> stream2 = Arrays.stream(array);

// 직접 생성
Stream<String> stream3 = Stream.of("1", "2", "3");

// 빈 스트림
Stream<String> empty = Stream.empty();

// 무한 스트림
Stream<Integer> infinite = Stream.iterate(0, n -> n + 2); // 0, 2, 4, 6...
Stream<Double> random = Stream.generate(Math::random);

// 범위 스트림 (기본 타입 특화)
IntStream range = IntStream.range(1, 10); // 1~9
IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1~10
```

**파일과 I/O Stream**
```java
// 파일 라인별 읽기
try (Stream<String> lines = Files.lines(Paths.get("file.txt"))) {
    lines.filter(line -> !line.isEmpty())
         .forEach(System.out::println);
}

// 디렉토리 탐색
try (Stream<Path> paths = Files.walk(Paths.get("."))) {
    paths.filter(Files::isRegularFile)
         .filter(path -> path.toString().endsWith(".java"))
         .forEach(System.out::println);
}
```

### 4.2 중간 연산 상세 분석

**filter(): 조건 필터링**
```java
List<Product> products = getProducts();

// 단일 조건
List<Product> expensiveProducts = products.stream()
    .filter(product -> product.getPrice() > 100000)
    .collect(Collectors.toList());

// 복합 조건
List<Product> validProducts = products.stream()
    .filter(product -> product.getPrice() > 0)
    .filter(product -> product.getStock() > 0)
    .filter(product -> product.getCategory() != null)
    .collect(Collectors.toList());

// Predicate 조합
Predicate<Product> isAvailable = product -> product.getStock() > 0;
Predicate<Product> isAffordable = product -> product.getPrice() < 50000;
Predicate<Product> isRecommended = isAvailable.and(isAffordable);

List<Product> recommendedProducts = products.stream()
    .filter(isRecommended)
    .collect(Collectors.toList());
```

**map(): 변환과 매핑**
```java
// 기본 변환
List<String> names = users.stream()
    .map(User::getName)
    .collect(Collectors.toList());

// DTO 변환
List<UserDto> userDtos = users.stream()
    .map(user -> UserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build())
    .collect(Collectors.toList());

// 체인 변환
List<String> upperCaseNames = users.stream()
    .map(User::getName)
    .map(String::trim)
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// 숫자 변환
List<Double> prices = products.stream()
    .map(Product::getPrice)
    .map(price -> price * 1.1) // 10% 인상
    .collect(Collectors.toList());
```

**flatMap(): 중첩 구조 평탄화**
```java
// 중첩 리스트 평탄화
List<List<String>> nestedList = Arrays.asList(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d", "e"),
    Arrays.asList("f")
);

List<String> flatList = nestedList.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList()); // [a, b, c, d, e, f]

// 실무 예제: 사용자의 모든 주문 상품
List<User> users = getUsers();
List<Product> allOrderedProducts = users.stream()
    .flatMap(user -> user.getOrders().stream())
    .flatMap(order -> order.getItems().stream())
    .map(OrderItem::getProduct)
    .distinct()
    .collect(Collectors.toList());

// 문자열 단어 분리
List<String> sentences = Arrays.asList(
    "Hello World",
    "Java Stream API",
    "Functional Programming"
);

List<String> words = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .collect(Collectors.toList());
```

**distinct(), sorted(), limit(), skip()**
```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5);

// 중복 제거
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList()); // [1, 2, 3, 4, 5]

// 정렬
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());

// 역순 정렬
List<Integer> reverseSorted = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// 커스텀 정렬
List<User> sortedUsers = users.stream()
    .sorted(Comparator.comparing(User::getName)
                     .thenComparing(User::getAge))
    .collect(Collectors.toList());

// 페이징 (skip + limit)
List<Product> page = products.stream()
    .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
    .skip(page * size) // 건너뛸 개수
    .limit(size)       // 가져올 개수
    .collect(Collectors.toList());
```

**peek(): 디버깅과 중간 확인**
```java
List<String> result = names.stream()
    .filter(name -> name.startsWith("A"))
    .peek(name -> System.out.println("필터 후: " + name))
    .map(String::toUpperCase)
    .peek(name -> System.out.println("변환 후: " + name))
    .collect(Collectors.toList());

// 주의: peek는 중간 연산이므로 최종 연산이 없으면 실행되지 않음
names.stream()
    .peek(System.out::println) // 실행되지 않음
    .filter(name -> name.length() > 3);

// 올바른 사용
names.stream()
    .peek(System.out::println)
    .filter(name -> name.length() > 3)
    .collect(Collectors.toList()); // 이제 실행됨
```

### 4.3 최종 연산 완전 정복

**collect(): 수집과 변환**
```java
// 기본 컬렉션으로 수집
List<String> list = stream.collect(Collectors.toList());
Set<String> set = stream.collect(Collectors.toSet());
Map<String, User> map = users.stream()
    .collect(Collectors.toMap(User::getId, Function.identity()));

// 문자열 조합
String joined = names.stream()
    .collect(Collectors.joining(", ", "[", "]")); // [Alice, Bob, Charlie]

// 그룹핑
Map<String, List<User>> usersByCity = users.stream()
    .collect(Collectors.groupingBy(User::getCity));

// 분할 (참/거짓으로 나누기)
Map<Boolean, List<User>> usersByAdult = users.stream()
    .collect(Collectors.partitioningBy(user -> user.getAge() >= 18));
```

**reduce(): 축약과 집계**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 합계
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
// 또는
int sum2 = numbers.stream()
    .reduce(0, Integer::sum);

// 최댓값
Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);

// 문자열 연결
Optional<String> concatenated = names.stream()
    .reduce((s1, s2) -> s1 + ", " + s2);

// 복잡한 객체 축약
Optional<User> youngestUser = users.stream()
    .reduce((user1, user2) -> 
        user1.getAge() < user2.getAge() ? user1 : user2);

// 병렬 처리를 위한 3-parameter reduce
int parallelSum = numbers.parallelStream()
    .reduce(0,                    // identity
            Integer::sum,         // accumulator
            Integer::sum);        // combiner
```

**조건 검사 연산들**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 모든 요소가 조건을 만족하는가?
boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0); // true

// 하나라도 조건을 만족하는가?
boolean anyEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0); // true

// 모든 요소가 조건을 만족하지 않는가?
boolean noneNegative = numbers.stream()
    .noneMatch(n -> n < 0); // true

// 실무 예제
boolean allUsersActive = users.stream()
    .allMatch(user -> user.getStatus() == UserStatus.ACTIVE);

boolean hasAdminUser = users.stream()
    .anyMatch(user -> user.getRole() == Role.ADMIN);
```

**findFirst(), findAny()**
```java
// 첫 번째 요소 찾기
Optional<String> first = names.stream()
    .filter(name -> name.startsWith("A"))
    .findFirst();

// 임의의 요소 찾기 (병렬 처리에서 유용)
Optional<String> any = names.parallelStream()
    .filter(name -> name.startsWith("A"))
    .findAny();

// 실무 예제
Optional<User> adminUser = users.stream()
    .filter(user -> user.getRole() == Role.ADMIN)
    .findFirst();

// 기본값과 함께 사용
String firstAdminName = users.stream()
    .filter(user -> user.getRole() == Role.ADMIN)
    .map(User::getName)
    .findFirst()
    .orElse("관리자 없음");
```

---

## 5. Collector 고급 활용법

### 5.1 기본 Collector들

**toCollection(): 특정 컬렉션 타입으로 수집**
```java
// LinkedList로 수집 (순서 보장)
LinkedList<String> linkedList = names.stream()
    .collect(Collectors.toCollection(LinkedList::new));

// TreeSet으로 수집 (정렬된 중복 제거)
TreeSet<String> treeSet = names.stream()
    .collect(Collectors.toCollection(TreeSet::new));

// 불변 컬렉션으로 수집
List<String> immutableList = names.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    ));
```

**joining(): 문자열 조합**
```java
// 기본 연결
String simple = names.stream()
    .collect(Collectors.joining()); // "AliceBobCharlie"

// 구분자와 함께
String withDelimiter = names.stream()
    .collect(Collectors.joining(", ")); // "Alice, Bob, Charlie"

// 접두사, 접미사와 함께
String withPrefixSuffix = names.stream()
    .collect(Collectors.joining(", ", "[", "]")); // "[Alice, Bob, Charlie]"

// 실무 예제: SQL IN 절 생성
String sqlInClause = userIds.stream()
    .map(id -> "'" + id + "'")
    .collect(Collectors.joining(", ", "(", ")"));
// 결과: "('user1', 'user2', 'user3')"
```

### 5.2 그룹핑과 분할

**groupingBy(): 그룹화**
```java
// 단순 그룹화
Map<String, List<User>> usersByCity = users.stream()
    .collect(Collectors.groupingBy(User::getCity));

// 중첩 그룹화
Map<String, Map<Integer, List<User>>> usersByCityAndAge = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.groupingBy(User::getAge)
    ));

// 그룹화 후 개수 세기
Map<String, Long> userCountByCity = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.counting()
    ));

// 그룹화 후 평균 계산
Map<String, Double> averageAgeByCity = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.averagingInt(User::getAge)
    ));

// 그룹화 후 최댓값/최솟값
Map<String, Optional<User>> oldestByCity = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.maxBy(Comparator.comparing(User::getAge))
    ));

// Optional 제거
Map<String, User> oldestByCityUnwrapped = users.stream()
    .collect(Collectors.groupingBy(
        User::getCity,
        Collectors.collectingAndThen(
            Collectors.maxBy(Comparator.comparing(User::getAge)),
            Optional::get
        )
    ));
```

**partitioningBy(): 분할**
```java
// 참/거짓으로 분할
Map<Boolean, List<User>> usersByAdult = users.stream()
    .collect(Collectors.partitioningBy(user -> user.getAge() >= 18));

List<User> adults = usersByAdult.get(true);
List<User> minors = usersByAdult.get(false);

// 분할 후 개수 세기
Map<Boolean, Long> countByAdult = users.stream()
    .collect(Collectors.partitioningBy(
        user -> user.getAge() >= 18,
        Collectors.counting()
    ));

// 분할 후 추가 처리
Map<Boolean, List<String>> namesByAdult = users.stream()
    .collect(Collectors.partitioningBy(
        user -> user.getAge() >= 18,
        Collectors.mapping(User::getName, Collectors.toList())
    ));
```

### 5.3 통계 수집

**숫자 통계**
```java
List<Integer> scores = Arrays.asList(85, 92, 78, 96, 88);

// 기본 통계
IntSummaryStatistics stats = scores.stream()
    .collect(Collectors.summarizingInt(Integer::intValue));

System.out.println("Count: " + stats.getCount());      // 5
System.out.println("Sum: " + stats.getSum());          // 439
System.out.println("Average: " + stats.getAverage());  // 87.8
System.out.println("Min: " + stats.getMin());          // 78
System.out.println("Max: " + stats.getMax());          // 96

// 사용자 정의 객체 통계
DoubleSummaryStatistics salaryStats = employees.stream()
    .collect(Collectors.summarizingDouble(Employee::getSalary));

// 그룹별 통계
Map<String, IntSummaryStatistics> scoresBySubject = students.stream()
    .collect(Collectors.groupingBy(
        Student::getSubject,
        Collectors.summarizingInt(Student::getScore)
    ));
```

### 5.4 커스텀 Collector 만들기

**간단한 커스텀 Collector**
```java
// 리스트를 역순으로 수집하는 Collector
public static <T> Collector<T, ?, List<T>> toReversedList() {
    return Collector.of(
        ArrayList::new,                    // supplier
        (list, item) -> list.add(0, item), // accumulator
        (list1, list2) -> {                // combiner
            list2.addAll(list1);
            return list2;
        }
    );
}

// 사용 예
List<String> reversed = names.stream()
    .collect(toReversedList());

// 통계와 함께 수집하는 Collector
public static <T> Collector<T, ?, Map<String, Object>> toStatsMap(
        Function<T, ? extends Number> mapper) {
    return Collector.of(
        () -> new HashMap<String, Object>(),
        (map, item) -> {
            Number value = mapper.apply(item);
            map.merge("count", 1, (a, b) -> (Integer) a + (Integer) b);
            map.merge("sum", value.doubleValue(), (a, b) -> (Double) a + (Double) b);
        },
        (map1, map2) -> {
            map1.merge("count", map2.get("count"), (a, b) -> (Integer) a + (Integer) b);
            map1.merge("sum", map2.get("sum"), (a, b) -> (Double) a + (Double) b);
            return map1;
        },
        map -> {
            int count = (Integer) map.get("count");
            double sum = (Double) map.get("sum");
            map.put("average", sum / count);
            return map;
        }
    );
}
```

---

## 6. Optional과 null-safe 프로그래밍

### 6.1 Optional 기초

**Optional 생성**
```java
// 값이 있는 Optional
Optional<String> present = Optional.of("Hello");

// null 가능한 값으로 Optional 생성
String nullable = getName(); // null일 수 있음
Optional<String> maybe = Optional.ofNullable(nullable);

// 빈 Optional
Optional<String> empty = Optional.empty();

// 주의: null로 Optional.of() 호출하면 NPE 발생
// Optional<String> error = Optional.of(null); // NullPointerException!
```

**값 존재 확인**
```java
Optional<String> optional = Optional.ofNullable(getString());

// 값이 있는지 확인
if (optional.isPresent()) {
    String value = optional.get(); // 값이 있을 때만 사용
    System.out.println(value);
}

// 값이 없는지 확인 (Java 11+)
if (optional.isEmpty()) {
    System.out.println("값이 없습니다");
}

// 람다식으로 값이 있을 때만 처리
optional.ifPresent(value -> System.out.println("값: " + value));

// 값이 있을 때와 없을 때 각각 처리 (Java 9+)
optional.ifPresentOrElse(
    value -> System.out.println("값: " + value),
    () -> System.out.println("값이 없습니다")
);
```

### 6.2 Optional 값 추출과 기본값

**안전한 값 추출**
```java
Optional<String> optional = Optional.ofNullable(getString());

// 기본값 제공
String result1 = optional.orElse("기본값");

// 지연 평가 기본값 (Supplier 사용)
String result2 = optional.orElseGet(() -> createDefaultValue());

// 예외 던지기
String result3 = optional.orElseThrow(); // NoSuchElementException
String result4 = optional.orElseThrow(() -> new CustomException("값이 없음"));

// 실무 예제
User user = userRepository.findById(userId)
    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

String displayName = Optional.ofNullable(user.getNickname())
    .orElse(user.getName());

Config config = Optional.ofNullable(getConfig())
    .orElseGet(() -> loadDefaultConfig());
```

### 6.3 Optional 변환과 체이닝

**map(): 값 변환**
```java
Optional<String> name = Optional.of("john");

// 대문자 변환
Optional<String> upperName = name.map(String::toUpperCase);

// 길이 계산
Optional<Integer> length = name.map(String::length);

// 체인으로 여러 변환
Optional<String> processed = name
    .map(String::trim)
    .map(String::toUpperCase)
    .map(s -> s + "님");

// 실무 예제: 사용자 이메일 도메인 추출
Optional<String> emailDomain = userRepository.findById(userId)
    .map(User::getEmail)
    .map(email -> email.split("@"))
    .filter(parts -> parts.length == 2)
    .map(parts -> parts[1]);
```

**flatMap(): 중첩 Optional 평탄화**
```java
// 중첩된 Optional 처리
Optional<User> user = userRepository.findById(userId);
Optional<String> city = user
    .flatMap(User::getAddress)  // User.getAddress()가 Optional<Address> 반환
    .map(Address::getCity);

// 체인으로 깊은 탐색
Optional<String> companyName = userRepository.findById(userId)
    .flatMap(User::getCompany)     // Optional<Company>
    .flatMap(Company::getAddress)  // Optional<Address>  
    .map(Address::getCity);

// 여러 Optional 조합
Optional<String> fullInfo = userRepository.findById(userId)
    .flatMap(user -> 
        user.getAddress()
            .map(address -> user.getName() + " - " + address.getCity())
    );
```

**filter(): 조건 필터링**
```java
Optional<User> user = userRepository.findById(userId);

// 성인 사용자만 필터링
Optional<User> adultUser = user.filter(u -> u.getAge() >= 18);

// 활성 사용자만 필터링
Optional<User> activeUser = user.filter(u -> u.getStatus() == UserStatus.ACTIVE);

// 체인으로 여러 조건
Optional<User> validUser = user
    .filter(u -> u.getAge() >= 18)
    .filter(u -> u.getStatus() == UserStatus.ACTIVE)
    .filter(u -> u.getEmail() != null);

// 실무 예제: 권한이 있는 사용자만
Optional<User> authorizedUser = userRepository.findById(userId)
    .filter(user -> authService.hasPermission(user, requiredPermission));
```

### 6.4 Optional 실무 패턴

**Repository 패턴에서 Optional 활용**
```java
@Repository
public class UserRepository {
    
    // 단일 결과 조회
    public Optional<User> findById(String id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    // 조건부 조회
    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

@Service
public class UserService {
    
    public UserDto getUserProfile(String userId) {
        return userRepository.findById(userId)
            .filter(user -> user.getStatus() == UserStatus.ACTIVE)
            .map(this::convertToDto)
            .orElseThrow(() -> new UserNotFoundException("활성 사용자를 찾을 수 없습니다"));
    }
    
    public void updateUserIfExists(String userId, UpdateRequest request) {
        userRepository.findById(userId)
            .ifPresent(user -> {
                user.updateProfile(request);
                userRepository.save(user);
            });
    }
}
```

**설정값 처리 패턴**
```java
@Component
public class ConfigService {
    
    public String getDatabaseUrl() {
        return Optional.ofNullable(System.getenv("DB_URL"))
            .filter(url -> !url.isEmpty())
            .orElseGet(() -> {
                log.warn("DB_URL 환경변수가 설정되지 않았습니다. 기본값을 사용합니다.");
                return "jdbc:h2:mem:testdb";
            });
    }
    
    public int getMaxConnections() {
        return Optional.ofNullable(System.getenv("DB_MAX_CONNECTIONS"))
            .map(Integer::parseInt)
            .filter(count -> count > 0)
            .orElse(10);
    }
}
```

**캐시 패턴**
```java
@Service
public class CacheService {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(cache.get(key))
            .filter(type::isInstance)
            .map(type::cast);
    }
    
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier) {
        return get(key, type)
            .orElseGet(() -> {
                T value = supplier.get();
                cache.put(key, value);
                return value;
            });
    }
}
```

---

## 7. 병렬 프로그래밍과 성능 최적화

### 7.1 병렬 스트림 기초

**병렬 스트림 생성**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 일반 스트림
long sum1 = numbers.stream()
    .mapToLong(Integer::longValue)
    .sum();

// 병렬 스트림
long sum2 = numbers.parallelStream()
    .mapToLong(Integer::longValue)
    .sum();

// 일반 스트림을 병렬로 변환
long sum3 = numbers.stream()
    .parallel()
    .mapToLong(Integer::longValue)
    .sum();

// 병렬을 순차로 변환
long sum4 = numbers.parallelStream()
    .sequential()
    .mapToLong(Integer::longValue)
    .sum();
```

**언제 병렬 스트림을 사용할까?**
```java
// 좋은 예: CPU 집약적 작업, 대용량 데이터
List<String> largeList = generateLargeList(1_000_000);

List<String> processed = largeList.parallelStream()
    .filter(s -> s.length() > 10)
    .map(String::toUpperCase)
    .map(s -> performHeavyComputation(s)) // CPU 집약적 작업
    .collect(Collectors.toList());

// 나쁜 예: 작은 데이터, I/O 작업
List<String> smallList = Arrays.asList("a", "b", "c");
List<String> result = smallList.parallelStream() // 오버헤드가 더 큼
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// 나쁜 예: I/O 바운드 작업
List<User> users = userIds.parallelStream()
    .map(userRepository::findById) // 데이터베이스 호출
    .collect(Collectors.toList()); // 스레드 풀 고갈 위험
```

### 7.2 Fork/Join 프레임워크 이해

**스레드 풀 설정**
```java
// 시스템 기본 병렬도 확인
int parallelism = ForkJoinPool.commonPool().getParallelism();
System.out.println("기본 병렬도: " + parallelism); // 보통 CPU 코어 수 - 1

// 커스텀 Fork/Join 풀 사용
ForkJoinPool customThreadPool = new ForkJoinPool(4);
try {
    List<Integer> result = customThreadPool.submit(() ->
        numbers.parallelStream()
            .map(this::heavyComputation)
            .collect(Collectors.toList())
    ).get();
} finally {
    customThreadPool.shutdown();
}

// 시스템 프로퍼티로 기본 병렬도 설정
// -Djava.util.concurrent.ForkJoinPool.common.parallelism=8
```

### 7.3 성능 측정과 최적화

**벤치마킹 예제**
```java
public class StreamPerformanceTest {
    
    private static final int SIZE = 10_000_000;
    private static final List<Integer> numbers = 
        IntStream.range(0, SIZE).boxed().collect(Collectors.toList());
    
    public void measurePerformance() {
        // 순차 스트림
        long start = System.currentTimeMillis();
        long sum1 = numbers.stream()
            .mapToLong(n -> n * n)
            .sum();
        long sequentialTime = System.currentTimeMillis() - start;
        
        // 병렬 스트림
        start = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
            .mapToLong(n -> n * n)
            .sum();
        long parallelTime = System.currentTimeMillis() - start;
        
        System.out.println("순차 실행 시간: " + sequentialTime + "ms");
        System.out.println("병렬 실행 시간: " + parallelTime + "ms");
        System.out.println("성능 향상: " + (double) sequentialTime / parallelTime + "배");
    }
}
```

**병렬 처리 최적화 팁**
```java
// 1. 기본 타입 스트림 사용 (박싱/언박싱 비용 절약)
// 나쁜 예
long sum1 = numbers.parallelStream()
    .map(n -> n * n)  // Integer 박싱
    .mapToLong(Integer::longValue) // 언박싱
    .sum();

// 좋은 예
long sum2 = numbers.parallelStream()
    .mapToLong(Integer::longValue) // 한 번만 언박싱
    .map(n -> n * n)
    .sum();

// 2. 적절한 데이터 구조 선택
// ArrayList: 병렬 처리에 좋음 (분할 용이)
List<Integer> arrayList = new ArrayList<>(numbers);
long sum3 = arrayList.parallelStream().mapToLong(Integer::longValue).sum();

// LinkedList: 병렬 처리에 나쁨 (분할 어려움)
List<Integer> linkedList = new LinkedList<>(numbers);
long sum4 = linkedList.parallelStream().mapToLong(Integer::longValue).sum(); // 느림

// 3. Spliterator 특성 활용
// 범위 스트림: 완벽한 분할 가능
long sum5 = IntStream.range(0, SIZE)
    .parallel()
    .asLongStream()
    .map(n -> n * n)
    .sum();
```

### 7.4 동시성 안전성과 주의사항

**공유 자원 문제**
```java
// 위험한 예: 공유 변수 수정
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> sharedList = new ArrayList<>(); // 스레드 안전하지 않음

// 문제가 있는 코드
numbers.parallelStream()
    .forEach(n -> sharedList.add(n * 2)); // Race condition 발생

System.out.println(sharedList.size()); // 예측 불가능한 결과

// 올바른 해결책 1: Collector 사용
List<Integer> result1 = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList()); // 스레드 안전

// 올바른 해결책 2: 동기화된 컬렉션
List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
numbers.parallelStream()
    .forEach(n -> synchronizedList.add(n * 2));

// 올바른 해결책 3: 동시성 컬렉션
ConcurrentLinkedQueue<Integer> concurrentQueue = new ConcurrentLinkedQueue<>();
numbers.parallelStream()
    .forEach(n -> concurrentQueue.add(n * 2));
```

**reduce 연산에서의 동시성**
```java
// 안전한 reduce 연산
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 결합 법칙을 만족하는 연산 (안전)
int sum = numbers.parallelStream()
    .reduce(0, Integer::sum); // (a + b) + c = a + (b + c)

int product = numbers.parallelStream()
    .reduce(1, (a, b) -> a * b); // (a * b) * c = a * (b * c)

// 결합 법칙을 만족하지 않는 연산 (위험)
// 순차와 병렬의 결과가 다를 수 있음
String concatenated = Arrays.asList("a", "b", "c").parallelStream()
    .reduce("", (a, b) -> a + b); // 순서가 보장되지 않음

// 안전한 문자열 연결
String safeConcatenated = Arrays.asList("a", "b", "c").parallelStream()
    .collect(Collectors.joining()); // 순서 보장
```

---

## 8. 실무 패턴과 베스트 프랙티스

### 8.1 데이터 처리 패턴

**일괄 처리 (Batch Processing)**
```java
@Service
public class DataProcessor {
    
    // 대용량 데이터 청크 단위 처리
    public void processBatch(List<DataRecord> records) {
        int batchSize = 1000;
        
        IntStream.range(0, (records.size() + batchSize - 1) / batchSize)
            .mapToObj(i -> records.subList(
                i * batchSize, 
                Math.min((i + 1) * batchSize, records.size())
            ))
            .forEach(this::processBatchChunk);
    }
    
    private void processBatchChunk(List<DataRecord> chunk) {
        List<ProcessedData> processed = chunk.stream()
            .filter(this::isValid)
            .map(this::transform)
            .collect(Collectors.toList());
            
        dataRepository.saveAll(processed);
        log.info("처리 완료: {} 건", processed.size());
    }
    
    // 병렬 배치 처리
    public void processParallelBatch(List<DataRecord> records) {
        records.parallelStream()
            .filter(this::isValid)
            .map(this::transform)
            .collect(Collectors.groupingBy(
                data -> data.getId() % 10, // 파티션 키
                Collectors.toList()
            ))
            .values()
            .forEach(dataRepository::saveAll);
    }
}
```

**ETL (Extract, Transform, Load) 패턴**
```java
@Service
public class ETLService {
    
    public void processUserData() {
        // Extract: 원본 데이터 추출
        List<RawUserData> rawData = extractUserData();
        
        // Transform: 데이터 변환 및 정제
        List<CleanUserData> cleanData = rawData.stream()
            .filter(this::isValidData)
            .map(this::normalizeData)
            .map(this::enrichData)
            .collect(Collectors.toList());
        
        // Load: 대상 시스템에 적재
        loadUserData(cleanData);
        
        // 통계 정보 생성
        generateStatistics(cleanData);
    }
    
    private boolean isValidData(RawUserData data) {
        return data.getEmail() != null && 
               data.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$") &&
               data.getAge() != null && 
               data.getAge() > 0 && data.getAge() < 150;
    }
    
    private CleanUserData normalizeData(RawUserData raw) {
        return CleanUserData.builder()
            .email(raw.getEmail().toLowerCase().trim())
            .name(normalizedName(raw.getName()))
            .age(raw.getAge())
            .phone(normalizePhone(raw.getPhone()))
            .build();
    }
    
    private CleanUserData enrichData(CleanUserData data) {
        // 외부 시스템에서 추가 정보 조회
        String city = geocodingService.getCityByPhone(data.getPhone());
        String segment = segmentationService.getSegment(data);
        
        return data.toBuilder()
            .city(city)
            .segment(segment)
            .processedAt(LocalDateTime.now())
            .build();
    }
}
```

### 8.2 API 응답 처리 패턴

**페이징과 정렬**
```java
@RestController
public class UserController {
    
    @GetMapping("/users")
    public PageResponse<UserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        List<User> users = userRepository.findAll();
        
        // 검색 필터링
        Stream<User> userStream = users.stream();
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            userStream = userStream.filter(user -> 
                user.getName().toLowerCase().contains(searchLower) ||
                user.getEmail().toLowerCase().contains(searchLower)
            );
        }
        
        // 정렬
        Comparator<User> comparator = getUserComparator(sortBy);
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        
        // 페이징과 DTO 변환
        List<UserDto> result = userStream
            .sorted(comparator)
            .skip((long) page * size)
            .limit(size)
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        long totalCount = users.size(); // 실제로는 카운트 쿼리 필요
        
        return PageResponse.<UserDto>builder()
            .content(result)
            .page(page)
            .size(size)
            .totalElements(totalCount)
            .totalPages((int) Math.ceil((double) totalCount / size))
            .build();
    }
    
    private Comparator<User> getUserComparator(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "name": return Comparator.comparing(User::getName);
            case "email": return Comparator.comparing(User::getEmail);
            case "age": return Comparator.comparing(User::getAge);
            case "createdat": return Comparator.comparing(User::getCreatedAt);
            default: return Comparator.comparing(User::getName);
        }
    }
}
```

**데이터 집계와 통계**
```java
@Service
public class AnalyticsService {
    
    public OrderAnalytics getOrderAnalytics(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByDateRange(startDate, endDate);
        
        // 기본 통계
        DoubleSummaryStatistics amountStats = orders.stream()
            .mapToDouble(Order::getAmount)
            .summaryStatistics();
        
        // 일별 매출
        Map<LocalDate, Double> dailySales = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCreatedAt().toLocalDate(),
                Collectors.summingDouble(Order::getAmount)
            ));
        
        // 상품별 판매량
        Map<String, Long> productSales = orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                item -> item.getProduct().getName(),
                Collectors.summingLong(OrderItem::getQuantity)
            ));
        
        // 고객별 주문 통계
        Map<String, CustomerStats> customerStats = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCustomer().getId(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    orderList -> CustomerStats.builder()
                        .totalOrders(orderList.size())
                        .totalAmount(orderList.stream()
                            .mapToDouble(Order::getAmount)
                            .sum())
                        .averageOrderAmount(orderList.stream()
                            .mapToDouble(Order::getAmount)
                            .average()
                            .orElse(0.0))
                        .lastOrderDate(orderList.stream()
                            .map(Order::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(null))
                        .build()
                )
            ));
        
        return OrderAnalytics.builder()
            .totalOrders(orders.size())
            .totalAmount(amountStats.getSum())
            .averageOrderAmount(amountStats.getAverage())
            .minOrderAmount(amountStats.getMin())
            .maxOrderAmount(amountStats.getMax())
            .dailySales(dailySales)
            .productSales(productSales)
            .customerStats(customerStats)
            .build();
    }
}
```

### 8.3 성능 최적화 베스트 프랙티스

**메모리 효율적인 처리**
```java
@Service
public class EfficientDataProcessor {
    
    // 나쁜 예: 중간 컬렉션 생성
    public List<ProcessedData> inefficientProcess(List<RawData> rawData) {
        List<RawData> filtered = rawData.stream()
            .filter(this::isValid)
            .collect(Collectors.toList()); // 불필요한 중간 컬렉션
        
        List<TransformedData> transformed = filtered.stream()
            .map(this::transform)
            .collect(Collectors.toList()); // 또 다른 중간 컬렉션
        
        return transformed.stream()
            .map(this::process)
            .collect(Collectors.toList());
    }
    
    // 좋은 예: 스트림 체인
    public List<ProcessedData> efficientProcess(List<RawData> rawData) {
        return rawData.stream()
            .filter(this::isValid)
            .map(this::transform)
            .map(this::process)
            .collect(Collectors.toList()); // 한 번만 수집
    }
    
    // 대용량 데이터를 위한 청크 처리
    public void processLargeDataset(Stream<RawData> dataStream) {
        final int CHUNK_SIZE = 1000;
        final AtomicInteger counter = new AtomicInteger();
        
        dataStream
            .filter(this::isValid)
            .map(this::transform)
            .collect(Collectors.groupingBy(
                data -> counter.getAndIncrement() / CHUNK_SIZE
            ))
            .values()
            .forEach(chunk -> {
                List<ProcessedData> processed = chunk.stream()
                    .map(this::process)
                    .collect(Collectors.toList());
                dataRepository.saveAll(processed);
            });
    }
}
```

**조건부 처리 최적화**
```java
public class ConditionalProcessor {
    
    // 조건에 따른 다른 처리 로직
    public List<Result> processWithConditions(List<Data> dataList) {
        Map<DataType, List<Data>> groupedByType = dataList.stream()
            .collect(Collectors.groupingBy(Data::getType));
        
        List<Result> results = new ArrayList<>();
        
        // 타입별로 다른 처리 로직 적용
        groupedByType.forEach((type, dataOfType) -> {
            switch (type) {
                case TYPE_A:
                    results.addAll(dataOfType.stream()
                        .map(this::processTypeA)
                        .collect(Collectors.toList()));
                    break;
                case TYPE_B:
                    results.addAll(dataOfType.stream()
                        .map(this::processTypeB)
                        .collect(Collectors.toList()));
                    break;
                case TYPE_C:
                    results.addAll(dataOfType.parallelStream() // CPU 집약적인 경우 병렬 처리
                        .map(this::processTypeC)
                        .collect(Collectors.toList()));
                    break;
            }
        });
        
        return results;
    }
    
    // 필터링 조건 최적화
    public List<User> findEligibleUsers(List<User> users) {
        // 빠른 조건부터 확인 (short-circuit)
        return users.stream()
            .filter(user -> user.getAge() >= 18) // 빠른 숫자 비교
            .filter(user -> user.getStatus() == UserStatus.ACTIVE) // enum 비교
            .filter(user -> !user.getEmail().isEmpty()) // 문자열 검사
            .filter(this::hasValidSubscription) // 복잡한 로직은 마지막에
            .collect(Collectors.toList());
    }
}
```

### 8.4 에러 처리와 로깅

**안전한 스트림 처리**
```java
@Service
public class SafeStreamProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(SafeStreamProcessor.class);
    
    public List<ProcessedData> safeProcess(List<RawData> rawData) {
        return rawData.stream()
            .filter(Objects::nonNull) // null 체크
            .map(this::safeTransform)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
    
    private Optional<ProcessedData> safeTransform(RawData raw) {
        try {
            ProcessedData processed = transform(raw);
            return Optional.of(processed);
        } catch (Exception e) {
            log.warn("데이터 변환 실패: {}, 에러: {}", raw.getId(), e.getMessage());
            return Optional.empty();
        }
    }
    
    // 에러 수집과 함께 처리
    public ProcessingResult processWithErrorCollection(List<RawData> rawData) {
        List<ProcessedData> successful = new ArrayList<>();
        List<ErrorRecord> errors = new ArrayList<>();
        
        rawData.stream().forEach(raw -> {
            try {
                ProcessedData processed = transform(raw);
                successful.add(processed);
            } catch (Exception e) {
                errors.add(ErrorRecord.builder()
                    .id(raw.getId())
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
            }
        });
        
        return ProcessingResult.builder()
            .successful(successful)
            .errors(errors)
            .successCount(successful.size())
            .errorCount(errors.size())
            .build();
    }
    
    // CompletableFuture와 함께 비동기 처리
    public CompletableFuture<List<ProcessedData>> processAsync(List<RawData> rawData) {
        return CompletableFuture.supplyAsync(() -> 
            rawData.parallelStream()
                .map(this::safeTransform)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );
    }
}
```

### 8.5 캐싱과 메모이제이션 패턴

**계산 결과 캐싱**
```java
@Service
public class CachedCalculationService {
    
    private final Map<String, Double> calculationCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> cacheTimestamps = new ConcurrentHashMap<>();
    private final Duration cacheExpiry = Duration.ofMinutes(30);
    
    // 메모이제이션 패턴
    public double expensiveCalculation(String key, List<Double> values) {
        return calculationCache.computeIfAbsent(key, k -> {
            log.info("캐시 미스 - 계산 실행: {}", key);
            cacheTimestamps.put(key, LocalDateTime.now());
            return values.stream()
                .mapToDouble(Double::doubleValue)
                .map(this::complexComputation)
                .sum();
        });
    }
    
    // 시간 기반 캐시 만료
    public double expensiveCalculationWithExpiry(String key, List<Double> values) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cachedTime = cacheTimestamps.get(key);
        
        if (cachedTime != null && Duration.between(cachedTime, now).compareTo(cacheExpiry) < 0) {
            return calculationCache.get(key);
        }
        
        // 캐시 만료 또는 미존재 시 재계산
        double result = values.stream()
            .mapToDouble(Double::doubleValue)
            .map(this::complexComputation)
            .sum();
            
        calculationCache.put(key, result);
        cacheTimestamps.put(key, now);
        return result;
    }
    
    // 함수형 캐시 래퍼
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }
    
    // 사용 예제
    Function<String, List<User>> expensiveUserLoader = memoize(this::loadUsersFromDatabase);
    
    public List<User> getUsers(String criteria) {
        return expensiveUserLoader.apply(criteria);
    }
}
```

### 8.6 함수 조합과 파이프라인 패턴

**데이터 파이프라인 구축**
```java
@Component
public class DataPipeline {
    
    // 함수형 파이프라인 구성
    private final Function<RawData, Optional<ValidatedData>> validator;
    private final Function<ValidatedData, EnrichedData> enricher;
    private final Function<EnrichedData, ProcessedData> processor;
    private final Consumer<ProcessedData> persistor;
    
    public DataPipeline() {
        this.validator = this::validateData;
        this.enricher = this::enrichData;
        this.processor = this::processData;
        this.persistor = this::persistData;
    }
    
    // 파이프라인 실행
    public void executePipeline(List<RawData> rawData) {
        rawData.stream()
            .map(validator)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(enricher)
            .map(processor)
            .forEach(persistor);
    }
    
    // 조건부 파이프라인
    public void executeConditionalPipeline(List<RawData> rawData) {
        rawData.stream()
            .map(validator)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(data -> {
                // 조건에 따라 다른 처리
                if (data.getType() == DataType.PREMIUM) {
                    return enricher.andThen(this::addPremiumFeatures).apply(data);
                } else {
                    return enricher.apply(data);
                }
            })
            .map(processor)
            .forEach(persistor);
    }
    
    // 병렬 파이프라인 (독립적인 처리 단계)
    public void executeParallelPipeline(List<RawData> rawData) {
        List<ValidatedData> validated = rawData.parallelStream()
            .map(validator)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        List<EnrichedData> enriched = validated.parallelStream()
            .map(enricher)
            .collect(Collectors.toList());
        
        List<ProcessedData> processed = enriched.parallelStream()
            .map(processor)
            .collect(Collectors.toList());
        
        // 저장은 순차적으로 (DB 연결 풀 고려)
        processed.forEach(persistor);
    }
}
```

**함수 조합 유틸리티**
```java
public class FunctionUtils {
    
    // 함수 체이닝
    @SafeVarargs
    public static <T> Function<T, T> chain(Function<T, T>... functions) {
        return Arrays.stream(functions)
            .reduce(Function.identity(), Function::andThen);
    }
    
    // 조건부 함수 적용
    public static <T> Function<T, T> when(Predicate<T> condition, Function<T, T> function) {
        return input -> condition.test(input) ? function.apply(input) : input;
    }
    
    // 예외 안전 함수
    public static <T, R> Function<T, Optional<R>> safe(Function<T, R> function) {
        return input -> {
            try {
                return Optional.of(function.apply(input));
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
    
    // 재시도 함수
    public static <T, R> Function<T, Optional<R>> retry(Function<T, R> function, int maxAttempts) {
        return input -> {
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    return Optional.of(function.apply(input));
                } catch (Exception e) {
                    if (i == maxAttempts - 1) {
                        return Optional.empty();
                    }
                    // 간단한 백오프
                    try {
                        Thread.sleep(100 * (i + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return Optional.empty();
                    }
                }
            }
            return Optional.empty();
        };
    }
    
    // 사용 예제
    public void example() {
        Function<String, String> processText = chain(
            String::trim,
            String::toLowerCase,
            text -> text.replaceAll("\\s+", " ")
        );
        
        Function<User, User> conditionalUpdate = when(
            user -> user.getAge() < 18,
            user -> user.withStatus(UserStatus.MINOR)
        );
        
        Function<String, Optional<Integer>> safeParseInt = safe(Integer::parseInt);
        Function<String, Optional<User>> retryUserLoad = retry(this::loadUser, 3);
    }
}
```

### 8.7 테스트 전략

**함수형 코드 테스트**
```java
@ExtendWith(MockitoExtension.class)
class StreamProcessorTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserProcessor userProcessor;
    
    @Test
    void testUserFiltering() {
        // Given
        List<User> users = Arrays.asList(
            createUser("user1", 25, UserStatus.ACTIVE),
            createUser("user2", 17, UserStatus.ACTIVE),
            createUser("user3", 30, UserStatus.INACTIVE),
            createUser("user4", 22, UserStatus.ACTIVE)
        );
        
        // When
        List<User> activeAdults = userProcessor.filterActiveAdults(users);
        
        // Then
        assertThat(activeAdults)
            .hasSize(2)
            .extracting(User::getName)
            .containsExactly("user1", "user4");
    }
    
    @Test
    void testStreamPipeline() {
        // Given
        List<String> emails = Arrays.asList(
            "valid@example.com",
            "invalid-email",
            "another@test.com",
            ""
        );
        
        // When
        List<String> validEmails = emails.stream()
            .filter(email -> !email.isEmpty())
            .filter(email -> email.contains("@"))
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        
        // Then
        assertThat(validEmails)
            .containsExactly("valid@example.com", "another@test.com");
    }
    
    @Test
    void testGroupingCollector() {
        // Given
        List<Order> orders = Arrays.asList(
            createOrder("2023-01-01", 100.0, OrderStatus.COMPLETED),
            createOrder("2023-01-01", 200.0, OrderStatus.COMPLETED),
            createOrder("2023-01-02", 150.0, OrderStatus.COMPLETED),
            createOrder("2023-01-01", 300.0, OrderStatus.CANCELLED)
        );
        
        // When
        Map<String, Double> dailyRevenue = orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .collect(Collectors.groupingBy(
                order -> order.getDate().toString(),
                Collectors.summingDouble(Order::getAmount)
            ));
        
        // Then
        assertThat(dailyRevenue)
            .containsEntry("2023-01-01", 300.0)
            .containsEntry("2023-01-02", 150.0)
            .hasSize(2);
    }
    
    // 병렬 스트림 테스트
    @Test
    void testParallelProcessing() {
        // Given
        List<Integer> numbers = IntStream.range(1, 1000)
            .boxed()
            .collect(Collectors.toList());
        
        // When
        long sequentialSum = numbers.stream()
            .mapToLong(Integer::longValue)
            .sum();
        
        long parallelSum = numbers.parallelStream()
            .mapToLong(Integer::longValue)
            .sum();
        
        // Then
        assertThat(parallelSum).isEqualTo(sequentialSum);
    }
}
```

**Property-based 테스트**
```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StreamPropertiesTest {
    
    @ParameterizedTest
    @MethodSource("generateNumberLists")
    void testStreamOperationProperties(List<Integer> numbers) {
        // 속성 1: filter 후 map은 map 후 filter와 결과가 같아야 함 (조건에 따라)
        List<Integer> filterThenMap = numbers.stream()
            .filter(n -> n > 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());
        
        List<Integer> mapThenFilter = numbers.stream()
            .map(n -> n * 2)
            .filter(n -> n > 0)
            .collect(Collectors.toList());
        
        // 양수만 필터링하는 경우 결과가 같아야 함
        List<Integer> positiveNumbers = numbers.stream()
            .filter(n -> n > 0)
            .collect(Collectors.toList());
        
        if (positiveNumbers.equals(numbers)) {
            assertThat(filterThenMap).isEqualTo(mapThenFilter);
        }
        
        // 속성 2: 순차와 병렬 결과가 같아야 함 (순서 무관한 연산)
        Set<Integer> sequentialResult = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toSet());
        
        Set<Integer> parallelResult = numbers.parallelStream()
            .map(n -> n * n)
            .collect(Collectors.toSet());
        
        assertThat(parallelResult).isEqualTo(sequentialResult);
    }
    
    Stream<List<Integer>> generateNumberLists() {
        return Stream.of(
            Arrays.asList(1, 2, 3, 4, 5),
            Arrays.asList(-1, 0, 1),
            Collections.emptyList(),
            Arrays.asList(100, -50, 25, -10)
        );
    }
}
```

### 8.8 실무 팁과 주의사항

**성능 고려사항**
```java
public class PerformanceTips {
    
    // 1. 조기 종료 활용
    public boolean hasExpensiveCondition(List<Data> dataList) {
        // 나쁜 예: 모든 요소를 확인
        boolean badExample = dataList.stream()
            .allMatch(this::expensiveCheck);
        
        // 좋은 예: 첫 번째 실패에서 즉시 종료
        return dataList.stream()
            .anyMatch(data -> !expensiveCheck(data)) == false;
        
        // 더 좋은 예: 명시적 조기 종료
        return dataList.stream()
            .allMatch(this::expensiveCheck);
    }
    
    // 2. 기본 타입 스트림 활용
    public double calculateAverage(List<Integer> numbers) {
        // 나쁜 예: 박싱/언박싱 오버헤드
        return numbers.stream()
            .mapToDouble(Integer::doubleValue)
            .average()
            .orElse(0.0);
        
        // 좋은 예: 기본 타입 스트림 사용
        return numbers.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }
    
    // 3. 컬렉션 크기 힌트 제공
    public List<String> processStrings(List<String> input) {
        return input.stream()
            .filter(s -> s.length() > 5)
            .map(String::toUpperCase)
            .collect(Collectors.toCollection(
                () -> new ArrayList<>(input.size()) // 초기 용량 설정
            ));
    }
    
    // 4. 스트림 재사용 금지
    public void avoidStreamReuse() {
        List<String> data = Arrays.asList("a", "b", "c");
        Stream<String> stream = data.stream();
        
        // 첫 번째 사용
        long count = stream.count();
        
        // 두 번째 사용 시도 (IllegalStateException 발생)
        try {
            stream.forEach(System.out::println); // 예외 발생!
        } catch (IllegalStateException e) {
            System.out.println("스트림은 재사용할 수 없습니다");
        }
        
        // 올바른 방법: 새 스트림 생성
        data.stream().forEach(System.out::println);
    }
}
```

**디버깅 팁**
```java
public class DebuggingTips {
    
    // 1. peek()를 활용한 중간 상태 확인
    public List<String> debugProcessing(List<String> input) {
        return input.stream()
            .peek(s -> System.out.println("입력: " + s))
            .filter(s -> s.length() > 3)
            .peek(s -> System.out.println("필터 후: " + s))
            .map(String::toUpperCase)
            .peek(s -> System.out.println("변환 후: " + s))
            .collect(Collectors.toList());
    }
    
    // 2. 조건별 개수 확인
    public void analyzeData(List<User> users) {
        Map<Boolean, Long> ageGroups = users.stream()
            .collect(Collectors.partitioningBy(
                user -> user.getAge() >= 18,
                Collectors.counting()
            ));
        
        System.out.println("성인: " + ageGroups.get(true));
        System.out.println("미성년자: " + ageGroups.get(false));
        
        // 상세 분석
        users.stream()
            .collect(Collectors.groupingBy(
                user -> user.getAge() / 10 * 10, // 연령대별 그룹핑
                Collectors.counting()
            ))
            .forEach((ageGroup, count) -> 
                System.out.println(ageGroup + "대: " + count + "명"));
    }
    
    // 3. 예외 상황 추적
    public List<ProcessedData> trackExceptions(List<RawData> rawData) {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        
        List<ProcessedData> result = rawData.stream()
            .map(data -> {
                try {
                    ProcessedData processed = processData(data);
                    successCount.incrementAndGet();
                    return Optional.of(processed);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("처리 실패: " + data.getId() + ", 오류: " + e.getMessage());
                    return Optional.<ProcessedData>empty();
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        System.out.println("처리 완료 - 성공: " + successCount + ", 실패: " + errorCount);
        return result;
    }
}
```

---

## 📚 실무 체크리스트

### ✅ 기본 원칙
- **가독성 우선**: 성능보다 코드의 명확성을 우선시
- **부작용 최소화**: 순수 함수 지향, 원본 데이터 보존
- **적절한 추상화**: 과도한 함수형 기법보다 적절한 수준 유지
- **팀 컨벤션 준수**: 팀의 코딩 스타일과 일관성 유지

### ✅ 성능 최적화
- **병렬 처리 신중 사용**: 작은 데이터나 I/O 작업에서는 피하기
- **기본 타입 스트림 활용**: IntStream, LongStream, DoubleStream 사용
- **조기 종료 활용**: anyMatch, allMatch, findFirst 등 적극 활용
- **불필요한 중간 컬렉션 생성 금지**: 스트림 체인으로 한 번에 처리

### ✅ 에러 처리
- **Optional 적극 활용**: null 체크 대신 Optional 사용
- **예외 안전성**: try-catch를 함수형으로 래핑
- **실패 추적**: 에러 로깅과 모니터링 체계 구축
- **우아한 성능 저하**: 일부 실패가 전체를 망치지 않도록

### ✅ 테스트
- **단위 테스트 작성**: 각 함수의 입출력 명확히 테스트
- **Property-based 테스트**: 다양한 입력에 대한 속성 검증
- **성능 테스트**: 대용량 데이터 처리 시나리오 검증
- **병렬 처리 테스트**: 동시성 안전성 확인

---

이 가이드를 통해 Java 함수형 프로그래밍의 핵심 개념부터 실무 적용까지 체계적으로 학습하실 수 있습니다. 출퇴근 시간을 활용해 조금씩 읽어보시고, 실제 프로젝트에 적용해보시면서 경험을 쌓아가시기 바랍니다!

**핵심 기억 포인트**: 함수형 프로그래밍은 도구일 뿐입니다. 무조건 사용하는 것보다는 문제 상황에 맞는 적절한 도구를 선택하는 지혜가 더 중요합니다. 🚀