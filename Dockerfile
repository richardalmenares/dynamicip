FROM openjdk:21-slim

COPY target/dynamicip.jar dynamicip.jar

ENTRYPOINT ["java", "-jar", "/dynamicip.jar"]