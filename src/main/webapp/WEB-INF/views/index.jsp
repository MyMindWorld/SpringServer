<!DOCTYPE html>
<html lang="en">
<head>
    <title>Script Server</title>
    <jsp:include page="init.jsp"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
    <!--=======================================================================================-->
    <%--    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/Linearicons-Free-v1.0.0/icon-font.min.css"/>">--%>
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/css-hamburgers/hamburgers.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/util.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/Newindex.css"/>">
    <!--===============================================================================================-->
    <%--    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>--%>
    <%--    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"/>--%>
    <script
            src="https://code.jquery.com/jquery-2.1.3.min.js"
            integrity="sha256-ivk71nXhz9nsyFDoYoGf2sbjrR9ddh+XDkCcfZxjvcM="
            crossorigin="anonymous"></script>
</head>
<body>
<h3>
    Hello, <sec:authentication property="name"/>!
</h3>
<div class='nav'>
    <ul>
        <li>
            <a class='logo' href='http://andytran.me'>
                <img src='https://s3-us-west-2.amazonaws.com/s.cdpn.io/169963/logo_(1).svg' width="10" height="10">
            </a>
        </li>
        <li>
            <a href='#portfolio'>Profile</a>
        </li>
        <li>
            <a href='#calendar'>
                112 ES
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <li>
            <a href='#calendar'>
                ВидеоПортал
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <li>
            <a href='#calendar'>
                Ксеон
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <li>
            <a href='#calendar'>
                NewPortal
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <li>
            <a href='#calendar'>
                RSSUI
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <li>
            <a href='#calendar'>
                MISC
                <i class='fa fa-caret-down'></i>
            </a>
            <ul class='menu'>
                <c:forEach items="${list}" var="item">
                    <sec:authorize access="hasAuthority('${item.name}')">
                        <li>
                            <a href='<c:url value="/index/${item.name}"/>'>${item.display_name}</a>
                        </li>
                    </sec:authorize>
                </c:forEach>
            </ul>
        <li/>
        <sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">
            <li>
                <a href="<c:url value="/admin"/>" style="color: grey">
                    <i class="fas fa-user-shield fa-2x"></i>
                </a>
            </li>
        </sec:authorize>
        <li>
            <a href="<c:url value="/perform_logout"/>" class="e" style="color: #ffe2ea">Logout
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </li>

    </ul>
</div>

<script>
    $(".nav li").hover(function () {
        $(this).children("ul").stop().delay(200).animate({height: "toggle", opacity: "toggle"}, 200);
    });
</script>


<%--<div>--%>
<%--    <div data-v-0012d28c="" class="app-layout">--%>
<%--        <div data-v-0012d28c="" class="app-sidebar collapsed">--%>
<%--            <div class="main-app-sidebar" data-v-0012d28c="" id="sidebar">--%>
<%--                <div class="list-header">--%>
<%--                    <h5 class="header server-header" title="v1.15.0-HEAD@ed2009f">--%>
<%--                        Hello, <sec:authentication property="name"/>! </h5>--%>
<%--                    &lt;%&ndash;                    <h6> You have roles : <sec:authentication property="roles"/></h6>&ndash;%&gt;--%>
<%--                    <table>--%>
<%--                        <thead>--%>
<%--                        <tr>--%>
<%--                            <td>--%>
<%--                                <div data-v-3f2cf002="" class="search-panel-root">--%>
<%--                                    <div data-v-3f2cf002="" class="search-panel collapsed"><label>--%>
<%--                                        <input data-v-3f2cf002=""--%>
<%--                                               autocomplete="off"--%>
<%--                                               name="searchField"--%>
<%--                                               placeholder="Search script"--%>
<%--                                               type="search" class="search-field"--%>
<%--                                               disabled="disabled">--%>
<%--                                    </label>--%>
<%--                                    </div>--%>
<%--                                    <i class="fas fa-search fa-2x " style="color: grey"></i>--%>
<%--&lt;%&ndash;                                    <i class="fas fa-search fa-2x " style="color: deepskyblue"></i>&ndash;%&gt;--%>
<%--                                </div>--%>


<%--                            </td>--%>
<%--                            <td>--%>
<%--                                <div class="header-link">--%>
<%--                                    <sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">--%>
<%--                                        <a href="<c:url value="/admin"/>" style="color: grey">--%>
<%--                                            <i class="fas fa-user-shield fa-2x"></i>--%>
<%--                                        </a>--%>

<%--                                    </sec:authorize>--%>


<%--                                </div>--%>

<%--                            </td>--%>
<%--                            <td>--%>
<%--                                <a href="<c:url value="/perform_logout"/>" class="e" style="color: grey">Logout</a>--%>
<%--                            </td>--%>
<%--                        </tr>--%>
<%--                        </thead>--%>

<%--                    </table>--%>


<%--                </div>--%>
<%--                &lt;%&ndash;                Таблица слева с номерами скриптов &ndash;%&gt;--%>
<%--                <div data-v-40a770e8="" class="scripts-list collection">--%>
<%--                    <c:forEach items="${list}" var="item">--%>
<%--                        <sec:authorize access="hasAuthority('${item.name}')">--%>
<%--                            <a data-v-40a770e8=""--%>
<%--                               href="<c:url value="/index/${item.name}"/>"--%>
<%--                               class="collection-item waves-effect waves-teal">--%>
<%--                                    ${item.display_name}--%>

<%--                                <div data-v-40a770e8="" class="menu-item-state idle"><i data-v-40a770e8=""--%>
<%--                                                                                        class="material-icons check-icon">check</i>--%>
<%--                                    <div data-v-40a770e8="" class="preloader-wrapper active">--%>
<%--                                        <div data-v-40a770e8="" class="spinner-layer">--%>
<%--                                            <div data-v-40a770e8="" class="circle-clipper left">--%>
<%--                                                <div data-v-40a770e8="" class="circle"></div>--%>
<%--                                            </div>--%>
<%--                                            <div data-v-40a770e8="" class="gap-patch">--%>
<%--                                                <div data-v-40a770e8="" class="circle"></div>--%>
<%--                                            </div>--%>
<%--                                            <div data-v-40a770e8="" class="circle-clipper right">--%>
<%--                                                <div data-v-40a770e8="" class="circle"></div>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </a>--%>
<%--                        </sec:authorize>--%>
<%--                    </c:forEach>--%>
<%--                </div> <!----></div>--%>
<%--        </div>--%>
<%--        Содержимое скрипта --%>
<%--<div data-v-0012d28c="" class="app-content">--%>
<div class="script-content">
    <c:choose>
        <c:when test="${script!=null}"> <%-- Проверка выбран ли какой-нибудь скрипт --%>
            <%--                    Показывается только если есть необходимая роль --%>
            <sec:authorize access="hasAuthority('${script.name}')">
                <h4>${script.display_name}</h4>
                <br/>

                <%--                        <table class="content-table">--%>
                <%--                            <thead>--%>
                <%--                            <tr>--%>
                <%--                                <th>Name</th>--%>
                <%--                                <th>Type</th>--%>
                <%--                                <th>Description</th>--%>
                <%--                            </tr>--%>
                <%--                            </thead>--%>
                <%--                            <c:forEach items="${parameters}" var="parameter">--%>
                <%--                            <tr>--%>
                <%--                                <td>--%>
                <%--                                    <c:out value="${parameter.name}"/>--%>
                <%--                                </td>--%>
                <%--                                <td>--%>
                <%--                                    <c:out value="${parameter.type}"/>--%>
                <%--                                </td>--%>
                <%--                                <td>--%>
                <%--                                    <c:out value="${parameter.description}"/>--%>
                <%--                                </td>--%>
                <%--                            </tr>--%>
                <%--                                </c:forEach>--%>
                <%--                        </table>--%>

                <c:forEach items="${parameters}" var="parameter">
                    <c:choose>
                        <c:when test="${parameter.type == 'list'}">
                            <select id="${parameter.name}" class="custom-select" style="width:200px;">
                                <c:forEach items="${parameter.values}" var="listValue">
                                    <option value="${listValue}">${listValue}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:when test="${parameter.type == 'text'}">
                            <input type="text" class="form__field" placeholder="${parameter.name}"
                                   name="${parameter.name}" id='${parameter.name}' required maxlength="15"/>
                            <label for="${parameter.name}" class="form__label">${parameter.name}</label>
                            <br/><br/>
                        </c:when>


                        <c:otherwise>
                            UNKNOWN TYPE
                            <br/><br/>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>


            </sec:authorize>
            <%--                   Показывается только если НЕТ необходимой роли --%>
            <sec:authorize access="!hasAuthority('${script.name}')">
                <div data-v-0012d28c="" class="app-content">
                    <div data-v-0012d28c="" class="content-header emptyHeader"><a data-v-0012d28c=""
                                                                                  class="btn-flat app-menu-button"><i
                            data-v-0012d28c="" class="material-icons">menu</i></a>
                        <h2 data-v-0012d28c="" class="script-header header"
                            style="display: none;"></h2></div>
                    <div data-v-0012d28c="" class="content-panel">
                        <div data-v-694020c9="" class="welcome-panel" data-v-0012d28c="">
                            <div data-v-694020c9="" class="welcome-text">
                                У вас отсутсвуют права на выполнение запрошенного скрипта.
                                <br>
                                Обратитесь к руководителю вашей группы.


                            </div>
                        </div>
                    </div>
                </div>
            </sec:authorize>

        </c:when>
        <c:otherwise> <%-- Если не выбран, дефолтная страница --%>
            <img src="<c:url value="/images/cutLogo.png"/>" alt="script server logo">
            <div>
                Доступ к необходимым скриптам можно запросить у руководителя вашей группы<br>
                (Cake is a lie!)
            </div>
        </c:otherwise>
    </c:choose>

</div>
</div>
</div>

<script type="text/javascript">
    function SetMenuVisible() {
        let e = document.getElementById("sidebar");
        e.style.display = 'none'
        console.log("Done2")

    }
</script>


<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/jquery/jquery-3.2.1.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/bootstrap/js/popper.js"/>"></script>--%>
<%--<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>--%>
<!--===============================================================================================-->
<%--<script src="<c:url value="/js/main.js"/>"></script>--%>

</body>
</html>