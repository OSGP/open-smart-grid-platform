#!/bin/bash

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <stream> <environment> <buildnumber> <project> <user> [<ssh key file>] [<additional java parameters>]"
  echo ""
  exit 1
fi

STREAM=$1
ENVIRONMENT=$2
BUILDNUMBER=$3
PROJECT=$4
USER=$5
SSH_KEY_FILE=$6
ADDITIONAL_PARAMETERS=$7

[ "${SSH_KEY_FILE}"=="" ] && [ -f "${HOME}/.ssh/$6" ] && SSH_KEY_FILE="-i ${HOME}/.ssh/$6"

echo "Going to run the cucumber project ${PROJECT} on ${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud ..."
echo "- Create directory structure ..."
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud "sudo mkdir -p /data/software/${PROJECT}/target/output"
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud "sudo mkdir -p /data/software/${PROJECT}/soap-ui-project"
ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud "sudo chown -R ${USER}:${USER} /data/software"

echo "- Copy over nesseccary files to ${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud ..."
scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${PROJECT}/soap-ui-project/* ${USER}@${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud:/data/software/${PROJECT}/soap-ui-project/

./runTestsAtRemoteServer.sh ${STREAM} ${ENVIRONMENT} ${BUILDNUMBER} ${PROJECT} ${USER} "$6"
