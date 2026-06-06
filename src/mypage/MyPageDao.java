package mypage;

import java.sql.*;
import java.util.*;
import DBConnect.DBconnector1;

public class MyPageDao {
    private Connection getConnection() throws Exception { 
        return DBconnector1.getConnection(); 
    }

    public MyPageDto getUserProfile(String loginId) {
        String sql = "SELECT * FROM user WHERE login_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new MyPageDto(
                rs.getString("login_id"), rs.getString("user_name"), rs.getString("role"), 
                rs.getString("email"), rs.getString("gender"), rs.getInt("age"), rs.getString("business_number")
            );
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
 // 1. 좌석 통계 (카페 ID 기준)
    public List<Map<String, Object>> getSeatStats(int cafeId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.seat_type, COUNT(*) as cnt " +
                 "FROM (SELECT seat_id FROM room_reservation UNION ALL SELECT seat_id FROM ticket) r " +
                 "JOIN seat s ON r.seat_id = s.seat_id " +
                 "WHERE s.cafe_id = ? GROUP BY s.seat_type ORDER BY cnt DESC LIMIT 3";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", rs.getString("seat_type"));
                m.put("cnt", rs.getInt("cnt"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. 피크타임 (카페 ID 기준)
    public List<Map<String, Object>> getPeakTimes(int cafeId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT HOUR(start_time) as hr, COUNT(*) as cnt " +
                 "FROM (SELECT start_time, seat_id FROM room_reservation UNION ALL SELECT start_time, seat_id FROM ticket) r " +
                 "JOIN seat s ON r.seat_id = s.seat_id " +
                 "WHERE s.cafe_id = ? GROUP BY hr ORDER BY cnt DESC LIMIT 3";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("hr", rs.getInt("hr"));
                m.put("cnt", rs.getInt("cnt"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. 증감률 (카페 ID 기준)
    public Map<String, Integer> getGrowthRate(int cafeId) {
        Map<String, Integer> map = new HashMap<>();
        // MONTH() 대신 DATE_SUB()를 사용하여 정확한 이전 달 계산
        String sql = "SELECT " +
                 "(SELECT COUNT(*) FROM (SELECT start_time, seat_id FROM room_reservation UNION ALL SELECT start_time, seat_id FROM ticket) r JOIN seat s ON r.seat_id = s.seat_id " +
                 " WHERE s.cafe_id = ? AND start_time >= DATE_FORMAT(NOW(), '%Y-%m-01') AND start_time < NOW()) as cur, " +
                 "(SELECT COUNT(*) FROM (SELECT start_time, seat_id FROM room_reservation UNION ALL SELECT start_time, seat_id FROM ticket) r JOIN seat s ON r.seat_id = s.seat_id " +
                 " WHERE s.cafe_id = ? AND start_time >= DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m-01') " +
                 " AND start_time < DATE_FORMAT(NOW(), '%Y-%m-01')) as prev";
        
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            pstmt.setInt(2, cafeId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) { 
                map.put("cur", rs.getInt("cur")); 
                map.put("prev", rs.getInt("prev")); 
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    // 4. 카페 목록 가져오기
    public List<Map<String, Object>> getMyCafeList(String loginId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sc.cafe_id, sc.cafe_name FROM study_cafe sc " +
                     "JOIN user u ON sc.user_id = u.user_id WHERE u.login_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("cafe_id"));
                m.put("name", rs.getString("cafe_name"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    

    public List<Map<String, Object>> getReservationHistory(String loginId) {
        List<Map<String, Object>> list = new ArrayList<>();
        // UNION ALL을 사용하여 일반석과 회의실 예약을 모두 가져옴
        // seat_name을 가져오고, 불필요한 초 계산은 SQL에서 제거함
        String sql = 
            "SELECT sc.cafe_name, s.seat_name, t.start_time, t.end_time " +
            "FROM ticket t " +
            "JOIN seat s ON t.seat_id = s.seat_id " +
            "JOIN study_cafe sc ON s.cafe_id = sc.cafe_id " +
            "JOIN user u ON t.user_id = u.user_id " +
            "WHERE u.login_id = ? " +
            "UNION ALL " +
            "SELECT sc.cafe_name, s.seat_name, r.start_time, r.end_time " +
            "FROM room_reservation r " +
            "JOIN seat s ON r.seat_id = s.seat_id " +
            "JOIN study_cafe sc ON s.cafe_id = sc.cafe_id " +
            "JOIN user u ON r.user_id = u.user_id " +
            "WHERE u.login_id = ? " +
            "ORDER BY start_time DESC";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginId);
            pstmt.setString(2, loginId); // 물음표가 2개이므로 두 번 설정
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("cafeName", rs.getString("cafe_name"));
                m.put("seatName", rs.getString("seat_name")); // 키 이름을 seatName으로 통일
                m.put("startTime", rs.getTimestamp("start_time"));
                m.put("endTime", rs.getTimestamp("end_time"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public String getCafeName(int cafeId) {
        String sql = "SELECT cafe_name FROM study_cafe WHERE cafe_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("cafe_name");
        } catch (Exception e) { e.printStackTrace(); }
        return "알 수 없는 카페";
    }
    
    public java.time.LocalDateTime getDbNow() {
        String sql = "SELECT NOW()"; 
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getTimestamp(1).toLocalDateTime();
            }
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
        return java.time.LocalDateTime.now(); 
    }

    public void updateEmail(String loginId, String newEmail) {
        String sql = "UPDATE user SET email = ? WHERE login_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, loginId);
            pstmt.executeUpdate();
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
    }
    
    public boolean checkPassword(String loginId, String currentPassword) {
        String sql = "SELECT COUNT(*) FROM user WHERE login_id = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, loginId);
            pstmt.setString(2, currentPassword);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(String loginId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE login_id = ?";
        try (Connection conn = getConnection(); 
        	PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, newPassword);
            pstmt.setString(2, loginId);
            pstmt.executeUpdate();
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
    }
}