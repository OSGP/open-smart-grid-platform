#!/bin/bash

set -e

if [ "$#" -eq 0 ]
then
  echo "Usage: $0 <server> <project> <user> [<ssh key file>] [<additional java parameters>] [<additional cucumber options>]"
  echo ""
  exit 1
fi

if [ "$#" -ne 7 ]; then
    echo "Illegal number of parameters"
    exit -1
fi

SERVER=$1
FOLDER=$2
PROJECT=$3
USER=$4
SSH_KEY_FILE=$5
ADDITIONAL_PARAMETERS=$6
ADDITIONAL_CUCUMBER_OPTIONS=$7

# If a space is found in the identity file then create a shortcut as the -i parameter for ssh can't handle spaces.
[[ $SSH_KEY_FILE != "" ]] && [[ $SSH_KEY_FILE =~ " " ]] && echo "Creating link ${HOME}/.ssh/${5/ /} => ${HOME}/.ssh/${5} ..." && ln -sf "${HOME}/.ssh/${5}" "${HOME}/.ssh/${5/ /}"

# Now determine if a -i parameter should be generated
[ "${SSH_KEY_FILE}"!="" ] && SSH_KEY_FILE="-oIdentityFile=\"${HOME}/.ssh/${5/ /}\"" && echo "SSH_KEY_FILE=[${SSH_KEY_FILE}]"

echo "Going to run the cucumber project ${PROJECT} on ${SERVER} ..."
echo "- Create directory structure ..."
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo mkdir -p /data/software/${PROJECT}/target/output\"\""
echo "  [${CMD}]"
${CMD}
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo mkdir -p /data/software/certificates\"\""
echo "  [${CMD}]"
${CMD}
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo chown -R ${USER}:${USER} /data/software\"\""
echo "  [${CMD}]"
${CMD}

echo "- Copy over necessary files to ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${FOLDER}/certificates/* ${USER}@${SERVER}:/data/software/certificates"
echo "  [${CMD}]"
${CMD}

echo "- Copy over cucumber project ${PROJECT} to ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${FOLDER}/${PROJECT}/target/cucumber-*test-jar-with-dependencies.jar ${USER}@${SERVER}:/data/software/${PROJECT}"
echo "  [${CMD}]"
${CMD}

echo "- Executing cucumber project ${PROJECT} remote on ${SERVER} ..."
CMD="sudo java -javaagent:/usr/share/tomcat/lib/jacocoagent.jar=destfile=target/code-coverage/jacoco-it.exec ${ADDITIONAL_PARAMETERS}\
 -Dcucumber.execution.strict=true\
 -Dcucumber.filter.tags=\"not @Skip ${ADDITIONAL_CUCUMBER_OPTIONS}\"\
 -DskipITs=false\
 -Dtimeout=30\
 -DskipITCoverage=false\
 -DrunHeadless=true\
 -jar cucumber-*-test-jar-with-dependencies.jar -report target/output"
echo "  [${CMD}]"
CMD="ssh -oStrictHostKeyChecking=no -oTCPKeepAlive=yes -oServerAliveInterval=50 ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"cd /data/software/${PROJECT} && ${CMD}\"\""
${CMD}

echo "- Take ownership over /data/software/${PROJECT}/* directory ..."
CMD="sudo chown -R ${USER}:${USER} /data/software/${PROJECT}/*"
echo "  [${CMD}]"
CMD="ssh -oStrictHostKeyChecking=no -oTCPKeepAlive=yes -oServerAliveInterval=50 ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"cd /data/software/${PROJECT} && ${CMD}\"\""
${CMD}

echo '- Create zip file from files from server ...'
CMD="sudo tar zhcvf /tmp/${SERVER}-${PROJECT}.tgz /etc/osgp /etc/httpd/conf.d /usr/share/tomcat/conf /var/log/tomcat /var/log/osgp --warning=no-file-changed || true"
echo "  [${CMD}]"
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"cd /data/software/${PROJECT} && ${CMD}\"\""
${CMD}

echo '- Take ownership over /tmp/${SERVER}-${PROJECT}.tgz ...'
CMD="sudo chown $USER:$USER /tmp/${SERVER}-${PROJECT}.tgz"
echo "  [${CMD}]"
CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"cd /data/software/${PROJECT} && ${CMD}\"\""
${CMD}

echo "- Collecting test output from cucumber project ${PROJECT} on ${SERVER} ..."
mkdir -p ${PROJECT}/target
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} -r ${USER}@${SERVER}:/data/software/${PROJECT}/target/* ${PROJECT}/target"
echo "  [${CMD}]"
${CMD}

echo "- Collecting code-coverage output from cucumber project ${PROJECT} on ${SERVER} ..."
mkdir -p ${PROJECT}/code-coverage
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} -r ${USER}@${SERVER}:/data/software/${PROJECT}/target/code-coverage/* ${PROJECT}/code-coverage"
echo "  [${CMD}]"
${CMD}

echo "- Collecting server files from ${SERVER} ..."
CMD="scp -oStrictHostKeyChecking=no ${SSH_KEY_FILE} -r ${USER}@${SERVER}:/tmp/${SERVER}-${PROJECT}.tgz ${PROJECT}/target"
echo "  [${CMD}]"
${CMD}

#echo "- Clean logging for next tests on ${SERVER} ..."
#CMD="ssh -oStrictHostKeyChecking=no ${SSH_KEY_FILE} ${USER}@${SERVER} \"\"sudo truncate -s 0 /var/log/tomcat/catalina.out && sudo find /var/log/osgp/logs/ -name 'osgp*.log' -exec truncate -s 0 {} \; && sudo find /var/log/osp/logs/ -name 'web*.log' -exec truncate -s0 {} \; \"\""
#echo "  [${CMD}]"
#${CMD}

echo "Done."
