FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY database ./database
COPY frontend ./frontend

RUN mvn clean package -DskipTests dependency:copy-dependencies -DoutputDirectory=target/dependency

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/classes ./target/classes
COPY --from=build /app/target/dependency ./target/dependency
COPY --from=build /app/frontend ./frontend

CMD ["sh", "-c", "java -cp target/classes:target/dependency/* app.web.WebServer"]