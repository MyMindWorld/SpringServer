# ScriptServer


---
### Конфиги скриптов

Базовая часть конфигурации:

```json
{
  "name" : "ES_Start", 
  "display_name": "Старт ES",
  "group": "Emergency",
  "python_version": "3.7",
  "venv": "EmergVenv",
  "requirements": "EmergencyRequirements.txt",
  "script_path": "start_es_server.py",
  "description": "Скрипт запуска ES в докере.",
  "parameters": []
}
```
`name` - Параметр для идентификации скрипта, должен быть уникальным.

`display_name` -  Имя скрипта. Отображается в меню выбора скриптов и в заголовке страницы скрипта.

`group` -  Название группы скриптов. Скрипты с одинаковой группой отображаются в меню вместе.

`python_version` - Версия python, с которой будет создан venv.

`venv` - С каким именем создать венв, если указать это же имя в другом скрипте - будет использован тот-же венв.
Указывать разные реквайрментс для одного venv - не получиться, если вам нужны другие зависимости либо дополняйте requirements, либо используйте другой venv с другими зависимостями.

`requirements` - Путь до файла с необходимыми зависимостями относительно корня зависимостей.

`script_path`- Путь до скрипта относительно папки со скриптами из файла property.

`description` - Описание скрипта. В следующих релизах оно будет отображено на странице запуска скрипта.

`parameters` - Лист параметров, с которыми может быть запущен скрипт.

---
#### Параметры

0. Базовая часть параметра:
```json
{
      "name": "Пример названия",
      "param": "--example_key",
      "type": "int",
      "required": true,
      "description": "Текст описания"
    }
```

Основные поля для каждого параметра:

`name` - Название параметра для отображения.

`param` -  Ключ, с которым параметр будет передан в скрипт.

`type` - Тип параметра.

`required` - Необходим ли данный параметр для запуска скрипта, по умолчанию - false.

`description` - В зависимости от типа параметра, описание отображается по-разному.


1. Параметр list:
    ```json
    {
          "name": "Ветка",
          "param": "--tag",
          "type": "list",
          "script": "cmd /c test.bat ${searchSelect} ${--type} ${--example_key}",
          "required": true,
          "description": "Выпадающий список версий",
          "values": [
            "branch1default",
            "branch2default",
            "branch3default"
          ]
        }
    ```
    
    `script` - Строка запуска скрипта для отображения параметров в выпадающем списке. Обязательно указывайте исполняемый файл для запуска, например `bash ./script.sh`
    
    Путь нужно указывать относительно папки scripts.
    
    Также в строке запуска можно использовать выбранные/введённые значения из других параметров с помощью синтаксиса `${param}`,
    где `param` - значение поля `param` у другого параметра. Чтобы скрипт использовал текст, введенный в поиск, необходимо передать параметр `${searchSelect}`
    Например у вас есть параметр "Зона для запуска", у которого поле "param" == "--zone", в таком случае чтобы передать это скрипту параметра нужно будет добавить к строке запуска
    `"cmd /c test.bat ${--zone}"`
    
    `values` - Дефолтные параметры. Они будут добавлены вне зависимости от того, привязан ли к параметру скрипт.
    
2. multiselect - лист с множественным выбором:
    ```json
    {
          "name": "Мультиселект",
          "param": "--multiselect",
          "description": "Текст в подсказке",
          "type": "multiselect",
          "required": true,
          "values": [
            "value 1",
            "value 2",
            "value 3",
            "value 4"
          ]
        }
    ```
   
  `script`,`values`, настраивается аналогично параметру list 

3. Boolean параметр:
    ```json
    {
          "name": "Интерактивный режим",
          "param": "--interactive",
          "type": "boolean",
          "description": "При выборе этого чекбокса после запуска сервера и установки лицензии у вас в этом окне будут показаны логи."
        }
    ```
   Чекбокс. Выбор пользователя будет передан в скрипт как on\off

4. Скрытый параметр:
    ```json
        {
              "name": "MaxExecutionTime",
              "param": "--max_execution_time",
              "type": "hidden"
            }
    ```
   
   Скрытый параметр, не изменяемый через скриптСервер. Полезен, если в скрипт нужно передавать константы, ненужные при конфигурировании через GUI.

5. Число
    ```json
        {
              "name": "Simple Int",
              "param": "--simple_int",
              "type": "number"
            }
    ```
   
   Параметр в который можно ввести целое или дробное число
   
6. Текст
    ```json
        {
              "name": "Example Text",
              "param": "--text_param_example",
              "type": "text"
            }
    ```
   
   Параметр в который можно ввести текст.
   поле `name` будет отображено в поле как плейсхолдер.
   
7. Загрузка файла скрипту
    ```json
            {
                  "name": "Настройки сервера",
                  "param": "--file_upload",
                  "type": "file_upload",
                  "description": "Сюда можно загрузить файл настроек."
                }
    ```
   
   В данный момент реализация открыта к обсуждению, предполагается, что файл будет загружаться в корень scriptServerResourcesPath/Download и путь передаваться скрипту как параметр

---
### Использование модальных окон

В скрипт сервере имеется возможность предоставления пользователю выбора вариантов непосредственно после запуска скрипта, для этого используются модальные окна.

Для передачи скрипт серверу информации о том что нужно вывести пользователю модальное окно нужно отправить в поток вывода сообщение вида:
```python
print("##ScriptServer[MODAL_TYPE'TEXT_FOR_USER']")
```
где MODAL_TYPE - тип модалки которая будет показана пользователю

TEXT_FOR_USER- текст который будет в заголовке

#### Виды модалок        
1. ShowInfo
    Выводит пользователю информационное сообщение, не прерывая работы скрипта.
    ```python
    print("##ScriptServer[ShowInfo'Сервер успешно запущен по адресу 192.168.72.45:5238']")
    ```
   
2. InputText
    Выводит пользователю модалку с однострочным текстовым полем и кнопкой подтверждения.
    
    ```python
    print("##ScriptServer[InputText'Введите адрес приложения']")
    ```
   
3. TextArea
    Выводит пользователю модалку с многострочным текстовым полем и кнопкой подтверждения.
    
    Нужно отметить, что все переносы строк которые сделает пользователь - будут отправлены отдельно,
    т.е. для получения большого количества текста следует воспользоваться параметром для загрузки файла
    
    ```python
    print("##ScriptServer[TextArea'Введите много-много текста']")
    ```
   
4. Boolean
    Выводит пользователю модалку с вариантами ответа да\нет, возвращает в скрипт строки 1/0
        
    ```python
    print("##ScriptServer[Boolean'Вам подходит порт 5025?']")
    ```

4. BooleanCustom
    Выводит пользователю модалку с двумя настраиваемыми вариантами ответа и  возвращает в скрипт строки 1/0
        
    ```python
    print("##ScriptServer[BooleanCustom'Вы хотите использовать встроенную базу данных или развернуть её отдельно?/Встроенную/Развернуть отдельно']")
    ```
---
## Использование сервера

После [инсталляции](https://git.protei.ru/script-server/SpringServer/blob/master/installGuide.md) сервер запустится на порту 8080 с эндпоинтом ScriptServer.

http://127.0.0.1:8080/ScriptServer/

Дефолтный логин и пароль для входа - admin:admin

---
### Меню

1. Крайний левый параметр - страница пользователя или администратора. Привилегия для просмотра админки - `ADMIN_PAGE_USAGE`

2. По центру - выпадающий список, отсортированный по группам скриптов.

3. Справа - выход ~~в окно~~ из учетной записи.
---
### Админка

1. Основная страница.
 
    На этой странице отображаются логи. Все строки кликабельные, при нажатии показывают более подробную информацию. 
    Таблица может быть отсортирована по столбикам.
    
2. Страница Scripts.

    1. Кнопка Update Scripts - 
    обновление конфигураций скриптов из файловой системы.
    2. Кнопка Update Scripts & clear venv - 
    аналогично обновлению, но ещё удаляет все созданные venv.
    3. Update Scripts from GitLab - 
    Удаляет все конфиги/реквайрменты и заменяет их скачанными из папки https://git.protei.ru/script-server/scripts.
    После этого можно производить любые изменения в файловой системе и обновлять конфиги с помощью первых двух кнопок.
    4. В таблице скриптов представлены все загруженные в бд конфигурации. В каждой строчке можно открыть модальное окно с действиями с помощью кнопки в столбике Action. Там также можно обновить конфигурацию конкретного скрипта.

3. Страница Roles.

    На этой странице можно создавать,изменять или удалять роли. 
    
    Роли `ROLE_ALL_SCRIPTS` и `ROLE_ADMIN` - защищены от любых изменений.
    
4. Страница Users.

    На этой странице можно создавать, изменять или удалять пользователей.  

5. Страница Server Control.

    На этой странице можно смотреть запущенные в данный момент скрипты и останавливать их.
    
    Также на этой странице можно перевести сервер в режим обслуживания, тем самым запретив запуск скриптов до выведения сервера из этого статуса. 
    Когда сервер переводится в режим обслуживания, все пользователи, у которых запущены скрипты, получают уведомление, что запуск новых скриптов временно запрещен.

---
 ### Привилегии
  
 `ADMIN_PAGE_USAGE` - Отвечает за возможность входа в админку и просмотр логов.
 
 `SCRIPTS_UPDATE` - Доступ ко вкладке скриптов, обновление конфигов.
 
 `ROLES_SETTING` - Отвечает за возможность доступа ко вкладкам Roles и Users, созданию/изменению и удалению пользователей и ролей.
 
 `SERVER_CONTROL` - Доступ ко вкладке управления сервером и остановке скриптов.