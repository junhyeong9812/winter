# 📘 8-MODEL-AND-VIEW\.md

## 👀 ModelAndView 이란?

Controller가 실행 결과로 반환하는 결과 객체로,
"뷰 이름" (viewName) 과 "모델 데이터" (model)를 합치게 가지는 형태.

Spring MVC의 `ModelAndView`과 같이, Dispatcher가 이 결과를 받아 ViewResolver → View → View\.render(model)로 전달한다.

---

## ✅ 진행 되어났는 요소

1. `ModelAndView` 클래스 생성

    * viewName을 저장하고, Map\<String, Object>로 model 저장

2. `Controller` 인터페이스가 `ModelAndView handle()` 으로 수정

3. `HelloController`, `ByeController`는 뷰 이름 + 메세지 데이터 추가

4. Dispatcher가 Controller가 반환한 `ModelAndView`를 가지고:

    * ViewResolver로 뷰 이름 → View 발금
    * View\.render(model) 으로 출력 결과 보여주기

5. `View`는 `render(Map<String, Object>)`로 확장

    * 현재는 컨솔에 데이터 출력 수준 (템플릿 처리 X)

---

## 파일 구조

```
winter/
├── dispatcher/
│   ├── HelloController.java // ModelAndView 반환
│   ├── ByeController.java
│   └── Controller.java      // handle() 수정
├── view/
│   ├── ModelAndView.java
│   ├── View.java              // render(model)
│   └── InternalResourceView.java // model 보여주기
```

---

## 테스트 결과

```text
WinterFramework Start
View Rendering:/views/hello.html
Model Data:
 message=hello from Winter Framework!
View Rendering:/views/bye.html
Model Data:
 message=Goodbye from winter Framework
404 Not Found: /invalid
```

---

## ✈️ 다음 단계

* Model 값을 패스트가 보여주고 바위기하는 구조를 구현
* `HttpServletRequest`, `HttpServletResponse` 전달 구조 바위기
* `Model` 구조를 더 반드트워 다양한 파란 데이터 추가
