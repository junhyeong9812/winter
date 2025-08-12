package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * LoggingInterceptor는 요청 처리 과정의 상세한 로깅을 담당하는 인터셉터입니다.
 *
 * 주요 기능:
 * - 요청별 고유 ID 생성 및 추적
 * - 요청 정보 상세 로깅 (Method, Path, Headers, Parameters 등)
 * - 처리 시간 측정 및 성능 모니터링
 * - 응답 상태 및 크기 로깅
 * - 예외 발생 시 상세 정보 기록
 *
 * 활용 사례:
 * - 디버깅 및 문제 해결
 * - 성능 모니터링
 * - 보안 감사 로그
 * - API 사용량 추적
 *
 * @author Winter Framework
 * @since 27단계
 */
public class LoggingInterceptor implements HandlerInterceptor {

    /**
     * 요청 시작 시간을 저장하기 위한 요청 파라미터 키
     */
    private static final String START_TIME_PARAM = "logging.startTime";

    /**
     * 요청 ID를 저장하기 위한 요청 파라미터 키
     */
    private static final String REQUEST_ID_PARAM = "logging.requestId";

    /**
     * 날짜 시간 포맷터 (로그 출력용)
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 요청 처리 시작 시점에 호출되어 요청 정보를 로깅하고 처리 시간 측정을 시작합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행될 핸들러
     * @return 항상 true (요청 처리 계속)
     */
    @Override
    public boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        // 요청별 고유 ID 생성
        String requestId = generateRequestId();
        request.addParameter(REQUEST_ID_PARAM, requestId);

        // 처리 시작 시간 기록
        long startTime = System.currentTimeMillis();
        request.addParameter(START_TIME_PARAM, String.valueOf(startTime));

        // 현재 시간
        String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        System.out.println("┌─────────────────────────────────────────────────────────────");
        System.out.println("│ [요청 시작] " + currentTime);
        System.out.println("│ Request ID: " + requestId);
        System.out.println("│ Method: " + request.getMethod());
        System.out.println("│ Path: " + request.getPath());

        // 쿼리 스트링 로깅 (HttpRequest에는 getQueryString 메서드가 없으므로 파라미터로 추정)
        if (!request.getParameterNames().isEmpty()) {
            System.out.println("│ Parameters: " + request.getParameterNames().size() + " entries");
            for (String paramName : request.getParameterNames()) {
                // REQUEST_ID_PARAM과 START_TIME_PARAM은 제외하고 출력
                if (!REQUEST_ID_PARAM.equals(paramName) && !START_TIME_PARAM.equals(paramName)) {
                    String paramValue = request.getParameter(paramName);
                    System.out.println("│   " + paramName + " = " + paramValue);
                }
            }
        } else {
            System.out.println("│ Parameters: 없음");
        }

        System.out.println("│ Handler: " + handler.getClass().getSimpleName());

        // 주요 헤더 정보 로깅
        logImportantHeaders(request);

        // 세션 정보 로깅
        logSessionInfo(request);

        System.out.println("└─────────────────────────────────────────────────────────────");

        return true; // 요청 처리 계속
    }

    /**
     * 핸들러 실행 후 호출되어 처리 결과를 로깅합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러
     * @param modelAndView 핸들러 실행 결과
     */
    @Override
    public void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestId = request.getParameter(REQUEST_ID_PARAM);
        String startTimeStr = request.getParameter(START_TIME_PARAM);

        long currentTime = System.currentTimeMillis();
        long handlerTime = 0;

        if (startTimeStr != null) {
            try {
                long startTime = Long.parseLong(startTimeStr);
                handlerTime = currentTime - startTime;
            } catch (NumberFormatException e) {
                handlerTime = 0;
            }
        }

        System.out.println("┌─────────────────────────────────────────────────────────────");
        System.out.println("│ [핸들러 완료] " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        System.out.println("│ Request ID: " + requestId);
        System.out.println("│ Handler 처리 시간: " + handlerTime + "ms");

        // ModelAndView 정보 로깅
        if (modelAndView != null) {
            System.out.println("│ View Name: " + modelAndView.getViewName());
            System.out.println("│ Model Keys: " + String.join(", ", modelAndView.getModel().keySet()));
            System.out.println("│ Model Size: " + modelAndView.getModelSize());
        } else {
            System.out.println("│ ModelAndView: null (직접 응답 처리)");
        }

        // 응답 상태 로깅
        System.out.println("│ Response Status: " + response.getStatus());

        System.out.println("└─────────────────────────────────────────────────────────────");
    }

    /**
     * 요청 처리 완료 후 호출되어 최종 처리 결과와 성능 정보를 로깅합니다.
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러
     * @param ex 발생한 예외 (없으면 null)
     */
    @Override
    public void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        String requestId = request.getParameter(REQUEST_ID_PARAM);
        String startTimeStr = request.getParameter(START_TIME_PARAM);

        long endTime = System.currentTimeMillis();
        long totalTime = 0;

        if (startTimeStr != null) {
            try {
                long startTime = Long.parseLong(startTimeStr);
                totalTime = endTime - startTime;
            } catch (NumberFormatException e) {
                totalTime = 0;
            }
        }

        System.out.println("┌─────────────────────────────────────────────────────────────");
        System.out.println("│ [요청 완료] " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        System.out.println("│ Request ID: " + requestId);
        System.out.println("│ 총 처리 시간: " + totalTime + "ms");
        System.out.println("│ 최종 상태: " + response.getStatus());

        // 응답 크기 정보 (있는 경우)
        String body = response.getBody();
        if (body != null) {
            System.out.println("│ 응답 크기: " + body.length() + " bytes");
        }

        // 예외 정보 로깅
        if (ex != null) {
            System.out.println("│ ❌ 예외 발생: " + ex.getClass().getSimpleName());
            System.out.println("│ 예외 메시지: " + ex.getMessage());
        } else {
            System.out.println("│ ✅ 정상 처리 완료");
        }

        // 성능 경고 (느린 요청)
        if (totalTime > 1000) {
            System.out.println("│ ⚠️  성능 경고: 처리 시간이 1초를 초과했습니다!");
        } else if (totalTime > 500) {
            System.out.println("│ ⚠️  성능 주의: 처리 시간이 500ms를 초과했습니다.");
        }

        System.out.println("└─────────────────────────────────────────────────────────────");
    }

    /**
     * 고유한 요청 ID를 생성합니다.
     *
     * @return 요청 ID (UUID의 앞 8자리)
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 중요한 HTTP 헤더들을 로깅합니다.
     *
     * @param request HTTP 요청 객체
     */
    private void logImportantHeaders(HttpRequest request) {
        System.out.println("│ 주요 헤더:");

        // User-Agent
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            System.out.println("│   User-Agent: " + userAgent);
        }

        // Content-Type
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            System.out.println("│   Content-Type: " + contentType);
        }

        // Accept
        String accept = request.getHeader("Accept");
        if (accept != null) {
            System.out.println("│   Accept: " + accept);
        }

        // Referer
        String referer = request.getHeader("Referer");
        if (referer != null) {
            System.out.println("│   Referer: " + referer);
        }

        // X-Forwarded-For (프록시 환경에서 실제 클라이언트 IP)
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
            System.out.println("│   X-Forwarded-For: " + forwardedFor);
        }

        // 헤더가 없는 경우
        if (request.getHeaders().isEmpty()) {
            System.out.println("│   헤더 없음");
        }
    }

    /**
     * 세션 정보를 로깅합니다.
     *
     * @param request HTTP 요청 객체
     */
    private void logSessionInfo(HttpRequest request) {
        if (request.getSession() != null) {
            System.out.println("│ 세션 정보:");
            System.out.println("│   Session ID: " + request.getSession().getId());
            System.out.println("│   Session New: " + request.getSession().isNew());
            // 세션 속성이 있는지 확인 (Iterator 방식)
            boolean hasAttributes = request.getSession().getAttributeNames().asIterator().hasNext();
            System.out.println("│   Session Attributes: " + hasAttributes);
        } else {
            System.out.println("│ 세션: 없음");
        }
    }
}