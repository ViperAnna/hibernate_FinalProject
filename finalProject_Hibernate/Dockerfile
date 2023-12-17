FROM amazoncorretto:18-alpine
ARG JAR_FILE=target/*.jar
COPY $JAR_FILE finalProject_Hibernate-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "finalProject_Hibernate-1.0-SNAPSHOT.jar"]

