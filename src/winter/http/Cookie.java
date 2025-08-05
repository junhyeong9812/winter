package winter.http;

/**
 * HTTP 쿠키를 표현하는 클래스
 * RFC 6265 표준을 준수합니다.
 * */
public class Cookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private int maxAge = -1;        // -1: 세션 쿠키, 0: 즉시 삭제, >0: 초 단위 수명
    private boolean httpOnly = false; // XSS 방지
    private boolean secure = false;   // HTTPS 전용
    private String sameSite;         // CSRF 방지 (Strict, Lax, None)

    /**
     * 쿠키 생성자
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * */
    public Cookie(String name,String value){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Cookie name cannot be null or empty");
        }
        this.name = name.trim();
        this.value = value != null ? value : "";
    }

    //Getter 메서드
    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }

    public String getPath(){
        return path;
    }

    public String getDomain(){
        return domain;
    }

    public int getMaxAge(){
        return maxAge;
    }

    public boolean isHttpOnly(){
        return httpOnly;
    }

    public boolean isSecure(){
        return secure;
    }

    public String getSameSite(){
        return sameSite;
    }

    //Setter 메서드들 (체이닝 방식)
    public Cookie setValue(String value){
        this.value =value != null ? value : "";
        return this;
    }

    public Cookie setPath(String path){
        this.path = path;
        return this;
    }

    public Cookie setDomain(String domain){
        this.domain = domain;
        return this;
    }

    public Cookie setMaxAge(int maxAge){
        this.maxAge = maxAge;
        return this;
    }

    public Cookie setHttpOnly(boolean httpOnly){
        this.httpOnly=httpOnly;
        return this;
    }

    public Cookie setSameSite(String sameSite){
        //유효한 SameSite 값 검증
        if(sameSite != null &&
            !sameSite.equalsIgnoreCase("strict") &&
            !sameSite.equalsIgnoreCase("Lax") &&
            !sameSite.equalsIgnoreCase("None")){
            throw new IllegalArgumentException("Invalid SameSite value: " + sameSite);
        }
        this.sameSite =sameSite;
        return this;
    }

    /**
     * 세션 쿠키로 설정 (브라우저 종료 시 삭제)
     * */
    public Cookie makeSessionCookie(){
        this.maxAge = -1;
        return this;
    }

    /**
     * 즉시 삭제되는 쿠키로 설정
     * */
    public Cookie makeExpired(){
        this.maxAge = 0;
        return this;
    }

    /**
     * 쿠키 수명을 일 단위로 설정
     * */
    public Cookie setMaxAgeDays(int days){
        this.maxAge =days * 24 * 60 * 60;
        return this;
    }

    /**
     * 쿠키 수명을 시간 단위로 설정
     * */
    public Cookie setMaxAgeHours(int hours){
        this.maxAge = hours * 60 * 60;
        return this;
    }

    /**
     * 보안 쿠키로 설정 (HTTPS + HttpOnly + sameSite=Strict)
     * */
    public Cookie makeSecure(){
        this.secure =true;
        this.httpOnly = true;
        this.sameSite = "Strict";
        return this;
    }

    /**
     * 쿠키가 만료되었는지 확인
     * */
    public boolean isExpired(){
        return maxAge == 0;
    }

    /**
     * 쿠키가 세션 쿠키인지 확인
     * */
    public boolean isSessionCookie(){
        return maxAge == -1;
    }

    @Override
    public String toString(){
        return String.format("Cookie{name='%s', value='%s', path='%s', domain='%s', " +
                            "maxAge=%d, httpOnly=%b, secure=%b, sameSite='%s'}",
                            name, value, path, domain, maxAge, httpOnly, secure, sameSite);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return  false;

        Cookie cookie = (Cookie) obj;
        return name.equals(cookie.name) &&
                (path != null ? path.equals(cookie.path): cookie.path == null)&&
                (domain != null ? domain.equals(cookie.domain) : cookie.domain == null);
    }

    @Override
    public int hashCode(){
        int result = name.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }
}
