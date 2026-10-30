# One image definition for every MyClawd service.
# Build the jars first (`mvn -B package`), then:
#   docker build -f docker/service.Dockerfile --build-arg SERVICE=work-order-service -t myclawd/work-order-service .
FROM eclipse-temurin:21-jre-noble

ARG SERVICE
RUN test -n "$SERVICE" || (echo "Pass --build-arg SERVICE=<module folder name>" && false)

RUN groupadd --system app && useradd --system --gid app --home /app app
WORKDIR /app
COPY services/${SERVICE}/target/app.jar /app/app.jar
USER app

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError" \
    PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
