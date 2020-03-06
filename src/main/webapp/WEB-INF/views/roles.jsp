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
<h1>Roles</h1>
<div class="header-link">
    <a href="<c:url value="/admin"/>"> <img src="<c:url value="/images/icons/back.png"/>" width="50" height="58"
                                            alt="ADMIN_LINK"></a>
</div>

<table class="content-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Privileges</th>
        <th>Users with this roles</th>
    </tr>
    </thead>
    <c:forEach items="${roles}" var="role">
    <tr>
        <td>
            <c:out value="${role.id}"/>
        </td>
        <td>
            <c:out value="${role.name}"/>
        </td>
        <td>
            <c:forEach items="${role.privileges}" var="role_privelege">
                [ <c:out value="${role_privelege.name}"/> ]
            </c:forEach>
        </td>
        <td>
            <c:forEach items="${role.users}" var="role_user">
                [ <c:out value="${role_user.username}"/> ]
            </c:forEach>
        </td>
        </c:forEach>
</table>

<form name='f' class="form__group field" action=
<c:url value='/admin/create_role'/> method='POST'>

    <input type="input" class="form__field" placeholder="Name" name="name" id='name' required/>
    <label for="name" class="form__label">Role Name</label>
    <br/><br/>

    <c:forEach items="${privileges}" var="privilege">
        <%--        <select name="category">--%>
        <%--            <option value="${privilege.name}">${privilege.name}</option>--%>
        <input type="checkbox" name="privileges" id="${privilege.id}" value="${privilege.id}"/>
        <label for="${privilege.id}"><c:out value="${privilege.name}"/></label>
        <br/><br/>
        <%--        </select>--%>
    </c:forEach>

    <input name="submit" type="submit" class="e" value="Add Role"/>

</form>


</body>
</html>