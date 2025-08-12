package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PerformanceInterceptor는 웹 애플리케이션의 성능을 모니터링하고 측정하는 인터셉터입니다.
 *
 * 주요 기능:
 * - 요청별 상세 처리 시간 측정 (preHandle, 핸들러 실행, postHandle, 뷰 렌더링, 전체)
 * - 전체 애플리케이션 성능 통계 수집 (평균, 최대, 최소 처리 시간)
 * - 느린 요청 감지 및 알림
 * - 동시 처리 요청 수 모니터링
 * - 메모리 사용량 추적
 * - 성능 리포트 생성
 *
 * 활용 사례:
 * - 성능 병목 지점 식별
 * - 시스템 부하 모니터링
 * - SLA 준수 확인
 * - 최적화 효과 측정
 *
 * @author Winter Framework
 * @since 27단계
 */
public class PerformanceInterceptor implements HandlerInterceptor {

    /**
     * 요청 처리 시간 정보를 저장하는 내부 클래스
     */
    private static class PerformanceData {
        long startTime;           // 요청 시작 시간
        long preHandleTime;       // preHandle 완료 시간
        long handlerStartTime;    // 핸들러 시작 시간
        long handlerEndTime;      // 핸들러 완료 시간
        long postHandleTime;      // postHandle 완료 시간
        long viewRenderTime;      // 뷰 렌더링 완료 시간
        String requestPath;       // 요청 경로
        String method;           // HTTP 메서드
    }

    // 요청별 성능 데이터 저장용 키 속성명
    private static final String PERFORMANCE_DATA_KEY = "performance.data";

    // 전체 성능 통계
    private final AtomicLong totalRequests = new AtomicLong(0);      // 총 요청 수
    private final AtomicLong totalTime = new AtomicLong(0);          // 총 처리 시간
    private final AtomicLong maxTime = new AtomicLong(0);            // 최대 처리 시간
    private final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE); // 최소 처리 시간
    private final AtomicInteger concurrentRequests = new AtomicInteger(0); // 현재 동시 처리 요청 수

    // 성능 임계값 설정
    private final long SLOW_REQUEST_THRESHOLD = 1000;  // 1초 이상이면 느린 요청
    private final long WARNING_THRESHOLD = 500;        // 500ms 이상이면 경고

    // 날짜 시간 포맷터
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * 요청 처리 시작 시점에 성능 측정을 시작합니다.
     */
    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        String requestKey = generateRequestKey(request);

        // 동시 요청 수 증가
        int currentConcurrent = concurrentRequests.incrementAndGet();

        // 성능 데이터 초기화
        PerformanceData data = new PerformanceData();
        data.startTime = startTime;
        data.requestPath = request.getPath();
        data.method = request.getMethod();

        // HttpRequest의 속성 기능을 사용하여 데이터 저장
        request.addParameter(PERFORMANCE_DATA_KEY, requestKey);
        setPerformanceData(requestKey, data);

        // 메모리 사용량 체크
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsage = (double) usedMemory / maxMemory * 100;

        System.out.println("🚀 [PERF] 요청 시작 - " + data.method + " " + data.requestPath);
        System.out.println("   시간: " + LocalDateTime.now().format(FORMATTER));
        System.out.println("   동시 요청: " + currentConcurrent);
        System.out.println("   메모리 사용률: " + String.format("%.1f%%", memoryUsage) +
                " (" + (usedMemory / 1024 / 1024) + "MB / " + (maxMemory / 1024 / 1024) + "MB)");

        // preHandle 완료 시간 기록
        data.preHandleTime = System.currentTimeMillis();

        return true;
    }

    /**
     * 핸들러 실행 후 호출되어 핸들러 처리 시간을 측정합니다.
     */
    @Override
    public void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestKey = request.getParameter(PERFORMANCE_DATA_KEY);
        PerformanceData data = getPerformanceData(requestKey);

        if (data != null) {
            long currentTime = System.currentTimeMillis();
            data.handlerEndTime = currentTime;
            data.postHandleTime = currentTime;

            // 핸들러 처리 시간 계산 (preHandle 완료 후부터 postHandle까지)
            long handlerTime = data.handlerEndTime - data.preHandleTime;
            long totalSoFar = currentTime - data.startTime;

            System.out.println("⚡ [PERF] 핸들러 완료 - " + data.method + " " + data.requestPath);
            System.out.println("   핸들러 처리 시간: " + handlerTime + "ms");
            System.out.println("   누적 시간: " + totalSoFar + "ms");

            // ModelAndView 정보
            if (modelAndView != null) {
                System.out.println("   뷰: " + modelAndView.getViewName() +
                        " (모델 " + modelAndView.getModelSize() + "개)");
            }

            // 느린 핸들러 경고
            if (handlerTime > WARNING_THRESHOLD) {
                System.out.println("   ⚠️ 핸들러 처리가 느립니다: " + handlerTime + "ms");
            }
        }
    }

    /**
     * 요청 처리 완료 후 최종 성능 분석을 수행합니다.
     */
    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        String requestKey = request.getParameter(PERFORMANCE_DATA_KEY);
        PerformanceData data = removePerformanceData(requestKey); // 정리도 함께

        if (data != null) {
            long endTime = System.currentTimeMillis();
            data.viewRenderTime = endTime;

            // 각 단계별 시간 계산
            long preHandleDuration = data.preHandleTime - data.startTime;
            long handlerDuration = data.handlerEndTime - data.preHandleTime;
            long postHandleDuration = data.postHandleTime - data.handlerEndTime;
            long viewRenderDuration = data.viewRenderTime - data.postHandleTime;
            long totalDuration = endTime - data.startTime;

            // 동시 요청 수 감소
            int remainingConcurrent = concurrentRequests.decrementAndGet();

            // 전체 통계 업데이트
            updateGlobalStats(totalDuration);

            // 상세 성능 리포트 출력
            printDetailedReport(data, preHandleDuration, handlerDuration,
                    postHandleDuration, viewRenderDuration, totalDuration,
                    remainingConcurrent, ex);

            // 성능 임계값 체크
            checkPerformanceThresholds(data, totalDuration);

            // 주기적 통계 출력 (100번째 요청마다)
            if (totalRequests.get() % 100 == 0) {
                printGlobalStats();
            }
        }
    }

    /**
     * 요청별 고유 키 생성
     */
    private String generateRequestKey(HttpRequest request) {
        return Thread.currentThread().getName() + "_" + System.nanoTime();
    }

    // Thread-safe한 데이터 저장을 위한 임시 맵
    private final ConcurrentHashMap<String, PerformanceData> performanceMap = new ConcurrentHashMap<>();

    /**
     * 성능 데이터 저장
     */
    private void setPerformanceData(String key, PerformanceData data) {
        performanceMap.put(key, data);
    }

    /**
     * 성능 데이터 조회
     */
    private PerformanceData getPerformanceData(String key) {
        return performanceMap.get(key);
    }

    /**
     * 성능 데이터 제거
     */
    private PerformanceData removePerformanceData(String key) {
        return performanceMap.remove(key);
    }

    /**
     * 전체 성능 통계 업데이트
     */
    private void updateGlobalStats(long duration) {
        totalRequests.incrementAndGet();
        totalTime.addAndGet(duration);

        // 최대값 업데이트 (원자적 연산)
        maxTime.updateAndGet(current -> Math.max(current, duration));

        // 최소값 업데이트 (원자적 연산)
        minTime.updateAndGet(current -> Math.min(current, duration));
    }

    /**
     * 상세 성능 리포트 출력
     */
    private void printDetailedReport(PerformanceData data, long preHandleDuration,
                                     long handlerDuration, long postHandleDuration,
                                     long viewRenderDuration, long totalDuration,
                                     int remainingConcurrent, Exception ex) {

        System.out.println("📊 [PERF] 처리 완료 - " + data.method + " " + data.requestPath);
        System.out.println("   총 처리 시간: " + totalDuration + "ms");
        System.out.println("   ├─ preHandle: " + preHandleDuration + "ms");
        System.out.println("   ├─ 핸들러 실행: " + handlerDuration + "ms");
        System.out.println("   ├─ postHandle: " + postHandleDuration + "ms");
        System.out.println("   └─ 뷰 렌더링: " + viewRenderDuration + "ms");
        System.out.println("   남은 동시 요청: " + remainingConcurrent);

        if (ex != null) {
            System.out.println("   ❌ 예외 발생: " + ex.getClass().getSimpleName());
        } else {
            System.out.println("   ✅ 정상 완료");
        }

        // 처리 시간 분포 표시
        String timeBar = generateTimeBar(preHandleDuration, handlerDuration,
                postHandleDuration, viewRenderDuration, totalDuration);
        System.out.println("   시간 분포: " + timeBar);
    }

    /**
     * 시간 분포를 시각적으로 표현하는 바 생성
     */
    private String generateTimeBar(long preHandle, long handler, long postHandle, long viewRender, long total) {
        if (total == 0) return "▓▓▓▓▓▓▓▓▓▓";

        int barLength = 20;
        int preHandleBar = (int) ((double) preHandle / total * barLength);
        int handlerBar = (int) ((double) handler / total * barLength);
        int postHandleBar = (int) ((double) postHandle / total * barLength);
        int viewRenderBar = barLength - preHandleBar - handlerBar - postHandleBar;

        StringBuilder bar = new StringBuilder();
        bar.append("█".repeat(Math.max(0, preHandleBar)));      // preHandle: █
        bar.append("▓".repeat(Math.max(0, handlerBar)));        // handler: ▓
        bar.append("▒".repeat(Math.max(0, postHandleBar)));     // postHandle: ▒
        bar.append("░".repeat(Math.max(0, viewRenderBar)));     // viewRender: ░

        return bar.toString();
    }

    /**
     * 성능 임계값 체크 및 경고
     */
    private void checkPerformanceThresholds(PerformanceData data, long totalDuration) {
        if (totalDuration > SLOW_REQUEST_THRESHOLD) {
            System.out.println("🐌 [PERF-ALERT] 매우 느린 요청 감지!");
            System.out.println("   요청: " + data.method + " " + data.requestPath);
            System.out.println("   처리 시간: " + totalDuration + "ms (임계값: " + SLOW_REQUEST_THRESHOLD + "ms)");
            System.out.println("   최적화가 필요합니다.");
        } else if (totalDuration > WARNING_THRESHOLD) {
            System.out.println("⚠️ [PERF-WARNING] 느린 요청: " + data.method + " " + data.requestPath +
                    " (" + totalDuration + "ms)");
        }
    }

    /**
     * 전체 성능 통계 출력
     */
    private void printGlobalStats() {
        long requests = totalRequests.get();
        long total = totalTime.get();
        long max = maxTime.get();
        long min = minTime.get() == Long.MAX_VALUE ? 0 : minTime.get();
        double average = requests > 0 ? (double) total / requests : 0;

        System.out.println("📈 [GLOBAL-STATS] 애플리케이션 성능 통계");
        System.out.println("   총 처리 요청: " + requests + "개");
        System.out.println("   평균 처리 시간: " + String.format("%.1fms", average));
        System.out.println("   최대 처리 시간: " + max + "ms");
        System.out.println("   최소 처리 시간: " + min + "ms");
        System.out.println("   현재 동시 요청: " + concurrentRequests.get() + "개");

        // 메모리 정보
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsage = (double) usedMemory / maxMemory * 100;

        System.out.println("   메모리 사용률: " + String.format("%.1f%%", memoryUsage));
        System.out.println("   사용 메모리: " + (usedMemory / 1024 / 1024) + "MB");
        System.out.println("   최대 메모리: " + (maxMemory / 1024 / 1024) + "MB");

        // 성능 등급 평가
        String performanceGrade = evaluatePerformance(average);
        System.out.println("   성능 등급: " + performanceGrade);
    }

    /**
     * 평균 처리 시간을 기반으로 성능 등급을 평가합니다.
     */
    private String evaluatePerformance(double averageTime) {
        if (averageTime < 50) {
            return "🟢 EXCELLENT (< 50ms)";
        } else if (averageTime < 100) {
            return "🟡 GOOD (50-100ms)";
        } else if (averageTime < 300) {
            return "🟠 FAIR (100-300ms)";
        } else if (averageTime < 1000) {
            return "🔴 POOR (300-1000ms)";
        } else {
            return "⚫ CRITICAL (> 1000ms)";
        }
    }

    /**
     * 현재 성능 통계를 반환합니다. (모니터링 도구에서 사용 가능)
     */
    public String getPerformanceStats() {
        long requests = totalRequests.get();
        long total = totalTime.get();
        long max = maxTime.get();
        long min = minTime.get() == Long.MAX_VALUE ? 0 : minTime.get();
        double average = requests > 0 ? (double) total / requests : 0;

        return String.format("Requests: %d, Avg: %.1fms, Max: %dms, Min: %dms, Concurrent: %d",
                requests, average, max, min, concurrentRequests.get());
    }

    /**
     * 성능 통계를 초기화합니다. (테스트 또는 주기적 리셋 시 사용)
     */
    public void resetStats() {
        totalRequests.set(0);
        totalTime.set(0);
        maxTime.set(0);
        minTime.set(Long.MAX_VALUE);
        concurrentRequests.set(0);
        performanceMap.clear();
        System.out.println("📊 [PERF] 성능 통계가 초기화되었습니다.");
    }
}