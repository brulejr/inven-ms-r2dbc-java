pipeline {

    agent any

    tools {
        jdk "openjdk-11"
    }

    stages {
        stage ('Checkout') {
            steps {
                checkout scm
                sh 'ls -lat'
            }
        }
        stage("Compile") {
            steps {
                sh "./gradlew clean classes testClasses"
            }
        }
        stage ('Analysis') {
            steps {
                sh './gradlew check'
                junit "**/build/test-results/test/*.xml"
                jacoco(
                    execPattern: 'build/jacoco/jacoco.exec'
                )
            }
        }
        stage ('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage ('Docker') {
            environment {
                DOCKERHUB_CREDENTIALS = credentials('dockerhub')
            }
            steps {
                sh "./gradlew jib"
            }
        }
        stage ('Deploy') {
            steps {
                echo "Will deploy to ${API_TARGET_HOST}"
                sh 'printenv'
            }
        }
    }

}
