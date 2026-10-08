FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app

# Download dependencies separately so source changes can reuse this layer.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=build /app/target/pharmacy-management-system-1.0.0.jar app.jar

RUN mkdir -p /app/uploads && chown 10001:10001 /app/uploads

EXPOSE 8081
USER 10001:10001

ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8081} -jar /app/app.jar"]