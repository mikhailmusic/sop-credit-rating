

## Репозитории проекта

| Репозиторий                                                                              | Описание                                                                          |
|------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------|
| **[sop-app-contracts](https://github.com/mikhailmusic/sop-app-contracts)**               | Контракты REST API, GraphQL, события RabbitMQ, gRPC (.proto)                      |
| **[sop-credit-rating](https://github.com/mikhailmusic/sop-credit-rating)**               | Главный сервис, предоставляющий REST (HATEOAS) и GraphQL API для пользователей.   |
| **[sop-grpcclient-calc](https://github.com/mikhailmusic/sop-grpcclient-calc)**               | Слушает события RabbitMQ и вызывает gRPC-server для расчётов.                     |
| **[sop-grpcserver-calc](https://github.com/mikhailmusic/sop-grpcserver-calc)**           | Сервис расчёта кредитоспособности и генерации оффера.                             |
| **[sop-audit-service](https://github.com/mikhailmusic/sop-audit-service)**               | Сервис аудита: собирает события (assessment, offer) в CSV и формирует статистику. |
| **[sop-notification-service](https://github.com/mikhailmusic/sop-notification-service)** | Отправляет клиентам уведомления о результатах оценки и генерации оффера.          |

https://www.jetbrains.com/guide/java/tutorials/hello-world/packaging-the-application/


REST API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
GraphiQL: http://localhost:8080/graphiql
RabbitMQ Console: http://localhost:15672 (guest/guest)
WebSocket Demo: http://localhost:8084
Zipkin: http://localhost:9411
Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin/admin)
Jenkins: http://localhost:8085