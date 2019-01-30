// Pipeline script for the OSGP Nightly Build job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-' + env.BUILD_NUMBER
def playbook = stream + '-at.yml'

// Choose the branch to use for SmartSocietyServices/release repository. Default value is 'master'.
def branchReleaseRepo = 'master'

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

        stage('Maven Build') {
            steps {
                withMaven(
                        maven: 'Apache Maven 3.5.0',
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
            }
        }

        stage ('Deploy AWS System') {
            steps {
                build job: 'Deploy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername),
                                                                string(name: 'PLAYBOOK', value: playbook),
                                                                string(name: 'BRANCH', value: branchReleaseRepo)]
            }
        }

        stage('Run Tests') {
            steps {
//                sh "ssh-keygen -f \"$HOME/.ssh/known_hosts\" -R ${servername}-instance.dev.osgp.cloud"
//                sh "./runTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-common centos \"OSGP Development.pem\""
//                sh "./runPubliclightingTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-publiclighting centos \"OSGP Development.pem\""
//                sh "./runMicrogridsTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-microgrids centos \"OSGP Development.pem\""
                sh "./runSmartMeteringTestsAtRemoteServer.sh ${servername}-instance.dev.osgp.cloud integration-tests cucumber-tests-platform-smartmetering centos \"OSGP Development.pem\""
            }
        }

        stage ('Collect Coverage') {
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
            echo "End of pipeline"
            build job: 'Destroy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook)]            
        }
        failure {
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'kevin.smeets@cgi.com,ruud.lemmers@cgi.com,sander.van.der.heijden@cgi.com', sendToIndividuals: false])
        }
        cleanup {
            // Delete workspace folder.
            cleanWs()
        }
    }
}
