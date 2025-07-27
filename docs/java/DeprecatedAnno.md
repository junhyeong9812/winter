# @Deprecated 어노테이션 완전 가이드

## 📋 기본 개념

### @Deprecated란?
`@Deprecated`는 특정 메서드, 클래스, 필드가 더 이상 사용되지 않음을 나타내는 Java 표준 어노테이션입니다.

```java
@Deprecated
public Map<String, String> getParameters() {
    // 구현...
}
```

## 🎯 사용 목적

### 1. 하위 호환성 유지
- 기존 코드가 깨지지 않도록 메서드를 유지
- 새로운 API로의 점진적 마이그레이션 지원

### 2. 개발자에게 경고
- IDE에서 취소선으로 표시
- 컴파일러 경고 메시지 생성
- 더 나은 대안이 있음을 알림

## 🔧 HttpRequest에서의 사용 사례

### 기존 메서드 (Deprecated)
```java
/**
 * 전체 파라미터 Map 반환 (하위 호환성을 위해 첫 번째 값만)
 * 
 * @deprecated getParameters() 대신 getParameterMap() 사용 권장
 * @return 파라미터명 -> 첫 번째 값 맵
 */
@Deprecated
public Map<String, String> getParameters() {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
        List<String> values = entry.getValue();
        if (!values.isEmpty()) {
            result.put(entry.getKey(), values.get(0)); // 첫 번째 값만 반환
        }
    }
    return result;
}
```

### 새로운 메서드 (권장)
```java
/**
 * 전체 파라미터 Map 반환 (다중값 지원)
 * 
 * @return 파라미터명 -> 값 리스트 맵
 */
public Map<String, List<String>> getParameterMap() {
    Map<String, List<String>> result = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
        result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return result;
}
```

## 📝 @Deprecated의 진화

### Java 9 이전
```java
@Deprecated
public void oldMethod() {
    // 구현
}
```

### Java 9 이후 (향상된 @Deprecated)
```java
@Deprecated(
    since = "1.2",           // 언제부터 deprecated인지
    forRemoval = true        // 향후 제거될 예정인지
)
public void oldMethod() {
    // 구현
}
```

## 🚨 컴파일러 경고

### 경고 메시지 예시
```
Warning: The method getParameters() from the type HttpRequest is deprecated
```

### 경고 억제
```java
@SuppressWarnings("deprecation")
public void useDeprecatedMethod() {
    Map<String, String> params = request.getParameters(); // 경고 없음
}
```

## 🔄 마이그레이션 전략

### 1. 점진적 교체
```java
// 기존 코드 (deprecated 사용)
Map<String, String> params = request.getParameters();
String name = params.get("name");

// 새로운 코드 (권장 방법)
String name = request.getParameter("name"); // 단일값
List<String> names = request.getParameterValues("name"); // 다중값
```

### 2. JavaDoc으로 가이드 제공
```java
/**
 * @deprecated 
 * 이 메서드는 다중값 파라미터를 제대로 처리하지 못합니다.
 * 대신 다음을 사용하세요:
 * <ul>
 *   <li>{@link #getParameter(String)} - 단일값</li>
 *   <li>{@link #getParameterValues(String)} - 다중값</li>
 *   <li>{@link #getParameterMap()} - 전체 맵</li>
 * </ul>
 * 
 * @see #getParameterMap()
 * @since 1.0
 */
@Deprecated
public Map<String, String> getParameters() {
    // 구현...
}
```

## 💡 베스트 프랙티스

### 1. 명확한 대안 제시
```java
/**
 * @deprecated Use {@link #newMethod()} instead
 */
@Deprecated
public void oldMethod() {
    newMethod(); // 내부적으로 새 메서드 호출
}
```

### 2. 점진적 제거 계획
```java
// Phase 1: @Deprecated 추가
@Deprecated
public void method() { ... }

// Phase 2: forRemoval = true 추가
@Deprecated(forRemoval = true)
public void method() { ... }

// Phase 3: 완전 제거
// public void method() { ... } // 제거됨
```

### 3. 테스트에서의 처리
```java
public class HttpRequestTest {
    
    @Test
    @SuppressWarnings("deprecation")
    public void testBackwardCompatibility() {
        // 하위 호환성 테스트에서만 사용
        Map<String, String> params = request.getParameters();
        assertNotNull(params);
    }
    
    @Test
    public void testNewAPI() {
        // 새로운 API 테스트
        Map<String, List<String>> paramMap = request.getParameterMap();
        assertNotNull(paramMap);
    }
}
```

## 🔍 실제 사용 시나리오

### 라이브러리 진화 과정
```java
// Version 1.0 - 원본
public String getValue() {
    return value;
}

// Version 1.1 - 새 메서드 추가
public String getValue() {
    return value;
}

public Optional<String> getValueSafely() { // 새로운 안전한 메서드
    return Optional.ofNullable(value);
}

// Version 1.2 - 기존 메서드 deprecated
@Deprecated
public String getValue() {
    return value;
}

public Optional<String> getValueSafely() {
    return Optional.ofNullable(value);
}

// Version 2.0 - 기존 메서드 제거
// public String getValue() { ... } // 제거됨
public Optional<String> getValueSafely() {
    return Optional.ofNullable(value);
}
```

## 📊 도구 지원

### IDE 표시
- **Eclipse**: 취소선 + 경고 아이콘
- **IntelliJ**: 취소선 + 회색 텍스트
- **VS Code**: 취소선 + 툴팁

### 정적 분석 도구
```xml
<!-- Maven에서 deprecated 사용 검사 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-Xlint:deprecation</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## 🎯 요약

1. **목적**: 하위 호환성 유지하면서 API 진화
2. **효과**: 개발자에게 명확한 신호 전달
3. **전략**: 점진적 마이그레이션 지원
4. **문서화**: JavaDoc으로 대안 명시
5. **도구 지원**: IDE와 빌드 도구의 경고 시스템 활용