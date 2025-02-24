FROM openjdk:22-jdk-oracle
RUN mkdir /app
WORKDIR /app
COPY  target/*.jar /app/app.jar
CMD [ "java", "-jar", "/app/app.jar"]