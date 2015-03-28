#!/bin/sh

# Does a clean and a complete build of OSP and all its sub-projects.

POM_FILE=../../java/osp/pom.xml

mvn -f $POM_FILE clean install

