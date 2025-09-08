pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        REGISTRY = 'chattingo-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        AWS_DEFAULT_REGION = 'ap-south-1'
        S3_BUCKET = 'chattingo-app'
    }

    stages {
        stage('Git Clone') {
            steps {
                git branch: 'mini-hackathon', url: 'https://github.com/dushyantkumark/chattingo.git'
            }
        }

        stage('Image Build') {
            steps {
                script {
                    sh 'docker build -t ${REGISTRY}/frontend:${IMAGE_TAG} ./frontend'
                    sh 'docker build -t ${REGISTRY}/backend:${IMAGE_TAG} ./backend'
                }
            }
        }

        stage('Filesystem Scan') {
            steps {
                script {
                    sh '''
                        echo "Starting filesystem security scan..." > filesystem-scan-results.txt
                        docker run --rm -v $(pwd):/src securecodewarrior/docker-security-scanner /src >> filesystem-scan-results.txt 2>&1
                        echo "Filesystem scan completed at $(date)" >> filesystem-scan-results.txt
                    '''
                }
            }
        }

        stage('Image Scan') {
            steps {
                script {
                    sh '''
                        echo "Starting Docker image security scan..." > docker-scan-results.txt
                        echo "Scanning frontend image..." >> docker-scan-results.txt
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image ${REGISTRY}/frontend:${IMAGE_TAG} >> docker-scan-results.txt 2>&1
                        echo "Scanning backend image..." >> docker-scan-results.txt
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image ${REGISTRY}/backend:${IMAGE_TAG} >> docker-scan-results.txt 2>&1
                        echo "Docker image scan completed at $(date)" >> docker-scan-results.txt
                    '''
                }
            }
        }

        stage('Upload Scans to S3') {
            steps {
                script {
                    def timestamp = new Date().format('yyyy-MM-dd-HH-mm-ss')

                    // Upload scan results to S3
                    sh "aws s3 cp filesystem-scan-results.txt s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/filesystem-scan-${timestamp}.txt"
                    sh "aws s3 cp docker-scan-results.txt s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/docker-scan-${timestamp}.txt"

                    // Generate 5-minute presigned URLs
                    def filesystemUrl = sh(
                        script: "aws s3 presign s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/filesystem-scan-${timestamp}.txt --expires-in 300",
                        returnStdout: true
                    ).trim()

                    def dockerUrl = sh(
                        script: "aws s3 presign s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/docker-scan-${timestamp}.txt --expires-in 300",
                        returnStdout: true
                    ).trim()

                    echo "=== SCAN RESULTS URLS (Valid for 5 minutes) ==="
                    echo "Filesystem Scan: ${filesystemUrl}"
                    echo "Docker Scan: ${dockerUrl}"

                    // Save URLs for reference
                    writeFile file: 'scan-urls.txt', text: """Filesystem Scan: ${filesystemUrl}
                    Docker Scan: ${dockerUrl}
                    Generated: ${new Date()}
                    Expires: 5 minutes"""
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                        sh 'docker push ${REGISTRY}/frontend:${IMAGE_TAG}'
                        sh 'docker push ${REGISTRY}/backend:${IMAGE_TAG}'
                    }
                }
            }
        }

        stage('Update Compose') {
            steps {
                sh """
                    sed -i 's|image: ${REGISTRY}/frontend:.*|image: ${REGISTRY}/frontend:${IMAGE_TAG}|g' docker-compose.yml
                    sed -i 's|image: ${REGISTRY}/backend:.*|image: ${REGISTRY}/backend:${IMAGE_TAG}|g' docker-compose.yml
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        cd /home/ubuntu/mini/chattingo
                        docker-compose pull
                        docker-compose up -d
                    """
                }
            }
        }
    }
    }

    post {
        always {
            archiveArtifacts artifacts: 'filesystem-scan-results.txt,docker-scan-results.txt,scan-urls.txt', allowEmptyArchive: true
            sh 'docker system prune -f'
        }
    }
}
