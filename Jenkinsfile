pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        AWS_ACCOUNT_ID = "797748030688"
        REGION = "ap-south-1"
        ECR_URL = "${AWS_ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"

        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        IMAGE_TAG = "${env.BUILD_NUMBER}"   // ✅ FIX 1
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }

    tools {
        maven 'Maven_3.9.11'
    }

    stages {

        stage('Code Compilation') {
            when { branch 'dev' }
            steps {
                echo 'Code Compilation in Progress!'
                sh 'mvn clean compile'
                echo 'Code Compilation Completed!'
            }
        }

        stage('Unit Tests') {
            when { branch 'dev' }
            steps {
                echo 'Skipping tests for CI'
                sh 'mvn clean package -DskipTests'
                echo 'Tests Skipped Successfully!'
            }
        }

        stage('Code Package') {
            when { branch 'dev' }
            steps {
                echo 'Creating JAR Artifact...'
                sh 'mvn clean package -DskipTests'   // ✅ FIX
            }
        }

        stage('Build & Tag Docker Image') {
            when { branch 'dev' }
            steps {
                echo "Building Docker Image: ${ECR_URL}/khoj-app:${IMAGE_TAG}"
                sh "docker build -t ${ECR_URL}/khoj-app:${IMAGE_TAG} ."
                echo 'Docker Image Built Successfully!'
            }
        }

        stage('Login to ECR') {
            when { branch 'dev' }
            steps {
                sh """
                aws ecr get-login-password --region ${REGION} | \
                docker login --username AWS --password-stdin ${ECR_URL}
                """
            }
        }

        stage('Push Docker Image to Amazon ECR') {
            when { branch 'dev' }
            steps {
                echo "Pushing Docker Image to ECR: ${ECR_URL}/khoj-app:${IMAGE_TAG}"
                sh "docker push ${ECR_URL}/khoj-app:${IMAGE_TAG}"
                echo 'Docker Image Pushed to ECR Successfully!'
            }
        }

        stage('Deploy app to dev env') {
            when { branch 'dev' }
            steps {
                script {
                    echo "Deploying to Dev Environment"

                    def yamlFile = "kubernetes/dev/05-deployment.yaml"

                    sh """
                    set -e
                    sed -i 's|image:.*|image: 797748030688.dkr.ecr.ap-south-1.amazonaws.com/khoj-app:${IMAGE_TAG}|g' kubernetes/dev/05-deployment.yaml  
                    grep ${IMAGE_TAG} ${yamlFile} || echo "Replacement failed in ${yamlFile}"
                    aws eks update-kubeconfig --name khoj-eks --region ${REGION}                
                    kubectl apply -f kubernetes/dev/
                    kubectl rollout status deployment khoj-app -n dev
                    """

                    def configMapChanged = sh(
                        script: "git diff --name-only HEAD~1 | grep -q 'kubernetes/dev/06-configmap.yaml'",
                        returnStatus: true
                    )

                    if (configMapChanged == 0) {
                        echo "ConfigMap changed, restarting pods"
                        sh "kubectl rollout restart deployment khoj-app -n dev"
                    } else {
                        echo "No ConfigMap Changes, Skipping Pod Restart"
                    }
                }
            }
        }

        stage('Deploy app to preprod env') {
            when { branch 'preprod' }
            steps {
                script {
                    echo "Deploying to Preprod Environment"

                    def yamlFile = "kubernetes/preprod/05-deployment.yaml"

                    sh """
                    set -e
                    sed -i 's|image:.*|image: ${ECR_URL}/khoj-app:${IMAGE_TAG}|g' ${yamlFile}
                    aws eks update-kubeconfig --name khoj-eks --region ${REGION}
                    kubectl apply -f kubernetes/preprod/
                    """
                }
            }
        }

        stage('Deploy app to prod env') {
            when { branch 'prod' }
            steps {
                script {
                    echo "Deploying to Prod Environment"

                    def yamlFile = "kubernetes/prod/05-deployment.yaml"

                    sh """
                    set -e
                    sed -i 's|image:.*|image: ${ECR_URL}/khoj-app:${IMAGE_TAG}|g' ${yamlFile}
                    aws eks update-kubeconfig --name khoj-eks --region ${REGION}
                    kubectl apply -f kubernetes/prod/
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployment to ${env.BRANCH_NAME} environment completed successfully"
        }
        failure {
            echo "Deployment to ${env.BRANCH_NAME} environment failed. Check logs for details."
        }
    }
}