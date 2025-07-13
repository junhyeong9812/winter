# 📘 11-MODEL-AND-VIEW-BINDING-IN-HTML.md

## 🧠 개요

컨트롤러는 이제 `ModelAndView` 객체를 반환합니다.  
이 객체에는 **논리 뷰 이름**과 **모델 데이터(Map 구조)**가 포함되며, 뷰는 HTML 파일로 렌더링됩니다.

Winter 프레임워크는 `ViewResolver`, `View` 인터페이스 구현을 통해 View 경로 매핑과 템플릿 치환을 단순화하였습니다.

---

## ✨ 도입 목적

| 목적                 | 설명 |
|----------------------|------|
| 컨트롤러 응답 구조 개선 | View 이름과 Model 데이터 분리 전달 |
| View 동적 렌더링 도입  | HTML 템플릿 파일에 데이터 바인딩 |
| 향후 템플릿 엔진 확장 기반 마련 | Thymeleaf, Mustache 등 도입 가능 |

---

## ✅ 주요 클래스 및 역할

### 📄 Controller.java

```java
public interface Controller {
    ModelAndView handle(HttpRequest request, HttpResponse response);
}
```

---

### 📄 ModelAndView.java

```java
public class ModelAndView {
    private final String viewName;
    private final Map<String, Object> model = new HashMap<>();

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void addAttribute(String key, Object value) {
        model.put(key, value);
    }
}
```

---

### 📄 SimpleViewResolver.java

```java
public class SimpleViewResolver implements ViewResolver {
    private final String prefix = "src/winter/templates/";
    private final String suffix = ".html";

    @Override
    public View resolveViewName(String viewName) {
        String fullPath = prefix + viewName + suffix;
        return new InternalResourceView(fullPath);
    }
}
```

---

### 📄 InternalResourceView.java

```java
public class InternalResourceView implements View {
    private final String path;

    public InternalResourceView(String path) {
        this.path = path;
    }

    @Override
    public void render(Map<String, Object> model) {
        try {
            String content = Files.readString(Paths.get(path));
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                content = content.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }
            System.out.println("Rendered View: \n" + content);
        } catch (IOException e) {
            System.out.println("❌ View Rendering Failed: " + path);
        }
    }
}
```

---

### 📄 hello.html (예시)

```html
<!-- src/winter/templates/hello.html -->
<!DOCTYPE html>
<html>
<head><title>Hello</title></head>
<body>
<h1 th:text="${message}">[default message]</h1>
</body>
</html>
```

---

## 🧪 테스트 결과

```text
WinterFramework Start
Rendered View: 
<!-- templates/hello.html -->
<!DOCTYPE html>
<html>
<head><title>Hello</title></head>
<body>
<h1 th:text="hello!winter!">[default message]</h1>
</body>
</html>
 HTTP Response 
status = 200
body = 

❌ View Rendering Failed: src\winter\templates\bye.html
 HTTP Response 
status = 200
body = 

 HTTP Response 
status = 404
body = 404 Not Found/invalid
```

---

## 🎯 효과 요약

- ✅ 컨트롤러에서 `ModelAndView`로 동적 데이터 전송
- ✅ ViewResolver가 논리 뷰 이름을 파일 경로로 해석
- ✅ HTML 내 `${}` 키워드를 실제 값으로 치환해 출력
- ✅ MVC 구조의 기본 구성 완성

---

## 🔜 다음 목표

- View → 실제 `HttpResponse`에 body로 출력하도록 확장
- 파일 렌더링 실패 시 예외 처리 및 fallback 메시지 처리
- 템플릿 파서 로직을 모듈화하여 분리 예정
