# Stage 1: build
# Start with a Maven image that includes JDK 21
FROM maven:3.9.8-amazoncorretto-21-al2023 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn package -DskipTests

#Stage 2: create image
# Start with Amazon Correto JDK 21
FROM amazoncorretto:21.0.4

# Set working folder to App and copy complied file from above step
WORKDIR /app

# Set timezone environment variable
ENV TZ=Asia/Ho_Chi_Minh

# Install tzdata package (only if needed)
RUN yum install -y tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone \

COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-Ddebug", "-jar", "app.jar"]