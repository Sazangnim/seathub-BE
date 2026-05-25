package DBConnect;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector2 {
	
	private static final String CONFIG_PATH = "config.properties";

    public static Connection getConnection() throws SQLException {
    Properties props = new Properties();
	
    // config.properties 파일 읽기
    try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
        props.load(fis);
    } catch (IOException e) {
        throw new SQLException("config.properties 파일을 읽을 수 없습니다.", e);
    }
    
	// config.properties에서 입력된 정보를 사용한다.
    String URL = props.getProperty("db.url");  
	String USER = props.getProperty("db.user");
	String PASS = props.getProperty("db.password");

		
	
	return DriverManager.getConnection(URL, USER, PASS);
	}
	public DBConnector2() {}
	
		
}