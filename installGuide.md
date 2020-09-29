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
    export SPRING_CONFIG_LOCATION=/opt/tomcat/ScriptServerConf/application.properties
   ``` 

---
Для работы Скрипт Сервера необходим mysql. Используйте версию 8.0.х - она гарантированно поддерживается.

Приложение само развернет структуру, вам нужно только создать базу. В противном случае на базе созданной самим приложением могут быть проблемы с русским языком, тк mysql драйвер не предоставляет возможности создания с необходимым charset.
```sql
CREATE DATABASE ScriptServer DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```

## Установка в tomcat-mysql контейнер
1. Запустите tomcat-mysql контейнер
    ```shell script
    docker run -d --name=script-serverSpring --restart unless-stopped  -p 8585:8080 -p 1313:3306 git.protei.ru:8443/docker/images/tomcat-mysql
    ```
2. Создайте директорию для конфигов ScriptServer
3. Создайте файл `application.properties`
4. [Отредактируйте конфигурацию](#Конфигурация)
5. Скачайте последнюю сборку [из Jenkins](https://jenkins.protei.ru/view/all/job/qa/job/ScriptServer/job/ScriptServerPipeline/)
6. Переименуйте *.war файл в `ScriptServer.war`
7. Если томкат был остановлен, запустите его через `/opt/tomcat/bin/startup.sh`
8. Приложение будет доступно по адресу `http://HOST:8585/ScriptServer/`, логи будут в `/opt/tomcat/bin/logs/ScriptServer/`

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

## Troubleshooting


1. Если при создании венв вылезает следующая ошибка :
    ```commandline
       Error: Command '/bin/python3', '-Im', 'ensurepip', '--upgrade', '--default-pip']' returned non-zero exit status 1.
    ``` 
   Проверьте что вы корректно установили python, а именно python3-distutils,python3-setuptools и python3-venv
   
   