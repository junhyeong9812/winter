# 📘 14-REQUEST-PARAM-PARSING.md

## 📌 도입 목적

기존까지는 컨트롤러에 전달되는 데이터를 수동으로 Model에 넣는 방식이었다. 이번 단계에서는 다음과 같은 기능을 도입하여 **실제 웹 요청 흐름에 가까운 구조**를 구현하였다.

* `/register?name=Jun&email=jun@test.com` 형식의 요청 처리
* 쿼리 파라미터를 자동으로 자바 객체(DTO)에 바인딩
* 뷰 템플릿에서 중첩 표현식(`${user.name}`)을 통한 렌더링

---

## ✅ 구현 목표

| 항목                        | 설명                                         |
| ------------------------- | ------------------------------------------ |
| `HttpRequest` 개선          | 쿼리 파라미터 자동 파싱 기능 추가 (`?key=value` 지원)      |
| `ModelAttributeBinder` 도입 | 리플렉션 기반 자동 setter 호출로 DTO 바인딩              |
| `RegisterController` 추가   | `UserForm`을 받아 Model에 등록, 뷰 반환             |
| `register.html` 템플릿 작성    | `${user.name}`, `${user.email}` 중첩 표현식 테스트 |

---

## 🧱 핵심 클래스 요약

### 📄 HttpRequest.java

```java
public HttpRequest(String rawPath) {
    String[] parts = rawPath.split("\\?", 2);
    this.path = parts[0];
    ... // queryString 파싱하여 parameters Map에 저장
}
```

* `/register?name=Jun&email=jun@test.com` → path: "/register", parameters: `{name=Jun, email=...}`

---

### 📄 ModelAttributeBinder.java

```java
for (Method method : clazz.getMethods()) {
    if (isSetter(method)) {
        String paramName = getParamNameFromSetter(method.getName());
        String paramValue = request.getParameter(paramName);
        if (paramValue != null) method.invoke(instance, paramValue);
    }
}
```

* setter 메서드(setXxx)를 자동으로 찾아 request 파라미터로 값 주입

---

### 📄 RegisterController.java

```java
public ModelAndView handle(HttpRequest request, HttpResponse response) {
    UserForm form = ModelAttributeBinder.bind(request, UserForm.class);
    ModelAndView mv = new ModelAndView("register");
    mv.addAttribute("user", form);
    return mv;
}
```

---

### 📄 register.html

```html
<p>Name: ${user.name}</p>
<p>Email: ${user.email}</p>
```

* 중첩 표현식 `${object.field}` 파싱은 기존 parseTemplate()에서 지원

---

## 🔁 전체 흐름 정리

1. `/register?name=Jun&email=jun@test.com` 요청 전달
2. `HttpRequest`가 쿼리 파라미터 자동 파싱
3. `ModelAttributeBinder`가 UserForm 객체에 값 주입
4. `RegisterController`가 user를 모델에 등록한 `ModelAndView` 반환
5. ViewResolver가 템플릿 파일을 View로 변환
6. View의 `render()`가 `${user.name}`, `${user.email}`을 치환하여 응답 구성

---

## 🧪 출력 예시

```
 HTTP Response 
status = 200
body = <!DOCTYPE html>
<html>
<head><title>Register</title></head>
<body>
<h2>Registration Info</h2>
<p>Name: Jun</p>
<p>Email: jun@test.com</p>
</body>
</html>
```

---

## 🔍 바인딩 구현 방식 비교

| 비교 항목        | 기존 (GPT 예시)   | 현재 구조 (사용자 구현)          |
| ------------ | ------------- | ----------------------- |
| Setter 접근 방식 | DTO 필드명 직접 접근 | Public setter 메서드 자동 탐색 |
| 유연성          | 낮음            | 높음 (모든 setter 자동 처리)    |
| 타입 지원        | String만 수동 대응 | 향후 다양한 타입 확장 용이         |
| 구조 정리        | 단순함           | 네이밍 룰 기반 규칙적 바인딩        |

---

## ✅ 효과 요약

* ✅ Dispatcher 흐름에 실질적인 Request 파라미터 전달 구현
* ✅ DTO 객체 자동 생성 및 데이터 주입 처리
* ✅ View에서 중첩 표현식으로 직관적인 템플릿 치환
* ✅ SRP에 기반한 Controller, View, Model 구조 완성
