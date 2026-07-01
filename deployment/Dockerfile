FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY applications/app-service/build/libs/msUsers.jar msUsers.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /msUsers.jar"]
