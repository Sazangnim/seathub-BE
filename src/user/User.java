package user;

// user_id + 입력 받는 순서
public class User {
	private int user_id; // AUTO INCREASEMENT
    private String role;
    private String user_name;
    private String login_id;
    private String password;
    private String email;
    private String gender;
    private int age;
    private String business_number; // role: USER -> null값 기본
    
    // 1. login
    public User(String login_id, String password) {
    	this.login_id = login_id;
    	this.password = password;
    }
    
    // 2. signup
    public User(String role, String user_name, String login_id, String password, String email,
    		String gender, int age, String business_number) {
    	this.role = role;
    	this.user_name = user_name;
    	this.login_id = login_id;
    	this.password = password;
    	this.email = email;
    	this.gender = gender;
    	this.age = age;
    	this.business_number = business_number;
    }
    
    // 3. 로그인 조회용(User 정보 사용할 때)
    public User(int user_id, String role, String user_name, String login_id, String password, String email,
            String gender, int age, String business_number) {
        this.user_id = user_id;
        this.role = role;
        this.user_name = user_name;
        this.login_id = login_id;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.business_number = business_number;
    }
    
    // getter
	public int getUser_id() {
		return user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public String getLogin_id() {
		return login_id;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public String getBusiness_number() {
		return business_number;
	}

	public String getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}
    
    
    
}
