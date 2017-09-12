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
        
        options {
            ansiColor('xterm')
            timestamps()
            timeout(240)
            // Only keep the 10 most recent builds
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        stages {
            stage('Git') {
                steps {
                    // Cleanup directory.
                    deleteDir()

                    // Checkout the pr branch
                    checkout([$class: 'GitSCM', branches: [[name: '${sha1}']], doGenerateSubmoduleConfigurations: false,
                              extensions: [[$class: 'SubmoduleOption', disableSubmodules: false,
                                            parentCredentials: false, recursiveSubmodules: true, reference: '',
                                            trackingSubmodules: true]], submoduleCfg: [],
                              userRemoteConfigs: [[credentialsId: '68539ca2-6175-4f68-a7af-caa86f7aa37f',
                                                   refspec: '+refs/pull/*:refs/remotes/origin/pr/*', url: repo]]])
                }
            }

            stage('Set status') {
                steps {
                    step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
                }
            }

            stage('Build') {
                steps {
                    withMaven(
                            maven: 'Apache Maven 3.5.0',
                            mavenLocalRepo: '.repository') {
                        sh "mvn clean install ${config.mavenParameters}"
                    }
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
