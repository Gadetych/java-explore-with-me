# Explore With Me

## Идея

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком
планировании поиск информации и переговоры. Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот
момент друзья, как всех пригласить и где собраться.

## Описание проекта

Этот проект - это Афиша, в которой можно предложить какое-либо событие от выставки до похода в кино и собрать компанию
для участия в нём.
Проект имеет микросервисную архитектуру.  
Основной сервис, API которого разделено на три части:

- публичная;
- закрытая;
- административная.

Сервис статистики. Его функционал содержит:

- запись информации о том, что был обработан запрос к эндпоинту API;
- предоставление статистики за выбранные даты по выбранному эндпоинту.

К основной функциональности добавил возможность оставлять комментарии к событиям и модерировать их.

### Жизненный цикл события

1. Создание.
2. Ожидание публикации. В статус ожидания публикации событие переходит сразу после создания.
3. Публикация. В это состояние событие переводит администратор.
4. Отмена публикации. В это состояние событие переходит в двух случаях. Первый — если администратор решил, что его
   нельзя публиковать. Второй — когда инициатор события решил отменить его на этапе ожидания публикации.

## Стэк

- Java 21
- JavaScript
- Maven
- Spring Boot
- PostgreSQL
- H2
- JPA
- Hibernate
- Swagger
- Junit5
- Mockito
- Postman
- Docker

## Запуск проекта

1. Скопируйте репозиторий: `git clone https://github.com/Gadetych/java-explore-with-me`
2. Соберите проект в jar-файл: `mvn package`
3. Запустите контейнеры: `docker-compose up`
4. Основной сервис будет доступен по ссылке: <https://localhost:4265/main_service_postgres_db>

## API

### API Основного сервиса

_____________________________________
[Swagger-спецификация API (файл для импорта)](swagger/ewm-main-service-spec.json)

#### Admin: Категории

##### POST /admin/categories

Parameters

| Name | Located in | Description                  | Required | Schema         | Default value |
|------|------------|------------------------------|----------|----------------|---------------|
| body | body       | данные добавляемой категории | Yes      | NewCategoryDto |               |

Responses

| Code | Description                  |
|------|------------------------------|
| 201  | Категория добавлена          |
| 400  | Запрос составлен некорректно |
| 409  | Нарушение целостности данных |

##### DELETE /admin/categories/{catId}

Parameters

| Name  | Located in | Description  | Required | Schema  | Default value |
|-------|------------|--------------|----------|---------|---------------|
| catId | path       | id категории | Yes      | integer |               |

Responses

| Code | Description                                |
|------|--------------------------------------------|
| 204  | Категория удалена                          |
| 404  | Категория не найдена или недоступна        |
| 409  | Существуют события, связанные с категорией |

##### PATCH /admin/categories/{catId}

Parameters

| Name  | Located in | Description                    | Required | Schema      | Default value |
|-------|------------|--------------------------------|----------|-------------|---------------|
| catId | path       | id категории                   | Yes      | integer     |               |
| body  | body       | Данные категории для изменения | Yes      | CategoryDto |               |

Responses

| Code | Description                         |
|------|-------------------------------------|
| 200  | Данные категории изменены           |
| 404  | Категория не найдена или недоступна |
| 409  | Нарушение целостности данных        |

---

#### Admin: Подборки событий

##### POST /admin/compilations

Parameters

| Name | Located in | Description           | Required | Schema            | Default value |
|------|------------|-----------------------|----------|-------------------|---------------|
| body | body       | данные новой подборки | Yes      | NewCompilationDto |               |

Responses

| Code | Description                  |
|------|------------------------------|
| 201  | Подборка добавлена           |
| 400  | Запрос составлен некорректно |
| 409  | Нарушение целостности данных |

##### DELETE /admin/compilations/{compId}

Parameters

| Name   | Located in | Description | Required | Schema  | Default value |
|--------|------------|-------------|----------|---------|---------------|
| compId | path       | id подборки | Yes      | integer |               |

Responses

| Code | Description                        |
|------|------------------------------------|
| 204  | Подборка удалена                   |
| 404  | Подборка не найдена или недоступна |

##### PATCH /admin/compilations/{compId}

Parameters

| Name   | Located in | Description                    | Required | Schema                   | Default value |
|--------|------------|--------------------------------|----------|--------------------------|---------------|
| compId | path       | id подборки                    | Yes      | integer                  |               |
| body   | body       | данные для обновления подборки | Yes      | UpdateCompilationRequest |               |

Responses

| Code | Description                        |
|------|------------------------------------|
| 200  | Подборка обновлена                 |
| 404  | Подборка не найдена или недоступна |

---

#### Admin: События

##### GET /admin/events

Parameters

| Name       | Located in | Description                     | Required | Schema         | Default value |
|------------|------------|---------------------------------|----------|----------------|---------------|
| users      | query      | список id пользователей         | No       | array[integer] |               |
| states     | query      | список состояний событий        | No       | array[string]  |               |
| categories | query      | список id категорий             | No       | array[integer] |               |
| rangeStart | query      | дата начала диапазона           | No       | string         |               |
| rangeEnd   | query      | дата конца диапазона            | No       | string         |               |
| from       | query      | количество пропускаемых событий | No       | integer        | 0             |
| size       | query      | количество событий в наборе     | No       | integer        | 10            |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | События найдены              |
| 400  | Запрос составлен некорректно |

##### PATCH /admin/events/{eventId}

Parameters

| Name    | Located in | Description          | Required | Schema                  | Default value |
|---------|------------|----------------------|----------|-------------------------|---------------|
| eventId | path       | id события           | Yes      | integer                 |               |
| body    | body       | Данные для изменения | Yes      | UpdateEventAdminRequest |               |

Responses

| Code | Description                                      |
|------|--------------------------------------------------|
| 200  | Событие отредактировано                          |
| 404  | Событие не найдено или недоступно                |
| 409  | Событие не удовлетворяет правилам редактирования |

---

#### Admin: Пользователи

##### GET /admin/users

Parameters
| Name | Located in | Description | Required | Schema | Default value |
| ---- | ---------- | ----------- | -------- | ---- | ---- |
| ids | query | id пользователей | No | array[integer] | |
| from | query | количество пропускаемых элементов | No | integer | 0 |
| size | query | количество элементов в наборе | No | integer | 10 |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | Пользователи найдены         |
| 400  | Запрос составлен некорректно |

##### POST /admin/users

Parameters

| Name | Located in | Description                      | Required | Schema         | Default value |
|------|------------|----------------------------------|----------|----------------|---------------|
| body | body       | Данные добавляемого пользователя | Yes      | NewUserRequest |               |

Responses

| Code | Description                  |
|------|------------------------------|
| 201  | Пользователь зарегистрирован |
| 400  | Запрос составлен некорректно |
| 409  | Нарушение целостности данных |

##### DELETE /admin/users/{userId}

Parameters

| Name   | Located in | Description     | Required | Schema  | Default value |
|--------|------------|-----------------|----------|---------|---------------|
| userId | path       | id пользователя | Yes      | integer |               |

Responses

| Code | Description                           |
|------|---------------------------------------|
| 204  | Пользователь удален                   |
| 404  | Пользователь не найден или недоступен |

---

#### Public: Категории

##### GET /categories

Parameters

| Name | Located in | Description                       | Required | Schema  | Default value |
|------|------------|-----------------------------------|----------|---------|---------------|
| from | query      | количество пропускаемых категорий | No       | integer | 0             |
| size | query      | количество категорий в наборе     | No       | integer | 10            |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | Категории найдены            |
| 400  | Запрос составлен некорректно |

##### GET /categories/{catId}

Parameters

| Name  | Located in | Description  | Required | Schema  | Default value |
|-------|------------|--------------|----------|---------|---------------|
| catId | path       | id категории | Yes      | integer |               |

Responses

| Code | Description                         |
|------|-------------------------------------|
| 200  | Категория найдена                   |
| 400  | Запрос составлен некорректно        |
| 404  | Категория не найдена или недоступна |

---

#### Public: События

##### GET /events

Parameters

| Name          | Located in | Description                     | Required | Schema                     | Default value |
|---------------|------------|---------------------------------|----------|----------------------------|---------------|
| text          | query      | текст для поиска                | No       | string                     |               |
| categories    | query      | список id категорий             | No       | array[integer]             |               |
| paid          | query      | платное/бесплатное              | No       | boolean                    |               |
| rangeStart    | query      | дата начала диапазона           | No       | string                     |               |
| rangeEnd      | query      | дата конца диапазона            | No       | string                     |               |
| onlyAvailable | query      | только доступные                | No       | boolean                    | false         |
| sort          | query      | сортировка                      | No       | string (EVENT_DATE, VIEWS) |               |
| from          | query      | количество пропускаемых событий | No       | integer                    | 0             |
| size          | query      | количество событий в наборе     | No       | integer                    | 10            |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | События найдены              |
| 400  | Запрос составлен некорректно |

##### GET /events/{id}

Parameters

| Name | Located in | Description | Required | Schema  | Default value |
|------|------------|-------------|----------|---------|---------------|
| id   | path       | id события  | Yes      | integer |               |

Responses

| Code | Description                       |
|------|-----------------------------------|
| 200  | Событие найдено                   |
| 400  | Запрос составлен некорректно      |
| 404  | Событие не найдено или недоступно |

---

#### Private: События

##### GET /users/{userId}/events

Parameters

| Name   | Located in | Description                     | Required | Schema  | Default value |
|--------|------------|---------------------------------|----------|---------|---------------|
| userId | path       | id пользователя                 | Yes      | integer |               |
| from   | query      | количество пропускаемых событий | No       | integer | 0             |
| size   | query      | количество событий в наборе     | No       | integer | 10            |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | События найдены              |
| 400  | Запрос составлен некорректно |

##### POST /users/{userId}/events

Parameters
| Name | Located in | Description | Required | Schema | Default value |
| ---- | ---------- | ----------- | -------- | ---- | ---- |
| userId | path | id пользователя | Yes | integer | |
| body | body | данные добавляемого события | Yes | NewEventDto | |

Responses

| Code | Description                                |
|------|--------------------------------------------|
| 201  | Событие добавлено                          |
| 400  | Запрос составлен некорректно               |
| 409  | Событие не удовлетворяет правилам создания |

##### GET /users/{userId}/events/{eventId}

Parameters

| Name    | Located in | Description     | Required | Schema  | Default value |
|---------|------------|-----------------|----------|---------|---------------|
| userId  | path       | id пользователя | Yes      | integer |               |
| eventId | path       | id события      | Yes      | integer |               |

Responses

| Code | Description                       |
|------|-----------------------------------|
| 200  | Событие найдено                   |
| 400  | Запрос составлен некорректно      |
| 404  | Событие не найдено или недоступно |

##### PATCH /users/{userId}/events/{eventId}

Parameters

| Name    | Located in | Description          | Required | Schema                 | Default value |
|---------|------------|----------------------|----------|------------------------|---------------|
| userId  | path       | id пользователя      | Yes      | integer                |               |
| eventId | path       | id события           | Yes      | integer                |               |
| body    | body       | Новые данные события | Yes      | UpdateEventUserRequest |               |

Responses

| Code | Description                                      |
|------|--------------------------------------------------|
| 200  | Событие обновлено                                |
| 400  | Запрос составлен некорректно                     |
| 404  | Событие не найдено или недоступно                |
| 409  | Событие не удовлетворяет правилам редактирования |

---

#### Private: Запросы на участие

##### GET /users/{userId}/requests

Parameters

| Name   | Located in | Description     | Required | Schema  | Default value |
|--------|------------|-----------------|----------|---------|---------------|
| userId | path       | id пользователя | Yes      | integer |               |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | Найдены запросы на участие   |
| 400  | Запрос составлен некорректно |
| 404  | Пользователь не найден       |

##### POST /users/{userId}/requests

Parameters

| Name    | Located in | Description     | Required | Schema  | Default value |
|---------|------------|-----------------|----------|---------|---------------|
| userId  | path       | id пользователя | Yes      | integer |               |
| eventId | query      | id события      | Yes      | integer |               |

Responses

| Code | Description                       |
|------|-----------------------------------|
| 201  | Заявка создана                    |
| 400  | Запрос составлен некорректно      |
| 404  | Событие не найдено или недоступно |
| 409  | Нарушение целостности данных      |

##### PATCH /users/{userId}/requests/{requestId}/cancel

Parameters

| Name      | Located in | Description     | Required | Schema  | Default value |
|-----------|------------|-----------------|----------|---------|---------------|
| userId    | path       | id пользователя | Yes      | integer |               |
| requestId | path       | id запроса      | Yes      | integer |               |

Responses

| Code | Description                     |
|------|---------------------------------|
| 200  | Заявка отменена                 |
| 404  | Запрос не найден или недоступен |

---

#### API Сервиса статистики

[Swagger-спецификация API (файл для импорта)](swagger/ewm-stats-service-spec.json)

##### `POST /hit`

Parameters

| Name | Located in | Description    | Required | Schema      | Default value |
|------|------------|----------------|----------|-------------|---------------|
| body | body       | данные запроса | Yes      | EndpointHit |               |

Responses

| Code | Description          |
|------|----------------------|
| 201  | Информация сохранена |

##### `GET /stats`

Parameters

| Name   | Located in | Description                           | Required | Schema        | Default value |
|--------|------------|---------------------------------------|----------|---------------|---------------|
| start  | query      | Дата и время начала диапазона         | Yes      | string        |               |
| end    | query      | Дата и время конца диапазона          | Yes      | string        |               |
| uris   | query      | Список URI для статистики             | No       | array[string] |               |
| unique | query      | Учитывать только уникальные посещения | No       | boolean       | false         |

Responses

| Code | Description        |
|------|--------------------|
| 200  | Статистика собрана |

---

#### API Фича Комментарии

[Swagger-спецификация API (файл для импорта)](swagger/ewm-feature-comments-spec.json)

#### Private: комментарии

##### `POST /users/{userId}/comments`

Parameters

| Name    | Located in | Description              | Required | Schema        | Default value |
|---------|------------|--------------------------|----------|---------------|---------------|
| userId  | path       | id текущего пользователя | Yes      | integer       |               |
| eventId | query      | id события               | Yes      | integer       |               |
| body    | body       | данные запроса           | Yes      | NewCommentDto |               |

Responses

| Code | Description                       |
|------|-----------------------------------|
| 201  | Комментарий создан                |
| 400  | Запрос составлен некорректно      |
| 404  | Событие не найдено или недоступно |
| 409  | Нарушение целостности данных      |

##### `PATCH /users/{userId}/comments/{comId}`

Parameters

| Name   | Located in | Description              | Required | Schema     | Default value |
|--------|------------|--------------------------|----------|------------|---------------|
| userId | path       | id текущего пользователя | Yes      | integer    |               |
| comId  | path       | id комментария           | Yes      | integer    |               |
| body   | body       | данные запроса           | Yes      | CommentDto |               |

Responses

| Code | Description                                  |
|------|----------------------------------------------|
| 200  | Комментарий обновлен                         |
| 400  | Запрос составлен некорректно                 |
| 404  | Комментарий не найден или недоступен         |
| 403  | Пользователь должен быть автором комментария |

##### `DELETE /users/{userId}/comments/{comId}`

Parameters

| Name   | Located in | Description              | Required | Schema  | Default value |
|--------|------------|--------------------------|----------|---------|---------------|
| userId | path       | id текущего пользователя | Yes      | integer |               |
| comId  | path       | id комментария           | Yes      | integer |               |

Responses

| Code | Description                                  |
|------|----------------------------------------------|
| 204  | Комментарий удален                           |
| 400  | Запрос составлен некорректно                 |
| 403  | Пользователь должен быть автором комментария |

---

#### Admin: комментарии

##### `DELETE /admin/comments/{comId}`

Parameters

| Name  | Located in | Description    | Required | Schema  | Default value |
|-------|------------|----------------|----------|---------|---------------|
| comId | path       | id комментария | Yes      | integer |               |

Responses

| Code | Description                  |
|------|------------------------------|
| 204  | Комментарий удален           |
| 400  | Запрос составлен некорректно |

---

#### Public: комментарии

##### `GET /comments`

Parameters

| Name    | Located in | Description                       | Required | Schema  | Default value |
|---------|------------|-----------------------------------|----------|---------|---------------|
| eventId | query      | id события                        | Yes      | integer |               |
| from    | query      | количество пропускаемых элементов | No       | integer | 0             |
| size    | query      | количество элементов в наборе     | No       | integer | 10            |

Responses

| Code | Description                  |
|------|------------------------------|
| 200  | Получен список комментариев  |
| 400  | Запрос составлен некорректно |