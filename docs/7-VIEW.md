# 📘 7-VIEW\.md

## 👀 View 객체는?

Controller 실행 후 Dispatcher가 복잡한 뷰 경로 차지 바로 출력하는 것이 아니라,
"핸들러 실행 - 뷰 찾기 - 뷰 보여주기" 라는 프레임워크 보통 활동 방식에서
뷰 변환 및 보여주기가 구현되는 것을 가지는 것이 View 객체다.

---

## ✅ 현재 진행 되어날 것

1. `View` 인터페이스 등장

    * `void render()` 메서드를 가지고, 최소 복잡한 출력을 수행

2. `InternalResourceView` 구현체 생성

    * path를 다수적으로 가지고, `render()`는 현재은 로그에 "보여주기" 수준

3. `ViewResolver` 인터페이스는 `String` 반환자에서 `View` 반환자로 개조

4. `SimpleViewResolver` 변경

    * prefix + suffix 을 조합해 `InternalResourceView` 객체를 반환

5. `Dispatcher`가 이전과 다른 점:

    * viewName을 ViewResolver에 전달 → View 발행 → `render()` 호출

---

## 파일 구조 변화

```
winter/
├── dispatcher/
│   └── Dispatcher.java
└── view/
    ├── View.java                  // 최소 View 구조
    ├── InternalResourceView.java // View 구현체
    ├── ViewResolver.java         // View 변환 전략 인터페이스
    └── SimpleViewResolver.java   // ViewResolver 구현체
```

---

## 효과 정리

| 효과      | 설명                                                                                  |
| ------- | ----------------------------------------------------------------------------------- |
| 체계 분리   | Dispatcher가 뷰 차지가 아닌, ViewResolver 가 변환. View 가 출력 차지.                              |
| 확장성     | View, ViewResolver 를 통해 다양한 공통 구조로 확장 가능                                            |
| 다음 프로젝트 | Controller가 viewName(String) 반환 → ModelAndView, 요청 객체 전달과 함께 render가 수행되는 구조로 확장 예정 |

---

## ✈️ 다음 단계

`feature/model-and-view` 버닝에서 Controller가 viewName을 반환하는 구조와
`Model` 전달과 바위기 추가를 수행할 계획
