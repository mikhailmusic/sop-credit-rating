FROM jenkins/jenkins:lts-jdk21

USER root

# Установка Docker CLI
RUN apt-get update && \
    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg \
        lsb-release && \
    install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc && \
    chmod a+r /etc/apt/keyrings/docker.asc && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli docker-buildx-plugin docker-compose-plugin && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Установка Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Проверка версий
RUN docker --version && \
    docker compose version && \
    mvn --version

# Добавление jenkins в группу docker
RUN groupadd -f docker && usermod -aG docker jenkins

USER jenkins

# Установка Jenkins плагинов
RUN jenkins-plugin-cli --plugins \
  workflow-aggregator:latest \
  git:latest \
  github:latest \
  generic-webhook-trigger:latest \
  docker-workflow:latest \
  pipeline-stage-view:latest \
  ansicolor:latest \
  timestamper:latest

# Создание init скрипта для настройки безопасности
RUN mkdir -p /usr/share/jenkins/ref/init.groovy.d
RUN echo 'import jenkins.model.*\n\
import hudson.security.*\n\
\n\
def instance = Jenkins.getInstance()\n\
\n\
def hudsonRealm = new HudsonPrivateSecurityRealm(false)\n\
hudsonRealm.createAccount("admin", "admin")\n\
instance.setSecurityRealm(hudsonRealm)\n\
\n\
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()\n\
strategy.setAllowAnonymousRead(false)\n\
instance.setAuthorizationStrategy(strategy)\n\
\n\
instance.save()' > /usr/share/jenkins/ref/init.groovy.d/01-security.groovy

# Пропуск setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"