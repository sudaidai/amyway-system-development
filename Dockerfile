FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY calculator/pom.xml calculator/pom.xml
COPY lucky-draw/pom.xml lucky-draw/pom.xml
RUN mvn -q -pl lucky-draw -am -DskipTests dependency:go-offline
COPY calculator calculator
COPY lucky-draw lucky-draw
RUN mvn -q -pl lucky-draw -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/lucky-draw/target/lucky-draw-1.0.0.jar app.jar
USER 10001
ENTRYPOINT ["java","-jar","app.jar"]
