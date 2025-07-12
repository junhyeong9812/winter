# 📘 4-HANDLER-ADAPTER.md

## 🧠 HandlerAdapter란?

`HandlerAdapter`는 Dispatcher가 핸들러(Controller 등)를 직접 실행하지 않고, **실행을 어댑터에게 위임하기 위해 도입된 실행 전략 인터페이스**이다. 다양한 타입의 핸들러를 유연하게 실행할 수 있게 만들어주는 핵심 구조이다.

Spring MVC에서도 `HandlerAdapter`는 핵심 구조로 존재하며, `@Controller`, `@RestController`, 함수형 핸들러 등 다양한 타입을 지원하는 기반이 된다.

---

## ✨ 도입 목적

| 목적     | 설명                                           |
| ------ | -------------------------------------------- |
| 실행 추상화 | Dispatcher는 핸들러 타입을 알 필요 없이 실행만 위임           |
| 확장성    | 다양한 핸들러 구조 대응 가능 (ex. Controller 외 Lambda 등) |
| 관심사 분리 | 실행 전략을 어댑터로 분리해 Dispatcher가 단순해짐             |

---

## ✅ 인터페이스 정의

```java
public interface HandlerAdapter {
    boolean supports(Object handler); // 이 핸들러를 실행할 수 있는가?
    void handle(Object handler);      // 핸들러 실행
}
```

---

## ✅ 구현체: ControllerHandlerAdapter

```java
public class ControllerHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Override
    public void handle(Object handler) {
        ((Controller) handler).handle();
    }
}
```

* `Controller` 인터페이스를 구현한 핸들러만 실행 대상
* 향후 다른 타입(람다식, 리플렉션 기반 등) 어댑터 추가 가능

---

## 🔁 Dispatcher 리팩토링 전후

### 🔴 Before

```java
if (handler instanceof Controller controller) {
    controller.handle();
} else {
    System.out.println("500 Internal Error");
}
```

### 🟢 After (Adapter 기반)

```java
for (HandlerAdapter adapter : handlerAdapters) {
    if (adapter.supports(handler)) {
        adapter.handle(handler);
        return;
    }
}
System.out.println("500 Internal Error: No adapter for handler type");
```

---

## 📂 파일 구조 업데이트

```text
dispatcher/
├── Dispatcher.java
├── HandlerAdapter.java
├── ControllerHandlerAdapter.java
├── Controller.java
├── HelloController.java
├── ByeController.java
└── HandlerMapping.java
```

---

## 🎯 도입 효과 요약

* Dispatcher가 핸들러 타입을 전혀 몰라도 동작함
* 실행 로직을 Adapter에게 완전히 위임 → 테스트 가능성 높아짐
* 이후 다양한 타입 핸들러 (람다, 어노테이션 기반 등) 확장이 쉬워짐
* 전략 패턴 기반 구조로 프레임워크답게 진화

---

## 🧪 다음 목표

* 요청 정보(Request/Response 객체) 전달 구조 설계
* View 처리 구조 (Model, ViewResolver 등) 설계 시작
* 핸들러 결과를 기반으로 렌더링 구조 연결
