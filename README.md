# 💊 pillyohae

---

## 👨‍💻 About Project

### 사용자의  건강 상태와 생활 패턴을 분석해 꼭 맞는 영양제를 추천해주는 영양제 이커머스

- 사용자 설문 기반 영양제 추천 서비스
- 영양제 판매 및 구매
- 상품 구매 유도를 위한 상품 관련 AI 메세지 자동생성
- 상품 이미지 업로드 시 상품이미지 기반 AI 이미지 제공
- 관리자 권한으로 쿠폰 발행을 발행하여 제품 할인 서비스 제공 가능

---

## 👨‍💻 프로젝트 기능

### 회원 관리 및 인증/인가

- 관리자, 판매자, 구매자별 기능 분리
- Spring Security + JWT 기반 인증/인가 적용

### 상품 관리

- S3를 활용한 이미지 관리
- 다양한 조건으로 검색 기능 제공

### 맞춤 영양제 추천

- 사용자가 설문을 통해 건강 상태와 관심사 입력
- 응답을 분석하여 맞춤 영양제 추천

### AI 기반 영양제 마케팅

- 영양제별로 개성 있는 페르소나 이미지 부여
- 귀여운 캐릭터와 대사를 활용하여 구매 욕구 자극

### 결제 시스템

- Toss Payments를 통한 간편하고 안전한 결제 지원
- 다양한 결제 수단 제공 (카드, 간편결제 등)

### 쿠폰 및 할인 시스템

- 관리자가 쿠폰 발급
- 사용 기한, 할인 금액 상한 등 세세한 옵션 지원
- 특정 조건 충족 시 할인 혜택 제공

---

## 🛠️ Tools

<img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"><img alt="Java" src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img alt="Redis" src ="https://img.shields.io/badge/Redis-FF4438.svg?&style=for-the-badge&logo=Java&logoColor=white"><img alt="RabbitMQ" src ="https://img.shields.io/badge/rabbitmq-FF6600.svg?&style=for-the-badge&logo=rabbitmq&logoColor=white"/><img alt="springsecurity" src ="https://img.shields.io/badge/springsecurity-6DB33F.svg?&style=for-the-badge&logo=springsecurity&logoColor=white"/><img alt="jsonwebtokens" src ="https://img.shields.io/badge/jsonwebtokens-000000.svg?&style=for-the-badge&logo=jsonwebtokens&logoColor=white"/><img alt="gradle" src ="https://img.shields.io/badge/gradle-02303A.svg?&style=for-the-badge&logo=gradle&logoColor=white"/><img alt="githubactions" src ="https://img.shields.io/badge/githubactions-2088FF.svg?&style=for-the-badge&logo=githubactions&logoColor=white"/><img alt="docker" src ="https://img.shields.io/badge/docker-2496ED.svg?&style=for-the-badge&logo=docker&logoColor=white"/><img alt="amazonec2" src ="https://img.shields.io/badge/amazonec2-FF9900.svg?&style=for-the-badge&logo=amazonec2&logoColor=white"/><img alt="git" src ="https://img.shields.io/badge/git-F05032.svg?&style=for-the-badge&logo=git&logoColor=white"/><img alt="github" src ="https://img.shields.io/badge/github-181717.svg?&style=for-the-badge&logo=github&logoColor=white"/><img alt="tosspay" src ="https://img.shields.io/badge/tosspay-0170CE.svg?&style=for-the-badge&logo=tosspay&logoColor=white"/><img src="https://img.shields.io/badge/openai-412991?style=for-the-badge&logo=openai&logoColor=white"><img alt="amazons3" src ="https://img.shields.io/badge/amazons3-569A31.svg?&style=for-the-badge&logo=amazons3&logoColor=white"/>

---

## ‍👨‍💻 인프라 설계도

[![Image](https://github.com/user-attachments/assets/fab941a4-3d78-437b-b6ba-8a7917674b3a)](https://github.com/user-attachments/assets/fab941a4-3d78-437b-b6ba-8a7917674b3a)


---

## 👨‍💻 Period : 2025/01/02 ~ 2025/02/11

---

## 👨‍💻 Pill요해? 팀원소개

| 위승현                                                             | 송정학                                        | 김현준                                | 김민주                                                   |
|-----------------------------------------------------------------|--------------------------------------------|------------------------------------|-------------------------------------------------------|
| [@Weseunghyun](https://github.com/Weseunghyun?tab=repositories) | [@sjhak8034](https://github.com/sjhak8034) | [@fl4kx](https://github.com/fl4kx) | [@kmj-23](https://github.com/kmj-23?tab=repositories) |

---

## ‍👨‍💻 프로젝트 서버 설치방법

### 1. 프로젝트 클론

```
git clone https://github.com/pillyohae/pillyohae.git
cd pillyohae
```

### 2. .env (환경변수 설정)

```
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
 

TOSS_SECRET_KEY=toss_secret_key
OPENAI_API_KEY=open_ai_key

RABBITMQ_HOST = localhost
RABBITMQ_USERNAME = rabbitmq_username
RABBITMQ_PASSWORD = rabbitmq_password
RABBITMQ_PORT = 

REDIS_HOST = localhost
```

### 3. Redis 설치

```
1. Homebrew 설치 (이미 설치되어 있다면 이 단계는 건너뛰어도 됩니다)
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

2. Homebrew 설치 확인
$ brew --version

3. Redis 설치
$ brew install redis

4. Redis 실행
$ brew services start redis

5. Redis 실행 여부 확인
$ brew services info redis

6. Redis 중지
$ brew services stop redis

7. Redis에 접속
$ redis-cli
```

### 4. RabbitMQ Docker 실행

```
// Docker로 RabbitMQ 실행
docker run -it --rm --name rabbitmq -p 0000:0000 -p 0000:00000 rabbitmq:4.0-management

```

---

## 👨‍💻 ERD

[![Image](https://github.com/user-attachments/assets/2211c2a2-75e9-43c2-bca2-c236cf2f34cf)](https://github.com/user-attachments/assets/2211c2a2-75e9-43c2-bca2-c236cf2f34cf)
### [Pill요해? ERD 바로가기](https://www.erdcloud.com/d/MwZ2z3BRAQSmeHNdg)
---

## 👨‍💻 API명세서

회원<br>
[![Image](https://github.com/user-attachments/assets/d924412a-a7cb-4d93-b428-04603295632a)](https://github.com/user-attachments/assets/d924412a-a7cb-4d93-b428-04603295632a)<br>
상품<br>
[![Image](https://github.com/user-attachments/assets/02ce0d3b-1f3b-468d-a9c9-55ac0f6c772c)](https://github.com/user-attachments/assets/02ce0d3b-1f3b-468d-a9c9-55ac0f6c772c)<br>
장바구니<br>
[![Image](https://github.com/user-attachments/assets/6f011046-e3d3-48b9-923d-151a74957efb)](https://github.com/user-attachments/assets/6f011046-e3d3-48b9-923d-151a74957efb)<br>
주문<br>
[![Image](https://github.com/user-attachments/assets/ddbf917c-d50d-459e-8571-37d7dd086537)](https://github.com/user-attachments/assets/ddbf917c-d50d-459e-8571-37d7dd086537)<br>
설문<br>
[![Image](https://github.com/user-attachments/assets/1d43c44e-fc27-4f5f-a0a3-690cb9058a04)](https://github.com/user-attachments/assets/1d43c44e-fc27-4f5f-a0a3-690cb9058a04))<br>
추천<br>
[![Image](https://github.com/user-attachments/assets/47dac33d-4bc8-466f-a16b-dbb4e4841af9)](https://github.com/user-attachments/assets/47dac33d-4bc8-466f-a16b-dbb4e4841af9)<br>
쿠폰<br>
[![Image](https://github.com/user-attachments/assets/645e7d04-c852-4b09-bc74-9bac9b2a8fa4)](https://github.com/user-attachments/assets/645e7d04-c852-4b09-bc74-9bac9b2a8fa4)<br>
페르소나(AI 이미지/메세지)<br>
[![Image](https://github.com/user-attachments/assets/a4c38091-b67a-4d52-bec3-74b7fa4f264a)](https://github.com/user-attachments/assets/a4c38091-b67a-4d52-bec3-74b7fa4f264a)<br>

---

## ‍👨‍💻 와이어 프레임

### [Pill요해? 와이어프레임 바로가기](https://www.figma.com/design/HrSfEtAO1Mgo6suY8qRCBI/Pill%EC%9A%94%ED%95%B4%3F?node-id=0-1)

---

