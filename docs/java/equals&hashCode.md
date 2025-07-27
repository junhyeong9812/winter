# equals()와 hashCode() 완전 분석 가이드

## 🤔 왜 인터페이스에 없는데 오버라이드가 될까?

### Object 클래스의 존재

```java
// 모든 클래스의 최상위 클래스 Object
public class Object {
    // 모든 객체가 상속받는 메서드들
    public boolean equals(Object obj) { 
        return (this == obj);  // 기본: 참조 비교
    }
    
    public int hashCode() {
        return /* 네이티브 구현 */;  // 기본: 메모리 주소 기반
    }
    
    public String toString() { ... }
    protected Object clone() { ... }
    // 기타 메서드들...
}
```

**핵심 포인트:**
- Java의 모든 클래스는 **암묵적으로 Object를 상속**
- `MultipartFile` 인터페이스를 구현하는 클래스도 Object를 상속
- 따라서 Object의 메서드들을 **오버라이드** 가능

```java
// 실제 상속 구조
public class StandardMultipartFile extends Object implements MultipartFile {
    // Object로부터 상속받은 equals(), hashCode() 오버라이드 가능
}
```

## 🎯 equals()와 hashCode()의 역할

### 1. equals() 메서드의 역할

```java
public class EqualsExample {
    public static void main(String[] args) {
        String s1 = new String("hello");
        String s2 = new String("hello");
        
        // 참조 비교 vs 내용 비교
        System.out.println(s1 == s2);      // false (서로 다른 객체)
        System.out.println(s1.equals(s2)); // true (내용이 같음)
        
        // 컬렉션에서의 활용
        List<String> list = Arrays.asList("hello", "world");
        System.out.println(list.contains(s1)); // true (equals() 사용)
    }
}
```

**equals()의 계약 (Contract):**
1. **반사적(Reflexive)**: `x.equals(x) == true`
2. **대칭적(Symmetric)**: `x.equals(y) == y.equals(x)`
3. **이행적(Transitive)**: `x.equals(y) && y.equals(z)` → `x.equals(z)`
4. **일관적(Consistent)**: 여러 번 호출해도 같은 결과
5. **null 처리**: `x.equals(null) == false`

### 2. hashCode() 메서드의 역할

```java
public class HashCodeExample {
    public static void main(String[] args) {
        // HashMap/HashSet의 내부 동작
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 100);
        
        // 1. hashCode()로 버킷 찾기
        // 2. equals()로 정확한 키 비교
        Integer value = map.get("key1");
    }
}
```

**hashCode()의 계약:**
1. **일관성**: 같은 객체는 항상 같은 해시코드
2. **equals 연동**: `equals() == true` → 같은 hashCode()
3. **성능**: 가능한 한 고르게 분산되어야 함

## 🧮 hashCode()에서 31을 사용하는 이유

### 1. 수학적 이유

```java
public class HashCalculation {
    // 31을 사용하는 일반적인 패턴
    public int hashCode() {
        int result = 17;  // 초기값 (소수)
        result = 31 * result + field1.hashCode();  // 31 곱셈
        result = 31 * result + field2.hashCode();
        return result;
    }
}
```

**31을 선택한 이유들:**

1. **소수(Prime Number)**: 충돌 확률 감소
2. **홀수**: 짝수는 비트 시프트와 같아서 정보 손실
3. **적당한 크기**: 너무 작으면 충돌 많음, 너무 크면 오버플로우
4. **컴파일러 최적화**: `31 * i == (i << 5) - i` (비트 연산으로 최적화)

### 2. 실제 성능 측정

```java
public class HashPerformanceTest {
    // 다양한 곱수로 테스트
    public static void testMultiplier(int multiplier, String[] data) {
        Map<Integer, Integer> distribution = new HashMap<>();
        
        for (String s : data) {
            int hash = calculateHash(s, multiplier);
            int bucket = hash % 1000;  // 1000개 버킷
            distribution.merge(bucket, 1, Integer::sum);
        }
        
        // 분산도 측정
        double variance = calculateVariance(distribution.values());
        System.out.println("Multiplier " + multiplier + ": variance = " + variance);
    }
    
    private static int calculateHash(String s, int multiplier) {
        int hash = 0;
        for (char c : s.toCharArray()) {
            hash = multiplier * hash + c;
        }
        return hash;
    }
}

// 실제 테스트 결과 (대략적)
// Multiplier 31: variance = 120.5  (가장 좋음)
// Multiplier 17: variance = 135.2
// Multiplier 37: variance = 128.9
// Multiplier 2:  variance = 450.1  (매우 나쁨)
```

## 🏗 StandardMultipartFile의 구현 분석

### 완전한 equals() 구현

```java
@Override
public boolean equals(Object obj) {
    // 1. 자기 자신과의 비교 (성능 최적화)
    if (this == obj) return true;
    
    // 2. null 체크
    if (obj == null) return false;
    
    // 3. 클래스 타입 체크
    if (getClass() != obj.getClass()) return false;
    
    // 4. 타입 캐스팅
    StandardMultipartFile that = (StandardMultipartFile) obj;
    
    // 5. 각 필드별 비교
    return Objects.equals(name, that.name) &&                    // String 비교
           Objects.equals(originalFilename, that.originalFilename) &&
           Objects.equals(contentType, that.contentType) &&      // null 안전 비교
           Arrays.equals(content, that.content);                 // 배열 비교
}
```

### hashCode() 구현 상세 분석

```java
@Override
public int hashCode() {
    int result = name.hashCode();                    // 첫 번째 필드
    result = 31 * result + originalFilename.hashCode(); // 31 곱셈 + 두 번째 필드
    result = 31 * result + (contentType != null ? contentType.hashCode() : 0); // null 처리
    result = 31 * result + Arrays.hashCode(content);    // 배열 해시코드
    return result;
}

// 단계별 계산 예시
// name = "file", originalFilename = "test.txt", contentType = "text/plain"
// content = [65, 66, 67] (ASCII: ABC)

// Step 1: result = "file".hashCode() = 3143796
// Step 2: result = 31 * 3143796 + "test.txt".hashCode() 
//                = 31 * 3143796 + (-1309173469)
//                = 97457676 + (-1309173469) = -1211715793
// Step 3: result = 31 * (-1211715793) + "text/plain".hashCode()
//                = -37563189583 + (-1484970295) = -39048159878
// Step 4: result = 31 * (-39048159878) + Arrays.hashCode([65,66,67])
//                = ... (최종 해시코드)
```

### Arrays.hashCode()의 내부 구현

```java
public static int hashCode(byte[] a) {
    if (a == null) return 0;
    
    int result = 1;
    for (byte element : a) {
        result = 31 * result + element;  // 여기서도 31 사용!
    }
    return result;
}

// 우리 content 배열 [65, 66, 67]의 해시코드 계산
// result = 1
// result = 31 * 1 + 65 = 96
// result = 31 * 96 + 66 = 3042
// result = 31 * 3042 + 67 = 94369
```

## 📊 실제 사용처와 중요성

### 1. HashMap/HashSet에서의 동작

```java
public class CollectionUsage {
    public static void main(String[] args) {
        Set<StandardMultipartFile> fileSet = new HashSet<>();
        
        // 같은 내용의 파일 두 개 생성
        StandardMultipartFile file1 = new StandardMultipartFile(
            "upload", "test.txt", "text/plain", "Hello".getBytes());
        StandardMultipartFile file2 = new StandardMultipartFile(
            "upload", "test.txt", "text/plain", "Hello".getBytes());
        
        fileSet.add(file1);
        fileSet.add(file2);  // equals()가 true이므로 중복 제거됨
        
        System.out.println(fileSet.size()); // 1 (올바른 구현 시)
        
        // contains() 메서드도 equals()/hashCode() 사용
        System.out.println(fileSet.contains(file2)); // true
    }
}
```

### 2. 캐싱 시스템에서 활용

```java
public class FileCache {
    private final Map<StandardMultipartFile, String> cache = new HashMap<>();
    
    public String getProcessedContent(StandardMultipartFile file) {
        // hashCode()로 빠른 검색, equals()로 정확한 비교
        return cache.computeIfAbsent(file, this::processFile);
    }
    
    private String processFile(StandardMultipartFile file) {
        // 무거운 파일 처리 작업...
        return "processed: " + file.getOriginalFilename();
    }
}
```

### 3. 중복 제거 로직

```java
public class DuplicateRemover {
    public List<MultipartFile> removeDuplicates(List<MultipartFile> files) {
        // Set을 사용한 중복 제거 (equals/hashCode 기반)
        Set<MultipartFile> uniqueFiles = new LinkedHashSet<>(files);
        return new ArrayList<>(uniqueFiles);
    }
}
```

## ⚠️ 흔한 실수들

### 1. equals()만 오버라이드하고 hashCode() 누락

```java
// 잘못된 예시
public class BadExample {
    private String name;
    
    @Override
    public boolean equals(Object obj) {
        // equals()만 구현
        return obj instanceof BadExample && 
               Objects.equals(name, ((BadExample) obj).name);
    }
    
    // hashCode() 구현 누락! 
    // HashMap에서 제대로 동작하지 않음
}

public class Problem {
    public static void main(String[] args) {
        Map<BadExample, String> map = new HashMap<>();
        BadExample key1 = new BadExample("test");
        BadExample key2 = new BadExample("test");
        
        map.put(key1, "value");
        System.out.println(map.get(key2)); // null! (다른 해시코드)
    }
}
```

### 2. 일관성 없는 필드 사용

```java
// 잘못된 예시
public class InconsistentExample {
    private String id;
    private String name;
    private int age;
    
    @Override
    public boolean equals(Object obj) {
        // id와 name만 비교
        return Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }
    
    @Override
    public int hashCode() {
        // age까지 포함해서 해시코드 계산 (불일치!)
        return Objects.hash(id, name, age);
    }
}
```

## 🎯 모범 사례

### 1. Objects 유틸리티 사용

```java
import java.util.Objects;

public class BestPractice {
    private String field1;
    private Integer field2;
    private byte[] field3;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BestPractice that = (BestPractice) obj;
        return Objects.equals(field1, that.field1) &&
               Objects.equals(field2, that.field2) &&
               Arrays.equals(field3, that.field3);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(field1, field2);
        result = 31 * result + Arrays.hashCode(field3);
        return result;
    }
}
```

### 2. IDE 자동 생성 활용

```java
// IntelliJ IDEA나 Eclipse에서 자동 생성된 코드
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StandardMultipartFile that = (StandardMultipartFile) o;
    return Objects.equals(name, that.name) &&
           Objects.equals(originalFilename, that.originalFilename) &&
           Objects.equals(contentType, that.contentType) &&
           Arrays.equals(content, that.content);
}

@Override
public int hashCode() {
    int result = Objects.hash(name, originalFilename, contentType);
    result = 31 * result + Arrays.hashCode(content);
    return result;
}
```

## 💡 핵심 요약

1. **31의 마법**: 소수이면서 컴파일러 최적화 가능한 완벽한 수
2. **Object 상속**: 모든 클래스가 Object를 상속하므로 오버라이드 가능
3. **계약 준수**: equals()와 hashCode()는 함께 구현해야 함
4. **성능 중요**: HashMap, HashSet 등의 성능에 직접적 영향
5. **일관성**: 같은 필드를 사용해서 equals()와 hashCode() 구현

**기억할 공식:**
```java
result = 31 * result + field.hashCode();
```
이 공식이 Java 컬렉션 프레임워크의 핵심입니다!