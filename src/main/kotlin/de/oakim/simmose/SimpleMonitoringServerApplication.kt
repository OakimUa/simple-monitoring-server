package de.oakim.simmose

import de.oakim.simmose.Sensor.PRESSURE
import de.oakim.simmose.Sensor.TEMPERATURE
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

/*
 * Класс приложения Spring Boot
 */
@SpringBootApplication
class SimpleMonitoringServerApplication

/*
 * Точка входа
 */
fun main(args: Array<String>) {
    runApplication<SimpleMonitoringServerApplication>(*args)
}

/*
 * Пример интерфейса репозиториз данных
 */
interface DataRepository {
    fun updateTemperature(newValue: Int)
    fun retrieveTemperature(): Int
    fun updatePressure(newValue: Int)
    fun retrievePressure(): Int
}

/*
 * Перечисление сенсоров
 */
enum class Sensor {
    TEMPERATURE, PRESSURE
}

/*
 * Базируясь на разных типах исключений, можно управлять кодом ответа сервера для оповещения клиента об ошибке
 */
class SensorException(sensor: Sensor, message: String) : Exception("[$sensor] $message")

/*
 * Пример реализации репозитория с хранением данных в памяти сервера
 */
@Repository
class InMemoryDataRepository : DataRepository {
    private var data: MutableMap<Sensor, Int> =
            Sensor.values().map { it to 0 }.toMap().toMutableMap()

    override fun updateTemperature(newValue: Int) {
        data[TEMPERATURE] = newValue
    }

    override fun retrieveTemperature(): Int =
            data[TEMPERATURE] ?: throw SensorException(TEMPERATURE, "Data not found")

    override fun updatePressure(newValue: Int) {
        data[PRESSURE] = newValue
    }

    override fun retrievePressure(): Int =
            data[PRESSURE] ?: throw SensorException(PRESSURE, "Data not found")

}

/*
 * Сервис - содержит бизнес-логику для работы с датчиками
 */
@Service
class DataService(@Autowired private val repository: DataRepository) {
    fun updateData(sensor: Sensor, newValue: Int) {
        when (sensor) {
            TEMPERATURE -> repository.updateTemperature(newValue)
            PRESSURE -> repository.updatePressure(newValue)
        }
    }

    fun retrieveData(sensor: Sensor): Int = when (sensor) {
        TEMPERATURE -> repository.retrieveTemperature()
        PRESSURE -> repository.retrievePressure()
    }
}

/*
 * Простейший data transfer object для обновления данных
 */
data class SampleDTO(val data: Int)

/*
 * Контроллер для обновления данных датчиков, когда устройство инициирует сеанс связи (PUSH)
 */
@RestController
@RequestMapping("/api/hardware")
class HardwareController(@Autowired val service: DataService) {
    /*
     * Датчик температуры
     * Чтобы отправить данные, пользуйте любой REST клиент или command line
     * `curl -X PUT -H "Content-Type: application/json" -d '{"data":1}' http://localhost:8080/api/hardware/temperature`
     */
    @PutMapping("/temperature")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateTemperature(@RequestBody data: SampleDTO) {
        service.updateData(TEMPERATURE, data.data)
    }

    /*
     * Датчик давления
     * Чтобы отправить данные, пользуйте любой REST клиент или command line
     * `curl -X PUT -H "Content-Type: application/json" -d '{"data":1}' http://localhost:8080/api/hardware/pressure`
     */
    @PutMapping("/pressure")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePressure(@RequestBody data: SampleDTO) {
        service.updateData(PRESSURE, data.data)
    }
}


/*
 * Контроллер для пользовательского интерфейса
 */
@RestController
@RequestMapping("/api/webclient")
class WebClientController(@Autowired val service: DataService) {
    /*
     * Данные всех датчиков как один JSON объект
     * Доступно в браузере по http://localhost:8080/api/webclient/sensor
     * или command line: `curl http://localhost:8080/api/webclient/sensor`
     */
    @GetMapping("/sensor")
    fun getSensorStatus() = Sensor.values()
            .map { sensor -> sensor to service.retrieveData(sensor) }
            .toMap()
}