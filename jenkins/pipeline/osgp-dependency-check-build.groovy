// Pipeline script for the OSGP Dependency Check Build job in Jenkins

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
                        maven: 'Apache Maven',
                        mavenLocalRepo: '.repository',
                        publisherStrategy: 'EXPLICIT') {
                    sh "mvn -V -B -T 1 clean install site -DskipTests=true -DskipITs=true -Dmaven.version.rules=file://${pwd()}/super/versions-plugin-rules.xml -Dmaven.site.distributionManagement.site.url="
                }
                publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: false,
                        reportDir: 'super/target/site',
                        reportFiles: '*.html',
                        reportName: 'Maven Site Reports'
                    ])
            }
        } // stage

    } // stages

    post {
        always {
            echo "End of pipeline"
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
