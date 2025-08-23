# 🎯 Winter 30챕터: RESTController 구현 완료 가이드

## ✅ **완료된 구현 목록**

### 1. **새로 생성할 파일들**

#### 📁 **어노테이션 클래스**
- `src/winter/annotation/RestController.java`
- `src/winter/annotation/ResponseBody.java`

#### 📁 **뷰 클래스**
- `src/winter/view/ResponseEntityView.java`

#### 📁 **핸들러 어댑터**
- `src/winter/dispatcher/RestHandlerAdapter.java`

### 2. **수정된 기존 파일들**

#### 🔧 **HttpStatus.java 수정**
- ✅ `getCode()` 메서드 추가 완료
- Spring 호환성을 위해 `value()`와 동일한 기능 제공

#### 🔧 **Dispatcher.java 수정**
```java
// handlerAdapters 리스트에 RestHandlerAdapter 추가
private final List<HandlerAdapter> handlerAdapters = List.of(
    new RestHandlerAdapter(),         // 30챕터 신규: 최우선 처리
    new AnnotationHandlerAdapter(),   // 기존 @Controller 처리  
    new ControllerHandlerAdapter()    // 레거시 처리
);
```

#### 🔧 **AnnotationHandlerMapping.java 대체**
- ✅ @RestController 지원 추가
- ✅ 컨트롤러 타입별 통계 기능
- ✅ 디버깅 기능 강화

#### 🔧 **ContentNegotiatingViewResolver.java 대체**
- ✅ ResponseEntity 전용 처리 ("responseEntity" → ResponseEntityView)
- ✅ JSON 응답 전용 처리 ("jsonResponse" → JsonView)
- ✅ 기존 Content Negotiation 기능 유지

#### 🔧 **CombinedHandlerMapping.java 대체**
- ✅ @RestController 등록 지원
- ✅ ResponseEntityController 등록
- ✅ REST API와 MVC 엔드포인트 분리 조회

## 🚀 **구현 순서 및 테스트**

### **1단계: 기본 파일 생성**
```bash
# 어노테이션 생성
src/winter/annotation/RestController.java
src/winter/annotation/ResponseBody.java

# 뷰 클래스 생성  
src/winter/view/ResponseEntityView.java

# 핸들러 어댑터 생성
src/winter/dispatcher/RestHandlerAdapter.java
```

### **2단계: 기존 파일 수정**
```bash
# HttpStatus.java - getCode() 메서드 추가 (완료)
# Dispatcher.java - RestHandlerAdapter 추가
# AnnotationHandlerMapping.java - 전체 대체
# ContentNegotiatingViewResolver.java - 전체 대체  
# CombinedHandlerMapping.java - 전체 대체
```

### **3단계: 테스트 컨트롤러 작성**
```java
@RestController
public class TestRestController {
    
    @RequestMapping("/api/test")
    public Map<String, String> test() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Hello REST API!");
        result.put("timestamp", new Date().toString());
        return result; // 자동으로 JSON 변환
    }
    
    @RequestMapping("/api/user/{id}")  
    public ResponseEntity<String> getUser(@RequestParam("id") String id) {
        if ("1".equals(id)) {
            return ResponseEntity.ok("User found: " + id);
        } else {
            return ResponseEntity.notFound();
        }
    }
}
```

### **4단계: 기존 컨트롤러 활용 테스트**
- `ResponseEntityController.java`가 이미 ResponseEntity를 사용 중
- 해당 컨트롤러에 `@RestController` 추가하여 테스트 가능
- 기존 MVC 컨트롤러들과 동시 작동 확인

## 🎯 **핵심 기능 검증 포인트**

### **REST API 응답 확인**
```bash
# JSON 자동 변환
GET /api/test
→ {"message": "Hello REST API!", "timestamp": "..."}

# ResponseEntity 상태 코드
GET /api/user/1 → 200 OK with body
GET /api/user/999 → 404 Not Found  

# 헤더 제어
GET /api/users → X-Total-Count: 10, Cache-Control: max-age=300
```

### **기존 MVC 기능 유지 확인**
```bash
# HTML 템플릿 렌더링
GET /products → products.html 렌더링

# 파일 업로드
POST /upload → upload-success.html 렌더링

# 세션 관리  
GET /session/info → 세션 정보 HTML 페이지
```

### **Content Negotiation 확인**
```bash
# Accept 헤더에 따른 응답 변환
GET /api/users 
Accept: application/json → JSON 응답
Accept: text/html → HTML 템플릿 (있다면)
```

## 📊 **예상 결과**

### **성공적인 구현 시 로그**
```
=== 30챕터: 모든 컨트롤러 등록 시작 ===

[MVC 컨트롤러 등록]
@Controller 등록: ProductController 
@Controller 등록: SearchController
...

[REST API 컨트롤러 등록]  
@RestController 등록: ResponseEntityController
@RestController 등록: TestRestController
...

MVC 컨트롤러: 15개 메서드
REST 컨트롤러: 8개 메서드
총 어노테이션 핸들러: 23개 메서드

  ✓ 전통적인 MVC 패턴 (@Controller → HTML 템플릿)
  ✓ REST API 패턴 (@RestController → JSON 응답)  
  ✓ ResponseEntity 지원 (상태 코드 + 헤더 제어)
```

### **요청 처리 시 로그**
```
=== RestHandlerAdapter 지원 여부 확인 ===
ResponseEntityController.getUsers → 지원함 (RestController=true, ResponseBody=false)

=== RestHandlerAdapter 실행 ===  
Controller: ResponseEntityController
Method: getUsers
ResponseEntity 감지 → ResponseEntityView 사용

=== ResponseEntity 렌더링 완료 ===
상태=200 OK, 헤더=2개, 본문=있음
```

## 💡 **문제 해결 가이드**

### **자주 발생할 수 있는 오류**

1. **import 오류**: 새로 생성한 어노테이션 import 확인
2. **getCode() 오류**: HttpStatus.java 수정 확인
3. **View 해결 실패**: ContentNegotiatingViewResolver 대체 확인
4. **Handler 매칭 실패**: CombinedHandlerMapping 대체 확인
5. **JSON 변환 실패**: JsonSerializer 정상 작동 확인

### **디버깅 팁**
- Dispatcher 로그에서 핸들러 어댑터 선택 과정 확인
- ContentNegotiatingViewResolver 로그에서 뷰 선택 과정 확인
- ResponseEntityView 로그에서 JSON 변환 과정 확인

## 🎉 **완료 후 확인사항**

- [ ] 기존 MVC 컨트롤러 정상 작동
- [ ] 새로운 REST API 컨트롤러 작동
- [ ] ResponseEntity 상태 코드/헤더 제어
- [ ] JSON 자동 직렬화
- [ ] Content Negotiation 기능
- [ ] 오류 처리 및 예외 응답

모든 파일을 생성하고 수정한 후, 테스트 컨트롤러로 기능을 검증해보세요!