FROM eclipse-temurin:17

WORKDIR /opt/app

COPY target/osgp-secret-management-*exec.jar osgp-secret-management-exec.jar
COPY java.security .

ENV SERVER_PORT=8443

CMD java -Djava.security.properties=/opt/app/java.security -jar osgp-secret-management-exec.jar --SECRET_MANAGEMENT_PROPERTIES=/etc/osp/osgp-secret-management.properties
