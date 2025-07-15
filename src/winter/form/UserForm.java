package winter.form;

public class UserForm {
    private String name;
    private String email;

    // 기본 생성자 필수
    public UserForm() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
