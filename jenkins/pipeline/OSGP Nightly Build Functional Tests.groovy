// Pipeline script for the OSGP Nightly Build Functional Tests job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-' + env.BUILD_NUMBER
def playbook = stream + '-at.yml'
def repo = 'git@github.com:OSGP/Integration-Tests.git'

pipeline {
    agent any

    options {
        ansiColor('xterm')
        timestamps()
        timeout(240)
        // Only keep the 10 most recent builds
        buildDiscarder(logRotator(numToKeepStr:'10'))
    }

    stages {

        // The nightly job will clone the git repository, but nothing more. So gitmodules are not downloaded. Therefore we
        // need to trigger this manually
        stage ('Update submodules') {
            steps {
                sh "git submodule update --remote --init"
            }
        }

        stage('Build') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.5.0',
                        mavenLocalRepo: '.repository',
                        options: [
                                artifactsPublisher(disabled: true)
                        ]) {
                    sh "mvn clean install -DskipTestJarWithDependenciesAssembly=false"
                }
            }
        }

        stage ('Deploy AWS system') {
            steps {
                build job: 'Deploy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]
            }
        }

        stage('Run tests') {
            steps {
                sh "ssh-keygen -f \"$HOME/.ssh/known_hosts\" -R ${servername}-instance.dev.osgp.cloud"
                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-common centos \"OSGP Development.pem\""
                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\""
                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-microgrids centos \"OSGP Development.pem\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\""
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
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]            
        }
        failure {
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'kevin.smeets@cgi.com,ruud.lemmers@cgi.com', sendToIndividuals: false])
        }
        success {
            // Clean the complete workspace
            cleanWs()
        }
    }
}
