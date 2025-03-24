FROM amazoncorretto:17-alpine-jdk

RUN apk add --update coreutils tzdata && rm -rf /var/cache/apk/*

COPY target/dlms-device-simulator-starter*.jar /app/dlms-device-simulator-starter.jar

ENTRYPOINT ["java", "-jar", "/app/dlms-device-simulator-starter.jar", "/config/simulators.json", "start"]
