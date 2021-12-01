FROM adoptopenjdk:11-jre-hotspot

WORKDIR app
ADD impl/target/javapro-socialnetwork-studygroup-19.jar javapro-socialnetwork-studygroup-19.jar

EXPOSE 8086 9092
EXPOSE 9092
ENTRYPOINT ["java", "-jar","javapro-socialnetwork-studygroup-19.jar"]
