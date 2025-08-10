package winter.session;

import winter.http.HttpSession;
import winter.http.StandardHttpSession;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 세션의 생명주기를 관리하는 중앙 관리자
 */
public class SessionManager {

    private final Map<String, StandardHttpSession> sessions = new ConcurrentHashMap<>();
    private final SessionConfig config;
    private final ScheduledExecutorService cleanupExecutor;
    private final SecureRandom secureRandom = new SecureRandom();

    // 통계 정보
    private volatile long totalSessionsCreated = 0;
    private volatile long totalSessionsExpired = 0;
    private volatile long totalSessionsInvalidated = 0;

    /**
     * SessionManager 생성자
     * @param config 세션 설정
     */
    public SessionManager(SessionConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SessionConfig cannot be null");
        }

        this.config = config;
        config.validate(); // 설정 유효성 검증

        // 백그라운드 정리 작업 스케줄링
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SessionCleanup");
            t.setDaemon(true); // 데몬 스레드로 설정
            return t;
        });

        // 주기적으로 만료된 세션 정리
        cleanupExecutor.scheduleAtFixedRate(
                this::cleanupExpiredSessions,
                config.getCleanupInterval(),
                config.getCleanupInterval(),
                TimeUnit.SECONDS
        );
    }

    /**
     * 새 세션 생성
     * @return 생성된 HttpSession
     */
    public HttpSession createSession() {
        String sessionId = generateSessionId();
        StandardHttpSession session = new StandardHttpSession(sessionId, config.getMaxInactiveInterval());

        sessions.put(sessionId, session);
        totalSessionsCreated++;

        return session;
    }

    /**
     * 세션 ID로 세션 조회
     * @param sessionId 세션 ID
     * @return HttpSession 또는 null (없거나 만료된 경우)
     */
    public HttpSession getSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return null;
        }

        StandardHttpSession session = sessions.get(sessionId);
        if (session != null) {
            if (session.isValid()) {
                session.updateLastAccessedTime();
                session.setNew(false);
                return session;
            } else {
                // 무효하거나 만료된 세션 제거
                removeSession(sessionId);
                totalSessionsExpired++;
            }
        }

        return null;
    }

    /**
     * 세션 제거
     * @param sessionId 제거할 세션 ID
     */
    public void removeSession(String sessionId) {
        if (sessionId != null) {
            StandardHttpSession removed = sessions.remove(sessionId);
            if (removed != null) {
                totalSessionsInvalidated++;
            }
        }
    }

    /**
     * 특정 세션 무효화
     * @param sessionId 무효화할 세션 ID
     */
    public void invalidateSession(String sessionId) {
        if (sessionId != null) {
            StandardHttpSession session = sessions.get(sessionId);
            if (session != null) {
                session.invalidate();
                removeSession(sessionId);
            }
        }
    }

    /**
     * 만료된 세션들을 정리
     */
    public void cleanupExpiredSessions() {
        int expiredCount = 0;

        // 만료된 세션 찾아서 제거
        for (Map.Entry<String, StandardHttpSession> entry : sessions.entrySet()) {
            StandardHttpSession session = entry.getValue();
            if (!session.isValid()) {
                sessions.remove(entry.getKey());
                expiredCount++;
                totalSessionsExpired++;
            }
        }

        if (expiredCount > 0) {
            System.out.printf("[SessionManager] Cleaned up %d expired sessions. Active sessions: %d%n",
                    expiredCount, sessions.size());
        }
    }

    /**
     * 모든 세션 무효화 (서버 종료시 등)
     */
    public void invalidateAllSessions() {
        int invalidatedCount = 0;
        for (Map.Entry<String, StandardHttpSession> entry : sessions.entrySet()) {
            StandardHttpSession session = entry.getValue();
            if (session.isValid()) {
                try {
                    session.invalidate();
                    invalidatedCount++;
                } catch (IllegalStateException e) {
                    // 이미 무효화된 세션은 무시
                }
            }
        }
        totalSessionsInvalidated += invalidatedCount;
        sessions.clear();
    }

    /**
     * 세션 관리자 종료 (리소스 해제)
     */
    public void shutdown() {
        try {
            invalidateAllSessions();
        } catch (Exception e) {
            System.err.println("세션 정리 중 오류 발생: " + e.getMessage());
        }

        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 보안 강화된 세션 ID 생성
     * @return 고유한 세션 ID
     */
    private String generateSessionId() {
        // 128bit 엔트로피 보장
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);

        // 현재 시간의 나노초 추가 (추가 엔트로피)
        long nanoTime = System.nanoTime();

        // Base64 URL-safe 인코딩 대신 Hex 사용 (더 안전)
        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        sb.append(Long.toHexString(nanoTime));

        String sessionId = sb.toString();

        // 중복 확인 (거의 불가능하지만 안전을 위해)
        while (sessions.containsKey(sessionId)) {
            secureRandom.nextBytes(randomBytes);
            sb = new StringBuilder();
            for (byte b : randomBytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            sb.append(Long.toHexString(System.nanoTime()));
            sessionId = sb.toString();
        }

        return sessionId;
    }

    // 통계 및 모니터링 메서드들

    /**
     * 현재 활성 세션 수
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }

    /**
     * 총 생성된 세션 수
     */
    public long getTotalSessionsCreated() {
        return totalSessionsCreated;
    }

    /**
     * 총 만료된 세션 수
     */
    public long getTotalSessionsExpired() {
        return totalSessionsExpired;
    }

    /**
     * 총 무효화된 세션 수
     */
    public long getTotalSessionsInvalidated() {
        return totalSessionsInvalidated;
    }

    /**
     * 세션 관리자 설정 정보
     */
    public SessionConfig getConfig() {
        return config;
    }

    /**
     * 모든 세션의 기본 정보 조회 (관리용)
     */
    public Map<String, Map<String, Object>> getAllSessionsInfo() {
        Map<String, Map<String, Object>> result = new ConcurrentHashMap<>();

        for (Map.Entry<String, StandardHttpSession> entry : sessions.entrySet()) {
            StandardHttpSession session = entry.getValue();
            if (session.isValid()) {
                result.put(entry.getKey(), session.getSessionInfo());
            }
        }

        return result;
    }

    /**
     * 특정 세션의 상세 정보 조회
     */
    public Map<String, Object> getSessionInfo(String sessionId) {
        StandardHttpSession session = sessions.get(sessionId);
        if (session != null && session.isValid()) {
            return session.getSessionInfo();
        }
        return null;
    }

    /**
     * 세션 관리자 전체 상태 정보
     */
    public Map<String, Object> getManagerStatus() {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("activeSessionCount", getActiveSessionCount());
        status.put("totalSessionsCreated", getTotalSessionsCreated());
        status.put("totalSessionsExpired", getTotalSessionsExpired());
        status.put("totalSessionsInvalidated", getTotalSessionsInvalidated());
        status.put("cleanupInterval", config.getCleanupInterval());
        status.put("maxInactiveInterval", config.getMaxInactiveInterval());
        status.put("cookieName", config.getCookieName());
        status.put("cookieSecure", config.isCookieSecure());
        status.put("cookieHttpOnly", config.isCookieHttpOnly());
        return status;
    }

    /**
     * 수동으로 세션 정리 실행
     */
    public int manualCleanup() {
        int beforeCount = sessions.size();
        cleanupExpiredSessions();
        int afterCount = sessions.size();
        return beforeCount - afterCount;
    }

    /**
     * 특정 사용자의 모든 세션 찾기 (사용자 속성 기반)
     * @param userAttributeName 사용자 식별 속성명 (예: "username", "userId")
     * @param userAttributeValue 사용자 식별 값
     * @return 해당 사용자의 세션 ID 리스트
     */
    public java.util.List<String> findSessionsByUser(String userAttributeName, Object userAttributeValue) {
        java.util.List<String> userSessions = new java.util.ArrayList<>();

        if (userAttributeName == null || userAttributeValue == null) {
            return userSessions;
        }

        for (Map.Entry<String, StandardHttpSession> entry : sessions.entrySet()) {
            StandardHttpSession session = entry.getValue();
            if (session.isValid()) {
                Object sessionUserValue = session.getAttribute(userAttributeName);
                if (userAttributeValue.equals(sessionUserValue)) {
                    userSessions.add(entry.getKey());
                }
            }
        }

        return userSessions;
    }

    /**
     * 특정 사용자의 다른 세션들 무효화 (동시 로그인 제한)
     * @param currentSessionId 현재 세션 ID (유지할 세션)
     * @param userAttributeName 사용자 식별 속성명
     * @param userAttributeValue 사용자 식별 값
     * @return 무효화된 세션 수
     */
    public int invalidateOtherUserSessions(String currentSessionId, String userAttributeName, Object userAttributeValue) {
        java.util.List<String> userSessions = findSessionsByUser(userAttributeName, userAttributeValue);
        int invalidatedCount = 0;

        for (String sessionId : userSessions) {
            if (!sessionId.equals(currentSessionId)) {
                invalidateSession(sessionId);
                invalidatedCount++;
            }
        }

        return invalidatedCount;
    }

    @Override
    public String toString() {
        return String.format("SessionManager{activeSessions=%d, totalCreated=%d, " +
                        "totalExpired=%d, totalInvalidated=%d, config=%s}",
                getActiveSessionCount(), getTotalSessionsCreated(),
                getTotalSessionsExpired(), getTotalSessionsInvalidated(), config);
    }
}
