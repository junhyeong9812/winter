# 📘 9-REQUEST-RESPONSE.md

## 🧠 HttpRequest / HttpResponse 도입이란?

Winter 프레임워크에서 `HttpRequest`와 `HttpResponse`는 컨트롤러가 클라이언트의 요청 데이터를 읽고, 응답 데이터를 구성할 수 있도록 도와주는 **추상화된 요청/응답 객체**이다.

기존에는 `Dispatcher`가 직접 요청 경로만 처리했다면, 이제는 `request.getPath()`를 포함한 다양한 요청 데이터와, `response.setBody()` 등 다양한 응답 설정을 분리된 객체를 통해 수행할 수 있게 된다.

---

## ✨ 도입 목적

| 목적      | 설명                                                             |
| ------- | -------------------------------------------------------------- |
| 역할 분리   | Dispatcher의 책임 중 요청/응답 로직을 전담 객체로 위임                           |
| 추상화 강화  | Controller가 단순 문자열 대신 HttpRequest/HttpResponse 객체를 통해 작업 수행 가능 |
| 확장성 향상  | 나중에 쿠키, 헤더, 파라미터 등 다양한 요청 정보 확장을 위해 유연한 구조 제공                  |
| 테스트 용이성 | 단위 테스트 시 요청/응답을 시뮬레이션 하기 쉬운 구조 도입                              |

---

## ✅ 클래스 정의

### 📄 HttpRequest.java

```java
package winter.http;

public class HttpRequest {
    private final String path;

    public HttpRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
```

### 📄 HttpResponse.java

```java
package winter.http;

public class HttpResponse {
    private int status = 200;
    private String body = "";

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void send() {
        System.out.println("HTTP Status: " + status);
        System.out.println("Response Body: " + body);
    }
}
```

---

## 🔁 기존 구조 리팩토링

### 🔴 Before (단순 문자열만 처리)

```java
String requestPath = "/hello";
```

### 🟢 After (HttpRequest/HttpResponse 객체 기반)

```java
HttpRequest request = new HttpRequest("/hello");
HttpResponse response = new HttpResponse();
dispatcher.dispatch(request, response);
```

---

## 📄 Dispatcher.java (변경 사항 요약)

```java
String requestPath = request.getPath();
...
response.setStatus(404);
response.setBody("404 Not Found: " + requestPath);
response.send();
```

---

## 🎯 도입 효과 요약

* `Dispatcher`는 더 이상 문자열 대신 객체 기반으로 요청/응답 처리
* `Controller`도 `request`에서 경로 등 데이터 추출 가능
* `response.send()`를 통해 일관된 응답 포맷 출력
* 추후 헤더, 쿠키, 쿼리 파라미터, POST 데이터 등으로 확장 가능

---

## 🧪 다음 목표

* `ModelAndView` 도입으로 데이터(Model)와 뷰(View) 명시적 분리
* `Controller.handle()` → `ModelAndView` 반환 구조 확립
* ViewResolver, View와 연결하여 MVC 흐름 완성
