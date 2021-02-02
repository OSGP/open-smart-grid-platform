def call() {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'JENKINS_KEY_FILE' ),
                     sshUserPrivateKey(credentialsId: 'osgp-development-ssh-key', keyFileVariable: 'OSGP_DEVELOPMENT_KEY_FILE' )]) {
        sh 'mkdir ~/.ssh'
        sh 'cp ${JENKINS_KEY_FILE} ~/.ssh/id_rsa'
        sh 'cp ${OSGP_DEVELOPMENT_KEY_FILE} ~/.ssh/\'osgp_development.pem\''
        sh 'chmod 600 ~/.ssh/*'
        sh 'echo \"Host github.com\n\tStrictHostKeyChecking no\n\" >> ~/.ssh/config'
    }
}
