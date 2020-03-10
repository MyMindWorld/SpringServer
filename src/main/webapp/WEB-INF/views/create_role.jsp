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
<h1>Create Role</h1>
<div class="header-link">
    <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
</div>

<form name='f' class="form__group field" action=
<c:url value='/admin/create_role'/> method='POST' onsubmit="return validateAddRoleForm()">

    <input type="input" class="form__field" placeholder="Name" name="name" id='name' required maxlength="20" pattern="^[a-z0-9_-]{3,20}$" title="3-20 characters, underscore or dash"/>
    <label for="name" class="form__label">Role Name</label>
    <br/><br/>

    <c:forEach items="${privileges}" var="privilege">
        <input type="checkbox" name="privileges" id="${privilege.id}" value="${privilege.id}"/>
        <label for="${privilege.id}"><c:out value="${privilege.name}"/></label>
        <br/><br/>
    </c:forEach>

    <input name="submit" type="submit" class="e" value="Add Role"/>

</form>


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

<script type="text/javascript">

    function validateAddRoleForm() {
        let roleCheckboxes = document.getElementsByName("privileges");
        for (const roleCheckbox of roleCheckboxes) {
            if (roleCheckbox.checked == true){
                return true
            }
        }
        alert("Please choose privileges!");
        return false


    }

</script>


</body>
</html>