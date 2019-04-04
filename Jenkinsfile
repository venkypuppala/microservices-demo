def project = 'venky-cicd'
def  apacheimageTag = "gcr.io/${project}/apache:0.0.1"
def  catalogimageTag = "gcr.io/${project}/catalog:0.0.1"
def  customerimageTag = "gcr.io/${project}/customer:0.0.1"
def  orderimageTag = "gcr.io/${project}/order:0.0.1"

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
    stage('Checkout') {
      steps {
          git(
              url: 'https://github.com/venkypuppala/microservices-demo.git',
              credentialsId: 'github',
              branch: "master"
            )
        }
    }
    stage('Build and Test') {
      steps {
        container('maven') {
          sh """
            mvn clean package
          """
        }
      }
    }
    stage('Code Coverage') {
      steps {
        container('maven') {
          sh """
            mvn sonar:sonar -Dsonar.host.url=http://104.196.201.144:9000
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
    stage('create dev cluster') {
        steps {
            container('gcloud') {
                sh "gcloud container clusters create devcluster --zone us-west1-a"
            }
        }
    }
    stage('Deploy to Dev') {
        steps {
            container('gcloud') {
                sh "gcloud container clusters list"
                sh "gcloud container clusters get-credentials devcluster --zone us-west1-a --project venky-cicd"
            }
            container('kubectl') {
                sh "gcloud container clusters get-credentials devcluster --zone us-west1-a --project venky-cicd"
                sh "kubectl config get-clusters"
                sh "kubectl apply -f microservices.yaml"
            }
        }
    }
  }
}
