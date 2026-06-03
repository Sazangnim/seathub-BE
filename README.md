#  🪑 SEATHUB
>**JDBC 기반 스터디카페 관리 시스템 개발 프로젝트** <br>
>일반 회원과 사장 회원 모두를 위한 DB 관리 시스템입니다.
<br>

### 👥 사장님(sazangnim)

- **이화여자대학교 데이터베이스 수업** - 백엔드 5팀
- **개발 기간**: 2026.05 ~ 2026.06
<br>

### 🔗 배포주소
```text
(추가 기재)
```
<br>

### 🔍 주요기능
- **회원 기능**: 일반/사장 회원 회원가입, 로그인, 회원탈퇴
- **카페 기능**: 스터디카페 검색 및 상세 조회
- **좌석 기능**: 좌석 조회 및 회의실 예약
- **마이페이지 기능**: 사용자 정보 조회, 이용 내역 조회, 카페별 통계 조회
<br>

## 시작 가이드

### 🛠️ 개발 환경
- **Language**: Java 17
- **DB**: MySQL 8.0
- **DB Access**: JDBC
- **IDE**: Eclipse IDE
<br>

### 🗂️ 파일 구조
유지보수와 확장성을 고려하여 DTO, DAO, Service, Main 계층을 분리한 **SOLID 구조**로 설계하였습니다.
```text
SEATHUB-BE/
├── .github/
├── 📁 db/                          # DB를 생성, 구성, 수정하는 SQL문
├── 📁 src/                         # JAVA 코드 파일
│   ├── 📁 DBConnect/               # DB 연결 기능 패키지
│   ├── 📁 cafe/                    # 스터디카페 검색, 상세 조회 기능 패키지
│   ├── 📁 mypage/                  # 회원 마이페이지 기능 - 이용 내역 조회, 카페별 통계 조회 패키지
│   ├── 📁 user/                    # 회원가입, 로그인 기능 패키지
│   └── 📁 seat/                    # 좌석 조회, 예약 기능 패키지
├── .gitignore
├── config-example.properties        # 'config.properties' 예시 파일
└── README.md
```
<br>

### 📑 DB 설계(ERD)

<img width="70%" alt="image" src="https://github.com/user-attachments/assets/9ac5d485-fb44-43d0-8b89-0b3a20c7248f" />

<br>

### 🔽 설치 및 실행 방법

1. Repository Clone
```sh
git clone https://github.com/Sazangnim/seathub-BE.git
```
2. 프로젝트 루트 경로에 `config.properties` 파일을 생성합니다.
```properties
db.url=jdbc:mysql://localhost:port-number/DB-name
db.user=username
db.password=password
```
3. `db` 폴더의 SQL파일들을 순서대로 실행합니다.
```text
db/01_create_database.sql
db/02_create_tables.sql
db/03_insert_sample_data.sql
...
```
4. Eclipse에서 프로젝트를 실행합니다.
```text
src/user/Main.java
```
