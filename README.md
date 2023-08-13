# Сервис для перевода с карты на карту
## Описание
RESTful приложение для перевода денег с карты на карту. Работает на порту 5500 по протоколу HTTP совместно с фронт частью приложения, расположенной по 
адресу: https://serp-ya.github.io/card-transfer/. Однако, в соответствие в принципами построения REST является независимым от нее. 
  Данные о картах, транзакциях и подтверждениях хранятся в файловой системе в потокобезопасных коллекциях. Данные о проведенных транзациях дополнительно записываются в файл по адресу 
/transfer-logs.log
## Запуск
В корневой папке выполнить команду:
```
docker compose up
```
Образ доступен для скачивания с докер хаба [munirsunchalyaev/card2card](https://hub.docker.com/r/munirsunchalyaev/card2card)
## Перед началом работы
Для тестирования задания было создано дополнительное API для взаимодействия с картами. Так, при запуске контейнера создаются новые карты с балансом 
1000 у.е., данные которых можно узнать в логах. К примеру: 
```
2023-08-13 14:48:20 2023-08-13 11:48:20,087  INFO  [        main] c.m.a.c.service.impl.CardServiceImpl     : Card: number=7427010245550803, validTill=08/28, cvv=173, value=1000, currency=RUR 
2023-08-13 14:48:20 2023-08-13 11:48:20,087  INFO  [        main] c.m.a.c.service.impl.CardServiceImpl     : Card: number=2558173289728577, validTill=08/28, cvv=976, value=1000, currency=RUR 
```
## Примеры запросов
1) Транзакция с подтверждением кодом. Смотрим в логах доступные карты:
   ```
   2023-08-13 15:06:27 2023-08-13 12:06:27,223  INFO  [        main] c.m.a.c.service.impl.CardServiceImpl     : Card: number=9642106773770464, validTill=08/28, cvv=211, value=1000, currency=RUR 
   2023-08-13 15:06:27 2023-08-13 12:06:27,223  INFO  [        main] c.m.a.c.service.impl.CardServiceImpl     : Card: number=4420185285230483, validTill=08/28, cvv=655, value=1000, currency=RUR 
   ```
   [](<img width="1440" alt="image" src="https://github.com/MunSunch/Card2Card/assets/61779834/18ab8f0a-fed3-457b-8b72-2e6ae5dc5e99">
)










