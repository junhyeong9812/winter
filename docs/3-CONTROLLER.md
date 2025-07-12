# 📘 3-CONTROLLER.md

## 🧠 Controller 인터페이스란?

Winter 프레임워크에서 모든 컨트롤러(핸들러)가 공통적으로 구현해야 하는 동작을 정의한 인터페이스이다. Dispatcher는 이 인터페이스만 보고 일관된 방식으로 `handle()` 메서드를 호출할 수 있게 된다.

Spring MVC에서의 `@Controller` 또는 `Controller 인터페이스`와 유사한 개념이다.

---

## ✨ 도입 목적

| 목적        | 설명                                    |
| --------- | ------------------------------------- |
| 코드 단순화    | Dispatcher의 `if-else` 분기 제거           |
| 일관된 실행 흐름 | 모든 컨트롤러가 동일한 `handle()` 메서드를 구현함      |
| 구조 확장성    | 이후 `HandlerAdapter` 도입 시 기반이 되는 규약 제공 |
| 테스트 용이성   | 실행 흐름을 인터페이스 단위로 테스트 가능               |

---

## ✅ 인터페이스 정의

```java
package winter.dispatcher;

public interface Controller {
    void handle();
}
```

* 모든 컨트롤러는 이 인터페이스를 `implements` 해야 함
* Dispatcher는 해당 타입으로만 `handle()` 호출 가능

---

## 🔁 기존 구조 리팩토링

### 🔴 Before (Dispatcher 내부)

```java
if (handler instanceof HelloController hello) {
    hello.handle();
} else if (handler instanceof ByeController bye) {
    bye.handle();
} else {
    System.out.println("500 Internal Error");
}
```

### 🟢 After (인터페이스 기반)

```java
if (handler instanceof Controller controller) {
    controller.handle();
} else {
    System.out.println("500 Internal Error");
}
```

* 어떤 컨트롤러든 `Controller` 타입이면 handle 가능 → 분기 제거

---

## 📄 HelloController.java (예시)

```java
package winter.dispatcher;

public class HelloController implements Controller {
    @Override
    public void handle() {
        System.out.println("👋 Hello from Winter Framework!");
    }
}
```

---

## 🎯 도입 효과 요약

* `handle()`이라는 **공통 실행 메서드 강제화**
* Dispatcher가 **구체 클래스 이름을 몰라도 실행 가능**
* 이후 다양한 실행 방식을 분리할 **HandlerAdapter 구조 도입의 기반 완성**

---

## 🧪 다음 목표

* HandlerAdapter 인터페이스 도입
* 다양한 타입의 핸들러 실행 가능하게 추상화
* Dispatcher는 Adapter에게 실행 위임 (전략 패턴 기반)
