package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	
		//Ip 확인후 수정합니다.
		private static final String URL = "jdbc:mysql://개인로컬DB/seathub";  
		private static final String USER = "username";
	 	private static final String PASS = "password";

		
		public static Connection getConnection() throws SQLException {
			return DriverManager.getConnection(URL, USER, PASS);
		}
		public DBConnector() {}
	
		
}