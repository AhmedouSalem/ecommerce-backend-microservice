pipeline {
	agent any

    tools {
		maven 'Maven_3'
        nodejs 'Node_23'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
    }

    stages {
		stage('Clone') {
			steps {
				echo "Code récupéré automatiquement si Git configuré"
            }
        }

        stage('Backend Build & Test') {
			steps {
				sh 'mvn clean install -DskipTests=false'
            }
        }

        stage('Frontend Build') {
			steps {
				dir('e-commerce-web') {
					sh '''
                        npm install
                        npm run build -- --configuration=development
                    '''
                }
            }
        }

        stage('Docker Build') {
			steps {
				sh '''
                docker build -t $DOCKER_IMAGE_PREFIX-config-service:$BUILD_NUMBER ./config-service
                docker build -t $DOCKER_IMAGE_PREFIX-discovery-service:$BUILD_NUMBER ./discovery-service
                docker build -t $DOCKER_IMAGE_PREFIX-gateway-service:$BUILD_NUMBER ./gateway-service
                docker build -t $DOCKER_IMAGE_PREFIX-user-service:$BUILD_NUMBER ./user-service
                docker build -t $DOCKER_IMAGE_PREFIX-order-service:$BUILD_NUMBER ./order-service
                docker build -t $DOCKER_IMAGE_PREFIX-cart-service:$BUILD_NUMBER ./cart-service
                docker build -t $DOCKER_IMAGE_PREFIX-category-service:$BUILD_NUMBER ./Category-service
                docker build -t $DOCKER_IMAGE_PREFIX-product-service:$BUILD_NUMBER ./product-service
                docker build -t $DOCKER_IMAGE_PREFIX-coupon-service:$BUILD_NUMBER ./coupon-service
                docker build -t $DOCKER_IMAGE_PREFIX-review-service:$BUILD_NUMBER ./review-service
                docker build -t $DOCKER_IMAGE_PREFIX-frontend:$BUILD_NUMBER ./e-commerce-web
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
