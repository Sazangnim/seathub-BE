package seat;

import DBConnect.DBconnector1;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    // 일반석/노트북석 조회 
    // 실시간 잔여 시간 연산 및 LEFT JOIN 활용
    public List<String[]> getSeatList(int cafeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT s.seat_id, s.seat_name, s.seat_type, " +
                     "CASE WHEN t.ticket_id IS NULL THEN '사용 가능' ELSE '사용중' END AS status, " +
                     "CASE WHEN t.ticket_id IS NOT NULL " +
                     "THEN TIMESTAMPDIFF(SECOND, NOW(), t.end_time) ELSE NULL END AS remaining_seconds " +
                     "FROM seat s " +
                     "LEFT JOIN ticket t ON s.seat_id = t.seat_id " +
                     "AND NOW() BETWEEN t.start_time AND t.end_time " +
                     "WHERE s.cafe_id = ? " +
                     "AND s.seat_type IN ('SEAT', 'NOTEBOOK') " +
                     "ORDER BY s.seat_type, s.seat_name";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("seat_id"),
                        rs.getString("seat_name"),
                        rs.getString("seat_type"),
                        rs.getString("status"),
                        rs.getString("remaining_seconds")
                    });
                }
            }
        }
        return list;
    }

    // 회의실 조회
    // 서브쿼리 활용하여 다음 예약 시간도 함께 조회
    public List<String[]> getRoomList(int cafeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT s.seat_id, s.seat_name, s.seat_type, " +
                     "CASE WHEN now_r.reservation_id IS NOT NULL THEN '사용중' " +
                     "WHEN next_r.start_time IS NOT NULL " +
                     "AND TIMESTAMPDIFF(MINUTE, NOW(), next_r.start_time) < 60 THEN '예약중' " +
                     "ELSE '사용 가능' END AS status, " +
                     "next_r.start_time AS next_reservation_start, " +
                     "next_r.end_time AS next_reservation_end " +
                     "FROM seat s " +
                     "LEFT JOIN room_reservation now_r ON s.seat_id = now_r.seat_id " +
                     "AND NOW() BETWEEN now_r.start_time AND now_r.end_time " +
                     "LEFT JOIN (SELECT r1.seat_id, r1.start_time, r1.end_time " +
                     "FROM room_reservation r1 " +
                     "WHERE r1.start_time = (SELECT MIN(r2.start_time) " +
                     "FROM room_reservation r2 " +
                     "WHERE r2.seat_id = r1.seat_id AND r2.start_time > NOW())) next_r " +
                     "ON s.seat_id = next_r.seat_id " +
                     "WHERE s.cafe_id = ? AND s.seat_type = 'ROOM' " +
                     "ORDER BY s.seat_name";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                        rs.getString("seat_id"),
                        rs.getString("seat_name"),
                        rs.getString("seat_type"),
                        rs.getString("status"),
                        rs.getString("next_reservation_start"),
                        rs.getString("next_reservation_end")
                    });
                }
            }
        }
        return list;
    }

    // 좌석 상태 확인 
    // ticket 테이블 기준으로 실시간 조회
    public String getSeatStatus(int seatId) throws SQLException {
        // ticket 테이블에서 현재 유효한 티켓이 있는지 확인
        String sql = "SELECT " +
                     "CASE WHEN COUNT(t.ticket_id) > 0 THEN 'OCCUPIED' ELSE 'AVAILABLE' END AS status " +
                     "FROM seat s " +
                     "LEFT JOIN ticket t ON s.seat_id = t.seat_id " +
                     "AND NOW() BETWEEN t.start_time AND t.end_time " +
                     "WHERE s.seat_id = ?";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seatId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        }
        return null; // 존재하지 않는 좌석
    }

    // 발권 
    // 트랜잭션 적용 - ticket INSERT + seat status UPDATE 원자적 처리
    // 동시성 락 -> 티켓 추가 -> 좌석 마스터 업데이트
    public void insertTicket(int userId, int seatId, int usageHours) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM ticket WHERE seat_id = ? AND NOW() BETWEEN start_time AND end_time";
        // 해당 유저가 현재 이용 중인 다른 티켓이 있는지 검사하는 쿼리 추가
        String checkUserSql = "SELECT COUNT(*) FROM ticket WHERE user_id = ? AND NOW() BETWEEN start_time AND end_time";
        String insertSql = "INSERT INTO ticket (user_id, seat_id, start_time, end_time, usage_hours) " +
                           "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? HOUR), ?)";
        String updateSeatSql = "UPDATE seat SET status = 'OCCUPIED' WHERE seat_id = ?"; // 명시적 상태 변경 추가

        try (Connection conn = DBconnector1.getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            try {
                // 동시성 제어 - FOR UPDATE 구문 -> 락 획득
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql + " FOR UPDATE")) {
                    checkStmt.setInt(1, seatId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            conn.rollback();
                            throw new SQLException("ALREADY_OCCUPIED");
                        }
                    }
                }
                
                // 유저 동시성 제어 - 한 유저가 동시 발권하는 것 차단
                try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserSql + " FOR UPDATE")) {
                    checkUserStmt.setInt(1, userId);
                    try (ResultSet rs = checkUserStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            conn.rollback();
                            throw new SQLException("USER_ALREADY_HAS_TICKET"); 
                        }
                    }
                }

                // 티켓 이력 테이블 데이터 삽입
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, seatId);
                    pstmt.setInt(3, usageHours);
                    pstmt.setInt(4, usageHours);
                    pstmt.executeUpdate();
                }

                // 마스터 좌석 테이블 상태 동기화 (트리거 대체 로직)
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSeatSql)) {
                    updateStmt.setInt(1, seatId);
                    updateStmt.executeUpdate();
                }

                conn.commit(); // 모든 작업 성공 시 최종 반영
            } catch (SQLException e) {
                conn.rollback(); // 하나라도 실패 시 전체 취소
                throw e;
            } finally {
                conn.setAutoCommit(true); // 커넥션 풀 반환 전 기본값 복원
            }
        }
    }

    // 회의실 예약 
    // 트랜잭션 적용 - 충돌 체크 + INSERT 원자적 처리
    // 충돌 범위 락 -> 예약 추가 -> 좌석 상태 업데이트
    public void insertReservation(int userId, int seatId, String reservationDate,
                                   String startTime, String endTime) throws SQLException {
        String conflictSql = "SELECT COUNT(*) FROM room_reservation " +
                             "WHERE seat_id = ? AND NOT (end_time <= ? OR start_time >= ?) FOR UPDATE";
        String insertSql = "INSERT INTO room_reservation " +
                           "(user_id, seat_id, reservation_date, start_time, end_time) " +
                           "VALUES (?, ?, ?, ?, ?)";
        String updateSeatSql = "UPDATE seat SET status = 'RESERVED' WHERE seat_id = ?"; // 명시적 상태 변경

        try (Connection conn = DBconnector1.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 상호 배제를 위한 범위 락 설정
                try (PreparedStatement checkStmt = conn.prepareStatement(conflictSql)) {
                    checkStmt.setInt(1, seatId);
                    checkStmt.setString(2, startTime);
                    checkStmt.setString(3, endTime);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            conn.rollback();
                            throw new SQLException("RESERVATION_CONFLICT");
                        }
                    }
                }

                // 회의실 예약 테이블 데이터 삽입
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, seatId);
                    pstmt.setString(3, reservationDate);
                    pstmt.setString(4, startTime);
                    pstmt.setString(5, endTime);
                    pstmt.executeUpdate();
                }

                // 마스터 좌석 테이블 상태 동기화 (트리거 대체 로직)
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSeatSql)) {
                    updateStmt.setInt(1, seatId);
                    updateStmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}