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
```bash
./gradlew app:deployWar
```

Только нужно создать в src/ файл deploy.sh и там написать:
```bash
sudo -A cp ./build/libs/app.war ${Твой_путь_до_tomcat}/webapps/${любое_название}.war
```

Приложение будет доступно на `http://localhost:8080/{любое_название}`.
