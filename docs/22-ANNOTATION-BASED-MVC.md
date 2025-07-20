# 📘 어노테이션 기반 MVC (22단계)

## 🎯 목표

기존 `Controller` 인터페이스 방식에 더해 **어노테이션 기반 MVC 구조**를 도입하여 `@Controller`, `@RequestMapping` 등을 사용한 현대적인 웹 개발 방식을 지원합니다.

---

## 🔧 구현 내용

### ✅ 1. 핵심 어노테이션 정의

#### @Controller 어노테이션
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
```
- 클래스 레벨에 적용
- 해당 클래스가 웹 요청을 처리하는 컨트롤러임을 표시

#### @RequestMapping 어노테이션
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();           // URL 경로
    String method() default ""; // HTTP 메서드 (생략 시 모든 메서드 허용)
}
```
- 메서드 레벨에 적용
- URL 경로와 HTTP 메서드를 매핑

### ✅ 2. HandlerMethod 구조

**파일**: `winter.dispatcher.HandlerMethod`

```java
public class HandlerMethod {
    private final Object controller;    // 컨트롤러 인스턴스
    private final Method method;        // 실행할 메서드
    private final String path;          // 매핑된 URL 경로
    private final String httpMethod;    // 허용된 HTTP 메서드
}
```

**특징**:
- 컨트롤러 객체 + 메서드 조합으로 핸들러 표현
- 요청 매칭 로직 내장 (`matches()` 메서드)
- 기존 Controller 인터페이스와 구별되는 새로운 핸들러 타입

### ✅ 3. AnnotationHandlerAdapter

**파일**: `winter.dispatcher.AnnotationHandlerAdapter`

**지원하는 메서드 시그니처**:
```java
// 1. 파라미터 없음
public ModelAndView getProducts() { ... }

// 2. HttpRequest만
public ModelAndView getDetail(HttpRequest request) { ... }

// 3. HttpRequest + HttpResponse
public ModelAndView createProduct(HttpRequest request, HttpResponse response) { ... }
```

**동작 과정**:
1. `HandlerMethod`에서 컨트롤러 객체와 메서드 추출
2. 메서드 파라미터 타입 분석
3. 리플렉션을 통한 메서드 호출
4. 반환값을 `ModelAndView`로 검증

### ✅ 4. AnnotationHandlerMapping

**파일**: `winter.dispatcher.AnnotationHandlerMapping`

**핵심 기능**:
- `@Controller` 클래스 등록
- `@RequestMapping` 메서드 스캔
- URL + HTTP 메서드 조합으로 핸들러 매핑
- `HandlerMethod` 객체 생성 및 관리

**등록 과정**:
```java
public void registerController(Class<?> controllerClass) {
    // 1. @Controller 어노테이션 확인
    // 2. 컨트롤러 인스턴스 생성
    // 3. @RequestMapping 메서드 스캔
    // 4. HandlerMethod 생성 및 등록
}
```

### ✅ 5. CombinedHandlerMapping

**파일**: `winter.dispatcher.CombinedHandlerMapping`

**통합 전략**:
```java
public Object getHandler(String path, String httpMethod) {
    // 1. 어노테이션 기반 핸들러 우선 검색
    HandlerMethod annotationHandler = annotationHandlerMapping.getHandler(path, httpMethod);
    if (annotationHandler != null) {
        return annotationHandler;
    }

    // 2. 레거시 핸들러 검색 (경로만 매치)
    return legacyHandlerMapping.getHandler(path);
}
```

**특징**:
- 어노테이션 방식 우선, 레거시 방식 대체
- 기존 코드 완전 호환 유지
- 점진적 마이그레이션 지원

---

## 🧪 테스트 시나리오

### 어노테이션 컨트롤러 예시
```java
@Controller
public class ProductController {

    @RequestMapping(value = "/products", method = "GET")
    public ModelAndView getAllProducts() { ... }

    @RequestMapping(value = "/product/detail", method = "GET")
    public ModelAndView getProductDetail(HttpRequest request) { ... }

    @RequestMapping(value = "/products", method = "POST")
    public ModelAndView createProduct(HttpRequest request, HttpResponse response) { ... }

    @RequestMapping("/product/info")  // 모든 HTTP 메서드 허용
    public ModelAndView getProductInfo(HttpRequest request) { ... }
}
```

# 📘 어노테이션 기반 MVC (22단계)

## 🎯 목표

기존 `Controller` 인터페이스 방식에 더해 **어노테이션 기반 MVC 구조**를 도입하여 `@Controller`, `@RequestMapping` 등을 사용한 현대적인 웹 개발 방식을 지원합니다.

---

## 🔧 구현 내용

### ✅ 1. 핵심 어노테이션 정의

#### @Controller 어노테이션
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}
```
- 클래스 레벨에 적용
- 해당 클래스가 웹 요청을 처리하는 컨트롤러임을 표시

#### @RequestMapping 어노테이션
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)  
public @interface RequestMapping {
    String value();           // URL 경로
    String method() default ""; // HTTP 메서드 (생략 시 모든 메서드 허용)
}
```
- 메서드 레벨에 적용
- URL 경로와 HTTP 메서드를 매핑

### ✅ 2. HandlerMethod 구조

**파일**: `winter.dispatcher.HandlerMethod`

```java
public class HandlerMethod {
    private final Object controller;    // 컨트롤러 인스턴스
    private final Method method;        // 실행할 메서드
    private final String path;          // 매핑된 URL 경로
    private final String httpMethod;    // 허용된 HTTP 메서드
}
```

**특징**:
- 컨트롤러 객체 + 메서드 조합으로 핸들러 표현
- 요청 매칭 로직 내장 (`matches()` 메서드)
- 기존 Controller 인터페이스와 구별되는 새로운 핸들러 타입

### ✅ 3. AnnotationHandlerAdapter

**파일**: `winter.dispatcher.AnnotationHandlerAdapter`

**지원하는 메서드 시그니처**:
```java
// 1. 파라미터 없음
public ModelAndView getProducts() { ... }

// 2. HttpRequest만
public ModelAndView getDetail(HttpRequest request) { ... }

// 3. HttpRequest + HttpResponse
public ModelAndView createProduct(HttpRequest request, HttpResponse response) { ... }
```

**동작 과정**:
1. `HandlerMethod`에서 컨트롤러 객체와 메서드 추출
2. 메서드 파라미터 타입 분석 (`getParameterTypes()` 사용)
3. 리플렉션을 통한 메서드 호출
4. 반환값을 `ModelAndView`로 검증

### ✅ 4. AnnotationHandlerMapping

**파일**: `winter.dispatcher.AnnotationHandlerMapping`

**핵심 기능**:
- `@Controller` 클래스 등록
- `@RequestMapping` 메서드 스캔
- URL + HTTP 메서드 조합으로 핸들러 매핑
- `HandlerMethod` 객체 생성 및 관리

**등록 과정**:
```java
public void registerController(Class<?> controllerClass) {
    // 1. @Controller 어노테이션 확인
    // 2. 컨트롤러 인스턴스 생성
    // 3. @RequestMapping 메서드 스캔
    // 4. HandlerMethod 생성 및 등록
}
```

### ✅ 5. CombinedHandlerMapping (중앙 집중식 관리)

**파일**: `winter.dispatcher.CombinedHandlerMapping`

**통합 전략**:
```java
public Object getHandler(String path, String httpMethod) {
    // 1. 어노테이션 기반 핸들러 우선 검색
    HandlerMethod annotationHandler = annotationHandlerMapping.getHandler(path, httpMethod);
    if (annotationHandler != null) {
        return annotationHandler;
    }
    
    // 2. 레거시 핸들러 검색 (경로만 매치)
    return legacyHandlerMapping.getHandler(path);
}
```

**중앙 집중식 컨트롤러 관리**:
```java
private void registerAllControllers() {
    // 모든 어노테이션 컨트롤러를 한 곳에서 등록
    annotationHandlerMapping.registerController(ProductController.class);
    // 레거시 컨트롤러는 HandlerMapping 생성자에서 자동 등록
}
```

**특징**:
- 어노테이션 방식 우선, 레거시 방식 대체
- 기존 코드 완전 호환 유지
- 점진적 마이그레이션 지원
- 모든 컨트롤러 등록을 한 곳에서 관리

### ✅ 6. Dispatcher 아키텍처 개선

**파일**: `winter.dispatcher.Dispatcher`

**개선사항**:
- 컨트롤러 등록 책임을 `CombinedHandlerMapping`에 위임
- 오직 요청 처리 흐름만 담당
- 어노테이션과 레거시 핸들러 어댑터 체인 지원

```java
private final List<HandlerAdapter> handlerAdapters = List.of(
    new AnnotationHandlerAdapter(),    // 어노테이션 우선
    new ControllerHandlerAdapter()     // 레거시 대체
);
```

---

## 🧪 테스트 시나리오 및 결과

### 어노테이션 컨트롤러 예시
```java
@Controller
public class ProductController {
    
    @RequestMapping(value = "/products", method = "GET")
    public ModelAndView getAllProducts() { ... }
    
    @RequestMapping(value = "/product/detail", method = "GET")  
    public ModelAndView getProductDetail(HttpRequest request) { ... }
    
    @RequestMapping(value = "/products", method = "POST")
    public ModelAndView createProduct(HttpRequest request, HttpResponse response) { ... }
    
    @RequestMapping("/product/info")  // 모든 HTTP 메서드 허용
    public ModelAndView getProductInfo(HttpRequest request) { ... }
}
```

### 실제 테스트 결과

#### ✅ 테스트 1: GET /products (파라미터 없는 메서드)
- **요청**: `GET /products`
- **결과**: 200 OK, products.html 템플릿 렌더링
- **검증**: 어노테이션 기반 메서드 정상 호출

#### ✅ 테스트 2: GET /product/detail (HttpRequest 파라미터)
- **요청**: `GET /product/detail?id=12345&name=Winter Laptop`
- **결과**: 200 OK, product-detail.html 템플릿 렌더링
- **검증**: HttpRequest 파라미터 전달 및 쿼리 파라미터 처리

#### ✅ 테스트 3: POST /products (HttpRequest + HttpResponse 파라미터)
- **요청**: `POST /products?name=Winter Phone&price=599000`
- **결과**: 200 OK, product-created.html 템플릿 렌더링
- **검증**: 2개 파라미터 메서드 호출 및 상품 생성 로직

#### ✅ 테스트 4: POST /products (유효성 검증)
- **요청**: `POST /products` (name 파라미터 없음)
- **결과**: 400 Bad Request 처리 후 error.html 렌더링
- **검증**: 비즈니스 로직 내 유효성 검증 동작

#### ✅ 테스트 5: PUT /product/info (모든 메서드 허용)
- **요청**: `PUT /product/info`
- **결과**: 200 OK, product-info.html 렌더링
- **검증**: HTTP 메서드 지정 없을 때 모든 메서드 허용

#### ✅ 테스트 6: GET /products (JSON 응답)
- **요청**: `GET /products` + `Accept: application/json`
- **결과**: JSON 응답 정상 동작
- **검증**: 21단계 JSON 응답과 완전 호환

#### ✅ 테스트 7: 핸들러 우선순위
- **요청**: `GET /user` (레거시 경로)
- **결과**: 레거시 UserController 정상 동작
- **검증**: 어노테이션 우선, 레거시 대체 전략 정상

---

## 📊 등록된 핸들러 현황

### Annotation-based Handlers
```
HandlerMethod{path='/products', method='GET', handler=ProductController.getAllProducts()}
HandlerMethod{path='/products', method='POST', handler=ProductController.createProduct()}  
HandlerMethod{path='/product/detail', method='GET', handler=ProductController.getProductDetail()}
HandlerMethod{path='/product/info', method='ALL', handler=ProductController.getProductInfo()}
```

### Legacy Handlers
```
/hello → HelloController
/bye → ByeController  
/register → RegisterController
/user → UserController
/api → ApiController
```

---

## 📊 기술적 특징

### 어노테이션 스캔 방식
- **리플렉션 기반**: `Class.isAnnotationPresent()` 사용
- **메서드 스캔**: `Class.getDeclaredMethods()` 활용
- **어노테이션 정보 추출**: `Method.getAnnotation()` 활용

### 메서드 파라미터 매칭
```java
private ModelAndView invokeHandlerMethod(Object controller, Method method, 
                                       HttpRequest request, HttpResponse response) {
    Class<?>[] paramTypes = method.getParameterTypes(); // ✅ 수정됨
    
    switch (paramTypes.length) {
        case 0: return method.invoke(controller);
        case 1: return method.invoke(controller, request);
        case 2: return method.invoke(controller, request, response);
        default: throw new IllegalArgumentException("Unsupported method signature");
    }
}
```

### HTTP 메서드 매칭 전략
- **정확한 매치**: `@RequestMapping(method = "GET")` → GET 요청만 허용
- **전체 허용**: `@RequestMapping("/path")` → 모든 HTTP 메서드 허용
- **우선순위**: 구체적인 매핑이 일반적인 매핑보다 우선

---

## 🔄 기존 기능과의 호환성

### ✅ 완전 호환
- 기존 Controller 인터페이스 구현체들 정상 동작
- `/hello`, `/user`, `/register` 등 레거시 경로 유지
- JSON 응답 지원 (21단계) 계속 동작

### ✅ 핸들러 우선순위
1. **어노테이션 기반 핸들러** (URL + HTTP 메서드 매치)
2. **레거시 핸들러** (URL만 매치)

### ✅ 어댑터 체인
```java
private final List<HandlerAdapter> handlerAdapters = List.of(
    new AnnotationHandlerAdapter(),    // 어노테이션 우선
    new ControllerHandlerAdapter()     // 레거시 대체
);
```

---

## 💡 핵심 설계 원칙

### 1. Single Responsibility Principle
- **Dispatcher**: 오직 요청 처리 흐름만 담당
- **CombinedHandlerMapping**: 모든 핸들러 등록/관리 담당
- **AnnotationHandlerAdapter**: 어노테이션 핸들러 실행만 담당

### 2. Open/Closed Principle
- 기존 코드 수정 없이 새로운 어노테이션 방식 추가
- `CombinedHandlerMapping`으로 두 방식 통합

### 3. Dependency Inversion Principle
- `HandlerAdapter` 인터페이스를 통한 다형성
- 구체적인 핸들러 타입에 의존하지 않음

---

## 🚀 Spring MVC와의 비교

| 기능 | Winter 구현 | Spring MVC | 완성도 |
|------|-------------|------------|--------|
| @Controller | ✅ | @Controller | 95% |
| @RequestMapping | ✅ | @RequestMapping | 85% |
| 메서드 파라미터 | ✅ 3가지 시그니처 | 다양한 파라미터 타입 | 70% |
| HTTP 메서드 매핑 | ✅ | ✅ | 95% |
| 핸들러 어댑터 | ✅ | RequestMappingHandlerAdapter | 80% |
| 중앙 집중식 관리 | ✅ | ComponentScan | 85% |

---

## 🔮 활용 사례

### RESTful API 설계
```java
@Controller
public class UserApiController {
    
    @RequestMapping(value = "/api/users", method = "GET")
    public ModelAndView getAllUsers() { ... }
    
    @RequestMapping(value = "/api/users", method = "POST")  
    public ModelAndView createUser(HttpRequest request) { ... }
    
    @RequestMapping(value = "/api/users", method = "PUT")
    public ModelAndView updateUser(HttpRequest request) { ... }
}
```

### 하나의 컨트롤러, 여러 엔드포인트
```java
@Controller 
public class ShopController {
    
    @RequestMapping("/products")
    public ModelAndView products() { ... }
    
    @RequestMapping("/cart") 
    public ModelAndView cart() { ... }
    
    @RequestMapping("/checkout")
    public ModelAndView checkout() { ... }
}
```

### JSON API와 HTML 페이지 동시 지원
```java
@RequestMapping("/products")
public ModelAndView getProducts() {
    // Accept 헤더에 따라 자동으로 JSON 또는 HTML 응답
    return new ModelAndView("products");
}
```

---

## 🆚 기존 방식과의 차이점

### 기존 Controller 인터페이스 방식
```java
public class UserController implements Controller {
    @Override
    public ModelAndView handle(HttpRequest request, HttpResponse response) {
        // 하나의 메서드에서 모든 로직 처리
        String method = request.getMethod();
        if ("GET".equals(method)) { ... }
        else if ("POST".equals(method)) { ... }
    }
}
```

### 새로운 어노테이션 방식
```java
@Controller
public class UserController {
    
    @RequestMapping(value = "/users", method = "GET")
    public ModelAndView getUsers() { ... }      // GET 전용
    
    @RequestMapping(value = "/users", method = "POST")
    public ModelAndView createUser() { ... }    // POST 전용
}
```

**장점**:
- 메서드별 역할 분리
- 가독성 향상
- 테스트 용이성
- URL 매핑의 명확성

---

## 🎯 23단계 준비 완료

22단계 완료로 **어노테이션 기반 MVC의 기초**가 완벽하게 마련되었습니다:

- **@Controller**, **@RequestMapping** 어노테이션 시스템 ✅
- **리플렉션 기반 메서드 호출** ✅
- **레거시와 어노테이션 방식 병행 지원** ✅
- **JSON 응답과 완전 호환** ✅
- **중앙 집중식 핸들러 관리** ✅

다음 23단계에서는 `@RequestParam`, `@ModelAttribute` 등을 통한 **더 정교한 파라미터 바인딩**을 구현할 예정입니다.

---

## 📋 변경된 파일 목록

| 파일 | 상태 | 설명 |
|------|------|------|
| `winter.annotation.Controller` | 신규 | @Controller 어노테이션 |
| `winter.annotation.RequestMapping` | 신규 | @RequestMapping 어노테이션 |
| `winter.dispatcher.HandlerMethod` | 신규 | 어노테이션 핸들러 정보 클래스 |
| `winter.dispatcher.AnnotationHandlerAdapter` | 신규 | 어노테이션 핸들러 어댑터 |
| `winter.dispatcher.AnnotationHandlerMapping` | 신규 | 어노테이션 핸들러 매핑 |
| `winter.dispatcher.CombinedHandlerMapping` | 신규 | 통합 핸들러 매핑 (중앙 관리) |
| `winter.dispatcher.Dispatcher` | 수정 | 핸들러 등록 책임 위임, 요청 처리만 담당 |
| `winter.controller.ProductController` | 신규 | 어노테이션 테스트 컨트롤러 |
| `src/winter/templates/products.html` | 신규 | 상품 목록 템플릿 |
| `src/winter/templates/product-detail.html` | 신규 | 상품 상세 템플릿 |
| `src/winter/templates/product-created.html` | 신규 | 상품 생성 성공 템플릿 |
| `src/winter/templates/product-info.html` | 신규 | 상품 정보 템플릿 |
| `src/winter/templates/error.html` | 신규 | 에러 페이지 템플릿 |
| `winter.WinterMain` | 수정 | 어노테이션 MVC 테스트 추가 |