## 📘 17. 정적 리소스 처리

### 🧠 개요

정적 HTML/CSS/JS 파일을 직접 서빙할 수 있도록 Dispatcher에서 `/static/` 경로 처리를 분리하여 정적 리소스 요청을 대응할 수 있는 구조를 구현했습니다.

---

### ✨ 도입 목적

| 목적               | 설명                                            |
| ---------------- | --------------------------------------------- |
| 정적 자원 서빙 지원      | CSS, JS, 이미지 등의 정적 리소스를 프레임워크 차원에서 제공         |
| 정적 처리 분리         | Controller 핸들링 로직과 독립된 정적 리소스 처리 구조로 유지보수성 향상 |
| MIME Type 설정 가능화 | 파일 확장자에 따른 Content-Type 응답 헤더 설정으로 웹 호환성 확보   |

---

### ✅ 주요 변경 사항

#### 1. `Dispatcher.java`

```java
if (requestPath.startsWith("/static/")) {
    handleStaticResource(requestPath, response);
    return;
}
```

* 정적 리소스 요청은 `/static/`으로 시작하는지 확인 후 분기 처리
* `handleStaticResource()`를 별도 메서드로 분리하여 파일 읽기 및 Content-Type 설정
* 정적 파일 경로는 기본적으로 `src/winter/resources/static/`를 기준으로 설정되며, 경로는 상수로 추후 확장 가능하도록 처리

```java
private static final String STATIC_ROOT = "src/winter/resources";

private void handleStaticResource(String path, HttpResponse response) {
    String filePath = STATIC_ROOT + path;
    ...
}
```

#### 2. `HttpResponse.java` 확장

```java
private final Map<String, String> headers = new HashMap<>();

public void addHeader(String key, String value) {
    headers.put(key, value);
}
```

* Content-Type 등을 위한 헤더 추가 기능 도입
* `.send()` 시 로그로 Content-Type 출력 가능

#### 3. `style.css` 생성 (테스트용)

파일 위치: `src/winter/resources/static/style.css`

```css
body {
  background-color: #f8f8f8;
  font-family: Arial, sans-serif;
  color: #333;
}
```

---

### 🧪 테스트 로그

```http
GET /static/style.css

HTTP Response
status = 200
Content-Type:text/css
body = body {
  background-color: #f8f8f8;
  font-family: Arial, sans-serif;
  color: #333;
}
```

---

### 🎯 효과 요약

| 항목              | 내용                                           |
| --------------- | -------------------------------------------- |
| 정적 파일 서빙        | CSS, JS, 이미지 등 클라이언트 리소스 직접 처리 가능            |
| 구조 분리           | Dispatcher 내 정적 처리 전용 로직으로 유지보수성 향상          |
| Content-Type 지원 | 파일 확장자 기반의 Content-Type 설정 가능 → 웹브라우저 정상 렌더링 |

---

### 🔜 다음 목표

**18단계: Content-Type 및 Request/Response 구조 개선**

* 요청/응답에서 Content-Type을 명시적으로 처리하고, 요청 본문 구조 바인딩 처리로 POST 요청 등 본격 지원
