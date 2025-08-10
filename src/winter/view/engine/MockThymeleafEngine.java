package winter.view.engine; // 뷰 엔진 관련 패키지 선언

import winter.http.HttpRequest; // HTTP 요청 객체 임포트
import winter.http.HttpResponse; // HTTP 응답 객체 임포트

import java.io.BufferedReader; // 파일 읽기를 위한 BufferedReader 임포트
import java.io.FileReader; // 파일 읽기를 위한 FileReader 임포트
import java.io.IOException; // 입출력 예외 처리를 위한 IOException 임포트
import java.util.List; // 리스트 자료구조 임포트
import java.util.Map; // 맵 자료구조 임포트
import java.util.regex.Matcher; // 정규식 매칭을 위한 Matcher 임포트
import java.util.regex.Pattern; // 정규식 패턴을 위한 Pattern 임포트

/**
 * Mock Thymeleaf 엔진
 * 실제 Thymeleaf의 기본 기능을 시뮬레이션
 */
public class MockThymeleafEngine implements ViewEngine { // ViewEngine 인터페이스를 구현하는 Mock Thymeleaf 엔진 클래스 정의

    // 지원하는 파일 확장자 배열 (.th, .thymeleaf)
    private static final String[] SUPPORTED_EXTENSIONS = {"th", "thymeleaf"};

    // Thymeleaf 속성 패턴들 - th:text 속성을 찾기 위한 정규식
    private static final Pattern TH_TEXT_PATTERN = Pattern.compile("th:text=\"([^\"]+)\"");
    // th:if 속성을 찾기 위한 정규식
    private static final Pattern TH_IF_PATTERN = Pattern.compile("th:if=\"([^\"]+)\"");
    // th:each 속성을 찾기 위한 정규식
    private static final Pattern TH_EACH_PATTERN = Pattern.compile("th:each=\"([^\"]+)\"");
    // ${표현식} 형태를 찾기 위한 정규식
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    @Override
    public String[] getSupportedExtensions() { // 지원하는 확장자 반환 메서드 구현
        return SUPPORTED_EXTENSIONS.clone(); // 배열의 복사본 반환 (불변성 보장)
    }

    @Override
    public String render(String templatePath, Map<String, Object> model, // 템플릿 렌더링 메서드 구현 시작
                         HttpRequest request, HttpResponse response) throws Exception { // 예외 던질 수 있음

        // Thymeleaf 엔진 렌더링 시작 로그 출력
        System.out.println("MockThymeleaf 엔진으로 템플릿 렌더링: " + templatePath);

        String template = readTemplateFile(templatePath); // 템플릿 파일을 읽어서 문자열로 저장

        String result = processThymeleafAttributes(template, model); // Thymeleaf 속성들 처리 (th:text, th:if, th:each)

        result = processExpressions(result, model); // 기본 ${표현식} 처리

        return result; // 최종 처리된 HTML 문자열 반환
    }

    @Override
    public void initialize() { // 뷰 엔진 초기화 메서드 구현
        System.out.println("MockThymeleafEngine 초기화 완료"); // 초기화 완료 로그 출력
    }

    @Override
    public String getEngineName() { // 엔진 이름 반환 메서드 구현
        return "MockThymeleafEngine"; // Mock Thymeleaf 엔진 이름 반환
    }

    @Override
    public int getPriority() { // 우선순위 반환 메서드 구현
        return 10; // 높은 우선순위 10 반환 (낮을수록 우선순위 높음)
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
     * Thymeleaf 속성들을 처리
     */
    private String processThymeleafAttributes(String template, Map<String, Object> model) { // Thymeleaf 속성 처리 메서드 정의
        String result = template; // 결과를 담을 변수에 원본 템플릿 할당

        result = processThText(result, model); // th:text 처리

        result = processThIf(result, model); // th:if 처리

        result = processThEach(result, model); // th:each 처리 (간단한 구현)

        return result; // 모든 속성이 처리된 결과 반환
    }

    /**
     * th:text 속성 처리
     */
    private String processThText(String template, Map<String, Object> model) { // th:text 속성 처리 메서드 정의
        // <태그 th:text="표현식" 기타속성>내용</태그> 형태를 찾는 정규식
        Pattern fullPattern = Pattern.compile("<([^>]+)\\s+th:text=\"([^\"]+)\"([^>]*)>([^<]*)</([^>]+)>");
        Matcher matcher = fullPattern.matcher(template); // 템플릿에서 패턴 매칭
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 th:text 속성을 순회
            String tagName = matcher.group(1); // 태그명 추출 (예: div, span)
            String expression = matcher.group(2); // th:text 속성 값 추출 (예: ${user.name})
            String otherAttrs = matcher.group(3); // 기타 속성들 추출
            String originalContent = matcher.group(4); // 원본 태그 내용 추출
            String closingTag = matcher.group(5); // 닫는 태그명 추출

            Object value = evaluateExpression(expression, model); // 표현식을 평가하여 값 가져오기
            String textContent = value != null ? value.toString() : ""; // 값이 있으면 문자열로 변환, 없으면 빈 문자열

            // 새 태그 생성 (th:text 제거하고 내용을 실제 값으로 교체)
            String replacement = "<" + tagName + otherAttrs + ">" + textContent + "</" + closingTag + ">";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 매칭된 부분을 새 태그로 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * th:if 속성 처리
     */
    private String processThIf(String template, Map<String, Object> model) { // th:if 속성 처리 메서드 정의
        // <태그 th:if="조건" 기타속성>내용</태그> 형태를 찾는 정규식 (DOTALL: 줄바꿈 포함)
        Pattern fullPattern = Pattern.compile("<([^>]+)\\s+th:if=\"([^\"]+)\"([^>]*)>(.*?)</([^>]+)>", Pattern.DOTALL);
        Matcher matcher = fullPattern.matcher(template); // 템플릿에서 패턴 매칭
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 th:if 속성을 순회
            String tagName = matcher.group(1); // 태그명 추출
            String condition = matcher.group(2); // th:if 조건 추출 (예: ${showDetails})
            String otherAttrs = matcher.group(3); // 기타 속성들 추출
            String content = matcher.group(4); // 태그 내용 추출
            String closingTag = matcher.group(5); // 닫는 태그명 추출

            boolean conditionResult = evaluateCondition(condition, model); // 조건 평가

            String replacement; // 교체할 문자열 변수 선언
            if (conditionResult) { // 조건이 참이면
                // 조건이 참이면 th:if 제거하고 태그 유지
                replacement = "<" + tagName + otherAttrs + ">" + content + "</" + closingTag + ">";
            } else { // 조건이 거짓이면
                replacement = ""; // 조건이 거짓이면 전체 태그 제거
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 매칭된 부분 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * th:each 속성 처리 (간단한 구현)
     */
    private String processThEach(String template, Map<String, Object> model) { // th:each 속성 처리 메서드 정의
        // <태그 th:each="반복변수" 기타속성>내용</태그> 형태를 찾는 정규식
        Pattern fullPattern = Pattern.compile("<([^>]+)\\s+th:each=\"([^\"]+)\"([^>]*)>(.*?)</([^>]+)>", Pattern.DOTALL);
        Matcher matcher = fullPattern.matcher(template); // 템플릿에서 패턴 매칭
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 th:each 속성을 순회
            String tagName = matcher.group(1); // 태그명 추출
            String eachExpression = matcher.group(2); // th:each 표현식 추출 (예: "item : ${items}")
            String otherAttrs = matcher.group(3); // 기타 속성들 추출
            String content = matcher.group(4); // 태그 내용 추출
            String closingTag = matcher.group(5); // 닫는 태그명 추출

            String[] parts = eachExpression.split(":"); // ":"로 분할 (변수명 : 컬렉션)
            if (parts.length == 2) { // 올바른 형태인지 확인
                String itemVar = parts[0].trim(); // 아이템 변수명 추출 (예: "item")
                String collectionExpr = parts[1].trim(); // 컬렉션 표현식 추출 (예: "${items}")

                Object collection = evaluateExpression(collectionExpr, model); // 컬렉션 표현식 평가
                StringBuilder listResult = new StringBuilder(); // 반복 결과를 담을 StringBuilder

                if (collection instanceof List) { // 컬렉션이 List인지 확인
                    List<?> list = (List<?>) collection; // List로 캐스팅
                    for (Object item : list) { // 리스트의 각 아이템을 순회
                        model.put(itemVar, item); // 임시로 모델에 아이템 추가

                        String processedContent = processExpressions(content, model); // 콘텐츠 내의 표현식 처리
                        // 각 아이템에 대한 HTML 생성
                        String itemHtml = "<" + tagName + otherAttrs + ">" + processedContent + "</" + closingTag + ">";
                        listResult.append(itemHtml); // 결과에 추가
                    }

                    model.remove(itemVar); // 임시 변수 제거 (모델 정리)
                }

                matcher.appendReplacement(result, Matcher.quoteReplacement(listResult.toString())); // 매칭된 부분을 반복 결과로 교체
            } else { // 파싱 실패 시
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0))); // 원본 유지
            }
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * ${expression} 형태의 표현식 처리
     */
    private String processExpressions(String template, Map<String, Object> model) { // 표현식 처리 메서드 정의
        Matcher matcher = EXPRESSION_PATTERN.matcher(template); // 템플릿에서 ${} 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 표현식을 순회
            String expression = matcher.group(1).trim(); // 표현식 내용 추출 (${} 안의 내용)
            Object value = evaluateExpression(expression, model); // 표현식 평가
            String replacement = value != null ? value.toString() : ""; // 값이 있으면 문자열로 변환, 없으면 빈 문자열
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 표현식을 값으로 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * 표현식 평가 (간단한 구현)
     */
    private Object evaluateExpression(String expression, Map<String, Object> model) { // 표현식 평가 메서드 정의
        // ${...} 제거 (이미 제거되었지만 혹시 모를 경우 대비)
        if (expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length() - 1).trim(); // ${} 제거
        }

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

    /**
     * 조건식 평가 (간단한 구현)
     */
    private boolean evaluateCondition(String condition, Map<String, Object> model) { // 조건식 평가 메서드 정의
        Object value = evaluateExpression(condition, model); // 조건 표현식을 평가하여 값 가져오기

        if (value == null) return false; // 값이 null이면 false
        if (value instanceof Boolean) return (Boolean) value; // Boolean 타입이면 그대로 반환
        if (value instanceof String) return !((String) value).isEmpty(); // String 타입이면 비어있지 않으면 true
        if (value instanceof Number) return ((Number) value).doubleValue() != 0; // Number 타입이면 0이 아니면 true

        return true; // 기본적으로 존재하면 true
    }
}