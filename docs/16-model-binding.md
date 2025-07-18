## 📘 16. MODEL → VIEW 중첩 속성 바인딩

### 🧠 개요

기존 템플릿 렌더링 방식은 `${user}`처럼 단일 키에만 대응할 수 있었습니다.
이번 단계에서는 `${user.name}`, `${user.address.city}` 등 **중첩 속성 표현식**을 지원하기 위해 `Java Reflection` 기반으로 뷰 처리 구조를 개선했습니다.

---

### ✨ 도입 목적

| 목적               | 설명                                                     |
| ---------------- | ------------------------------------------------------ |
| 표현식 확장           | `${user.name}`, `${user.address.city}` 같은 중첩 표현식 처리 지원 |
| MVC 구조 정교화       | View가 객체 내부 구조까지 렌더링할 수 있도록 개선                         |
| 템플릿 엔진 구조 확장성 확보 | 향후 조건문/반복문 등 템플릿 엔진 고도화를 위한 기반 마련                      |

---

### ✅ 주요 변경 사항

#### 1. `InternalResourceView.java`

```java
// 중첩 표현식 파싱 및 객체 속성 접근 방식 추가
Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_.]+)}");
// ${user.address.city} 같은 표현식 처리
// reflection 기반 getter 접근: getAddress() -> getCity()
```

* `resolvePlaceholders`에서 `.split(".")`로 객체 경로를 분해하고, `getProperty()`로 각 단계의 getter를 호출
* `getProperty()`는 `getXxx()`, `isXxx()`를 순차적으로 탐색

#### 2. `User`, `Address` 클래스 생성

```java
public class User {
    private String name;
    private Address address;
    // getter/setter
}

public class Address {
    private String city;
    private String zipcode;
    // getter/setter
}
```

#### 3. `UserController.java`

```java
public class UserController implements Controller {
    public ModelAndView handle(HttpRequest request, HttpResponse response) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(405);
            response.setBody("Method Not Allowed: " + request.getMethod());
            return null;
        }

        User user = new User();
        user.setName(request.getParameter("name"));

        Address addr = new Address();
        addr.setCity(request.getParameter("city"));
        addr.setZipcode(request.getParameter("zipcode"));
        user.setAddress(addr);

        ModelAndView mv = new ModelAndView("user");
        mv.addAttribute("user", user);
        return mv;
    }
}
```

#### 4. `user.html`

```html
<!DOCTYPE html>
<html>
<head><title>User Info</title></head>
<body>
<h2>User Detail</h2>
<p>Name: ${user.name}</p>
<p>City: ${user.address.city}</p>
<p>Zipcode: ${user.address.zipcode}</p>
</body>
</html>
```

---

### 🧪 테스트 로그

```http
/register?name=Jun&city=Seoul&zipcode=12345

HTTP Response
status = 200
body =
<!DOCTYPE html>
<html>
<head><title>User Info</title></head>
<body>
<h2>User Detail</h2>
<p>Name: Jun</p>
<p>City: Seoul</p>
<p>Zipcode: 12345</p>
</body>
</html>
```

---

### 🎯 효과 요약

| 항목        | 내용                                     |
| --------- | -------------------------------------- |
| 표현력 향상    | 단일 키 → 중첩 객체 표현으로 View 렌더링 가능          |
| 구조 확장성 확보 | 객체 내부까지 탐색 가능한 구조 → 템플릿 엔진 기능 확장 기반 마련 |

---

### 🔜 다음 목표

**17단계: 정적 리소스 처리 (예: /static/style.css, /favicon.ico)** 를 지원하여
프론트에서 사용하는 정적 파일 서빙 기능을 프레임워크에 추가합니다.
