environment {
    IMAGE_NAME = 'my-node-app'
    DOCKER_REPO = 'nikhilsuryawanshi07/node-app'
    CONTAINER_NAME = 'my-node-container'
}

stage('build docker image') {
    steps {
        sh 'docker build -t ${DOCKER_REPO}:${BUILD_NUMBER} .'
    }
}

stage('docker Login') {
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

stage('push docker image') {
    steps {
        sh 'docker push ${DOCKER_REPO}:${BUILD_NUMBER}'
    }
}

stage('deploy to docker container') {
    steps {
        sh '''
        docker rm -f ${CONTAINER_NAME} || true
        docker run -d -p 3000:3000 \
        --name ${CONTAINER_NAME} \
        ${DOCKER_REPO}:${BUILD_NUMBER}
        '''
    }
}