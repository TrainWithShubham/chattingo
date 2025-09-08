pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        AWS_CREDENTIALS        = credentials('aws-cred')
        FRONTEND_REPO          = credentials('frontend-repo')
        BACKEND_REPO           = credentials('backend-repo')
        IMAGE_TAG              = "${BUILD_NUMBER}"
        AWS_DEFAULT_REGION     = credentials('my-region')
        S3_BUCKET              = credentials('bucket')
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
                    sh 'docker build --no-cache -t ${FRONTEND_REPO}:${IMAGE_TAG} ./frontend'
                    sh 'docker build --no-cache -t ${BACKEND_REPO}:${IMAGE_TAG} ./backend'
                }
            }
        }

        stage('Filesystem Scan') {
            steps {
                script {
                    sh 'trivy fs --format json --output filesystem-scan-report.json .'
                }
            }
        }

        stage('Image Scan') {
            steps {
                script {
                    sh 'trivy image --format json --output frontend-scan-report.json ${FRONTEND_REPO}:${IMAGE_TAG}'
                    sh 'trivy image --format json --output backend-scan-report.json ${BACKEND_REPO}:${IMAGE_TAG}'
                }
            }
        }

        stage('Upload Scans to S3') {
            steps {
                script {
                    def timestamp = new Date().format('yyyy-MM-dd-HH-mm-ss')

                    withCredentials([aws(credentialsId: 'aws-cred')]) {
                        ['filesystem-scan-report.json', 'frontend-scan-report.json', 'backend-scan-report.json'].each { file ->
                            sh """
                                docker run --rm \
                                    -e AWS_ACCESS_KEY_ID=\$AWS_ACCESS_KEY_ID \
                                    -e AWS_SECRET_ACCESS_KEY=\$AWS_SECRET_ACCESS_KEY \
                                    -e AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} \
                                    -v \$(pwd):/workspace \
                                    amazon/aws-cli s3 cp /workspace/${file} s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/${file.replace('.json','')}-${timestamp}.json
                            """
                        }

                        def filesystemUrl = sh(
                            script: """docker run --rm \
                                -e AWS_ACCESS_KEY_ID=\$AWS_ACCESS_KEY_ID \
                                -e AWS_SECRET_ACCESS_KEY=\$AWS_SECRET_ACCESS_KEY \
                                -e AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} \
                                amazon/aws-cli s3 presign s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/filesystem-scan-report-${timestamp}.json --expires-in 300""",
                            returnStdout: true
                        ).trim()

                        def frontendUrl = sh(
                            script: """docker run --rm \
                                -e AWS_ACCESS_KEY_ID=\$AWS_ACCESS_KEY_ID \
                                -e AWS_SECRET_ACCESS_KEY=\$AWS_SECRET_ACCESS_KEY \
                                -e AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} \
                                amazon/aws-cli s3 presign s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/frontend-scan-report-${timestamp}.json --expires-in 300""",
                            returnStdout: true
                        ).trim()

                        def backendUrl = sh(
                            script: """docker run --rm \
                                -e AWS_ACCESS_KEY_ID=\$AWS_ACCESS_KEY_ID \
                                -e AWS_SECRET_ACCESS_KEY=\$AWS_SECRET_ACCESS_KEY \
                                -e AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION} \
                                amazon/aws-cli s3 presign s3://${S3_BUCKET}/jenkins-chattingo-app-scanning/backend-scan-report-${timestamp}.json --expires-in 300""",
                            returnStdout: true
                        ).trim()

                        echo "=== SCAN RESULTS URLS (Valid for 5 minutes) ==="
                        echo "Filesystem Scan: ${filesystemUrl}"
                        echo "Frontend Scan: ${frontendUrl}"
                        echo "Backend Scan: ${backendUrl}"

                        writeFile file: 'scan-urls.txt', text: """Filesystem Scan: ${filesystemUrl}
                        Frontend Scan: ${frontendUrl}
                        Backend Scan: ${backendUrl}
                        Generated: ${new Date()}
                        Expires: 5 minutes"""
                    }
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                        sh 'docker push ${FRONTEND_REPO}:${IMAGE_TAG}'
                        sh 'docker push ${BACKEND_REPO}:${IMAGE_TAG}'
                    }
                }
            }
        }

        stage('Update Compose') {
            steps {
                sh """
                    sed -i 's|image: .*frontend.*|image: ${FRONTEND_REPO}:${IMAGE_TAG}|g' docker-compose.yml
                    sed -i 's|image: .*backend.*|image: ${BACKEND_REPO}:${IMAGE_TAG}|g' docker-compose.yml
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh """
                        echo "updated docker-compose.yml to deployment directory"
                        cp docker-compose.yml /home/ubuntu/mini/chattingo/

                        # go into deployment folder
                        cd /home/ubuntu/mini/chattingo
                        docker compose down
                        docker compose pull
                        docker compose up -d

                        # return back to original workspace
                        cd -
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                // 1. Archive scan files (latest ones only)
                archiveArtifacts artifacts: 'filesystem-scan-report.json,frontend-scan-report.json,backend-scan-report.json,scan-urls.txt', allowEmptyArchive: true

                // 2. Cleanup old scan files in workspace (keep latest only)
                sh '''
                    echo "Cleaning up old scan reports..."
                    for f in filesystem-scan-report.json frontend-scan-report.json backend-scan-report.json scan-urls.txt; do
                    if [ -f "$f" ]; then
                        echo "Keeping latest: $f"
                    fi
                    done

                    # remove any older .json or .txt scan reports
                    find . -maxdepth 1 -type f \\( -name "*scan*.json" -o -name "*scan*.txt" \\) ! -newer scan-urls.txt -exec rm -f {} +
                '''

                // 3. Remove unused Docker images (dangling + old build images)
                sh '''
                    echo "Cleaning up unused Docker images..."
                    docker image prune -af --filter "until=24h" || true

                    # Remove old build images except the current IMAGE_TAG
                    docker images --format "{{.Repository}}:{{.Tag}}" | grep chattingo-app || true
                    docker images --format "{{.Repository}}:{{.Tag}}" | grep chattingo-app | grep -v ":${IMAGE_TAG}" | xargs -r docker rmi -f
                '''
            }
        }
    }
}
