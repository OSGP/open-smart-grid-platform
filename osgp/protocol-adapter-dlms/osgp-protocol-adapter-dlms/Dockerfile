FROM tomcat:10.1-jre17-temurin-noble
RUN apt-get update && apt-get install iputils-ping -y && rm -rf /var/lib/apt/lists/*
COPY target/war/*/* /usr/local/tomcat/webapps/