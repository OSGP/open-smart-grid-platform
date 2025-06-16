FROM eclipse-temurin:17

WORKDIR /opt/app

COPY target/osgp-secret-management-*exec.jar osgp-secret-management-exec.jar

ENV SERVER_PORT=8443

CMD java -Djava.security.properties=/opt/app/java.security --module-path /opt/nfast/java/classes -jar osgp-secret-management-exec.jar
