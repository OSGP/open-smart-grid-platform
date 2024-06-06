// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

// Pipeline script for the OSGP Integration-Tests Pull Request job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-pr-' + env.BUILD_NUMBER
def playbook = stream + '-at.yml'

// Choose the branch to use for SmartSocietyServices/release repository. Default value is 'master'.
def branchReleaseRepo = 'master'

void setBuildStatus(String message, String state) {
    echo "Set status on GitHub to: " + state + " with message: " + message
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: "git@github.com:OSGP/open-smart-grid-platform.git"],
        contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "default"],
        errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
        commitShaSource: [$class: "ManuallyEnteredShaSource", sha:  env.ghprbActualCommit],
        statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
    ]);
}

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

        stage ('Set GitHub Status') {
            steps {
                // Set status on GitHub to PENDING.
                setBuildStatus("Build triggered", "PENDING")
            }
        } // stage

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
                    sh "mvn clean install -B -T1C -DskipTestJarWithDependenciesAssembly=false"
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

        stage ('Parallel Stages') {
            parallel {
                stage ('Sonar Analysis') {
                    steps {
                        withSonarQubeEnv('sonar.osgp.cloud') {
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
                                sh '''
                                   mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -B \
                                       -Dmaven.repo.local=.repository \
                                       -Dsonar.java.source=8 \
                                       -Dsonar.ws.timeout=600 \
                                       -Dsonar.pullrequest.key=$ghprbPullId \
                                       -Dsonar.pullrequest.branch=$ghprbSourceBranch \
                                       -Dsonar.pullrequest.base=$ghprbTargetBranch
                                   '''
                            }
                        }
                    }
                } // stage

                stage ('Deploy AWS System') {
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
                } // stage
            } // parallel
        } // stage

        stage ('Run Tests') {
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
# - Replace spaces with and, commas with or surrounded with parentheses
# - Replace ~@ with not @ surrounded with parentheses
# - Output to cucumber-tags.txt, which is imported as environment variables

EXTRACTED_TAGS=`echo $ghprbPullLongDescription | grep -o \'\\[@.*\\]\' | sed \'s/\\[/ /g\' | sed \'s/\\]//g\' | sed \':a;N;$!ba;s/\\n/ /g\' | sed \'s/ / and /g\' | sed \'s/\\([^[:blank:]]\\+,[^[:blank:]]\\+\\)/\\(\\1\\)/g\' | sed \'s/,/ or /g\' | sed \'s/~\\(@[^[:blank:]]\\+\\)/\\(not \\1\\)/g\'`

echo "$EXTRACTED_TAGS and not @NightlyBuildOnly" > "${WORKSPACE}/cucumber-tags"

echo Found cucumber tags: [$EXTRACTED_TAGS]'''
                sh "ssh-keygen -f \"$HOME/.ssh/known_hosts\" -R ${servername}-instance.dev.osgp.cloud"
                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-common centos \"OSGP Development.pem\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-microgrids centos \"OSGP Development.pem\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runDistributionAutomationTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-distributionautomation centos \"OSGP Development.pem\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
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
                cucumber buildStatus: 'FAILURE', fileIncludePattern: '**/cucumber.json', sortingMethod: 'ALPHABETICAL'
                archiveArtifacts '**/target/*.tgz'

                // Check the console log for failed tests
                step([$class: 'LogParserPublisher', projectRulePath: 'console-test-result-rules', unstableOnWarning: true, failBuildOnError: true, useProjectRule: true])
            }
        } // stage
    } // stages

    post {
        always {
            echo "End of pipeline"
            // Always destroy the test environment
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]
        }
        aborted {
            setBuildStatus("Build failed", "FAILURE")
        }
        unstable {
            setBuildStatus("Build failed", "FAILURE")
        }
        failure {
            // Mail everyone that the job failed
            emailext (
                subject: '${DEFAULT_SUBJECT}',
                body: '${DEFAULT_CONTENT}',
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}',
                from: '${DEFAULT_REPLYTO}')

            setBuildStatus("Build failed", "FAILURE")
        }
        success {
            setBuildStatus("Build succeeded", "SUCCESS")
        }
        cleanup {
            // Delete workspace folder.
            cleanWs()
        }
    } // post
} // pipeline
