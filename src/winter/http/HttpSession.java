package winter.http;

import java.util.Enumeration;

/**
 * HTTP 세션을 추상화하는 인터페이스
 * Spring Framework의 HttpSession과 동일한 API를 제공합니다.
 */
public interface HttpSession {

    /**
     * 세션 ID를 반환합니다.
     * @return 고유한 세션 식별자
     */
    String getId();

    /**
     * 세션에서 지정된 이름의 속성을 조회합니다.
     * @param name 속성 이름
     * @return 속성 값, 없으면 null
     */
    Object getAttribute(String name);

    /**
     * 세션에 속성을 설정합니다.
     * @param name 속성 이름
     * @param value 속성 값
     */
    void setAttribute(String name, Object value);

    /**
     * 세션에서 지정된 속성을 제거합니다.
     * @param name 제거할 속성 이름
     */
    void removeAttribute(String name);

    /**
     * 세션을 무효화합니다.
     * 모든 속성이 제거되고 세션이 삭제됩니다.
     */
    void invalidate();

    /**
     * 새로 생성된 세션인지 확인합니다.
     * @return 새 세션이면 true, 기존 세션이면 false
     */
    boolean isNew();

    /**
     * 세션이 생성된 시간을 반환합니다.
     * @return 생성 시간 (밀리초)
     */
    long getCreationTime();

    /**
     * 클라이언트가 마지막으로 요청을 보낸 시간을 반환합니다.
     * @return 마지막 접근 시간 (밀리초)
     */
    long getLastAccessedTime();

    /**
     * 세션의 최대 비활성 간격을 초 단위로 반환합니다.
     * @return 비활성 타임아웃 (초)
     */
    int getMaxInactiveInterval();

    /**
     * 세션의 최대 비활성 간격을 설정합니다.
     * @param interval 비활성 타임아웃 (초), 0 이하이면 세션이 만료되지 않음
     */
    void setMaxInactiveInterval(int interval);

    /**
     * 세션에 저장된 모든 속성 이름을 반환합니다.
     * @return 속성 이름들의 Enumeration
     */
    Enumeration<String> getAttributeNames();
}