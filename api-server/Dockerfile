## Build stage
FROM amazoncorretto:17.0.7-al2023-headless as build_stage
 
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml

# Allow for caching of dependencies
RUN ./mvnw verify clean --fail-never

COPY src/ src/

EXPOSE 8080

RUN ./mvnw package -DskipTests

## Final stage
FROM amazoncorretto:17.0.7-al2023-headless as final_stage
 
ARG JAR_FILE=target/*.jar

COPY --from=build_stage ${JAR_FILE} app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]