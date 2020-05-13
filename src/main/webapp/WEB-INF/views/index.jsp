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
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/Newindex.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/ToggleButtons.css"/>">
    <!--===============================================================================================-->
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <script type="text/javascript">
        let stompClient = null;
        let sessionId = null;
        let color = "black";
        let isConnected = false;
        const connectionName = '<c:out value="${username}"></c:out>'
        const $form = $("#ScriptForm");
        const data = getFormData($form);

        function setConnected(connected) {
            isConnected = connected;
            document.getElementById('runScriptButton').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('disconnect').textContent = "Stop script";
        }

        function connect(scriptName) {
            if (!document.forms[0].checkValidity()) {
                document.forms[0].reportValidity();
                return;
            }

            document.getElementById('response').innerHTML = '';
            const socket = new SockJS('/ScriptServer/scriptsSocket');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {

                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/messages/', function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
                stompClient.subscribe('/user/' + connectionName + '/reply/' + scriptName, function (messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
                let sessionIdRaw = socket._transport.url
                sessionId = sessionIdRaw.split("/")[6]
                console.log(sessionId);
                setTimeout(() => {
                    runScript(sessionId);
                }, 50);
            });

        }

        function disconnect() {
            setConnected(false);
            if (stompClient != null) {
                stompClient.disconnect();
            }
            console.log("Disconnected");
        }

        function sendMessage(scriptName) {
            const text = document.getElementById('text').value;
            stompClient.send("/scriptsWS/scriptsSocket", {},
                JSON.stringify({
                    'username': connectionName,
                    'text': text,
                    'scriptName': scriptName,
                    'uniqueSessionId': sessionId
                }));
        }

        function showMessageOutput(messageOutput) {
            messageOutput.text = messageOutput.text.replace('"[" + ${script.script_path}' + ".INFO]", "") // removing default script info logging message
            // Тут можно будет менять цвет и стиль сообщений, в зависимости от адреса или содержимого
            if (messageOutput.modalType != null) {
                parseModalMessage(messageOutput)
                return
            } else if (messageOutput.serviceMessage === "Stopped") {
                disconnect()
            }
            const response = document.getElementById('response');
            const p = document.createElement('p');
            console.log(messageOutput.text)
            if (messageOutput.text.includes("\u001B[34m")) { // Debug
                color = "darkblue" // set all received messages color to blue, until default color received
                messageOutput.text = messageOutput.text.replace("\u001B[34m", "")
            }
            if (messageOutput.text.includes("\u001B[32m")) { // Info
                color = "black"
                messageOutput.text = messageOutput.text.replace("\u001B[32m", "")
            }
            if (messageOutput.text.includes("\u001B[31m")) { // Err
                color = "red"
                messageOutput.text = messageOutput.text.replace("\u001B[31m", "")
            }

            p.style.color = color;

            if (messageOutput.text.includes("\u001B[0m")) { // Set Default color back, after color setting because message can be completed
                color = "black"
                messageOutput.text = messageOutput.text.replace("\u001B[0m", "")
            }


            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(
                messageOutput.time + " " + messageOutput.username + ": " + messageOutput.text));
            response.appendChild(p);
        }

        function parseModalMessage(messageOutput) {
            if (messageOutput.modalType === "Boolean") {
                openBooleanModal(messageOutput.text)
                return
            } else if (messageOutput.modalType === "BooleanCustom") {
                openCustomBooleanModal(messageOutput.text.split("/")[0], messageOutput.text.split("/")[1], messageOutput.text.split("/")[2])
                return
            } else if (messageOutput.modalType === "InputText") {
                openInputTextModal(messageOutput.text)
                return
            } else if (messageOutput.modalType === "TextArea") {
                openTextAreaModal(messageOutput.text)
                return
            } else if (messageOutput.modalType === "ShowInfo") {
                openInfoModal(messageOutput.text)
                return
            }

        }

        function getFormData($form) {
            const unindexed_array = $form.serializeArray();
            const indexed_array = {};

            $.map(unindexed_array, function (n) {
                indexed_array[n['name']] = n['value'];
            });

            return indexed_array;
        }

        function runScript(sessionId) {
            $.ajax({
                dataType: "json",
                method: "POST",
                url: '<c:url value="/scripts/run_script"/>',
                data: $('#ScriptForm').serialize(),
                headers: {
                    'sessionId': sessionId
                },
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

        function killScript(sessionId) {
            document.getElementById('disconnect').disabled = true;
            document.getElementById('disconnect').textContent = "Stopping...";
            $.ajax({
                dataType: "json",
                method: "POST",
                url: '<c:url value="/scripts/kill_script"/>',
                data: $('#ScriptForm').serialize(),
                headers: {
                    'sessionId': sessionId
                },
                success: function () {
                    console.log("SUCCESS!");
                    disconnect()
                },
                error: function (jqXHR, exception) {
                    if (jqXHR.status == 200){
                        console.log("Script was stopped successfully.")
                        disconnect()
                        return
                    }
                    console.log(jqXHR.status)
                    console.log(exception)
                    console.log('ERROR OCCURRED! message send : ' + data.toString());
                }

            });
            return false;
        }


        let c = 0

        setInterval(function () {
            if (isConnected == false){
                return
            }
            const out = document.getElementById("output")
            // allow 1px inaccuracy by adding 1
            const isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1
            // scroll to bottom if isScrolledToBottom is true
            if (!isScrolledToBottom) {
                out.scrollTop = out.scrollHeight - out.clientHeight
            }
        }, 100)

        function format() {
            return Array.prototype.slice.call(arguments).join(' ')
        }
    </script>
    <script src="<c:url value="/vendor/sockjs/sockjs.js"/>"></script>
    <script src="<c:url value="/vendor/stomp/stomp.js"/>"></script>
</head>
<body>

<div class='nav'>
    <ul>
        <li>
            <sec:authorize access="!hasAuthority('ADMIN_PAGE_USAGE')">
                <a class='logo'
                    <%--                   href='<c:url value="/profile"--%>
                />'>
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


<c:choose>
    <c:when test="${script!=null}"> <%-- Проверка выбран ли какой-нибудь скрипт --%>

        <%-- Показывается только если есть необходимая роль --%>
        <sec:authorize access="hasAuthority('${script.name}')">
            <div class="script-params">
            <h3>${script.display_name}</h3>
            <form name='f' id="ScriptForm" onsubmit="return connect();">
                <input name="scriptName" id='name' type="hidden" value="${script.name}"/>

                <c:forEach items="${parameters}" var="parameter">
                    <div class="field">
                        <c:choose>
                            <c:when test="${parameter.type == 'list' and parameter.script == null}">
                                <label class="col-sm-2 control-label" for="${parameter.param}">
                                    <c:out value="${parameter.name}"></c:out>

                                    <select id="${parameter.param}" name="${parameter.param}"
                                            class="single ${parameter.param}"
                                            style="width: 100%; padding-left: 50px;"
                                            <c:if test="${parameter.required}">required</c:if>>
                                        <c:forEach items="${parameter.values}" var="listValue">
                                            <option value="${listValue}">${listValue}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <script>
                                    $(document).ready(function () {
                                        $(".${parameter.param}").select2({
                                            placeholder: "${parameter.name}"
                                        });
                                    });
                                </script>
                            </c:when>
                            <c:when test="${parameter.type == 'list' and parameter.script != null}">
                                <label class="col-sm-2 control-label" for="${parameter.param}">
                                    <c:out value="${parameter.name}"></c:out>

                                    <select id="${parameter.param}" name="${parameter.param}" class="${parameter.param}"
                                            style="width: 100%; padding-left: 50px;"
                                            <c:if test="${parameter.required}">required</c:if>>
                                    </select>
                                </label>
                                <script>
                                    $(document).ready(function () {
                                        var select = $('.${parameter.param}');
                                        $(".${parameter.param}").select2({
                                            placeholder: "${parameter.name}",
                                            minimumInputLength: 0,
                                            delay: 0,
                                            allowClear: true,
                                            ajax: {
                                                url: '<c:url value="/scripts/run_script_select"/>',
                                                dataType: "json",
                                                type: "GET",
                                                data: function (params) {
                                                    return {
                                                        scriptName: '${script.name}',
                                                        paramName: '${parameter.name}',
                                                        search: params.term,
                                                        formData: $('#ScriptForm').serialize()
                                                        // page: params.page || 1,

                                                    };

                                                },
                                                processResults: function (data) {
                                                    console.log(data)
                                                    return {
                                                        results: $.map(data.items, function (item) {
                                                            return {
                                                                text: item.resultValue,
                                                                id: item.resultText
                                                            }
                                                        })
                                                    };
                                                }
                                            }
                                        });
                                    });
                                </script>
                            </c:when>
                            <c:when test="${parameter.type == 'multiselect' and parameter.script == null}">
                                <label for="${parameter.description}">
                                    <c:out value="${parameter.name}"></c:out>
                                    <select name="${parameter.param}" class="multy form__field ${parameter.param}"
                                            multiple="multiple"
                                            style="width: 100%;"
                                            <c:if test="${parameter.required}">required</c:if>>
                                        <c:forEach items="${parameter.values}" var="listValue">
                                            <option value="${listValue}">${listValue}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <script>
                                    $(document).ready(function () {
                                        $(".${parameter.param}").select2({
                                            placeholder: "${parameter.name}"
                                        });
                                    });
                                </script>
                            </c:when>
                            <c:when test="${parameter.type == 'multiselect' and parameter.script != null}">
                                <label for="${parameter.description}">
                                    <c:out value="${parameter.name}"></c:out>
                                    <select name="${parameter.param}" class="${parameter.param} form__field"
                                            multiple="multiple"
                                            style="width: 100%;"
                                            <c:if test="${parameter.required}">required</c:if>>
                                    </select>
                                </label>
                                <script>
                                    $(document).ready(function () {
                                        $(".${parameter.param}").select2({
                                            placeholder: "${parameter.name}",
                                            minimumInputLength: 0,
                                            delay: 0,
                                            allowClear: true,
                                            ajax: {
                                                url: '<c:url value="/scripts/run_script_select"/>',
                                                //todo filter results
                                                dataType: "json",
                                                type: "GET",
                                                data: function (params) {
                                                    return {
                                                        scriptName: '${script.name}',
                                                        paramName: '${parameter.name}',
                                                        search: params.term,
                                                        formData: $('#ScriptForm').serialize()
                                                        // page: params.page || 1,

                                                    };
                                                },
                                                processResults: function (data) {
                                                    console.log(data)
                                                    return {
                                                        results: $.map(data.items, function (item) {
                                                            return {
                                                                text: item.resultValue,
                                                                id: item.resultText
                                                            }
                                                        })
                                                    };
                                                }
                                            }
                                        });
                                    });
                                </script>
                            </c:when>
                            <c:when test="${parameter.type == 'text'}">
                                <div class="form__group field" style="width: 100%">
                                    <input type="input" class="form__field" placeholder="Name"
                                           style="width: 100%; float: right;"
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
                                <c:out value="${parameter.name}"></c:out>
                                <input name="${parameter.param}" class="tgl tgl-ios" id="${parameter.param}"
                                       type="checkbox" <c:if test="${parameter.required}">required</c:if>/>
                                <label class="tgl-btn" for="${parameter.param}"></label>

                                <br>
                            </c:when>
                            <c:when test="${parameter.type == 'number'}">
                                <input name="${parameter.param}" type="number" id="${parameter.name}"
                                       min=${parameter.min} max=${parameter.max}
                                       <c:if test="${parameter.required}">required</c:if>>
                                <label for="${parameter.name}">${parameter.name}</label>
                                <br>
                            </c:when>
                            <c:when test="${parameter.type == 'file_upload'}">
                                <input name="${parameter.param}" type="file" size="100"
                                       <c:if test="${parameter.required}">required</c:if>/>
                                <br>
                            </c:when>
                            <c:when test="${parameter.type == 'username'}">
                            </c:when>

                            <c:otherwise>
                                | UNKNOWN "${parameter.type}" TYPE |
                                <br>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <br>
                </c:forEach>

            </form>
            <button id="runScriptButton" class="e"
                    onclick="connect('${script.name}');"
            >Run Script
            </button>
            <button id="disconnect" class="e" disabled="disabled" onclick="killScript(sessionId)">
                Stop script
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
<pre>
    <code class="script-content log-content" id="output">
        <p id="response"></p>
<%--        <div id="conversationDiv">--%>
<%--            <input type="text" id="text" placeholder="Write a message..."/>--%>
<%--            <button id="sendMessage" onclick="sendMessage('${script.name}');">Send</button>--%>
<%--        </div>--%>
    </code>
</pre>
        </sec:authorize>
        <%-- Модальные окна и связанные с ними скрипты --%>
        <div id="booleanModal" class="modal">

            <!-- Modal content -->
            <div class="modal-content" style="height: auto;overflow-y:hidden;">
                    <%--        <span class="close">&times;</span>--%>
                <p style="font-size:235%;text-align:center;" id="booleanModalText"></p>
                <button id="booleanResultYes" class="e"
                        onclick="sendMessageFromModal('${script.name}',1);closeBooleanModal();">
                    <b>Yes</b></button>
                <button id="booleanResultNo" class="e"
                        onclick="sendMessageFromModal('${script.name}',0);closeBooleanModal();">
                    <b>No</b></button>

            </div>

        </div>

        <div id="BooleanCustomModal" class="modal">

            <!-- Modal content -->
            <div class="modal-content" style="height: auto;overflow-y:hidden;">
                <p style="font-size:235%;text-align:center;" id="booleanCustomModalText"></p>
                <button id="booleanCustomYes" class="e"
                        onclick="sendMessageFromModal('${script.name}',1);closeCustomBooleanModal();"><b>Yes</b>
                </button>
                <button id="booleanCustomNo" class="e"
                        onclick="sendMessageFromModal('${script.name}',0);closeCustomBooleanModal();"><b>No</b></button>

            </div>

        </div>
        <div id="InputTextModal" class="modal">

            <!-- Modal content -->
            <div class="modal-content" style="height: auto;overflow-y:hidden;">
                <p style="font-size:235%;text-align:center;" id="InputTextModalText"></p>

                <div class="form__group field" style="width: 100%">
                    <input type="input" class="form__field" placeholder="Name"
                           style="width: 100%; float: right;"
                           name="InputTextParam" id='InputTextParamId'/>
                    <label for="InputTextParamId" class="form__label">TEXT</label>
                    <button id="InputTextButton" class="e"
                            onclick="sendMessageFromModalWithElementText('${script.name}',0,'InputTextParamId');closeInputTextModal();">
                        <b>Submit text</b></button>
                </div>

            </div>

        </div>
        <div id="TextAreaModal" class="modal">

            <!-- Modal content -->
            <div class="modal-content" style="height: auto">
                <p style="font-size:235%;text-align:center;" id="TextAreaModalText"></p>

                <textarea id="TextAreaFieldId" placeholder="Enter text and press submit below" cols="100"
                          rows="10"></textarea>
                <br>
                <button id="TextAreaButton" class="e"
                        onclick="sendMessageFromModalWithElementText('${script.name}',0,'TextAreaFieldId');closeTextAreaModal();">
                    <b>Submit text</b></button>


            </div>

        </div>
        <div id="InfoModal" class="modal">

            <!-- Modal content -->
            <div class="modal-content" style="height: auto">
                <span class="close" id="CloseInfoModal">&times;</span>
                <p style="font-size:235%;text-align:center;" id="InfoText"></p>
            </div>

        </div>
        <script type="text/javascript">
            function sendMessageFromModal(scriptName, userAnswer) {
                stompClient.send("/scriptsWS/scriptsSocket", {},
                    JSON.stringify({
                        'username': connectionName,
                        'text': userAnswer,
                        'scriptName': scriptName,
                        'uniqueSessionId': sessionId
                    }));
            }

            function sendMessageFromModalWithElementText(scriptName, userAnswer, modalId) {
                const getTextFrom = document.getElementById(modalId);
                stompClient.send("/scriptsWS/scriptsSocket", {},
                    JSON.stringify({
                        'username': connectionName,
                        'text': getTextFrom.value,
                        'scriptName': scriptName,
                        'uniqueSessionId': sessionId
                    }));
            }


            function openBooleanModal(textToShowUser) {
                // Get the modal
                const modal = document.getElementById("booleanModal");

                const textToSet = document.getElementById("booleanModalText");

                textToSet.textContent = textToShowUser

                modal.style.display = "block";
                console.log("Opened boolean Modal")
            }

            function closeBooleanModal() {
                // Get the modal
                const modal = document.getElementById("booleanModal");
                modal.style.display = "none";
                console.log("Closed boolean Modal")
            }

            function openCustomBooleanModal(textToShowUser, ans1, ans2) {
                // Get the modal
                const modal = document.getElementById("BooleanCustomModal");

                const textToSet = document.getElementById("booleanCustomModalText");

                const button1 = document.getElementById("booleanCustomYes");
                const button2 = document.getElementById("booleanCustomNo");

                textToSet.textContent = textToShowUser
                button1.textContent = ans1
                button2.textContent = ans2

                modal.style.display = "block";
                console.log("Opened custom boolean Modal")
            }

            function closeCustomBooleanModal() {
                // Get the modal
                const modal = document.getElementById("BooleanCustomModal");
                modal.style.display = "none";
                console.log("Closed custom boolean Modal")
            }

            function openInputTextModal(textToShowUser) {
                // Get the modal
                const modal = document.getElementById("InputTextModal");

                const textToSet = document.getElementById("InputTextModalText");
                // const textToSet = document.getElementById("InputTextParamId");


                textToSet.textContent = textToShowUser

                modal.style.display = "block";
                console.log("Opened InputText Modal")
            }

            function closeInputTextModal() {
                // Get the modal
                const modal = document.getElementById("InputTextModal");
                modal.style.display = "none";
                console.log("Closed InputText Modal")
            }

            function openTextAreaModal(textToShowUser) {
                // Get the modal
                const modal = document.getElementById("TextAreaModal");

                const textToSet = document.getElementById("TextAreaModalText");


                textToSet.textContent = textToShowUser

                modal.style.display = "block";
                console.log("Opened TextArea Modal")
            }

            function closeTextAreaModal() {
                // Get the modal
                const modal = document.getElementById("TextAreaModal");
                modal.style.display = "none";
                console.log("Closed TextArea Modal")
            }

            function openInfoModal(textToShowUser) {
                console.log("Opening info modal")
                // Get the modal
                const modal = document.getElementById("InfoModal");
                const span = document.getElementById("CloseInfoModal");

                const textToSet = document.getElementById("InfoText");

                textToSet.textContent = textToShowUser

                modal.style.display = "block";

                // When the user clicks on <span> (x), close the modal
                span.onclick = function () {
                    modal.style.display = "none";
                }

                // When the user clicks anywhere outside of the modal, close it
                window.onclick = function (event) {
                    if (event.target == modal) {
                        modal.style.display = "none";
                    }
                }
            }


        </script>


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
        <footer class="footer">Версия : <c:out value="${AppVersion}"/></footer>
    </c:otherwise>
</c:choose>
</body>


</html>