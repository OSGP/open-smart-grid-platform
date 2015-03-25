@echo off

REM Does a clean and a complete build of OSGP and all its sub-projects.

SET POM_FILE=../../java/osgp/pom.xml

mvn -f %POM_FILE% clean install

PAUSE

