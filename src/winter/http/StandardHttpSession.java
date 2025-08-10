package winter.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HttpSession의 표준 구현체
 * 메모리 기반으로 세션 데이터를 관리합니다.
 */
public class StandardHttpSession implements HttpSession {

    private final String id;
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();
    private final long creationTime;
    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;
    private volatile boolean valid = true;
    private volatile boolean isNew = true;

    /**
     * 새 세션 생성
     * @param id 세션 ID
     * @param maxInactiveInterval 비활성 타임아웃 (초)
     */
    public StandardHttpSession(String id, int maxInactiveInterval) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (maxInactiveInterval <= 0) {
            throw new IllegalArgumentException("Max inactive interval must be positive");
        }

        this.id = id.trim();
        this.maxInactiveInterval = maxInactiveInterval;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = this.creationTime;
    }

    @Override
    public String getId() {
        checkValidity();
        return id;
    }

    @Override
    public Object getAttribute(String name) {
        checkValidity();
        if (name == null) {
            return null;
        }
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValidity();
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }

        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
        updateLastAccessedTime();
    }

    @Override
    public void removeAttribute(String name) {
        checkValidity();
        if (name != null) {
            attributes.remove(name);
        }
        updateLastAccessedTime();
    }

    @Override
    public void invalidate() {
        checkValidity();
        attributes.clear();
        valid = false;
    }

    @Override
    public boolean isNew() {
        checkValidity();
        return isNew;
    }

    @Override
    public long getCreationTime() {
        checkValidity();
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        checkValidity();
        return lastAccessedTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValidity();
        return Collections.enumeration(attributes.keySet());
    }

    /**
     * 마지막 접근 시간을 현재 시간으로 업데이트
     */
    public void updateLastAccessedTime() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * 세션을 기존 세션으로 표시
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * 세션이 유효한지 확인
     */
    public boolean isValid() {
        return valid && !isExpired();
    }

    /**
     * 세션이 만료되었는지 확인
     */
    public boolean isExpired() {
        if (maxInactiveInterval <= 0) {
            return false; // 만료되지 않는 세션
        }

        long currentTime = System.currentTimeMillis();
        long inactiveTime = (currentTime - lastAccessedTime) / 1000; // 초 단위
        return inactiveTime > maxInactiveInterval;
    }

    /**
     * 세션의 활성 시간을 초 단위로 반환
     */
    public long getActiveTime() {
        return (System.currentTimeMillis() - creationTime) / 1000;
    }

    /**
     * 세션의 비활성 시간을 초 단위로 반환
     */
    public long getInactiveTime() {
        return (System.currentTimeMillis() - lastAccessedTime) / 1000;
    }

    /**
     * 세션에 저장된 속성 개수 반환
     */
    public int getAttributeCount() {
        checkValidity();
        return attributes.size();
    }

    /**
     * 세션 정보를 Map으로 반환 (디버깅 및 모니터링용)
     */
    public java.util.Map<String, Object> getSessionInfo() {
        checkValidity();
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        info.put("id", id);
        info.put("creationTime", creationTime);
        info.put("lastAccessedTime", lastAccessedTime);
        info.put("maxInactiveInterval", maxInactiveInterval);
        info.put("isNew", isNew);
        info.put("valid", valid);
        info.put("expired", isExpired());
        info.put("activeTime", getActiveTime());
        info.put("inactiveTime", getInactiveTime());
        info.put("attributeCount", getAttributeCount());
        return info;
    }

    /**
     * 세션 속성을 복사하여 반환 (안전한 읽기 전용 접근)
     */
    public java.util.Map<String, Object> getAttributesCopy() {
        checkValidity();
        return new java.util.HashMap<>(attributes);
    }

    /**
     * 세션 유효성 검사
     */
    private void checkValidity() {
        if (!valid) {
            throw new IllegalStateException("Session has been invalidated");
        }
        if (isExpired()) {
            invalidate();
            throw new IllegalStateException("Session has expired");
        }
    }

    @Override
    public String toString() {
        return String.format("StandardHttpSession{id='%s', creationTime=%d, " +
                        "lastAccessedTime=%d, maxInactiveInterval=%d, " +
                        "isNew=%b, valid=%b, expired=%b, attributeCount=%d}",
                id, creationTime, lastAccessedTime, maxInactiveInterval,
                isNew, valid, isExpired(), attributes.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StandardHttpSession that = (StandardHttpSession) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}