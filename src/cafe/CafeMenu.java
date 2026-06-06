package cafe;

import java.util.List;
import java.util.Scanner;
import user.User;
import seat.SeatMain;
import mypage.MyPageMain;

public class CafeMenu {
	// 로그인 성공 후에 들어 올 수 있는 카페 메뉴 부분

	//학생 권한 제한 하기 위한 상수 추가했습니다 나이 수정은 여기서 하면됩니다!
    private static final int STUDENT_MIN_AGE = 13;
	private static final int STUDENT_MAX_AGE = 19;
	
	public static void cafeMenu(Scanner sc, User loginUser) {
        CafeDAO cafeDao = new CafeDAO();
        
        

        while (true) {
            showMenu();
            String choice = sc.nextLine();
       

            switch (choice) {
                case "1" -> {
                    List<Cafe> cafes = cafeDao.findAll();
                    printCafes(cafes);
                    selectCafe(sc, loginUser, cafes);
                }
                case "2" -> {
                    System.out.print("지역 입력 (Ewha / Hongdae / Hyehwa / Jongno): ");
                    String region = sc.nextLine();
                    List<Cafe> cafes = cafeDao.findByRegion(region);
                    printCafes(cafes);
                    selectCafe(sc, loginUser, cafes);
                }
                case "3" -> {
                    System.out.print("태그 입력 (24H / Laptop / Quiet / Women Only / Student Only): ");
                    String tag = sc.nextLine();
                    List<Cafe> cafes = cafeDao.findByTag(tag);
                    printCafes(cafes);
                    selectCafe(sc, loginUser, cafes);
                }
                case "4" -> {
                	// 사장 회원만 카페 등록 가능
                	// 로그인한 회원의 role이 OWNER가 아니면 등록 차단으로 해뒀습니다
                	if (!loginUser.getRole().equals("OWNER")) {
                        System.out.println(">> 사장 회원만 카페를 등록할 수 있습니다.");
                        continue;
                    }
                   
                    System.out.print("카페 이름: ");
                    String cafeName = sc.nextLine();
                    String region = selectRegion(sc);
                    System.out.print("주소: ");
                    String address = sc.nextLine();

                 // userId는 로그인한 사장 정보에서 자동으로 가져오도록 수정했습니다
                    Cafe newCafe = new Cafe(loginUser.getUser_id(), cafeName, region, address);
                    Cafe saved = cafeDao.save(newCafe);

                    String tags = selectTags(sc);
                    cafeDao.saveTags(saved.getCafeId(), tags);

                    System.out.println("---------------------------------");
                    System.out.println("          카페 등록 완료!         ");
                    System.out.println("---------------------------------");
                    //System.out.println("cafeId: " + saved.getCafeId());
                }
                case "5" -> {
                	//마이페이지로 연결
                	int result = MyPageMain.runMyPage(loginUser.getLogin_id());
                	if (result==1) {
                		return;
                	}		
                }
                case "0" -> {
                	System.out.println("---------------------------------");
                	System.out.println("          로그아웃 중......");
					System.out.println("---------------------------------");
                    return;
                }
                default -> System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
            }
        }
    }

    public static void showMenu() {
        System.out.println("\n---------------------------------");
        System.out.println("             SEATHUB             ");
        System.out.println("---------------------------------");
        System.out.println("1. 전체 카페 조회");
        System.out.println("2. 지역별 카페 조회");
        System.out.println("3. 태그별 카페 조회");
        System.out.println("4. 카페 등록 (사장회원)");
        System.out.println("5. 마이페이지");
        System.out.println("0. 로그아웃");
        System.out.println("---------------------------------");
        System.out.print("선택: ");
    }

    public static void printCafes(List<Cafe> cafes) {
        if (cafes.isEmpty()) {
            System.out.println("조회된 카페가 없습니다.");
            return;
        }
        System.out.println("\n===카페 목록===");
        System.out.printf("%-5s %-20s %-10s %-25s %-40s%n", "No", "카페명", "지역", "태그", "주소");
        System.out.println("-".repeat(100));
        for (int i = 0; i < cafes.size(); i++) {
        	Cafe c = cafes.get(i);
        	System.out.printf("%-5d %-20s %-10s %-25s %-40s%n",
                    i + 1,
                    c.getCafeName(),
                    c.getRegion(),
                    c.getTags() != null ? c.getTags() : "없음",
                    c.getAddress());
        
        }
    }
    
    //카페 선택에서 좌석 예약 연결하는 부분 (일반 회원만 가능하도록 수정완료)
    public static void selectCafe(Scanner sc, User loginUser, List<Cafe> cafes) {
        if (cafes.isEmpty()) {
            return;
        }

        while (true) {
            System.out.println("\n예약할 카페 번호를 입력해주세요. (0: 뒤로가기)");
            System.out.println("---------------------------------");
            System.out.print("선택: ");
            String input = sc.nextLine();

            if (input.trim().equals("0")) {
                return;
            }

            // 좌석 예약은 일반 회원(USER)만 이용 가능
            if (!loginUser.getRole().equals("USER")) {
                System.out.println(">> 좌석 예약은 일반 회원만 이용하실 수 있습니다.");
                return;
            }

            int no;
            try {
                no = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
                continue;
            }

            // 입력한 번호가 카페 목록에 있는지 확인! ( 좌석 예역 콘솔과 통일되도록 cafeId-> No 검색으로 바꿨습니다 ) 
            if (no < 1 || no > cafes.size()) {
                System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
                continue;
            }
            // No(순번) -> 실제 cafe_id로 변환해서 좌석 화면에 넘길 수 있도록
            Cafe selected = cafes.get(no-1);
            int cafeId = selected.getCafeId();
            
            //Women Only 권한 제한 하기
            if (selected.getTags() != null && selected.getTags().contains("Women Only")
                    && !loginUser.getGender().equals("FEMALE")) {
                System.out.println(">> 여성 전용 카페는 여성 회원만 이용하실 수 있습니다.");
                continue;
            }
            
            //Student Only 카페는 학생(13~19세)으로 권한 제한하기
            if (selected.getTags() != null && selected.getTags().contains("Student Only")
                    && (loginUser.getAge() < STUDENT_MIN_AGE || loginUser.getAge() > STUDENT_MAX_AGE)) {
                System.out.println(">> 학생 전용 카페는 학생 회원만 이용하실 수 있습니다.");
                continue;
            }
           
           

            System.out.println(">> 좌석 예약 화면으로 이동합니다.");
            
            // 좌석 발권/예약 연결 (좌석 코드 머지 후 아래 주석 해제하겠습니다)
            SeatMain.run(cafeId, loginUser);
            return;
        }
    }
    
    // 지역 선택
    public static String selectRegion(Scanner sc) {
    	
        String[] regionList = {"Ewha", "Hongdae", "Hyehwa", "Jongno"};

        System.out.println("\n--- 지역 선택 ---");
      
        for (int i = 0; i < regionList.length; i++) {
            System.out.println((i + 1) + ". " + regionList[i]);
        }

        while (true) {
            System.out.print("지역 번호 입력 (1 ~ 4): ");
            
            String input = sc.nextLine().trim();
            try {
                int idx = Integer.parseInt(input);
                
                
                if (idx >= 1 && idx <= regionList.length) {
                    return regionList[idx - 1]; 
                }
                
            } 
            catch (NumberFormatException e) { }
            System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
        }
       }

    // 태그 선택
    public static String selectTags(Scanner sc) {
        String[] tagList = {"24H", "Women Only", "Student Only", "Quiet", "Laptop"};

        System.out.println("\n--- 태그 선택 ---");
        for (int i = 0; i < tagList.length; i++) {
            System.out.println((i + 1) + ". " + tagList[i]);
        }
        
        while (true) {
            System.out.print("태그 번호 입력 (여러 개는 콤마로 구분, 예: 1,4 / 없으면 0): ");
            String input = sc.nextLine();

        if (input.trim().equals("0") || input.trim().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean valid = true;
        for (String part : input.split(",")) {
            part = part.trim();
            try {
                int idx = Integer.parseInt(part);
                if (idx >= 1 && idx <= tagList.length) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(tagList[idx - 1]);
                } else {
                	valid = false;
                	break;
                }
            } catch (NumberFormatException e) {
            	valid = false;
            	break;
            }
        }
        if (valid) {
            return sb.toString();
        }
        System.out.println(">> 잘못된 입력입니다.\n>> 다시 입력해주세요.");
    }
    }
}