pipelineJob('gxf-nightly-build') {
    description('GXF nightly build job building all open-smart-grid-platform components and running integration tests. [Managed by Job DSL]')
    definition {
        cpsScm {
            scm {
                git {
                    branch('${BRANCH}')
                    remote {
                        credentials('jenkins-ssh-key')
                        github('OSGP/open-smart-grid-platform', 'git', 'github.com')
                    }
                }
            scriptPath('jenkins/pipeline/gxf-nightly-build.jenkinsfile')
            }
        }
    }
    parameters {
        stringParam('BRANCH', 'development', 'Source branch from the open-smart-grid-platform repository to use for the build. Default value: development')
    }
    properties {
        pipelineTriggers {
            triggers {
                cron {
                    spec('H 22 * * *')
                }
            }
        }
    }
}
