# Updates the Maven dependencies in FitNesse.

CLASSPATH_PAGE=FitNesseRoot/content.txt
POM_FILE=../../implementation/main/java/osp/pom.xml

echo "Updating the Maven dependencies in FitNesse."
echo "    Using classpath page: $CLASSPATH_PAGE"
echo "    Using POM file      : $POM_FILE"

tf checkout $CLASSPATH_PAGE

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

!path ../../java/osgp/platform/target/classes
!path ../../java/osgp/platform/target/test-classes
!path ../../java/osgp/web-operator/target/classes
!path ../../java/osgp/web-operator/target/test-classes
!path ../../java/osgp/web-owner/target/classes
!path ../../java/osgp/web-owner/target/test-classes
" > $CLASSPATH_PAGE

echo "Re-initialized classpath page."

mvn dependency:build-classpath -f ${POM_FILE} | grep -v INFO | grep -v WARNING | tr ";" "\n" | sed "/^$/d" | sed "s/^/-path /" | tr "\134" "\057" | sed "s/.*\.m2\/repository/!path \${MAVEN_REPO}/" >> $CLASSPATH_PAGE

echo "
*!" >> $CLASSPATH_PAGE

echo "Succesfully updated Maven dependencies in FitNesse."
echo "Warning! Remember to startup your FitNesse instance with th \$MAVEN_REPO JVM environment variable."
