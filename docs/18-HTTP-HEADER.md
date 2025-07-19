## 📘 18. HTTP Request Header 구조 확장

### 🧠 개요

기존에는 `HttpRequest` 객체가 URI, 쿼리 파라미터만 파싱했으나, 이번 단계에서는 **Request Header** 구조를 도입하여 `Accept`, `Content-Type` 등 주요 HTTP 헤더 정보를 파싱하고 조회할 수 있도록 기능을 확장했습니다.

---

### ✨ 도입 목적

| 목적                 | 설명                                                |
| ------------------ | ------------------------------------------------- |
| 헤더 기반 요청 처리        | `Content-Type`, `Accept` 등 Header 값 파싱 및 조회 기능 추가 |
| JSON/HTML 분기 구조 기반 | 이후 View 응답 시 JSON/HTML 등의 분기 처리 기반 확보             |

---

### ✅ 주요 변경 사항

#### 1. `HttpRequest` 구조 확장

```java
private final Map<String, String> headers = new HashMap<>();
```

#### 2. 헤더 파싱 로직 추가

```java
String line;
while (!(line = reader.readLine()).isEmpty()) {
    int colonIndex = line.indexOf(":");
    if (colonIndex != -1) {
        String key = line.substring(0, colonIndex).trim();
        String value = line.substring(colonIndex + 1).trim();
        headers.put(key, value);
    }
}
```

#### 3. 헤더 조회 메서드 추가

```java
public String getHeader(String name) {
    return headers.get(name);
}
```

---

### 🧪 테스트 로그

```http
GET /user?name=Jun HTTP/1.1
Host: localhost
Accept: text/html
Content-Type: application/json

HttpRequest.getHeader("Accept")        -> text/html
HttpRequest.getHeader("Content-Type")  -> application/json
```

---

### 🎯 효과 요약

| 항목          | 내용                                 |
| ----------- | ---------------------------------- |
| 구조 확장       | 헤더 정보를 파싱하고 조회할 수 있는 구조로 개선        |
| 후속 기능 기반 확보 | JSON 응답 등 Content-Type 분기 처리 기반 마련 |

---

### 🔜 다음 목표

**19단계: 전역 예외 처리 및 컨트롤러 어드바이스 구조 설계**

* Controller 계층에서 발생하는 예외를 일괄적으로 처리할 수 있는 구조 구현
* `ExceptionResolver`, `ControllerAdvice` 등 전역 처리기 구조 설계
