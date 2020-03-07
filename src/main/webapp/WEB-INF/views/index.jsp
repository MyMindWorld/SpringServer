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
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/Linearicons-Free-v1.0.0/icon-font.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/css-hamburgers/hamburgers.min.css"/>">
    <!--=======================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/util.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/index.css"/>">
    <!--===============================================================================================-->
</head>
<body>


<div>
    <div data-v-0012d28c="" class="app-layout">
        <div data-v-0012d28c="" class="app-sidebar collapsed">
            <div class="main-app-sidebar" data-v-0012d28c="">
                <div class="list-header">
                    <h5 class="header server-header" title="v1.15.0-HEAD@ed2009f">
                        Hello, <sec:authentication property="name"/>! </h5>
                    <%--                    <h6> You have roles : <sec:authentication property="roles"/></h6>--%>
                    <table>
                        <thead>
                        <tr>
                            <td>
                                <div data-v-3f2cf002="" class="search-panel-root">
                                    <div data-v-3f2cf002="" class="search-panel collapsed"><label>
                                        <input data-v-3f2cf002=""
                                               autocomplete="off"
                                               name="searchField"
                                               placeholder="Search script"
                                               type="search" class="search-field"
                                               disabled="disabled">
                                    </label>
                                    </div>
                                    <i class="fas fa-search fa-2x "></i>
                                </div>


                            </td>
                            <td>
                                <div class="header-link">
                                    <sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">
                                        <a href="<c:url value="/admin"/>">
                                            <i class="fas fa-user-shield fa-2x"></i>
                                        </a>

                                    </sec:authorize>


                                </div>

                            </td>
                            <td>
                                <a href="<c:url value="/perform_logout"/>" class="e">Logout</a>
                            </td>
                        </tr>
                        </thead>

                    </table>


                </div>
                <%--                Таблица слева с номерами скриптов --%>
                <div data-v-40a770e8="" class="scripts-list collection">
                    <c:forEach items="${list}" var="item">
                        <sec:authorize access="hasAuthority('${item.name}')">
                            <a data-v-40a770e8=""
                               href="<c:url value="/index/${item.name}"/>"
                               class="collection-item waves-effect waves-teal">
                                    ${item.display_name}

                                <div data-v-40a770e8="" class="menu-item-state idle"><i data-v-40a770e8=""
                                                                                        class="material-icons check-icon">check</i>
                                    <div data-v-40a770e8="" class="preloader-wrapper active">
                                        <div data-v-40a770e8="" class="spinner-layer">
                                            <div data-v-40a770e8="" class="circle-clipper left">
                                                <div data-v-40a770e8="" class="circle"></div>
                                            </div>
                                            <div data-v-40a770e8="" class="gap-patch">
                                                <div data-v-40a770e8="" class="circle"></div>
                                            </div>
                                            <div data-v-40a770e8="" class="circle-clipper right">
                                                <div data-v-40a770e8="" class="circle"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </sec:authorize>
                    </c:forEach>
                </div> <!----></div>
        </div>
        <%--        Содержимое скрипта --%>
        <div data-v-0012d28c="" class="app-content">
            <c:choose>
                <c:when test="${script!=null}"> <%-- Проверка выбран ли какой-нибудь скрипт --%>
                    <%--                    Показывается только если есть необходимая роль --%>
                    <sec:authorize access="hasAuthority('${script.name}')">

                        <h4>${script.display_name}</h4>
                        <br/>
                        <table class="content-table">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Type</th>
                                <th>Description</th>
                            </tr>
                            </thead>
                            <c:forEach items="${parameters}" var="parameter">
                            <tr>
                                <td>
                                    <c:out value="${parameter.name}"/>
                                </td>
                                <td>
                                    <c:out value="${parameter.type}"/>
                                </td>
                                <td>
                                    <c:out value="${parameter.description}"/>
                                </td>
                                </c:forEach>
                        </table>

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
                    <div data-v-0012d28c="" class="app-content">
                        <div data-v-0012d28c="" class="content-header emptyHeader"><a data-v-0012d28c=""
                                                                                      class="btn-flat app-menu-button"><i
                                data-v-0012d28c="" class="material-icons">menu</i></a>
                            <h2 data-v-0012d28c="" class="script-header header"
                                style="display: none;"></h2></div>
                        <div data-v-0012d28c="" class="content-panel">
                            <div data-v-694020c9="" class="welcome-panel" data-v-0012d28c="">
                                <img
                                        data-v-694020c9=""
                                        src="<c:url value="/images/cutLogo.png"/>"
                                        alt="script server logo">
                                <div data-v-694020c9="" class="welcome-text">
                                    Доступ к необходимым скриптам можно запросить у руководителя вашей группы<br>
                                    (Cake is a lie!)


                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>


<!--===============================================================================================-->
<script src="<c:url value="/vendor/jquery/jquery-3.2.1.min.js"/>"></script>
<!--===============================================================================================-->
<script src="<c:url value="/vendor/bootstrap/js/popper.js"/>"></script>
<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js"/>"></script>
<!--===============================================================================================-->
<script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
<!--===============================================================================================-->
<script src="<c:url value="/js/main.js"/>"></script>

</body>
</html>