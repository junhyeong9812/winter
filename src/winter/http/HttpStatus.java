package winter.http;

/**
 * HTTP 상태 코드를 나타내는 열거형
 * RFC 7231에 정의된 표준 HTTP 상태 코드들을 포함
 */
public enum HttpStatus {

    // 1xx Informational
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),

    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    // 4xx Client Error
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    I_AM_A_TEAPOT(418, "I'm a teapot"),
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    TOO_EARLY(425, "Too Early"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * HTTP 상태 코드 값을 반환
     *
     * @return HTTP 상태 코드 (예: 200, 404, 500)
     */
    public int value() {
        return this.code;
    }

    /**
     * HTTP 상태 코드 값을 반환 (value()와 동일, Spring 호환성을 위해 추가)
     *
     * @return HTTP 상태 코드 (예: 200, 404, 500)
     */
    public int getCode() {
        return this.code;
    }

    /**
     * HTTP 상태 코드의 설명 문구를 반환
     *
     * @return 상태 코드 설명 (예: "OK", "Not Found", "Internal Server Error")
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * 1xx 정보성 응답인지 확인
     */
    public boolean is1xxInformational() {
        return this.code >= 100 && this.code < 200;
    }

    /**
     * 2xx 성공 응답인지 확인
     */
    public boolean is2xxSuccessful() {
        return this.code >= 200 && this.code < 300;
    }

    /**
     * 3xx 리다이렉션 응답인지 확인
     */
    public boolean is3xxRedirection() {
        return this.code >= 300 && this.code < 400;
    }

    /**
     * 4xx 클라이언트 오류 응답인지 확인
     */
    public boolean is4xxClientError() {
        return this.code >= 400 && this.code < 500;
    }

    /**
     * 5xx 서버 오류 응답인지 확인
     */
    public boolean is5xxServerError() {
        return this.code >= 500 && this.code < 600;
    }

    /**
     * 오류 응답인지 확인 (4xx 또는 5xx)
     */
    public boolean isError() {
        return is4xxClientError() || is5xxServerError();
    }

    /**
     * 상태 코드 값으로 HttpStatus를 찾아 반환
     *
     * @param statusCode HTTP 상태 코드 값
     * @return 해당하는 HttpStatus 열거형 값
     * @throws IllegalArgumentException 유효하지 않은 상태 코드인 경우
     */
    public static HttpStatus valueOf(int statusCode) {
        for (HttpStatus status : values()) {
            if (status.code == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }

    /**
     * 상태 코드 값으로 HttpStatus를 찾아 반환 (없으면 null)
     *
     * @param statusCode HTTP 상태 코드 값
     * @return 해당하는 HttpStatus 열거형 값 또는 null
     */
    public static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : values()) {
            if (status.code == statusCode) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code + " " + this.reasonPhrase;
    }
}