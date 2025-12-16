## Репозитории проекта

| Проект                       | Описание                                                                          |
| ---------------------------- | --------------------------------------------------------------------------------- |
| **sop-app-contracts**        | Контракты REST API, GraphQL, события RabbitMQ, gRPC (.proto)                      |
| **sop-credit-rating**        | Главный сервис, предоставляющий REST (HATEOAS) и GraphQL API для пользователей.   |
| **sop-grpcclient-cal**       | Слушает события RabbitMQ и вызывает gRPC-server для расчётов.                     |
| **sop-grpcserver-calc**      | Сервис расчёта кредитоспособности и генерации оффера.                             |
| **sop-audit-service**        | Сервис аудита: собирает события (assessment, offer) в CSV и формирует статистику. |
| **sop-notification-service** | Отправляет клиентам уведомления о результатах оценки и генерации оффера.          |


## Ссылки после запуска

- **REST API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **GraphiQL**: http://localhost:8080/graphiql
- **WebSocket Demo**: http://localhost:8084
- **RabbitMQ Console**: http://localhost:15672 (guest/guest)
- **Zipkin**: http://localhost:9411
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Jenkins**: http://localhost:8085
