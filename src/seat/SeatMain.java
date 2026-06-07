package seat;

import java.util.List;
import java.util.Scanner;
import user.User;

public class SeatMain {

    private static final Scanner scanner = new Scanner(System.in);
    private static final SeatService seatService = new SeatService();

    public static void main(String[] args) {
    	/* 테스트용 코드 주석 처리 해놨습니다!
        System.out.print("카페 ID를 입력하세요: ");
        int cafeId = scanner.nextInt();
        
        
        User testUser = new User(1, "USER", "테스트", "user100", "1234", "test@test.com", "M", 25, null);
        
        run(cafeId, testUser); // 테스트용 run
        */
        
        scanner.close();
    }

    // 고정 상단바 출력 메서드
    private static void printHeader() {
        System.out.println("\n---------------------------------");
        System.out.println("             SEATHUB             ");
        System.out.println("---------------------------------");
    }
    
    
    
 // 앞 팀원 카페 선택 이후 여기서 진입
    public static int run(int cafeId, User loginUser) {
        while (true) {
            printHeader();
            System.out.println("  1. 일반석 / 노트북석 조회 및 발권");
            System.out.println("  2. 회의실 조회 및 예약");
            System.out.println("  0. 뒤로가기");
            System.out.println("---------------------------------");
            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                	int result = seatMenu(cafeId, loginUser);
                	if (result == 1) {
                        return 1; // 회원탈퇴 → 온보딩
                    } else if (result == 0) {
                        return 0; // 예약 후 마이페이지 뒤로가기 → 카페메뉴
                    }
                	
                	}
                case 2 -> {
                	int result = roomMenu(cafeId, loginUser);
                	if (result == 1) {
                        return 1; // 회원탈퇴 → 온보딩
                    } else if (result == 0) {
                        return 0; // 예약 후 마이페이지 뒤로가기 → 카페메뉴
                    }
                	}
                case 0 -> { return 0; }
                default -> System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
            }
        }
    }

    // 일반석/노트북석: 목록 출력 → 선택 → 발권
    private static int seatMenu(int cafeId, User loginUser) {
        // 잘못 입력 시 목록에 머물도록 함수 전체를 while문으로 감싸기
        while (true) {
            List<String[]> seats = seatService.getSeatList(cafeId);
            if (seats.isEmpty()) { 
                System.out.println("[안내] 현재 조회 가능한 좌석이 없습니다."); 
                return 0; // 좌석이 아예 없으면 메인 메뉴판으로 돌아감
            }

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
            System.out.println("---------------------------------");
            System.out.print("선택: ");
            int idx = scanner.nextInt();
            
            // 0을 누르면 이 함수를 종료하고 1, 2, 0번이 있는 메인 메뉴판으로 나감
            if (idx == 0) return 0; 
            
            // 번호를 잘못 입력했을 때 return 대신 continue를 써서 좌석 목록을 다시 보여줌
            if (idx < 1 || idx > seats.size()) { 
                System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
                continue;
            }

            String[] selected = seats.get(idx - 1);
            int seatId = Integer.parseInt(selected[0]);
            
            String currentStatus = seatService.checkSeatStatus(seatId);

            // 이미 사용 중인 좌석일 때도 메뉴판으로 튕기지 않고 목록을 다시 띄워줌
            if ("OCCUPIED".equalsIgnoreCase(currentStatus)) {
                System.out.println("\n[오류] 해당 좌석은 이미 사용 중인 좌석입니다.");
                System.out.println(">> 다른 좌석을 선택해 주세요.");
                System.out.println("---------------------------------");
                
                // 안전장치: 혹시 모를 스캐너 버퍼 찌꺼기 청소
                scanner.nextLine(); 
                
                continue; 
            }

            // 발권 진행 (여기까지 도달했다면 완벽히 유효한 좌석 번호임)
            System.out.println("\n▶ 선택한 좌석: " + selected[1] + " (" + selected[2] + ")");
            
            System.out.print("이용 시간 입력 (시간): ");
            int hours = scanner.nextInt();
            
            // 서비스에서 중복 유저 걸러서 false가 리턴되면, 메뉴로 안 튕기고 목록을 다시 보여줌
            boolean isSuccess = seatService.issueTicket(loginUser.getUser_id(), Integer.parseInt(selected[0]), hours);
            if (!isSuccess) {
            	System.out.println(">> 발권에 실패했습니다. 다시 시도해 주세요.");
                continue; // 다시 좌석 선택 목록의 처음으로 돌아감
            }
            
            // 발권 성공 시 팀원의 마이페이지로 이동하도록 수정
            System.out.println("\n=================================");
            System.out.println(">> 발권이 완료되었습니다! 마이페이지로 이동합니다...");
            System.out.println("=================================");
            
            int myPageResult = mypage.MyPageMain.runMyPage(loginUser.getLogin_id());
            
            if (myPageResult == 1) {
            	return 1;
            }
            
            return 0; 
        }
    }

    // 회의실: 목록 출력 → 선택 → 예약
    private static int roomMenu(int cafeId, User loginUser) {
        // 잘못 입력 시 머물 수 있게 while문 감싸기
        while (true) {
            List<String[]> rooms = seatService.getRoomList(cafeId);
            if (rooms.isEmpty()) { 
                System.out.println("[안내] 현재 조회 가능한 회의실이 없습니다."); 
                return 0; 
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

            System.out.println("\n예약할 회의실 번호를 선택하세요. (0: 뒤로가기)");
            System.out.println("---------------------------------");
            System.out.print("선택: ");
            int idx = scanner.nextInt();
            if (idx == 0) return 0;
            
            if (idx < 1 || idx > rooms.size()) { 
                System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
                continue;
            }

            String[] selected = rooms.get(idx - 1);

            // 예약 진행
            System.out.println("\n▶ 선택한 회의실: " + selected[1]);
            System.out.print("예약 날짜 입력 (예: 2026-05-26): ");
            String date = scanner.next();
            System.out.print("시작 시간 입력 (예: 14:00): ");
            String startT = scanner.next();
            System.out.print("종료 시간 입력 (예: 16:00): ");
            String endT = scanner.next();

            String startTime = date + " " + startT + ":00";
            String endTime = date + " " + endT + ":00";

            boolean isSuccess = seatService.reserveRoom(loginUser.getUser_id(), Integer.parseInt(selected[0]), date, startTime, endTime);
            
            // 실패(SQL 에러, 이미 선점된 예약 등)했을 경우 마이페이지로 가지 않고 다시 회의실 목록으로
            if (!isSuccess) {
                System.out.println("\n[오류] 예약에 실패했습니다. 입력한 시간이나 예약 상태를 확인 후 다시 시도해 주세요.");
                System.out.println("---------------------------------");
                continue; 
            }
            
            // 예약 성공 시 팀원의 마이페이지로 이동하도록 수정
            System.out.println("\n=================================");
            System.out.println(">> 예약이 완료되었습니다! 마이페이지로 이동합니다...");
            System.out.println("=================================");
            
            int seatResult = mypage.MyPageMain.runMyPage(loginUser.getLogin_id());
            
            if (seatResult == 1) {
            	return 1;
            }
            return 0; 
        }
    }

    // 초 → "X시간 Y분" 변환
    private static String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) return hours + "시간 " + minutes + "분";
        return minutes + "분";
    }
}