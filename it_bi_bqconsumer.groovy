ORG = 'IT-BI'
PROJECT_NAME = 'BQConsumer'
PROJECT_URL = "https://panwgithub.paloaltonetworks.local/IT-BI/${PROJECT_NAME}"
GIT_URL = "${PROJECT_URL}.git"

// Read list of github_admins into an ArrayList
def ADMINS = ['venkypuppala']

// Creates a pipelineJob for BQProducer
pipelineJob(PROJECT_NAME) {

    // You don't need to change this
    properties {
        githubProjectUrl(PROJECT_URL)
    }

    parameters {
        // parameter, default value, description
        stringParam('ZONE', 'us-west1-a', 'The Zone to build the test cluster in')
        stringParam('PROJECT_ID', 'itd-aia-demo-ps2', 'The Project to build the test cluster in')
        stringParam('REGION', 'us-west1', 'The Region to build the test cluster in')
        stringParam('sha1', 'master', 'The default branch to trigger when not a pull request')
        stringParam('DEPLOY_TO_ENV', 'dev', 'Which environment do i deploy to')
    }

    Triggers {
        githubPullRequest {
            useGitHubHooks()
            orgWhitelist(ORG)
            admins(ADMINS)
        }
    }
    definition {
        cpsScm {
            scm {
                branch('${sha1}')
                remote{
                    name {
                    refspec('+refs/pull/${ghprbPullId}/*:refs/remotes/origin/pr/${ghprbPullId}/*')
                    url(GIT_URL)
                    }
                }
            }
        }
    }
}
