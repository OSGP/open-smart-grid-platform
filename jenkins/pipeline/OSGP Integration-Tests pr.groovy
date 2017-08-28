// Pipeline script for the OSGP Integration-Tests Pull Request job in Jenkins

def stream = 'osgp'
def servername = stream + '-at-pr-' + env.BUILD_NUMBER
//def servername = stream + '-at-pr-26'
def playbook = stream + '-at.yml'
def extravars = 'ec2_instance_type=t2.large'
def repo = 'git@github.com:SmartSocietyServices/Integration-Tests.git'

def server = Artifactory.server 'OSGP Artifactory Server'
def rtMaven = Artifactory.newMavenBuild()

pipeline {
    agent any
    options {
        ansiColor('xterm')
        timeout(240)
        // Only keep the 10 most recent builds
        buildDiscarder(logRotator(numToKeepStr:'10'))
    }

    stages {
        stage ('Git') {
            steps {
                // Example sha1 c0c708ef65fa1217e84d9762c974e6b8a40d35b3
                deleteDir()
                checkout([$class: 'GitSCM', branches: [[name: '${sha1}']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: false, recursiveSubmodules: true, reference: '', trackingSubmodules: true]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '68539ca2-6175-4f68-a7af-caa86f7aa37f', refspec: '+refs/pull/*:refs/remotes/origin/pr/*', url: 'git@github.com:OSGP/Integration-Tests.git']]])
            }
        }

        stage ('Set status') {
            steps {
                step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])
            }
        }
        
        stage 'Artifactory configuration' {
            steps {
				rtMaven.tool = MAVEN_TOOL // Tool name from Jenkins configuration
				rtMaven.deployer releaseRepo:'osgp-release-local', snapshotRepo:'osgp-snapshot-local', server: server
				rtMaven.resolver releaseRepo:'osgp-release', snapshotRepo:'osgp-snapshot', server: server
				def buildInfo = Artifactory.newBuildInfo()
			}
		}

        stage ('Build') {
            steps {
                // TODO: use withMaven
                //withMaven(
                      // Maven installation declared in the Jenkins "Global Tool Configuration"
                //	maven: 'M3',
                      // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                      // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                //	mavenSettingsConfig: 'my-maven-settings',
                //	mavenLocalRepo: '.repository') {

                      // Run the maven build
                //	sh "mvn clean install -DskipTestJarWithDependenciesAssembly=false"
                //} // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe reports and FindBugs reports
                
                //sh "mvn clean install -DskipTestJarWithDependenciesAssembly=false"
                
                rtMaven.run pom: 'pom.xml', goals: 'clean install -DskipTestJarWithDependenciesAssembly=false', buildInfo: buildInfo
            }
        }
        
        stage 'Publish build info' {
        	steps { 
				server.publishBuildInfo buildInfo
        	} 
        }

        stage ('Deploy AWS system') {
            steps {
                build job: 'Deploy an AWS System', parameters: [string(name: 'SERVERNAME', value: servername), string(name: 'PLAYBOOK', value: playbook), string(name: 'EXTRAVARS', value: extravars)]
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

echo $EXTRACTED_TAGS > "${WORKSPACE}/cucumber-tags"

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
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'kevin.smeets@cgi.com,ruud.lemmers@cgi.com,hans.rooden@cgi.com,martijn.sips@cgi.com', sendToIndividuals: false])
            step([$class: 'GitHubSetCommitStatusBuilder', contextSource: [$class: 'ManuallyEnteredCommitContextSource']])

            // Clean only those things which are unnecessary to keep.
            cleanWs(patterns: [[pattern: '**/target/*-SNAPSHOT/', type: 'INCLUDE']])
        }
    }
}
