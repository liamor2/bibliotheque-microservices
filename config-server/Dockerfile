# Étape de construction
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY book-service/pom.xml book-service/pom.xml
COPY loan-service/pom.xml loan-service/pom.xml
COPY config-server/src config-server/src

RUN mvn -q -pl config-server -am package -DskipTests

# Étape d'exécution
FROM eclipse-temurin:25-jre-ubi10-minimal
WORKDIR /app
COPY --from=build /workspace/config-server/target/config-server-*.jar app.jar
COPY config-server/config-repo config-repo

EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
