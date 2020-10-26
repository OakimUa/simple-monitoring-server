# Simple monitoring server
a sample

To build:
```
./gradlew bootJar
```
Win:
```
gradlew.bat bootJar
```


To Run:
```
java -jar build/libs/simple-monitoring-server-0.0.1-SNAPSHOT.jar
```

## Class description:

`class SimpleMonitoringServerApplication` - Класс приложения Spring Boot

`fun main(args: Array<String>)` - Точка входа

`interface DataRepository` - Пример интерфейса репозиториз данных

`enum class Sensor` - Перечисление сенсоров

`class SensorException` - Базируясь на разных типах исключений, можно управлять кодом ответа сервера для оповещения клиента об ошибке

`class InMemoryDataRepository` - Пример реализации репозитория с хранением данных в памяти сервера

`class DataService` - Сервис - содержит бизнес-логику для работы с датчиками

`data class SampleDTO` - Простейший data transfer object для обновления данных

`class HardwareController` - Контроллер для обновления данных датчиков, когда устройство инициирует сеанс связи (PUSH)

`class WebClientController` - Контроллер для пользовательского интерфейса

## Usage:

Чтобы отправить данные, пользуйте любой REST клиент или command line

Температура:
```
curl -X PUT -H "Content-Type: application/json" -d '{"data":1}' http://localhost:8080/api/hardware/temperature
```

Давление:
```
curl -X PUT -H "Content-Type: application/json" -d '{"data":1}' http://localhost:8080/api/hardware/pressure
```

Данные всех датчиков как один JSON объект Доступно в браузере по http://localhost:8080/api/webclient/sensor

или command line:
```
curl http://localhost:8080/api/webclient/sensor
```