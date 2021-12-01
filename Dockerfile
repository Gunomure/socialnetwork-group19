FROM maven:3.8.3-jdk-11 as build
WORKDIR src
ADD api api
ADD db db
ADD domain domain
ADD impl impl
ADD pom.xml pom.xml

RUN mvn clean package
RUN mkdir app

FROM adoptopenjdk:11-jre-hotspot as app

WORKDIR app
COPY --from=build /src/impl/target/javapro-socialnetwork-studygroup-19.jar .

EXPOSE 8086 9092
EXPOSE 9092
ENTRYPOINT ["java", "-jar","javapro-socialnetwork-studygroup-19.jar"]
