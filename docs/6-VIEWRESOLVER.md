# 📘 6-VIEWRESOLVER.md

## 👀 ViewResolver라는?

특정 동작을 수행한 핸들러로부터 반환되는 논리적 뷰 이름 (ex: "hello")을 시작 경로 (ex: "/views/hello.html")로 변환해주는 체계를 구현.

Spring MVC의 `InternalResourceViewResolver`와 유사한 구조로, Dispatcher는 뷰 경로 변환을 도메인의 단위 체계에 의존한다.

---

## ✅ 도입 배경

### 🔴 Before

Dispatcher가 컨트롤러 실행에만 초점을 두고, 뷰가 없어보이는 `System.out.println("hello from Winter Framework!")`로 구조가 구현되어 있었다.

### 🔵 After

Controller 실행 후 `ViewResolver`를 통해 "hello" → "/views/hello.html"로 변환한 경로를 로그로 출력. 다음 단계에서 View 객체 구조로 확장 계획.

---

## 파일 구조 변화

```
winter/
├── dispatcher/
│   └── Dispatcher.java
└── view/
    ├── ViewResolver.java         // 인터페이스
    └── SimpleViewResolver.java   // 구현체
```

---

## ViewResolver 인터페이스

```java
public interface ViewResolver {
    String resolveViewName(String viewName);
}
```

> Dispatcher가 뷰 이름을 전달하면, 시작 경로로 변환해 보내고 결과를 반환한다.

---

## SimpleViewResolver 구현체

```java
public class SimpleViewResolver implements ViewResolver {
    private final String prefix = "/views/";
    private final String suffix = ".html";

    @Override
    public String resolveViewName(String viewName) {
        return prefix + viewName + suffix;
    }
}
```

> 가장 기본적인 prefix + suffix 구조의 경로 조합 로지가 포함됨.
> 후일에\uub294 .jsp, .json 차이 출력을 대응하는 구조로 확장 가능.

---

## Dispatcher 수정 보감

```java
ViewResolver viewResolver = new SimpleViewResolver();
String viewPath = viewResolver.resolveViewName("hello");
System.out.println("뷰 출력 경로: " + viewPath);
```

> 현재는 controller가 뷰 이름을 반환하지 않으며, 테스트 목적으로 "hello"를 포함.

---

## 🌟 효과 및 다음 단계

| 효과     | 설명                                                      |
| ------ | ------------------------------------------------------- |
| 체계 분리  | Dispatcher는 프레임워크 돌보기에 중요한 부록만 차지, ViewResolver가 변환을 차지 |
| 확장성 확립 | 다양한 View 전략(예: .jsp, .json)을 업그레이드 할 수 있는 경로로 확장 가능     |
| 다음 단계  | View 인터페이스, View\.render() 구조 구현 계획                     |
