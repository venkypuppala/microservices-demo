// Change this
PROJECT_NAME = 'microservices-demo'
ORG = 'venkypuppala'

// You don't need to change these
PROJECT_URL = "https://github.com/${ORG}/${PROJECT_NAME}"
GIT_URL = "${PROJECT_URL}.git"

// Read list of github_admins into an ArrayList
def ADMINS = ['venkypuppala','vpuppala']

pipelineJob(PROJECT_NAME) {

    // You don't need to change this
    properties {
        githubProjectUrl(PROJECT_URL)
    }

    // Change these for your pipelines requirements
    parameters {
        // parameter, default value, description
        stringParam('ZONE', 'us-west1-a', 'The zone to build the test cluster in')
        stringParam('PROJECT_ID', 'venky-cicd', 'The project to build the test cluster in')
        stringParam('REGION', 'us-west1', 'The region to build the test cluster in')
        stringParam('sha1', 'master', 'I clone which branch')
        stringParam('ENV', 'dev', 'I deploy where')
    }

    // You don't need to change this
    triggers {
        githubPullRequest {
            useGitHubHooks()
            orgWhitelist(ORG)
            admins(ADMINS)
        }
    }

    // You don't need to change this
    definition {
        cpsScm {
            scm {
                git {
                    branch('${sha1}')
                    remote {
                        name('origin')
                        refspec('+refs/pull/${ghprbPullId}/*:refs/remotes/origin/pr/${ghprbPullId}/*')
                        url(GIT_URL)
                    }
                    extensions {
                      wipeOutWorkspace()
                    }
                }
            }
        }
    }
}













def label = "mypod-${UUID.randomUUID().toString()}"
def SONAR_URL = ""
podTemplate(
  label: label, 
  containers: [
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'golang', image: 'golang:1.8.0', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)],
  volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]) {

    node(label) {
        stage ('Build Project') {
            container('maven') {
                sh """
                mvn clean install -DskipTests 
                """
            }
        }
        stage ('Run Unit Tests') {
            container('maven') {
                sh """
                mvn test
                """
            }
        }
        stage ('Run static Analysis') {
            container('maven') {
                sh """
                mvn sonar:sonar -Dsonar.host.url=${SONAR_URL}
                """
            }
        }
        stage ('Run jacoco') {
            container('maven') {
                jacoco()
            }
        }

    }
}
