package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/**
 * HandlerInterceptor는 웹 요청 처리 과정에서 특정 시점에 개입할 수 있는 인터페이스입니다.
 * Spring MVC의 HandlerInterceptor 패턴을 구현하여 횡단 관심사(Cross-cutting concerns)를
 * 컨트롤러 로직과 분리하여 처리할 수 있게 합니다.
 *
 * 주요 활용 사례:
 * - 로깅: 요청/응답 정보 기록
 * - 인증/인가: 사용자 권한 확인
 * - 성능 측정: 요청 처리 시간 계산
 * - CORS 처리: Cross-Origin 요청 헤더 설정
 * - 보안: XSS, CSRF 방지 헤더 추가
 * - 캐싱: 응답 캐시 제어
 *
 * 실행 순서:
 * 1. preHandle() - 핸들러 실행 전
 * 2. [Handler/Controller 실행]
 * 3. postHandle() - 핸들러 실행 후, 뷰 렌더링 전
 * 4. [View 렌더링]
 * 5. afterCompletion() - 모든 처리 완료 후 (예외 발생 시에도 실행)
 *
 * @author Winter Framework
 * @since 27단계
 */
public interface HandlerInterceptor {

    /**
     * 핸들러(컨트롤러) 실행 전에 호출되는 메서드입니다.
     *
     * 이 메서드에서 수행할 수 있는 작업:
     * - 요청 검증 (파라미터, 헤더 등)
     * - 인증/인가 확인
     * - 요청 로깅
     * - 성능 측정 시작
     * - CORS preflight 처리
     * - 요청 정보 변환 또는 추가
     *
     * @param request HTTP 요청 객체 - 요청 정보 조회 및 속성 설정 가능
     * @param response HTTP 응답 객체 - 응답 헤더 설정 및 상태 코드 변경 가능
     * @param handler 실행될 핸들러 객체 (Controller 인스턴스 또는 HandlerMethod)
     * @return true: 다음 인터셉터 또는 핸들러 실행 계속
     *         false: 요청 처리 중단 (응답은 직접 처리해야 함)
     * @throws Exception 처리 중 오류 발생 시 - 체인 중단되고 afterCompletion 호출됨
     */
    default boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        // 기본 구현: 아무것도 하지 않고 다음 단계로 진행
        return true;
    }

    /**
     * 핸들러 실행 후, 뷰 렌더링 전에 호출되는 메서드입니다.
     *
     * 이 메서드에서 수행할 수 있는 작업:
     * - ModelAndView 수정 (모델 데이터 추가/변경, 뷰 이름 변경)
     * - 응답 헤더 추가 설정
     * - 처리 결과 로깅
     * - 캐시 관련 헤더 설정
     * - 보안 헤더 추가
     *
     * 주의사항:
     * - 핸들러에서 예외가 발생한 경우 호출되지 않음
     * - 이전 인터셉터의 preHandle이 false를 반환한 경우 호출되지 않음
     * - JSON 응답 등으로 이미 응답이 커밋된 경우 ModelAndView가 null일 수 있음
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러 객체
     * @param modelAndView 핸들러가 반환한 ModelAndView (null일 수 있음)
     * @throws Exception 처리 중 오류 발생 시 - afterCompletion에서 예외 정보 전달됨
     */
    default void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 기본 구현: 아무것도 하지 않음
        // 상속받는 인터셉터에서 필요한 경우에만 오버라이드
    }

    /**
     * 요청 처리가 완전히 완료된 후 호출되는 메서드입니다.
     * 뷰 렌더링까지 모두 완료된 후 또는 예외 발생으로 인해 처리가 중단된 후 호출됩니다.
     *
     * 이 메서드에서 수행할 수 있는 작업:
     * - 리소스 정리 (파일 핸들, 데이터베이스 연결 등)
     * - 최종 로깅 (처리 시간, 결과 상태 등)
     * - 성능 메트릭 기록
     * - 예외 상황 알림
     * - 임시 파일 삭제
     * - 감사(Audit) 로그 기록
     *
     * 특징:
     * - preHandle이 호출된 인터셉터에 대해서만 호출됨
     * - 예외 발생 여부와 관계없이 반드시 호출됨 (finally 블록과 유사)
     * - 인터셉터 등록 순서의 역순으로 호출됨
     * - 이 메서드에서 발생한 예외는 로깅만 되고 클라이언트에는 전달되지 않음
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러 객체 (처리가 핸들러까지 도달한 경우)
     * @param ex 처리 중 발생한 예외 (정상 처리된 경우 null)
     * @throws Exception 정리 작업 중 오류 발생 시 - 로깅되지만 클라이언트에는 영향 없음
     */
    default void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
        // 기본 구현: 아무것도 하지 않음
        // 상속받는 인터셉터에서 정리 작업이 필요한 경우에만 오버라이드
    }
}