pipeline {
	agent any

    tools {
		maven 'Maven_3'
        nodejs 'Node_23'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        VERSION = "v1.0.${BUILD_NUMBER}"
    }

    stages {
		stage('Prepare') {
			steps {
				echo "🔄 Code récupéré automatiquement si Git configuré"
            }
        }

        stage('Backend Build') {
			steps {
				echo "🔨 Building backend microservices..."
                sh 'mvn clean package -DskipTests=true'
            }
        }

        stage('Backend Tests') {
			steps {
				echo "🧪 Running backend tests..."
                sh 'mvn test'
            }
        }

        stage('Frontend Build') {
			steps {
				dir('e-commerce-web') {
					echo "🖌️ Building frontend Angular..."
                    sh '''
                        npm install
                        npm run build -- --configuration=development --no-watch --no-progress
                    '''
                }
            }
        }

        stage('Docker Build') {
			steps {
				echo "🐳 Building Docker images version ${VERSION}..."
                sh '''
                    docker build -t $DOCKER_IMAGE_PREFIX-config-service:$VERSION ./config-service
                    docker build -t $DOCKER_IMAGE_PREFIX-discovery-service:$VERSION ./discovery-service
                    docker build -t $DOCKER_IMAGE_PREFIX-gateway-service:$VERSION ./gateway-service
                    docker build -t $DOCKER_IMAGE_PREFIX-user-service:$VERSION ./user-service
                    docker build -t $DOCKER_IMAGE_PREFIX-order-service:$VERSION ./order-service
                    docker build -t $DOCKER_IMAGE_PREFIX-cart-service:$VERSION ./cart-service
                    docker build -t $DOCKER_IMAGE_PREFIX-category-service:$VERSION ./Category-service
                    docker build -t $DOCKER_IMAGE_PREFIX-product-service:$VERSION ./product-service
                    docker build -t $DOCKER_IMAGE_PREFIX-coupon-service:$VERSION ./coupon-service
                    docker build -t $DOCKER_IMAGE_PREFIX-review-service:$VERSION ./review-service
                    docker build -t $DOCKER_IMAGE_PREFIX-frontend:$VERSION ./e-commerce-web
                '''
            }
        }
    }

    post {
		always {
			echo '🎯 Pipeline terminé !'
        }
        failure {
			echo '💥 Échec du pipeline.'
        }
    }
}
