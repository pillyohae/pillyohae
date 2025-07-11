# 초기 프로젝트 커밋하는 방법 (How to Commit Initial Project)

이 문서는 "어떻게 초기 프로젝트 커밋하지?" 질문에 대한 완전한 답변입니다.

## 🎯 요약 (TL;DR)

```bash
# 1. 프로젝트 클론
git clone https://github.com/pillyohae/pillyohae.git
cd pillyohae

# 2. 자동 설정 실행
chmod +x setup.sh
./setup.sh

# 3. 첫 커밋 (예시)
echo "# My Changes" > CHANGES.md
git add CHANGES.md
git commit  # 템플릿이 자동으로 열림
```

## 📚 상세 가이드

### 1. 새 프로젝트 시작할 때

```bash
# 빈 저장소에서 시작하는 경우
git init
git add README.md
git commit -m "chore: 초기 프로젝트 구조 생성

- 프로젝트 기본 구조 설정
- README.md 파일 생성"

git remote add origin https://github.com/username/project.git
git push -u origin main
```

### 2. 기존 프로젝트에 기여할 때

```bash
# 1. 저장소 클론
git clone https://github.com/pillyohae/pillyohae.git
cd pillyohae

# 2. 자동 설정 (권장)
./setup.sh

# 3. 새 브랜치 생성
git checkout -b feature/my-feature

# 4. 작업 후 커밋
git add .
git commit  # 템플릿 사용

# 5. 푸시 및 PR
git push origin feature/my-feature
```

### 3. 멀티모듈 Spring Boot 프로젝트 초기 커밋 순서

이 프로젝트의 초기 설정을 위한 권장 커밋 순서:

```bash
# 1단계: 프로젝트 구조
git commit -m "feat: Spring Boot 멀티모듈 프로젝트 구조 생성

- common, main-server, payment-server 모듈 분리
- Gradle 빌드 설정 구성
- 모듈별 의존성 설정"

# 2단계: 기본 설정
git commit -m "config: 프로젝트 기본 설정 파일 추가

- .gitignore: 빌드 아티팩트 제외 설정
- application.yml: 스프링 부트 설정
- 환경별 프로필 구성"

# 3단계: 문서화
git commit -m "docs: 프로젝트 문서 및 가이드 추가

- README.md: 프로젝트 소개 및 설치 방법
- INITIAL_SETUP.md: 상세 설정 가이드
- CONTRIBUTING.md: 기여 가이드라인"

# 4단계: 개발 도구
git commit -m "chore: 개발 환경 도구 설정

- Git 커밋 메시지 템플릿 추가
- 자동 설정 스크립트 생성
- IDE 설정 파일"
```

## 🔧 이 프로젝트에서 사용 가능한 도구들

### 자동 설정 스크립트
```bash
./setup.sh  # 모든 초기 설정을 자동으로 처리
```

### Git 커밋 템플릿
```bash
git commit  # 메시지 없이 실행하면 템플릿이 열림
```

### 빌드 및 실행
```bash
./gradlew clean build           # 전체 프로젝트 빌드
./gradlew :main-server:bootRun  # 메인 서버 실행
./gradlew :payment-server:bootRun # 결제 서버 실행
```

## ✅ 체크리스트

프로젝트 시작 전 확인사항:

- [ ] Java 17+ 설치됨
- [ ] Git 설정 완료 (user.name, user.email)
- [ ] IDE 설정 (IntelliJ IDEA 권장)
- [ ] Docker 설치 (Redis, RabbitMQ용)
- [ ] 환경변수 파일(.env) 설정
- [ ] 외부 서비스 준비 (MySQL, Redis, RabbitMQ)

첫 커밋 전 확인사항:

- [ ] 빌드 성공 (`./gradlew build`)
- [ ] 테스트 통과 (`./gradlew test`)
- [ ] 커밋 메시지 템플릿 설정됨
- [ ] .gitignore 설정 확인
- [ ] 민감한 정보 제외 확인

## 🎓 학습 자료

- [상세 설정 가이드](INITIAL_SETUP.md)
- [기여 가이드라인](CONTRIBUTING.md)
- [프로젝트 README](README.md)

---

**이제 "어떻게 초기 프로젝트 커밋하지?" 질문에 대한 완전한 답변을 제공했습니다!** 🎉

추가 질문이 있으면 이슈를 생성하거나 팀원에게 문의하세요.