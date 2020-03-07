<html>

<head>
    <title>Admin</title>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <jsp:include page="init.jsp"/>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <style>
        <%@include file="/css/style.css" %>
    </style>
</head>

<body>
<h1>Admin Page</h1>
<div class="header-link">
    <a href="<c:url value="/index"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Home </a>
</div>
<sec:authorize access="hasAuthority('SCRIPTS_UPDATE')">
    <a href="<c:url value="/admin/update_scripts"/>" class="e">Update Scripts</a>
</sec:authorize>
<sec:authorize access="hasAuthority('ROLES_SETTING')">
    <a href="<c:url value="/admin/create_role"/>" class="e">Create Role</a>
    <a href="<c:url value="/admin/create_user"/>" class="e">Create User</a>
    <a href="<c:url value="/admin/update_user"/>" class="e">Update User Role</a>
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
        <option value="0">Triggered By</option>
        <option value="1">Date</option>
        <option value="2">Ip</option>
        <option value="3">Action</option>
        <option value="4">Params</option>
        <option value="5">Error Log</option>
    </select>
</div>


<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>Triggered By</th>
        <th>Date</th>
        <th>Ip</th>
        <th>Action</th>
        <th>Params</th>
        <th>Error Log</th>
    </tr>
    </thead>
    <c:forEach items="${log}" var="logEntity">
        <tr>
            <td>
                <c:out value="${logEntity.triggeredBy}"/>
            </td>
            <td>
                <c:out value="${logEntity.date}"/>
            </td>
            <td>
                <c:out value="${logEntity.ip}"/>
            </td>
            <td>
                <c:out value="${logEntity.action}"/>
            </td>
            <td>
                <c:out value="${logEntity.params}"/>
            </td>
            <td>
                <c:out value="${logEntity.errorLog}"/>
            </td>
        </tr>

    </c:forEach>
</table>

<script type="text/javascript">
    console.log("Sort enabling start");
    const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

    const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
            v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
    )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

    document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
        const table = th.closest('table');
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
            .forEach(tr => table.appendChild(tr));
    })));
    console.log("Sort Enable Done!");

</script>
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


</body>
</html>