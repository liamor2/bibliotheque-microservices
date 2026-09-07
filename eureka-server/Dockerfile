# Étape de construction
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY book-service/pom.xml book-service/pom.xml
COPY loan-service/pom.xml loan-service/pom.xml
COPY eureka-server/src eureka-server/src

RUN mvn -q -pl eureka-server -am package -DskipTests

# Étape d'exécution
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/eureka-server-*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
