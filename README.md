# ❄ Winter: Minimal Web MVC Framework

`Winter`는 Spring MVC의 구조를 직접 구현하며 이해하는 학습용 가게 웹 프레임워크입니다.
DispatcherServlet, HandlerMapping, ViewResolver 등의 구성 요소를 단계별로 구현해내며, 각 컴포넌트의 역할과 협업 방식을 체등하는 것을 목표로 합니다.

---

## 함수 구현 단계별 문서

모든 기능은 브랜치 단위로 구현되며, 각 브랜치에 대응되는 설명 문서를 `docs/` 디렉토리에 작성합니다.

| 단계 | 브랜치명                              | 문서 파일명                                 | 설명                                 |
| -- | --------------------------------- | -------------------------------------- | ---------------------------------- |
| 1  | `feature/dispatcher`              | `1-DISPATCHER.md`                      | Dispatcher 기본 흐름 구현                |
| 2  | `feature/handler-mapping`         | `2-HANDLER-MAPPING.md`                 | URL → 핸들러 매핑 분리                    |
| 3  | `feature/controller-interface`    | `3-CONTROLLER.md`                      | Controller 공통 실행 구조 도입             |
| 4  | `feature/handler-adapter`         | `4-HANDLER-ADAPTER.md`                 | 어데퍼 실행 전략 구조 설계                    |
| 5  | `feature/dispatcher-summary`      | `5-DISPATCHER-SUMMARY.md`              | Dispatcher 흐름 리파트링 요조              |
| 6  | `feature/view-resolver`           | `6-VIEWRESOLVER.md`                    | 비웃 경로 → View 객체 변환 전략 도입           |
| 7  | `feature/view-object`             | `7-VIEW.md`                            | View 객체화 및 render 메서드 분리           |
| 8  | `feature/model-and-view`          | `8-MODEL-AND-VIEW.md`                  | Controller 반환 구조 → ModelAndView 개정 |
| 9  | `feature/request-response`        | `9-REQUEST-RESPONSE.md`                | HttpRequest/HttpResponse 추사화       |
| 10 | `feature/view-summary`            | `10-VIEW-SUMMARY.md`                   | View 관련 구조 개정 요조                   |
| 11 | `feature/model-and-view-binding`  | `11-MODEL-AND-VIEW-BINDING-IN-HTML.md` | HTML에서 `${key}` 형태로 Model 렌더링      |
| 12 | `feature/view-html-response`      | `12-VIEW-HTML-RENDERING.md`            | HTML 렌더링 결과를 실제 HTTP 응답 부문으로       |
| 13 | `feature/template-parser-module`  | `13-TEMPLATE-PARSER-MODULE.md`         | 템플릿 파서 모듈화: `${user.name}` 지원      |
| 14 | `feature/request-param-parsing`   | `14-REQUEST-PARSING.md`                | 쿼리 파라미터 → DTO 바인딩 처리               |
| 15 | `feature/http-method-support`     | `15-HTTP-METHOD.md`                    | HTTP Method(GET/POST 등) 분기 처리      |
| 16 | `feature/model-binding`           | `16-model-binding.md`                  | 요청 파라미터 → 중차 모델 객체 자동 바인딩          |
| 17 | `feature/static-resource-support` | `17-static-resource.md`                | 정적 리소스 처리(css, js 등)               |
| 18 | `feature/http-header-support`     | `18-HTTP-HEADER.md`                    | HTTP Request Header 구조 확장          |
| 19 | `feature/exception-resolver`      | `19-EXCEPTION-RESOLVER.md`             | 전역 예외 처리 구조 도입                     |

---

## 협업 로드맵

| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 20 | `feature/json-response-support`   | JSON 응답을 위한 View 분기 처리 (`Content-Type: application/json`) |
| 21 | `feature/annotation-based-mvc`    | `@Controller`, `@RequestMapping` 등 어널티션 기반 구조 설계          |
| 22 | `feature/parameter-binding`       | `@RequestParam`, `@ModelAttribute` 지원                     |
| 23 | `feature/file-upload`             | Multipart 파일 업로드 기능                                       |
| 24 | `feature/session-management`      | 세션, 쿠키 관리 기능 구현                                           |
| 25 | `feature/view-engine-integration` | Thymeleaf, Mustache, JSP 등의 외부 비어 엔진 통합                   |

---

## 특징: Winter 친학

Winter는 다음을 가지는 것을 목표로 합니다.

* 기능 구현 보다 **구조적 이해**와 **프레임워크 개발 방식** 체등
* 단일 체기(SRP) 기반의 컴포넌트 구조를 설계
* Dispatcher, Adapter, ViewResolver, ExceptionResolver 등의 여러 요소가 **uc720기적으로 협업**하는 구조 이해
* Spring MVC 데이터 활용 목적의 **자체 프레임워크 설계 역량** 기본 구성

---
