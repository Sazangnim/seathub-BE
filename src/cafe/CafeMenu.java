package cafe;

import java.util.List;
import java.util.Scanner;
import user.User;

public class CafeMenu {
	// 로그인 성공 후에 들어 올 수 있는 카페 메뉴 부분

	
	public static void cafeMenu(Scanner sc, User loginUser) {
        CafeDAO cafeDao = new CafeDAO();
        

        while (true) {
            showMenu();
            String choice = sc.nextLine();
       

            switch (choice) {
                case "1" -> {
                    List<Cafe> cafes = cafeDao.findAll();
                    printCafes(cafes);
                    backToMenu(sc);
                }
                case "2" -> {
                    System.out.print("지역 입력 (Ewha / Hongdae / Hyehwa / Jongno): ");
                    String region = sc.nextLine();
                    List<Cafe> cafes = cafeDao.findByRegion(region);
                    printCafes(cafes);
                    backToMenu(sc);
                }
                case "3" -> {
                    System.out.print("태그 입력 (24H / Laptop / Quiet / Women Only / Student Only): ");
                    String tag = sc.nextLine();
                    List<Cafe> cafes = cafeDao.findByTag(tag);
                    printCafes(cafes);
                    backToMenu(sc);
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
                    System.out.print("지역 (Ewha / Hongdae / Hyehwa / Jongno): ");
                    String region = sc.nextLine();
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
                    System.out.println("cafeId: " + saved.getCafeId());
                }
                case "5" -> {
                	//마이페이지로 연결
                }
                case "0" -> {
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
            }
        }
    }

    public static void showMenu() {
        System.out.println("---------------------------------");
        System.out.println("             SEATHUB             ");
        System.out.println("---------------------------------");
        System.out.println("1. 전체 카페 조회");
        System.out.println("2. 지역별 카페 조회");
        System.out.println("3. 태그별 카페 조회");
        System.out.println("4. 카페 등록 (사장회원)");
        System.out.println("5. 마이페이지");
        System.out.println("0. 로그아웃");
        System.out.print("선택: ");
    }

    public static void printCafes(List<Cafe> cafes) {
        if (cafes.isEmpty()) {
            System.out.println("조회된 카페가 없습니다.");
            return;
        }
        System.out.println("\n--- 카페 목록 ---");
        System.out.println("cafe_id | cafe_name | region | address | tags");
        for (Cafe c : cafes) {
            System.out.println(
                "[" + c.getCafeId() + "] " +
                c.getCafeName() +
                " | " + c.getRegion() +
                " | " + c.getAddress() +
                " | " + (c.getTags() != null ? c.getTags() : "없음")
            );
        }
    }

    // 조회 결과 확인 후 0 입력하면 메인 메뉴로 복귀
    public static void backToMenu(Scanner sc) {
        while (true) {
            System.out.print("\n0. 메인 메뉴로 돌아가기\n선택: ");
            String input = sc.nextLine();
            if (input.trim().equals("0")) {
                return;
            }
            System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
        }
    }

    // 태그 선택
    public static String selectTags(Scanner sc) {
        String[] tagList = {"24H", "Women Only", "Student Only", "Quiet", "Laptop"};

        System.out.println("\n--- 태그 선택 ---");
        for (int i = 0; i < tagList.length; i++) {
            System.out.println((i + 1) + ". " + tagList[i]);
        }
        System.out.print("태그 번호 입력 (여러 개는 콤마로 구분, 예: 1,4 / 없으면 0): ");
        String input = sc.nextLine();

        if (input.trim().equals("0") || input.trim().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String part : input.split(",")) {
            part = part.trim();
            try {
                int idx = Integer.parseInt(part);
                if (idx >= 1 && idx <= tagList.length) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(tagList[idx - 1]);
                } else {
                    System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> 잘못된 입력입니다.\n>>다시 입력해주세요.");
            }
        }
        return sb.toString();
    }
}