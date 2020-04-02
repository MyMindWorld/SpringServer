<!DOCTYPE html>
<html lang="en">
<head>
    <title>Script Server</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <!--===============================================================================================-->
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css"/>">--%>
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
    <!--=======================================================================================-->
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--=======================================================================================-->
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/css-hamburgers/hamburgers.min.css"/>">--%>
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/css/util.css"/>">--%>
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>">--%>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/Newindex.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    <!--===============================================================================================-->
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <script src="<c:url value="/vendor/sockjs/sockjs.js"/>"></script>
    <script src="<c:url value="/vendor/stomp/stomp.js"/>"></script>
    <script type="text/javascript">
        let stompClient = null;
        const connectionName = "<sec:authentication property='principal.username' />";
        // var scriptFormData = new FormData();
        // var test = document.getElementsByName("commandParams")
        const $form = $("#ScriptForm");
        const data = getFormData($form);

        // function buildPostData(count) {
        //     for (let element in count) {
        //         scriptFormData.append('username', '');
        //     }
        //
        //
        // }

        function setConnected(connected) {
            document.getElementById('runScriptButton').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
                = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            const socket = new SockJS('/ScriptServer/chat');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/messages/', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
                stompClient.subscribe('/user/' + connectionName + '/errors', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
                stompClient.subscribe('/user/' + connectionName + '/reply', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
            });
            setTimeout(() => {
                runScript();
            }, 50);

        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            const text = document.getElementById('text').value;
            stompClient.send("/scriptsWS/chat", {},
                JSON.stringify({'from': connectionName, 'text': text}));
        }

        function showMessageOutput(messageOutput) {
            const response = document.getElementById('response');
            const p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": "
                + messageOutput.text + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        }

        function getFormData($form) {
            const unindexed_array = $form.serializeArray();
            const indexed_array = {};

            $.map(unindexed_array, function (n) {
                indexed_array[n['name']] = n['value'];
            });

            return indexed_array;
        }

        function runScript() {
            $.ajax({
                dataType: "json",
                method: "POST",
                url: '<c:url value="/scripts/run_script"/>',
                data: $('#ScriptForm').serialize(),
                // data: data,
                success: function (data) {
                    console.log("SUCCESS!");
                    console.log(data)
                },
                error: function (data) {
                    console.log('ERROR OCCURRED! message send : ' + data.toString());
                }

            });
            return false;
        }
        const out = document.getElementById("output")
        let c = 0

        setInterval(function() {
            // allow 1px inaccuracy by adding 1
            const isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1
            // scroll to bottom if isScrolledToBottom is true
            if (isScrolledToBottom) {
                out.scrollTop = out.scrollHeight - out.clientHeight
            }
        }, 500)

        function format () {
            return Array.prototype.slice.call(arguments).join(' ')
        }
    </script>
</head>
<body>

<div class='nav'>
    <ul>
        <li>
            <sec:authorize access="!hasAuthority('ADMIN_PAGE_USAGE')">
                <a class='logo' href='<c:url value="/profile"/>'>
                    <i class="fas fa-user fa-2x" style="color: white;"></i>
                </a>
            </sec:authorize>
            <sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">
                <a class='logo' href="<c:url value="/admin"/>" style="color: white">
                    <i class="fas fa-user-shield fa-2x"></i>
                </a>
            </sec:authorize>
        </li>
        <%--        <li>--%>
        <%--            <a href="<c:url value="/admin"/>">Profile</a>--%>
        <%--        </li>--%>
        <c:forEach items="${groups}" var="group">
        <li>
            <a>
                    ${group}
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <c:if test="${item.group_name == group}">
                        <sec:authorize access="hasAuthority('${item.name}')">
                            <li>
                                <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                            </li>
                        </sec:authorize>
                    </c:if>
                </c:forEach>
            </ul>
            </c:forEach>
        <li>
            <a href="<c:url value="/perform_logout"/>" style="color: white">Logout
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </li>

    </ul>
</div>
<%--<h3>--%>
<%--    Hello, <sec:authentication property="name"/>!--%>
<%--</h3>--%>

<script>
    $(".nav li").hover(function () {
        $(this).children("ul").stop().delay(20).animate({height: "toggle", opacity: "toggle"}, 200);
    });
</script>


<div class="script-params">
    <c:choose>
    <c:when test="${script!=null}"> <%-- Проверка выбран ли какой-нибудь скрипт --%>

        <%-- Показывается только если есть необходимая роль --%>
    <sec:authorize access="hasAuthority('${script.name}')">
        <h4>${script.display_name}</h4>
        <form name='f' id="ScriptForm" onsubmit="return connect();">
            <input name="name" id='name' type="hidden" value="${script.name}"/>

            <c:forEach items="${parameters}" var="parameter">
                <c:choose>
                    <c:when test="${parameter.type == 'list'}">
                        <label class="col-sm-2 control-label" for="${parameter.param}">
                            <c:out value="${parameter.name}"></c:out>

                            <select id="${parameter.param}" name="${parameter.param}" class="single"
                                    style="width: 200px; padding-left: 50px;"
                                    <c:if test="${parameter.required}">required</c:if>>
                                <c:forEach items="${parameter.values}" var="listValue">
                                    <option value="${listValue}">${listValue}</option>
                                </c:forEach>
                            </select>
                        </label>
                    </c:when>
                    <c:when test="${parameter.type == 'multiselect'}">
                        <label for="${parameter.description}">
                            <c:out value="${parameter.name}"></c:out>
                            <select name="${parameter.param}" class="multy form__field"
                                    multiple="multiple"
                                    style="width: 200px;"
                                    <c:if test="${parameter.required}">required</c:if>>
                                <c:forEach items="${parameter.values}" var="listValue">
                                    <option value="${listValue}">${listValue}</option>
                                </c:forEach>
                            </select>
                        </label>
                    </c:when>
                    <c:when test="${parameter.type == 'text'}">
                        <div class="form__group field">
                            <input type="input" class="form__field" placeholder="Name"
                                   name="${parameter.param}" id='${parameter.param}'
                                   maxlength="${parameter.max}"
                                   <c:if test="${parameter.required}">required</c:if>/>
                            <label for="${parameter.param}" class="form__label">${parameter.name}</label>
                        </div>
                    </c:when>
                    <c:when test="${parameter.type == 'hidden'}">
                        <%--                                        Параметр типа HIDDEN обрабатывается на стороне сервера, либо дефолтное значение, либо результат работы скрипта--%>
                    </c:when>
                    <c:when test="${parameter.type == 'boolean'}">
                        <input name="${parameter.param}" class="tgl tgl-light" id="${parameter.param}"
                               type="checkbox" <c:if test="${parameter.required}">required</c:if>/>
                        <label class="tgl-btn" for="${parameter.param}">${parameter.name}</label>
                        <br>
                    </c:when>
                    <c:when test="${parameter.type == 'int'}">
                        <input name="${parameter.param}" type="number" id="${parameter.name}"
                               min=${parameter.min} max=${parameter.max}
                               <c:if test="${parameter.required}">required</c:if>>
                        <label for="${parameter.name}">${parameter.name}</label>
                        <br>
                    </c:when>
                    <c:when test="${parameter.type == 'file_upload'}">
                        <input name="${parameter.param}" type="file" size="50"
                               <c:if test="${parameter.required}">required</c:if>/>
                        <br>
                    </c:when>


                    <c:otherwise>
                        | UNKNOWN "${parameter.type}" TYPE |
                        <br>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

        </form>
        <button id="runScriptButton" class="e"
                onclick="connect();"
        >Run Script
        </button>
        <button id="disconnect" class="e" disabled="disabled" onclick="disconnect();">
            Disconnect
        </button>
    </sec:authorize>
</div>
<script>
    $('#ScriptForm').on('submit', function () {
        alert('Form submitted! ITS A BUG!!!');
        connect();
        return false;
    });
</script>
    <%--Показывается только если НЕТ необходимой роли --%>
<sec:authorize access="!hasAuthority('${script.name}')">
    <div class="blank_message">
        У вас отсутсвуют права на выполнение запрошенного скрипта.
        <br>
        Обратитесь к руководителю вашей группы.
    </div>
</sec:authorize>
    <%--Показывается только если выбран скрипт и есть роль --%>
<sec:authorize access="hasAuthority('${script.name}')">
    <div class="script-content" id="output">
        <p id="response"></p>
        <div id="conversationDiv">
            <input type="text" id="text" placeholder="Write a message..."/>
            <button id="sendMessage" onclick="sendMessage();">Send</button>
        </div>
    </div>
</sec:authorize>

</c:when>
<c:otherwise> <%-- Если не выбран, дефолтная страница --%>
    <div class="blank_message">
        <img src="<c:url value="/images/cutLogo.png"/>" alt="script server logo">
        <br>
        <br>
        <br>
        <br>
        <br>
        Доступ к необходимым скриптам можно запросить у руководителя вашей группы<br>
        (Cake is a lie!)
    </div>
</c:otherwise>
</c:choose>


<script type="text/javascript">

    $(document).ready(function () {
        $('.multy').select2();
        $('.single').select2();
    });
</script>


</body>
</html>