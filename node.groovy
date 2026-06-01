pipeline {
    agent any

    tools {
        nodejs 'NodeJS-20'
    }
    environment {
        IMAGE_NAME = 'my-node-app'
        DOCKER_REPO = 'my-docker-repo'
        CONTAINER_NAME = 'my-node-container'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                 url: 'https://github.com/Nikhil-Suryawanshi07/node-app.git'
                 
            }
        }           
        stage('verify Environment') {
            steps {
                sh '''
                echo "Node Version:"
                node -v

                echo "NPM Version:"
                node -v

                echo "Docker Version:"
                docker --version

                '''
            }
        }
        stage('install dependencies'){
            steps {
                sh 'npm install'
            }
        }
        stage('run tests') {
            steps {
                sh 'npm test'
            }
        }
        stage('build docker image') {
            steps {
               sh '''docker build -t { DOCKER_REPO }:${BUILD_NUMBER} .'''
            }
        }
        stage('docker Login') {
            steps{
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]){
                    sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                }
                }
            }
        stage('push docker image') {
            steps {
                sh ''' docker push ${DOCKER_REPO}:${BUILD_NUMBER} '''
            }
        }
        stage('deploy to docker container') {
            steps {
                sh '''
                docker rm -f ${CONTAINER_NAME} || true
                docker run -d -p 3000:3000 --name ${CONTAINER_NAME} ${DOCKER_REPO}:${BUILD_NUMBER}  
                '''
            }
        }
    }
}