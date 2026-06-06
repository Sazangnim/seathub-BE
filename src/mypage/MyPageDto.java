package mypage;

public class MyPageDto {
    private String loginId, userName, role, email, gender, businessNumber;
    private int age;

    public MyPageDto(String loginId, String userName, String role, String email, String gender, int age, String businessNumber) {
        this.loginId = loginId;
        this.userName = userName;
        this.role = role;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.businessNumber = businessNumber;
    }

    public String getLoginId() { return loginId; }
    public String getUserName() { return userName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public String getBusinessNumber() { return businessNumber; }
}