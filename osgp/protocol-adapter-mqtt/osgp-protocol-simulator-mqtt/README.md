# README #

Maven build:
```
mvn clean install
```

Create Docker image
```
mkdir target\dependency
cd target\dependency
jar -xf ..\*.jar
cd ..\..
mvn com.google.cloud.tools:jib-maven-plugin:dockerBuild -Dimage=osgp-protocol-simulator-mqtt
```

Push Docker image to registry
```
mvn com.google.cloud.tools:jib-maven-plugin:build -Dimage=osgp-protocol-simulator-mqtt
```

Check the image:
```
docker image inspect osgp-protocol-simulator-mqtt
```

Run it
```
docker run -p 8883:8883 -t osgp-protocol-simulator-mqtt
```

Tail the logs:
```
docker logs -f CONTAINER
```