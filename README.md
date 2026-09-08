## 환경변수

DB 접속 정보는 `.env`로 관리 프로젝트 루트에 `.env` 파일 만들고 아래 값 채우기.

```
DB_URL=jdbc:mysql://localhost:3306/process
DB_USERNAME=root
DB_PASSWORD=
```

## Redis Setting
### 1. 설치
#### 1.1 Redis 설치
`brew install redis`
#### 1.2 Redis 설치 확인
`redis-cli --version`
### 2. 실행
#### 2.1 백그라운드 실행
`brew services start redis`

# Redis가 실행 중이지 않으면 앱 구동이 실패합니다