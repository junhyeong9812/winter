# 📘 JSON 응답 지원 (21단계)

## 🎯 목표

기존 HTML 템플릿 기반 응답에 더해 **JSON 형태 응답**을 지원하여, 동일한 컨트롤러가 클라이언트의 요청 헤더(`Accept`)에 따라 HTML 또는 JSON으로 응답할 수 있도록 구현합니다.

---

## 🔧 구현 내용

### ✅ 1. JSON 직렬화 유틸리티 추가

**파일**: `winter.util.JsonSerializer`

```java
// 객체를 JSON 문자열로 변환
String json = JsonSerializer.toJson(userObject);
// 결과: {"name": "Jun", "address": {"city": "Seoul", "zipcode": "12345"}}
```

**주요 기능**:
- 기본 타입(String, Number, Boolean) 처리
- `Map<String, Object>` → JSON 객체 변환
- 일반 Java 객체 → Reflection을 통한 getter 호출로 JSON 변환
- JSON 특수문자 이스케이프 처리

### ✅ 2. JsonView 클래스 구현

**파일**: `winter.view.JsonView`

```java
// 모델 데이터를 JSON으로 렌더링
@Override
public void render(Map<String, Object> model, HttpResponse response) {
    String jsonContent = JsonSerializer.toJson(model);
    response.addHeader("Content-Type", "application/json");
    response.setBody(jsonContent);
}
```

**특징**:
- `Content-Type: application/json` 자동 설정
- JSON 직렬화 실패 시 에러 응답 구성
- UTF-8 인코딩 지원

### ✅ 3. Content Negotiation 구현

**파일**: `winter.view.ContentNegotiatingViewResolver`

**동작 방식**:
```java
// Accept 헤더 확인
String acceptHeader = request.getHeader("Accept");

if (acceptHeader.contains("application/json")) {
    return new JsonView();        // JSON 응답
} else {
    return htmlViewResolver.resolveViewName(viewName); // HTML 응답
}
```

**지원하는 Accept 헤더**:
- `application/json` → JsonView
- `text/html` 또는 기타 → InternalResourceView (HTML 템플릿)

### ✅ 4. Dispatcher 업데이트

**변경사항**:
- `SimpleViewResolver` → `ContentNegotiatingViewResolver` 교체
- ViewResolver에 현재 요청 정보 전달 (`setCurrentRequest()`)

### ✅ 5. API 테스트 컨트롤러 추가

**파일**: `winter.dispatcher.ApiController`

**테스트 데이터**:
```java
ModelAndView mv = new ModelAndView("api");
mv.addAttribute("user", user);
mv.addAttribute("message", "Welcome to Winter API!");
mv.addAttribute("timestamp", System.currentTimeMillis());
```

---

## 🧪 테스트 시나리오

### 시나리오 1: HTML 응답 (기본)
```http
GET /api HTTP/1.1
Accept: text/html
```
**결과**: HTML 템플릿(`api.html`) 렌더링

### 시나리오 2: JSON 응답
```http
GET /api HTTP/1.1
Accept: application/json
```
**결과**: JSON 형태 응답
```json
{
  "user": {
    "name": "Jun",
    "address": {
      "city": "Seoul",
      "zipcode": "12345"
    }
  },
  "message": "Welcome to Winter API!",
  "timestamp": 1640995200000
}
```

### 시나리오 3: 복합 Accept 헤더
```http
GET /api HTTP/1.1
Accept: text/html, application/json;q=0.9, */*;q=0.8
```
**결과**: JSON 응답 (application/json 감지)

---

## 📊 기술적 특징

### Content Negotiation 패턴
- **단일 컨트롤러**: 동일한 비즈니스 로직
- **다중 표현**: HTML과 JSON 동시 지원
- **헤더 기반**: Accept 헤더로 응답 형식 결정

### JSON 직렬화 방식
- **Reflection 기반**: `getXxx()` 메서드 자동 호출
- **중첩 객체 지원**: `${user.address.city}` 형태 접근
- **타입 안전성**: String, Number, Boolean, 객체 구분 처리

### 확장성 고려
- **ViewResolver 체인**: 여러 ViewResolver 조합 가능
- **JSON 라이브러리 교체**: Jackson 등으로 쉽게 교체 가능
- **추가 미디어 타입**: XML, CSV 등 확장 용이

---

## 🔄 기존 기능과의 호환성

### ✅ 완전 호환
- 기존 HTML 컨트롤러들(`/hello`, `/user`, `/register`) 정상 동작
- 정적 리소스 처리 유지
- 예외 처리 메커니즘 유지

### ✅ 기존 컨트롤러 JSON 변환
```java
// 기존 UserController도 JSON 응답 가능
GET /user?name=Jun&city=Seoul&zipcode=12345
Accept: application/json

// 응답
{
  "user": {
    "name": "Jun", 
    "address": {
      "city": "Seoul",
      "zipcode": "12345"
    }
  }
}
```

---

## 💡 핵심 설계 원칙

### 1. Single Responsibility
- **JsonView**: JSON 렌더링만 담당
- **JsonSerializer**: JSON 변환만 담당
- **ContentNegotiatingViewResolver**: View 선택만 담당

### 2. Open/Closed Principle
- 기존 코드 수정 최소화
- 새로운 View 타입 추가로 확장

### 3. Dependency Inversion
- ViewResolver 인터페이스를 통한 추상화
- 구체적인 View 구현체에 의존하지 않음

---

## 🚀 활용 사례

### RESTful API 개발
```java
// 같은 데이터, 다른 표현
GET /api/users/1          // JSON for API clients  
GET /users/1              // HTML for web browsers
```

### Progressive Enhancement
```javascript
// 클라이언트에서 동적 요청
fetch('/api', {
  headers: { 'Accept': 'application/json' }
})
.then(response => response.json())
.then(data => updateUI(data));
```

### 모바일 앱 지원
- 웹: HTML 템플릿으로 페이지 제공
- 모바일 앱: JSON API로 데이터 제공

---

## 🔮 다음 단계 준비

21단계 완료로 **View 계층의 다형성**이 확보되었습니다:

- **22단계**: `@Controller`, `@RequestMapping` 어노테이션 기반 구조
- **23단계**: `@RequestParam`, `@ModelAttribute` 파라미터 바인딩
- **24단계**: Multipart 파일 업로드

JSON 응답 기반이 마련되어 **RESTful API 구조**로의 발전이 가능해졌습니다.

---

## 📋 변경된 파일 목록

| 파일 | 상태 | 설명 |
|------|------|------|
| `winter.util.JsonSerializer` | 신규 | JSON 직렬화 유틸리티 |
| `winter.view.JsonView` | 신규 | JSON 응답 View |
| `winter.view.ContentNegotiatingViewResolver` | 신규 | Content Negotiation ViewResolver |
| `winter.dispatcher.Dispatcher` | 수정 | ViewResolver 교체 |
| `winter.dispatcher.HandlerMapping` | 수정 | `/api` 경로 추가 |
| `winter.dispatcher.ApiController` | 신규 | JSON 테스트 컨트롤러 |
| `src/winter/templates/api.html` | 신규 | API HTML 템플릿 |
| `winter.WinterMain` | 수정 | JSON 테스트 케이스 추가 |