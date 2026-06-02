package user;

import user.UserDao;
import user.User;

public class UserService {
	
	private UserDao ud = new UserDao();
	
	// 1. 회원가입 검사
	public boolean signUp(User user) {
		
		int result = ud.insertUser(user);
		
		if (result > 0) {
			// 회원가입 성공
			return true;
		} else {
            // 회원가입 실패
            return false;
		}		
	}
	
	// 2. 로그인 검사
	public User login(String login_id, String password) {
		User user = ud.findByLoginIdAndPassword(login_id, password);
		
		if (user != null) {
			// 로그인 성공
			return user;
		} else {
			// 로그인 실패
			return null;
			
		}
	}
	
	// Dao 불러서 Main에 쓰인다.
	
	// 3. 중복확인 메소드 연결
	public boolean isUserNameDuplicated(String user_name) {
	    return ud.findByUsername(user_name);
	}

	public boolean isLoginIdDuplicated(String login_id) {
	    return ud.findByLoginId(login_id);
	}
}
