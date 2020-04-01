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
    <a href="<c:url value="/admin/update_scripts"/>" class="e" style="width:120px;">Update Scripts</a>
    <a href="<c:url value="/admin/update_scripts_and_drop_venv"/>" class="e" style="width:200px">Update Scripts & clear venv</a>
    <a href="<c:url value="/admin/update_scripts_from_gitlab"/>" class="e" style="width:200px">Update Scripts from GitLab</a>
    <br>
    <br>
</sec:authorize>


<div class="search-box" style="margin-top: unset">
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
    <c:forEach items="${scriptsList}" var="script">
    <tr>
        <td>
            <c:out value="${script.name}"/>
        </td>
        <td>
            <c:out value="${script.group_name}"/>
        </td>
        <td>
            <c:out value="${script.display_name}"/>
        </td>
        <td>
            <c:out value="${script.python_version}"/>
        </td>
        <td>
            <c:out value="${script.venv}"/>
        </td>
        <td>
            <c:out value="${script.requirements}"/>
        </td>
        <td>
            <button id="updateScript" class="e" onclick="openModalUpdate('${script.name}','${script.display_name}');">Update config</button>
        </td>
        </c:forEach>
</table>


<div id="updateScriptModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content" style="height: auto">
        <span class="close">X</span>
        <p style="font-size:235%;text-align:center;" id="modalText">'IF YOU SEE THIS,ITS A BUG!</p>
        <form name='f' class="form__group field" action=
        <c:url value='/admin/update_script'/> method='POST'>

            <input name="name" id="scriptToUpdate" type="hidden" value=""/>

            <input name="submit" type="submit" class="e" value="Update script config and clear venv"/>

        </form>
    </div>

</div>


</body>
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
<script type="text/javascript">
    // Get the modal
    const modalUpdate = document.getElementById("updateScriptModal");

    function openModalUpdate(script_name, script_display_name) {
        modalUpdate.style.display = "block";
        if (script_name != null){
            document.getElementById('modalText').innerHTML = "Are you sure want to update Script <b>" + script_display_name.toString() + "</b> ?";
        }
        document.getElementById('scriptToUpdate').value = script_name;

        const span = document.getElementsByClassName("close")[0];

        // When the user clicks on <span> (x), close the modal
        span.onclick = function () {
            modalUpdate.style.display = "none";
        }

        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function (event) {
            if (event.target == modalUpdate) {
                modalUpdate.style.display = "none";
            }
        }

    }
</script>
<script type="text/javascript">
    console.log("Sort enabling start");
    const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

    const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
            v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
    )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

    document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
        const table = th.closest('table');
        Array.from(table.querySelectorAll('tbody tr'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
            .forEach(tr => table.querySelector("tbody").appendChild(tr));
    })));
    console.log("Sort Enable Done!");

</script>
</html>