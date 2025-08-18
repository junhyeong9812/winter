package winter.form;

import winter.validation.*;

/**
 * 검증 어노테이션이 적용된 사용자 폼 클래스
 * 다양한 검증 어노테이션의 사용 예제
 */
public class ValidatedUserForm {

    @NotNull(message = "이름은 필수입니다")
    @NotEmpty(message = "이름을 입력해주세요")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
    private String name;

    @NotNull(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;

    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;

    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식을 입력해주세요 (예: 010-1234-5678)")
    private String phoneNumber;

    @NotNull(message = "나이는 필수입니다")
    private Integer age;

    @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다")
    private String bio;

    // 기본 생성자
    public ValidatedUserForm() {}

    // 전체 필드 생성자
    public ValidatedUserForm(String name, String email, String password,
                             String phoneNumber, Integer age, String bio) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.bio = bio;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "ValidatedUserForm{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", bio='" + bio + '\'' +
                '}';
    }
}