package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	
		//ip 확인후 수정합니다.
		private static final String URL = "jdbc:mysql://개인 url";  
		private static final String USER = "유저명";
	 	private static final String PASS = "패스워드"; //자프실2

		
		public static Connection getConnection() throws SQLException {
			return DriverManager.getConnection(URL, USER, PASS);
		}
		public DBConnector() {}
	
		
}