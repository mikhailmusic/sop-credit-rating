pipeline {
  agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
        ansiColor('xterm')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Получение исходного кода из GitHub...'
                checkout scm
            }
        }
        
        stage('Build Contracts') {
            steps {
                echo 'Сборка общих контрактов...'
                script {
                    dir('sop-app-contracts/sop-main-contracts') {
                        sh 'mvn clean install'
                    }
                    dir('sop-app-contracts/sop-grpc-contracts') {
                        sh 'mvn clean install'
                    }
                    dir('sop-app-contracts/sop-event-contracts') {
                        sh 'mvn clean install'
                    }
                }
            }
        }
        
        stage('Build Services') {
            parallel {
                stage('Gateway') {
                    steps {
                        dir('sop-credit-rating') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Audit') {
                    steps {
                        dir('sop-audit-service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('gRPC Client') {
                    steps {
                        dir('sop-grpcclient-calc') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('gRPC Server') {
                    steps {
                        dir('sop-grpcserver-calc') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Notification') {
                    steps {
                        dir('sop-notification-service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }
        
        stage('Cleanup Old Deployment') {
            steps {
                echo 'Cleanup only this compose project'
                script {
                    sh '''
                        docker compose down -v --rmi all --remove-orphans || true
                    '''
                }
            }
        }
        
        
        stage('Build Docker Images') {
            steps {
                echo 'Сборка Docker образов...'
                sh 'docker compose build --parallel'
            }
        }
        
        stage('Deploy Services') {
            steps {
                echo 'Запуск всех сервисов...'
                sh 'docker compose up -d'
            }
        }
        
        stage('Wait for Services') {
            steps {
                echo 'Ожидание готовности сервисов...'
                sleep(time: 45, unit: 'SECONDS')
            }
        }
        
        stage('Health Checks') {
            steps {
                echo 'Проверка здоровья сервисов...'
                script {
                    def services = [
                        'Gateway': 'http://localhost:8080/actuator/health',
                        'Audit': 'http://localhost:8081/actuator/health',
                        'gRPC Client': 'http://localhost:8082/actuator/health',
                        'gRPC Server': 'http://localhost:8083/actuator/health',
                        'Notification': 'http://localhost:8084/actuator/health',
                        'RabbitMQ': 'http://localhost:15672',
                        'Prometheus': 'http://localhost:9090/-/healthy',
                        'Grafana': 'http://localhost:3000/api/health',
                        'Zipkin': 'http://localhost:9411/health'
                    ]
                    
                    def failed = []
                    
                    services.each { name, url ->
                        def maxRetries = 5
                        def success = false
                        
                        for (int i = 0; i < maxRetries; i++) {
                            try {
                                def response = sh(
                                    script: "curl -s -o /dev/null -w '%{http_code}' ${url}",
                                    returnStdout: true
                                ).trim()
                                
                                if (response == '200') {
                                    echo "${name}: UP (${response})"
                                    success = true
                                    break
                                }
                            } catch (Exception e) {
                                // Retry on error
                            }
                            
                            if (i < maxRetries - 1) {
                                sleep(5)
                            }
                        }
                        
                        if (!success) {
                            echo "${name}: DOWN"
                            failed.add(name)
                        }
                    }
                    
                    if (failed.size() > 0) {
                        error("Health check failed for: ${failed.join(', ')}")
                    }
                }
            }
        }
        
    }
    
    post {
        success {
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            echo 'Pipeline completed successfully!'
            echo 'All services are up and running!'
        }
        
        failure {
            echo 'Pipeline failed!'
            echo 'Showing last 100 lines of logs...'
            sh 'docker compose logs --tail=100 || true'
        }
        
        always {
            echo 'Pipeline Completed...'
        }
    }
}
