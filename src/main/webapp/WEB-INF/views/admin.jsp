<html>

<head>
    <title>Admin</title>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
        <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <jsp:include page="init.jsp"/>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    </head>

<body>
<h1>Admin Page</h1>
<div class="header-link">
<a href="<c:url value="/index"/>"> <img src="<c:url value="/images/icons/back.png"/>" width="50" height="58"
                                       alt="ADMIN_LINK"></a>
</div>

<a href="<c:url value="/admin/update_scripts"/>" class="e">Update Scripts</a>
<a href="<c:url value="/admin/roles"/>" class="e">Create Role</a>
<a href="<c:url value="/admin/create_user"/>" class="e">Create User</a>
<a href="<c:url value="/admin/update_user"/>" class="e">Update User</a>

<%--TODO logs table--%>

<table class="content-table">
    <thead>
    <tr>
        <th>Triggered By</th>
        <th>Date</th>
        <th>Ip</th>
        <th>Action</th>
        <th>Params</th>
        <th>Error Log</th>
    </tr>
    </thead>
    <c:forEach items="${log}" var="logEntity">
    <tr>
        <td>
            <c:out value="${logEntity.triggeredBy}"/>
        </td>
        <td>
            <c:out value="${logEntity.date}"/>
        </td>
        <td>
            <c:out value="${logEntity.ip}"/>
        </td>
        <td>
            <c:out value="${logEntity.action}"/>
        </td>
        <td>
            <c:out value="${logEntity.params}"/>
        </td>
        <td>
            <c:out value="${logEntity.errorLog}"/>
        </td>

        </c:forEach>
</table>


</body>
</html>