# Winter 25단계: 세션 관리 구현 가이드

## 🎯 25단계 목표: Session Management (세션, 쿠키 관리)

24단계 파일 업로드 기능을 완성한 후, 25단계에서는 **세션과 쿠키 관리 기능**을 구현합니다. 사용자의 상태를 유지하고, 로그인 상태 관리, 장바구니, 사용자 설정 등을 위한 세션 시스템을 완성합니다.

---

## 📋 브랜치 생성 및 시작 단계

### 1. 브랜치 생성
```bash
# 현재 브랜치 확인 (24단계가 완료된 상태여야 함)
git status
git branch

# 25단계 브랜치 생성 및 전환
git checkout -b feature/session-management

# 브랜치 생성 확인
git branch
```

### 2. 문서 작성
```bash
# docs 디렉토리에 25단계 문서 생성
touch docs/25-SESSION-MANAGEMENT.md
```

---

## 🎯 25단계 주요 구현 목표

### 1. HTTP 세션 관리
- **HttpSession 인터페이스** - Spring 스타일의 세션 추상화
- **세션 생성과 관리** - 고유 세션 ID 생성 및 생명주기 관리
- **세션 저장소** - 메모리 기반 세션 저장 및 관리
- **세션 만료 처리** - 타임아웃 기반 자동 세션 만료

### 2. 쿠키 지원
- **Cookie 클래스** - 쿠키 데이터 모델링
- **쿠키 읽기/쓰기** - HTTP 헤더를 통한 쿠키 처리
- **쿠키 옵션** - Path, Domain, MaxAge, HttpOnly, Secure 속성 지원
- **JSESSIONID 쿠키** - 세션 ID 전달을 위한 표준 쿠키

### 3. 컨트롤러 통합
- **@SessionAttribute** - 세션 속성 자동 바인딩
- **HttpSession 파라미터** - 컨트롤러 메서드에서 세션 직접 접근
- **세션 기반 데이터 유지** - 요청 간 데이터 유지

### 4. 보안 및 설정
- **세션 하이재킹 방지** - 세션 ID 재생성
- **세션 고정 공격 방지** - 로그인 시 세션 ID 갱신
- **세션 설정 관리** - 타임아웃, 쿠키 설정 등

---

## 🔧 구현할 클래스 목록

### 새로 추가할 클래스들

1. **HttpSession** (인터페이스)
    - `getAttribute(String name)` - 세션 속성 조회
    - `setAttribute(String name, Object value)` - 세션 속성 설정
    - `removeAttribute(String name)` - 세션 속성 제거
    - `invalidate()` - 세션 무효화
    - `getId()` - 세션 ID 조회
    - `isNew()` - 새 세션 여부
    - `getCreationTime()` - 세션 생성 시간
    - `getLastAccessedTime()` - 마지막 접근 시간

2. **StandardHttpSession** (구현체)
    - 메모리 기반 세션 데이터 저장
    - 세션 속성 관리
    - 생명주기 관리

3. **SessionManager** (세션 관리자)
    - 세션 생성, 조회, 삭제
    - 세션 ID 생성
    - 세션 만료 처리
    - 세션 저장소 관리

4. **Cookie** (쿠키 모델)
    - 쿠키 속성 관리 (name, value, path, domain, maxAge 등)
    - 쿠키 헤더 생성
    - 쿠키 파싱

5. **CookieUtil** (쿠키 유틸리티)
    - HTTP 헤더에서 쿠키 파싱
    - Set-Cookie 헤더 생성
    - 쿠키 유효성 검증

6. **SessionConfig** (세션 설정)
    - 세션 타임아웃 설정
    - 쿠키 이름, 경로 설정
    - 보안 옵션 설정

### 수정할 클래스들

1. **HttpRequest** 확장
    - `getSession()` - 세션 조회/생성
    - `getSession(boolean create)` - 세션 조회 (생성 여부 제어)
    - `getCookies()` - 요청 쿠키 조회
    - `getCookie(String name)` - 특정 쿠키 조회

2. **HttpResponse** 확장
    - `addCookie(Cookie cookie)` - 응답에 쿠키 추가
    - `addHeader()` 확장으로 Set-Cookie 지원

3. **Dispatcher** 확장
    - 세션 처리 통합
    - 요청/응답에 세션 및 쿠키 연결

4. **ParameterResolver** 확장
    - HttpSession 파라미터 바인딩 지원
    - @SessionAttribute 어노테이션 처리

---

## 📝 구현 계획 (단계별)

### Step 1: 기본 세션 인터페이스 및 모델
```java
// 1-1. HttpSession 인터페이스 정의
public interface HttpSession {
    String getId();
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    void removeAttribute(String name);
    void invalidate();
    boolean isNew();
    long getCreationTime();
    long getLastAccessedTime();
    int getMaxInactiveInterval();
    void setMaxInactiveInterval(int interval);
}

// 1-2. Cookie 클래스 정의
public class Cookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private int maxAge = -1;
    private boolean httpOnly = false;
    private boolean secure = false;
    
    // 생성자, getter/setter, toString 등
}
```

### Step 2: 세션 관리자 구현
```java
// 2-1. SessionManager 구현
public class SessionManager {
    private final Map<String, StandardHttpSession> sessions = new ConcurrentHashMap<>();
    private final SessionConfig config;
    
    public HttpSession createSession();
    public HttpSession getSession(String sessionId);
    public void removeSession(String sessionId);
    public void cleanupExpiredSessions();
    private String generateSessionId();
}

// 2-2. StandardHttpSession 구현체
public class StandardHttpSession implements HttpSession {
    private final String id;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final long creationTime;
    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;
    private volatile boolean valid = true;
    
    // HttpSession 인터페이스 구현
}
```

### Step 3: 쿠키 처리 유틸리티
```java
// 3-1. CookieUtil 구현
public class CookieUtil {
    public static List<Cookie> parseCookies(String cookieHeader);
    public static String formatSetCookieHeader(Cookie cookie);
    public static Cookie findCookie(List<Cookie> cookies, String name);
    public static boolean isValidCookieName(String name);
    public static boolean isValidCookieValue(String value);
}

// 3-2. SessionConfig 설정 클래스
public class SessionConfig {
    private int maxInactiveInterval = 1800; // 30분
    private String cookieName = "JSESSIONID";
    private String cookiePath = "/";
    private boolean cookieHttpOnly = true;
    private boolean cookieSecure = false;
    
    // 빌더 패턴 및 체이닝 메서드
}
```

### Step 4: HttpRequest/HttpResponse 세션 통합
```java
// 4-1. HttpRequest 확장
public class HttpRequest {
    private HttpSession session;
    private List<Cookie> cookies;
    
    public HttpSession getSession() {
        return getSession(true);
    }
    
    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = sessionManager.createSession();
        }
        return session;
    }
    
    public List<Cookie> getCookies() {
        if (cookies == null) {
            cookies = CookieUtil.parseCookies(getHeader("Cookie"));
        }
        return cookies;
    }
    
    public Cookie getCookie(String name) {
        return CookieUtil.findCookie(getCookies(), name);
    }
}

// 4-2. HttpResponse 확장
public class HttpResponse {
    private final List<Cookie> cookies = new ArrayList<>();
    
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
        addHeader("Set-Cookie", CookieUtil.formatSetCookieHeader(cookie));
    }
}
```

### Step 5: Dispatcher 세션 처리 통합
```java
// 5-1. Dispatcher 수정
public class Dispatcher {
    private final SessionManager sessionManager;
    private final SessionConfig sessionConfig;
    
    public void dispatch(HttpRequest request, HttpResponse response) {
        try {
            // 0. 세션 처리 (새로 추가)
            processSession(request, response);
            
            // 기존 처리 흐름...
            
        } finally {
            // 세션 정리 작업
            finalizeSession(request, response);
        }
    }
    
    private void processSession(HttpRequest request, HttpResponse response) {
        // JSESSIONID 쿠키에서 세션 ID 추출
        Cookie sessionCookie = request.getCookie(sessionConfig.getCookieName());
        
        if (sessionCookie != null) {
            // 기존 세션 복원
            HttpSession existingSession = sessionManager.getSession(sessionCookie.getValue());
            if (existingSession != null && !isExpired(existingSession)) {
                request.setSession(existingSession);
                ((StandardHttpSession) existingSession).updateLastAccessedTime();
            }
        }
    }
    
    private void finalizeSession(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.isNew()) {
            // 새 세션인 경우 JSESSIONID 쿠키 생성
            Cookie sessionCookie = new Cookie(sessionConfig.getCookieName(), session.getId());
            sessionCookie.setPath(sessionConfig.getCookiePath());
            sessionCookie.setHttpOnly(sessionConfig.isCookieHttpOnly());
            sessionCookie.setSecure(sessionConfig.isCookieSecure());
            
            response.addCookie(sessionCookie);
        }
    }
}
```

### Step 6: 컨트롤러 파라미터 바인딩
```java
// 6-1. @SessionAttribute 어노테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SessionAttribute {
    String value() default "";
    String name() default "";
    boolean required() default true;
}

// 6-2. ParameterResolver 확장
public class ParameterResolver {
    public Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response) {
        Class<?> paramType = parameter.getType();
        
        // HttpSession 파라미터 처리
        if (paramType.equals(HttpSession.class)) {
            return request.getSession();
        }
        
        // @SessionAttribute 처리
        if (parameter.isAnnotationPresent(SessionAttribute.class)) {
            return resolveSessionAttribute(parameter, request);
        }
        
        // 기존 처리 로직...
    }
    
    private Object resolveSessionAttribute(Parameter parameter, HttpRequest request) {
        SessionAttribute annotation = parameter.getAnnotation(SessionAttribute.class);
        String attributeName = getAttributeName(annotation, parameter);
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object value = session.getAttribute(attributeName);
            if (value != null) {
                return value;
            }
        }
        
        if (annotation.required()) {
            throw new IllegalStateException("Required session attribute '" + attributeName + "' not found");
        }
        
        return null;
    }
}
```

---

## 🧪 테스트 컨트롤러 작성

25단계 구현을 검증하기 위한 SessionTestController 작성:

```java
// SessionTestController.java
@Controller
public class SessionTestController {
    
    // 1. 기본 세션 생성 및 속성 설정
    @RequestMapping("/session/create")
    public ModelAndView createSession(HttpSession session) {
        session.setAttribute("username", "testUser");
        session.setAttribute("loginTime", new Date());
        session.setAttribute("visitCount", 1);
        
        ModelAndView mav = new ModelAndView("session-created");
        mav.addAttribute("sessionId", session.getId());
        mav.addAttribute("isNew", session.isNew());
        return mav;
    }
    
    // 2. 세션 정보 조회
    @RequestMapping("/session/info")
    public ModelAndView sessionInfo(HttpSession session) {
        ModelAndView mav = new ModelAndView("session-info");
        mav.addAttribute("sessionId", session.getId());
        mav.addAttribute("username", session.getAttribute("username"));
        mav.addAttribute("loginTime", session.getAttribute("loginTime"));
        mav.addAttribute("visitCount", session.getAttribute("visitCount"));
        mav.addAttribute("creationTime", new Date(session.getCreationTime()));
        mav.addAttribute("lastAccessedTime", new Date(session.getLastAccessedTime()));
        return mav;
    }
    
    // 3. 방문 횟수 증가 (세션 속성 수정)
    @RequestMapping("/session/visit")
    public ModelAndView incrementVisit(HttpSession session) {
        Integer count = (Integer) session.getAttribute("visitCount");
        if (count == null) count = 0;
        session.setAttribute("visitCount", count + 1);
        
        ModelAndView mav = new ModelAndView("visit-incremented");
        mav.addAttribute("visitCount", count + 1);
        return mav;
    }
    
    // 4. @SessionAttribute 테스트
    @RequestMapping("/session/user")
    public ModelAndView getUserFromSession(
        @SessionAttribute("username") String username,
        @SessionAttribute(value = "visitCount", required = false) Integer visitCount
    ) {
        ModelAndView mav = new ModelAndView("user-info");
        mav.addAttribute("username", username);
        mav.addAttribute("visitCount", visitCount != null ? visitCount : 0);
        return mav;
    }
    
    // 5. 로그인 시뮬레이션 (세션 기반)
    @RequestMapping(value = "/session/login", method = "POST")
    public ModelAndView login(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        HttpSession session
    ) {
        // 간단한 인증 로직
        if ("admin".equals(username) && "password".equals(password)) {
            session.setAttribute("loggedIn", true);
            session.setAttribute("username", username);
            session.setAttribute("loginTime", new Date());
            session.setAttribute("userRole", "ADMIN");
            
            ModelAndView mav = new ModelAndView("login-success");
            mav.addAttribute("username", username);
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("login-failed");
            mav.addAttribute("error", "Invalid username or password");
            return mav;
        }
    }
    
    // 6. 로그아웃 (세션 무효화)
    @RequestMapping("/session/logout")
    public ModelAndView logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        session.invalidate();
        
        ModelAndView mav = new ModelAndView("logout-success");
        mav.addAttribute("username", username);
        return mav;
    }
    
    // 7. 쿠키 테스트
    @RequestMapping("/session/cookie/set")
    public ModelAndView setCookie(HttpResponse response) {
        Cookie userPref = new Cookie("userPreference", "darkMode");
        userPref.setMaxAge(86400); // 1일
        userPref.setPath("/");
        userPref.setHttpOnly(true);
        
        Cookie language = new Cookie("language", "ko");
        language.setMaxAge(86400 * 30); // 30일
        language.setPath("/");
        
        response.addCookie(userPref);
        response.addCookie(language);
        
        ModelAndView mav = new ModelAndView("cookie-set");
        mav.addAttribute("message", "쿠키가 설정되었습니다.");
        return mav;
    }
    
    // 8. 쿠키 읽기
    @RequestMapping("/session/cookie/read")
    public ModelAndView readCookies(HttpRequest request) {
        List<Cookie> cookies = request.getCookies();
        
        ModelAndView mav = new ModelAndView("cookie-info");
        mav.addAttribute("cookies", cookies);
        mav.addAttribute("userPreference", 
            request.getCookie("userPreference") != null ? 
            request.getCookie("userPreference").getValue() : "없음");
        mav.addAttribute("language", 
            request.getCookie("language") != null ? 
            request.getCookie("language").getValue() : "없음");
        return mav;
    }
    
    // 9. 장바구니 시뮬레이션 (세션 기반)
    @RequestMapping("/session/cart/add")
    public ModelAndView addToCart(
        @RequestParam("productId") String productId,
        @RequestParam("productName") String productName,
        @RequestParam("price") int price,
        HttpSession session
    ) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        
        cart.add(new CartItem(productId, productName, price));
        
        ModelAndView mav = new ModelAndView("cart-updated");
        mav.addAttribute("cart", cart);
        mav.addAttribute("totalItems", cart.size());
        mav.addAttribute("totalPrice", cart.stream().mapToInt(CartItem::getPrice).sum());
        return mav;
    }
    
    // 10. 장바구니 조회
    @RequestMapping("/session/cart/view")
    public ModelAndView viewCart(@SessionAttribute(value = "cart", required = false) List<CartItem> cart) {
        if (cart == null) cart = new ArrayList<>();
        
        ModelAndView mav = new ModelAndView("cart-view");
        mav.addAttribute("cart", cart);
        mav.addAttribute("totalItems", cart.size());
        mav.addAttribute("totalPrice", cart.stream().mapToInt(CartItem::getPrice).sum());
        return mav;
    }
}

// CartItem 모델 클래스
public class CartItem {
    private String productId;
    private String productName;
    private int price;
    
    // 생성자, getter/setter
}
```

---

## 📁 필요한 HTML 템플릿 목록

25단계 테스트를 위해 생성할 HTML 템플릿들:

1. **session-created.html** - 세션 생성 결과
2. **session-info.html** - 세션 정보 표시
3. **visit-incremented.html** - 방문 횟수 증가 결과
4. **user-info.html** - 사용자 정보 (세션에서)
5. **login-success.html** - 로그인 성공
6. **login-failed.html** - 로그인 실패
7. **logout-success.html** - 로그아웃 성공
8. **cookie-set.html** - 쿠키 설정 완료
9. **cookie-info.html** - 쿠키 정보 표시
10. **cart-updated.html** - 장바구니 업데이트
11. **cart-view.html** - 장바구니 보기
12. **session-test-menu.html** - 세션 테스트 메뉴

---

## 🚀 구현 순서 가이드

### 1단계: 기본 인터페이스와 모델 (1일차)
- HttpSession 인터페이스 정의
- Cookie 클래스 구현
- SessionConfig 설정 클래스

### 2단계: 세션 관리자 구현 (2일차)
- SessionManager 구현
- StandardHttpSession 구현체
- 세션 생명주기 관리

### 3단계: 쿠키 처리 (3일차)
- CookieUtil 구현
- HTTP 헤더 파싱/생성
- 쿠키 유효성 검증

### 4단계: Request/Response 통합 (4일차)
- HttpRequest 세션 메서드 추가
- HttpResponse 쿠키 메서드 추가
- 기존 코드와의 호환성 확보

### 5단계: Dispatcher 통합 (5일차)
- 세션 처리 로직 추가
- JSESSIONID 쿠키 자동 처리
- 세션 생명주기 관리

### 6단계: 파라미터 바인딩 (6일차)
- @SessionAttribute 어노테이션
- ParameterResolver 확장
- HttpSession 파라미터 지원

### 7단계: 테스트 및 검증 (7일차)
- SessionTestController 구현
- HTML 템플릿 작성
- 다양한 시나리오 테스트

---

## ✅ 완성 후 확인사항

### 기능 검증
- [ ] 세션 생성 및 속성 관리
- [ ] 쿠키 읽기/쓰기
- [ ] 세션 만료 처리
- [ ] @SessionAttribute 바인딩
- [ ] JSESSIONID 자동 처리

### 보안 검증
- [ ] 세션 하이재킹 방지
- [ ] 쿠키 보안 속성 설정
- [ ] 세션 고정 공격 방지

### 성능 검증
- [ ] 메모리 사용량 확인
- [ ] 만료된 세션 정리
- [ ] 동시 세션 처리

---

## 🎉 25단계 완성 후 얻는 것

- **상태 유지 기능** - 사용자별 데이터 관리
- **로그인 시스템** - 인증 상태 유지
- **장바구니 기능** - 세션 기반 임시 데이터
- **사용자 설정** - 쿠키 기반 환경설정
- **보안 강화** - 세션 기반 보안 기능

25단계를 완성하면 Winter Framework는 **실제 웹 애플리케이션에서 필요한 상태 관리 기능**을 완전히 지원하게 됩니다!