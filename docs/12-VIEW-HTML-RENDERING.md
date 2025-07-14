# 12. HTML View 렌더링 처리

## 📌 도입 목적

기존 `View` 추상화는 단순히 `System.out.println()`으로 뷰 경로와 모델 데이터를 출력하는 수준에 머물러 있었습니다. 이제부터는 실제 HTML 파일을 읽어와 클라이언트에게 HTML 콘텐츠를 응답 본문으로 제공하는 방식으로 개선합니다.

---

## ✅ 주요 변경점

| 항목       | 변경 전                   | 변경 후                                        |
| -------- | ---------------------- | ------------------------------------------- |
| 뷰 렌더링 방식 | 콘솔 로그 출력               | 실제 HTML 파일을 읽어 응답 본문 구성                     |
| 응답 처리    | `System.out.println()` | `HttpResponse.setStatus()` + `setBody()` 사용 |
| 모델 적용 방식 | 콘솔에 Map 출력             | `${key}` 플레이스홀더를 HTML 내에서 동적으로 치환           |

---

## 🧱 기본 구현 방식: InternalResourceView

```java
public class InternalResourceView implements View {

    private final String path;

    public InternalResourceView(String path) {
        this.path = path;
    }

    @Override
    public void render(Map<String, Object> model, HttpResponse response) {
        try {
            File file = new File(path);
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);

            for (Map.Entry<String, Object> entry : model.entrySet()) {
                content = content.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }

            response.setStatus(200);
            response.setBody(content);
        } catch (IOException e) {
            response.setStatus(500);
            response.setBody("View Rendering Failed: " + path);
        }
    }
}
```

HTML 내 `${message}`와 같은 플레이스홀더를 모델 값으로 치환

`src/winter/templates/hello.html` 같은 실제 파일을 읽어 응답 본문에 포함

🧾 예시 템플릿 (hello.html)

```html
<!-- src/winter/templates/hello.html -->
<!DOCTYPE html>
<html>
<head><title>Hello</title></head>
<body>
<h1>${message}</h1>
</body>
</html>
```

---

## 🕰️ 레거시 방식: InternalResourceViewLegacy

```java
public class InternalResourceViewLegacy implements View {

    private final String path;

    public InternalResourceViewLegacy(String path) {
        this.path = path;
    }

    @Override
    public void render(Map<String, Object> model, HttpResponse response) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

            String rendered = content.toString();
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                rendered = rendered.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }

            response.setStatus(200);
            response.setBody(rendered);
        } catch (IOException e) {
            response.setStatus(500);
            response.setBody("View Rendering Failed (Legacy): " + path);
        }
    }
}
```

`Files.readString()`을 지원하지 않는 구버전 호환을 위한 `BufferedReader` 방식

내부적으로는 동일하게 `${key}` 플레이스홀더를 모델 값으로 대체

---

## 🔄 두 방식 비교 요약

| 항목          | 최신 방식 (기본)           | 레거시 방식                             |
| ----------- | -------------------- | ---------------------------------- |
| 사용 API      | `Files.readString()` | `BufferedReader + FileInputStream` |
| Java 버전 의존성 | Java 11 이상           | Java 8 이상 가능                       |
| 코드 가독성      | 높음                   | 다소 복잡                              |
| 사용 목적       | 기본 렌더링 구현 기준점        | 하위 호환 또는 학습 비교용                    |

---

## 📌 향후 확장 계획

* `${user.name}` 형태의 중첩 프로퍼티 파싱 지원
* Thymeleaf 또는 JSP 포워딩 기능 구현 실험
* ViewResolver에 View 타입별 선택 전략 도입 (ex: HTML vs JSON 등)
