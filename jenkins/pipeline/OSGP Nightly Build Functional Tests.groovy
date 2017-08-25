// Pipeline script for the OSGP Nightly Build Functional Tests job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-' + env.BUILD_NUMBER
//def servername = stream + '-at-26'
def playbook = stream + '-at.yml'
def extravars = 'ec2_instance_type=t2.large'

pipeline {
    agent any
    options {
        ansiColor('xterm')
        timeout(240)
        // Only keep the 10 most recent builds
        buildDiscarder(logRotator(numToKeepStr:'10'))
    }
    stages {
        stage('Git') {
            steps {
                // Cleanup workspace
                deleteDir()

                git branch: 'development', credentialsId: '68539ca2-6175-4f68-a7af-caa86f7aa37f', url: 'git@github.com:OSGP/Integration-Tests.git'
                sh 'git submodule update --init --recursive --remote'
            }
        }

        stage('Build') {
            steps {
                // TODO: use withMaven
                sh "/usr/local/apache-maven/bin/mvn clean install -DskipTestJarWithDependenciesAssembly=false"
            }
        }

        stage ('Deploy AWS system') {
            steps {
                build job: 'Deploy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook), string(name: 'EXTRAVARS', value: extravars)]
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
                // TODO: use withMaven
                sh "/usr/local/apache-maven/bin/mvn -Djacoco.destFile=target/code-coverage/jacoco-it.exec -Djacoco.address=${servername}.dev.osgp.cloud org.jacoco:jacoco-maven-plugin:0.7.9:dump"
            }
        }

        stage('Reporting') {
            steps {
                jacoco execPattern: '**/code-coverage/jacoco-it.exec'
                cucumber '**/cucumber.json'
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
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'kevin.smeets@cgi.com,ruud.lemmers@cgi.com,hans.rooden@cgi.com,martijn.sips@cgi.com', sendToIndividuals: false])
        }
    }
}
