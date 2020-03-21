<!DOCTYPE html>
<html lang="en">
<head>
    <title>Script Server</title>
    <jsp:include page="init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
    <!--=======================================================================================-->
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/Linearicons-Free-v1.0.0/icon-font.min.css"/>">--%>
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/css-hamburgers/hamburgers.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/util.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/Newindex.css"/>">
    <!--===============================================================================================-->
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>"></link>
    <script src="<c:url value="/vendor/sockjs/sockjs.js"/>"></script>
    <script src="<c:url value="/vendor/stomp/stomp.js"/>"></script>
    <script type="text/javascript">
        var stompClient = null;
        var connectionName = "<sec:authentication property='principal.username' />";
        // var scriptFormData = new FormData();
        // var test = document.getElementsByName("commandParams")
        var $form = $("#ScriptForm");
        var data = getFormData($form);

        function buildPostData(count) {
            for (let element in count) {
                scriptFormData.append('username', '');
            }


        }

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
                = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/ScriptServer/chat');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/messages/', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
            });
        }

        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            var text = document.getElementById('text').value;
            stompClient.send("/app/chat", {},
                JSON.stringify({'from': connectionName, 'text': text}));
        }

        function showMessageOutput(messageOutput) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": "
                + messageOutput.text + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        }

        function getFormData($form) {
            var unindexed_array = $form.serializeArray();
            var indexed_array = {};

            $.map(unindexed_array, function (n, i) {
                indexed_array[n['name']] = n['value'];
            });

            return indexed_array;
        }

        function runScript() {
            $.ajax({
                dataType: "json",
                method: "POST",
                url: '<c:url value="/scripts/run_script"/>',
                data: $('#ScriptForm').serializeArray(),
                // data: data,
                success: function (data) {
                    console.log("IM HERE")
                    console.log(data)
                },
                error: function (data) {
                    console.log('my message' + data);
                }

            });

        }
    </script>
</head>
<body>

<div class='nav'>
    <ul>
        <li>
            <%--                        <a class='logo' href='http://andytran.me'>--%>
            <%--                            <img src='https://s3-us-west-2.amazonaws.com/s.cdpn.io/169963/logo_(1).svg' width="10" height="10">--%>
            <%--                        </a>--%>
        </li>
        <li>
            <a href="<c:url value="/admin"/>">Profile</a>
        </li>
        <c:forEach items="${groups}" var="group">
            <li>
                <a name="menuItem">
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
            <li/>
        </c:forEach>
        <sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">
            <li>
                <a href="<c:url value="/admin"/>" style="color: grey">
                    <i class="fas fa-user-shield fa-2x"></i>
                </a>
            </li>
        </sec:authorize>
        <li>
            <a href="<c:url value="/perform_logout"/>" class="e" style="color: #ffe2ea">Logout
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


<div class="script-content">
    <c:choose>
        <c:when test="${script!=null}"> <%-- Проверка выбран ли какой-нибудь скрипт --%>

            <%-- Показывается только если есть необходимая роль --%>
            <sec:authorize access="hasAuthority('${script.name}')">
                <h4>${script.display_name}</h4>
                <table>
                    <thead>
                    <form name='f' id="ScriptForm" onsubmit="return validateAddUserForm()" class="form__group field"
                          action=
                              <c:url value='/scripts/run_script'/> method='GET'>
                        <input name="name" id='name' type="hidden" value="${script.name}"/>

                        <c:forEach items="${parameters}" var="parameter">
                            <tr>
                                <c:choose>
                                    <c:when test="${parameter.type == 'list'}">
                                        <select name="${parameter.param}" class="single"
                                                style="width: 200px" <c:if test="${parameter.required}">required</c:if>>
                                            <c:forEach items="${parameter.values}" var="listValue">
                                                <option value="${listValue}">${listValue}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${parameter.type == 'multiselect'}">
                                        <select name="${parameter.param}" class="multy"
                                                multiple="multiple"
                                                style="width: 200px" <c:if test="${parameter.required}">required</c:if>>
                                            <c:forEach items="${parameter.values}" var="listValue">
                                                <option value="${listValue}">${listValue}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${parameter.type == 'text'}">
                                        <input type="text" class="form__field" placeholder="${parameter.name}"
                                               name="${parameter.param}" id='${parameter.name}'
                                               maxlength="${parameter.max}"
                                               <c:if test="${parameter.required}">required</c:if>/>
                                    </c:when>
                                    <c:when test="${parameter.type == 'hidden'}">
                                        <%--                                        Параметр типа HIDDEN обрабатывается на стороне сервера, либо дефолтное значение, либо результат работы скрипта--%>
                                    </c:when>
                                    <c:when test="${parameter.type == 'boolean'}">
                                        <input name="${parameter.param}" class="tgl tgl-light" id="${parameter.name}"
                                               type="checkbox" <c:if test="${parameter.required}">required</c:if>/>
                                        <label class="tgl-btn" for="${parameter.name}">${parameter.name}</label>
                                    </c:when>
                                    <c:when test="${parameter.type == 'int'}">
                                        <input name="${parameter.param}" type="number" id="${parameter.name}"
                                               min=${parameter.min} max=${parameter.max}
                                               <c:if test="${parameter.required}">required</c:if>>
                                        <label for="${parameter.name}">${parameter.name}</label>
                                    </c:when>
                                    <c:when test="${parameter.type == 'file_upload'}">
                                        <input name="${parameter.param}" type="file" size="50"
                                               <c:if test="${parameter.required}">required</c:if>/>
                                    </c:when>


                                    <c:otherwise>
                                        UNKNOWN TYPE
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                            <%--                        <input name="submit" type="submit" class="e" value="Run Script"/>--%>

                    </form>
                    </thead>
                </table>

                <div>
                    <div>
                            <%--                        <input type="text" id="from" placeholder="Choose a nickname"/>--%>
                    </div>
                    <br/>
                    <div>
                        <button id="connect" class="e" onclick="connect(); runScript();">Run Script</button>
                        <button id="disconnect" disabled="disabled" onclick="disconnect();">
                            Disconnect
                        </button>
                    </div>
                    <br/>
                    <div id="conversationDiv">
                        <input type="text" id="text" placeholder="Write a message..."/>
                        <button id="sendMessage" onclick="sendMessage();">Send</button>
                        <p id="response"></p>
                    </div>
                </div>


            </sec:authorize>
            <%--                   Показывается только если НЕТ необходимой роли --%>
            <sec:authorize access="!hasAuthority('${script.name}')">
                У вас отсутсвуют права на выполнение запрошенного скрипта.
                <br>
                Обратитесь к руководителю вашей группы.
            </sec:authorize>

        </c:when>
        <c:otherwise> <%-- Если не выбран, дефолтная страница --%>
            <img src="<c:url value="/images/cutLogo.png"/>" alt="script server logo">
            <div>
                Доступ к необходимым скриптам можно запросить у руководителя вашей группы<br>
                (Cake is a lie!)
            </div>
        </c:otherwise>
    </c:choose>

</div>


<script type="text/javascript">
    function SetMenuVisible() {
        let e = document.getElementById("sidebar");
        e.style.display = 'none'
    }

    $(document).ready(function () {
        $('.multy').select2();
        $('.single').select2();
    });
</script>


<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/jquery/jquery-3.2.1.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/bootstrap/js/popper.js"/>"></script>--%>
<%--<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/js/main.js"/>"></script>--%>

</body>
</html>