# OpenJDK 17 slim 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /apps

# 애플리케이션 JAR 복사
COPY build/libs/app.jar /apps/app.jar

# 포트 노출
EXPOSE 8080

# entrypoint는 wait-for-it 사용
ENTRYPOINT ["java", "-jar", "/apps/app.jar"]
