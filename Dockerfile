FROM openjdk:8-jdk-alpine
MAINTAINER Spinnel consulting
EXPOSE 8080
COPY target/enerdeal-1.0.jar enerdeal-1.0.jar
ENTRYPOINT ["java","-jar","/enerdeal-1.0.jar"]
