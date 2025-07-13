
---

## 📚 문서 기록 기준

각 기능은 브랜치 단위로 구현되며, 각 브랜치마다 1:1 대응되는 설명 문서를 `docs/`에 작성합니다.

| 브랜치명                           | 문서 파일명                                | 내용                             |
| ---------------------------------- | ------------------------------------------ | -------------------------------- |
| `feature/dispatcher`              | 1-DISPATCHER.md                            | Dispatcher 기본 흐름             |
| `feature/handler-mapping`         | 2-HANDLER-MAPPING.md                       | URL → 핸들러 매핑 분리           |
| `feature/controller-interface`    | 3-CONTROLLER.md                            | Controller 공통 실행 구조        |
| `feature/handler-adapter`         | 4-HANDLER-ADAPTER.md                       | 어댑터 실행 전략 구조            |
| `feature/dispatcher-summary`      | 5-DISPATCHER-SUMMARY.md                    | Dispatcher 흐름 리팩토링 정리     |
| `feature/view-resolver`           | 6-VIEWRESOLVER.md                          | 뷰 경로 변환 전략 구조           |
| `feature/view-object`             | 7-VIEW.md                                  | View 객체화 및 render 분리       |
| `feature/model-and-view`          | 8-MODEL-AND-VIEW.md                        | Controller 반환값 구조 개선       |
| `feature/request-response`        | 9-REQUEST-RESPONSE.md                      | 요청/응답 추상화 구조 설계       |
| `feature/view-summary`           | 10-VIEW-SUMMARY.md                         | 6~9단계 흐름 및 역할 요약 정리    |
| `feature/model-and-view-binding` | 11-MODEL-AND-VIEW-BINDING-IN-HTML.md       | HTML 파일에 Model 동적 렌더링 구현 |

---

## 🚧 향후 구현 예정 로드맵

| 단계   | 브랜치 제안명                      | 목표 내용                                                                 |
| ------ | ---------------------------------- | -------------------------------------------------------------------------- |
| 12단계 | `feature/view-html-response`      | HTML 렌더링 결과를 실제 HttpResponse 객체로 전송 (`System.out` → 스트림) |
| 13단계 | `feature/template-parser-module`  | 템플릿 파싱 로직을 별도 모듈화 (반복문, 조건문 등 확장성 고려)             |
| 14단계 | `feature/request-param-parsing`   | Request에서 쿼리 파라미터 추출 및 컨트롤러 전달                           |
| 15단계 | `feature/http-method-support`     | GET/POST 등 HTTP 메서드별 요청 처리                                       |
| 16단계 | `feature/static-resource-support` | 정적 리소스(css, js, image) 처리                                          |
| 17단계 | `feature/controller-advice`       | 전역 예외 처리 및 응답 변환 구조 설계                                     |
| 18단계 | `feature/interceptor`             | 요청 전/후 로직을 처리하는 인터셉터 구조 설계                              |
| 19단계 | `feature/json-response-support`   | JSON 응답을 위한 View 구현 및 Content-Type 분기 처리                       |
| 20단계 | `feature/annotation-based-mvc`    | `@Controller`, `@RequestMapping` 등 어노테이션 기반 MVC 설계               |
| 21단계 | `feature/parameter-binding`       | 자동 파라미터 바인딩(`@RequestParam`, `@ModelAttribute`)                  |
| 22단계 | `feature/exception-resolver`      | 예외 → View 혹은 JSON으로 매핑하는 Resolver 구조                           |
| 23단계 | `feature/file-upload`             | Multipart 요청 처리 및 파일 업로드 기능                                    |
| 24단계 | `feature/session-management`      | 세션 및 쿠키 관리 기능 구현                                               |
| 25단계 | `feature/view-engine-integration`| Thymeleaf, Mustache, JSP 등 외부 템플릿 엔진 통합                          |

---

## 🧠 핵심 철학

Winter는 단순한 모방이 아닌 **구조적 이해**와 **프레임워크 설계 경험**을 목표로 합니다.  
각 단계는 작고 명확한 책임을 가진 컴포넌트를 도입하며, SRP 원칙에 따라 역할 분리를 지향합니다.  
궁극적으로 Spring MVC의 동작 흐름을 체득하고, 자신만의 웹 프레임워크를 설계할 수 있는 기초를 다집니다.
