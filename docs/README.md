# ❄️ Winter Framework

Winter는 스프링 MVC의 핵심 개념을 직접 구현하며, 프레임워크의 요청 처리 흐름을 구조적으로 학습하기 위한 자바 기반 프로젝트입니다. Dispatcher → HandlerMapping → Controller → HandlerAdapter → ViewResolver → View 구조를 단계적으로 구현하고 문서화합니다.

---

## 🛠️ 개발 목표

* DispatcherServlet과 유사한 구조를 직접 구현해보기
* 역할별 책임 분리(SRP)를 통한 객체지향 설계 연습
* 실행 전략 패턴(HandlerAdapter), 라우팅 매핑(HandlerMapping) 등 MVC 핵심 개념 학습
* 논리 뷰 이름 처리, 뷰 객체 추상화 등 View 처리 구조 구현

---

## ✅ 기능별 구현 현황

| 단계  | 기능               | 설명                           |
| --- | ---------------- | ---------------------------- |
| 1단계 | Dispatcher       | 요청 URL에 따라 컨트롤러 실행 흐름 구성     |
| 2단계 | HandlerMapping   | URL → 핸들러 객체 매핑 책임 분리        |
| 3단계 | Controller 인터페이스 | 모든 컨트롤러에 공통 실행 메서드 적용        |
| 4단계 | HandlerAdapter   | 핸들러 실행 전략 분리 및 Adapter 구조 도입 |
| 5단계 | ViewResolver     | 논리 뷰 이름 → 물리 경로 문자열 변환       |
| 6단계 | View 객체          | View 추상화 및 render() 분리 완료    |

---

## 📦 디렉토리 구조 (일부)

```
winter/
├── dispatcher/
│   ├── Dispatcher.java
│   ├── HandlerMapping.java
│   ├── ControllerHandlerAdapter.java
│   ├── HelloController.java
│   └── ByeController.java
├── view/
│   ├── View.java
│   ├── ViewResolver.java
│   ├── InternalResourceView.java
│   └── SimpleViewResolver.java
├── docs/
│   ├── 0-ROLE.md
│   ├── 1-DISPATCHER.md
│   ├── 2-HANDLER-MAPPING.md
│   ├── 3-CONTROLLER.md
│   ├── 4-HANDLER-ADAPTER.md
│   ├── 6-VIEWRESOLVER.md
│   └── 7-VIEW.md
└── README.md
```

---

## 📚 문서 기록 기준

각 기능은 브랜치 단위로 구현되며, 각 브랜치마다 1:1 대응되는 설명 문서를 `docs/`에 작성합니다.

| 브랜치명                           | 문서 파일명               | 내용                   |
| ------------------------------ | -------------------- | -------------------- |
| `feature/dispatcher`           | 1-DISPATCHER.md      | Dispatcher 기본 흐름     |
| `feature/handler-mapping`      | 2-HANDLER-MAPPING.md | URL → 핸들러 매핑 분리      |
| `feature/controller-interface` | 3-CONTROLLER.md      | Controller 공통 실행 구조  |
| `feature/handler-adapter`      | 4-HANDLER-ADAPTER.md | 어댑터 실행 전략 구조         |
| `feature/view-resolver`        | 6-VIEWRESOLVER.md    | 뷰 경로 변환 전략 구조        |
| `feature/view-object`          | 7-VIEW\.md           | View 객체화 및 render 분리 |

---

## 🚧 향후 구현 예정

* `feature/model-and-view`: 컨트롤러에서 viewName 또는 ModelAndView 반환
* `feature/model-support`: 렌더링 시 model 데이터 전달 구조 설계
* `feature/request-response`: 요청/응답 객체 전달 구조 설계
* 테스트 프레임워크 및 통합 흐름 점검
