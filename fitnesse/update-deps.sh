#!/bin/sh

# Updates the Maven dependencies in FitNesse.

CLASSPATH_PAGE=FitNesseRoot/content.txt
# POM_FILE=../../implementation/main/java/osgp/pom.xml
POM_FILE=../pom.xml

echo "Updating the Maven dependencies in FitNesse."
echo "    Using classpath page: $CLASSPATH_PAGE"
echo "    Using POM file      : $POM_FILE"

# tf checkout $CLASSPATH_PAGE

echo "Checked out classpath page."

echo "!**> setup test system

!define TEST_SYSTEM {slim}

!define CM_SYSTEM {fitnesse.wiki.cmSystems.TfsCmSystem}

!path plugins/junit-4.5.jar
!path plugins/commons-logging.jar
!path plugins/javassist.jar
!path plugins/guava-r06.jar
!path plugins/dom4j-1.6.1.jar
!path plugins/commons-vfs-1.0.jar
!path plugins/clover-2.6.1.jar
!path plugins/givwenzen-1.0.3-SNAPSHOT.jar



!path ../Platform/osgp-adapter-ws-core/target/classes
!path ../Platform/osgp-adapter-ws-core/target/test-classes
!path ../Platform/osgp-adapter-ws-admin/target/classes
!path ../Platform/osgp-adapter-ws-admin/target/test-classes
!path ../Platform/osgp-adapter-ws-publiclighting/target/classes
!path ../Platform/osgp-adapter-ws-publiclighting/target/test-classes
!path ../Platform/osgp-adapter-ws-tariffswitching/target/classes
!path ../Platform/osgp-adapter-ws-tariffswitching/target/test-classes
!path ../Platform/osgp-adapter-ws-shared/target/classes
!path ../Platform/osgp-adapter-ws-shared/target/test-classes
!path ../Platform/osgp-adapter-ws-shared-db/target/classes
!path ../Platform/osgp-adapter-ws-shared-db/target/test-classes

!path ../Platform/osgp-adapter-domain-admin/target/classes
!path ../Platform/osgp-adapter-domain-core/target/classes
!path ../Platform/osgp-adapter-domain-publiclighting/target/classes
!path ../Platform/osgp-adapter-domain-tariffswitching/target/classes
!path ../Shared/osgp-dto/target/classes

!path ../Platform/osgp-domain-publiclighting/target/classes
!path ../Platform/osgp-domain-publiclighting/target/test-classes
!path ../Platform/osgp-domain-tariffswitching/target/classes
!path ../Platform/osgp-domain-tariffswitching/target/test-classes
!path ../Platform/osgp-domain-core/target/classes
!path ../Platform/osgp-domain-core/target/test-classes

!path ../Shared/shared/target/classes
!path ../Shared/shared/target/test-classes

!path ../Platform/osgp-core/target/classes
!path ../Platform/osgp-core/target/test-classes
!path ../osgp-platform-test/target/test-classes

!path ../Protocol-Adapter-OSLP/osgp-core-db-api/target/classes
!path ../Protocol-Adapter-OSLP/osgp-core-db-api/target/test-classes

!path ../Protocol-Adapter-OSLP/osgp-adapter-protocol-oslp/target/classes
!path ../Protocol-Adapter-OSLP/osgp-adapter-protocol-oslp/target/test-classes

!path ../Protocol-Adapter-OSLP/oslp/target/classes
!path ../Protocol-Adapter-OSLP/oslp/target/test-classes

" > $CLASSPATH_PAGE

echo "Re-initialized classpath page."

mvn dependency:build-classpath -f $POM_FILE  | grep -v INFO | grep -v WARNING | tr ":" "\n" | sort -u | sed '/^$/d' |sed "s/^/-path /" | sed "s/.*\.m2\/repository/!path \${MAVEN_REPO}/" >> $CLASSPATH_PAGE

echo "
*!" >> $CLASSPATH_PAGE

echo "Succesfully updated Maven dependencies in FitNesse."
echo "Warning! Remember to startup your FitNesse instance with th \$MAVEN_REPO JVM environment variable."
