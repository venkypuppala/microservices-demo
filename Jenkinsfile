def  SONAR_URL = "http://104.196.201.144:9000"
def  VERSION = "0.0.env.${ghprbPullId}"
def  apacheimageTag = "gcr.io/env.${PROJECT_ID}/apache:${VERSION}"
def  catalogimageTag = "gcr.io/env.${PROJECT_ID}/catalog:${VERSION}"
def  customerimageTag = "gcr.io/env.${PROJECT_ID}/customer:${VERSION}"
def  orderimageTag = "gcr.io/env.${PROJECT_ID}/order:${VERSION}"

pipeline {
  agent {
    kubernetes {
      label 'sample-app'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  # Use service account that can deploy to all namespaces
  serviceAccountName: cd-jenkins
  containers:
  - name: maven
    image: maven:3.3-jdk-8
    command:
    - cat
    tty: true
  - name: gcloud
    image: gcr.io/cloud-builders/gcloud
    command:
    - cat
    tty: true
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
"""
}
  }
  stages {
    stage('Setup') {
      // checkout code from scm i.e. commits related to the PR
      checkout scm
    }
    stage('Build and Test') {
      steps {
        container('maven') {
          sh """
            echo "${ghprbPullId}"
            mvn clean package
          """
        }
      }
    }
    stage('Code Coverage') {
      steps {
        container('maven') {
          sh """
            mvn sonar:sonar -Dsonar.host.url=${SONAR_URL}
          """
        }
      }
    }
    stage('publish coverage reports') {
      steps {
          jacoco()
      }
    }
    stage('Build Images') {
      steps {
          container('gcloud') {
              //Build apache image
              sh "PYTHONUNBUFFERED=1 gcloud builds submit -t ${apacheimageTag} ./apache"
              //Build catalog image
              sh "PYTHONUNBUFFERED=1 gcloud builds submit -t ${catalogimageTag} ./microservice-kubernetes-demo-catalog"
              //Build customer image
              sh "PYTHONUNBUFFERED=1 gcloud builds submit -t ${customerimageTag} ./microservice-kubernetes-demo-customer"
              //Build order image
              sh "PYTHONUNBUFFERED=1 gcloud builds submit -t ${orderimageTag} ./microservice-kubernetes-demo-order"
          }
      }
    }
    stage('Deploy to Dev') {
        steps {
          container('gcloud') {
              sh "gcloud container clusters create devcluster1 --zone env.${ZONE}"
              sh "gcloud container clusters list"
          }
          container('kubectl') {
              sh "gcloud container clusters get-credentials devcluster1 --zone env.${ZONE} --project env.${PROJECT_ID}"
              sh "kubectl config get-clusters"
              sh "kubectl apply -f microservices.yaml"
        }
      }
    }
  }
}
