<html>

<head>
    <title>Admin</title>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <jsp:include page="init.jsp"/>
    <style>
        <%@include file="/css/style.css" %>
        <%@include file="/js/main.js" %>
    </style>
    <style>
        /* The Modal (background) */
        .modal {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1; /* Sit on top */
            padding-top: 100px; /* Location of the box */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgb(0,0,0); /* Fallback color */
            background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
            overflow-x: hidden;
            overflow-x: auto;
        }

        /* Modal Content */
        .modal-content {
            background-color: #fefefe;
            margin: auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            height: 80%;
            overflow: auto;
            overflow-y: scroll;
            text-align:justify;
        }

        /* The Close Button */
        .close {
            color: #aaaaaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }

        .close:hover,
        .close:focus {
            color: #000;
            text-decoration: none;
            cursor: pointer;
        }
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
<h1>Update User Roles</h1>
<div class="header-link">
    <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
</div>

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

<script type="text/javascript">
    function Search() {
        let input, filter, table, tr, td, i, txtValue;
        let e = document.getElementById("tableHeaderSearch");
        let searchBy = e.options[e.selectedIndex].value;

        input = document.getElementById("SearchInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("SearchableTable");
        tr = table.getElementsByTagName("tr");
        for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[searchBy];
            if (td) {
                txtValue = td.textContent || td.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }
</script>

<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Roles</th>
        <th>Action</th>
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
                [ <c:out value="${user_role.name}"/> ]
            </c:forEach>
        </td>
        <td>
            <button id="updateUserButton" class="e" onclick="openModal(${user.id},
                    [<c:forEach items="${user.roles}" var="user_role">${user_role.id},</c:forEach>],'${user.username}');">Update roles</button>
        </td>
        </c:forEach>
</table>

<%--MODAL SHIT--%>

<!-- The Modal -->
<div id="updateUserModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content">
        <span class="close">&times;</span>
        <p style="font-size:235%;text-align:center;"id="ModalWindowUsername">Updating HERE SHOULD BE USERNAME roles</p>
        <form name='f' class="form__group field" action=
        <c:url value='/admin/update_user'/> method='POST'>

            <input name="username" id='username' type="hidden" value=""/>

            <c:forEach items="${roles}" var="role">
                <input type="checkbox" name="roleVar" id="${role.id}" value="${role.id}"/>
                <label for="${role.id}"><c:out value="${role.name}"/></label>
                <br/><br/>
            </c:forEach>

            <input name="submit" type="submit" class="e" value="Update Roles"/>

        </form>
    </div>

</div>
<script type="text/javascript">
    // Get the modal
    const modal = document.getElementById("updateUserModal");

    // Get the <span> element that closes the modal
    const span = document.getElementsByClassName("close")[0];


    function openModal (id,roles,username){
        modal.style.display = "block";
        console.log(id)
        console.log(roles)
        let roleCheckboxes = document.getElementsByName("roleVar");
        for (const roleCheckbox of roleCheckboxes) {
            roleCheckbox.checked = false;
        }
        for (const role of roles) {
            document.getElementById(role).checked = true;
        }
        document.getElementById('ModalWindowUsername').innerHTML = "Updating <b>" + username.toString() + "</b> roles";
        document.getElementById('username').value = username;

    }

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
</script>

</body>
</html>