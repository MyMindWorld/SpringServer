<html>
<head>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
<style><%@include file="/WEB-INF/css/style.css"%></style>
</head>

<body>
	<h1>Login</h1>

	<form name='f' action="perform_login" method='POST'>

		<table>
			<tr>
				<td>User:</td>
				<td><input type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td><input name="submit" type="submit" value="submit" /></td>
			</tr>
		</table>

	</form>
	<c:if test="${error}">
        <div class="alert">
            <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
            Login or password is incorrect!
        </div>
    </c:if>

</body>
</html>