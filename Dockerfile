# OpenJDK 17 slim 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /apps

# 애플리케이션 JAR 복사
COPY build/libs/app.jar /apps/app.jar

# wait-for-it 스크립트 복사 및 실행 권한 부여
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# 포트 노출
EXPOSE 8080

# entrypoint는 wait-for-it 사용
ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--timeout=60", "--strict", "--", "java", "-jar", "/apps/app.jar"]
