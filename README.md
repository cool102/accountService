```md
# Account Service

Сервис для работы с аккаунтами/балансами (учебный/демо микросервис).
Проект показывает типичную архитектуру backend-сервиса: REST API, слои (controller/service/repository), валидация, ошибки, тестирование.

## Что умеет
- CRUD для аккаунтов 
- Операции с балансом: пополнение/списание/перевод 
- Валидация входных данных
- Единообразные ошибки (HTTP codes + message)

## Технологии
- Java 
- Spring Boot 
- БД: PostgreSQL/H2
- Миграции: Flyway/Liquibase
- Сборка: Maven/Gradle
- Тесты: JUnit 5 + Spring Test 

## Архитектура
- `controller` — REST endpoints
- `service` — бизнес-логика
- `repository` — доступ к данным
- `dto` — вход/выход API

## Запуск
### Локально
```bash
# TODO: Gradle
./gradlew bootRun

# TODO: Maven
mvn spring-boot:run

API (пример)


POST /accounts — создать аккаунт

GET /accounts/{id} — получить аккаунт

POST /accounts/{id}/deposit — пополнить

POST /accounts/{id}/withdraw — списать

POST /transfers — перевод между аккаунтами

curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"name":"Ivan","currency":"RUB"}'

