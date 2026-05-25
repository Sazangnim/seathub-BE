package user;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    private static final String CONFIG_FILE = "config.properties";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();

        // JDBC 드라이버 강제 로드
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC 드라이버를 찾을 수 없습니다.", e);
        }

        // config.properties 읽기
        try (InputStream is = DBConnector.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new SQLException("config.properties 파일을 클래스패스에서 찾을 수 없습니다.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new SQLException("config.properties 파일을 읽을 수 없습니다.", e);
        }

        String URL = props.getProperty("db.url");
        String USER = props.getProperty("db.user");
        String PASS = props.getProperty("db.password");

        return DriverManager.getConnection(URL, USER, PASS);
    }

    public DBConnector() {}
}
