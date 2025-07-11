# 📘 DISPATCHER.md

## 1장. Dispatcher란?

Dispatcher는 클라이언트 요청을 받아서, 그 요청 경로(URL)에 맞는 컨트롤러(핸들러)를 찾아 실행해주는 핵심 클래스다. 이는 Spring MVC의 DispatcherServlet과 유사하며, 프레임워크의 중심 허브 역할을 한다.

---

## 🧩 현재 구조

```text
winter/
└── src/
    └── winter/
        ├── WinterMain.java          // 진입점
        └── dispatcher/
            ├── Dispatcher.java      // Dispatcher 역할
            ├── HelloController.java // 테스트 핸들러
            └── ByeController.java   // 테스트 핸들러
```

---

## 🔍 궁금했던 점과 해석

### Q1. `/invalid` 요청이 왜 404 Not Found가 되는가?

* Dispatcher는 내부적으로 `Map<String, Object> handlerMapping`을 사용하여 경로와 컨트롤러 객체를 매핑한다.
* `/hello`와 `/bye`는 이 맵에 등록되어 있지만, `/invalid`는 등록된 적이 없다.
* `Map.get("/invalid")`의 결과는 `null`이기 때문에 다음 조건에 걸린다:

```java
if (handler == null) {
    System.out.println("404 Not Found: " + requestPath);
    return;
}
```

✅ 즉, "정의되지 않은 경로는 핸들러가 null이기 때문에 404" 라는 것이 논리적 흐름이다.

---

### Q2. `instanceof`는 왜 쓰는가? 어떤 기능인가?

* `instanceof`는 객체가 특정 클래스의 인스턴스인지 확인하는 키워드다.
* 자바 16부터는 패턴 매칭을 지원하여 다음과 같은 간결한 표현이 가능:

```java
if (handler instanceof HelloController hello) {
    hello.handle();
}
```

* 위 코드는 다음과 동일한 전통적인 코드의 축약 버전이다:

```java
if (handler instanceof HelloController) {
    HelloController hello = (HelloController) handler;
    hello.handle();
}
```

✅ 즉, 타입 검사 + 다운캐스팅 + 선언을 한 줄로 수행하는 최신 문법이다.

---

### Q3. 왜 if문으로 직접 핸들러를 호출했는가?

* 아직은 단순한 구조이기 때문에, URL 경로에 따른 객체 타입을 직접 if문으로 비교함.
* 하지만 이 구조는 확장성이 떨어지고, 핸들러 수가 많아지면 비효율적이다.
* 다음 챕터에서 `HandlerMapping`과 `HandlerAdapter`를 분리하여 리팩토링할 예정.

---

## ✅ 출력 예시

```text
🌨️ Winter Framework 시작!
👋 Hello from Winter Framework!
👋 Goodbye from Winter Framework!
404 Not Found: /invalid
```

---

## 📄 주석 포함 전체 Dispatcher 코드

```java
package winter.dispatcher;

import java.util.HashMap;
import java.util.Map;

// Dispatcher는 클라이언트 요청을 받아서 해당 핸들러(컨트롤러)로 전달하는 핵심 클래스다.
// 우리가 수동으로 만든 HandlerMapping, HandlerAdapter와 연결될 예정이며,
// 실제 요청 URL에 따라 어떤 메서드를 실행할지 라우팅 책임을 가진다.

public class Dispatcher {

    // 요청 URL과 컨트롤러(핸들러) 객체를 매핑하는 역할
    private final Map<String, Object> handlerMapping = new HashMap<>();

    public Dispatcher() {
        // 임시 라우팅 등록 (핸들러 직접 연결)
        // 나중엔 자동 등록 방식으로 리팩토링 예정
        handlerMapping.put("/hello", new HelloController());
        handlerMapping.put("/bye", new ByeController());
    }

    // 요청을 처리하는 메서드
    public void dispatch(String requestPath) {
        Object handler = handlerMapping.get(requestPath);

        // 핸들러가 없으면 404 응답
        if (handler == null) {
            System.out.println("404 Not Found: " + requestPath);
            return;
        }

        // Java 16+ instanceof 패턴 매칭 사용:
        // handler가 HelloController 타입이면 hello 변수에 캐스팅해서 handle() 실행
        if (handler instanceof HelloController hello) {
            hello.handle();
        } 
        // handler가 ByeController 타입이면 bye 변수에 캐스팅해서 handle() 실행
        else if (handler instanceof ByeController bye) {
            bye.handle();
        } 
        // 등록된 핸들러지만 타입이 알 수 없을 경우 → 500 에러
        else {
            System.out.println("500 Internal Error: Unknown handler type");
        }
    }
}
```

---

## 🧪 다음 목표

* `HandlerMapping`을 별도 클래스로 분리하여 객체 지향적 구조 강화
* 핸들러 추상화 및 공통 인터페이스 설계
