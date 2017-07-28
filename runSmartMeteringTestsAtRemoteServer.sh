#!/bin/bash

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <servername> <project> <user> [<ssh key file>] [<additional java parameters>]"
  echo ""
  exit 1
fi

SERVERNAME=$1
PROJECT=$2
USER=$3
SSH_KEY_FILE=$4
ADDITIONAL_PARAMETERS=$5

# If a space is found in the identity file then create a shortcut as the -i parameter for ssh can't handle spaces.
[ "${SSH_KEY_FILE}"!="" ] && [ "${SSH_KEY_FILE}"=~" " ] && echo "Creating link ${HOME}/.ssh/${4/ /} => ${HOME}/.ssh/${4} ..." && ln -sf "${HOME}/.ssh/${4}" "${HOME}/.ssh/${4/ /}"

# Now determine if a -i parameter should be generated
[ "${SSH_KEY_FILE}"!="" ] && SSH_KEY_FILE="-i \"${HOME}/.ssh/${4/ /}\"" && echo "SSH_KEY_FILE=[${SSH_KEY_FILE}]"

echo "Going to run the cucumber project ${PROJECT} on ${SERVERNAME} ..."
echo "- Create directory structure ..."
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVERNAME} "sudo mkdir -p /data/software/${PROJECT}/target/output"
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVERNAME} "sudo mkdir -p /data/software/${PROJECT}/soap-ui-project"
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVERNAME} "sudo chown -R ${USER}:${USER} /data/software"

echo "- Copy over nesseccary files to ${SERVERNAME} ..."
scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${PROJECT}/soap-ui-project/* ${USER}@${SERVERNAME}:/data/software/${PROJECT}/soap-ui-project/

./runTestsAtRemoteServer.sh ${SERVERNAME} ${PROJECT} ${USER} "$4" "-Ddynamic.properties.base.url=https://${SERVERNAME}/osgp-simulator-dlms-triggered/wakeup $5"
