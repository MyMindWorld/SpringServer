## Установка
Скрипт сервер может быть запущен двумя способами
1. Запуск с помощью IDE.

    Запустите maven-goal "spring-boot:run" из плагина spring-boot, либо выполните команду:
    ```shell script
    mvn sping-boot:run
    ``` 
2. Запуск через tomcat. 
   
   Для этого необходимо прописать в tomcat_installation/bin/setenv.sh директорию с файлом конфигурации. 
   Если файл отсутствует - создайте его.
   ```shell script
    export SPRING_CONFIG_LOCATION=/usr/opt/tomcat/ScriptServerConf/application.properties
   ``` 

---
Для работы Скрипт Сервера необходим mysql. Используйте версию 8.0.х - она гарантированно поддерживается.

Приложение само развернет структуру, вам нужно только создать базу. В противном случае на базе созданной самим приложением могут быть проблемы с русским языком, тк mysql драйвер не предоставляет возможности создания с необходимым charset.
```sql
CREATE DATABASE ScriptServer DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```

## Конфигурация

Конфигурация может находиться в папке resources в корне проекта. При наличии установленной системной переменной `SPRING_CONFIG_LOCATION`,
конфигурация будет прочитана оттуда. Второй вариант для приложения приоритетнее.

Файл с конфигурацией нужно назвать application.properties, либо script-server.properties, причем последний приоритетнее для сервера.

---
```properties
# В этом параметре указываются параметры подключения к бд, рекомендуется оставить всё после знака "?" для корректной работы с бд
spring.datasource.url = jdbc:mysql://localhost:3306/ScriptServer?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&creatIfNotExists=true
# Логин и пароль для подключения к бд
spring.datasource.username = root
spring.datasource.password = elephant

# Задаётся только при запуске через tomcat, сюда нужно вставить путь до catalina.home/
# TomcatPath=/xxx/xxx/xxx + /
# Путь до папки с конфигурационными файлами\скриптами\venv относительно корня (В случае с запуском через tomcat корень это TomcatPath, в случае с запуском через IDE это папка webapp)
scriptServerResourcesPath=/ScriptsConfig
# Путь до папки с конфигами
configPath=/config
# Путь до папки со скриптами, относительно неё нужно будет указывать путь до скрипта в конфигурации
scriptsPath=/scripts
# Путь до папки где будут создаваться venv
venvPath=/venvDir
# Путь до папки с requirements, относительно неё нужно будет указывать путь до requirements в конфигах
requirementsPath=/requirements
# Название дефолтного venv, в котором будут запускаться скрипты у которых venv не указан
defaultVenvName=defaultVenv
# Название файла с дефолтными requirements для дефолтного venv, файл должен находиться в одной директории с файлами конфигурации
defaultVenvRequirementsFileName=defaultVenvRequirements.txt
```