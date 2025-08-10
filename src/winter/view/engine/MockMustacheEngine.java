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
 * Mock Mustache 엔진
 * Mustache 템플릿 엔진의 기본 기능을 시뮬레이션
 */
public class MockMustacheEngine implements ViewEngine { // ViewEngine 인터페이스를 구현하는 Mock Mustache 엔진 클래스 정의

    // 지원하는 파일 확장자 배열 (.mustache, .hbs)
    private static final String[] SUPPORTED_EXTENSIONS = {"mustache", "hbs"};

    // Mustache 패턴들 정의
    // {{변수}} 형태의 변수를 찾기 위한 정규식 (# / 제외)
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}#/]+)\\}\\}");
    // {{#섹션}}...{{/섹션}} 형태의 섹션을 찾기 위한 정규식 (DOTALL: 줄바꿈 포함)
    private static final Pattern SECTION_PATTERN = Pattern.compile("\\{\\{#([^}]+)\\}\\}(.*?)\\{\\{/\\1\\}\\}", Pattern.DOTALL);
    // {{^섹션}}...{{/섹션}} 형태의 inverted 섹션을 찾기 위한 정규식
    private static final Pattern INVERTED_SECTION_PATTERN = Pattern.compile("\\{\\{\\^([^}]+)\\}\\}(.*?)\\{\\{/\\1\\}\\}", Pattern.DOTALL);
    // {{! 주석 }} 형태의 주석을 찾기 위한 정규식
    private static final Pattern COMMENT_PATTERN = Pattern.compile("\\{\\{!.*?\\}\\}", Pattern.DOTALL);

    @Override
    public String[] getSupportedExtensions() { // 지원하는 확장자 반환 메서드 구현
        return SUPPORTED_EXTENSIONS.clone(); // 배열의 복사본 반환 (불변성 보장)
    }

    @Override
    public String render(String templatePath, Map<String, Object> model, // 템플릿 렌더링 메서드 구현 시작
                         HttpRequest request, HttpResponse response) throws Exception { // 예외 던질 수 있음

        // Mustache 엔진 렌더링 시작 로그 출력
        System.out.println("MockMustache 엔진으로 템플릿 렌더링: " + templatePath);

        String template = readTemplateFile(templatePath); // 템플릿 파일을 읽어서 문자열로 저장

        String result = processMustacheTemplate(template, model); // Mustache 문법 처리

        return result; // 최종 처리된 HTML 문자열 반환
    }

    @Override
    public void initialize() { // 뷰 엔진 초기화 메서드 구현
        System.out.println("MockMustacheEngine 초기화 완료"); // 초기화 완료 로그 출력
    }

    @Override
    public String getEngineName() { // 엔진 이름 반환 메서드 구현
        return "MockMustacheEngine"; // Mock Mustache 엔진 이름 반환
    }

    @Override
    public int getPriority() { // 우선순위 반환 메서드 구현
        return 20; // 높은 우선순위 20 반환
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
     * Mustache 템플릿 처리
     */
    private String processMustacheTemplate(String template, Map<String, Object> model) { // Mustache 템플릿 처리 메서드 정의
        String result = template; // 결과를 담을 변수에 원본 템플릿 할당

        result = processComments(result); // 1. 주석 제거 ({{! ... }})

        result = processInvertedSections(result, model); // 2. Inverted sections 처리 ({{^...}})

        result = processSections(result, model); // 3. Sections 처리 ({{#...}})

        result = processVariables(result, model); // 4. Variables 처리 ({{...}})

        return result; // 모든 처리가 완료된 결과 반환
    }

    /**
     * 주석 처리 {{! comment }}
     */
    private String processComments(String template) { // 주석 제거 메서드 정의
        return COMMENT_PATTERN.matcher(template).replaceAll(""); // 모든 주석을 빈 문자열로 교체
    }

    /**
     * Inverted sections 처리 {{^variable}}...{{/variable}}
     * 변수가 false, null, 빈 배열일 때 내용을 렌더링
     */
    private String processInvertedSections(String template, Map<String, Object> model) { // Inverted 섹션 처리 메서드 정의
        Matcher matcher = INVERTED_SECTION_PATTERN.matcher(template); // 템플릿에서 {{^...}} 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 inverted 섹션을 순회
            String variableName = matcher.group(1).trim(); // 변수명 추출 ({{^ 와 }} 사이의 내용)
            String content = matcher.group(2); // 섹션 내용 추출

            Object value = resolveVariable(variableName, model); // 변수 값 해결
            boolean shouldRender = isFalsy(value); // falsy 값인지 확인 (null, false, 빈 배열 등)

            String replacement = shouldRender ? content : ""; // falsy면 내용 렌더링, 아니면 빈 문자열
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 매칭된 부분 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * Sections 처리 {{#variable}}...{{/variable}}
     * 변수에 따라 반복 또는 조건부 렌더링
     */
    private String processSections(String template, Map<String, Object> model) { // 섹션 처리 메서드 정의
        Matcher matcher = SECTION_PATTERN.matcher(template); // 템플릿에서 {{#...}} 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 섹션을 순회
            String variableName = matcher.group(1).trim(); // 변수명 추출
            String content = matcher.group(2); // 섹션 내용 추출

            Object value = resolveVariable(variableName, model); // 변수 값 해결
            String replacement = processSectionContent(content, value, model); // 섹션 내용 처리

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 매칭된 부분 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * Section 내용 처리
     */
    private String processSectionContent(String content, Object value, Map<String, Object> model) { // 섹션 내용 처리 메서드 정의
        if (isFalsy(value)) { // 값이 falsy인지 확인
            return ""; // falsy 값이면 렌더링하지 않음
        }

        if (value instanceof List) { // 값이 List인지 확인
            // 배열/리스트인 경우 반복 렌더링
            List<?> list = (List<?>) value; // List로 캐스팅
            StringBuilder result = new StringBuilder(); // 반복 결과를 담을 StringBuilder

            for (Object item : list) { // 리스트의 각 아이템을 순회
                if (item instanceof Map) { // 아이템이 Map인지 확인
                    // 객체인 경우 컨텍스트 변경
                    @SuppressWarnings("unchecked") // 타입 캐스팅 경고 억제
                    Map<String, Object> itemMap = (Map<String, Object>) item; // Map으로 캐스팅
                    String processedContent = processMustacheTemplate(content, itemMap); // 아이템을 컨텍스트로 하여 내용 처리
                    result.append(processedContent); // 결과에 추가
                } else { // 원시 값인 경우
                    // 원시 값인 경우 . 으로 접근 가능하도록 임시 컨텍스트 생성
                    Map<String, Object> tempContext = Map.of(".", item); // 현재 아이템을 "." 키로 저장
                    String processedContent = processMustacheTemplate(content, tempContext); // 임시 컨텍스트로 내용 처리
                    result.append(processedContent); // 결과에 추가
                }
            }

            return result.toString(); // 반복 결과 반환
        } else { // 단일 값인 경우
            return processMustacheTemplate(content, model); // 단일 값인 경우 조건부 렌더링
        }
    }

    /**
     * Variables 처리 {{variable}}
     */
    private String processVariables(String template, Map<String, Object> model) { // 변수 처리 메서드 정의
        Matcher matcher = VARIABLE_PATTERN.matcher(template); // 템플릿에서 {{변수}} 패턴 찾기
        StringBuffer result = new StringBuffer(); // 결과를 담을 StringBuffer 생성

        while (matcher.find()) { // 매칭되는 모든 변수를 순회
            String variableName = matcher.group(1).trim(); // 변수명 추출
            Object value = resolveVariable(variableName, model); // 변수 값 해결
            String replacement = value != null ? escapeHtml(value.toString()) : ""; // 값이 있으면 HTML 이스케이프 후 변환, 없으면 빈 문자열
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // 변수를 값으로 교체
        }
        matcher.appendTail(result); // 나머지 부분 추가

        return result.toString(); // 최종 처리된 문자열 반환
    }

    /**
     * 변수 해결 (중첩 속성 지원)
     */
    private Object resolveVariable(String variableName, Map<String, Object> model) { // 변수 해결 메서드 정의
        if (".".equals(variableName)) { // 변수명이 "."인지 확인
            // 현재 컨텍스트 반환 (배열 반복 시 사용)
            return model.get("."); // "." 키의 값 반환
        }

        String[] parts = variableName.split("\\."); // 점(.)으로 분할하여 중첩 속성 접근 준비
        Object current = model.get(parts[0]); // 첫 번째 부분으로 모델에서 객체 가져오기

        for (int i = 1; i < parts.length && current != null; i++) { // 두 번째 부분부터 순회
            if (current instanceof Map) { // 현재 객체가 Map인지 확인
                @SuppressWarnings("unchecked") // 타입 캐스팅 경고 억제
                Map<String, Object> map = (Map<String, Object>) current; // Map으로 캐스팅
                current = map.get(parts[i]); // Map에서 다음 속성 가져오기
            } else { // Map이 아닌 경우
                // 리플렉션을 사용하여 속성 접근
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
        }

        return current; // 최종 해결된 값 반환
    }

    /**
     * Falsy 값 확인 (Mustache 방식)
     */
    private boolean isFalsy(Object value) { // falsy 값 확인 메서드 정의
        if (value == null) return true; // null이면 true
        if (value instanceof Boolean) return !(Boolean) value; // Boolean이면 false인 경우 true
        if (value instanceof String) return ((String) value).isEmpty(); // String이면 비어있으면 true
        if (value instanceof List) return ((List<?>) value).isEmpty(); // List이면 비어있으면 true
        if (value instanceof Number) return ((Number) value).doubleValue() == 0; // Number이면 0이면 true

        return false; // 그 외의 경우는 false (truthy)
    }

    /**
     * HTML 이스케이핑 (기본적인 구현)
     */
    private String escapeHtml(String text) { // HTML 이스케이프 메서드 정의
        if (text == null) return ""; // null이면 빈 문자열 반환

        // HTML 특수 문자들을 엔티티로 변환
        return text.replace("&", "&amp;") // & -> &amp;
                .replace("<", "&lt;") // < -> &lt;
                .replace(">", "&gt;") // > -> &gt;
                .replace("\"", "&quot;") // " -> &quot;
                .replace("'", "&#x27;"); // ' -> &#x27;
    }
}