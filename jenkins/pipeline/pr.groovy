@Library("OSGP-jenkins") _
standardPipeline {
    stream = "OSGP"
    repo = "git@github.com:OSGP/Integration-Tests.git"
    mailRecipients = "kevin.smeets@cgi.com,ruud.lemmers@cgi.com,hans.rooden@cgi.com,martijn.sips@cgi.com"
    mavenParameters = "-DskipTestJarWithDependenciesAssembly=false"
    runAutomaticTests = true
}