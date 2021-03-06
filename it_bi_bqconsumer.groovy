// Change this
PROJECT_NAME = 'BQConsumer'
ORG = 'IT-BI'

// You don't need to change these
PROJECT_URL = "https://panwgithub.paloaltonetworks.local/${ORG}/${PROJECT_NAME}"
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
