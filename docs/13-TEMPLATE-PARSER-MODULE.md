# 13. Template Parser Module (중첩 속성 바인딩)

## 📌 도입 목적

기존 View 렌딩 방식은 `${key}` 형태로 1단계 까지의 단순한 문자열만 치환할 수 있었습니다. 가령 `${user.name}`처럼 객체 내부의 속성을 뷰 템플리트에서 표현하는 경우가 복잡해진 시스템에서 보호되기 때문에, 이 단계에서는 객체.속성 형태의 그림자를 인식하고 모델 객체를 통해 실제 값으로 치환할 수 있도록 템플리트 파서 모듈을 구현합니다.

---

## ✅ 주요 변경 사항

| 항목         | 변경 전                            | 변경 후                             |
| ---------- | ------------------------------- | -------------------------------- |
| 템플리트 치환 방식 | `${key}` 단순 문자열 키만 인식           | `${object.field}` 중차 속성까지 파싱 지원  |
| 치환 처리 위치   | `InternalResourceView` 내부 직접 처리 | `parseTemplate()` 메서드로 파싱 로직 모듈화 |
| 리플렉션 사용 여부 | ❌ 사용 안 함                        | ✅ 리플렉션을 활용해 getter 호출 방식으로 처리    |

---

## 🛡️ InternalResourceView 개선 코드 요약

```java
@Override
public void render(Map<String, Object> model, HttpResponse response) {
    try {
        File file = new File(path);
        String content = Files.readString(file.toPath());

        // 템플리트 파서 호출
        String rendered = parseTemplate(content, model);

        response.setStatus(200);
        response.setBody(rendered);
    } catch (IOException e) {
        response.setStatus(500);
        response.setBody("View Rendering Failed: " + path);
    }
}
```

### 템플리트 파서 로직

가령 `${user.name}` 같은 중차 속성을 지원하기 위해, 정규식으로 `${...}` 표현을 추출하고 `.`을 기준으로 객체 필드를 탐색하여 값을 찾습니다.

```java
private String parseTemplate(String content, Map<String, Object> model) {
    Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_.]+)}");
    Matcher matcher = pattern.matcher(content);
    StringBuffer sb = new StringBuffer();

    while (matcher.find()) {
        String expression = matcher.group(1); // e.g. user.name
        String[] parts = expression.split("\\.");
        Object value = model.get(parts[0]);

        for (int i = 1; i < parts.length && value != null; i++) {
            value = getPropertyValue(value, parts[i]);
        }

        matcher.appendReplacement(sb, value != null ? Matcher.quoteReplacement(value.toString()) : "");
    }

    matcher.appendTail(sb);
    return sb.toString();
}
```

### 클래스 특성 값 가져오기 (Reflection)

```java
private Object getPropertyValue(Object obj, String propertyName) {
    try {
        String getter = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        Method method = obj.getClass().getMethod(getter);
        return method.invoke(obj);
    } catch (Exception e) {
        return null;
    }
}
```

---

## 📃 예시 결과

```html
<!-- register.html -->
<p>Name: ${user.name}</p>
<p>Email: ${user.email}</p>
```

모델에 `User` 객체가 `name = 콤피`, `email = campi@example.com`가 들어있다면, 다음것같이 치환됩니다:

```html
<p>Name: 콤피</p>
<p>Email: campi@example.com</p>
```

---

## 📜 정리

* `${key}` → 단순 값 치환
* `${object.field}` → 객체 내부 속성 탐색 후 치환
* 후일에는 `${object.field1.field2}` 같은 격감 탐색과 보호적 치환은 가능

---

## 🛠️ 다음 목표

* `@ModelAttribute`와 같은 컨트롤러 단에서 자동 바인딩 기능 연동
* 템플리트 파서 모듈을 별도 클래스로 추출해 테스트 및 재사용 가능하도록 개선
