# 📘 2-HANDLER-MAPPING.md

## 🧠 HandlerMapping이란?

Dispatcher에서 직접 관리하던 "요청 경로 ↔ 핸들러 객체" 매핑 로직을 별도의 클래스로 분리한 구조이다. 이를 통해 Dispatcher는 단순히 요청을 받아 전달하는 역할에 집중하고, 핸들러 탐색 로직은 HandlerMapping이 전담한다.

이 구조는 Spring의 `HandlerMapping` 개념과 유사하며, SRP(단일 책임 원칙)를 만족시킨다.

---

## 🔄 리팩토링 전과 후

### 🔴 Before (Dispatcher가 모든 책임 가짐)

```java
private final Map<String, Object> handlerMapping = new HashMap<>();

public Dispatcher() {
    handlerMapping.put("/hello", new HelloController());
    handlerMapping.put("/bye", new ByeController());
}
```

### 🟢 After (HandlerMapping 클래스로 위임)

```java
public class Dispatcher {
    private final HandlerMapping handlerMapping = new HandlerMapping();

    public void dispatch(String requestPath) {
        Object handler = handlerMapping.getHandler(requestPath);
        // 이하 생략...
    }
}
```

---

## 📂 디렉토리 구조 변화

```text
winter/
└── src/
    └── winter/
        └── dispatcher/
            ├── Dispatcher.java         // Dispatcher 흐름 담당
            ├── HandlerMapping.java     // URL → 핸들러 매핑 책임 분리
            ├── HelloController.java    // 테스트용 핸들러
            └── ByeController.java      // 테스트용 핸들러
```

---

## 📄 HandlerMapping.java 주요 내용

```java
public class HandlerMapping {
    private final Map<String, Object> mapping = new HashMap<>();

    public HandlerMapping() {
        mapping.put("/hello", new HelloController());
        mapping.put("/bye", new ByeController());
    }

    public Object getHandler(String path) {
        return mapping.get(path);
    }
}
```

* 요청 경로 문자열을 키로, 핸들러 객체를 값으로 갖는 Map 사용
* 추후 자동 등록, 어노테이션 기반 매핑 확장을 고려한 구조

---

## ✅ Dispatcher.java 수정 사항

```java
Object handler = handlerMapping.getHandler(requestPath);
```

* 더 이상 Dispatcher가 직접 Map을 들고 있지 않음
* Dispatcher는 오직 "요청 전달 및 실행"에 집중

---

## 🎯 리팩토링 이유 및 효과

| 이유      | 설명                                   |
| ------- | ------------------------------------ |
| SRP 적용  | Dispatcher와 매핑 책임을 분리하여 각각 단일 책임을 가짐 |
| 테스트 용이성 | HandlerMapping 단독 테스트 가능             |
| 확장성 확보  | 이후 어노테이션 기반 자동 매핑 등 구조 확장이 용이        |
| 코드 가독성  | Dispatcher 코드가 단순해지고, 관심사 분리가 잘 됨    |

---

## 🧪 다음 목표
* Controller 인터페이스 도입 → if-else 구조 제거
* 공통 `handle()` 메서드 정의
* Dispatcher의 실행 흐름을 동적 처리 방식으로 개선
