package DBConnect;

import java.sql.Connection;
import java.sql.SQLException;

public class DBconnectorTest1 {

    public static void main(String[] args) {
        try {
            Connection conn = DBconnector1.getConnection();

            System.out.println("DB 연결 성공!");
            System.out.println("현재 DB: " + conn.getCatalog());

            conn.close();
        } catch (SQLException e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }
    }
}