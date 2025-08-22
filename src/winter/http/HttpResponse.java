package winter.http;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * HTTP 응답 정보를 표현하는 인터페이스 (29단계로 확장)
 *
 * ===== 기존 26단계 기능 완전 유지 =====
 * - Writer 지원 (뷰 엔진에서 스트림 방식 출력)
 * - 쿠키 관리 (25단계)
 * - 다양한 편의 메서드들
 *
 * ===== 29단계 확장 사항 =====
 * - 인터페이스로 추상화하여 ResponseEntity와의 호환성 개선
 * - Mock 테스트 환경 지원
 * - getHeader() 메서드 추가로 헤더 조회 가능
 * - 더 유연한 구현체 교체 가능
 */
public interface HttpResponse {

    // ===== 기본 HTTP 응답 메서드들 =====

    /**
     * HTTP 상태 코드 설정
     * @param status HTTP 상태 코드 (200, 404, 500 등)
     */
    void setStatus(int status);

    /**
     * HTTP 상태 코드 조회
     * @return 현재 설정된 HTTP 상태 코드
     */
    int getStatus();

    /**
     * 응답 본문 설정
     * @param body 응답 본문 문자열
     */
    void setBody(String body);

    /**
     * 응답 본문 조회
     * @return 현재 설정된 응답 본문
     */
    String getBody();

    // ===== HTTP 헤더 관리 메서드들 =====

    /**
     * HTTP 헤더 추가/설정
     * @param key 헤더 이름
     * @param value 헤더 값
     */
    void addHeader(String key, String value);

    /**
     * 29단계 신규: HTTP 헤더 조회
     * ResponseEntity에서 헤더 값을 확인할 때 필요
     * @param name 헤더 이름
     * @return 헤더 값, 없으면 null
     */
    String getHeader(String name);

    /**
     * 모든 HTTP 헤더 조회
     * @return 헤더 Map (이름 -> 값)
     */
    Map<String, String> getHeaders();

    // ===== 26단계 Writer 지원 메서드들 (완전 유지) =====

    /**
     * 응답 본문을 작성하기 위한 PrintWriter 반환
     * 뷰 엔진에서 템플릿 렌더링 결과를 스트림 방식으로 출력할 때 사용
     * @return PrintWriter 객체
     */
    PrintWriter getWriter();

    /**
     * Writer에 작성된 내용을 body 필드로 설정
     * Writer를 통해 출력된 내용을 최종 응답 본문으로 반영
     */
    void flushWriter();

    /**
     * Writer 내용을 초기화
     * 새로운 응답을 위해 Writer 상태를 리셋할 때 사용
     */
    void resetWriter();

    // ===== 25단계 쿠키 관리 메서드들 (완전 유지) =====

    /**
     * 쿠키를 응답에 추가
     * @param cookie 추가할 Cookie 객체
     */
    void addCookie(Cookie cookie);

    /**
     * 간단한 쿠키를 생성하여 추가
     * @param name 쿠키 이름
     * @param value 쿠키 값
     */
    void addCookie(String name, String value);

    /**
     * 세션 쿠키를 설정
     * @param sessionId 세션 ID
     * @param maxAge 쿠키 수명 (초), -1이면 브라우저 세션까지만
     * @param secure HTTPS에서만 전송할지 여부
     * @param httpOnly JavaScript 접근 차단 여부
     */
    void setSessionCookie(String sessionId, int maxAge, boolean secure, boolean httpOnly);

    /**
     * 세션 쿠키를 기본 설정으로 설정
     * @param sessionId 세션 ID
     */
    void setSessionCookie(String sessionId);

    /**
     * 세션 쿠키를 삭제
     */
    void deleteSessionCookie();

    /**
     * 쿠키를 삭제
     * @param name 삭제할 쿠키 이름
     */
    void deleteCookie(String name);

    /**
     * 모든 쿠키를 반환
     * @return Cookie 리스트
     */
    List<Cookie> getCookies();

    /**
     * 특정 이름의 쿠키를 찾기
     * @param name 쿠키 이름
     * @return Cookie 객체 또는 null
     */
    Cookie getCookie(String name);

    /**
     * 쿠키가 설정되었는지 확인
     * @param name 쿠키 이름
     * @return 존재하면 true
     */
    boolean hasCookie(String name);

    // ===== 편의 메서드들 (기존 기능 유지) =====

    /**
     * Content-Type 헤더를 설정
     * @param contentType MIME 타입
     */
    void setContentType(String contentType);

    /**
     * 캐시 제어 헤더를 설정
     * @param cacheControl 캐시 제어 값
     */
    void setCacheControl(String cacheControl);

    /**
     * JSON 응답으로 설정
     */
    void setJsonResponse();

    /**
     * HTML 응답으로 설정
     */
    void setHtmlResponse();

    /**
     * 플레인 텍스트 응답으로 설정
     */
    void setTextResponse();

    /**
     * 리다이렉트 응답을 설정
     * @param location 리다이렉트할 URL
     */
    void sendRedirect(String location);

    /**
     * 에러 응답을 설정
     * @param statusCode HTTP 상태 코드
     * @param message 에러 메시지
     */
    void sendError(int statusCode, String message);

    /**
     * 응답을 출력 (쿠키 헤더 포함, Writer 내용 반영)
     */
    void send();

    /**
     * 실제 HTTP 응답 헤더 문자열을 생성
     * @return HTTP 응답 헤더 문자열
     */
    String toHttpString();
}