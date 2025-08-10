package winter.view.engine; // 뷰 엔진 패키지

import java.util.*; // Collection 클래스들
import java.util.concurrent.ConcurrentHashMap; // 동시성 안전한 Map

/**
 * ViewEngineRegistry - 뷰 엔진 레지스트리
 * 여러 뷰 엔진을 등록하고 관리하는 중앙 저장소
 * 26챕터: View Engine Integration의 핵심 관리 클래스
 */
public class ViewEngineRegistry { // 뷰 엔진 레지스트리 클래스

    // 확장자별 뷰 엔진 매핑 (동시성 안전한 Map 사용)
    // Key: 파일 확장자 (예: "html", "th", "mustache")
    // Value: 해당 확장자를 처리하는 ViewEngine
    private final Map<String, ViewEngine> extensionEngineMap = new ConcurrentHashMap<>();

    // 등록된 모든 뷰 엔진 목록 (등록 순서 유지)
    private final List<ViewEngine> registeredEngines = new ArrayList<>();

    // 기본 뷰 엔진 (다른 엔진으로 처리할 수 없는 경우 사용)
    private ViewEngine defaultEngine;

    /**
     * 뷰 엔진을 레지스트리에 등록
     *
     * @param engine 등록할 뷰 엔진
     */
    public void registerEngine(ViewEngine engine) {
        // null 체크
        if (engine == null) {
            System.err.println("null 뷰 엔진은 등록할 수 없습니다."); // 에러 로깅
            return; // 등록 중단
        }

        // 뷰 엔진 초기화
        try {
            engine.initialize(); // 엔진별 초기화 수행
            System.out.println("뷰 엔진 초기화 완료: " + engine.getEngineName()); // 초기화 완료 로깅
        } catch (Exception e) {
            System.err.println("뷰 엔진 초기화 실패: " + engine.getEngineName() + " - " + e.getMessage()); // 초기화 실패 로깅
            return; // 등록 중단
        }

        // 지원하는 확장자들을 맵에 등록
        String[] extensions = engine.getSupportedExtensions(); // 엔진이 지원하는 확장자 목록
        if (extensions != null && extensions.length > 0) { // 확장자가 존재하는 경우
            for (String extension : extensions) { // 각 확장자에 대해
                String normalizedExt = extension.toLowerCase().trim(); // 소문자로 정규화

                // 기존에 등록된 엔진이 있는지 확인
                ViewEngine existingEngine = extensionEngineMap.get(normalizedExt);
                if (existingEngine != null) {
                    // 우선순위 비교 (낮은 숫자가 높은 우선순위)
                    if (engine.getPriority() < existingEngine.getPriority()) {
                        // 새 엔진이 더 높은 우선순위면 교체
                        extensionEngineMap.put(normalizedExt, engine);
                        System.out.println("확장자 '" + normalizedExt + "' 엔진 교체: " +
                                existingEngine.getEngineName() + " -> " + engine.getEngineName() +
                                " (우선순위: " + existingEngine.getPriority() + " -> " + engine.getPriority() + ")");
                    } else {
                        // 기존 엔진이 더 높은 우선순위면 유지
                        System.out.println("확장자 '" + normalizedExt + "' 엔진 유지: " +
                                existingEngine.getEngineName() + " (우선순위: " + existingEngine.getPriority() +
                                " vs " + engine.getPriority() + ")");
                    }
                } else {
                    // 새로운 확장자면 바로 등록
                    extensionEngineMap.put(normalizedExt, engine);
                    System.out.println("확장자 '" + normalizedExt + "' 등록: " + engine.getEngineName());
                }
            }
        } else {
            System.err.println("뷰 엔진 '" + engine.getEngineName() + "'가 지원하는 확장자가 없습니다."); // 경고 로깅
        }

        // 등록된 엔진 목록에 추가 (중복 체크)
        if (!registeredEngines.contains(engine)) {
            registeredEngines.add(engine); // 엔진 목록에 추가
            System.out.println("뷰 엔진 등록 완료: " + engine.getEngineName() +
                    " (지원 확장자: " + Arrays.toString(extensions) +
                    ", 우선순위: " + engine.getPriority() + ")");
        }
    }

    /**
     * 템플릿 파일 경로에 적합한 뷰 엔진을 찾아 반환
     *
     * @param templatePath 템플릿 파일 경로
     * @return 적합한 뷰 엔진, 없으면 기본 엔진 또는 null
     */
    public ViewEngine getEngineForTemplate(String templatePath) {
        // null이나 빈 경로 체크
        if (templatePath == null || templatePath.trim().isEmpty()) {
            System.err.println("템플릿 경로가 비어있습니다."); // 에러 로깅
            return defaultEngine; // 기본 엔진 반환
        }

        // 파일 확장자 추출
        String extension = extractFileExtension(templatePath);
        if (extension == null || extension.isEmpty()) {
            System.out.println("템플릿 파일에 확장자가 없습니다: " + templatePath); // 경고 로깅
            return defaultEngine; // 기본 엔진 반환
        }

        // 확장자에 해당하는 뷰 엔진 찾기
        ViewEngine engine = extensionEngineMap.get(extension.toLowerCase());
        if (engine != null) {
            System.out.println("템플릿 '" + templatePath + "'에 대한 엔진 발견: " + engine.getEngineName()); // 엔진 발견 로깅
            return engine; // 발견된 엔진 반환
        }

        // 적합한 엔진을 찾지 못한 경우
        System.out.println("확장자 '" + extension + "'에 대한 뷰 엔진을 찾을 수 없습니다. 기본 엔진 사용."); // 경고 로깅
        return defaultEngine; // 기본 엔진 반환
    }

    /**
     * 기본 뷰 엔진 설정
     * 다른 엔진으로 처리할 수 없는 템플릿에 대해 사용될 엔진
     *
     * @param engine 기본으로 사용할 뷰 엔진
     */
    public void setDefaultEngine(ViewEngine engine) {
        this.defaultEngine = engine; // 기본 엔진 설정
        if (engine != null) {
            System.out.println("기본 뷰 엔진 설정: " + engine.getEngineName()); // 설정 완료 로깅
        } else {
            System.out.println("기본 뷰 엔진이 null로 설정되었습니다."); // 경고 로깅
        }
    }

    /**
     * 등록된 모든 뷰 엔진 목록 반환
     *
     * @return 등록된 뷰 엔진들의 읽기 전용 리스트
     */
    public List<ViewEngine> getAllEngines() {
        return Collections.unmodifiableList(registeredEngines); // 읽기 전용 리스트 반환
    }

    /**
     * 특정 확장자를 처리하는 뷰 엔진 반환
     *
     * @param extension 파일 확장자
     * @return 해당 확장자를 처리하는 뷰 엔진, 없으면 null
     */
    public ViewEngine getEngineByExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return null; // 빈 확장자면 null 반환
        }
        return extensionEngineMap.get(extension.toLowerCase().trim()); // 정규화된 확장자로 엔진 검색
    }

    /**
     * 등록된 뷰 엔진 수 반환
     *
     * @return 등록된 뷰 엔진의 개수
     */
    public int getEngineCount() {
        return registeredEngines.size(); // 등록된 엔진 수 반환
    }

    /**
     * 지원하는 모든 확장자 목록 반환
     *
     * @return 지원하는 확장자들의 Set
     */
    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(extensionEngineMap.keySet()); // 읽기 전용 Set 반환
    }

    /**
     * 레지스트리가 비어있는지 확인
     *
     * @return 등록된 엔진이 없으면 true
     */
    public boolean isEmpty() {
        return registeredEngines.isEmpty(); // 엔진 목록이 비어있는지 반환
    }

    /**
     * 레지스트리 초기화 (모든 엔진 제거)
     */
    public void clear() {
        extensionEngineMap.clear(); // 확장자 맵 초기화
        registeredEngines.clear(); // 엔진 목록 초기화
        defaultEngine = null; // 기본 엔진 제거
        System.out.println("뷰 엔진 레지스트리가 초기화되었습니다."); // 초기화 완료 로깅
    }

    /**
     * 레지스트리 정보를 콘솔에 출력 (디버깅용)
     */
    public void printRegistryInfo() {
        System.out.println("\n=== 뷰 엔진 레지스트리 정보 ==="); // 헤더 출력
        System.out.println("등록된 엔진 수: " + registeredEngines.size()); // 엔진 수 출력
        System.out.println("지원 확장자 수: " + extensionEngineMap.size()); // 확장자 수 출력
        System.out.println("기본 엔진: " + (defaultEngine != null ? defaultEngine.getEngineName() : "없음")); // 기본 엔진 출력

        // 등록된 엔진들 상세 정보 출력
        if (!registeredEngines.isEmpty()) {
            System.out.println("\n📋 등록된 뷰 엔진들:");
            for (int i = 0; i < registeredEngines.size(); i++) { // 인덱스와 함께 순회
                ViewEngine engine = registeredEngines.get(i);
                System.out.println("  " + (i + 1) + ". " + engine.getEngineName() +
                        " (우선순위: " + engine.getPriority() +
                        ", 확장자: " + Arrays.toString(engine.getSupportedExtensions()) + ")");
            }
        }

        // 확장자별 엔진 매핑 출력
        if (!extensionEngineMap.isEmpty()) {
            System.out.println("\n🔗 확장자별 엔진 매핑:");
            extensionEngineMap.entrySet().stream() // 스트림 사용
                    .sorted(Map.Entry.comparingByKey()) // 확장자순 정렬
                    .forEach(entry -> System.out.println("  ." + entry.getKey() + " -> " + entry.getValue().getEngineName()));
        }

        System.out.println("===========================\n"); // 푸터 출력
    }

    /**
     * 파일 경로에서 확장자 추출하는 헬퍼 메서드
     *
     * @param filePath 파일 경로
     * @return 파일 확장자, 없으면 null
     */
    private String extractFileExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null; // 빈 경로면 null 반환
        }

        // 마지막 점(.)의 위치 찾기
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return null; // 점이 없거나 파일명 끝에 있으면 null 반환
        }

        // 점 이후의 문자열 추출 (확장자)
        String extension = filePath.substring(lastDotIndex + 1).trim();
        return extension.isEmpty() ? null : extension; // 빈 확장자면 null, 아니면 확장자 반환
    }

    /**
     * 특정 뷰 엔진이 등록되어 있는지 확인
     *
     * @param engineName 엔진 이름
     * @return 등록되어 있으면 true
     */
    public boolean hasEngine(String engineName) {
        if (engineName == null || engineName.trim().isEmpty()) {
            return false; // 빈 이름이면 false
        }

        return registeredEngines.stream() // 스트림 사용
                .anyMatch(engine -> engineName.equals(engine.getEngineName())); // 이름이 일치하는 엔진이 있는지 확인
    }

    /**
     * 이름으로 뷰 엔진 찾기
     *
     * @param engineName 엔진 이름
     * @return 해당 이름의 뷰 엔진, 없으면 null
     */
    public ViewEngine getEngineByName(String engineName) {
        if (engineName == null || engineName.trim().isEmpty()) {
            return null; // 빈 이름이면 null
        }

        return registeredEngines.stream() // 스트림 사용
                .filter(engine -> engineName.equals(engine.getEngineName())) // 이름이 일치하는 엔진 필터링
                .findFirst() // 첫 번째 일치하는 엔진
                .orElse(null); // 없으면 null 반환
    }

    /**
     * 레지스트리 상태를 문자열로 반환 (디버깅용)
     */
    @Override
    public String toString() {
        return "ViewEngineRegistry{" +
                "engines=" + registeredEngines.size() + // 등록된 엔진 수
                ", extensions=" + extensionEngineMap.size() + // 지원 확장자 수
                ", defaultEngine=" + (defaultEngine != null ? defaultEngine.getEngineName() : "null") + // 기본 엔진
                '}';
    }
}