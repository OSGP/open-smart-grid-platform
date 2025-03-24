FROM tomcat:10.1-jre17-temurin-noble

RUN rm -rf /usr/local/tomcat/webapps.dist

# Enable the use of system environment variables in Tomcat configuration files.
RUN echo "org.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource" >> /usr/local/tomcat/conf/catalina.properties

RUN mkdir -p /var/log/osp && \
    rm -rf /usr/local/tomcat/webapps/* && \
    chmod 755 /usr/local/tomcat/webapps /var/log/osp && \
    mkdir -p /usr/local/tomcat/webapps/probe/ && \
    touch /usr/local/tomcat/webapps/probe/probe.txt \

WORKDIR /opt/app
COPY target/osgp-secret-management-*exec.jar osgp-secret-management-exec.jar

ENV SERVER_PORT=8443

CMD java -Djava.security.properties=/opt/app/java.security --module-path /opt/nfast/java/classes -jar osgp-secret-management-exec.jar
