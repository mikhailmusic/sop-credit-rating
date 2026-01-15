# Credit Rating System

Распределённая система для оценки кредитоспособности клиентов и обработки кредитных заявок с поддержкой REST, GraphQL, gRPC, асинхронной обработки через RabbitMQ и real-time уведомлений через WebSocket.

## Архитектура системы

![Схема взаимодействия сервисов](docs/architecture.svg)

## Сервисы проекта

| Сервис                       | Описание                                                                          |
| ---------------------------- | --------------------------------------------------------------------------------- |
| **sop-app-contracts**        | Контракты REST API, GraphQL, события RabbitMQ, gRPC (.proto)                      |
| **sop-credit-rating**        | Главный сервис, предоставляющий REST (HATEOAS) и GraphQL API для пользователей.   |
| **sop-grpcclient-calc**       | Слушает события RabbitMQ и вызывает gRPC-server для расчётов.                     |
| **sop-grpcserver-calc**      | Сервис расчёта кредитоспособности и генерации оффера.                             |
| **sop-audit-service**        | Сервис аудита: собирает события (assessment, offer) в CSV и формирует статистику. |
| **sop-notification-service** | Отправляет клиентам уведомления о результатах оценки и генерации оффера.          |

## Порты сервисов

| Сервис                  | Порт  | Назначение          |
| ----------------------- | ----- | ------------------- |
| sop-credit-rating       | 8080  | REST/GraphQL API    |
| sop-audit-service       | 8081  | Actuator/Metrics    |
| sop-grpcclient-calc     | 8082  | Actuator/Metrics    |
| sop-grpcserver-calc     | 8083  | Actuator/Metrics    |
| sop-grpcserver-calc     | 9091  | gRPC                |
| sop-notification-service| 8084  | WebSocket/Actuator  |
| PostgreSQL              | 5432  | База данных         |
| RabbitMQ                | 5672, 15672 | AMQP, Web UI   |
| Zipkin                  | 9411  | Трассировка         |
| Prometheus              | 9090  | Метрики             |
| Grafana                 | 3000  | Дашборды            |

## Быстрый старт

### Требования

- Docker и Docker Compose
- Bash (для запуска скриптов)
- Java 17+ и Maven (встроен в проекты через Maven Wrapper)

### Запуск системы

```bash
# Сборка всех сервисов и Docker-образов
./scripts/build.sh

# Остановка старых контейнеров и запуск всей системы через Docker Compose
./scripts/start.sh

# Остановка всех запущенных контейнеров   
./scripts/stop.sh

# Полная очистка (контейнеры, volumes, Maven артефакты)
./scripts/clean.sh
```


## Ссылки после запуска

- **REST API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **GraphiQL**: http://localhost:8080/graphiql
- **WebSocket Demo**: http://localhost:8084
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Zipkin**: http://localhost:9411
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## Демонстрация функционала

### REST API с HATEOAS

**Swagger UI - документация API клиентов**

![Swagger UI](docs/demo/swagger-ui.png)

**Корневая точка входа REST API с гипермедиа**

![REST Root Endpoint](docs/demo/rest-root.png)

**Гипермедийные ссылки в Postman**

![Hypermedia Links](docs/demo/postman-hateoas.png)

### GraphQL API

**Запрос данных клиента и его заявок**

![GraphQL Query](docs/demo/graphiql-query.png)

**Мутация: создание нового клиента**

![GraphQL Mutation](docs/demo/graphiql-mutation.png)

**Подписка на результаты оценки заявки**

![GraphQL Subscription](docs/demo/graphiql-subscription.png)

### Асинхронная обработка (RabbitMQ)

**Список очередей в RabbitMQ Management**

![RabbitMQ Queues](docs/demo/rabbitmq-queues.png)

**Fanout и Topic Exchange с привязанными очередями**

![RabbitMQ Fanout](docs/demo/rabbitmq-fanout.png)

### Real-time уведомления (WebSocket)

**Интерфейс получения уведомлений**

![WebSocket Notifications](docs/demo/websocket-notifications.png)

### Мониторинг и трассировка

**Дашборд Grafana**

![Grafana Dashboard](docs/demo/grafana-dashboard.png)

**Распределённая трассировка запроса в Zipkin**

![Zipkin Trace](docs/demo/zipkin-trace.png)
