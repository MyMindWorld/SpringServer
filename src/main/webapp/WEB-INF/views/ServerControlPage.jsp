<html>

<head>
    <title>Admin</title>
    <meta charset="UTF-8">
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<%--    <link rel="stylesheet" type="text/css" href="<c:url value="/css/Newindex.css"/>">--%>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/ToggleButtons.css"/>">
    <!--===============================================================================================-->
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
</head>

<body>
<c:if test="${error}">
    <div class="alertError">
        <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
        <c:out value="${errorMessage}"/>
    </div>
</c:if>
<c:if test="${success}">
    <div class="alertSuccess">
        <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
        <c:out value="${successMessage}"/>
    </div>
</c:if>
<h1>Scripts</h1>
<div class="header-link">
    <div class="header-link">
        <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
    </div>
</div>

<sec:authorize access="hasAuthority('SCRIPTS_UPDATE')">
    <div style="float:right;text-align: center;">
            <h4>Maintenance</h4>
            <input class="tgl tgl-ios" id="cb2" type="checkbox"/>
            <label class="tgl-btn" for="cb2"></label>
    </div>
</sec:authorize>



<div class="search-box">
    <input
            id="SearchInput"
            type="text"
            class="search-bar"
            placeholder="Search..."
            onkeyup="Search()"
    />
    <label for="tableHeaderSearch">Search by column :</label>
    <select id="tableHeaderSearch">
        <option value="0">ID</option>
        <option value="1">Name</option>
        <option value="2">Email</option>
        <option value="3">Roles</option>
    </select>
</div>
<p> Таблица в которой будут показаны активные WS сессии \ залогининые пользователи итп</p>
<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>Running script</th>
        <th>User</th>
        <th>Session Id</th>
        <th>Action</th>
    </tr>
    </thead>
    <c:forEach items="${processMap}" var="process">
    <tr>
        <td>
            <c:out value="${process.key.scriptName}"/>
        </td>
        <td>
            <c:out value="${process.key.userName}"/>
        </td>
        <td>
            <c:out value="${process.key.sessionId}"/>
        </td>
        <td>
            <button id="notifyUserButton" class="e" onclick="openModal('${role.name}',
                    [<c:forEach items="${role.privileges}"
                                var="role_privelege">${role_privelege.id},</c:forEach>]);">Notify user
            </button>
            <button id="killProcessButton" class="e" onclick="killScript('${process.key.sessionId}','${process.key.scriptName}','${process.key.userName}');">Kill process
            </button>
        </td>
        </c:forEach>

</table>
<script>
    function killScript(uniqueSessionIdStr,scriptNameStr,userStartedScriptStr) {
        console.log(uniqueSessionIdStr)
        console.log(scriptNameStr)
        console.log(userStartedScriptStr)
        let payload = {
            scriptName:scriptNameStr,
            uniqueSessionId : uniqueSessionIdStr,
            userStartedScript : userStartedScriptStr
        }

        $.ajax({
            dataType: "json",
            method: "POST",
            url: '<c:url value="/scripts/kill_script_admin"/>',
            data: payload,
            success: function () {
                console.log("SUCCESS!");
                disconnect()
            },
            error: function (jqXHR, exception) {
                if (jqXHR.status == 200){
                    alert("Script was stopped successfully.")
                    document.location.reload(true);
                    return
                }
                console.log(jqXHR.status)
                console.log(exception)
                alert(exception);
                document.location.reload(true);
            }

        });
        return false;
    }
</script>
</html>