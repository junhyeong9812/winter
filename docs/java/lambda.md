# 📘 Java 메서드 vs 람다식 가이드

## 🎯 개요

Java에서 코드를 실행하는 방법은 크게 **전통적인 메서드**와 **람다식(Lambda Expression)**으로 나뉩니다. 각각의 특징과 사용 시기를 이해하는 것이 중요합니다.

---

## 🔧 전통적인 메서드 (Traditional Methods)

### 1. 기본 메서드 정의
```java
public class Calculator {
    
    // 인스턴스 메서드
    public int add(int a, int b) {
        return a + b;
    }
    
    // 정적 메서드
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    // private 메서드
    private void logOperation(String operation) {
        System.out.println("Operation: " + operation);
    }
}

// 사용
Calculator calc = new Calculator();
int sum = calc.add(5, 3);              // 인스턴스 메서드 호출
int product = Calculator.multiply(4, 7); // 정적 메서드 호출
```

### 2. 메서드 오버로딩
```java
public class Printer {
    
    public void print(String message) {
        System.out.println(message);
    }
    
    public void print(int number) {
        System.out.println("Number: " + number);
    }
    
    public void print(String message, int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(message);
        }
    }
}

// 사용 - 컴파일러가 파라미터에 따라 적절한 메서드 선택
Printer printer = new Printer();
printer.print("Hello");           // print(String) 호출
printer.print(42);               // print(int) 호출
printer.print("Hi", 3);          // print(String, int) 호출
```

### 3. 메서드 반환 타입
```java
public class DataProcessor {
    
    // void - 반환값 없음
    public void processData() {
        System.out.println("Processing...");
    }
    
    // 기본 타입 반환
    public int calculateSum(int[] numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
    
    // 객체 반환
    public List<String> getProcessedData() {
        List<String> result = new ArrayList<>();
        result.add("processed");
        return result;
    }
    
    // 조건부 반환
    public String getStatus(boolean isComplete) {
        if (isComplete) {
            return "Complete";
        } else {
            return "In Progress";
        }
    }
}
```

---

## 🚀 람다식 (Lambda Expressions)

### 1. 람다식 기본 문법
```java
// 기본 형태: (parameters) -> expression
// 또는: (parameters) -> { statements; }

// 파라미터 없음
() -> System.out.println("Hello")

// 파라미터 1개 (괄호 생략 가능)
x -> x * 2
(x) -> x * 2  // 괄호 있어도 OK

// 파라미터 여러 개
(x, y) -> x + y

// 블록 형태 (여러 문장)
(x, y) -> {
    int sum = x + y;
    System.out.println("Sum: " + sum);
    return sum;
}

// 타입 명시 (보통은 생략)
(int x, int y) -> x + y
```

### 2. 함수형 인터페이스와 람다식
```java
// 함수형 인터페이스 (추상 메서드가 1개만 있는 인터페이스)
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// 람다식으로 구현
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;
Calculator subtract = (a, b) -> {
    System.out.println("Subtracting " + b + " from " + a);
    return a - b;
};

// 사용
int result1 = add.calculate(5, 3);        // 8
int result2 = multiply.calculate(4, 7);   // 28
int result3 = subtract.calculate(10, 4);  // 6
```

### 3. 주요 내장 함수형 인터페이스
```java
import java.util.function.*;

// Predicate<T> - 조건 판단 (T -> boolean)
Predicate<String> isEmpty = str -> str.isEmpty();
Predicate<Integer> isEven = num -> num % 2 == 0;

// Function<T, R> - 변환 (T -> R)
Function<String, Integer> stringLength = str -> str.length();
Function<Integer, String> toString = num -> "Number: " + num;

// Consumer<T> - 소비 (T -> void)
Consumer<String> printer = msg -> System.out.println(msg);
Consumer<List<String>> listPrinter = list -> list.forEach(System.out::println);

// Supplier<T> - 공급 (() -> T)
Supplier<String> currentTime = () -> new Date().toString();
Supplier<Integer> randomNumber = () -> (int)(Math.random() * 100);

// BiFunction<T, U, R> - 두 파라미터 변환 ((T, U) -> R)
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
BiFunction<String, String, String> concat = (s1, s2) -> s1 + s2;

// 사용 예시
System.out.println(isEmpty.test(""));        // true
System.out.println(isEven.test(4));          // true
System.out.println(stringLength.apply("Hello")); // 5
printer.accept("Hello World");               // 출력: Hello World
System.out.println(currentTime.get());       // 현재 시간 출력
```

---

## 🔄 메서드 참조 (Method References)

### 1. 정적 메서드 참조
```java
// 람다식
Function<String, Integer> parseInt1 = str -> Integer.parseInt(str);

// 메서드 참조 (더 간결)
Function<String, Integer> parseInt2 = Integer::parseInt;

// 사용
int number = parseInt2.apply("123");  // 123
```

### 2. 인스턴스 메서드 참조
```java
String text = "Hello World";

// 람다식
Supplier<String> toUpper1 = () -> text.toUpperCase();

// 메서드 참조
Supplier<String> toUpper2 = text::toUpperCase;

// 사용
System.out.println(toUpper2.get());  // "HELLO WORLD"
```

### 3. 임의 객체의 인스턴스 메서드 참조
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 람다식
names.sort((s1, s2) -> s1.compareToIgnoreCase(s2));

// 메서드 참조
names.sort(String::compareToIgnoreCase);

// 더 간단한 예시
List<String> upperNames = names.stream()
    .map(name -> name.toUpperCase())  // 람다식
    .collect(Collectors.toList());

List<String> upperNames2 = names.stream()
    .map(String::toUpperCase)         // 메서드 참조
    .collect(Collectors.toList());
```

### 4. 생성자 참조
```java
// 람다식
Supplier<ArrayList<String>> listCreator1 = () -> new ArrayList<>();

// 생성자 참조
Supplier<ArrayList<String>> listCreator2 = ArrayList::new;

// 파라미터가 있는 생성자
Function<String, StringBuilder> sbCreator = StringBuilder::new;

// 사용
ArrayList<String> list = listCreator2.get();
StringBuilder sb = sbCreator.apply("Hello");
```

---

## 📊 스트림 API와 람다식

### 1. 컬렉션 처리
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 전통적인 방식
List<Integer> evenNumbers = new ArrayList<>();
for (Integer num : numbers) {
    if (num % 2 == 0) {
        evenNumbers.add(num * 2);
    }
}

// 람다식과 스트림 API
List<Integer> evenNumbers2 = numbers.stream()
    .filter(num -> num % 2 == 0)        // 짝수만 필터링
    .map(num -> num * 2)                // 2배로 변환
    .collect(Collectors.toList());       // 리스트로 수집

// 메서드 체이닝
int sum = numbers.stream()
    .filter(num -> num > 5)             // 5보다 큰 수
    .mapToInt(Integer::intValue)        // int로 변환
    .sum();                             // 합계 계산
```

### 2. 복잡한 데이터 처리
```java
public class Person {
    private String name;
    private int age;
    private String city;
    
    // 생성자, getter, setter 생략
}

List<Person> people = Arrays.asList(
    new Person("Alice", 25, "Seoul"),
    new Person("Bob", 30, "Busan"),
    new Person("Charlie", 35, "Seoul"),
    new Person("Diana", 28, "Busan")
);

// 서울에 사는 사람들의 평균 나이
double avgAge = people.stream()
    .filter(person -> "Seoul".equals(person.getCity()))  // 서울 거주자만
    .mapToInt(Person::getAge)                            // 나이 추출
    .average()                                           // 평균 계산
    .orElse(0.0);                                        // 기본값

// 도시별 그룹핑
Map<String, List<Person>> peopleByCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity));

// 나이순 정렬 후 이름 추출
List<String> namesByAge = people.stream()
    .sorted(Comparator.comparing(Person::getAge))       // 나이순 정렬
    .map(Person::getName)                               // 이름 추출
    .collect(Collectors.toList());
```

---

## 🆚 메서드 vs 람다식 비교

### 1. 익명 클래스 vs 람다식
```java
// 익명 클래스 (전통적인 방식)
Runnable task1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Task running...");
    }
};

// 람다식 (간결한 방식)
Runnable task2 = () -> System.out.println("Task running...");

// 사용
new Thread(task1).start();
new Thread(task2).start();
new Thread(() -> System.out.println("Inline task")).start();  // 인라인 사용
```

### 2. 컬렉션 정렬 비교
```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");

// 전통적인 방식 - Comparator 구현
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
});

// 람다식 방식
Collections.sort(names, (s1, s2) -> s1.compareToIgnoreCase(s2));

// 메서드 참조 방식 (가장 간결)
Collections.sort(names, String::compareToIgnoreCase);

// 스트림 API 방식
List<String> sortedNames = names.stream()
    .sorted(String::compareToIgnoreCase)
    .collect(Collectors.toList());
```

### 3. 이벤트 처리 비교
```java
// Swing에서의 버튼 클릭 처리

// 전통적인 방식
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button clicked!");
    }
});

// 람다식 방식
button.addActionListener(e -> System.out.println("Button clicked!"));

// 메서드 참조 방식
button.addActionListener(this::onButtonClick);

// 별도 메서드 정의
private void onButtonClick(ActionEvent e) {
    System.out.println("Button clicked!");
}
```

---

## 🎯 언제 무엇을 사용할까?

### ✅ 전통적인 메서드를 사용하는 경우

1. **재사용성이 중요한 경우**
```java
// 여러 곳에서 사용되는 로직
public class MathUtils {
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    public static boolean isPrime(int number) {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}

// 여러 클래스에서 재사용
double dist1 = MathUtils.calculateDistance(0, 0, 3, 4);
double dist2 = MathUtils.calculateDistance(1, 1, 5, 7);
```

2. **복잡한 비즈니스 로직**
```java
public class OrderService {
    
    public void processOrder(Order order) {
        validateOrder(order);
        calculateDiscount(order);
        updateInventory(order);
        sendConfirmationEmail(order);
        logOrderProcessing(order);
    }
    
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        // 복잡한 검증 로직...
    }
    
    private void calculateDiscount(Order order) {
        // 복잡한 할인 계산 로직...
    }
    
    // 기타 메서드들...
}
```

3. **명확한 API가 필요한 경우**
```java
public class DatabaseManager {
    
    public Connection getConnection() throws SQLException {
        // 커넥션 생성 로직
    }
    
    public void executeQuery(String sql, Object... params) throws SQLException {
        // 쿼리 실행 로직
    }
    
    public <T> List<T> selectList(String sql, Class<T> resultType, Object... params) {
        // 결과 매핑 로직
    }
}
```

### ✅ 람다식을 사용하는 경우

1. **일회성 간단한 로직**
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 간단한 변환
List<String> upperNames = names.stream()
    .map(name -> name.toUpperCase())  // 간단한 일회성 변환
    .collect(Collectors.toList());

// 간단한 필터링
List<String> longNames = names.stream()
    .filter(name -> name.length() > 4)  // 간단한 조건
    .collect(Collectors.toList());
```

2. **함수형 프로그래밍 스타일**
```java
// 데이터 파이프라인 처리
List<Integer> result = numbers.stream()
    .filter(n -> n > 0)              // 양수만
    .map(n -> n * n)                 // 제곱
    .filter(n -> n % 2 == 0)         // 짝수만
    .sorted()                        // 정렬
    .collect(Collectors.toList());
```

3. **이벤트 핸들링**
```java
// 버튼 클릭 처리
button.setOnAction(e -> {
    statusLabel.setText("Button clicked!");
    System.out.println("Action performed at: " + new Date());
});

// 스레드 실행
new Thread(() -> {
    try {
        Thread.sleep(1000);
        System.out.println("Background task completed");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

4. **콜백 함수**
```java
public class AsyncProcessor {
    
    public void processAsync(String data, Consumer<String> onSuccess, Consumer<String> onError) {
        CompletableFuture.supplyAsync(() -> {
            // 비동기 처리 로직
            if (data.length() > 10) {
                throw new RuntimeException("Data too long");
            }
            return "Processed: " + data;
        })
        .thenAccept(onSuccess)  // 성공 콜백
        .exceptionally(ex -> {
            onError.accept(ex.getMessage());  // 실패 콜백
            return null;
        });
    }
}

// 사용
processor.processAsync("test data",
    result -> System.out.println("Success: " + result),    // 성공 콜백
    error -> System.err.println("Error: " + error)         // 실패 콜백
);
```

---

## 🔍 성능 고려사항

### 1. 람다식 성능
```java
// 람다식은 첫 번째 호출 시 약간의 오버헤드가 있음
Supplier<String> lambda = () -> "Hello";

// 반복 호출 시에는 성능 차이가 미미함
for (int i = 0; i < 1000000; i++) {
    String result = lambda.get();  // 성능상 문제없음
}

// 스트림 API는 작은 컬렉션에서는 오히려 느릴 수 있음
List<Integer> smallList = Arrays.asList(1, 2, 3, 4, 5);

// 작은 컬렉션에서는 전통적인 루프가 더 빠를 수 있음
int sum1 = 0;
for (int num : smallList) {
    sum1 += num;
}

// 큰 컬렉션에서는 스트림이 더 효율적일 수 있음 (병렬 처리 가능)
List<Integer> bigList = IntStream.range(1, 1000000).boxed().collect(Collectors.toList());
int sum2 = bigList.parallelStream()
    .mapToInt(Integer::intValue)
    .sum();
```

### 2. 메모리 사용
```java
// 람다식은 메모리 효율적
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 익명 클래스 - 더 많은 메모리 사용
names.sort(new Comparator<String>() {
    @Override
    public int compare(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
});

// 람다식 - 메모리 효율적
names.sort((s1, s2) -> s1.compareToIgnoreCase(s2));

// 메서드 참조 - 가장 효율적
names.sort(String::compareToIgnoreCase);
```

---

## 🛠 실제 사용 예시

### 1. 웹 개발에서의 활용
```java
// Spring Boot에서의 REST API
@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) String city) {
        List<User> users = userService.getAllUsers();
        
        return users.stream()
            .filter(user -> city == null || city.equals(user.getCity()))  // 람다식으로 필터링
            .map(this::convertToDto)  // 메서드 참조로 변환
            .collect(Collectors.toList());
    }
    
    // 전통적인 메서드 - 재사용 가능한 변환 로직
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
```

### 2. 데이터베이스 처리
```java
public class UserRepository {
    
    // 전통적인 메서드 - 복잡한 쿼리 로직
    public List<User> findActiveUsersByCity(String city, Date since) {
        String sql = "SELECT * FROM users WHERE city = ? AND last_login > ? AND status = 'ACTIVE'";
        return jdbcTemplate.query(sql, this::mapRowToUser, city, since);
    }
    
    // 결과 매핑을 위한 메서드
    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
    
    // 람다식으로 간단한 처리
    public void processUsers(List<User> users) {
        users.forEach(user -> {
            if (user.getEmail() != null) {
                emailService.sendWelcomeEmail(user.getEmail());  // 간단한 일회성 처리
            }
        });
    }
}
```

### 3. 테스트 코드에서의 활용
```java
@Test
public void testUserProcessing() {
    List<User> users = Arrays.asList(
        new User("Alice", "alice@test.com"),
        new User("Bob", "bob@test.com"),
        new User("Charlie", null)
    );
    
    // 람다식으로 테스트 조건 설정
    List<User> usersWithEmail = users.stream()
        .filter(user -> user.getEmail() != null)  // 이메일이 있는 사용자만
        .collect(Collectors.toList());
    
    assertEquals(2, usersWithEmail.size());
    
    // 메서드 참조로 간단한 검증
    assertTrue(usersWithEmail.stream().allMatch(user -> user.getEmail() != null));
}

// 복잡한 검증 로직은 별도 메서드로
private void validateUserData(User user) {
    assertNotNull(user);
    assertNotNull(user.getName());
    assertTrue(user.getName().length() > 0);
    if (user.getEmail() != null) {
        assertTrue(user.getEmail().contains("@"));
    }
}
```

---

## 📚 정리 및 권장사항

### 🎯 메서드 사용 권장 상황
- **재사용성**이 중요한 로직
- **복잡한 비즈니스** 규칙
- **명확한 네이밍**이 필요한 경우
- **테스트**가 용이해야 하는 경우
- **디버깅**이 중요한 경우

### 🎯 람다식 사용 권장 상황
- **일회성** 간단한 로직
- **데이터 변환** 및 필터링
- **이벤트 핸들링**
- **함수형 프로그래밍** 스타일
- **스트림 API**와 함께 사용

### 🎯 혼합 사용 전략
```java
public class DataProcessor {
    
    // 복잡한 로직은 메서드로
    public List<ProcessedData> processUserData(List<User> users) {
        return users.stream()
            .filter(this::isValidUser)        // 메서드 참조 - 재사용 가능한 검증
            .map(user -> enrichUserData(user)) // 메서드 호출 - 복잡한 변환
            .filter(data -> data.getScore() > 50) // 람다식 - 간단한 조건
            .sorted(Comparator.comparing(ProcessedData::getScore).reversed()) // 메서드 참조
            .collect(Collectors.toList());
    }
    
    // 재사용 가능한 검증 로직
    private boolean isValidUser(User user) {
        return user != null && 
               user.getName() != null && 
               user.getEmail() != null && 
               user.getEmail().contains("@");
    }
    
    // 복잡한 데이터 변환 로직
    private ProcessedData enrichUserData(User user) {
        ProcessedData data = new ProcessedData();
        data.setUserId(user.getId());
        data.setScore(calculateUserScore(user));
        data.setRiskLevel(assessRiskLevel(user));
        return data;
    }
}
```

이렇게 메서드와 람다식을 적절히 조합하면 **가독성**, **재사용성**, **유지보수성**을 모두 향상시킬 수 있습니다!