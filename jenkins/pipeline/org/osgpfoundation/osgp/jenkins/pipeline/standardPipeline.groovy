/**
 * Pipeline script for a standard pull request build.
 */
def call(body) {

    // The following makes it possible to parameterize this script.
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Some defaults
    def stream = '${config.stream}'
    def serverName = stream + '-at-pr-' + env.BUILD_NUMBER
    def playbook = stream + '-at.yml'
    def repo = '${config.repo}'

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
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        stages {
            stage('Set status') {
                steps {
                    step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
                }
            }

            stage('Build') {
                steps {
                    withMaven(
                            maven: 'Apache Maven 3.5.0',
                            mavenLocalRepo: '.repository',
                            options: [
                                    artifactsPublisher(disabled: true),
                            ]) {
                        sh "mvn clean install ${config.mavenParameters}"
                    }

                    // Collect all build wars and copy them to target/artifacts
                    sh "rm -rf target/artifacts && mkdir -p target/artifacts && find . -name *.war -exec cp -f {} target/artifacts \\;"

                    // Clone the release repository in order to deploy
                    sh "git clone git@github.com:SmartSocietyServices/release.git"

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
                    sh "cd release && plays/deploy-files-to-system.yml -e osgp_version=${POMVERSION} -e deployment_name=${servername} -e directory_to_deploy=../../target/artifacts -e tomcat_restart=false -e ec2_instance_type=m4.large"
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
                            booleanParam(name: 'ARTIFACT_DIRECTORY_REMOTE_SRC', value: true)]
                }
            }
        }

        post {
            always {
                // Always destroy the test environment
                build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: serverName),
                                                                 string(name: 'PLAYBOOK', value: playbook)]
            }
            success {
                // Clean the complete workspace
                cleanWs()
                step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
            }
            failure {
                // Mail everyone that the job failed
                step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: '${config.mailRecipients}', sendToIndividuals: false])
                step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])

                // Clean only those things which are unnecessary to keep.
                cleanWs(patterns: [[pattern: '**/target/*-SNAPSHOT/', type: 'INCLUDE']])
            }
        }
    }
}
