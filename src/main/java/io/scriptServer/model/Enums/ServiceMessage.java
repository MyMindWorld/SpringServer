package io.scriptServer.model.Enums;

public enum ServiceMessage {
    /*
   Сообщение информирующее UI о запуске скрипта
    */
    Started,
    /*
    Сообщение информирующее UI об остановке скрипта,
    после него пользователь будеот отключен от сокета
     */
    Stopped
}
