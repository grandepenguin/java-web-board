# java-web-board

## 프로젝트 소개

이 프로젝트는 Dynamic Web Project로 JSP, Java, JavaScript, CSS 등을 사용하여 개발된 웹 애플리케이션(게시판)입니다.

### 기술 스택

- **JSP (JavaServer Pages):** 서버 측 템플릿 언어
- **Java:** 서버 측 로직 처리
- **JavaScript:** 클라이언트 측 동적 동작 및 상호 작용
- **CSS (Cascading Style Sheets):** 스타일 및 레이아웃 정의
- **Oracle:** 데이터베이스

### 개발 환경

- **IDE(통합 개발 환경):** 이클립스 (2023-03)
- **웹 애플리케이션 서버:** Apache Tomcat v9.0
- **데이터베이스:** Oracle Database 11g Express Edition Release 11.2.0.2.0

### 외부 라이브러리

프로젝트에서는 다음과 같은 외부 라이브러리를 사용합니다:

- **ojdbc6.jar:** Oracle JDBC 드라이버
- **gson-2.10.1.jar:** Google Gson 라이브러리
- **bootstrap.min.css:** 부트스트랩 스타일 지원 파일

ojdbc6.jar, gson-2.10.1.jar 라이브러리는 `src/main/webapp/WEB-INF/lib` 폴더에 위치합니다.  
bootstrap.min.css 라이브러리는 `src/main/webapp/css` 폴더에 위치합니다.  
따라서 별도의 다운로드나 설치 작업이 필요하지 않습니다.

의존성 관리 도구 사용 : X

## 데이터베이스 연결 정보

서비스 이름: xe  
호스트: localhost  
포트: 1521  
사용자명: board  
패스워드: 1234  
권한 : connect, resource  
