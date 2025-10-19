# Production Plan

## ✅ Реализовано

    3 сервиса:   `authorization-server`, `service-a`, `service-b`.
    Аутентификация:   через JWT (client_credentials), без хранения токенов в БД.
    Spring Security:   Java Config с OAuth2 Resource Server и Client.
    Взаимодействие между сервисами:   machine-to-machine через WebClient.
    Кэширование токенов:   Caffeine Cache для повторных обращений.
    Конфигурация окружения:   централизована через `application.yml`.
    Логирование:   через Lombok `@Slf4j`.
    Сборка:   Gradle (Java 21, мультимодульная структура).

## ⚙️ Что необходимо переделать перед продом

1.   HTTPS: заменить все `http://localhost` на боевые HTTPS-домены.
2.   Secrets: вынести `client-secret` в переменные окружения или Vault.
3.   CORS / Security: уточнить разрешённые источники и политики.
4.   Rate Limiting: добавить защиту от избыточных запросов.
5.   CI/CD:   настроить пайплайн сборки и деплоя (GitHub Actions / Jenkins).
6.   Error Handling: унифицировать ответы об ошибках для всех API.

