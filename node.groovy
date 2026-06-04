pipeline {
agent any


tools {
    demo-node 'NodeJS-20'
}

environment {
    DOCKER_REPO = 'vedantwagh/node-app'
    CONTAINER_NAME = 'my-node-container'
}

stages {

    stage('Checkout') {
        steps {
            git branch: 'main',
                url: 'https://github.com/Nikhil-Suryawanshi07/node-app.git'
        }
    }

    stage('Verify Environment') {
        steps {
            sh '''
            echo "Node Version:"
            node -v

            echo "NPM Version:"
            npm -v

            echo "Docker Version:"
            docker --version
            '''
        }
    }

    stage('Install Dependencies') {
        steps {
            sh 'npm install'
        }
    }

    stage('Run Tests') {
        steps {
            sh 'npm test'
        }
    }

    stage('Build Docker Image') {
        steps {
            sh 'docker build -t ${DOCKER_REPO}:${BUILD_NUMBER} .'
        }
    }

    stage('Docker Login') {
        steps {
            withCredentials([
                usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )
            ]) {
                sh '''
                echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                '''
            }
        }
    }

    stage('Push Docker Image') {
        steps {
            sh 'docker push ${DOCKER_REPO}:${BUILD_NUMBER}'
        }
    }

    stage('Deploy Container') {
        steps {
            sh '''
            docker rm -f ${CONTAINER_NAME} || true

            docker run -d \
            --name ${CONTAINER_NAME} \
            -p 3000:3000 \
            ${DOCKER_REPO}:${BUILD_NUMBER}
            '''
        }
    }
}


}
