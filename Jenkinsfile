pipeline {
	agent any

    tools {
		maven 'Maven_3'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        SERVICE_NAME = "frontend"
        //SERVICE_DB_NAME = "mysql-user"
    }

    stages {
		stage('Checkout') {
			steps {
				echo "🔄 Clonage du code source..."
                checkout scm
                script {
					// Afficher infos Git
                    def branch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    def commit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_BRANCH = branch
                    env.GIT_COMMIT = commit
                    env.VERSION = "${commit}"
                    echo "📚 Branche Git : ${branch}"
                    echo "🔖 Commit Git : ${commit}"
                    echo "🏷️ Version du build : ${VERSION}"
                }
            }
        }

        stage('Frontend Build') {
			steps {
				dir("e-commerce-web") {
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
				echo "🐳 Construction de l'image Docker ${DOCKER_IMAGE_PREFIX}-${SERVICE_NAME}:${VERSION}..."
                sh '''
                    echo "Building $SERVICE_NAME..."
                    docker build -t $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION ./$SERVICE_NAME
                    docker tag $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:latest
                '''
            }
        }

        stage('Docker Compose Down') {
            steps {
                echo "🛑 Arrêt de ${SERVICE_NAME}..."
                dir('..') {
                    sh '''
                        docker-compose -f docker-compose.yml stop $SERVICE_NAME || true
                        docker-compose -f docker-compose.yml rm $SERVICE_NAME || true
                    '''
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo "🚀 Démarrage de ${SERVICE_NAME}..."
                sh '''
                    docker-compose -f docker-compose.yml -d $SERVICE_NAME
                '''
            }
        }
    }

    post {
		always {
			echo '🎯 Pipeline terminé (always block).'
        }
        success {
			echo '✅ Succès du pipeline.'
        }
        failure {
			echo '💥 Échec du pipeline.'
        }
    }
}