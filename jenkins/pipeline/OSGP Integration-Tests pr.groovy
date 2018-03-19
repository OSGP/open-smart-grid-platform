// Pipeline script for the OSGP Integration-Tests Pull Request job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-pr-' + env.BUILD_NUMBER
def playbook = stream + '-at.yml'
def repo = 'git@github.com:OSGP/Integration-Tests.git'
// Choose the branch to use for SmartSocietyServices/release repository. Default value is 'master'.
def branchReleaseRepo = 'master'

pipeline {
    agent any

    environment {
        // Default the pom version
        POMVERSION="latest"
    }

    options {
        ansiColor('xterm')
        timestamps()
        timeout(240)
        // Only keep the 10 most recent builds
        buildDiscarder(logRotator(numToKeepStr:'10'))
    }

    stages {

        // The pr job will clone the git repository, but nothing more. So gitmodules are not downloaded. Therefore we
        // need to trigger this manually
        stage ('Update submodules') {
            steps {
                sh "git submodule update --remote --init"
            }
        }

        stage ('Set status') {
            steps {
                step([$class: 'GitHubSetCommitStatusBuilder',
                      contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
            }
        }
        
        stage ('Build') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.5.0',
                        mavenLocalRepo: '.repository',
                        options: [
                                artifactsPublisher(disabled: true),
                        ]) {
                    sh "mvn clean install -DskipTestJarWithDependenciesAssembly=false"
                }

                // Collect all build wars and copy them to target/artifacts
                sh "rm -rf target/artifacts && mkdir -p target/artifacts && find . -name *.war -exec cp -uf {} target/artifacts \\;"

                // Clone the release repository in order to deploy
                sh "rm -rf release && git clone git@github.com:SmartSocietyServices/release.git"

                // Checkout branch for release repository
                sh "cd release && git checkout ${branchReleaseRepo}"

                script {
                    // Determine the actual pom version from the pom.
                    POMVERSION = sh ( script: "grep \"<version>\" pom.xml | sed \"s#<[/]\\?version>##g;s# ##g\" | grep SNAPSHOT", returnStdout: true).trim()
                }
                echo "Using version ${POMVERSION} (from pom) to collect artifacts which are needed to deploy a new environment but weren't build in this job."

                // Download missing artifacts from artifactory for the same version
                // - The following artifacts are not in this repository
                sh "cd release && plays/download-artifacts.yml -e artifactstodownload='{{ configuration_artifacts }}' -e deployment_type=snapshot -e osgp_version=${POMVERSION} -e tmp_artifacts_directory=../../target/artifacts"
                sh "cd release && plays/download-artifacts.yml -e artifactstodownload='{{ dlms_simulator_artifacts }}' -e deployment_type=snapshot -e osgp_version=${POMVERSION} -e tmp_artifacts_directory=../../target/artifacts"
                // - The following artifacts are not specified in the root pom.xml, thus they should be retrieved from the artifactory.
                sh "cd release && plays/download-artifacts.yml -e artifactstodownload='{{ distribution_automation_artifacts }}' -e deployment_type=snapshot -e osgp_version=${POMVERSION} -e tmp_artifacts_directory=../../target/artifacts"
                sh "cd release && plays/download-artifacts.yml -e artifactstodownload='{{ iec61850_simulator_artifacts }}' -e deployment_type=snapshot -e osgp_version=${POMVERSION} -e tmp_artifacts_directory=../../target/artifacts"

                // Now create a new single instance (not stream specific) and put all the artifacts in /data/software/artifacts
                sh "cd release && plays/deploy-files-to-system.yml -e osgp_version=${POMVERSION} -e deployment_name=${servername} -e directory_to_deploy=../../target/artifacts -e tomcat_restart=false -e ec2_instance_type=m4.large -e ami_name=CentOS6SingleInstance -e ami_owner=self"
            }
        }

        stage ('Deploy local artifacts') {
            steps {
                // Deploy stream specific system using the local artifacts from /data/software/artifacts on the system
                build job: 'Deploy an AWS System', parameters: [
                        string(name: 'SERVERNAME', value: servername),
                        string(name: 'PLAYBOOK', value: playbook),
                        booleanParam(name: 'INSTALL_FROM_LOCAL_DIR', value: true),
                        string(name: 'ARTIFACT_DIRECTORY', value: "/data/software/artifacts"),
                        string(name: 'OSGP_VERSION', value: POMVERSION),
                        booleanParam(name: 'ARTIFACT_DIRECTORY_REMOTE_SRC', value: true),
                        string(name: 'BRANCH', value: branchReleaseRepo)]
            }
        }
       
        stage('Run tests') {
            steps {
                sh '''echo Searching for specific Cucumber tags in git commit.

# Format for cucumber-tags in Pull request description: [@tag1 @tag2 @tags3a,@tags3b]
#   will lead to cucumber.options=\'--tags @tag1 --tags @tag2 --tags @tags3a,@tags3b\'
# These tags will be available as ENV var: ${CUCUMBER_TAGS} for use in maven -Dcucumber.options

# Search algorithm:
# - Search for PR env var: ghprbPullLongDescription
# - Search for [<tags>]
# - Remove brackets []
# - Replace new-lines with spaces
# - Replace spaces with --tags
# - Output to cucumber-tags.txt, which is imported as environment variables

EXTRACTED_TAGS=`echo $ghprbPullLongDescription | grep -o \'\\[@.*\\]\' | sed \'s/\\[/ /g\' | sed \'s/\\]//g\' | sed \':a;N;$!ba;s/\\n/ /g\' | sed \'s/ / --tags /g\'`

echo $EXTRACTED_TAGS --tags ~@NightlyBuildOnly > "${WORKSPACE}/cucumber-tags"

echo Found cucumber tags: [$EXTRACTED_TAGS]'''

                sh "ssh-keygen -f \"$HOME/.ssh/known_hosts\" -R ${servername}-instance.dev.osgp.cloud"
                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-common centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-microgrids centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
            }
        }

        stage ('Collect coverage') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.5.0',
                        mavenLocalRepo: '.repository',
                        options: [
                                artifactsPublisher(disabled: true),
                                openTasksPublisher(disabled: true)
                        ]) {
                    sh "mvn -Djacoco.destFile=target/code-coverage/jacoco-it.exec -Djacoco.address=${servername}.dev.osgp.cloud org.jacoco:jacoco-maven-plugin:0.7.9:dump"
                }
            }
        }

        stage('Reporting') {
            steps {
                jacoco execPattern: '**/code-coverage/jacoco-it.exec'
                cucumber buildStatus: null, fileIncludePattern: '**/cucumber.json', sortingMethod: 'ALPHABETICAL'
                archiveArtifacts '**/target/*.tgz'

                // Check the console log for failed tests
                step([$class: 'LogParserPublisher', projectRulePath: 'console-test-result-rules', unstableOnWarning: true, useProjectRule: true])
            }
        }
    }

    post {
        always {
            // Always destroy the test environment
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]
        }
        success {
            // Clean the complete workspace
            cleanWs()
            step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
        }
        failure {
            // Mail everyone that the job failed
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'kevin.smeets@cgi.com,ruud.lemmers@cgi.com', sendToIndividuals: false])
            step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])

            // Clean only those things which are unnecessary to keep.
            cleanWs(patterns: [[pattern: '**/target/*-SNAPSHOT/', type: 'INCLUDE']])
        }
    }
}
