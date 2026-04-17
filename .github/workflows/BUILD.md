# Build process
[Back to GitHub actions documentation](../../.github/workflows/README.md)

## Jobs
* [Build and analyze](#Build)
* [Deploy shared libraries](#Deploy)
* [Build Docker images](#Docker)
* [Run Cucumber tests](#Cucumber)

## Build
Builds, analyzes and stages OSGP artifacts

### Steps
* Checkout
* Set up JDK
* Cache SonarCloud packages
* Set Maven options for cucumber containers
* Build and analyze
* Stage jar/war files

## Deploy
Deploys shared OSGP artifacts as GitHub packages

### Prerequisites
* Build job has run, so that artifacts are available
* Build is triggered by a new release tag

### Steps
* Checkout
* Download artifacts
* Configure Maven for GitHub packages
* Deploy

## Docker
Creates docker images in parallel

### Prerequisites
* Build job has run, so that artifacts are available
* Build is triggered by:
  * a new release tag
  * a push to the development branch
  * a pull request containing one of the following labels:
    * `build_containers`
    * `cucumber_testing`
    * `dependencies`

### Steps
* Checkout
* Download artifacts
* Show files in current context
* Extract version from maven pom
* Extract metadata for docker
* Build and push docker image
* Generate artifact attestation

## Cucumber
* Runs cucumber tests in parallel

### Prerequisites
* Docker job has run, so that container images are available
* Build is triggered by:
    * a new release tag
    * a push to the development branch
    * a pull request containing one of the following labels:
        * `cucumber_testing`
        * `dependencies`

### Steps
* Checkout gitops repository
* Setup k3d cluster
* Deploy platform
* Run cucumber tests
* Wait for jobs to finish
* Run maven cucumber reporting
* Publish action annotations from Cucumber reports 
