package seat;

import java.util.List;
import java.util.Scanner;

public class SeatMain {

    private static final Scanner scanner = new Scanner(System.in);
    private static final SeatService seatService = new SeatService();

    public static void main(String[] args) {
        System.out.print("카페 ID를 입력하세요: ");
        int cafeId = scanner.nextInt();
        run(cafeId);
        scanner.close();
    }

    // 고정 상단바 출력 메서드
    private static void printHeader() {
        System.out.println("\n---------------------------------");
        System.out.println("             SEATHUB             ");
        System.out.println("---------------------------------");
    }

    // 앞 팀원 카페 선택 이후 여기서 진입
    public static void run(int cafeId) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("  1. 일반석 / 노트북석 조회 및 발권");
            System.out.println("  2. 회의실 조회 및 예약");
            System.out.println("  0. 뒤로가기");
            System.out.println("=============================");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> seatMenu(cafeId);
                case 2 -> roomMenu(cafeId);
                case 0 -> { System.out.println("이전 화면으로 돌아갑니다."); return; }
                default -> System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
            }
        }
    }

    // 일반석/노트북석: 목록 출력 → 선택 → 발권
    private static void seatMenu(int cafeId) {
        List<String[]> seats = seatService.getSeatList(cafeId);
        if (seats.isEmpty()) { 
            System.out.println("[안내] 현재 조회 가능한 좌석이 없습니다."); 
            return; 
        }

        printHeader();
        System.out.println("\n=== 일반석 / 노트북석 목록 ===");
        System.out.printf("%-5s %-12s %-10s %-10s %-12s%n",
                          "No", "이름", "종류", "상태", "잔여시간");
        System.out.println("-".repeat(55));

        for (int i = 0; i < seats.size(); i++) {
            String[] s = seats.get(i);
            String remaining = s[4] != null ? formatSeconds(Long.parseLong(s[4])) : "-";
            System.out.printf("%-5d %-12s %-10s %-10s %-12s%n",
                              i + 1, s[1], s[2], s[3], remaining);
        }

        System.out.println("\n예약할 좌석 번호를 선택하세요 (0: 뒤로가기): ");
        int idx = scanner.nextInt();
        if (idx == 0) return;
        if (idx < 1 || idx > seats.size()) { 
        	System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
            return; 
        }

        String[] selected = seats.get(idx - 1);
        int seatId = Integer.parseInt(selected[0]);
        
        String currentStatus = seatService.checkSeatStatus(seatId);

        if ("OCCUPIED".equals(currentStatus)) {
            System.out.println("[오류] 해당 좌석은 사용중인 좌석입니다.");
            return; // 
        }

        // 발권 진행
        System.out.println("\n▶ 선택한 좌석: " + selected[1] + " (" + selected[2] + ")");
        System.out.print("사용자 ID 입력: ");
        int userId = scanner.nextInt();
        System.out.print("이용 시간 입력 (시간): ");
        int hours = scanner.nextInt();

        seatService.issueTicket(userId, Integer.parseInt(selected[0]), hours);
    }

    // 회의실: 목록 출력 → 선택 → 예약
    private static void roomMenu(int cafeId) {
        List<String[]> rooms = seatService.getRoomList(cafeId);
        if (rooms.isEmpty()) { 
            System.out.println("[안내] 현재 조회 가능한 회의실이 없습니다."); 
            return; 
        }

        printHeader();
        System.out.println("\n=== 회의실 목록 ===");
        System.out.printf("%-5s %-12s %-10s %-22s %-22s%n",
                          "No", "이름", "상태", "다음예약 시작", "다음예약 종료");
        System.out.println("-".repeat(75));

        for (int i = 0; i < rooms.size(); i++) {
            String[] r = rooms.get(i);
            System.out.printf("%-5d %-12s %-10s %-22s %-22s%n",
                              i + 1, r[1], r[3],
                              r[4] != null ? r[4] : "-",
                              r[5] != null ? r[5] : "-");
        }

        System.out.print("\n예약할 회의실 번호를 선택하세요. (0: 뒤로가기) : ");
        int idx = scanner.nextInt();
        if (idx == 0) return;
        if (idx < 1 || idx > rooms.size()) { 
        	System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
            return; 
        }

        String[] selected = rooms.get(idx - 1);

        // 예약 진행
        System.out.println("\n▶ 선택한 회의실: " + selected[1]);
        System.out.print("사용자 ID 입력: ");
        int userId = scanner.nextInt();
        System.out.print("예약 날짜 입력 (예: 2026-05-26): ");
        String date = scanner.next();
        System.out.print("시작 시간 입력 (예: 14:00): ");
        String startT = scanner.next();
        System.out.print("종료 시간 입력 (예: 16:00): ");
        String endT = scanner.next();

        String startTime = date + " " + startT + ":00";
        String endTime = date + " " + endT + ":00";

        seatService.reserveRoom(userId, Integer.parseInt(selected[0]), date, startTime, endTime);
    }

    // 초 → "X시간 Y분" 변환
    private static String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) return hours + "시간 " + minutes + "분";
        return minutes + "분";
    }
}