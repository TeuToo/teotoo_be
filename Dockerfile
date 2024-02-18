# 1. 베이스 이미지 선택
FROM bellsoft/liberica-openjdk-alpine:21 as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 애플리케이션 코드 복사
COPY . .

# 4. Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 5. 애플리케이션 빌드
RUN ./gradlew build

# 6. 애플리케이션 실행을 위한 새로운 스테이지
FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

# 빌드 스테이지에서 생성된 실행 가능한 JAR 파일을 현재 이미지로 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 6. 애플리케이션 실행
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
