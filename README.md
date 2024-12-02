
# 📋 React-SpringBoot-Board-Incomplete

**React-SpringBoot-Board-Incomplete**는 **Spring Boot**와 **React**를 활용한 게시판 애플리케이션입니다.  
사용자 계정 관리, 게시글 작성/관리, 파일 업로드, 태그 기반 검색 및 AWS S3 파일 저장소를 포함한 다양한 기능을 제공합니다.  
이 문서는 프로젝트의 구조, 실행 방법, 사용된 기술 스택, 그리고 주의사항을 자세히 설명합니다.

---

## 📖 참고 사이트

이 프로젝트는 개발 과정에서 다양한 기술과 개념을 이해하고 구현하기 위해 **제 개인 Tistory 블로그**를 참조하며 진행되었습니다.  
블로그에는 프로젝트 진행 중 배운 기술 스택이 상세히 기록되어 있습니다.  
더 많은 정보와 저의 개발 여정을 확인하고 싶으시다면, 저의 [Tistory 블로그](https://myinfo503.tistory.com/)를 방문해 주세요!  

---

## 🚀 프로젝트 개요

이 프로젝트는 프론트엔드와 백엔드 간의 원활한 통신과 확장성을 염두에 두고 설계되었습니다.  
**JWT 기반 인증 시스템**, **AWS S3 연동**, **SMTP 이메일 알림**을 포함한 다양한 기술을 적용했습니다.  
회원가입 시 이메일 인증을 통해 사용자 계정을 활성화하는 기능을 제공합니다.

---

## 📂 프로젝트 구조

### 1. **Frontend**
React 기반의 사용자 인터페이스(UI)입니다.
- **사용된 주요 라이브러리**:
  - Axios: API 호출 및 데이터 통신.
  - React Router: 라우팅 처리.
  - CKEditor: 게시글 작성 시 WYSIWYG 에디터 제공.
  
- **폴더 구조**:
  ```
  frontend/
  ├── public/
  ├── src/
      ├── components/    # UI 컴포넌트
      ├── pages/         # 주요 페이지 (게시글 리스트, 작성 등)
      ├── utils/         # Axios 설정 및 유틸리티 함수
      └── App.js         # 메인 엔트리 포인트
  ```

### 2. **Backend**
Spring Boot 기반의 RESTful API 서버입니다.
- **주요 모듈**:
  - `controller`: 클라이언트 요청 처리 및 응답 반환.
  - `service`: 비즈니스 로직 처리.
  - `repository`: 데이터베이스 연동 및 CRUD 처리.
  - `entity`: JPA 엔티티 정의.
  - `dto`: 데이터 전송 객체(Data Transfer Object) 정의.
  - `security`: Spring Security 설정 및 JWT 처리.
  - `smtp`: 이메일 인증 및 알림 로직.
  - `s3`: AWS S3 파일 업로드 및 다운로드 처리.
  
- **폴더 구조**:
 ```
backend/
├── src/main/java/com/example/backendboard/
    ├── controller/  # API 엔드포인트
    ├── service/     # 비즈니스 로직
    ├── repository/  # 데이터베이스 작업
    ├── entity/      # JPA 엔티티
    ├── dto/         # 데이터 전송 객체
    ├── security/    # Spring Security 설정 및 JWT 처리
    ├── smtp/        # 이메일 인증 및 알림 로직
    └── s3/          # AWS S3 파일 업로드 및 다운로드 처리
 ```



---

## 🚀 주요 기능

1. **사용자 인증 및 권한 관리**:
   - Spring Security와 JWT를 활용한 인증 및 인가 구현.
   - 회원가입 시 이메일 인증 코드 발송.

2. **게시글 작성 및 관리**:
   - 게시글 작성, 수정, 삭제 기능.
   - 첨부파일 업로드 및 관리 (AWS S3 연동).

3. **태그 기반 검색**:
   - 게시글 검색 및 필터링 기능.

4. **SMTP 이메일 인증**:
   - 회원가입 시 이메일 인증 코드를 발송하여 계정 활성화.

---

## 🛠️ 프로젝트를 하며 느낀 점

이 프로젝트를 시작하면서 가장 큰 도전은 React에 대한 부족한 이해와 프론트엔드와 백엔드의 매핑 과정이었습니다.  
처음에는 React의 컴포넌트 구조와 상태 관리(State Management) 개념을 익히는 데 상당한 시간을 소요하였고, 이로 인해 작업 속도가 지연되기도 했습니다.  
하지만 프로젝트를 진행하며 점차 React와 Spring Boot의 연동 방식을 체득하게 되었고, 프론트엔드와 백엔드 간의 원활한 통신을 구현하는 데 성공했습니다.

### 🚀 구체적인 경험과 성과

1. **회원가입 절차 설계 및 구현**  
   사용자 경험을 고려하여 회원가입 절차를 설계하였으며, 이메일 인증 코드를 통한 회원가입 기능을 구현했습니다.  
   이 과정에서 React를 활용한 폼 유효성 검사와 Spring Boot 기반의 SMTP 이메일 전송 로직을 매핑하는 데 집중했습니다.

2. **JWT 인증 및 로그인 구현**  
   Spring Security와 JWT를 활용하여 안전한 인증 및 인가 시스템을 구축했습니다.  
   로그인 성공 시, 사용자 정보와 S3에서 프로필 이미지를 로드하는 기능을 구현하여 사용자 경험을 향상시켰습니다.

3. **게시판 CRUD 및 태그 기반 검색**  
   게시글 작성, 수정, 삭제와 같은 기본 기능 외에도 태그 기반 검색 기능을 추가하여 게시판의 확장성과 유용성을 높였습니다.  
   특히, AWS S3와의 연동을 통해 게시글에 파일 첨부 기능을 제공하고, 효율적인 파일 관리 로직을 설계했습니다.

4. **단계별 기능 구현과 테스트**  
   기능 구현을 단계별로 나누어 진행했습니다.  
   예를 들어:
   - 게시글 작성 및 수정: React의 WYSIWYG 에디터를 사용하여 사용자 친화적인 UI 제공.
   - 댓글 기능: 댓글 작성, 수정, 삭제와 같은 CRUD 기능 구현.
   - 검색 및 필터링: 게시글의 제목, 내용, 태그를 기준으로 한 검색 기능 제공.
   
   이러한 기능 구현을 통해 매번 기능이 완성될 때마다 느낀 성취감은 매우 컸으며, 프로젝트의 완성도를 점차 높일 수 있었습니다.

---

## 💻 실행 방법

### 1. 사전 준비
1. **Node.js 설치**: React 실행을 위해 Node.js가 필요합니다.
2. **MySQL 설치**: 데이터베이스를 MySQL로 설정했습니다.
3. **AWS 계정 생성**: S3 버킷과 IAM 사용자 자격 증명(accessKey, secretKey)을 준비하세요.

### 2. 설정
1. `backend/src/main/resources/application.yml` 파일을 작성하세요:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/board
       username: root
       password: yourpassword
     jpa:
       hibernate:
         ddl-auto: update

   cloud:
     aws:
       credentials:
         accessKey: YOUR_AWS_ACCESS_KEY
         secretKey: YOUR_AWS_SECRET_KEY
       region:
         static: ap-northeast-2

   spring:
     mail:
       username: your-email@gmail.com
       password: your-email-password
       properties:
         mail:
           smtp:
             auth: true
             starttls.enable: true
   ```

2. React 환경 설정:
   ```
   cd frontend
   npm install
   ```

### 3. 실행
1. 백엔드 실행:
   ```bash
   cd backend
   ./gradlew bootRun
   ```
2. 프론트엔드 실행:
   ```bash
   cd frontend
   npm start
   ```

---

## 🛠️ 사용된 기술

- **Spring Boot**: 백엔드 프레임워크.
- **React**: 프론트엔드 라이브러리.
- **JPA**: 데이터베이스 매핑.
- **AWS S3**: 파일 업로드.
- **SMTP**: 이메일 인증.
- **Gradle**: 프로젝트 빌드 관리.

---

## 🔒 보안 주의사항

1. `application.yml` 파일에는 민감한 정보를 포함하지 마세요.
2. AWS Key와 SMTP 비밀번호는 환경 변수로 관리하세요.

---

## 🛠️ 추가 참고

- 이 프로젝트는 계속 개발 중입니다.
- 기능 추가 및 코드 개선에 대한 아이디어를 언제든지 제안해주세요.

---

## 📝 기여 방법

1. 이 저장소를 Fork합니다.
2. 새로운 브랜치를 생성합니다: `git checkout -b feature/새로운기능`.
3. 변경 사항을 커밋합니다: `git commit -m "새로운 기능 추가"`.
4. 브랜치에 Push합니다: `git push origin feature/새로운기능`.
5. Pull Request를 생성합니다.

---

## 📧 문의

- 프로젝트와 관련된 문의는 [이메일](mailto:jinsu8828@gmail.com)로 보내주세요.
