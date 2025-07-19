# 📘 View 흐름 요약 (11\~19)

## ✍️ 원본 정리

컨트롤러가 기본적으로 반환하는 형식에는 `Model`과 `View`라는 객체를 통해 논리적 뷰 이름과 모델 데이터가 포함되며, 뷰는 HTML 파일로 렌더링된다.

뷰 리졸버는 프리픽스를 통해 경로를 재정의하고 서픽스를 통해 확장자를 지정하여, `.html` 파일명을 따로 지정하지 않아도 **논리 이름만으로 View 접근이 가능**하도록 돕는다.

리소스 뷰는 실제 파일의 경로에서 HTML 컨텐츠를 읽어오고, `${}` 표현식을 통해 모델 내의 데이터를 치환하는 방식으로 작동한다. 이 방식이 **기본적인 모델 데이터 바인딩 처리 구조**이다.

이후 다음과 같은 발전이 이어졌다:

* `.html` 템플릿을 사용하여 정적 HTML 본문을 읽고, 치환하여 완성된 View를 응답하는 방식으로 확장됨
* 레거시는 `BufferedReader` 기반이었으나, 이를 개선하여 `Files.readString`과 `Map.EntrySet` 반복을 통해 구조화됨

이전에는 단일 키 형태(`name`, `city` 등)만을 모델에 넣을 수 있었으나, 이후 중첩 속성 표현(`${user.name}`, `${user.address.zipcode}` 등)을 처리하기 위해 정규 표현식과 매처 그룹을 통해 키 추출 후, 리플렉션을 통해 단계적으로 프로퍼티를 추출하는 구조로 진화함.

수동으로 모델 데이터를 주입하던 구조는, 이후 요청 파라미터에서 데이터를 자동 파싱하여 `Map<String, String>` 형태로 저장한 뒤, `setter` 메서드를 찾아 바인딩하는 구조로 변경되었다.

컨트롤러는 이제 요청에서 받은 데이터로 `User` 같은 객체를 생성해 모델로 반환하고, 템플릿은 중첩 객체의 getter를 호출해 중첩 속성 접근이 가능해졌다.

---

## ✅ 11\~19 단계 요약 정리

### ✅ \[11\~12] HTML 템플릿 렌더링

* InternalResourceView에서 실제 HTML 파일을 읽음
* `${key}` 형태 표현식을 HTML 내에서 모델 데이터로 치환
* Files API로 HTML 본문 전체를 읽고 `Map.EntrySet` 반복으로 처리
* 레거시는 BufferedReader 방식 → 현재는 Files.readString 기반

### ✅ \[13] 중첩 속성 표현식 파서

* `${user.name}`, `${user.address.zipcode}`와 같은 중첩 표현식 처리
* 정규 표현식으로 `${(.*?)}` 매칭 및 Reflection을 통한 객체 속성 탐색

### ✅ \[14] Request → DTO 자동 바인딩

* 요청 파라미터를 `Map<String, String>` 형태로 파싱
* setter 메서드를 찾아 DTO 객체에 자동 값 주입

### ✅ \[15] HTTP Method 구분 처리

* Dispatcher가 method + path로 핸들러 매핑
* GET, POST 등 메서드별로 별도 매핑 가능

### ✅ \[16] 중첩 객체 바인딩 + getter 체이닝

* 중첩 객체 구조 메모리 구성 후 getter 체이닝으로 값 접근
* `${user.address.zipcode}` → `user.getAddress().getZipcode()` 호출

### ✅ \[17] 정적 리소스 핸들링 분리

* `/static/` 요청을 StaticResourceHandler에서 처리
* 정적 파일 직접 응답 본문에 담아 반환

### ✅ \[18] HTTP Header 처리

* Request/Response 객체에 Header 추가하여 MIME 타입 설정
* Dispatcher가 Content-Type 헤더 기반으로 처리 결정

### ✅ \[19] Exception Resolver 처리

* 예외 발생 시 JSON 형태의 표준 오류 응답 구성 (status, message 포함)

---

## 📌 핵심 흐름 요약

1. Controller는 Model + View 또는 DTO 객체를 반환
2. ViewResolver가 논리 이름을 실제 경로로 변환
3. View 객체가 HTML을 읽고 모델 데이터를 치환하여 응답 구성
4. 정적 리소스 요청은 Static 핸들러 처리, MIME 타입은 Header 기반 설정
5. ExceptionResolver가 JSON 형태 오류 응답 제공

---

## 🧠 도입 효과 요약

| 도입 요소              | 주요 효과                                       |
| ------------------ | ------------------------------------------- |
| HTML 템플릿 렌더링       | 정적 파일 기반 View 렌더링 처리 구조 확립                  |
| 중첩 속성 표현식          | 객체 내부 속성에 대한 동적 접근 가능                       |
| Request → DTO 바인딩  | 사용자 요청 데이터를 직접 객체로 매핑 가능                    |
| HTTP Method 분기 처리  | RESTful한 요청 핸들링 구조 도입                       |
| 정적 리소스 분리 처리       | HTML/CSS/JS 등 정적 자원의 효율적 서빙 구조 확립           |
| Header 처리          | MIME 타입 제어 및 요청/응답 헤더 활용 가능                 |
| Exception Resolver | 통일된 예외 처리 응답 포맷 제공 (status, message 명시적 제공) |

---

## 🛠 잘못된 이해 또는 누락된 부분 보완 설명

### ✅ 1. View 렌더링 형식의 다양성

**기존 표현:** “뷰는 html파일로 렌더링이 되는 구조이다”

**보완 설명:**
View는 HTML 외에도 JSP, JSON, XML 등 다양한 형식으로 렌더링 가능하며, View 구현체에 따라 응답 형태가 달라질 수 있습니다.

### ✅ 2. 중첩 속성 표현식 처리 시 Null 객체 초기화 전략

**기존 표현:** “getter를 호출하여 순차적으로 탐색”

**보완 설명:**
중첩 객체 탐색 중 중간 객체가 null일 경우 NullPointerException 발생 가능성이 있으므로, 자동 초기화나 예외 처리 전략이 필요합니다.

### ✅ 3. RequestParam → 객체 바인딩 시 타입 변환 필요

**기존 표현:** “setter 메소드 찾아서 인보크로 값을 넣는다”

**보완 설명:**
setter 호출 전 문자열을 정수, 날짜 등으로 변환하는 과정이 필요하며, 타입 변환기(converter)를 사용하는 것이 일반적입니다.

### ✅ 4. ExceptionResolver는 상태 코드 및 메시지까지 응답 구성

**기존 표현:** “예외처리된 데이터를 담아서 보낼 수 있게 됨”

**보완 설명:**
예외 발생 시 HTTP 상태 코드, 응답 메시지, 에러 코드 등을 명시적으로 설정하여 클라이언트에게 구조화된 오류 정보를 전달합니다.

### ✅ 5. 헤더 타입 지정의 MIME Type 설정 중요성

**기존 표현:** “헤더 기능도 추가하여 내부데이터에 대한 타입도 지정”

**보완 설명:**
Content-Type 헤더를 통한 MIME Type 설정은 클라이언트가 데이터를 파싱할 때 필수적인 요소로, 단순한 헤더 지정이 아니라 MIME 협상의 의미를 충분히 이해해야 합니다.

---

## 🛠 다음 목표

* JSON View 구현
* Interceptor/Filter 도입
* 파일 업로드 및 바이너리 응답 처리
