package winter.interceptor;

import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InterceptorChain은 여러 개의 HandlerInterceptor를 체인으로 연결하여
 * 순차적으로 실행하는 책임을 가진 클래스입니다.
 *
 * Spring MVC의 인터셉터 체인 메커니즘을 구현하여:
 * - 인터셉터들의 실행 순서 관리
 * - 예외 발생 시 적절한 정리 작업 보장
 * - 인터셉터 간의 상태 전파 처리
 *
 * 실행 순서:
 * 1. preHandle: 등록 순서대로 실행 (A → B → C)
 * 2. postHandle: 등록 순서의 역순으로 실행 (C → B → A)
 * 3. afterCompletion: 등록 순서의 역순으로 실행 (C → B → A)
 *
 * 예외 처리:
 * - preHandle에서 false 반환 시: 이후 인터셉터 및 핸들러 실행 중단
 * - 어느 단계에서든 예외 발생 시: afterCompletion은 실행된 인터셉터들에 대해 역순으로 호출
 *
 * @author Winter Framework
 * @since 27단계
 */
public class InterceptorChain {

    /**
     * 등록된 인터셉터들의 목록
     * 순서가 중요하므로 ArrayList 사용
     */
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    /**
     * 현재까지 preHandle이 성공적으로 실행된 인터셉터의 최대 인덱스
     * afterCompletion 호출 시 이 인덱스까지의 인터셉터들만 호출해야 함
     * -1: 아직 아무 인터셉터도 실행되지 않음
     */
    private int interceptorIndex = -1;

    /**
     * 인터셉터를 체인에 추가합니다.
     *
     * @param interceptor 추가할 인터셉터 (null이면 무시됨)
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
            System.out.println("인터셉터 등록: " + interceptor.getClass().getSimpleName());
        }
    }

    /**
     * 등록된 모든 인터셉터들의 preHandle 메서드를 순차적으로 실행합니다.
     *
     * 실행 규칙:
     * - 인터셉터들을 등록된 순서대로 실행
     * - 하나라도 false를 반환하면 즉시 중단
     * - 예외 발생 시에도 즉시 중단
     * - 성공적으로 실행된 인터셉터들의 인덱스를 기록
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행될 핸들러 객체
     * @return 모든 인터셉터가 true를 반환했으면 true, 하나라도 false를 반환했으면 false
     * @throws Exception 인터셉터 실행 중 예외 발생 시
     */
    public boolean applyPreHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        System.out.println("=== preHandle 체인 실행 시작 ===");

        // 등록된 인터셉터가 없으면 바로 성공
        if (interceptors.isEmpty()) {
            System.out.println("등록된 인터셉터 없음 - 체인 실행 완료");
            return true;
        }

        // 각 인터셉터의 preHandle을 순차적으로 실행
        for (int i = 0; i < interceptors.size(); i++) {
            HandlerInterceptor interceptor = interceptors.get(i);
            System.out.println("preHandle 실행: " + interceptor.getClass().getSimpleName() + " [" + i + "]");

            try {
                // preHandle 실행
                boolean result = interceptor.preHandle(request, response, handler);

                if (result) {
                    // 성공한 경우 인덱스 업데이트 (afterCompletion에서 사용)
                    this.interceptorIndex = i;
                    System.out.println("preHandle 성공: " + interceptor.getClass().getSimpleName());
                } else {
                    // false 반환 시 체인 중단
                    System.out.println("preHandle 중단: " + interceptor.getClass().getSimpleName() + " (false 반환)");
                    return false;
                }
            } catch (Exception ex) {
                // 예외 발생 시에도 체인 중단
                System.err.println("preHandle 예외 발생: " + interceptor.getClass().getSimpleName() + " - " + ex.getMessage());
                throw ex; // 예외를 다시 던져서 상위에서 처리하도록 함
            }
        }

        System.out.println("=== preHandle 체인 실행 완료 (성공) ===");
        return true;
    }

    /**
     * 등록된 모든 인터셉터들의 postHandle 메서드를 역순으로 실행합니다.
     *
     * 실행 규칙:
     * - preHandle이 성공한 인터셉터들만 실행 (interceptorIndex 기준)
     * - 등록 순서의 역순으로 실행 (후입선출 방식)
     * - 예외가 발생해도 모든 인터셉터 실행 시도 (로깅 후 계속)
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러 객체
     * @param modelAndView 핸들러가 반환한 ModelAndView (null 가능)
     */
    public void applyPostHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("=== postHandle 체인 실행 시작 ===");

        // preHandle이 성공한 인터셉터들만 역순으로 실행
        for (int i = interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptors.get(i);
            System.out.println("postHandle 실행: " + interceptor.getClass().getSimpleName() + " [" + i + "]");

            try {
                interceptor.postHandle(request, response, handler, modelAndView);
                System.out.println("postHandle 완료: " + interceptor.getClass().getSimpleName());
            } catch (Exception ex) {
                // postHandle에서 예외가 발생해도 다른 인터셉터들은 계속 실행
                System.err.println("postHandle 예외 발생: " + interceptor.getClass().getSimpleName() + " - " + ex.getMessage());
                ex.printStackTrace(); // 로깅 목적
                // 예외를 던지지 않고 계속 진행 (다른 인터셉터들도 실행되어야 함)
            }
        }

        System.out.println("=== postHandle 체인 실행 완료 ===");
    }

    /**
     * 등록된 모든 인터셉터들의 afterCompletion 메서드를 역순으로 실행합니다.
     *
     * 실행 규칙:
     * - preHandle이 성공한 인터셉터들만 실행 (interceptorIndex 기준)
     * - 등록 순서의 역순으로 실행 (후입선출 방식)
     * - 예외 발생 여부와 관계없이 반드시 모든 인터셉터 실행
     * - 이 메서드에서 발생한 예외는 로깅만 하고 전파하지 않음
     *
     * 주의사항:
     * - 이 메서드는 반드시 finally 블록에서 호출되어야 함
     * - 리소스 정리의 마지막 기회이므로 모든 인터셉터가 실행되어야 함
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler 실행된 핸들러 객체 (핸들러까지 도달하지 못한 경우 null 가능)
     * @param ex 처리 중 발생한 예외 (정상 처리된 경우 null)
     */
    public void triggerAfterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) {
        System.out.println("=== afterCompletion 체인 실행 시작 ===");
        System.out.println("처리 예외: " + (ex != null ? ex.getClass().getSimpleName() + " - " + ex.getMessage() : "없음"));

        // preHandle이 성공한 인터셉터들만 역순으로 실행
        for (int i = interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptors.get(i);
            System.out.println("afterCompletion 실행: " + interceptor.getClass().getSimpleName() + " [" + i + "]");

            try {
                interceptor.afterCompletion(request, response, handler, ex);
                System.out.println("afterCompletion 완료: " + interceptor.getClass().getSimpleName());
            } catch (Exception afterEx) {
                // afterCompletion에서 예외가 발생해도 다른 인터셉터들은 계속 실행
                // 클라이언트에게는 영향을 주지 않음 (로깅만)
                System.err.println("afterCompletion 예외 발생: " + interceptor.getClass().getSimpleName() + " - " + afterEx.getMessage());
                afterEx.printStackTrace(); // 로깅 목적
                // 예외를 던지지 않고 계속 진행 (정리 작업은 반드시 완료되어야 함)
            }
        }

        System.out.println("=== afterCompletion 체인 실행 완료 ===");

        // 체인 실행 완료 후 상태 초기화
        this.interceptorIndex = -1;
    }

    /**
     * 등록된 인터셉터 목록을 반환합니다 (읽기 전용).
     *
     * @return 등록된 인터셉터들의 불변 리스트
     */
    public List<HandlerInterceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

    /**
     * 등록된 인터셉터의 개수를 반환합니다.
     *
     * @return 인터셉터 개수
     */
    public int size() {
        return interceptors.size();
    }

    /**
     * 등록된 모든 인터셉터를 제거합니다.
     * 테스트 시나리오나 동적 구성 변경 시 사용할 수 있습니다.
     */
    public void clear() {
        interceptors.clear();
        interceptorIndex = -1;
        System.out.println("모든 인터셉터 제거됨");
    }

    /**
     * 현재 체인의 상태 정보를 문자열로 반환합니다.
     * 디버깅 및 로깅 목적으로 사용됩니다.
     *
     * @return 체인 상태 정보
     */
    @Override
    public String toString() {
        return "InterceptorChain{" +
                "size=" + interceptors.size() +
                ", currentIndex=" + interceptorIndex +
                ", interceptors=" + interceptors.stream()
                .map(i -> i.getClass().getSimpleName())
                .reduce((a, b) -> a + " → " + b)
                .orElse("none") +
                '}';
    }
}