# 🚀 Pillyohae 프로젝트 초기 설정 가이드

이 문서는 Pillyohae 프로젝트를 처음 설정하고 초기 커밋을 수행하는 방법을 설명합니다.

## 📋 초기 프로젝트 설정 체크리스트

### 1. 개발 환경 준비

- [ ] **Java 17 설치 확인**
  ```bash
  java -version
  # OpenJDK 17 이상 필요
  ```

- [ ] **Git 설치 및 설정**
  ```bash
  git --version
  git config --global user.name "Your Name"
  git config --global user.email "your.email@example.com"
  ```

- [ ] **IDE 설정** (IntelliJ IDEA 권장)
  - Gradle 플러그인 활성화
  - Lombok 플러그인 설치
  - Spring Boot 플러그인 설치

### 2. 프로젝트 클론 및 초기 설정

```bash
# 프로젝트 클론
git clone https://github.com/pillyohae/pillyohae.git
cd pillyohae

# Gradle wrapper 실행 권한 부여
chmod +x gradlew

# 프로젝트 빌드 확인 (인터넷 연결 필요)
./gradlew clean build
```

### 3. 환경 설정 파일 구성

프로젝트 루트에 `.env` 파일 생성:

```env
# 데이터베이스 설정
DATABASE_URL=jdbc:mysql://localhost:3306/pillyohae
DATABASE_USERNAME=root
DATABASE_PASSWORD=yourpassword
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
JPA_HIBERNATE_DDL=

# JWT 설정
JWT_SECRET_KEY=your_secret_key

# AWS 설정
ACCESS_KEY=your_access_key
SECRET_KEY=your_secret_key
BUCKET_NAME=your-s3-bucket-name
BASE_URL=https://api.yourservice.com

# 결제 시스템
TOSS_SECRET_KEY=toss_secret_key

# AI 설정
OPENAI_API_KEY=open_ai_key

# 메시지 큐
RABBITMQ_HOST=localhost
RABBITMQ_USERNAME=rabbitmq_username
RABBITMQ_PASSWORD=rabbitmq_password
RABBITMQ_PORT=5672

# 캐시 설정
REDIS_HOST=localhost
```

### 4. 필수 외부 서비스 설정

#### Redis 설치 및 실행
```bash
# macOS (Homebrew 사용)
brew install redis
brew services start redis

# Docker 사용
docker run -d --name redis -p 6379:6379 redis:latest
```

#### RabbitMQ 실행
```bash
# Docker로 RabbitMQ 실행
docker run -it --rm --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:4.0-management
```

#### MySQL 설정
```bash
# Docker로 MySQL 실행
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=pillyohae \
  -p 3306:3306 \
  mysql:8.0
```

## 🔧 초기 커밋 가이드라인

### 새 프로젝트 시작 시 커밋 순서

1. **초기 프로젝트 구조 커밋**
   ```bash
   git add .
   git commit -m "feat: 초기 프로젝트 구조 설정
   
   - Spring Boot 멀티모듈 프로젝트 구조 생성
   - 공통 모듈(common), 메인 서버(main-server), 결제 서버(payment-server) 분리
   - Gradle 빌드 설정 및 의존성 구성"
   ```

2. **기본 설정 파일 커밋**
   ```bash
   git add .gitignore .gitattributes
   git commit -m "config: 기본 Git 설정 파일 추가
   
   - .gitignore: 빌드 아티팩트, IDE 설정 파일 제외
   - .gitattributes: 줄바꿈 문자 설정"
   ```

3. **문서화 커밋**
   ```bash
   git add README.md INITIAL_SETUP.md
   git commit -m "docs: 프로젝트 문서 추가
   
   - README.md: 프로젝트 개요 및 기능 소개
   - INITIAL_SETUP.md: 초기 설정 가이드"
   ```

4. **CI/CD 설정 커밋**
   ```bash
   git add .github/
   git commit -m "ci: GitHub Actions 워크플로우 설정
   
   - AWS 배포 자동화 설정
   - 빌드 및 테스트 파이프라인 구성"
   ```

### 커밋 메시지 규칙

프로젝트에서 사용하는 커밋 메시지 형식:

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type 종류:
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 포맷팅, 세미콜론 누락 등
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드 스크립트, 패키지 매니저 설정 등
- `config`: 설정 파일 변경

#### Scope 예시:
- `auth`: 인증/인가 관련
- `payment`: 결제 시스템
- `product`: 상품 관리
- `recommendation`: 추천 시스템
- `ai`: AI 기능
- `coupon`: 쿠폰 시스템

#### 예시:
```bash
git commit -m "feat(auth): JWT 기반 인증 시스템 구현

- Spring Security와 JWT 토큰을 이용한 인증 구현
- 사용자 역할별 권한 관리 (관리자, 판매자, 구매자)
- 토큰 만료 및 갱신 로직 추가

Resolves: #123"
```

## 🔍 개발 환경 검증

설정이 완료되면 다음 명령어로 환경을 검증하세요:

```bash
# 1. 빌드 테스트
./gradlew clean build

# 2. 테스트 실행
./gradlew test

# 3. 애플리케이션 실행 (각 모듈별)
./gradlew :main-server:bootRun
./gradlew :payment-server:bootRun
```

## 🚨 주의사항

1. **환경 변수 보안**: `.env` 파일은 절대 Git에 커밋하지 마세요
2. **데이터베이스**: 로컬 개발 시 테스트 데이터베이스 사용 권장
3. **API 키**: 실제 서비스 키는 프로덕션 환경에서만 사용
4. **빌드**: 첫 빌드 시 의존성 다운로드로 시간이 소요될 수 있습니다

## 📚 추가 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Gradle 멀티 프로젝트 가이드](https://docs.gradle.org/current/userguide/multi_project_builds.html)
- [Git 커밋 메시지 가이드](https://www.conventionalcommits.org/)
- [프로젝트 API 문서](README.md#api명세서)

---

문제가 발생하면 프로젝트 이슈를 생성하거나 팀원에게 문의하세요! 🙋‍♂️