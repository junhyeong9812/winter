# 28. Validation Support - Bean Validation API 통합

## 📋 목표

Bean Validation API (`@Valid`, `@NotNull`, `@NotEmpty`, `@Size`, `@Email`, `@Pattern`)를 Winter 프레임워크에 통합하여 자동 객체 검증 기능을 구현합니다.

---

## 🏗 주요 구현 사항

### 1. Validation 어노테이션
- `@Valid`: 검증 트리거 어노테이션
- `@NotNull`: null 값 검증
- `@NotEmpty`: 빈 값 검증
- `@Size`: 크기/길이 검증
- `@Email`: 이메일 형식 검증
- `@Pattern`: 정규식 패턴 검증

### 2. 검증 결과 관리
- `BindingResult`: 검증 결과 컨테이너
- `FieldError`: 개별 필드 오류 정보
- `ValidationException`: 검증 실패 예외

### 3. 검증기 구현
- `Validator`: 검증기 인터페이스
- `AnnotationBasedValidator`: 어노테이션 기반 검증기
- `ValidationUtils`: 검증 유틸리티

---

## 🔧 사용 방법

### 1. 폼 클래스에 검증 어노테이션 추가

```java
public class UserForm {
    @NotNull(message = "이름은 필수입니다")
    @NotEmpty(message = "이름을 입력해주세요")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
    private String name;
    
    @NotNull(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;
    
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;
    
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", 
             message = "올바른 전화번호 형식을 입력해주세요")
    private String phoneNumber;
    
    // getters and setters...
}
```

### 2. 컨트롤러에서 검증 사용

```java
@Controller
public class UserController {
    
    @RequestMapping(value = "/user/register", method = "POST")
    public ModelAndView registerUser(@Valid @ModelAttribute UserForm userForm, 
                                   BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView();
        
        // 검증 오류 확인
        if (bindingResult.hasErrors()) {
            mv.setViewName("user-form");
            mv.addObject("userForm", userForm);
            mv.addObject("errors", bindingResult.getFieldErrorMessages());
            return mv;
        }
        
        // 검증 성공 시 처리
        mv.setViewName("user-success");
        mv.addObject("user", userForm);
        return mv;
    }
}
```

### 3. 템플릿에서 오류 표시

```html
<form action="/user/register" method="post">
    <div class="form-group">
        <label>이름:</label>
        <input type="text" name="name" value="${userForm.name}">
        ${errors.name}
            <span class="error">${errors.name}</span>
        ${/errors.name}
    </div>
    
    <div class="form-group">
        <label>이메일:</label>
        <input type="email" name="email" value="${userForm.email}">
        ${errors.email}
            <span class="error">${errors.email}</span>
        ${/errors.email}
    </div>
    
    <button type="submit">등록</button>
</form>
```

---

## 🎯 검증 어노테이션 상세

### @Valid
컨트롤러 메서드 파라미터에 사용하여 자동 검증을 활성화합니다.

```java
public ModelAndView process(@Valid @ModelAttribute UserForm form, 
                          BindingResult result) {
    // 자동으로 검증이 수행됨
}
```

### @NotNull
필드가 null이 아님을 검증합니다.

```java
@NotNull(message = "필수 입력 항목입니다")
private String requiredField;
```

### @NotEmpty
필드가 null이 아니고 비어있지 않음을 검증합니다. (String, Collection, Array 지원)

```java
@NotEmpty(message = "값을 입력해주세요")
private String text;
```

### @Size
필드의 크기가 지정된 범위 내에 있는지 검증합니다.

```java
@Size(min = 2, max = 100, message = "2자 이상 100자 이하로 입력해주세요")
private String description;
```

### @Email
이메일 형식이 올바른지 검증합니다.

```java
@Email(message = "올바른 이메일 주소를 입력해주세요")
private String email;
```

### @Pattern
정규식 패턴과 일치하는지 검증합니다.

```java
@Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", 
         message = "010-1234-5678 형식으로 입력해주세요")
private String phoneNumber;
```

---

## 🚀 고급 기능

### 1. 커스텀 검증 로직
기본 어노테이션 검증 후 추가 비즈니스 로직 검증이 가능합니다.

```java
@RequestMapping(value = "/user/register", method = "POST")
public ModelAndView registerUser(@Valid @ModelAttribute UserForm userForm, 
                               BindingResult bindingResult) {
    
    // 추가 비즈니스 검증
    if (userService.isEmailExists(userForm.getEmail())) {
        bindingResult.addFieldError("email", userForm.getEmail(), 
                                  "이미 사용 중인 이메일입니다");
    }
    
    if (bindingResult.hasErrors()) {
        // 오류 처리
    }
    
    // 성공 처리
}
```

### 2. 검증 예외 처리
`BindingResult` 파라미터가 없으면 `ValidationException`이 발생합니다.

```java
@RequestMapping("/api/user")
public ModelAndView createUser(@Valid @ModelAttribute UserForm form) {
    // BindingResult가 없으므로 검증 실패 시 ValidationException 발생
    return new ModelAndView("success");
}
```

### 3. JSON API 응답
API 컨트롤러에서 검증 결과를 JSON으로 반환할 수 있습니다.

```java
@RequestMapping("/api/validate")
public ModelAndView validateApi(@Valid @ModelAttribute UserForm form, 
                              BindingResult result) {
    ModelAndView mv = new ModelAndView("json");
    
    if (result.hasErrors()) {
        mv.addObject("success", false);
        mv.addObject("errors", result.getFieldErrorMessages());
    } else {
        mv.addObject("success", true);
    }
    
    return mv;
}
```

---

## 📊 ValidationUtils 유틸리티

### 주요 메서드

```java
// 빈 값 확인
ValidationUtils.isEmpty(value)
ValidationUtils.isNotEmpty(value)

// 이메일 형식 확인
ValidationUtils.isValidEmail("test@example.com")

// 크기 확인
ValidationUtils.getSize("hello") // 5
ValidationUtils.isSizeInRange(value, 1, 10)

// 패턴 확인
ValidationUtils.matchesPattern("010-1234-5678", "^\\d{3}-\\d{4}-\\d{4}$")

// 메시지 플레이스홀더 치환
ValidationUtils.replacePlaceholders("size must be between {min} and {max}", 2, 10)
```

---

## 🔄 동작 흐름

1. **요청 수신**: 컨트롤러 메서드 호출
2. **파라미터 바인딩**: `@ModelAttribute`로 객체 생성 및 데이터 바인딩
3. **검증 수행**: `@Valid` 어노테이션 감지 시 자동 검증 실행
4. **결과 처리**: `BindingResult`에 검증 결과 저장
5. **메서드 실행**: 검증 결과와 함께 컨트롤러 메서드 실행

---

## 🎮 테스트 및 예제

### 1. 기본 테스트 실행

```java
public class ValidationTest {
    public static void main(String[] args) {
        AnnotationBasedValidator validator = new AnnotationBasedValidator();
        
        ValidatedUserForm form = new ValidatedUserForm();
        form.setName(""); // 빈 값
        form.setEmail("invalid-email"); // 잘못된 이메일
        
        BindingResult result = new BindingResult(form);
        validator.validate(form, result);
        
        System.out.println("오류 개수: " + result.getErrorCount());
        for (FieldError error : result.getFieldErrors()) {
            System.out.println(error.getField() + ": " + error.getMessage());
        }
    }
}
```

### 2. 웹 테스트 URL

- **검증 폼**: `/validation/form`
- **사용자 등록**: `/validation/register` (POST)
- **API 검증**: `/api/validation/check` (POST)
- **커스텀 검증**: `/validation/custom` (POST)
- **검증 통계**: `/validation/stats`

---

## 🔧 확장 가능성

### 1. 새로운 검증 어노테이션 추가

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    long value();
    String message() default "must be greater than or equal to {value}";
}

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    long value();
    String message() default "must be less than or equal to {value}";
}
```

### 2. 그룹 검증 지원

```java
public interface CreateGroup {}
public interface UpdateGroup {}

public class UserForm {
    @NotNull(groups = CreateGroup.class)
    private String password;
    
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private String email;
}
```

### 3. 검증 프로파일링

검증 성능 모니터링 및 통계 수집 기능을 추가할 수 있습니다.

```java
public class ValidationProfiler {
    private static final Map<String, ValidationStats> stats = new ConcurrentHashMap<>();
    
    public static void recordValidation(String field, boolean success, long duration) {
        // 통계 수집 로직
    }
}
```

---

## 🚨 주의사항

### 1. 성능 고려사항
- 리플렉션을 사용하므로 대량 데이터 처리 시 성능 영향
- 캐싱 전략 적용 검토 필요

### 2. 메모리 사용
- `BindingResult`는 모든 오류 정보를 메모리에 저장
- 대용량 폼에서는 메모리 사용량 주의

### 3. 스레드 안전성
- `AnnotationBasedValidator`는 상태가 없으므로 스레드 안전
- `BindingResult`는 요청별로 생성되므로 안전

---

## 📈 Spring과의 호환성

Winter의 Validation 구현은 Spring의 Bean Validation과 유사한 구조를 가집니다:

| 기능 | Winter | Spring |
|------|--------|--------|
| 검증 트리거 | `@Valid` | `@Valid` |
| 결과 객체 | `BindingResult` | `BindingResult` |
| 검증기 | `Validator` | `Validator` |
| 어노테이션 | 커스텀 구현 | JSR-303/JSR-380 |

### 마이그레이션 가이드

Spring에서 Winter로 마이그레이션 시:

1. JSR-303 어노테이션을 Winter 어노테이션으로 변경
2. 컨트롤러 메서드 시그니처는 동일하게 유지
3. 템플릿의 오류 표시 로직은 거의 동일

---

## 🎯 다음 단계 (29장 예고)

29장에서는 **ResponseEntity와 HTTP 상태 코드 제어** 기능을 구현할 예정입니다:

- `ResponseEntity<T>` 클래스 구현
- HTTP 상태 코드 세밀한 제어
- 헤더 조작 기능
- RESTful API 응답 최적화

---

## 📝 구현 체크리스트

### ✅ 완료된 기능
- [x] 기본 검증 어노테이션 (@Valid, @NotNull, @NotEmpty, @Size, @Email, @Pattern)
- [x] BindingResult 및 FieldError 구현
- [x] AnnotationBasedValidator 구현
- [x] AnnotationHandlerAdapter에 검증 기능 통합
- [x] ValidationUtils 유틸리티 구현
- [x] 컨트롤러 예제 및 템플릿 작성
- [x] 테스트 코드 작성

### 🔄 향후 개선 사항
- [ ] 검증 그룹 지원
- [ ] 커스텀 검증 어노테이션 쉬운 추가 방법
- [ ] 검증 성능 프로파일링
- [ ] 국제화 메시지 지원
- [ ] 조건부 검증 (Conditional Validation)

---

## 🏁 결론

28장에서 구현한 Validation Support는 Winter 프레임워크에 강력한 객체 검증 기능을 추가했습니다. 이제 개발자들은 간단한 어노테이션만으로 복잡한 검증 로직을 구현할 수 있으며, Spring과 유사한 방식으로 웹 애플리케이션의 데이터 무결성을 보장할 수 있습니다.

다음 장에서는 더욱 정교한 HTTP 응답 제어 기능을 통해 RESTful API 개발을 위한 기반을 마련할 예정입니다.