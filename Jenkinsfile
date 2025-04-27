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

        stage('Backend Build && Test') {
			steps {
				dir('config-service') {
					sh 'mvn clean install -DskipTests=true'
				}
        		dir('discovery-service') {
					sh 'mvn clean install -DskipTests=true'
        		}
        		dir('gateway-service') {
					sh 'mvn clean install -DskipTests=true'
        		}
        		dir('user-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('order-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('cart-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('Category-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('product-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('coupon-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
        		dir('review-service') {
					sh 'mvn clean install -DskipTests=false'
        		}
    		}
		}

		stage('SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarQube') {
					dir('config-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('discovery-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('gateway-service') {
						sh 'mvn sonar:sonar'
            		}
           			dir('user-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('order-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('cart-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('Category-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('product-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('coupon-service') {
						sh 'mvn sonar:sonar'
            		}
            		dir('review-service') {
						sh 'mvn sonar:sonar'
            		}
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

		stage('Docker Compose Down') {
			steps {
				echo "🛑 Arrêt des anciens conteneurs..."
                sh '''
            		docker-compose -f docker-compose.yml down
            		docker-compose -f docker-compose-db.yml down
        		'''
            }
        }

		stage('Docker Compose Up') {
			steps {
				echo "🚀 Lancement de docker-compose build et up..."
                sh '''
                    docker-compose -f docker-compose-db.yml up -d
                    docker-compose -f docker-compose.yml up -d
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
