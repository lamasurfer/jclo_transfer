# Курсовой проект "Сервис перевода денег"

Простой REST-сервис для перевода денег с карты на карту, написан на `java` с использованием `Spring boot` для интеграции с заранее подготовленным [веб-приложением](https://github.com/serp-ya/card-transfer) в соответствии со [спецификацией](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml).

Для автоматического запуска с `docker-compose` перейдите в репозиторий https://github.com/lamasurfer/jclo_transfer_docker

Для запуска вручную понадобится `gradle`:

1. склонируйте (или просто [скачайте](https://github.com/lamasurfer/jclo_transfer/archive/refs/heads/master.zip)) репозиторий:
```bash
git clone https://github.com/lamasurfer/jclo_transfer.git
```

2. запустите в корневом каталоге репозитория следующие команды:
```bash
gradle clean build -x test

# если используете docker
docker build -t transfer .

docker run -d -p 5500:8080 --name transfer transfer

# если docker-compose
docker-compose up -d
```
3. откройте уже развернутое приложение по адресу https://serp-ya.github.io/card-transfer/

Переводить можно на любой [валидный](https://www.freeformatter.com/credit-card-number-generator-validator.html) номер кредитной карты с аккаунтов:
|Номер карты|CVV/CVC|баланс, руб|
|---|---|---|
|4242424242424242|424|1000000|
|4111111111111111|343|100000|
|5500000000000004|543|10000|
|5555555555554444|123|0|

В качестве срока действия карт указывайте текущий месяц и год.

4. завершение работы:
```bash
# docker
docker stop transfer

# docker-compose
docker-compose down
```

Ссылка на задание: https://github.com/netology-code/jd-homeworks/blob/master/diploma/moneytransferservice.md
