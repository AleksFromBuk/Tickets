# Анализатор авиабилетов

Приложение для анализа данных авиабилетов из JSON-файла.

## Возможности

* Расчет минимального времени полета между городами для каждого авиаперевозчика
* Расчет разницы между средней ценой и медианой для билетов
* Поддержка временных зон аэропортов
* Потоковая обработка больших файлов
* Кэширование для небольших файлов (<3MB)

## Требования

* Java 17+
* Maven

## Сборка

```bash
mvn clean package
```

## Использование

```bash
# Базовый вариант (использует Tickets.json в текущей директории)
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar [параметры]
```

### Параметры

| Флаг           | Описание                         | По умолчанию        |
| -------------- | -------------------------------- |---------------------|
| `-p`, `--path` | Путь к JSON-файлу с билетами     | `Tickets.json`      |
| `-o`, `--out`  | Файл для результатов             | `result.text`       |
| `-f`, `--from` | Код аэропорта вылета (3 буквы)   | `VVO` (Владивосток) |
| `-t`, `--to`   | Код аэропорта прибытия (3 буквы) | `TLV` (Тель-Авив)   |
| `--cache`      | Включить кэширование файлов      | Выключено           |

### Примеры

```bash
# Базовый вариант (файл Tickets.json в текущей директории)
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar

# С указанием файла данных
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar --path=/home/user/data/tickets.json

# С указанием выходного файла
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar --out=/var/reports/result.txt

# С указанием маршрута
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar --from=LED --to=IST

# С включенным кэшированием
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar --cache

# Полный пример
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar \
  --path=/home/user/data/tickets.json \
  --out=/var/reports/flight_analysis.txt \
  --from=VVO \
  --to=TLV \
  --cache
```

## Особенности реализации

### Временные зоны

Для расчета длительности полета учитываются фактические часовые пояса аэропортов.
Данные берутся из `src/main/resources/airports.yaml`.

**Пример записи:**

```yaml
VVO: Asia/Vladivostok
TLV: Asia/Jerusalem
```

### Потоковая обработка

Для больших файлов используется потоковый парсинг JSON (не требует загрузки всего файла в память).

### Кэширование

Для файлов <3MB автоматически включается кэширование при указании флага `--cache`.

### Обработка ошибок

Некорректные билеты пропускаются с записью в лог.

## Формат выходных данных

```text
Расчет выполнен на основе:
- Временная зона вылета: Asia/Vladivostok (UTC+10:00)
- Временная зона прилета: Asia/Jerusalem (UTC+02:00)

Минимальное время полета (VVO -> TLV):
  SU: 10h 30m (Asia/Vladivostok - Asia/Jerusalem)
  TK: 11h 15m (Asia/Vladivostok - Asia/Jerusalem)

Разница между средней ценой и медианой: 1500,00
```

## Тестирование

```bash
mvn test
```

## Быстрый старт на Linux
 1. Установите зависимости:

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```
 2. Клонируйте репозиторий:

```bash
git clone https://github.com/AleksFromBuk/Tickets.git
cd Tickets
```
 3. Соберите приложение:

```bash
mvn clean package
```
 4. Запустите анализ:

```bash
java -jar target/Tickets_parse-1.0-SNAPSHOT.jar
```
