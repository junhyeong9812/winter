# 📘 15-HTTP-METHOD.md

## 📌 도입 목적

실제 웹 환경에서는 동일한 URL 경로라도 HTTP 메서드(GET, POST, PUT 등)에 따라 수행되는 로직이 다르다. 이를 반영하여 `/register` 요청에 대해 `POST`만 허용하고, 그 외의 메서드는 "405 Method Not Allowed" 에러를 반환하도록 개선하였다.

---

## ✅ 구현 목표

| 항목               | 설명                                      |
| ---------------- | --------------------------------------- |
| HttpRequest 개선   | 경로 외에 HTTP 메서드도 함께 관리 (`GET`, `POST` 등) |
| Controller 분기 처리 | 메서드에 따라 허용/거부 분기 (`if (!POST) → 405`)   |
| Dispatcher 예외 처리 | `ModelAndView`가 null일 경우 응답 반환 추가       |

---

## 🧱 주요 클래스 요약

### 📄 HttpRequest.java

```java
public HttpRequest(String rawPath, String method) {
    this.method = method.toUpperCase();
    ... // 기존 path/query 파싱 유지
}
```

* `HttpRequest request = new HttpRequest("/register", "POST")` → request.method = "POST"

---

### 📄 RegisterController.java

```java
public ModelAndView handle(HttpRequest request, HttpResponse response) {
    if (!"POST".equals(request.getMethod())) {
        response.setStatus(405);
        response.setBody("Method Not Allowed: " + request.getMethod());
        return null;
    }
    ... // 기존 로직 유지
}
```

* `POST`가 아닌 요청이면 `405` 상태와 에러 메시지 반환

---

### 📄 Dispatcher.java

```java
ModelAndView mv = adapter.handle(handler, request, response);
if (mv == null) {
    response.send(); // 컨트롤러가 직접 처리한 경우 (예: 405 응답)
    return;
}
```

* `ModelAndView`가 null이어도 `response.send()` 호출로 정상 종료 처리

---

## 🔁 전체 흐름

1. `/register` 경로로 `POST` 요청 시 → 정상 처리
2. `/register` 경로로 `GET`이나 `PUT` 요청 시 → 405 반환
3. `/invalid` 같은 경로 없음 → 404 반환

---

## 🧪 출력 예시

```
 HTTP Response
status = 405
body = Method Not Allowed: PUT

 HTTP Response
status = 404
body = 404 Not Found/invalid

 HTTP Response
status = 200
body = <!DOCTYPE html>
<html>
<head><title>Register</title></head>
<body>
<h2>Registration Info</h2>
<p>Name: Jun</p>
<p>Email: jun@test.com</p>
</body>
</html>
```

---

## ✅ 효과 요약

* ✅ HTTP 메서드에 따른 요청 처리 분기 가능
* ✅ 405/404 에러 응답 처리 개선
* ✅ 컨트롤러에서 유연한 메서드 제어 구조 확보
* ✅ 프레임워크의 HTTP 추상화 완성도 향상
