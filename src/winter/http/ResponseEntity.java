package winter.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 응답을 구성하는 엔터티 클래스
 * Spring의 ResponseEntity와 유사한 기능을 제공
 *
 * @param <T> 응답 본문의 타입
 */
public class ResponseEntity<T> {
    private final T body;
    private final HttpStatus statusCode;
    private final Map<String, String> headers;

    public ResponseEntity(T body, HttpStatus statusCode) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
    }

    public ResponseEntity(T body, HttpStatus statusCode, Map<String, String> headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = new HashMap<>(headers);
    }

    // 정적 팩토리 메서드들

    /**
     * 200 OK 응답 생성
     */
    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    /**
     * 200 OK 응답 생성 (본문 없음)
     */
    public static ResponseEntity<Void> ok() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 201 Created 응답 생성
     */
    public static <T> ResponseEntity<T> created(T body) {
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    /**
     * 204 No Content 응답 생성
     */
    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    /**
     * 400 Bad Request 응답 생성
     */
    public static <T> ResponseEntity<T> badRequest(T body) {
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 Bad Request 응답 생성 (본문 없음)
     */
    public static ResponseEntity<Void> badRequest() {
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    /**
     * 401 Unauthorized 응답 생성
     */
    public static <T> ResponseEntity<T> unauthorized(T body) {
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 403 Forbidden 응답 생성
     */
    public static <T> ResponseEntity<T> forbidden(T body) {
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * 404 Not Found 응답 생성
     */
    public static ResponseEntity<Void> notFound() {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    /**
     * 409 Conflict 응답 생성
     */
    public static <T> ResponseEntity<T> conflict(T body) {
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * 500 Internal Server Error 응답 생성
     */
    public static <T> ResponseEntity<T> internalServerError(T body) {
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 커스텀 상태 코드로 응답 생성 (본문 없음)
     */
    public static ResponseEntity<Void> withStatus(HttpStatus httpStatus) {
        return new ResponseEntity<>(null, httpStatus);
    }

    /**
     * 커스텀 상태 코드와 본문으로 응답 생성
     */
    public static <T> ResponseEntity<T> withStatus(HttpStatus httpStatus, T body) {
        return new ResponseEntity<>(body, httpStatus);
    }

    // Getter 메서드들
    public T getBody() {
        return body;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public boolean hasBody() {
        return body != null;
    }

    /**
     * 헤더를 추가한 새로운 ResponseEntity 반환
     */
    public ResponseEntity<T> withHeader(String name, String value) {
        Map<String, String> newHeaders = new HashMap<>(this.headers);
        newHeaders.put(name, value);
        return new ResponseEntity<>(this.body, this.statusCode, newHeaders);
    }

    /**
     * 여러 헤더를 추가한 새로운 ResponseEntity 반환
     */
    public ResponseEntity<T> withHeaders(Map<String, String> additionalHeaders) {
        Map<String, String> newHeaders = new HashMap<>(this.headers);
        newHeaders.putAll(additionalHeaders);
        return new ResponseEntity<>(this.body, this.statusCode, newHeaders);
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

    // Builder 패턴을 위한 정적 클래스
    public static class Builder<T> {
        private HttpStatus statusCode;
        private T body;
        private final Map<String, String> headers = new HashMap<>();

        private Builder(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }

        public static Builder<Void> withStatus(HttpStatus httpStatus) {
            return new Builder<>(httpStatus);
        }

        public static Builder<Void> ok() {
            return new Builder<>(HttpStatus.OK);
        }

        public static Builder<Void> created() {
            return new Builder<>(HttpStatus.CREATED);
        }

        public static Builder<Void> noContent() {
            return new Builder<>(HttpStatus.NO_CONTENT);
        }

        public static Builder<Void> badRequest() {
            return new Builder<>(HttpStatus.BAD_REQUEST);
        }

        public static Builder<Void> notFound() {
            return new Builder<>(HttpStatus.NOT_FOUND);
        }

        public Builder<T> header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder<T> headers(Map<String, String> additionalHeaders) {
            this.headers.putAll(additionalHeaders);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <U> Builder<U> body(U bodyValue) {
            Builder<U> newBuilder = new Builder<>(this.statusCode);
            newBuilder.body = bodyValue;
            newBuilder.headers.putAll(this.headers);
            return newBuilder;
        }

        public ResponseEntity<T> build() {
            return new ResponseEntity<>(this.body, this.statusCode, this.headers);
        }
    }
}