package winter.view.engine; // 뷰 엔진 관련 패키지 선언

import winter.http.HttpRequest; // HTTP 요청 객체 임포트
import winter.http.HttpResponse; // HTTP 응답 객체 임포트

import java.io.BufferedReader; // 파일 읽기를 위한 BufferedReader 임포트
import java.io.FileReader; // 파일 읽기를 위한 FileReader 임포트
import java.io.IOException; // 입출력 예외 처리를 위한 IOException 임포트
import java.time.LocalDateTime; // 현재 날짜/시간을 위한 LocalDateTime 임포트
import java.time.format.DateTimeFormatter; // 날짜 포맷팅을 위한 DateTimeFormatter 임포트
import java.util.Map; // 맵 자료구조 임포트
import java.util.regex.Matcher; // 정규식 매칭을 위한 Matcher 임포트
import java.util.regex.Pattern; // 정규식 패턴을 위한 Pattern 임포트

/**
 * Mock JSP 엔진
 * JSP(JavaServer Pages)의 기본 기능을 시뮬레이션
 */
public class MockJspEngine implements ViewEngine { // ViewEngine 인터페이스를 구현하는 Mock JSP 엔진 클래스 정의

    // 지원하는 파일 확장자 배열 (.jsp)
    private static final String[] SUPPORTED_EXTENSIONS = {"jsp"};

    // JSP 패턴들 정의
    // <%= 표현식 %> 형태를 찾기 위한 정규식
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("<%=\\s*(.*?)\\s*%>");
    // <% 스크립틀릿 %> 형태를 찾기 위한 정규식 (DOTALL: 줄바꿈 포함)
    private static final Pattern SCRIPTLET_PATTERN = Pattern.compile("<%\\s*(.*?)\\s*%>", Pattern.DOTALL);
    // <%@ 지시자 %> 형태를 찾기 위한 정규식
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("<%@\\s*(.*?)\\s*%>");
    // <%! 선언부 %> 형태를 찾기 위한 정규식
    private static final Pattern DECLARATION_PATTERN = Pattern.compile("<%!\\s*(.*?)\\s*%>", Pattern.DOTALL);

    @Override
    public String[] getSupportedExtensions() { // 지원하는 확장자 반환 메서드 구현
        return SUPPORTED_EXTENSIONS.clone(); // 배열의 복사본 반환 (불변성 보장)
    }

    @Override
    public String render(String templatePath, Map<String, Object> model, // 템플릿 렌더링 메서드 구현 시작
                         HttpRequest request, HttpResponse response) throws Exception { // 예외 던질 수 있음

        // JSP 엔진 렌더링 시작 로그 출력
        System.out.println("MockJSP 엔진으로 템플릿 렌더링: " + templatePath);

        String template = readTemplateFile(templatePath); // 템플릿 파일을 읽어서 문자열로 저장

        String result = processJspTemplate(template, model, request, response); // JSP 문법 처리

        return result; // 최종 처리된 HTML 문자열 반환
    }

    @Override
    public void initialize() { // 뷰 엔진 초기화 메서드 구현
        System.out.println("MockJspEngine 초기화 완료"); // 초기화 완료 로그 출력
    }

    @Override
    public String getEngineName() { // 엔진 이름 반환 메서드 구현
        return "MockJspEngine"; // Mock JSP 엔진 이름 반환
    }

    @Override
    public int getPriority() { // 우선순위 반환 메서드 구현
        return 30; // 중간 우선순위 30 반환
    }

    private String readTemplateFile(String templatePath) throws IOException { // 템플릿 파일 읽기 메서드 정의
        StringBuilder content = new StringBuilder(); // 파일 내용을 담을 StringBuilder 생성

        // try-with-resources로 자동 리소스 관리 (파일 자동 닫기)
        try (BufferedReader reader = new BufferedReader(new FileReader(templatePath))) {
            String line; // 한 줄씩 읽을 변수 선언
            while ((line = reader.readLine()) != null) { // 파일의 끝까지 한 줄씩 읽기
                content.append(line).append("\n"); // 읽은 줄을 StringBuilder에 추가하고 줄바꿈 추가
            }
        }

        return content.toString(); // 최종 문자열 반환
    }

    /**
     * JSP 템플릿 처리
     */
    private String processJspTemplate(String template, Map<String, Object> model, // JSP 템플릿 처리 메서드 정의
                                      HttpRequest request, HttpResponse response) {
        String result = template; // 결과를 담을 변수에 원본 템플릿 할당

        result = processDirectives(result); // 1. Directives 처리 (<%@ ... %>)

        result = processDeclarations(result, model); // 2. Declarations 처리 (<%! ... %>)

        result = processScriptlets(result, model, request, response); // 3. Scriptlets 처리 (<% ... %>)

        result = processExpressions(result, model, request, response); // 4. Expressions 처리 (<%= ... %>)

        return result; // 모든 처리가 완료된 결과 반환
    }

    /**
     * JSP Directives 처리 (<%@ page ... %>)
     */
    private String processDirectives(String template) { // 지시자 처리 메서드 정의
        // 간단한 구현: 대부분의 directive는 제거
        Matcher matcher = DIRECTIVE_PATTERN.matcher(template); // 템플릿에서 <%@ %> 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 지시자를 순회
            String directive = matcher.group(1).trim(); // 지시자 내용 추출

            // 특별한 처리가 필요한 directive가 있다면 여기서 처리
            // 현재는 단순히 제거
            matcher.appendReplacement(result, ""); // 지시자를 빈 문자열로 교체 (제거)
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * JSP Declarations 처리 (<%! ... %>)
     */
    private String processDeclarations(String template, Map<String, Object> model) { // 선언부 처리 메서드 정의
        // 간단한 구현: 선언부는 제거 (실제로는 클래스 멤버로 추가되어야 함)
        Matcher matcher = DECLARATION_PATTERN.matcher(template); // 템플릿에서 <%! %> 패턴 찾기
        return matcher.replaceAll(""); // 모든 선언부를 빈 문자열로 교체 (제거)
    }

    /**
     * JSP Scriptlets 처리 (<% ... %>)
     */
    private String processScriptlets(String template, Map<String, Object> model, // 스크립틀릿 처리 메서드 정의
                                     HttpRequest request, HttpResponse response) {
        Matcher matcher = SCRIPTLET_PATTERN.matcher(template); // 템플릿에서 <% %> 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 스크립틀릿을 순회
            String code = matcher.group(1).trim(); // 스크립틀릿 코드 추출

            // 간단한 Java 코드 실행 시뮬레이션
            String output = executeScriptlet(code, model, request, response); // 스크립틀릿 실행
            matcher.appendReplacement(result, Matcher.quoteReplacement(output)); // 스크립틀릿을 실행 결과로 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * JSP Expressions 처리 (<%= ... %>)
     */
    private String processExpressions(String template, Map<String, Object> model, // 표현식 처리 메서드 정의
                                      HttpRequest request, HttpResponse response) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(template); // 템플릿에서 <%= %> 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 표현식을 순회
            String expression = matcher.group(1).trim(); // 표현식 코드 추출

            // 표현식 평가
            Object value = evaluateExpression(expression, model, request, response); // 표현식 평가
            String output = value != null ? value.toString() : ""; // 값이 있으면 문자열로 변환, 없으면 빈 문자열
            matcher.appendReplacement(result, Matcher.quoteReplacement(output)); // 표현식을 평가 결과로 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * Scriptlet 코드 실행 시뮬레이션
     */
    private String executeScriptlet(String code, Map<String, Object> model, // 스크립틀릿 실행 메서드 정의
                                    HttpRequest request, HttpResponse response) {

        // 간단한 제어 구조 시뮬레이션
        if (code.contains("if") && code.contains("!=") && code.contains("null")) { // if (변수 != null) 형태 확인
            // if (variable != null) 형태 처리
            String varName = extractVariableFromCondition(code); // 조건문에서 변수명 추출
            if (varName != null && model.get(varName) != null) { // 변수가 존재하고 null이 아닌지 확인
                return "<!-- if condition true -->"; // 조건이 참인 경우 주석 반환
            } else {
                return "<!-- if condition false -->"; // 조건이 거짓인 경우 주석 반환
            }
        }

        if (code.contains("for") || code.contains("while")) { // 반복문 확인
            return "<!-- loop start -->"; // 반복문 시작 주석 반환
        }

        // 기본적으로는 빈 문자열 반환 (실제 실행 결과가 아님)
        return "";
    }

    /**
     * 표현식 평가
     */
    private Object evaluateExpression(String expression, Map<String, Object> model, // 표현식 평가 메서드 정의
                                      HttpRequest request, HttpResponse response) {

        // 내장 객체들 처리
        if ("request.getParameter(\"name\")".equals(expression)) { // 요청 파라미터 접근
            return request.getParameter("name"); // 요청에서 name 파라미터 반환
        }

        if ("request.getMethod()".equals(expression)) { // 요청 메서드 접근
            return request.getMethod(); // 요청 메서드 반환 (GET, POST 등)
        }

        if ("new java.util.Date()".equals(expression)) { // 새 Date 객체 생성
            return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); // 현재 시간을 ISO 형식으로 반환
        }

        // 간단한 산술 연산
        if (expression.matches("\\d+\\s*[+\\-*/]\\s*\\d+")) { // 숫자 연산 패턴 확인
            return evaluateArithmetic(expression); // 산술 연산 수행
        }

        // 모델에서 변수 조회
        if (model.containsKey(expression)) { // 모델에 해당 키가 있는지 확인
            return model.get(expression); // 모델에서 값 반환
        }

        // 중첩 속성 접근 (object.property)
        if (expression.contains(".")) { // 점이 포함되어 있으면 중첩 속성 접근
            return resolveNestedProperty(expression, model); // 중첩 속성 해결
        }

        // 문자열 리터럴
        if (expression.startsWith("\"") && expression.endsWith("\"")) { // 문자열 리터럴 확인
            return expression.substring(1, expression.length() - 1); // 따옴표 제거하고 반환
        }

        // 숫자 리터럴
        try {
            return Integer.parseInt(expression); // 정수로 파싱 시도
        } catch (NumberFormatException e) { // 정수 파싱 실패 시
            try {
                return Double.parseDouble(expression); // 실수로 파싱 시도
            } catch (NumberFormatException e2) { // 실수 파싱도 실패 시
                // 파싱 실패
            }
        }

        return expression; // 평가할 수 없으면 원본 반환
    }

    /**
     * 조건문에서 변수명 추출
     */
    private String extractVariableFromCondition(String code) { // 조건문 변수명 추출 메서드 정의
        Pattern pattern = Pattern.compile("if\\s*\\(\\s*(\\w+)\\s*!=\\s*null\\s*\\)"); // if (변수 != null) 패턴
        Matcher matcher = pattern.matcher(code); // 코드에서 패턴 찾기
        if (matcher.find()) { // 패턴이 매칭되면
            return matcher.group(1); // 변수명 반환 (첫 번째 그룹)
        }
        return null; // 매칭되지 않으면 null 반환
    }

    /**
     * 간단한 산술 연산 평가
     */
    private Object evaluateArithmetic(String expression) { // 산술 연산 평가 메서드 정의
        try {
            if (expression.contains("+")) { // 덧셈 연산 확인
                String[] parts = expression.split("\\+"); // + 기호로 분할
                return Integer.parseInt(parts[0].trim()) + Integer.parseInt(parts[1].trim()); // 두 수를 더해서 반환
            } else if (expression.contains("-")) { // 뺄셈 연산 확인
                String[] parts = expression.split("-"); // - 기호로 분할
                return Integer.parseInt(parts[0].trim()) - Integer.parseInt(parts[1].trim()); // 두 수를 빼서 반환
            } else if (expression.contains("*")) { // 곱셈 연산 확인
                String[] parts = expression.split("\\*"); // * 기호로 분할
                return Integer.parseInt(parts[0].trim()) * Integer.parseInt(parts[1].trim()); // 두 수를 곱해서 반환
            } else if (expression.contains("/")) { // 나눗셈 연산 확인
                String[] parts = expression.split("/"); // / 기호로 분할
                return Integer.parseInt(parts[0].trim()) / Integer.parseInt(parts[1].trim()); // 두 수를 나누어서 반환
            }
        } catch (Exception e) { // 계산 중 예외 발생 시
            // 계산 실패
        }

        return expression; // 계산할 수 없으면 원본 표현식 반환
    }

    /**
     * 중첩 속성 접근
     */
    private Object resolveNestedProperty(String expression, Map<String, Object> model) { // 중첩 속성 해결 메서드 정의
        String[] parts = expression.split("\\."); // 점(.)으로 분할하여 중첩 속성 접근 준비
        Object current = model.get(parts[0]); // 첫 번째 부분으로 모델에서 객체 가져오기

        for (int i = 1; i < parts.length && current != null; i++) { // 두 번째 부분부터 순회
            try {
                String fieldName = parts[i]; // 속성명 가져오기
                // 게터 메서드명 생성 (예: name -> getName)
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) +
                        fieldName.substring(1);
                // 리플렉션을 사용하여 게터 메서드 호출
                current = current.getClass().getMethod(getterName).invoke(current);
            } catch (Exception e) { // 리플렉션 실행 중 예외 발생 시
                return null; // 속성 접근 실패 시 null 반환
            }
        }

        return current; // 최종 해결된 값 반환
    }
}