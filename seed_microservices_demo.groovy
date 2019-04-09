// Change this
PROJECT_NAME = 'microservices-demo'
ORG = 'venkypuppala'

// You don't need to change these
PROJECT_URL = "https://github.com/${ORG}/${PROJECT_NAME}"
GIT_URL = "${PROJECT_URL}.git"

// Read list of github_admins into an ArrayList
def ADMINS = []
new File(WORKSPACE + '/github_admins').eachLine { line ->
    ADMINS << line
}

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
        stringParam('sha1', 'master', '')
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
