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

SERVER=${STREAM}-${ENVIRONMENT}-${BUILDNUMBER}-instance.dev.osgp.cloud

# If a space is found in the identity file then create a shortcut as the -i parameter for ssh can't handle spaces.
[ "${SSH_KEY_FILE}"!="" ] && [ -f "${HOME}/.ssh/$6" ] && [ "${SSH_KEY_FILE}"=~" " ] && ln -sf "${HOME}.ssh/$6" "${HOME}.ssh/${6/ /}"

# Now determine if a -i parameter should be generated
[ "${SSH_KEY_FILE}"!="" ] && [ -f "${HOME}/.ssh/$6" ] && SSH_KEY_FILE="-i ${HOME}.ssh/${6/ /}"

echo "Going to run the cucumber project ${PROJECT} on ${SERVER} ..."
echo "- Create directory structure ..."
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo mkdir -p /data/software/${PROJECT}/target/output\"\""
echo "  [${CMD}]"
$CMD
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo mkdir -p /data/software/certificates\"\""
echo "  [${CMD}]"
$CMD
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo chown -R ${USER}:${USER} /data/software\"\""
echo "  [${CMD}]"
$CMD

echo "- Copy over nesseccary files to ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} certificates/* ${USER}@${SERVER}:/data/software/certificates"
echo "  [${CMD}]"
$CMD

echo "- Copy over cucumber project ${PROJECT} to ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${PROJECT}/target/cucumber-*test-jar-with-dependencies.jar ${USER}@${SERVER}:/data/software/${PROJECT}"
echo "  [${CMD}]"
$CMD

echo "- Executing cucumber project ${PROJECT} remote on ${SERVER} ..."
CMD="sudo java -javaagent:/usr/share/tomcat/lib/jacocoagent.jar=destfile=target/coverage-reports/jacoco-it.exec ${ADDITIONAL_PARAMETERS} -DskipITs=false -DskipITCoverage=false -jar cucumber-*-test-jar-with-dependencies.jar -report target/output"
echo "  [${CMD}]"
ssh -oStrictHostKeyChecking=no "${SSH_KEY_FILE}" ${USER}@${SERVER} "cd /data/software/${PROJECT} && ${CMD}"

echo "- Collecting output from cucumber project ${PROJECT} on ${SERVER} ..."
mkdir -p ${PROJECT}/target
scp -oStrictHostKeyChecking=no "${SSH_KEY_FILE}" -r ${USER}@${SERVER}:/data/software/${PROJECT}/target/* ${PROJECT}/target

echo "Done."
