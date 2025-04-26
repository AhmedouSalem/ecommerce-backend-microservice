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
				dir('config-service') {
					sh 'mvn clean package -DskipTests=true'
				}
        		dir('discovery-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('gateway-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('user-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('order-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('cart-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('Category-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('product-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('coupon-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
        		dir('review-service') {
					sh 'mvn clean package -DskipTests=true'
        		}
    		}
		}


        stage('Backend Tests') {
			steps {
				dir('config-service') {
					sh 'mvn test'
        		}
        		dir('discovery-service') {
					sh 'mvn test'
        		}
        		dir('gateway-service') {
					sh 'mvn test'
        		}
        		dir('user-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('order-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('cart-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('Category-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('product-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('coupon-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        		}
        		dir('review-service') {
					sh 'mvn test -Dspring.profiles.active=test'
        			}
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
            		echo "Building config-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-config-service:$VERSION ./config-service

            		echo "Building discovery-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-discovery-service:$VERSION ./discovery-service

            		echo "Building gateway-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-gateway-service:$VERSION ./gateway-service

            		echo "Building user-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-user-service:$VERSION ./user-service

            		echo "Building order-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-order-service:$VERSION ./order-service

            		echo "Building cart-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-cart-service:$VERSION ./cart-service

            		echo "Building category-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-category-service:$VERSION ./Category-service

            		echo "Building product-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-product-service:$VERSION ./product-service

            		echo "Building coupon-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-coupon-service:$VERSION ./coupon-service

            		echo "Building review-service..."
            		docker build -t $DOCKER_IMAGE_PREFIX-review-service:$VERSION ./review-service

            		echo "Building frontend (Angular)..."
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
