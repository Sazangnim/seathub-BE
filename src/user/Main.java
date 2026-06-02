package user;

import java.util.Scanner;
import cafe.CafeMenu; 

// 기능: 온보딩, 로그인, 회원가입

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		UserService us = new UserService();
		
		// === 온보딩 화면 ===
		while (true) {
			System.out.println("---------------------------------");
			System.out.println("             SEATHUB             ");
			System.out.println("---------------------------------");
			System.out.println("1. 로그인");
			System.out.println("2. 회원가입");
			System.out.println();
			System.out.println("[0] 종료");
			
			while (true) {
				System.out.println("---------------------------------");
				System.out.print("선택 >> ");
				String start = sc.nextLine();
				
				if (start.equals("1")) {
					loginScreen(sc, us);
					break;
				} else if (start.equals("2")) {
					signupScreen(sc, us);
					break;
				} else if (start.equals("0")) {
					System.out.println("---------------------------------");
					System.out.println(">> SEATHUB를 종료합니다.");
					System.out.println("---------------------------------");
					return;
				} else {
					System.out.println(">> 잘못된 입력입니다.");
					System.out.println(">> 다시 입력해주세요.");
				}
			}
			
			
			System.out.println();
		}
	}
	
	// === 로그인 화면 ===
	public static void loginScreen(Scanner sc, UserService us) {
		while(true) {
			System.out.println("---------------------------------");
			System.out.println("             SEATHUB             ");
			System.out.println("---------------------------------");
			System.out.print("아이디: ");
			String login_id = sc.nextLine();
			System.out.print("비밀번호: ");
			String password = sc.nextLine();
			
			// 로그인 유저 정보는 여기에서!
			User loginUser = us.login(login_id, password);
			
			if (loginUser != null) {
				System.out.println("---------------------------------");
				System.out.println("            로그인 성공!            ");
				System.out.println("---------------------------------");
				
				CafeMenu.cafeMenu(sc, loginUser);
				// 메인 메뉴 화면 메소드 올 자리!
				// 임포트 하고
				// 예시) 클래스.메소드(sc, loginUser); 로그인 유저 개체도 같이 넘김
				
				return;
			} else {
				System.out.println("---------------------------------");
				System.out.println("            로그인 실패             ");
				System.out.println("---------------------------------");
				System.out.println("[0] 시작 화면으로 돌아가기");
				System.out.println("[1] 다시 로그인하기");
				
				while (true) {
					System.out.println("---------------------------------");
					System.out.print("선택 >> ");
					
					String re = sc.nextLine();
					
					if (re.equals("0")) {
						return;
					} else if (re.equals("1")) {
						break;
					} else {
	                    System.out.println(">> 잘못된 입력입니다.");
	                    System.out.println(">> 다시 입력해주세요.");
	                }
				}
			}
		}
	}
		
	
	// === 회원가입 화면 ===
	public static void signupScreen(Scanner sc, UserService us) {
		System.out.println("---------------------------------");
		System.out.println("             SEATHUB             ");
		System.out.println("---------------------------------");
		System.out.println("회원 유형: ");
		System.out.println("[1] 일반 회원");
		System.out.println("[2] 사장 회원");

		String role;
		
		while(true) {
			System.out.println();
			System.out.print("선택 >> ");
			String roleChoice = sc.nextLine();
			System.out.println();
			
			if (roleChoice.equals("1")) {
				role = "USER";
				break;
			} else if (roleChoice.equals("2")) {
				role = "OWNER";
				break;
			} else {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			}
		}
		
		// 입력 시작
		
		String user_name;
			
		while(true) {
			System.out.print("이름: ");
			user_name = sc.nextLine();
			if (user_name.isBlank()) {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else if (us.isUserNameDuplicated(user_name)) {
				System.out.println(">> 이미 존재하는 이름입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else {
				break;
			}
		}
		
		String login_id;
		
		while(true) {
			System.out.print("아이디: ");
			login_id = sc.nextLine();
			if (login_id.isBlank()) {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else if (us.isLoginIdDuplicated(login_id)) {
				System.out.println(">> 이미 존재하는 아이디입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else {
				break;
			}
		}
		
		String password;
		
		while (true) {
			System.out.print("비밀번호: ");
			password = sc.nextLine();
			if (password.isBlank()) {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else if (password.equals("0")) {
				System.out.println(">> 비밀번호는 0으로 설정할 수 없습니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else {
				break;
			}
		}
		
		String checkPassword;
		
		while (true) {
			System.out.print("비밀번호 확인: ");
			checkPassword = sc.nextLine();
			if (!checkPassword.equals(password)) {
				System.out.println(">> 비밀번호가 일치하지 않습니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else {
				break;
			}
		}
		
		String email;
		
		while (true) {
			System.out.print("이메일: ");
			email = sc.nextLine();
			if (password.isBlank()) {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			} else {
				break;
			}
		}

		String gender;
		System.out.println("성별: ");
		System.out.println("[1] 여성");
		System.out.println("[2] 남성");
		
		while(true) {
			System.out.println();
			System.out.print("선택 >> ");
			String roleChoice = sc.nextLine();
			System.out.println();
			
			if (roleChoice.equals("1")) {
				gender = "FEMALE";
				break;
			} else if (roleChoice.equals("2")) {
				gender = "MALE";
				break;
			} else {
				System.out.println(">> 잘못된 입력입니다.");
				System.out.println(">> 다시 입력해주세요.");
			}
		}
		
		int age;
		
		while (true) {
			System.out.print("나이: ");
			
			try {
				age = Integer.parseInt(sc.nextLine());
				
				if (age <= 0) {
					System.out.println(">> 잘못된 입력입니다.");
					System.out.println(">> 다시 입력해주세요.");
				} else {
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println(">> 나이는 숫자로 입력해야 합니다.");
				System.out.println(">> 다시 입력해주세요.");
			}
		}
		
		String business_number = null;
		
		if (role.equals("OWNER")) {
			while (true) {
				System.out.print("사업자등록번호: ");
				business_number = sc.nextLine();
				
				if (business_number.isBlank()) {
					System.out.println(">> 잘못된 입력입니다.");
					System.out.println(">> 다시 입력해주세요.");
				} else {
					break;
				}
			}
			
		}
		
		// 입력 종료
		
		// 입력값 저장
		User user = new User(
                role,
                user_name,
                login_id,
                password,
                email,
                gender,
                age,
                business_number
        );
		
		boolean result = us.signUp(user);
		
		if (result) {
			System.out.println("---------------------------------");
			System.out.println("           회원가입 성공!           ");
			System.out.println("---------------------------------");
			System.out.println(">> 로그인 화면으로 이동합니다.");
            loginScreen(sc, us);
            return;
		} else {
			System.out.println(">> 회원가입에 실패했습니다.");
			System.out.println("---------------------------------");
			System.out.println("[0] 첫 화면으로 돌아가기");
			System.out.println("[1] 다시 회원가입하기");

			String re;
			
			while (true) {
				System.out.println("---------------------------------");
				System.out.print("선택 >> ");
				re = sc.nextLine();
				
				if (re.equals("0")) {
					return;
				} else if (re.equals("1")) {
					break;
				} else {
					System.out.println(">> 잘못된 입력입니다.");
					System.out.println(">> 다시 입력해주세요.");
				}
			}
		}
		
	}

}
