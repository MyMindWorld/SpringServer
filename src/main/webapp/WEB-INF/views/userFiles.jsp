<html>

<head>
    <title>Admin</title>
    <%@ page contentType="text/html;charset=UTF-8" %>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
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

<h1>Files</h1>
<sec:authorize access="hasAuthority('ADMIN_PAGE_USAGE')">
    <div class="header-link">
        <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
    </div>
</sec:authorize>

<br/><br/>
<br/><br/>

<sec:authorize access="hasAuthority('FILES_UPLOAD')">
    <form name='upload_file' enctype="multipart/form-data" action=
        <c:url value='/files/upload_file'/> method='POST' onsubmit="return validateFileUpload('privileges')">

        <tr>
            <td>File to upload:</td>
            <td><input id="fileToUpload" type="file" name="file"/></td>
        </tr>

        <label class="col-sm-2 control-label" for="ScriptSelect">
            For script :

            <select id="ScriptSelect" name="ScriptSelect"
                    class="single ScriptSelect"
                    style="width: auto; padding-left: 50px;"
                    required>
                <c:forEach items="${scripts}" var="listValue">
                    <option value="${listValue.name}">${listValue.displayName}</option>
                </c:forEach>
            </select>
        </label>
        <script>
            $(document).ready(function () {
                $(".ScriptSelect").select2({
                    placeholder: "${parameter.name}"
                });
            });
        </script>

        <br/><br/>

        <input name="submit" type="submit" class="e" value="Upload"/>

    </form>
</sec:authorize>


<div class="search-box">
    <input
            id="SearchInput"
            type="text"
            class="search-bar"
            placeholder="Search..."
            onkeyup="SearchAll()"
    />
</div>

<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Script</th>
        <th>Last modified</th>
        <th>Last modified By</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${files}" var="file">
    <tr>
        <td>
            <c:out value="${file.id}"/>
        </td>
        <td>
            <c:out value="${file.name}"/>
        </td>
        <td>
            <c:out value="${file.script}"/>
        </td>
        <td>
            <c:out value="${file.lastModified}"/>
        </td>
        <td>
            <c:out value="${file.lastModifiedBy}"/>
        </td>
        <td>
            <sec:authorize access="hasAuthority('FILES_EDIT')">
                <button id="deleteUser" class="e" onclick="openModalDelete('${file.id}','${file.name}');">Delete file
                </button>
            </sec:authorize>
            <button id="deleteUser" class="e" onclick="location.href='<c:url value='/files/serve_file/${file.id}'/>'">
                Download
            </button>
        </td>
        </c:forEach>
    </tbody>
</table>

<script type="text/javascript">

    function validateFileUpload() {
        var matchingFiles = $("tr").filter(function () {
            console.log($(this).text())
            return $(this).text().includes(document.getElementById("fileToUpload").files[0].name) &&
                $(this).text().includes(document.getElementById("ScriptSelect").value);
        }).closest("tr");

        document.upload_file.action = "/ScriptServer/files/upload_file/" + document.getElementById("ScriptSelect").value

        if (document.getElementById("fileToUpload").value == '') {
            alert("Please choose file!"); // todo normal error
            return false
        }
        if (matchingFiles.length > 0) {
            return confirm("Вы уверены что хотите перезаписать файл?");
        }
        return true;
    }

</script>

<div id="deleteFileModel" class="modal">

    <!-- Modal content -->
    <div class="modal-content" style="height: auto;overflow-y: unset">
        <span class="close">&times;</span>
        <p style="font-size:235%;text-align:center;" id="DeleteFileModalWindow">'ERROR!'</p>
        <form name='f' class="form__group field" action=
        <c:url value='/files/delete_file'/> method='POST'>

            <input name="name" id='fileDeleteName' type="hidden" value=""/>
            <input name="id" id='fileIdDelete' type="hidden" value=""/>

            <input name="submit" style="align-self: center" type="submit" class="e" value="Delete File"/>


        </form>
    </div>

</div>
<script type="text/javascript">
    // Get the modal
    const modalDelete = document.getElementById("deleteFileModel");

    function openModalDelete(id, roleName) {
        modalDelete.style.display = "block";
        document.getElementById('DeleteFileModalWindow').innerHTML = "Are you sure want to delete file <b>" + roleName.toString() + "</b> ?";
        document.getElementById('fileDeleteName').value = roleName;
        document.getElementById('fileIdDelete').value = id;

        const span = document.getElementsByClassName("close")[0];

        // When the user clicks on <span> (x), close the modal
        span.onclick = function () {
            modalDelete.style.display = "none";
        }

        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function (event) {
            if (event.target == modalDelete) {
                modalDelete.style.display = "none";
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
<script type="text/javascript">

    function SearchAll() {
        let input, filter, table, tr, td, i, txtValue, td_list;

        input = document.getElementById("SearchInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("SearchableTable");
        tr = table.getElementsByTagName("tr");
        for (i = 0; i < tr.length; i++) {
            td_list = tr[i].getElementsByTagName("td");
            for (let j = 0; j < td_list.length; j++) {
                td = td_list[j];
                if (td) {
                    txtValue = td.textContent || td.innerText;
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                        break
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }

        }
    }


</script>


</body>
</html>