# 27단계: HandlerInterceptor 체인 구조 구현

## 목표
Spring MVC의 HandlerInterceptor 패턴을 구현하여 요청 전후에 공통 처리(로깅, 인증, 성능 측정 등)를 할 수 있는 인터셉터 체인 구조를 구현합니다.

## 학습 내용
1. **HandlerInterceptor 인터페이스**: 요청 처리 전후 시점에 개입할 수 있는 인터페이스 설계
2. **InterceptorChain**: 여러 인터셉터를 체인으로 연결하는 구조
3. **인터셉터 실행 순서**: preHandle → HandlerAdapter 실행 → postHandle → afterCompletion
4. **인터셉터 예외 처리**: 인터셉터에서 예외 발생 시 체인 중단 및 정리
5. **다양한 인터셉터 구현**: 로깅, 인증, 성능 측정, CORS 등

## 구현 단계

### 1. HandlerInterceptor 인터페이스 설계
```java
public interface HandlerInterceptor {
    /**
     * 핸들러 실행 전에 호출됩니다.
     * false를 반환하면 요청 처리가 중단됩니다.
     */
    default boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        return true;
    }
    
    /**
     * 핸들러 실행 후, 뷰 렌더링 전에 호출됩니다.
     * ModelAndView를 수정할 수 있습니다.
     */
    default void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 기본 구현 없음
    }
    
    /**
     * 요청 처리 완료 후 호출됩니다. (뷰 렌더링 후)
     * 예외가 발생했더라도 반드시 호출됩니다.
     */
    default void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        // 기본 구현 없음
    }
}
```

### 2. InterceptorChain 구현
```java
public class InterceptorChain {
    private List<HandlerInterceptor> interceptors = new ArrayList<>();
    private int interceptorIndex = -1;  // 현재 실행된 인터셉터 인덱스
    
    /**
     * 인터셉터 등록
     */
    public void addInterceptor(HandlerInterceptor interceptor);
    
    /**
     * preHandle 체인 실행
     */
    public boolean applyPreHandle(HttpRequest request, HttpResponse response, Object handler);
    
    /**
     * postHandle 체인 실행 (역순)
     */
    public void applyPostHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView mv);
    
    /**
     * afterCompletion 체인 실행 (역순)
     */
    public void triggerAfterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex);
}
```

### 3. Dispatcher에 인터셉터 체인 통합
- 기존 dispatch 메서드를 수정하여 인터셉터 체인 실행
- preHandle → HandlerAdapter → postHandle → ViewResolver → afterCompletion 순서
- 예외 발생 시에도 afterCompletion 보장

### 4. 다양한 인터셉터 구현

#### LoggingInterceptor
- 요청/응답 로깅
- 처리 시간 측정
- 요청 ID 생성

#### AuthenticationInterceptor
- 사용자 인증 확인
- 세션 기반 인증
- 인증 실패 시 로그인 페이지 리다이렉트

#### PerformanceInterceptor
- 요청 처리 시간 측정
- 느린 요청 감지
- 성능 메트릭 수집

#### CorsInterceptor
- CORS 헤더 설정
- Preflight 요청 처리
- Origin 검증

#### SecurityInterceptor
- XSS 방지 헤더 설정
- CSRF 토큰 검증
- 보안 관련 응답 헤더 추가

## 테스트 시나리오

### 테스트 1: 기본 인터셉터 체인
```
GET /hello
→ LoggingInterceptor.preHandle
→ HelloController 실행
→ LoggingInterceptor.postHandle
→ 뷰 렌더링
→ LoggingInterceptor.afterCompletion
```

### 테스트 2: 인증 인터셉터
```
GET /secure/admin (인증 없음)
→ AuthenticationInterceptor.preHandle (false 반환)
→ 401 Unauthorized 응답
→ AuthenticationInterceptor.afterCompletion
```

### 테스트 3: 다중 인터셉터 체인
```
GET /api/data
→ LoggingInterceptor.preHandle
→ AuthenticationInterceptor.preHandle
→ PerformanceInterceptor.preHandle
→ ApiController 실행
→ PerformanceInterceptor.postHandle
→ AuthenticationInterceptor.postHandle
→ LoggingInterceptor.postHandle
→ 뷰 렌더링
→ LoggingInterceptor.afterCompletion
→ AuthenticationInterceptor.afterCompletion
→ PerformanceInterceptor.afterCompletion
```

### 테스트 4: 예외 발생 시 인터셉터 처리
```
GET /error-test
→ LoggingInterceptor.preHandle
→ ErrorController 실행 (예외 발생)
→ 예외 처리
→ LoggingInterceptor.afterCompletion (예외 정보 포함)
```

### 테스트 5: CORS 인터셉터
```
OPTIONS /api/data (Preflight)
→ CorsInterceptor.preHandle
→ CORS 헤더 설정 후 요청 중단
→ CorsInterceptor.afterCompletion
```

### 테스트 6: 성능 측정 인터셉터
```
GET /slow-operation
→ PerformanceInterceptor.preHandle (시작 시간 기록)
→ SlowController 실행 (3초 대기)
→ PerformanceInterceptor.postHandle
→ 뷰 렌더링
→ PerformanceInterceptor.afterCompletion (총 처리 시간 로깅)
```

## 디렉토리 구조
```
src/winter/
├── interceptor/
│   ├── HandlerInterceptor.java (인터페이스)
│   ├── InterceptorChain.java
│   ├── LoggingInterceptor.java
│   ├── AuthenticationInterceptor.java
│   ├── PerformanceInterceptor.java
│   ├── CorsInterceptor.java
│   └── SecurityInterceptor.java
├── dispatcher/
│   └── Dispatcher.java (수정)
├── controller/
│   └── InterceptorTestController.java (테스트용)
└── templates/
    ├── secure-admin.html
    ├── performance-result.html
    └── cors-test.html
```

## 기대 효과
1. **관심사 분리**: 비즈니스 로직과 횡단 관심사의 분리
2. **재사용성**: 공통 처리 로직을 여러 컨트롤러에서 재사용
3. **유연성**: 인터셉터 체인의 동적 구성 가능
4. **확장성**: 새로운 횡단 관심사를 쉽게 추가
5. **디버깅**: 요청 처리 과정의 상세한 추적 가능

## 향후 확장 방향
- 인터셉터 설정을 통한 URL 패턴 매핑
- @Order 어노테이션을 통한 인터셉터 순서 제어
- 조건부 인터셉터 적용 (특정 조건에서만 실행)
- 인터셉터 그룹핑 및 계층 구조
- 비동기 요청에 대한 인터셉터 지원