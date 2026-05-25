package DBConnect;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    public static Connection getConnection() throws SQLException {
        
        // MySQL 드라이버 명시적 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL 드라이버를 찾을 수 없습니다.", e);
        }

        Properties props = new Properties();
        try (InputStream is = DBConnector.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new SQLException("config.properties 파일을 찾을 수 없습니다.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new SQLException("config.properties 파일을 읽을 수 없습니다.", e);
        }

        String URL  = props.getProperty("db.url");
        String USER = props.getProperty("db.user");
        String PASS = props.getProperty("db.password");

        return DriverManager.getConnection(URL, USER, PASS);
    }

    public DBConnector() {}
}