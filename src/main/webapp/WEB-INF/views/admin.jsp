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

<table class="content-table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Roles</th>
    </tr>
    </thead>
    <c:forEach items="${users}" var="user">
    <tr>
        <td>
            <c:out value="${user.id}"/>
        </td>
        <td>
            <c:out value="${user.username}"/>
        </td>
        <td>
            <c:out value="${user.email}"/>
        </td>
        <td>
            <c:forEach items="${user.roles}" var="user_role">
               [ ${user_role.name} ]
            </c:forEach>
        </td>
        </c:forEach>
</table>

<form name='f' class="form__group field" action=<c:url value='/admin/invite_user'/> method='POST'>

    <input type="text" class="form__field" placeholder="Name" name="username" id='name' required/>
    <label for="name" class="form__label">Username</label>

    <c:forEach items="${roles}" var="role">
        <%--        <select name="category">--%>
        <%--            <option value="${privilege.name}">${privilege.name}</option>--%>
        <input type="checkbox" name="roleVar" id="${role.id}" value="${role.id}"/>
        <label for="${role.id}">${role.name}</label>
        <br/><br/>
        <%--        </select>--%>
    </c:forEach>

    <input name="submit" type="submit" value="Add user \ update roles"/>


</form>


</body>
</html>