<%--
  Created by IntelliJ IDEA.
  User: VelvetPc
  Date: 11.03.2020
  Time: 23:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="./vendor/sockjs/sockjs.js"></script>
    <script src="./vendor/stomp/stomp.js"></script>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
</head>
</head>
<body>

<div>
    <form method="POST" enctype="multipart/form-data" action="<c:url value='/files/upload_file'/>">
        <table>
            <tr>
                <td>File to upload:</td>
                <td><input type="file" name="file"/></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Upload"/></td>
            </tr>
        </table>
    </form>
</div>

<div>
    <ul>
        <c:forEach items="${files}" var="file">
            <li>
                <a href="${file}">${file}</a>
            </li>
        </c:forEach>

    </ul>
</div>

</body>

</html>
