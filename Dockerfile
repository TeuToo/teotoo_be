# 1. 베이스 이미지 선택
FROM openjdk:17-jdk-alpine as build

# 2. 작업 디렉토리 설정
WORKDIR /build

# 3. 애플리케이션 코드 복사
COPY . .

# 4. Gradle Wrapper 실행 권한 부여 및 애플리케이션 빌드
RUN chmod +x ./gradlew && \
    ./gradlew build

# 5. 애플리케이션 실행을 위한 새로운 스테이지
FROM openjdk:17-jdk-alpine

WORKDIR /srv/teutoo-server/lib

# 빌드 스테이지에서 생성된 실행 가능한 JAR 파일을 현재 이미지로 복사
# docker-compose.yml에 정의된 경로와 일치시키기 위해 수정
COPY --from=build /build/build/libs/teutoo-0.0.1-SNAPSHOT.jar /srv/teutoo-server/lib/teutoo-0.0.1-SNAPSHOT.jar

# 6. 애플리케이션 실행 환경 변수 설정 (필요한 경우)
ENV SPRING_PROFILES_ACTIVE=dev

# 7. 애플리케이션 실행
# docker-compose.yml의 command와 일치
ENTRYPOINT ["java", "-jar", "/srv/teutoo-server/lib/teutoo-0.0.1-SNAPSHOT.jar"]
