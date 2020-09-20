# alice-security
 Spring Security, jwt, Social Login(Kakao), Image Handling, Thymeleaf

## 프로젝트 환경
- JDK 1.8
- Spring Boot 2.3.3.RELEASE
- JPA2
- Gradle 4.8+
- DB : h2

## 프로젝트 실행 방법
1. 프로젝트 Clone
2. Gradle Build 수행
3. Run Application
4. C:\UploadFiles\ 폴더가 없으면 생성
   - FileUploadConfig.java 확인
5. http://localhost:8080/ 접속
6. H2 database JDBC 정보 : http://localhost:8080/h2 (application.properties 참고 )
   - URL : jdbc:h2:mem:project
   - ID/PW : admin/admin 

## 핵심 아이디어
1. Spring Security 설정을 SecurifyConfig.java 파일로 관리
2. 파일 업로드에 관련된 부분은 https://spring.io/guides/gs/uploading-files/ 참조
3. User 와 UserLoginHistory 사이에 1:N mapping 적용

## 개선 아이디어
1. 동작 중간에 새로고침 하는 경우 filter 처리에 대한 검증 로직 구현 필요
2. Test 코드 추가 작성 필요

## 기능 구현 내용
1. Ouath 2.0 방식을 이용한 로그인시스템.
   - Kakao 소셜 로그인 적용 (API KEY는 2020.9월 말에 재발급 받을 예정)
2. Spring Security를 이용한 페이지 블락 및 세션 처리
3. 로그인/회원가입/로그아웃 기능 구현
4. 로그인/로그아웃 이력 페이지 구현
5. 이미지 업로드 기능 구현
6. 이미지 갤러리 페이지 구현
7. 이미지 클릭시 팝업으로 이미지 정보 표현(이름, 크기, 업로드한 사람, 업로드 시점 등)
