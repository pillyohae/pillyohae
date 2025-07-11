#!/bin/bash

# Pillyohae 프로젝트 초기 설정 스크립트
# 이 스크립트는 프로젝트의 초기 설정을 자동화합니다.

set -e  # 에러 발생 시 스크립트 종료

echo "🚀 Pillyohae 프로젝트 초기 설정을 시작합니다..."

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수 정의
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Java 버전 확인
check_java() {
    print_status "Java 버전을 확인합니다..."
    if command -v java &> /dev/null; then
        java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$java_version" -ge 17 ]; then
            print_success "Java $java_version 설치됨"
        else
            print_error "Java 17 이상이 필요합니다. 현재 버전: $java_version"
            exit 1
        fi
    else
        print_error "Java가 설치되지 않았습니다."
        exit 1
    fi
}

# Git 설정 확인
check_git() {
    print_status "Git 설정을 확인합니다..."
    if command -v git &> /dev/null; then
        print_success "Git 설치됨"
        
        # Git 사용자 설정 확인
        if ! git config user.name &> /dev/null; then
            print_warning "Git 사용자 이름이 설정되지 않았습니다."
            read -p "Git 사용자 이름을 입력하세요: " git_username
            git config --global user.name "$git_username"
        fi
        
        if ! git config user.email &> /dev/null; then
            print_warning "Git 이메일이 설정되지 않았습니다."
            read -p "Git 이메일을 입력하세요: " git_email
            git config --global user.email "$git_email"
        fi
        
        print_success "Git 사용자 설정 완료"
    else
        print_error "Git이 설치되지 않았습니다."
        exit 1
    fi
}

# Gradle wrapper 권한 설정
setup_gradle() {
    print_status "Gradle wrapper 권한을 설정합니다..."
    if [ -f "./gradlew" ]; then
        chmod +x gradlew
        print_success "gradlew 실행 권한 부여 완료"
    else
        print_error "gradlew 파일을 찾을 수 없습니다."
        exit 1
    fi
}

# Git 커밋 메시지 템플릿 설정
setup_git_template() {
    print_status "Git 커밋 메시지 템플릿을 설정합니다..."
    if [ -f ".gitmessage" ]; then
        git config commit.template .gitmessage
        print_success "커밋 메시지 템플릿 설정 완료"
    else
        print_warning ".gitmessage 파일을 찾을 수 없습니다."
    fi
}

# 환경 변수 파일 생성
setup_env_file() {
    print_status "환경 변수 파일을 확인합니다..."
    if [ ! -f ".env" ]; then
        print_warning ".env 파일이 없습니다. 템플릿을 생성합니다..."
        cat > .env << EOF
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
EOF
        print_success ".env 템플릿 파일 생성 완료"
        print_warning "실제 값으로 .env 파일을 수정해주세요!"
    else
        print_success ".env 파일이 이미 존재합니다."
    fi
}

# Docker 서비스 실행 여부 확인
check_docker_services() {
    print_status "필수 서비스 상태를 확인합니다..."
    
    # Redis 확인
    if command -v redis-cli &> /dev/null; then
        if redis-cli ping &> /dev/null; then
            print_success "Redis 서비스 실행 중"
        else
            print_warning "Redis 서비스가 실행되지 않고 있습니다."
            echo "  Redis 실행: brew services start redis 또는 docker run -d --name redis -p 6379:6379 redis:latest"
        fi
    else
        print_warning "Redis가 설치되지 않았습니다."
        echo "  설치: brew install redis 또는 Docker 사용"
    fi
    
    # MySQL 확인 (선택적)
    if command -v mysql &> /dev/null; then
        print_success "MySQL 클라이언트가 설치되어 있습니다."
    else
        print_warning "MySQL 클라이언트가 설치되지 않았습니다."
        echo "  Docker로 MySQL 실행: docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=yourpassword -e MYSQL_DATABASE=pillyohae -p 3306:3306 mysql:8.0"
    fi
}

# 프로젝트 빌드 테스트
test_build() {
    print_status "프로젝트 빌드를 테스트합니다..."
    read -p "프로젝트를 빌드하시겠습니까? (인터넷 연결이 필요합니다) [y/N]: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if ./gradlew clean build --no-daemon; then
            print_success "프로젝트 빌드 성공!"
        else
            print_warning "빌드에 실패했습니다. 의존성 문제일 수 있습니다."
        fi
    else
        print_status "빌드 테스트를 건너뜁니다."
    fi
}

# 메인 실행
main() {
    echo
    echo "=========================================="
    echo "  🔧 Pillyohae 프로젝트 초기 설정"
    echo "=========================================="
    echo
    
    check_java
    check_git
    setup_gradle
    setup_git_template
    setup_env_file
    check_docker_services
    test_build
    
    echo
    echo "=========================================="
    print_success "초기 설정이 완료되었습니다! 🎉"
    echo "=========================================="
    echo
    echo "다음 단계:"
    echo "1. .env 파일의 실제 값들을 설정하세요"
    echo "2. Redis와 MySQL 서비스를 시작하세요"
    echo "3. ./gradlew :main-server:bootRun 으로 애플리케이션을 실행하세요"
    echo
    echo "문제가 발생하면 INITIAL_SETUP.md 문서를 참조하세요."
    echo
}

# 스크립트 실행
main "$@"