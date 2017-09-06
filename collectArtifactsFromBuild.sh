#!/bin/bash

echo "Collecting all artifacts ..."
rm -rf target/artifacts
mkdir -p target/artifacts
find . -name *.war -exec cp -f {} target/artifacts \;

echo "retrieve additional artifacts ..."
VERSION=`grep "<version>" pom.xml | sed "s#<[/]\?version>##g;s# ##g"`
echo "  [$VERSION]"
ARTIFACTORY_URL=https://artifactory.smartsocietyservices.com
CURL_URL=${ARTIFACTORY_URL}/artifactory/osgp-snapshots/com/alliander/osgp/config/${VERSION}/config-${VERSION}.zip
CURL_TARGET_FILE=target/artifacts/config-${VERSION}.zip
echo "  [curl -XGET \"${CURL_URL}\" -o ${CURL_TARGET_FILE}]"
curl -XGET "${CURL_URL}" -o ${CURL_TARGET_FILE}

echo "Done."
