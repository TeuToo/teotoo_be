# 1. 베이스 이미지 선택
FROM openjdk:17-jdk-alpine as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 애플리케이션 코드 및 configuration 파일 복사
COPY . .

# application-dev.yaml 파일을 애플리케이션 리소스 디렉토리로 복사
# Gradle 빌드가 이 파일을 찾을 수 있도록 적절한 위치에 복사해야 합니다.
COPY application-dev.yaml src/main/resources/

# 4. Gradle Wrapper 실행 권한 부여 및 애플리케이션 빌드
RUN chmod +x ./gradlew && \
    ./gradlew build

# 5. 애플리케이션 실행을 위한 새로운 스테이지
FROM openjdk:17-jdk-alpine

# 애플리케이션 파일 저장을 위한 디렉토리 생성
WORKDIR /app

# 빌드 스테이지에서 생성된 실행 가능한 JAR 파일을 현재 이미지로 복사
COPY --from=build /app/build/libs/*.jar teotoo.jar

# 6. 애플리케이션 실행 환경 변수 설정 (필요한 경우)
ENV SPRING_PROFILES_ACTIVE=dev

# 7. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "teotoo.jar"]
