pipeline {
  agent {
    docker {
      image 'maven:3-alpine'
      args '-v /Users/xuqiang/Work/mavenRepo:/root/.m2'
    }

  }
  stages {
    stage('compile') {
      steps {
        sh 'mvn clean compile'
      }
    }
    stage('test') {
      steps {
        sh 'mvn test'
      }
    }
    stage('package') {
      steps {
        sh 'mvn package'
      }
    }
  }
}