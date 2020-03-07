<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<jsp:include page="../views/init.jsp"/>--%>
<%--<style>--%>
<%--    <%@include file="/css/elements.css" %>--%>
<%--/*</style>*/--%>
<form name='f' class="form__group field">
<input type="checkbox" name="params" id="${param.id}" value="${param.id}"/>
<label for="${param.id}"><c:out value="${param.name}"/></label>
</form>