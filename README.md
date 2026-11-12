# Refit Server

**학원 수업의 결석부터 보강까지, 한 곳에서 관리하는 서비스 — Refit**

Refit Server는 학생과 선생님이 수업·출결·결석·보강 일정을 함께 관리할 수 있도록 돕는 Spring Boot API 서버입니다.
학생은 결석과 보강을 신청하고, 선생님은 이를 승인하며 출석을 체크합니다. 모든 일정은 캘린더 하나에서 확인할 수 있습니다.


---

## 기술 스택

| 분류 | 기술 |
| --- | --- |
| Language / Framework | Java, Spring Boot |
| Web | Spring MVC, Spring Security, Spring Validation |
| Persistence | Spring Data JPA, MySQL |
| Auth | JWT |
| Cache | Redis |
| API Docs | Swagger (SpringDoc OpenAPI) |

---

## 프로젝트 구조

도메인별로 패키지를 나누고, 공통 설정과 유틸은 `global`에서 관리합니다.

```
src/main/java/com/option/refit/
├── domain/
│   ├── auth/          # 회원가입 · 로그인 · 역할 선택 (Google 로그인, 토큰 발급)
│   ├── email/         # 이메일 인증 코드 발송/검증
│   ├── user/          # 프로필 조회/등록/수정, 프로필 이미지 변경, 회원 탈퇴
│   ├── notification/  # 알림 확인, 모두 읽음
│   ├── classroom/     # 수업 생성/참여/나가기/목록, 수업 홈(캘린더 · 출결 요약)
│   ├── notice/        # 공지 작성/조회/수정/삭제
│   ├── absence/       # 결석 신청/취소/승인
│   ├── makeup/        # 보강 신청/승인, 보강 가능 시간 설정
│   ├── attendance/    # 출석 체크/조회, NFC 태그 관리 및 NFC 출석
│   ├── schedule/      # 캘린더, 시간표/스케줄 관리 (스케줄러 포함)
│   └── file/          # 파일 업로드/삭제
├── global/
│   ├── error/         # CustomException, ErrorCode, GlobalExceptionHandler, 도메인별 에러 코드
│   ├── jwt/           # JWT 발급 · 검증, 인증 필터, 리프레시 토큰 저장소(Redis)
│   ├── verification/  # 이메일 인증 코드 생성 · 저장(Redis) · 메일 발송
│   ├── google/        # Google ID 토큰 검증, 사용자 정보
│   ├── config/        # Security, Web, Swagger, Async, 정적 리소스 설정
│   ├── entity/        # BaseEntity
│   └── common/        # 공통 응답(ApiResponse)
└── RefitApplication
```

각 도메인은 `controller` · `service`(+`impl`) · `repository` · `entity` · `dto`(`request`/`response`) 계층으로 구성됩니다.

---


## 시작하기

### 1. 환경변수

DB 접속 정보는 `.env`로 관리합니다. 프로젝트 루트에 `.env` 파일을 만들고 아래 값을 채워주세요.

```
DB_URL=jdbc:mysql://localhost:3306/process
DB_USERNAME=root
DB_PASSWORD=
```

### API 서버 주소 (운영)

- Base URL: `https://refit.team` (HTTPS 전용, 포트 번호 없음. 기존 IP/http 주소 사용 금지)
- Swagger: https://refit.team/swagger-ui/index.html
- CORS 허용 출처: `https://refit.team`, `https://www.refit.team` (다른 도메인에서 호스팅하면 `CORS_ALLOWED_ORIGINS`에 추가)
- 전체 환경변수 목록은 `.env.example` 참고

### 2. Redis 설치 및 실행

>  Redis가 실행 중이지 않으면 앱 구동이 실패합니다.

**설치**

```bash
brew install redis
redis-cli --version   # 설치 확인
```

**백그라운드 실행**

```bash
brew services start redis
```

### 3. 서버 실행

```bash
./gradlew bootRun
```

