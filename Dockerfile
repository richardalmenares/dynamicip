FROM amazoncorretto:21-alpine

COPY target/dynamicip.jar dynamicip.jar

ENTRYPOINT ["java", "-jar", "/dynamicip.jar"]