FROM adoptopenjdk:11-jre-hotspot

WORKDIR app
ADD impl/target/javapro-socialnetwork-studygroup-19.jar javapro-socialnetwork-studygroup-19.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar","javapro-socialnetwork-studygroup-19.jar"]