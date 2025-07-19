# ❄ Winter: Minimal Web MVC Framework

`Winter`는 Spring MVC의 구조를 직접 구현하며 이해하는 학습용 경량 웹 프레임워크입니다.
DispatcherServlet, HandlerMapping, ViewResolver 등의 구성 요소를 단계별로 구현해내며, 각 컴포넌트의 역할과 협업 방식을 체득하는 것을 목표로 합니다.

---

## 함수 구현 단계별 문서

모든 기능은 브랜치 단위로 구현되며, 각 브랜치에 대응되는 설명 문서를 `docs/` 디렉토리에 작성합니다.

| 단계 | 브랜치명                              | 문서 파일명                                 | 설명                                 |
| -- | --------------------------------- | -------------------------------------- | ---------------------------------- |
| 1  | `feature/dispatcher`              | `1-DISPATCHER.md`                      | Dispatcher 기본 흐름 구현                |
| 2  | `feature/handler-mapping`         | `2-HANDLER-MAPPING.md`                 | URL → 핸들러 매핑 분리                    |
| 3  | `feature/controller-interface`    | `3-CONTROLLER.md`                      | Controller 공통 실행 구조 도입             |
| 4  | `feature/handler-adapter`         | `4-HANDLER-ADAPTER.md`                 | 어댑터 실행 전략 구조 설계                    |
| 5  | `feature/dispatcher-summary`      | `5-DISPATCHER-SUMMARY.md`              | Dispatcher 흐름 리팩토링 정리              |
| 6  | `feature/view-resolver`           | `6-VIEWRESOLVER.md`                    | 뷰 경로 → View 객체 변환 전략 도입           |
| 7  | `feature/view-object`             | `7-VIEW.md`                            | View 객체화 및 render 메서드 분리           |
| 8  | `feature/model-and-view`          | `8-MODEL-AND-VIEW.md`                  | Controller 반환 구조 → ModelAndView 정리 |
| 9  | `feature/request-response`        | `9-REQUEST-RESPONSE.md`                | HttpRequest/HttpResponse 추상화       |
| 10 | `feature/view-summary`            | `10-VIEW-SUMMARY.md`                   | View 관련 구조 정리 요약                   |
| 11 | `feature/model-and-view-binding`  | `11-MODEL-AND-VIEW-BINDING-IN-HTML.md` | HTML에서 `${key}` 형태로 Model 렌더링      |
| 12 | `feature/view-html-response`      | `12-VIEW-HTML-RENDERING.md`            | HTML 렌더링 결과를 실제 HTTP 응답 본문으로       |
| 13 | `feature/template-parser-module`  | `13-TEMPLATE-PARSER-MODULE.md`         | 템플릿 파서 모듈화: `${user.name}` 지원      |
| 14 | `feature/request-param-parsing`   | `14-REQUEST-PARSING.md`                | 쿼리 파라미터 → DTO 바인딩 처리               |
| 15 | `feature/http-method-support`     | `15-HTTP-METHOD.md`                    | HTTP Method(GET/POST 등) 분기 처리      |
| 16 | `feature/model-binding`           | `16-model-binding.md`                  | 요청 파라미터 → 모델 객체 자동 바인딩             |
| 17 | `feature/static-resource-support` | `17-static-resource.md`                | 정적 리소스 처리(css, js 등)               |
| 18 | `feature/http-header-support`     | `18-HTTP-HEADER.md`                    | HTTP Request Header 구조 확장          |
| 19 | `feature/exception-resolver`      | `19-EXCEPTION-RESOLVER.md`             | 전역 예외 처리 구조 도입                     |
| 20 | `feature/model-view-summary`      | `20-MODEL-VIEW-SUMMARY.md`             | 11-19 챕터 Model & View 구조 종합 정리     |

---

## 향후 로드맵

### Phase 1: Web MVC 완성 (21-30)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 21 | `feature/json-response-support`   | JSON 응답을 위한 View 분기 처리 (`Content-Type: application/json`) |
| 22 | `feature/annotation-based-mvc`    | `@Controller`, `@RequestMapping` 등 어노테이션 기반 구조 설계          |
| 23 | `feature/parameter-binding`       | `@RequestParam`, `@ModelAttribute` 지원                     |
| 24 | `feature/file-upload`             | Multipart 파일 업로드 기능                                       |
| 25 | `feature/session-management`      | 세션, 쿠키 관리 기능 구현                                           |
| 26 | `feature/view-engine-integration` | Thymeleaf, Mustache, JSP 등의 외부 뷰 엔진 통합                   |
| 27 | `feature/interceptor-chain`       | HandlerInterceptor 체인 구조 구현                               |
| 28 | `feature/validation-support`      | Bean Validation API 통합 (`@Valid`, `@NotNull` 등)           |
| 29 | `feature/response-entity`         | ResponseEntity와 HTTP 상태 코드 제어                            |
| 30 | `feature/rest-controller`         | RESTful API를 위한 @RestController 구조                       |

### Phase 2: IoC Container & DI (31-45)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 31 | `feature/bean-definition`         | Bean 정의와 메타데이터 관리 구조                                      |
| 32 | `feature/bean-factory`            | BeanFactory 인터페이스와 기본 구현                                  |
| 33 | `feature/application-context`     | ApplicationContext 확장과 추가 기능                              |
| 34 | `feature/dependency-injection`    | Constructor/Setter 기반 의존성 주입                              |
| 35 | `feature/component-scan`          | @Component, @Service, @Repository 스캔                     |
| 36 | `feature/autowired-annotation`    | @Autowired 어노테이션 기반 자동 주입                               |
| 37 | `feature/bean-lifecycle`          | InitializingBean, DisposableBean 라이프사이클                  |
| 38 | `feature/configuration-class`     | @Configuration, @Bean Java Config                        |
| 39 | `feature/property-injection`      | @Value, Environment를 통한 프로퍼티 주입                          |
| 40 | `feature/profiles-conditional`    | @Profile, @Conditional 조건부 빈 등록                          |
| 41 | `feature/bean-post-processor`     | BeanPostProcessor를 통한 빈 후처리                              |
| 42 | `feature/proxy-creation`          | 프록시 기반 빈 생성과 AOP 준비                                     |
| 43 | `feature/circular-dependency`     | 순환 의존성 해결 메커니즘                                           |
| 44 | `feature/factory-bean`            | FactoryBean 인터페이스와 복잡한 빈 생성                             |
| 45 | `feature/container-extension`     | ApplicationListener, BeanFactoryPostProcessor             |

### Phase 3: AOP (Aspect-Oriented Programming) (46-55)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 46 | `feature/proxy-pattern`           | 프록시 패턴 기반 AOP 기초 구조                                     |
| 47 | `feature/jdk-dynamic-proxy`       | JDK Dynamic Proxy 구현                                     |
| 48 | `feature/cglib-proxy`             | CGLIB 기반 클래스 프록시 구현                                     |
| 49 | `feature/pointcut-expression`     | Pointcut 표현식 파싱과 매칭                                      |
| 50 | `feature/advice-types`            | Before, After, Around Advice 구현                          |
| 51 | `feature/aspect-annotation`       | @Aspect, @Before, @After 어노테이션 지원                       |
| 52 | `feature/joinpoint-context`       | JoinPoint와 ProceedingJoinPoint 구현                        |
| 53 | `feature/aop-auto-proxy`          | 자동 프록시 생성과 어드바이저 체인                                     |
| 54 | `feature/transaction-aspect`      | 트랜잭션을 위한 AOP 적용 예제                                      |
| 55 | `feature/aop-integration`         | IoC Container와 AOP 통합                                    |

### Phase 4: Data Access & Transaction (56-70)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 56 | `feature/transaction-manager`     | PlatformTransactionManager 인터페이스                         |
| 57 | `feature/transaction-definition`  | TransactionDefinition과 격리 수준                             |
| 58 | `feature/transaction-template`    | TransactionTemplate 프로그래밍 방식                             |
| 59 | `feature/declarative-transaction` | @Transactional 선언적 트랜잭션                                 |
| 60 | `feature/jdbc-template`           | JdbcTemplate과 반복 코드 제거                                   |
| 61 | `feature/connection-pooling`      | DataSource와 커넥션 풀 관리                                     |
| 62 | `feature/orm-integration`         | Hibernate/JPA 통합 기초                                      |
| 63 | `feature/repository-pattern`      | Repository 패턴과 Data Access Object                        |
| 64 | `feature/cache-abstraction`       | 캐시 추상화 레이어 (@Cacheable, @CacheEvict)                    |
| 65 | `feature/jpa-repositories`        | Spring Data JPA 스타일 Repository                           |
| 66 | `feature/query-methods`           | 메서드 이름 기반 쿼리 생성                                         |
| 67 | `feature/database-migration`      | 스키마 마이그레이션과 초기화                                        |
| 68 | `feature/multiple-datasource`     | 다중 데이터소스 관리                                            |
| 69 | `feature/transaction-propagation` | 트랜잭션 전파 레벨과 중첩 트랜잭션                                   |
| 70 | `feature/data-access-exception`   | 데이터 접근 예외 추상화                                          |

### Phase 5: Security & Authentication (71-85)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 71 | `feature/security-context`       | SecurityContext와 Authentication 객체                       |
| 72 | `feature/authentication-manager`  | AuthenticationManager와 Provider 체인                       |
| 73 | `feature/user-details-service`    | UserDetailsService와 사용자 정보 로딩                           |
| 74 | `feature/password-encoder`        | PasswordEncoder와 암호화                                     |
| 75 | `feature/security-filter-chain`   | Filter 기반 보안 체인 구현                                      |
| 76 | `feature/method-security`         | @PreAuthorize, @PostAuthorize 메서드 보안                    |
| 77 | `feature/session-security`        | 세션 관리와 동시 세션 제어                                         |
| 78 | `feature/csrf-protection`         | CSRF 토큰 생성과 검증                                          |
| 79 | `feature/oauth2-client`           | OAuth2 클라이언트 구현                                         |
| 80 | `feature/jwt-token`               | JWT 토큰 기반 인증                                            |
| 81 | `feature/remember-me`             | Remember-Me 인증 구현                                        |
| 82 | `feature/access-control`          | 역할 기반 접근 제어 (RBAC)                                      |
| 83 | `feature/security-events`         | 보안 이벤트와 감사 로그                                          |
| 84 | `feature/security-headers`        | 보안 헤더 자동 추가                                            |
| 85 | `feature/security-test`           | 보안 테스트 지원 도구                                           |

### Phase 6: Enterprise Integration (86-100)
| 단계 | 브랜치 제안명                           | 목표 내용                                                     |
| -- | --------------------------------- | --------------------------------------------------------- |
| 86 | `feature/messaging-support`      | JMS 메시징 지원                                              |
| 87 | `feature/async-processing`        | @Async 비동기 처리                                           |
| 88 | `feature/scheduling`              | @Scheduled 스케줄링 지원                                       |
| 89 | `feature/event-publishing`        | ApplicationEvent 발행과 처리                                 |
| 90 | `feature/jmx-support`             | JMX를 통한 모니터링                                           |
| 91 | `feature/mail-support`            | 이메일 발송 추상화                                             |
| 92 | `feature/web-socket`              | WebSocket 지원                                             |
| 93 | `feature/reactive-streams`        | Reactive Programming 기초                                  |
| 94 | `feature/microservice-support`    | Cloud Native 기능 (Config, Discovery)                     |
| 95 | `feature/metrics-monitoring`      | 메트릭 수집과 모니터링                                           |
| 96 | `feature/health-check`            | 헬스 체크와 Actuator 엔드포인트                                  |
| 97 | `feature/testing-support`         | @SpringBootTest, TestContext 프레임워크                     |
| 98 | `feature/dev-tools`               | 개발 도구와 hot-reload                                       |
| 99 | `feature/native-compilation`      | GraalVM Native Image 지원                                 |
| 100| `feature/spring-ecosystem`       | 전체 Spring 생태계 통합과 마이그레이션                             |

---

## 특징: Winter 철학

Winter는 다음을 갖는 것을 목표로 합니다.

* 기능 구현 보다 **구조적 이해**와 **프레임워크 개발 방식** 체득
* 단일 책임(SRP) 기반의 컴포넌트 구조를 설계
* Dispatcher, Adapter, ViewResolver, ExceptionResolver 등의 여러 요소가 **유기적으로 협업**하는 구조 이해
* Spring MVC 데이터 활용 목적의 **자체 프레임워크 설계 역량** 기본 구성

---