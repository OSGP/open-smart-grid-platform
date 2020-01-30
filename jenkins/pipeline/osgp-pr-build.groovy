// Pipeline script for the OSGP Integration-Tests Pull Request job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-pr-' + env.BUILD_NUMBER
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
        FIXED_LIB_UPGRADE_VERSION="4.40.0-SNAPSHOT"
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
                step([$class: 'GitHubSetCommitStatusBuilder',
                      contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
            }
        }

        stage ('Maven Build') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.6.2',
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
                    sh "mvn clean install -B -T4 -DskipTestJarWithDependenciesAssembly=false"
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
                sh "cd release && plays/download-artifacts.yml -e artifactstodownload='{{ dlms_simulator_artifacts }}' -e deployment_type=snapshot -e osgp_version=${FIXED_LIB_UPGRADE_VERSION} -e tmp_artifacts_directory=../../target/artifacts"
                // Make sure a standalone version of the dlms device simulator is present
// Ruud, temporarily modify the version number by copying the DLMS simulator files
                sh "cp -p target/artifacts/dlms-device-simulator-${FIXED_LIB_UPGRADE_VERSION}.jar target/artifacts/dlms-device-simulator-${POMVERSION}-standalone.jar"
                sh "cp -p target/artifacts/osgp-simulator-dlms-triggered-${FIXED_LIB_UPGRADE_VERSION}.war target/artifacts/osgp-simulator-dlms-triggered-${POMVERSION}.war"

                // Now create a new single instance (not stream specific) and put all the artifacts in /data/software/artifacts
                sh "cd release && plays/deploy-files-to-system.yml -e osgp_version=${POMVERSION} -e deployment_name=${servername} -e directory_to_deploy=../../target/artifacts -e tomcat_restart=false -e ec2_instance_type=m4.large -e ami_name=CentOS6SingleInstance -e ami_owner=self"
            }
        }

        stage ('Sonar Analysis') {
            steps {
                withSonarQubeEnv('SonarQube local') {
                    withMaven(
                        maven: 'Apache Maven 3.6.2',
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
                        sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -B -Dmaven.test.failure.ignore=true -Dclirr=true " +
                          "-Dsonar.github.repository=OSGP/open-smart-grid-platform -Dsonar.analysis.mode=preview " +
                          "-Dsonar.issuesReport.console.enable=true -Dsonar.forceUpdate=true -Dsonar.github.pullRequest=$ghprbPullId " +
                          "${SONAR_EXTRA_PROPS}"
                    }
                }
            }
        }

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
        }

        stage('Run Tests') {
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
                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-common centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-microgrids centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\" \"\" \"\" \"`cat \"${WORKSPACE}/cucumber-tags\"`\""
            }
        }

        stage ('Collect Coverage') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.6.2',
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
    } // stages

    post {
        always {
            echo "End of pipeline"
            // Always destroy the test environment
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]
        }
        success {
            step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
        }
        failure {
            // Mail everyone that the job failed
            emailext (
                subject: '${DEFAULT_SUBJECT}',
                body: '${DEFAULT_CONTENT}',
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}',
                from: '${DEFAULT_REPLYTO}')

            step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
        }
        cleanup {
            // Delete workspace folder.
            cleanWs()
        }
    } // post
} // pipeline

