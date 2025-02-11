FROM eclipse-temurin:17
WORKDIR /
COPY target/cucumber-*-test-jar-with-dependencies.jar /
RUN wget https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.12/org.jacoco.agent-0.8.12-runtime.jar
CMD java -javaagent:org.jacoco.agent-0.8.12-runtime.jar=destfile=/target/code-coverage/jacoco-it.exec \
     -Dcucumber.execution.strict=true\
     -Dcucumber.filter.tags='not @Skip' \
     -DskipITs=false\
     -Dtimeout=30\
     -DskipITCoverage=false\
     -DrunHeadless=true\
     -jar cucumber-*-test-jar-with-dependencies.jar -report /target/output/osgp-cucumber-tests-platform-smartmetering
