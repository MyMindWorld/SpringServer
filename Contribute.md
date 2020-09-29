## Config

В этой части хранятся Spring Конфигурации различных модулей системы.

Например, `CustomLdapAuth` - используется для предоставления `AuthenticationProvider` для `SecurityConfig` части модуля `Spring Security`

`WebConfig` - Отвечает за процессинг `jsp` страниц и указывает место для `view` страниц

`WebSocketConfig` - Создание эндпоинтов для WS взаимодействия веб клиента и сервера

`ProcessQueueConfig` и `MessageQueueConfig` - очереди запущенных скриптов и полученных от клиента сообщений соответсвенно.

## Controller

В этой части проекта находятся контроллера для Api и Web частей проекта. Api части помечены постфиксом `Rest`

#### Web

Web контроллеры помечаются аннотацией `@Controller`

Типичный метод в веб контроллере выглядит следующим образом: 
```java
@Controller
public class RolesController {
     @RequestMapping(value = "/admin/roles", method = RequestMethod.GET)
     public String roles(Model model) {
 
         model.addAttribute("roles", roleRepository.findAll());
         model.addAttribute("privileges", privilegeRepository.findAll());
         
         return "roles";
     }
}
```

Аннотация `@RequestMapping` отвечает за указание эндпоинта и HTTP метода. 
В аргументах указываются либо объекты которые мы можем получить от Spring (Например, контекст безопасности или модель страницы)

Добавление аттрибутов к модели позволит использовать их как переменные в JSP странице
```jsp
<c:forEach items="${roles}" var="role">
    <tr>
        <td>
            <c:out value="${role.id}"/>
        </td>
    <tr>
</c:forEach>
```

`return` методов в классах @Controller с возвращаемым значением типа `String` будет искать совпадение с jsp файлом.

Таким образом приведённый выше метод будет возвращать страницу из `/WEB-INF/views/` (Указано в WebConfig) с названием `roles` 

#### Rest

Rest контроллеры помечаются аннотацией `@RestController`

Типичный метод такого контроллера будет выглядеть похожим образом:

```java
@RestController
public class FileUploadRestController {

     @GetMapping("/files/get_all")
     public ResponseEntity<?> listUploadedFiles() {
         return ResponseEntity.ok().body(storageService.getAllResourceFiles());
     }
}
```

------
[Подробнее про контроллеры](https://www.baeldung.com/spring-controllers)

[Подробнее про аннотации контроллеров](https://www.baeldung.com/spring-mvc-annotations) 


## Model & Repository

В этом пакете хранятся модели системы, в том числе модели для базы данных и их репозитории

[Паттерн Repository](https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html)

[Проект Lombok](https://projectlombok.org/features/all)

[Все jpa аннотации](https://dzone.com/articles/all-jpa-annotations-mapping-annotations)  

## Service

Тут находится вся бизнес логика, разделённая по сущностям. 
Таким образом `RoleService` будет содержать методы для работы с ролями, а `VenvService` методы для работы с Venv

## Utils

Здесь находятся все части проекта подходящие под описание _Вспомогательных_

#### System Integration
В части [SystemIntegration](./src/main/java/ru/protei/scriptServer/utils/SystemIntegration) находится всё связанное с запуском исполняемых файлов в системе.

В частности Python скриптов и программ для выпадающих списков (Bash\PowerShell etc)

#### TestLoginDataLoader 
[TestLoginDataLoader](./src/main/java/ru/protei/scriptServer/utils/TestLoginDataLoader.java)
В этой части находится метод для заполнения бд тестовыми данными (Логами и привилегиями) для удобства разработки.

#### OnEventsAction
[OnEventsAction](./src/main/java/ru/protei/scriptServer/utils/OnEventsAction.java)
В этой части находится код, который выполняется непосредственно перед стартом приложения.

Тут обновляются скрипты с гита, обновляется роль со всеми правами и создаются дефолтные юзеры

## JSP

[Подробный полный гайд](https://www.baeldung.com/jsp)



## TODO

0. В проекте раскиданы //Todo и //legshooting, к ним можно обратиться из вкладки TODO в IntellijIdea на панели внизу слева
1. Часть [SystemIntegration](./src/main/java/ru/protei/scriptServer/utils/SystemIntegration) молит о рефакторинге и читаемости
2. Веб часть просит редизайн и вынос js скриптов в отдельные файлы
3. Тесты на основной функционал

