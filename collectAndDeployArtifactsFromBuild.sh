#!/bin/bash

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <server> <user> [<ssh key file>]"
  echo ""
  exit 1
fi

SERVER=$1
USER=$2
SSH_KEY_FILE=$3
ARTIFACTORYAUTH=$4
ARTIFACTSDIR=target/artifacts
REMOTESOFTWAREDIR=/data/software
REMOTEARTIFACTSDIR=${REMOTESOFTWAREDIR}/artifacts

# If a space is found in the identity file then create a shortcut as the -i parameter for ssh can't handle spaces.
[ "${SSH_KEY_FILE}"!="" ] && [ "${SSH_KEY_FILE}"=~" " ] && echo "Creating link ${HOME}/.ssh/${3/ /} => ${HOME}/.ssh/${3} ..." && ln -sf "${HOME}/.ssh/${3}" "${HOME}/.ssh/${3/ /}"

# Now determine if a -i parameter should be generated
[ "${SSH_KEY_FILE}"!="" ] && SSH_KEY_FILE="-oIdentityFile=\"${HOME}/.ssh/${3/ /}\"" && echo "SSH_KEY_FILE=[${SSH_KEY_FILE}]"

echo "Collecting all artifacts ..."
rm -rf ${ARTIFACTSDIR}
mkdir -p ${ARTIFACTSDIR}
find . -name *.war -exec cp -f {} ${ARTIFACTSDIR} \;

echo "retrieve additional artifacts ..."
VERSION=`grep "<version>" pom.xml | sed "s#<[/]\?version>##g;s# ##g" | grep SNAPSHOT`
echo "  [$VERSION]"
ARTIFACTORY_URL=https://artifactory.smartsocietyservices.com/artifactory

CURL_URL=${ARTIFACTORY_URL}/osgp-snapshots/com/alliander/osgp/config/${VERSION}/config-${VERSION}.tar.gz
CURL_TARGET_FILE=${ARTIFACTSDIR}/config-${VERSION}.tar.gz
echo "  [curl -u ${ARTIFACTORYAUTH} -XGET \"${CURL_URL}\" -o ${CURL_TARGET_FILE}]"
curl -u ${ARTIFACTORYAUTH} -XGET "${CURL_URL}" -o ${CURL_TARGET_FILE}

CURL_URL=${ARTIFACTORY_URL}/sss-snapshots/com/alliander/osgp/configuration/${VERSION}/configuration-${VERSION}-configuration.tgz
CURL_TARGET_FILE=${ARTIFACTSDIR}/configuration-${VERSION}.tgz
echo "  [curl -u ${ARTIFACTORYAUTH} -XGET \"${CURL_URL}\" -o ${CURL_TARGET_FILE}]"
curl -u ${ARTIFACTORYAUTH} -XGET "${CURL_URL}" -o ${CURL_TARGET_FILE}

echo "- Create directory structure ..."
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo mkdir -p ${REMOTEARTIFACTSDIR}/\"\""
echo "  [${CMD}]"
${CMD}
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo chown -R ${USER}:${USER} ${REMOTESOFTWAREDIR}\"\""
echo "  [${CMD}]"
${CMD}

echo "- Copy over artifacts to ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${ARTIFACTSDIR}/* ${USER}@${SERVER}:${REMOTEARTIFACTSDIR}/"
echo "  [${CMD}]"
${CMD}

echo "Done."
