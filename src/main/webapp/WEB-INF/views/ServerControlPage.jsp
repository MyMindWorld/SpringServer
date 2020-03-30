<html>

<head>
    <title>Admin</title>
    <meta charset="UTF-8">
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<h1>Scripts</h1>
<div class="header-link">
    <div class="header-link">
        <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
    </div>
</div>

<sec:authorize access="hasAuthority('SCRIPTS_UPDATE')">
    <div style="float:right;text-align: center;">
            <h4>Maintenance</h4>
            <input class="tgl tgl-ios" id="cb2" type="checkbox"/>
            <label class="tgl-btn" for="cb2"></label>
    </div>
</sec:authorize>



<div class="search-box">
    <input
            id="SearchInput"
            type="text"
            class="search-bar"
            placeholder="Search..."
            onkeyup="Search()"
    />
    <label for="tableHeaderSearch">Search by column :</label>
    <select id="tableHeaderSearch">
        <option value="0">ID</option>
        <option value="1">Name</option>
        <option value="2">Email</option>
        <option value="3">Roles</option>
    </select>
</div>
<p> Таблица в которой будут показаны активные WS сессии \ залогининые пользователи итп</p>
<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>Name</th>
        <th>Group</th>
        <th>DisplayName</th>
        <th>Python</th>
        <th>Venv</th>
        <th>Requirements</th>
        <th>Action</th>
    </tr>
    </thead>

</table>
<style>
    .tgl {
        display: none;
    }
    .tgl, .tgl:after, .tgl:before, .tgl *, .tgl *:after, .tgl *:before, .tgl + .tgl-btn {
        box-sizing: border-box;
    }
    .tgl::-moz-selection, .tgl:after::-moz-selection, .tgl:before::-moz-selection, .tgl *::-moz-selection, .tgl *:after::-moz-selection, .tgl *:before::-moz-selection, .tgl + .tgl-btn::-moz-selection {
        background: none;
    }
    .tgl::selection, .tgl:after::selection, .tgl:before::selection, .tgl *::selection, .tgl *:after::selection, .tgl *:before::selection, .tgl + .tgl-btn::selection {
        background: none;
    }
    .tgl + .tgl-btn {
        outline: 0;
        display: block;
        width: 4em;
        height: 2em;
        position: relative;
        cursor: pointer;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
        zoom: 0.8;
    }
    .tgl + .tgl-btn:after, .tgl + .tgl-btn:before {
        position: relative;
        display: block;
        content: "";
        width: 50%;
        height: 100%;
    }
    .tgl + .tgl-btn:after {
        left: 0;
    }
    .tgl + .tgl-btn:before {
        display: none;
    }
    .tgl:checked + .tgl-btn:after {
        left: 50%;
    }

    .tgl-light + .tgl-btn {
        background: #f0f0f0;
        border-radius: 2em;
        padding: 2px;
        -webkit-transition: all .4s ease;
        transition: all .4s ease;
    }
    .tgl-light + .tgl-btn:after {
        border-radius: 50%;
        background: #fff;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
    }
    .tgl-light:checked + .tgl-btn {
        background: #9FD6AE;
    }

    .tgl-ios + .tgl-btn {
        background: #fbfbfb;
        border-radius: 2em;
        padding: 2px;
        -webkit-transition: all .4s ease;
        transition: all .4s ease;
        border: 1px solid #e8eae9;
    }
    .tgl-ios + .tgl-btn:after {
        border-radius: 2em;
        background: #fbfbfb;
        -webkit-transition: left 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), padding 0.3s ease, margin 0.3s ease;
        transition: left 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), padding 0.3s ease, margin 0.3s ease;
        box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1), 0 4px 0 rgba(0, 0, 0, 0.08);
    }
    .tgl-ios + .tgl-btn:hover:after {
        will-change: padding;
    }
    .tgl-ios + .tgl-btn:active {
        box-shadow: inset 0 0 0 2em #e8eae9;
    }
    .tgl-ios + .tgl-btn:active:after {
        padding-right: .8em;
    }
    .tgl-ios:checked + .tgl-btn {
        background: #86d993;
    }
    .tgl-ios:checked + .tgl-btn:active {
        box-shadow: none;
    }
    .tgl-ios:checked + .tgl-btn:active:after {
        margin-left: -.8em;
    }

    .tgl-skewed + .tgl-btn {
        overflow: hidden;
        -webkit-transform: skew(-10deg);
        transform: skew(-10deg);
        -webkit-backface-visibility: hidden;
        backface-visibility: hidden;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        font-family: sans-serif;
        background: #888;
    }
    .tgl-skewed + .tgl-btn:after, .tgl-skewed + .tgl-btn:before {
        -webkit-transform: skew(10deg);
        transform: skew(10deg);
        display: inline-block;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        width: 100%;
        text-align: center;
        position: absolute;
        line-height: 2em;
        font-weight: bold;
        color: #fff;
        text-shadow: 0 1px 0 rgba(0, 0, 0, 0.4);
    }
    .tgl-skewed + .tgl-btn:after {
        left: 100%;
        content: attr(data-tg-on);
    }
    .tgl-skewed + .tgl-btn:before {
        left: 0;
        content: attr(data-tg-off);
    }
    .tgl-skewed + .tgl-btn:active {
        background: #888;
    }
    .tgl-skewed + .tgl-btn:active:before {
        left: -10%;
    }
    .tgl-skewed:checked + .tgl-btn {
        background: #86d993;
    }
    .tgl-skewed:checked + .tgl-btn:before {
        left: -100%;
    }
    .tgl-skewed:checked + .tgl-btn:after {
        left: 0;
    }
    .tgl-skewed:checked + .tgl-btn:active:after {
        left: 10%;
    }

    .tgl-flat + .tgl-btn {
        padding: 2px;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        background: #fff;
        border: 4px solid #f2f2f2;
        border-radius: 2em;
    }
    .tgl-flat + .tgl-btn:after {
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        background: #f2f2f2;
        content: "";
        border-radius: 1em;
    }
    .tgl-flat:checked + .tgl-btn {
        border: 4px solid #7FC6A6;
    }
    .tgl-flat:checked + .tgl-btn:after {
        left: 50%;
        background: #7FC6A6;
    }

    .tgl-flip + .tgl-btn {
        padding: 2px;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        font-family: sans-serif;
        -webkit-perspective: 100px;
        perspective: 100px;
    }
    .tgl-flip + .tgl-btn:after, .tgl-flip + .tgl-btn:before {
        display: inline-block;
        -webkit-transition: all .4s ease;
        transition: all .4s ease;
        width: 100%;
        text-align: center;
        position: absolute;
        line-height: 2em;
        font-weight: bold;
        color: #fff;
        position: absolute;
        top: 0;
        left: 0;
        -webkit-backface-visibility: hidden;
        backface-visibility: hidden;
        border-radius: 4px;
    }
    .tgl-flip + .tgl-btn:after {
        content: attr(data-tg-on);
        background: #02C66F;
        -webkit-transform: rotateY(-180deg);
        transform: rotateY(-180deg);
    }
    .tgl-flip + .tgl-btn:before {
        background: #FF3A19;
        content: attr(data-tg-off);
    }
    .tgl-flip + .tgl-btn:active:before {
        -webkit-transform: rotateY(-20deg);
        transform: rotateY(-20deg);
    }
    .tgl-flip:checked + .tgl-btn:before {
        -webkit-transform: rotateY(180deg);
        transform: rotateY(180deg);
    }
    .tgl-flip:checked + .tgl-btn:after {
        -webkit-transform: rotateY(0);
        transform: rotateY(0);
        left: 0;
        background: #7FC6A6;
    }
    .tgl-flip:checked + .tgl-btn:active:after {
        -webkit-transform: rotateY(20deg);
        transform: rotateY(20deg);
    }

</style>

</html>