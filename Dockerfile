FROM amazoncorretto:25-alpine

COPY target/dynamicip.jar dynamicip.jar

ENTRYPOINT ["java", "-jar", "/dynamicip.jar"]