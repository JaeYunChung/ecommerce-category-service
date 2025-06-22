FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/category-app.jar category-app.jar
ENTRYPOINT ["java", "-jar", "category-app.jar"]