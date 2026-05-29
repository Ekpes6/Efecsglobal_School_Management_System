FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY service-registry/pom.xml service-registry/
COPY config-server/pom.xml config-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/
COPY school-service/pom.xml school-service/
COPY student-service/pom.xml student-service/
COPY academic-service/pom.xml academic-service/
COPY guardian-service/pom.xml guardian-service/
COPY file-service/pom.xml file-service/
COPY financial-service/pom.xml financial-service/
COPY notification-service/pom.xml notification-service/
COPY file-service/src file-service/src
RUN --mount=type=cache,target=/root/.m2 mvn -pl file-service package -DskipTests --no-transfer-progress -q -Dmaven.wagon.http.retryHandler.count=5

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/file-service/target/*.jar app.jar
RUN mkdir -p /app/uploads
VOLUME ["/app/uploads"]
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]