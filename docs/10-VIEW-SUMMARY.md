# 📘 Dispatcher \~ View 흐름 요약 (6\~9)

## ✍️ 원본 정리

```
기존의 구조에서는
Controller나 어댑터에서 코드에 대한 판별을 했지만
ViewResolver라는 클래스를 통해
view단으로 파일을 호출하도록 정적 파일 처리의 초기버전과 같네? 그리고
이게 결국 그냥 컨트롤러와 레스트 컨트롤러의 초기 분기점의 시작과 같은 느낌이고

이때 spring에서 그냥 .html을 빼고 파일명만 넣어도 되었던 이유가 이렇게 경로를 처리하고 있었기 때문이구나?

그리고 그 다음 InternalResourceView에서 각각의 패스에 따른 동작 render 함수들을 가지고 우선 경로를 보도록만 했지만 결국 역할을 나눠서 여기서 렌더링을 하는 실제 기능을 구현하게 되는 거구나

다음 이제 모델과 뷰 이건
뷰가 있다면 뷰의 이름과 모델은 해시맵으로 키밸류 형태로 화면에 필요한 정보를 가져가도록 만드는 거구나.

이래서 기존에 모델에 attribute로 객체를 넣는 거구나?
그리고 이제 컨트롤러에서는
모델과 뷰가 반환값이 되는 거구나?

그리고 각 컨트롤러에서 뷰 이름과 메시지를 추가하고
그러면 결국 컨트롤러에서 모델과 뷰 처리를 하고 이후
Dispatcher에서 ViewResolver로 해당 데이터를 받으면 이때 인터셉터를 해서 view 화면 객체를 반환하는 구조가 되는구나?
하지만 여기까지는 그냥 파라미터와 뷰 경로를 그대로 넣었다면
이번에 Request와 Response를 만들어서 이러한 객체를 Request와 Response라는 클래스 형태로 만들어서 역할을 분리한 거구나.

기존에는 그냥 문자열 파라미터였다면 이젠 하나의 객체가 되어버린 거네?
그리고 send라는 함수를 통해 반환값을 보여주는 거고 나중에는 실제로 이걸로 보내겠구나?
```

---

## ✅ 6\~9단계 요약 정리

### ✅ \[6] ViewResolver 도입

* 기존 구조에서는 핸들러가 직접 System.out을 출력했음
* ViewResolver는 **논리적인 View 이름(String)** 을 받아 실제 물리 경로(View 객체)로 변환
* `"hello"` → `/views/hello.html` 과 같은 변환을 담당

✔️ 정적 파일 처리의 초기 형태 → 이후 템플릿 뷰로 발전

### ✅ \[7] InternalResourceView (View 구현체)

* ViewResolver가 반환한 View 객체의 `render(model)` 메서드를 통해 실제 출력 수행
* 현재는 System.out으로 경로 출력하지만,
* 구조상 향후 HTML 렌더링이나 JSP forward 등으로 확장 가능

✔️ 역할 분리: View 이름 → View 객체 → render() 실행

### ✅ \[8] ModelAndView 구조

* 컨트롤러는 더 이상 void handle() 대신 `ModelAndView` 를 반환
* Model: `Map<String, Object>` → 화면에서 사용될 데이터
* ViewName: 문자열("hello")

✔️ Spring MVC의 반환 구조와 유사함: **컨트롤러가 Model + View 반환 → Dispatcher가 처리**

### ✅ \[9] Request / Response 객체 도입

* Controller가 단순 path를 받던 구조 → Request/Response 객체로 추상화
* ex) `controller.handle(request, response)` 형태
* Response는 `send()` 메서드를 통해 응답 전송 (현재는 System.out, 향후는 실제 OutputStream)

✔️ SRP 기반 책임 분리:

* Request: 요청 정보 보관 (ex: 파라미터)
* Response: 응답 작성 책임 전담

---

## 📌 핵심 흐름 요약

1. Dispatcher는 HandlerMapping으로 핸들러 검색
2. HandlerAdapter로 실행 위임 → ModelAndView 반환
3. ViewResolver가 ViewName → View 객체로 변환
4. View\.render(model, request, response) 실행
5. Response.send()로 출력

---

## 🧠 도입 효과 요약

| 도입 요소                | 주요 효과                            |
| -------------------- | -------------------------------- |
| ViewResolver         | View 이름과 경로 분리 → 깔끔한 뷰 관리 구조     |
| InternalResourceView | render() 메서드 분리 → 출력 책임 단일화      |
| ModelAndView         | 데이터 + 뷰 동시 전달 → 컨트롤러 역할 명확화      |
| Request/Response     | 요청/응답의 구조적 관리 → 향후 HTTP 확장 기반 마련 |

---

## 🛠 수정 및 보완 포인트 정리

### 🔹 1. ViewResolver 역할 명확화
- ❗ 기존 표현: “정적파일 처리의 초기버전과 같네”
- ✅ 보완 설명:  
  ViewResolver는 단순 정적 파일 경로 매핑이 아닌, **논리적인 뷰 이름 → 실제 View 객체 변환** 역할을 수행함.  
  `RestController`와의 분기보다는 **정적 vs 동적 뷰 처리 책임 분리**로 보는 것이 더 정확함.

---

### 🔹 2. InternalResourceView는 단순 경로지정자가 아님
- ❗ 기존 표현: “경로를 보도록만 했지만 결국 랜더링을 하는 기능을 구현”
- ✅ 보완 설명:  
  `InternalResourceView`는 실제로 `.forward()` 등의 방식으로 **JSP 등의 뷰 파일을 렌더링**하는 책임이 있음.  
  단순한 경로지정자가 아닌, **뷰 렌더링을 수행하는 객체(View Renderer)** 로 봐야 함.

---

### 🔹 3. ModelAndView의 의미 보완
- ❗ 기존 표현: “컨트롤러에서 뷰이름과 메세지를 추가”
- ✅ 보완 설명:  
  `ModelAndView`는 단순 메시지가 아니라, **화면에 전달할 key-value 기반 데이터(Map)** 를 담는 객체임.  
  메시지 수준이 아니라 **뷰 렌더링에 필요한 모든 데이터를 포함하는 구조**임을 명확히 하자.

---

### 🔹 4. MyRequest / MyResponse 객체의 목적 명확히
- ❗ 기존 표현: “문자열 파라미터였다면 이젠 하나의 객체가 된 거네?”
- ✅ 보완 설명:  
  `MyRequest`, `MyResponse`는 **서블릿 API에 의존하지 않는 추상화된 래퍼 객체**임.  
  테스트 가능성과 프레임워크 확장을 고려해 설계된 구조라는 점을 덧붙이면 더 좋음.

---

### 🔹 5. ViewResolver → View 흐름에서 인터셉터 언급은 시기상조
- ❗ 기존 표현: “뷰리졸버로 해당 데이터를 받으면 이떄 인터셉터를 해서 view 객체를 반환하는 구조”
- ✅ 보완 설명:  
  이 시점에서는 **Interceptor는 등장하지 않음.**  
  Dispatcher는 ViewResolver에게 뷰 이름을 넘기고, **ViewResolver가 View 객체를 반환**한 뒤  
  View가 직접 render(request, response, model)을 실행하는 구조임.

---


## 🛠 다음 목표

* View 내부에 템플릿 처리 기능 도입 (ex: 동적 메시지 출력)
* Request에 파라미터 전달 구조 추가
* Response 객체 기반 파일/JSON 응답 처리로 확장
