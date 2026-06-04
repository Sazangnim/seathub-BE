package seat;

import java.sql.SQLException;
import java.util.List;

public class SeatService {

    private final SeatDAO seatDAO = new SeatDAO();

    // 일반석 / 노트북석 목록 조회
    public List<String[]> getSeatList(int cafeId) {
        try {
            return seatDAO.getSeatList(cafeId);
        } catch (SQLException e) {
            System.out.println("[오류] 좌석 조회 실패: " + e.getMessage());
            return List.of();
        }
    }

    // 회의실 목록 조회
    public List<String[]> getRoomList(int cafeId) {
        try {
            return seatDAO.getRoomList(cafeId);
        } catch (SQLException e) {
            System.out.println("[오류] 회의실 조회 실패: " + e.getMessage());
            return List.of();
        }
    }

    // 발권
    public boolean issueTicket(int userId, int seatId, int usageHours) {
        try {
            // 사전에 '조회'하고 '등록'하는 이중 작업 제거
            // 곧바로 트랜잭션 진입하는 코드로 수정
            seatDAO.insertTicket(userId, seatId, usageHours);
            
            System.out.println("[완료] 발권 성공! 좌석 ID: " + seatId + " | 이용시간: " + usageHours + "시간");
            return true;
        } catch (SQLException e) {
            // DAO에서 비관적 락(FOR UPDATE) 검증 후 던진 예외를 가로채서 처리
            if ("ALREADY_OCCUPIED".equals(e.getMessage())) {
                System.out.println("[오류] 선택하신 좌석은 이미 다른 사용자가 결제 중이거나 사용 중입니다.");
            } else if ("USER_ALREADY_HAS_TICKET".equals(e.getMessage())) {
                System.out.println("[오류] 이미 현재 이용 중인 다른 좌석이 존재합니다.");
                System.out.println(">> 1인당 1개의 좌석만 이용 가능합니다.");
            } else {
                System.out.println("[오류] 시스템 문제로 인해 발권에 실패했습니다: " + e.getMessage());
            }
            return false;
        }
    }

    // 회의실 예약
    // 시간 충돌 검증 및 트랜잭션 매핑
    public boolean reserveRoom(int userId, int seatId, String reservationDate,
                                String startTime, String endTime) {
        try {
            seatDAO.insertReservation(userId, seatId, reservationDate, startTime, endTime);

            System.out.println("[완료] 예약 성공!");
            System.out.println("  ▶ 좌석 ID: " + seatId + " | 예약 날짜: " + reservationDate);
            System.out.println("  ▶ 이용 시간: " + startTime + " ~ " + endTime);
            return true;
        } catch (SQLException e) {
            // DAO의 트랜잭션 도중 'NOT (end_time <= ? OR start_time >= ?)' 조건에 걸린 경우
            if ("RESERVATION_CONFLICT".equals(e.getMessage())) {
                System.out.println("[오류] 해당 시간대에 이미 다른 예약이 존재합니다.");
            } else {
                System.out.println("[오류] 예약 실패: " + e.getMessage());
            }
            return false;
        }
    }
    
    public String checkSeatStatus(int seatId) {
        try {
            return seatDAO.getSeatStatus(seatId); // OCCUPIED 또는 AVAILABLE 반환
        } catch (SQLException e) {
            return "ERROR";
        }
    }
}