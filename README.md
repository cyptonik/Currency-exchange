# Currency Exchange

REST API для работы с валютами и обменными курсами. 
Учебный проект, реализованный на Java Servlets.

## Стек

- Jakarta Servlets (Tomcat 10)
- SQLite + HikariCP
- Gradle

## Возможности

- CRUD для валют и обменных курсов
- Конвертация валют по трём стратегиям: прямой курс, обратный курс, кросс-курс через USD

## Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/currencies` | Список всех валют |
| GET | `/currency/{code}` | Валюта по коду |
| POST | `/currencies` | Добавить валюту |
| GET | `/exchangeRates` | Все обменные курсы |
| GET | `/exchangeRate/{pair}` | Курс по паре (например `USDRUB`) |
| POST | `/exchangeRates` | Добавить курс |
| PATCH | `/exchangeRate/{pair}` | Обновить курс |
| GET | `/exchange?from=X&to=Y&amount=Z` | Конвертация |

## Запуск
Перед деплоем нужно один раз создать скрипт, указав путь до своего Tomcat:

```bash
echo "sudo -A cp ./build/libs/app.war ${Путь_до_tomcat}/webapps/tennis-scoreboard.war" > src/deploy.sh
```

Затем из корня проекта:

```bash
./gradlew app:deployWar
```

Приложение будет доступно на http://localhost:8080/tennis-scoreboard.
