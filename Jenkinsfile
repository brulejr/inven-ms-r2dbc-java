pipeline {

    agent any

    tools {
        jdk "openjdk-11"
    }

    stages {
        stage ('Setup parameters') {
            steps {
                script {
                    properties([
                        parameters([
                            string(defaultValue: '', description: '', name: 'TARGET_HOST', trim: true)
                        ])
                    ])
                }
            }
        }
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
            when {
                expression {
                   return params.TARGET_HOST != ''
                }
            }
            steps {
                echo "Will deploy to ${TARGET_HOST}"
            }
        }
    }

}
