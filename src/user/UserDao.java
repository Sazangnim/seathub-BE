package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DBConnect.DBconnector1;
import user.User;

// SQL 사용하는 메소드들
public class UserDao {

	// 1. login_id 중복 확인
	public boolean findByLoginId(String login_id) {
		String sql ="SELECT COUNT(*) FROM user WHERE login_id = ?";
		try (Connection conn = DBconnector1.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, login_id);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1) > 0; // 중복 O
			}
		} catch (Exception e) {
			System.out.println("login_id 중복 확인 오류: " + e.getMessage());
		}
		return false; // 중복 X
	}
	
	// 2. user_name 중복 확인
	public boolean findByUsername(String user_name) {
		String sql ="SELECT COUNT(*) FROM user WHERE user_name = ?";
		try (Connection conn = DBconnector1.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, user_name);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1) > 0; // 중복 O
			}
		} catch (Exception e) {
			System.out.println("user_name 중복 확인 오류: " + e.getMessage());
		}
		return false; // 중복 X
	}
	
	// 3. User 등록
	// 순서: String role, String user_name, String login_id, String password, String email,
	// String gender, int age, String business_number
	// -> 입력 받는 순서대로
	public int insertUser(User user) {
		String sql = "INSERT INTO user "
				+ "(role, user_name, login_id, password, email, gender, age, business_number) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try(Connection conn = DBconnector1.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			
			pstmt.setString(1, user.getRole());
			pstmt.setString(2, user.getUser_name());
			pstmt.setString(3, user.getLogin_id());
			pstmt.setString(4, user.getPassword());
			pstmt.setString(5, user.getEmail());
			pstmt.setString(6, user.getGender());
			pstmt.setInt(7, user.getAge());
			pstmt.setString(8, user.getBusiness_number());
			
			return pstmt.executeUpdate(); // INSERT문 실행 -> 성공한 행(user) 횟수 반환 = 1
		} catch (Exception e) {
			// 필요성 확인
			System.out.println("회원가입 DB 오류: " + e.getMessage());
		}
		return 0;
	}
	
	// 4. 로그인
	public User findByLoginIdAndPassword(String login_id, String password) {
		String sql = "SELECT * FROM user WHERE login_id = ? AND password =?";
		
		try(Connection conn = DBconnector1.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			
			pstmt.setString(1, login_id);
			pstmt.setString(2, password);
			
			ResultSet rs = pstmt.executeQuery(); // SELECT문 실행
			
			// user 정보 존재하는 경우 -> 현재 user 값 불러오기
			if (rs.next()) {
				return new User(
						// user_id는 AUTO INCREASEMENT
						rs.getInt("user_id"),
						rs.getString("role"),
						rs.getString("user_name"),
						rs.getString("login_id"),
						rs.getString("password"),
						rs.getString("email"),
						rs.getString("gender"),
						rs.getInt("age"),
						rs.getString("business_number")
						);
			}
		} catch (Exception e) {
			// 필요성 확인
			System.out.println("로그인 DB 오류: " + e.getMessage());
		}
		return null;
	}
}
