package mypage;

import java.util.*;
import java.time.LocalDateTime;
import user.UserService;

public class MyPageMain {
    private static Scanner sc = new Scanner(System.in);
    private static MyPageDao dao = new MyPageDao();
    private static final String ERR_MSG = ">> 잘못된 입력입니다.\n>> 다시 입력해주세요.";

    public static void main(String[] args) {
    	// [운영 환경 연동 시 아래 코드로 교체 예정]
        // HttpSession session = request.getSession();
        // String loginId = (String) session.getAttribute("loginId");
        // runMyPage(loginId);
        
        // 연동 전 임시 테스트용
        runMyPage("user01");
    }

    public static int runMyPage(String loginId) {
        while (true) {
            MyPageDto u = dao.getUserProfile(loginId);
            UserService us = new UserService(); // for 회원탈퇴
            if (u == null) {
                System.out.println("사용자를 찾을 수 없습니다.");
                return 0;
            }

            System.out.println("\n---------------------------------");
            System.out.println("            마이페이지            ");
            System.out.println("---------------------------------");

            if (u.getRole().equals("USER")) {
                System.out.println("1. 내 정보 조회 및 수정");
                System.out.println("2. 내 예약 내역 확인");
                System.out.println("3. 회원 탈퇴");
                System.out.println("0. 이전 메뉴로 돌아가기");
            } else if (u.getRole().equals("OWNER")) {
                System.out.println("1. 내 정보 조회 및 수정");
                System.out.println("2. 내 카페 대시보드");
                System.out.println("3. 회원 탈퇴");
                System.out.println("0. 이전 메뉴로 돌아가기");
            }
            System.out.println("---------------------------------");

            System.out.print("선택 >> ");
            String menu = sc.nextLine();

            if (menu.equals("1")) {
                showAndEditProfile(u);
            } else if (menu.equals("2")) {
                if (u.getRole().equals("OWNER")) {
                    showCafeList(u.getLoginId()); // 카페 목록 페이지
                } else {
                    showUserReservations(u.getLoginId());
                }
            } else if (menu.equals("3")) {
            	// 회원 탈퇴 기능 추가
            	while (true) {
            		System.out.println("---------------------------------");
            		System.out.println(">> 회원탈퇴를 위해 비밀번호를 입력해주세요.");
                    System.out.print("\n비밀번호 입력: ");
                    String chkpw = sc.nextLine();

                    if (dao.checkPassword(u.getLoginId(), chkpw)) {
                        System.out.println(">> 비밀번호가 일치합니다.\n");
                        System.out.println("---------------------------------");
                        System.out.println("          회원탈퇴 중......");
                        System.out.println("---------------------------------");
                        
                        // 시간 소요되는 것처럼 보이고 싶어서 thread 기능 추가했습니다.
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        boolean result = us.deleteUser(u.getLoginId());
                        
                    	if (result) {
                    		System.out.println("\n>> 회원탈퇴가 완료되었습니다.");
                    		System.out.println(">> 첫 화면으로 이동합니다.\n");
                    		return 1; // 마이 -> 메인 -> 온보딩
                    		// [!] 잘 돌아가는지 메인페이지 연동 이후 체크할 것.
                    	} else {
                    		System.out.println(ERR_MSG);
                    		continue;
                    	}
                    } 
                    else {
                        System.out.println(">> 비밀번호가 일치하지 않습니다.\n>> 다시 입력해주세요.");
                    }
                }
            } else if (menu.equals("0")) {
            	// 메인 메뉴로 연결!!!
                return 0; 
            } else {
                System.out.println(ERR_MSG);
            }
        }
    }

    public static void showAndEditProfile(MyPageDto u) {
        while (true) {
            System.out.println("\n---------------------------------");
            System.out.println("         내 정보 조회 및 수정      ");
            System.out.println("---------------------------------");
            System.out.println("아이디: " + u.getLoginId());
            System.out.println("이름: " + u.getUserName());
            System.out.println("회원 유형: " + u.getRole());
            System.out.println("현재 이메일: " + u.getEmail());
            System.out.println("성별: " + u.getGender());
            System.out.println("나이: " + u.getAge());
            if(u.getRole().equals("OWNER")) 
            	System.out.println("사업자 번호: " + u.getBusinessNumber());

            System.out.println("---------------------------------");
            System.out.println("1. 이메일 변경하기  2. 비밀번호 변경하기  \n0. 뒤로 가기");
            System.out.println("---------------------------------");

            System.out.print("선택 >> ");
            String sub = sc.nextLine();

            if (sub.equals("1")) {
                while(true) {
                    System.out.print("\n새 이메일 입력하기\n또는 [0] 뒤로가기 >> ");
                    String e = sc.nextLine();
                    if (e.equals("0")) break; // 내 정보 조회 메뉴로 돌아감
                    if (!e.isBlank()) {
                        dao.updateEmail(u.getLoginId(), e);
                        System.out.println("이메일 변경이 완료되었습니다!");
                        break;
                    }
                    System.out.println(ERR_MSG);
                }
            } else if (sub.equals("2")) {
                while (true) {
                    System.out.print("\n현재 비밀번호 입력\n또는 [0] 뒤로가기 >> ");
                    String cur = sc.nextLine();
                    if (cur.equals("0")) 
                    	break; // 내 정보 조회 메뉴로 돌아감

                    if (dao.checkPassword(u.getLoginId(), cur)) {
                        while(true) {
                            System.out.print("\n새 비밀번호 입력하기 >> ");
                            String p = sc.nextLine();
                            if (!p.isBlank() && !p.equals("0")) {
                                dao.updatePassword(u.getLoginId(), p);
                                System.out.println(">> 비밀번호 변경이 완료되었습니다!");
                                break;
                            }
                            System.out.println(ERR_MSG);
                        }
                        break;
                    } else {
                        System.out.println(">> 비밀번호가 일치하지 않습니다.\n>> 다시 입력해주세요.");
                    }
                }
            } else if (sub.equals("0")) {
                return; // 마이페이지로 돌아감
            } else {
                System.out.println(ERR_MSG);
            }
        }
    }

    public static void showUserReservations(String loginId) {
        while (true) {
            System.out.println("\n---------------------------------");
            System.out.println("          내 예약 내역            ");
            System.out.println("---------------------------------");

            List<Map<String, Object>> history = dao.getReservationHistory(loginId);
            LocalDateTime now = dao.getDbNow();

            System.out.println("[현재 이용 중인 좌석]");
            boolean hasCurrent = false;
            for (Map<String, Object> r : history) {
                LocalDateTime start = ((java.sql.Timestamp) r.get("startTime")).toLocalDateTime();
                LocalDateTime end = ((java.sql.Timestamp) r.get("endTime")).toLocalDateTime();

                if (now.isAfter(start) && now.isBefore(end)) {
                    long diffMinutes = java.time.Duration.between(now, end).toMinutes();
                    long leftHours = diffMinutes / 60;
                    long leftMinutes = diffMinutes % 60;

                    System.out.println("- 매장명: " + r.get("cafeName"));
                    System.out.println("- 좌석 번호: " + r.get("seatId"));
                    System.out.println("- 이용 시간: " + start.toLocalTime() + " ~ " + end.toLocalTime());
                    System.out.printf("- 남은 시간: %02d:%02d\n", leftHours, leftMinutes);
                    hasCurrent = true;
                }
            }
            if (!hasCurrent) System.out.println("현재 이용 중인 좌석이 없습니다.");

            System.out.println("\n[과거 이용 내역]");
            for (Map<String, Object> r : history) {
                LocalDateTime end = ((java.sql.Timestamp) r.get("endTime")).toLocalDateTime();
                if (now.isAfter(end)) {
                    String date = ((java.sql.Timestamp) r.get("startTime")).toString().substring(0, 10);
                    System.out.println("- " + r.get("cafeName") + "(" + date + ")");
                }
            }
            System.out.println("---------------------------------");
            System.out.print("[0] 뒤로 가기 >> ");
            if (sc.nextLine().equals("0")) 
            	return; // 마이페이지로 돌아감
            else System.out.println(ERR_MSG);
        }
    }

    public static void showCafeList(String loginId) {
        List<Map<String, Object>> cafes = dao.getMyCafeList(loginId);
        while (true) {
            System.out.println("---------------------------------");
            System.out.println("      내 카페 대시 보드 - 매장 선택      ");
            System.out.println("---------------------------------");
            for(int i = 0; i < cafes.size(); i++) {
                System.out.println((i + 1) + ". " + cafes.get(i).get("name"));
            }
            System.out.println("0. 뒤로 가기");
            System.out.println("---------------------------------");

            System.out.print("선택 >> ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 0) 
                	return; // OWNER 메뉴로 돌아감
                if (choice > 0 && choice <= cafes.size()) {
                    showOwnerDashboard((int)cafes.get(choice - 1).get("id"));
                } else {
                    System.out.println(ERR_MSG);
                }
            } catch (NumberFormatException e) {
                System.out.println(ERR_MSG);
            }
        }
    }

    public static void showOwnerDashboard(int cafeId) {
        String cafeName = dao.getCafeName(cafeId);
        while(true) {
            System.out.println("\n---------------------------------");
            System.out.println(" 내 카페 대시보드 - "+cafeName+" ");
            System.out.println("---------------------------------");

            // 1. 좌석 통계
            System.out.println("좌석 유형별 인기도");
            List<Map<String, Object>> seatStats = dao.getSeatStats(cafeId);
            int rank = 1;
            for(Map<String, Object> s : seatStats) {
                System.out.println("· " + rank++ + "위. " + s.get("type") + " (" + s.get("cnt") + "회 이용)");
            }
            
            // 2. 피크타임
            System.out.println("---------------------------------");
            System.out.println("피크타임 분석");
            List<Map<String, Object>> peaks = dao.getPeakTimes(cafeId);
            for(Map<String, Object> p : peaks) {
                System.out.println("· " + p.get("hr") + "시 ~ " + ((int)p.get("hr") + 1) + "시 (" + p.get("cnt") + "명 방문)");
            }
            
            // 3. 증감률
            System.out.println("---------------------------------");
            Map<String, Integer> g = dao.getGrowthRate(cafeId); 
            double rate = g.get("prev") == 0 ? 0 : ((double)(g.get("cur") - g.get("prev")) / g.get("prev")) * 100;
            System.out.println("이번 달 이용 건수: " + g.get("cur") + "건 | 지난 달: " + g.get("prev") + "건");
            System.out.println("전월 대비 증감률: " + String.format("%.2f", rate) + "%");
            System.out.println("---------------------------------");
            
            // 종료 및 뒤로가기
            System.out.print("[0] 뒤로 가기 >> ");
            if (sc.nextLine().equals("0")) {
                return; 
            } else {
                System.out.println(ERR_MSG);
            }
        }
    }
}