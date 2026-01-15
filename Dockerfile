FROM amazoncorretto:21

COPY ./application/build/libs/application-*.*.*-SNAPSHOT.jar /application.jar

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "/application.jar"]
