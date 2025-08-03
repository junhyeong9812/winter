package winter.form;

/**
 * 사용자 프로필 정보를 담는 폼 클래스
 *
 * 파일 업로드와 일반 폼 데이터를 함께 처리하는 예제로 사용됩니다.
 * @ModelAttribute 어노테이션과 함께 사용하여 자동 바인딩됩니다.
 */
public class UserProfile {

    private String name;
    private String email;
    private String bio;
    private String phone;
    private String website;
    private String avatarPath;
    private int age;
    private boolean isPublic;

    public UserProfile() {
        this.isPublic = true; // 기본값
    }

    public UserProfile(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    /**
     * 사용자 이름을 반환합니다.
     */
    public String getName() {
        return name;
    }

    /**
     * 사용자 이름을 설정합니다.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 이메일 주소를 반환합니다.
     */
    public String getEmail() {
        return email;
    }

    /**
     * 이메일 주소를 설정합니다.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 자기소개를 반환합니다.
     */
    public String getBio() {
        return bio;
    }

    /**
     * 자기소개를 설정합니다.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * 전화번호를 반환합니다.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 전화번호를 설정합니다.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 웹사이트 URL을 반환합니다.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * 웹사이트 URL을 설정합니다.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * 아바타 이미지 파일 경로를 반환합니다.
     */
    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * 아바타 이미지 파일 경로를 설정합니다.
     */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    /**
     * 나이를 반환합니다.
     */
    public int getAge() {
        return age;
    }

    /**
     * 나이를 설정합니다.
     */
    public void setAge(int age) {
        this.age = Math.max(0, age);
    }

    /**
     * 프로필 공개 여부를 반환합니다.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * 프로필 공개 여부를 설정합니다.
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * 아바타 이미지가 있는지 확인합니다.
     */
    public boolean hasAvatar() {
        return avatarPath != null && !avatarPath.trim().isEmpty();
    }

    /**
     * 프로필이 유효한지 검증합니다.
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                isValidEmail(email);
    }

    /**
     * 이메일 형식이 유효한지 간단히 검증합니다.
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * 프로필 검증 오류 메시지를 반환합니다.
     */
    public String getValidationError() {
        if (name == null || name.trim().isEmpty()) {
            return "이름은 필수 입력 항목입니다.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "이메일은 필수 입력 항목입니다.";
        }
        if (!isValidEmail(email)) {
            return "올바른 이메일 형식이 아닙니다.";
        }
        if (age < 0 || age > 150) {
            return "올바른 나이를 입력해주세요.";
        }
        return null;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", age=" + age +
                ", isPublic=" + isPublic +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserProfile that = (UserProfile) obj;
        return age == that.age &&
                isPublic == that.isPublic &&
                (name != null ? name.equals(that.name) : that.name == null) &&
                (email != null ? email.equals(that.email) : that.email == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (isPublic ? 1 : 0);
        return result;
    }
}