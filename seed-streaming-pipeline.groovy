PROJECT_NAME = 'microservices-demo'
ORG = 'venkypuppala'

PROJECT_URL = "https://github.com/venkypuppala/microservices-demo"
GIT_URL = "${PROJECT_URL}.git"

// Read List of github_admins into ArrayList

def ADMINS = []
new File(WORKSPACE + 'github_admins' ).eachLine { line ->
    ADMINS << line
}

pipelineJob(PROJECT_NAME) {
    //
    properties {
        githubProjectUrl(PROJECT_URL)
    }

    parameters {
        // parameter, default value, description
        stringParam('ZONE', 'us-west1-a', 'The Zone to build the test cluster in')
        stringParam('PROJECT_ID', 'venky-cicd', 'The Project to build the test cluster in')
        stringParam('REGION', 'us-west1', 'The Region to build the test cluster in')
        stringParam('sha1' 'master', '')
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
