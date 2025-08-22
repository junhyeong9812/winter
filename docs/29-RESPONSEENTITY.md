# 🎯 29단계: ResponseEntity와 HTTP 상태 코드 제어

> **Winter Framework의 29번째 진화** - Spring MVC 수준의 RESTful API 개발 지원

Winter 프레임워크가 드디어 Spring MVC와 동등한 수준의 HTTP 응답 제어 기능을 갖추었습니다! 기존 28단계까지의 모든 기능을 완벽히 유지하면서, ResponseEntity를 통한 정교한 HTTP 응답 제어가 가능해졌습니다.

## 📋 목차

- [✨ 새로운 기능들](#-새로운-기능들)
- [🔄 하위 호환성](#-하위-호환성)
- [📁 구현된 파일들](#-구현된-파일들)
- [🚀 사용법](#-사용법)
- [🧪 테스트 실행](#-테스트-실행)
- [📊 기능 매트릭스](#-기능-매트릭스)
- [🎨 설계 원칙](#-설계-원칙)

## ✨ 새로운 기능들

### 🎯 ResponseEntity 완전 지원
```java
@Controller
public class ApiController {
    // HTTP 상태 코드와 헤더를 명시적으로 제어
    @RequestMapping("/api/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(users)
                .withHeader("X-Total-Count", "10")
                .withHeader("Cache-Control", "max-age=300");
    }
    
    // 생성 시 201 Created + Location 헤더
    @RequestMapping("/api/users/create")
    public ResponseEntity<User> createUser(@Valid @ModelAttribute UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest(null)
                    .withHeader("X-Error-Code", "VALIDATION_FAILED");
        }
        
        User newUser = userService.create(form);
        return ResponseEntity.created(newUser)
                .withHeader("Location", "/api/users/" + newUser.getId());
    }
}
```

### 🏷️ 표준 HTTP 상태 코드
```java
// 다양한 HTTP 상태 코드 지원
return ResponseEntity.ok(data);                    // 200 OK
return ResponseEntity.created(user);               // 201 Created  
return ResponseEntity.noContent();                 // 204 No Content
return ResponseEntity.badRequest(errors);          // 400 Bad Request
return ResponseEntity.notFound();                  // 404 Not Found
return ResponseEntity.conflict(message);           // 409 Conflict
return ResponseEntity.internalServerError(error);  // 500 Internal Server Error

// 커스텀 상태 코드
return ResponseEntity.withStatus(HttpStatus.ACCEPTED, data);
```

### 🔄 JSON 자동 변환
```java
@RequestMapping("/api/products")
public ResponseEntity<List<Product>> getProducts() {
    // 객체가 자동으로 JSON으로 변환됨
    List<Product> products = productService.findAll();
    return ResponseEntity.ok(products);
    // → Content-Type: application/json으로 자동 설정
}
```

### 🔗 기존 검증 기능과의 완벽한 통합
```java
@RequestMapping("/api/users/validate")
public ResponseEntity<User> validateAndCreate(
        @Valid @ModelAttribute UserForm form, 
        BindingResult result) {
    
    // 28단계 검증 기능이 29단계 ResponseEntity와 완벽 통합!
    if (result.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.badRequest(errors)
                .withHeader("X-Validation-Errors", String.valueOf(result.getErrorCount()));
    }
    
    User user = userService.create(form);
    return ResponseEntity.created(user);
}
```

## 🔄 하위 호환성

### ✅ 기존 코드는 수정 없이 그대로 동작
```java
// 28단계 이전 코드 - 전혀 수정 없이 계속 사용 가능
@Controller  
public class LegacyController {
    @RequestMapping("/users")
    public ModelAndView getUsers(@Valid @ModelAttribute UserForm form, BindingResult result) {
        // 모든 기존 기능이 정확히 동일하게 동작
        if (result.hasErrors()) {
            return new ModelAndView("error-page");
        }
        return new ModelAndView("user-list", model);
    }
}
```

### 🤝 혼재 사용 완전 지원
```java
@Controller
public class HybridController {
    // 기존 방식 - HTML 뷰 렌더링
    @RequestMapping("/users/page")
    public ModelAndView getUserPage() {
        return new ModelAndView("users", model);
    }
    
    // 새로운 방식 - JSON API
    @RequestMapping("/api/users")  
    public ResponseEntity<List<User>> getUsersApi() {
        return ResponseEntity.ok(users);
    }
}
```

## 📁 구현된 파일들

### 🆕 새로 추가된 파일들

#### **winter/http/**
- **`ResponseEntity.java`** - HTTP 응답 엔터티 (상태코드, 헤더, 본문 제어)
- **`HttpStatus.java`** - 표준 HTTP 상태 코드 열거형 (RFC 7231 준수)
- **`HttpResponse.java`** - 인터페이스로 추상화 (29단계)
- **`StandardHttpResponse.java`** - 기존 HttpResponse 구현체

#### **winter/controller/**
- **`ResponseEntityController.java`** - 실제 사용 예제 컨트롤러

#### **winter/view/**
- **`EnhancedViewResolver.java`** - JSON 뷰 지원하는 확장된 뷰 리졸버

#### **winter/util/**
- **`JsonSerializer.java`** - 간단한 JSON 직렬화 유틸리티

#### **winter/**
- **`ResponseEntityTestMain.java`** - 완전한 통합 테스트

### 🔧 수정된 기존 파일들

#### **winter/dispatcher/**
- **`AnnotationHandlerAdapter.java`** - ResponseEntity 지원 추가 (28단계 모든 기능 유지)
- **`Dispatcher.java`** - EnhancedViewResolver 사용 (27단계 모든 기능 유지)

## 🚀 사용법

### 1️⃣ 기본 ResponseEntity 사용
```java
@Controller
public class BasicApiController {
    
    @RequestMapping("/api/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, Winter Framework!");
    }
    
    @RequestMapping("/api/users/{id}")
    public ResponseEntity<User> getUser(@RequestParam("id") String id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound();
        }
        return ResponseEntity.ok(user);
    }
}
```

### 2️⃣ 헤더와 상태 코드 제어
```java
@Controller
public class AdvancedApiController {
    
    @RequestMapping("/api/users/create")
    public ResponseEntity<User> createUser(@ModelAttribute UserForm form) {
        User newUser = userService.create(form);
        
        return ResponseEntity.created(newUser)
                .withHeader("Location", "/api/users/" + newUser.getId())
                .withHeader("X-Created-By", "Winter Framework")
                .withHeader("Cache-Control", "no-cache");
    }
    
    @RequestMapping("/api/users/search")  
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        List<User> results = userService.search(query);
        
        return ResponseEntity.ok(results)
                .withHeader("X-Search-Results", String.valueOf(results.size()))
                .withHeader("X-Search-Query", query);
    }
}
```

### 3️⃣ 검증과 함께 사용
```java
@Controller
public class ValidationApiController {
    
    @RequestMapping("/api/users/validate")
    public ResponseEntity<User> createWithValidation(
            @Valid @ModelAttribute UserForm form,
            BindingResult result) {
        
        // 28단계 검증 기능과 완벽 통합
        if (result.hasErrors()) {
            return ResponseEntity.badRequest(null)
                    .withHeader("X-Error-Count", String.valueOf(result.getErrorCount()));
        }
        
        User user = userService.create(form);
        return ResponseEntity.created(user);
    }
}
```

### 4️⃣ 에러 처리
```java
@Controller  
public class ErrorHandlingController {
    
    @RequestMapping("/api/error-demo")
    public ResponseEntity<Map<String, String>> errorDemo(@RequestParam("type") String type) {
        Map<String, String> response = new HashMap<>();
        
        switch (type) {
            case "not-found":
                response.put("error", "Resource not found");
                return ResponseEntity.notFound()
                        .withHeader("X-Error-Type", "RESOURCE_NOT_FOUND");
                        
            case "conflict":
                response.put("error", "Resource already exists");  
                return ResponseEntity.conflict(response)
                        .withHeader("X-Error-Type", "DUPLICATE_RESOURCE");
                        
            case "validation":
                response.put("error", "Invalid input data");
                return ResponseEntity.badRequest(response)
                        .withHeader("X-Error-Type", "VALIDATION_ERROR");
                        
            default:
                response.put("success", "Everything is fine");
                return ResponseEntity.ok(response);
        }
    }
}
```

## 🧪 테스트 실행

### ResponseEntityTestMain 실행
```bash
# 프로젝트 루트에서 실행
java winter.ResponseEntityTestMain
```

### 테스트 시나리오
```java
public class ResponseEntityTestMain {
    public static void main(String[] args) {
        // 1. 기존 방식 호환성 테스트
        testLegacyCompatibility(dispatcher);
        
        // 2. ResponseEntity 기능 테스트  
        testResponseEntityFeatures(dispatcher);
        
        // 3. 검증 + ResponseEntity 통합 테스트
        testValidationWithResponseEntity(dispatcher);
        
        // 4. 세션 + ResponseEntity 통합 테스트
        testSessionWithResponseEntity(dispatcher);
        
        // 5. 에러 처리 + ResponseEntity 통합 테스트
        testErrorHandlingWithResponseEntity(dispatcher);
    }
}
```

### 예상 출력
```
=== 29단계 Winter Framework ResponseEntity 통합 테스트 ===

1. 기존 방식 호환성 테스트
================================
✅ 기존 ModelAndView 방식 정상 동작
   Status: 200
   Response: <html>...</html>

2. ResponseEntity 기능 테스트
================================
테스트: 모든 사용자 조회
  ✅ 성공 - Status: 200
  📊 데이터 개수: 2
  📋 응답 타입: application/json
  📄 응답 본문: [{"name":"john","address":{"street":"123 Main St"...

테스트: 새 사용자 생성
  ✅ 성공 - Status: 201
  📍 생성된 리소스: /api/users/3
  📄 응답 본문: {"name":"Alice","address":{"street":"789 Elm St"...

=== 통합 테스트 완료 ===
```

## 📊 기능 매트릭스

| 기능 분류 | 25단계 | 26단계 | 27단계 | 28단계 | 29단계 |
|-----------|---------|---------|---------|---------|---------|
| **HTTP 처리** |
| 세션 관리 | ✅ | ✅ | ✅ | ✅ | ✅ 완전 호환 |
| 쿠키 관리 | ✅ | ✅ | ✅ | ✅ | ✅ 완전 호환 |
| HTTP 상태 제어 | ❌ | ❌ | ❌ | ❌ | ✅ **신규** |
| HTTP 헤더 제어 | ❌ | ❌ | ❌ | ❌ | ✅ **신규** |
| **뷰 처리** |
| Writer 지원 | ❌ | ✅ | ✅ | ✅ | ✅ 완전 호환 |
| 뷰 엔진 통합 | ❌ | ✅ | ✅ | ✅ | ✅ 완전 호환 |
| JSON 자동 변환 | ❌ | ❌ | ❌ | ❌ | ✅ **신규** |
| **MVC 구조** |
| 인터셉터 체인 | ❌ | ❌ | ✅ | ✅ | ✅ 완전 호환 |
| 어노테이션 MVC | ❌ | ❌ | ✅ | ✅ | ✅ 완전 호환 |
| **검증 & 바인딩** |
| @Valid 검증 | ❌ | ❌ | ❌ | ✅ | ✅ 완전 호환 |
| BindingResult | ❌ | ❌ | ❌ | ✅ | ✅ 완전 호환 |
| 파라미터 바인딩 | ❌ | ❌ | ❌ | ✅ | ✅ 완전 호환 |
| **REST API** |
| ResponseEntity | ❌ | ❌ | ❌ | ❌ | ✅ **신규** |
| RESTful 상태 코드 | ❌ | ❌ | ❌ | ❌ | ✅ **신규** |

## 🎨 설계 원칙

### 🔄 완전한 하위 호환성
- **기존 코드 제로 수정**: 25-28단계 코드가 한 줄도 바뀌지 않고 정확히 동일하게 동작
- **점진적 마이그레이션**: 기존 컨트롤러는 그대로 두고 새로운 API만 ResponseEntity 사용
- **혼재 사용 지원**: 한 애플리케이션에서 ModelAndView와 ResponseEntity 동시 사용

### 🧩 기능 통합성
- **검증 통합**: @Valid + BindingResult가 ResponseEntity와 완벽 통합
- **세션 통합**: 세션/쿠키 관리가 ResponseEntity와 완벽 통합
- **인터셉터 통합**: 인터셉터 체인이 ResponseEntity와 완벽 통합
- **뷰 엔진 통합**: 기존 뷰 엔진들과 JSON 응답이 동시 지원

### 🚀 확장성
- **인터페이스 기반**: HttpResponse 인터페이스화로 Mock 테스트 지원
- **플러그인 구조**: 새로운 응답 타입 추가 용이
- **설정 유연성**: 커스텀 상태 코드 및 헤더 완전 지원

### 💡 개발자 경험
- **Spring MVC 호환**: Spring 개발자들에게 친숙한 API
- **명확한 의도**: HTTP 응답의 모든 측면을 명시적으로 제어
- **풍부한 에러 처리**: 다양한 HTTP 상태 코드로 세밀한 에러 처리

## 🎯 핵심 성과

### 🏆 Spring MVC와 동등한 기능
Winter 프레임워크가 이제 Spring MVC의 핵심 기능들을 모두 갖추었습니다:

- ✅ **어노테이션 기반 MVC** (22단계)
- ✅ **파일 업로드** (24단계)
- ✅ **세션 관리** (25단계)
- ✅ **뷰 엔진 통합** (26단계)
- ✅ **인터셉터 체인** (27단계)
- ✅ **검증 프레임워크** (28단계)
- ✅ **ResponseEntity & REST API** (29단계) ← **NEW!**

### 🔥 완벽한 RESTful API 지원
```java
// 이제 이런 모던한 REST API를 완벽하게 구현할 수 있습니다!
@Controller
public class UserApiController {
    
    @RequestMapping("/api/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(users)
                .withHeader("X-Total-Count", String.valueOf(users.size()));
    }
    
    @RequestMapping("/api/users/create")
    public ResponseEntity<User> createUser(@Valid @ModelAttribute UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest(extractErrors(result))
                    .withHeader("X-Validation-Errors", String.valueOf(result.getErrorCount()));
        }
        
        User user = userService.create(form);
        return ResponseEntity.created(user)
                .withHeader("Location", "/api/users/" + user.getId());
    }
    
    @RequestMapping("/api/users/{id}")
    public ResponseEntity<User> getUser(@RequestParam("id") String id) {
        User user = userService.findById(id);
        return user != null ? 
            ResponseEntity.ok(user) : 
            ResponseEntity.notFound();
    }
}
```

---

### 🚀 다음 단계는?

29단계로 Winter 프레임워크의 **Web MVC 기능이 완성**되었습니다!

다음은 **IoC Container & Dependency Injection** (30-45단계)로 진화할 예정입니다:
- Bean 정의와 관리
- 의존성 주입 (@Autowired)
- Component Scan (@Component, @Service, @Repository)
- Java Config (@Configuration, @Bean)
- AOP (Aspect-Oriented Programming)

Winter 프레임워크의 여정은 계속됩니다! 🌟

---

**Winter Framework** - *Spring MVC를 직접 구현하며 이해하는 학습용 프레임워크*