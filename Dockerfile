FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

#Stage 2: create image
FROM eclipse-temurin:21-alpine as production-stage
WORKDIR /app
RUN apk add --no-cache tzdata
ENV TZ=Asia/Ho_Chi_Minh
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-Ddebug", "-jar", "app.jar"]