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





<form name='f' onsubmit="return validateAddUserForm()" class="form__group field" action=<c:url value='/admin/invite_user'/> method='POST' >


    <input type="text" class="form__field" placeholder="Name" name="username" id='name' required/>
    <label for="name" class="form__label">Username</label>

    <c:forEach items="${roles}" var="role">
        <input type="checkbox" name="roleVar" id="${role.id}" value="${role.id}"/>
        <label for="${role.id}"><c:out value="${role.name}"/></label>
        <br/><br/>
    </c:forEach>

    <input name="submit" type="submit" class="e" value="Add user"/>


</form>


</body>
<script type="text/javascript">
    
    function validateAddUserForm() {
        let roleCheckboxes = document.getElementsByName("roleVar");
        for (const roleCheckbox of roleCheckboxes) {
            if (roleCheckbox.checked == true){
                return true
            }
        }
        alert("Please choose roles!");
        return false


    }

</script>
</html>