<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<label for="${param.name}">${param.name}</label>
<select id="${param.name}">
    <c:forEach items="${param.listValues}" var="listValue">
    <option value="${listValue}">${listValue}</option>
    </c:forEach>
</select>
<c:forEach items="${paramValues}" var="valuea">
    ${valuea.key} = ${paramValues.valuea}
    <br>
</c:forEach>
