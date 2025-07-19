# 19. 전역 예외 처리 구조 (ExceptionResolver)

## 📌 도입 목적

서블릿 기반 웹 프레임워크에서는 공통적인 예외 처리 구조가 필요합니다. 기존에는 Controller 또는 Dispatcher에서 예외가 발생하면 콘솔 로그 외의 별도 처리가 불가능했지만, 이제 `ExceptionResolver`를 통해 전역 예외 처리 체계를 구성합니다.

---

## 🎯 목표

| 항목            | 내용                                                |
| ------------- | ------------------------------------------------- |
| 예외 처리 추상화 도입  | `ExceptionResolver` 인터페이스 도입으로 다양한 예외 처리 전략 분리 가능 |
| Dispatcher 연계 | Dispatcher에서 발생한 예외를 전역적으로 위임하여 처리                |
| 응답 일관성 확보     | 예외 발생 시에도 클라이언트에게 일관된 HTTP 응답을 제공                 |

---

## ✅ 주요 변경 사항

### 1. `ExceptionResolver` 인터페이스 도입

```java
public interface ExceptionResolver {
    boolean resolveException(HttpRequest request, HttpResponse response, Exception ex);
}
```

* 모든 전역 예외 처리기는 해당 인터페이스를 구현
* `resolveException()` 내부에서 상태코드, 헤더, 응답 바디를 설정
* true/false 반환으로 처리 여부 전달

---

### 2. `SimpleExceptionResolver` 구현체 추가

```java
public class SimpleExceptionResolver implements ExceptionResolver {
    @Override
    public boolean resolveException(HttpRequest request, HttpResponse response, Exception ex) {
        response.setStatus(500);
        response.addHeader("Content-Type", "text/plain");
        response.setBody("Internal Server Error: " + ex.getMessage());
        return true;
    }
}
```

* 내부적으로 500 상태와 간단한 텍스트 응답을 리턴
* 추후 JSON 응답 구조나 HTML 에러 페이지로 교체 가능

---

### 3. Dispatcher 연동 처리

```java
try {
    adapter.handle(handler, request, response);
} catch (Exception ex) {
    for (ExceptionResolver resolver : exceptionResolvers) {
        if (resolver.resolveException(request, response, ex)) {
            return;
        }
    }
    throw ex; // 해결되지 않으면 재던짐
}
```

* Dispatcher 내부에서 예외 발생 시 등록된 ExceptionResolver 순회하여 위임
* 하나라도 처리하면 그 즉시 종료

---

## 🧪 테스트 로그

```http
GET /error

HTTP Response
status = 500
Content-Type:text/plain
body = Internal Server Error: Something went wrong
```

---

## 📁 파일 구조 변화

```
📦winter
 ┣ 📂exception
 ┃ ┣ 📜ExceptionResolver.java
 ┃ ┗ 📜SimpleExceptionResolver.java
```

---

## 🎯 효과 요약

| 항목        | 내용                              |
| --------- | ------------------------------- |
| 전역 예외 핸들링 | 컨트롤러 및 Dispatcher의 예외를 일괄 처리 가능 |
| 구조 확장 용이성 | 다양한 예외 타입별로 Resolver 확장 가능      |
| 응답 통일성    | 예외 발생 시에도 클라이언트에 안정적인 응답 제공     |

---

## 🔜 다음 목표

**20단계: JSON View 및 Content-Type 분기 처리**

* View 타입을 동적으로 처리하여 JSON 응답을 반환할 수 있도록 View 추상화를 확장합니다.
* `Content-Type: application/json`인 경우 JSONView를 통해 직렬화하여 출력할 수 있도록 구현 예정입니다.
