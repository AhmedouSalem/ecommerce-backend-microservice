pipeline {
	agent any

    tools {
		maven 'Maven_3'
    }

    environment {
		DOCKER_IMAGE_PREFIX = "ecom"
        SERVICE_NAME = "user-service"
        SERVICE_DB_NAME = "user-service-db"
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

        stage('Build & Test') {
			steps {
				echo "🏗️ Build Maven + Tests unitaires..."
                sh 'mvn clean install -DskipTests=false'
            }
        }

        stage('SonarQube Analysis') {
			steps {
				echo "🔎 Analyse SonarQube..."
                withSonarQubeEnv('SonarQube') {
					sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
			steps {
				echo "🐳 Construction de l'image Docker ${DOCKER_IMAGE_PREFIX}-${SERVICE_NAME}:${VERSION}..."
                sh '''
                    echo "Building $SERVICE_NAME..."
                    docker build -t $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION .
                    docker tag $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:$VERSION $DOCKER_IMAGE_PREFIX-$SERVICE_NAME:latest
                '''
            }
        }

        stage('Docker Compose Down') {
            steps {
                echo "🛑 Arrêt de ${SERVICE_NAME} et ${SERVICE_DB_NAME}..."
                dir('..') {
                    sh '''
                        docker-compose -f docker-compose.yml -f docker-compose-db.yml stop $SERVICE_DB_NAME $SERVICE_NAME || true
                        docker-compose -f docker-compose.yml -f docker-compose-db.yml rm -f $SERVICE_DB_NAME $SERVICE_NAME || true
                    '''
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo "🚀 Démarrage de ${SERVICE_NAME} et ${SERVICE_DB_NAME}..."
                dir('..') {
                    sh '''
                        docker compose -f docker-compose.yml -f docker-compose-db.yml up -d $SERVICE_DB_NAME $SERVICE_NAME
                    '''
                }
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