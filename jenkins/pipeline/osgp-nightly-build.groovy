// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

// Pipeline script for the OSGP Nightly Build job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-' + env.BUILD_NUMBER
def playbook = stream + '-at.yml'

// Choose the branch to use for SmartSocietyServices/release repository. Default value is 'master'.
def branchReleaseRepo = 'master'

pipeline {
    agent {
        node {
            label 'buildslave'
        }
    }

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

        stage ('Maven Build') {
            steps {
                withMaven(
                        maven: 'Apache Maven',
                        mavenLocalRepo: '.repository',
                        options: [
                                artifactsPublisher(disabled: true),
                                junitPublisher(disabled: true),
                                findbugsPublisher(disabled: true),
                                openTasksPublisher(disabled: true),
                                dependenciesFingerprintPublisher(disabled: true),
                                concordionPublisher(disabled: true),
                                invokerPublisher(disabled: true),
                                jgivenPublisher(disabled: true),
                                jacocoPublisher(disabled: true)
                        ]) {
                    sh "mvn clean install -DskipTestJarWithDependenciesAssembly=false"
                }
                
                // Create clean folder target/artifacts
                sh "rm -rf target/artifacts && mkdir -p target/artifacts"

                // Collect all build wars and simulator jar and copy them to target/artifacts
                sh "find . -name *.war -exec cp -uf {} target/artifacts \\;"
                // Collect dlms device simulator jar and copy to target/artifacts
                sh "find . -name dlms-device-simulator*.jar -exec cp -uf {} target/artifacts \\;"

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

                // Now create a new single instance (not stream specific) and put all the artifacts in /data/software/artifacts
                sh "cd release && plays/deploy-files-to-system.yml -e osgp_version=${POMVERSION} -e deployment_name=${servername} -e directory_to_deploy=../../target/artifacts -e tomcat_restart=false -e ec2_instance_type=m4.xlarge -e ami_name=CentOS7SingleInstance -e ami_owner=self"
            }
        } // stage

        stage ('Deploy AWS System') {
            steps {
                build job: 'Deploy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername),
                                                                string(name: 'PLAYBOOK', value: playbook),
                                                                booleanParam(name: 'INSTALL_FROM_LOCAL_DIR', value: true),
                                                                string(name: 'ARTIFACT_DIRECTORY', value: "/data/software/artifacts"),
                                                                string(name: 'OSGP_VERSION', value: POMVERSION),
                                                                booleanParam(name: 'ARTIFACT_DIRECTORY_REMOTE_SRC', value: true),
                                                                string(name: 'BRANCH', value: branchReleaseRepo)]
            }
        } // stage

        stage ('Run Tests') {
            steps {
                sh "ssh-keygen -f \"$HOME/.ssh/known_hosts\" -R ${servername}-instance.dev.osgp.cloud"
                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-common centos \"OSGP Development.pem\" \"\" \"\""
                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\" \"\" \"\""
                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-microgrids centos \"OSGP Development.pem\" \"\" \"\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\" \"\" \"\""
                sh "./runDistributionAutomationTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-distributionautomation centos \"OSGP Development.pem\" \"\" \"\""
            }
        } // stage

        stage ('Collect Coverage') {
            steps {
                withMaven(
                        maven: 'Apache Maven',
                        mavenLocalRepo: '.repository',
                        options: [
                                artifactsPublisher(disabled: true),
                                openTasksPublisher(disabled: true)
                        ]) {
                    sh "mvn -Djacoco.destFile=target/code-coverage/jacoco-it.exec -Djacoco.address=${servername}.dev.osgp.cloud org.jacoco:jacoco-maven-plugin:0.7.9:dump"
                }
            }
        } // stage

        stage ('Reporting') {
            steps {
                jacoco execPattern: '**/code-coverage/jacoco-it.exec'

                // Check the console log for failed tests
                step([$class: 'LogParserPublisher', projectRulePath: 'console-test-result-rules', unstableOnWarning: true, failBuildOnError: true, useProjectRule: true])
            }
        } // stage
    } // stages

    post {
        always {
            echo "End of pipeline"
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]
            
            cucumber buildStatus: 'FAILURE', fileIncludePattern: '**/cucumber.json', sortingMethod: 'ALPHABETICAL'
            archiveArtifacts '**/target/*.tgz'
        }
        failure {
            emailext (
                subject: '${DEFAULT_SUBJECT}',
                body: '${DEFAULT_CONTENT}',
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}',
                from: '${DEFAULT_REPLYTO}')
        }
        cleanup {
            // Delete workspace folder.
            cleanWs()
        }
    } // post
} // pipeline
