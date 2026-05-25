package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/mypage")
public class MyPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // 테스트용 유저 ID 고정 
        int loggedInUserId = 1; 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnector.getConnection();
            
            // 1. 유저 정보 조회
            String userSql = "SELECT user_name, login_id, email, role FROM user WHERE user_id = ?";
            pstmt = conn.prepareStatement(userSql);
            pstmt.setInt(1, loggedInUserId);
            rs = pstmt.executeQuery();
            
            String userName = "";
            String loginId = "";
            String email = "";
            String role = "";
            
            if (rs.next()) {
                userName = rs.getString("user_name");
                loginId = rs.getString("login_id");
                email = rs.getString("email");
                role = rs.getString("role");
            }
            rs.close();
            pstmt.close();
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"user_name\":\"").append(userName).append("\",");
            json.append("\"login_id\":\"").append(loginId).append("\",");
            json.append("\"email\":\"").append(email).append("\",");
            json.append("\"role\":\"").append(role).append("\",");
            
            // 2. 일반 회원(USER) 이용 내역 조회
            json.append("\"history\": [");
            if ("USER".equals(role)) {
                String historySql = "SELECT t.start_time, c.cafe_name, s.seat_name, t.usage_hours " +
                                    "FROM ticket t " +
                                    "JOIN seat s ON t.seat_id = s.seat_id " +
                                    "JOIN study_cafe c ON s.cafe_id = c.cafe_id " +
                                    "WHERE t.user_id = ? " +
                                    "ORDER BY t.start_time DESC";
                
                pstmt = conn.prepareStatement(historySql);
                pstmt.setInt(1, loggedInUserId);
                rs = pstmt.executeQuery();
                
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append("{");
                    json.append("\"date\":\"").append(rs.getTimestamp("start_time").toString().substring(0, 10)).append("\",");
                    json.append("\"cafe_name\":\"").append(rs.getString("cafe_name")).append("\",");
                    json.append("\"seat_info\":\"").append(rs.getString("seat_name")).append("\",");
                    json.append("\"hours\":").append(rs.getInt("usage_hours"));
                    json.append("}");
                    first = false;
                }
                rs.close();
                pstmt.close();
            }
            json.append("],");
            
            // 3. 사장 회원(OWNER) 분석 대시보드 조회
            json.append("\"dashboard\": {");
            if ("OWNER".equals(role)) {
                // 임시로 사장님이 소유한 첫 번째 카페 ID를 조회 (실제로는 동적으로 매핑)
                int targetCafeId = 1; 
                
                // [SQL 시나리오 A] 좌석 유형별 인기도 분석 (가장 인기가 많은 유형 1개 추출)
                String popSql = "SELECT s.seat_type, COUNT(*) AS usage_count " +
                                "FROM seat s JOIN (SELECT seat_id FROM ticket UNION ALL SELECT seat_id FROM room_reservation) u " +
                                "ON s.seat_id = u.seat_id " +
                                "WHERE s.cafe_id = ? GROUP BY s.seat_type ORDER BY usage_count DESC LIMIT 1";
                
                pstmt = conn.prepareStatement(popSql);
                pstmt.setInt(1, targetCafeId);
                rs = pstmt.executeQuery();
                String popularityResult = "데이터 없음";
                if (rs.next()) {
                    popularityResult = rs.getString("seat_type") + " (" + rs.getInt("usage_count") + "회)";
                }
                rs.close();
                pstmt.close();
                
                // [SQL 시나리오 B] 피크타임 분석 (가장 이용량이 많은 시간대 1개 추출)
                String peakSql = "SELECT HOUR(u.start_time) AS hour, COUNT(*) AS usage_count " +
                                 "FROM (SELECT seat_id, start_time FROM ticket UNION ALL SELECT seat_id, start_time FROM room_reservation) u " +
                                 "JOIN seat s ON u.seat_id = s.seat_id " +
                                 "WHERE s.cafe_id = ? GROUP BY HOUR(u.start_time) ORDER BY usage_count DESC LIMIT 1";
                
                pstmt = conn.prepareStatement(peakSql);
                pstmt.setInt(1, targetCafeId);
                rs = pstmt.executeQuery();
                String peakTimeResult = "데이터 없음";
                if (rs.next()) {
                    int hour = rs.getInt("hour");
                    peakTimeResult = hour + "시 ~ " + (hour + 1) + "시";
                }
                rs.close();
                pstmt.close();
                
                // 완성된 DB 데이터를 JSON 문자열에 바인딩
                json.append("\"popularity\":\"").append(popularityResult).append("\",");
                json.append("\"peak_time\":\"").append(peakTimeResult).append("\",");
                json.append("\"growth_rate\":\"+15.42%\""); // 전월 대비 증감률 쿼리 결과용 (우선 가독성용 고정)
            }
            json.append("}");
            
            json.append("}");
            out.print(json.toString());
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"DB 연동 에러 발생\"}");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}