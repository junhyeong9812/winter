package winter.view.engine; // 뷰 엔진 패키지

import winter.http.HttpRequest; // HTTP 요청 객체
import winter.http.HttpResponse; // HTTP 응답 객체

import java.io.IOException; // 파일 I/O 예외 처리
import java.nio.file.Files; // 파일 읽기 기능
import java.nio.file.Path; // 파일 경로 표현
import java.nio.file.Paths; // 파일 경로 생성
import java.util.Map; // 모델 데이터 저장용
import java.util.regex.Matcher; // 정규식 매칭
import java.util.regex.Pattern; // 정규식 패턴

/**
 * SimpleTemplateEngine - 기본 템플릿 엔진 구현
 * ${변수명} 형태의 플레이스홀더를 지원하는 간단한 템플릿 엔진
 * 26챕터: View Engine Integration의 기본 구현체
 */
public class SimpleTemplateEngine implements ViewEngine { // ViewEngine 인터페이스 구현

    // 정규식 패턴: ${변수명} 형태를 찾기 위한 패턴
    // \\$\\{ : ${로 시작 ($ 특수문자 이스케이프)
    // ([^}]+) : }가 아닌 문자들을 그룹으로 캡처 (변수명)
    // \\} : }로 끝
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 지원하는 파일 확장자 반환
     * SimpleTemplateEngine은 .html 파일을 처리
     */
    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"html"}; // HTML 파일만 지원
    }

    /**
     * 템플릿 파일을 읽어서 모델 데이터로 치환하여 최종 HTML 생성
     *
     * @param templatePath 템플릿 파일의 절대 경로
     * @param model 템플릿에 치환할 모델 데이터
     * @param request HTTP 요청 객체 (필요시 활용)
     * @param response HTTP 응답 객체 (필요시 활용)
     * @return 렌더링된 최종 HTML 문자열
     * @throws Exception 파일 읽기 또는 렌더링 실패 시
     */
    @Override
    public String render(String templatePath, Map<String, Object> model,
                         HttpRequest request, HttpResponse response) throws Exception {
        // 렌더링 시작 로깅
        System.out.println("SimpleTemplateEngine 렌더링 시작: " + templatePath);
        System.out.println("모델 데이터 수: " + model.size());

        try {
            // 1. 템플릿 파일 읽기
            Path path = Paths.get(templatePath); // 파일 경로 객체 생성
            String template = Files.readString(path); // 파일 내용을 문자열로 읽기

            System.out.println("템플릿 파일 읽기 완료: " + template.length() + " 문자");

            // 2. 플레이스홀더 치환 수행
            String result = processTemplate(template, model);

            System.out.println("SimpleTemplateEngine 렌더링 완료: " + result.length() + " 문자");

            return result; // 최종 렌더링 결과 반환

        } catch (IOException e) {
            // 파일 읽기 실패 시 상세 에러 정보 제공
            System.err.println("템플릿 파일 읽기 실패: " + templatePath);
            System.err.println("에러 원인: " + e.getMessage());
            throw new RuntimeException("템플릿 파일을 읽을 수 없습니다: " + templatePath, e);

        } catch (Exception e) {
            // 기타 렌더링 오류 시
            System.err.println("템플릿 렌더링 중 오류: " + e.getMessage());
            throw new RuntimeException("템플릿 렌더링 실패: " + templatePath, e);
        }
    }

    /**
     * 뷰 엔진 초기화
     * SimpleTemplateEngine은 특별한 초기화가 필요 없음
     */
    @Override
    public void initialize() {
        System.out.println("SimpleTemplateEngine 초기화 완료"); // 초기화 완료 로깅
        // 캐시나 설정 파일 로드 등이 필요한 경우 여기서 수행
    }

    /**
     * 뷰 엔진 이름 반환
     * 로깅이나 디버깅 시 엔진 식별용
     */
    @Override
    public String getEngineName() {
        return "SimpleTemplateEngine"; // 엔진 고유 이름
    }

    /**
     * 뷰 엔진 우선순위 반환
     * 낮은 숫자일수록 높은 우선순위
     * SimpleTemplateEngine은 기본 엔진이므로 중간 우선순위 설정
     */
    @Override
    public int getPriority() {
        return 50; // 중간 우선순위 (기본값 100보다 높음)
    }

    /**
     * 템플릿 문자열에서 ${변수} 플레이스홀더를 모델 데이터로 치환
     *
     * @param template 원본 템플릿 문자열
     * @param model 치환할 데이터가 담긴 Map
     * @return 플레이스홀더가 치환된 최종 문자열
     */
    private String processTemplate(String template, Map<String, Object> model) {
        System.out.println("플레이스홀더 치환 시작"); // 치환 시작 로깅

        // 정규식 매처 생성
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);

        // StringBuffer를 사용하여 치환 결과 저장 (StringBuilder보다 동기화 안전)
        StringBuffer result = new StringBuffer();

        int placeholderCount = 0; // 치환된 플레이스홀더 개수 카운트

        // 패턴과 일치하는 모든 플레이스홀더를 찾아서 처리
        while (matcher.find()) {
            // 그룹 1 (괄호 안의 내용)에서 변수명 추출
            String variableName = matcher.group(1);

            // 모델에서 해당 변수에 대응하는 값 찾기
            Object value = getValueFromModel(model, variableName);

            // 값이 null이면 빈 문자열로, 아니면 문자열로 변환
            String replacement = value != null ? value.toString() : "";

            // 정규식 특수문자 이스케이프 처리 (특수문자가 정규식으로 해석되지 않도록)
            replacement = Matcher.quoteReplacement(replacement);

            // 플레이스홀더를 실제 값으로 치환
            matcher.appendReplacement(result, replacement);

            placeholderCount++; // 치환 카운트 증가

            // 로깅: 각 치환 정보 출력
            System.out.println("  치환 [" + placeholderCount + "]: ${" + variableName + "} -> " +
                    (value != null ? "'" + value.toString() + "'" : "null"));
        }

        // 나머지 부분 추가 (마지막 플레이스홀더 이후의 템플릿 내용)
        matcher.appendTail(result);

        System.out.println("플레이스홀더 치환 완료: " + placeholderCount + "개 치환됨"); // 치환 완료 로깅

        return result.toString(); // 최종 결과 반환
    }

    /**
     * 모델에서 점 표기법(user.name)을 지원하여 값을 가져오는 메서드
     *
     * @param model 모델 데이터 Map
     * @param variableName 변수명 (점 표기법 지원: user.name, address.city 등)
     * @return 해당하는 값, 없으면 null
     */
    private Object getValueFromModel(Map<String, Object> model, String variableName) {
        // 점(.)이 포함되지 않은 단순한 변수명인 경우
        if (!variableName.contains(".")) {
            Object value = model.get(variableName); // 모델에서 직접 값 반환
            System.out.println("    단순 변수 '" + variableName + "': " + value);
            return value;
        }

        // 점 표기법으로 중첩된 객체 접근 (예: user.name, user.address.city)
        System.out.println("    중첩 변수 접근: " + variableName);

        String[] parts = variableName.split("\\."); // 점으로 분리
        Object current = model.get(parts[0]); // 첫 번째 부분으로 시작 객체 찾기

        System.out.println("      시작 객체 '" + parts[0] + "': " + current);

        // 각 부분을 순차적으로 탐색하여 중첩된 값 접근
        for (int i = 1; i < parts.length && current != null; i++) {
            String fieldName = parts[i]; // 현재 접근할 필드명
            current = getFieldValue(current, fieldName); // 다음 레벨 필드 값 가져오기
            System.out.println("      필드 '" + fieldName + "': " + current);
        }

        System.out.println("    최종 결과: " + current);
        return current; // 최종 값 반환
    }

    /**
     * 객체에서 필드 값을 가져오는 메서드 (리플렉션 사용)
     *
     * @param obj 대상 객체
     * @param fieldName 필드명
     * @return 필드 값, 가져올 수 없으면 null
     */
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            // 객체의 클래스 정보 가져오기
            Class<?> clazz = obj.getClass();

            // 해당 이름의 필드 찾기
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);

            // private 필드에도 접근할 수 있도록 설정
            field.setAccessible(true);

            // 필드 값 반환
            Object value = field.get(obj);
            System.out.println("        리플렉션 필드 '" + fieldName + "': " + value);
            return value;

        } catch (NoSuchFieldException e) {
            // 필드를 찾을 수 없는 경우
            System.err.println("        필드 '" + fieldName + "'를 찾을 수 없음: " + obj.getClass().getSimpleName());
            return null;

        } catch (IllegalAccessException e) {
            // 필드에 접근할 수 없는 경우
            System.err.println("        필드 '" + fieldName + "'에 접근할 수 없음: " + e.getMessage());
            return null;

        } catch (Exception e) {
            // 기타 예외
            System.err.println("        필드 '" + fieldName + "' 접근 중 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 엔진 정보를 문자열로 반환 (디버깅용)
     */
    @Override
    public String toString() {
        return "SimpleTemplateEngine{" +
                "name='" + getEngineName() + '\'' + // 엔진 이름
                ", extensions=" + java.util.Arrays.toString(getSupportedExtensions()) + // 지원 확장자
                ", priority=" + getPriority() + // 우선순위
                '}';
    }
}