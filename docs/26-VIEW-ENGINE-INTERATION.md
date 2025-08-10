# 26단계: View Engine Integration - 외부 뷰 엔진 통합

## 목표
기존의 단순한 HTML 템플릿 시스템을 확장하여 Thymeleaf, Mustache, JSP 등의 외부 뷰 엔진을 플러그인 방식으로 통합할 수 있는 구조를 구현합니다.

## 학습 내용
1. **ViewEngine 추상화**: 다양한 뷰 엔진을 통합하는 인터페이스 설계
2. **뷰 엔진 레지스트리**: 여러 뷰 엔진을 등록하고 관리하는 시스템
3. **템플릿 확장자 매핑**: 파일 확장자에 따른 뷰 엔진 자동 선택
4. **뷰 엔진별 컨텍스트**: 각 뷰 엔진에 맞는 데이터 변환
5. **fallback 메커니즘**: 지원하지 않는 템플릿에 대한 대체 처리

## 구현 단계

### 1. ViewEngine 인터페이스 설계
```java
public interface ViewEngine {
    /**
     * 뷰 엔진이 지원하는 파일 확장자들 반환
     */
    String[] getSupportedExtensions();
    
    /**
     * 템플릿을 렌더링하여 HTML 결과 반환
     */
    String render(String templatePath, Map<String, Object> model, HttpRequest request, HttpResponse response);
    
    /**
     * 뷰 엔진 초기화
     */
    void initialize();
    
    /**
     * 뷰 엔진 이름 반환 (로깅/디버깅용)
     */
    String getEngineName();
}
```

### 2. 내장 뷰 엔진 구현

#### SimpleTemplateEngine (기존 확장)
- `.html` 파일 지원
- `${변수}` 치환 기능
- 기존 로직을 ViewEngine 인터페이스로 래핑

#### MockThymeleafEngine
- `.thymeleaf`, `.th` 파일 지원
- Thymeleaf 문법 일부 시뮬레이션
- `th:text`, `th:if`, `th:each` 등 기본 속성 처리

#### MockMustacheEngine
- `.mustache` 파일 지원
- Mustache 문법 시뮬레이션
- `{{변수}}`, `{{#section}}` 등 기본 문법

#### MockJspEngine
- `.jsp` 파일 지원
- JSP 문법 일부 시뮬레이션
- `<%= %>`, `<% %>` 스크립틀릿 처리

### 3. ViewEngineRegistry 구현
```java
public class ViewEngineRegistry {
    private Map<String, ViewEngine> engines = new HashMap<>();
    private ViewEngine defaultEngine;
    
    public void registerEngine(ViewEngine engine);
    public ViewEngine getEngineForTemplate(String templatePath);
    public void setDefaultEngine(ViewEngine engine);
}
```

### 4. 통합 ViewResolver 확장
```java
public class IntegratedViewResolver implements ViewResolver {
    private ViewEngineRegistry engineRegistry;
    private String templatePrefix = "/templates/";
    private Map<String, String> engineMappings;
    
    @Override
    public View resolveViewName(String viewName) {
        // 1. 템플릿 파일 경로 결정
        // 2. 확장자에 따른 뷰 엔진 선택
        // 3. IntegratedView 객체 생성
    }
}
```

### 5. IntegratedView 구현
```java
public class IntegratedView implements View {
    private ViewEngine engine;
    private String templatePath;
    
    @Override
    public void render(Map<String, Object> model, HttpRequest request, HttpResponse response) {
        String result = engine.render(templatePath, model, request, response);
        // 응답 설정 및 출력
    }
}
```

## 테스트 시나리오

### 테스트 1: 기본 HTML 템플릿 (SimpleTemplateEngine)
```
GET /view/simple
→ templates/simple.html (기존 ${} 문법)
```

### 테스트 2: Thymeleaf 템플릿
```
GET /view/thymeleaf  
→ templates/thymeleaf.th
→ th:text, th:if 등 Thymeleaf 문법 처리
```

### 테스트 3: Mustache 템플릿
```
GET /view/mustache
→ templates/mustache.mustache  
→ {{}} 문법 처리
```

### 테스트 4: JSP 템플릿
```
GET /view/jsp
→ templates/jsp.jsp
→ <%= %> 스크립틀릿 처리
```

### 테스트 5: 다중 뷰 엔진 우선순위
```
GET /view/priority
→ 여러 확장자 템플릿이 있을 때 우선순위 적용
```

### 테스트 6: JSON 응답과 뷰 엔진 조합
```
GET /view/json (Accept: application/json)
→ JSON 뷰와 템플릿 뷰 선택 확인
```

### 테스트 7: 존재하지 않는 템플릿 처리
```
GET /view/nonexistent
→ 404 또는 기본 에러 템플릿 처리
```

### 테스트 8: 뷰 엔진별 성능 비교
```
→ 각 뷰 엔진의 렌더링 시간 측정
```

## 디렉토리 구조
```
src/winter/
├── view/
│   ├── engine/
│   │   ├── ViewEngine.java (인터페이스)
│   │   ├── ViewEngineRegistry.java
│   │   ├── SimpleTemplateEngine.java
│   │   ├── MockThymeleafEngine.java
│   │   ├── MockMustacheEngine.java
│   │   └── MockJspEngine.java
│   ├── IntegratedViewResolver.java
│   ├── IntegratedView.java
│   └── (기존 View 관련 클래스들)
├── controller/
│   └── ViewEngineController.java (테스트용)
└── templates/
    ├── view-simple.html
    ├── view-thymeleaf.th
    ├── view-mustache.mustache
    └── view-jsp.jsp
```

## 기대 효과
1. **확장성**: 새로운 뷰 엔진을 쉽게 추가 가능
2. **유연성**: 프로젝트 요구사항에 따른 뷰 엔진 선택
3. **호환성**: 기존 템플릿과 새로운 뷰 엔진의 공존
4. **성능**: 각 뷰 엔진의 특성에 맞는 최적화
5. **마이그레이션**: 단계적 뷰 엔진 전환 지원

## 향후 확장 방향
- 실제 Thymeleaf, Mustache 라이브러리 통합
- 템플릿 캐싱 및 성능 최적화
- 뷰 엔진별 설정 관리
- 인클루드/상속 등 고급 템플릿 기능
- 뷰 엔진 플러그인 시스템