@Library('jenkins-shared-library') _

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

        stage('Build Images') {
            steps {
                script {
                    buildImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Scan Filesystem') {
            steps {
                script {
                    scanFilesystem()
                }
            }
        }

        stage('Scan Images') {
            steps {
                script {
                    scanImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Upload Scans to S3') {
            steps {
                script {
                    uploadScansToS3([
                        awsCredentialsId: 'aws-cred',
                        awsRegion: env.AWS_DEFAULT_REGION,
                        s3Bucket: env.S3_BUCKET
                    ])
                }
            }
        }

        stage('Push Images') {
            steps {
                script {
                    pushImages([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG,
                        dockerCredentialsId: 'docker-hub-credentials'
                    ])
                }
            }
        }

        stage('Update Compose') {
            steps {
                script {
                    updateCompose([
                        frontendRepo: env.FRONTEND_REPO,
                        backendRepo: env.BACKEND_REPO,
                        imageTag: env.IMAGE_TAG
                    ])
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    deployApp([
                        deploymentPath: '/home/ubuntu/mini/chattingo/'
                    ])
                }
            }
        }
    }

    post {
        always {
            script {
                cleanupResources([
                    imageTag: env.IMAGE_TAG
                ])
            }
        }
    }
}
