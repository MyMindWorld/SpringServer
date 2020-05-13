<html>

<head>
    <title>Admin</title>
    <%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
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
<h1>Roles</h1>
<div class="header-link">
    <a href="<c:url value="/admin"/>" class="e"><i class="far fa-arrow-alt-circle-left"></i> Back </a>
</div>

<form name='f' action=
<c:url value='/admin/create_role'/> method='POST' onsubmit="return validateAddRoleForm('privileges')">
    <div class="form__group field">
        <input type="input" class="form__field" placeholder="Name" name="name" id='name' required maxlength="20"
               pattern="^[a-zA-Z0-9_-]{3,20}$" title="ONLY 3-20 Latin characters, underscore or dash"/>
        <label for="name" class="form__label">Role Name</label>
    </div>
    <br/><br/>
    <fieldset class="group">
        <legend>Select Privileges</legend>
        <ul class="checkbox">
            <c:forEach items="${privileges}" var="privilege">
                <li>
                    <input type="checkbox" name="privileges" id="${privilege.name}" value="${privilege.id}"/>
                    <label for="${privilege.name}"><c:out value="${privilege.name}"/></label>
                </li>
            </c:forEach>
        </ul>
    </fieldset>

    <input name="submit" type="submit" class="e" value="Add Role"/>

</form>
<button id="selectAllButton" class="e" onclick="selectAllCheckBoxesModal('privileges');"> Select All
</button>
<button id="deSelectAllButton" class="e" onclick="deSelectAllCheckBoxesModal('privileges');"> DeSelect All
</button>

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
        <option value="1">Name</option>
        <option value="2">Privileges</option>
        <option value="3">Users with this roles</option>
    </select>
</div>

<table class="content-table" id="SearchableTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Privileges</th>
        <th>Users with this roles</th>
        <th>Action</th>
    </tr>
    </thead>
    <c:forEach items="${roles}" var="role">
    <tr>
        <td>
            <c:out value="${role.id}"/>
        </td>
        <td>
            <c:out value="${role.name}"/>
        </td>
        <td>
            <c:forEach items="${role.privileges}" var="role_privelege">
                [ <c:out value="${role_privelege.name}"/> ]
            </c:forEach>
        </td>
        <td>
            <c:forEach items="${role.users}" var="role_user">
                [ <c:out value="${role_user.username}"/> ]
            </c:forEach>
        </td>
        <td>
            <button id="updateUserButton" class="e" onclick="openModal('${role.name}',
                    [<c:forEach items="${role.privileges}"
                                var="role_privelege">${role_privelege.id},</c:forEach>]);">Update
                role
            </button>
            <button id="deleteUser" class="e" onclick="openModalDelete('${role.id}','${role.name}');">Delete role
            </button>
        </td>
        </c:forEach>
</table>

<script type="text/javascript">

    function validateAddRoleForm(checkboxesName) {
        let roleCheckboxes = document.getElementsByName(checkboxesName);
        for (const roleCheckbox of roleCheckboxes) {
            if (roleCheckbox.checked == true) {
                return true
            }
        }
        alert("Please choose privileges!");
        return false
    }

</script>
<div id="updateRoleModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content" style="height: auto">
        <span class="close">&times;</span>
        <p style="font-size:235%;text-align:center;" id="ModalWindowRoleName">Updating HERE SHOULD BE USERNAME roles</p>
        <button id="selectAllModalButton" class="e" onclick="selectAllCheckBoxesModal('privilegesUpdate');"> Select All
        </button>
        <button id="deSelectAllModalButton" class="e" onclick="deSelectAllCheckBoxesModal('privilegesUpdate');">
            DeSelect All
        </button>
        <form name='f' action=
        <c:url value='/admin/update_role'/> method='POST' onsubmit="return validateAddRoleForm('privilegesUpdate')">

            <input name="name" id='roleName' type="hidden" value=""/>
            <div class="form__group field">
                <input type="input" class="form__field" placeholder="Name" name="nameNew" id='roleNameNew'
                       maxlength="20"
                       pattern="^[a-zA-Z0-9_-]{3,20}$" title="ONLY 3-20 Latin characters, underscore or dash"/>
                <label for="roleNameNew" class="form__label">New Role Name</label>
            </div>


            <fieldset class="group">
                <legend>Select Privileges</legend>
                <ul class="checkbox">
                    <c:forEach items="${privileges}" var="privilege">
                        <li>
                            <input type="checkbox" name="privilegesUpdate" id="${privilege.id}"
                                   value="${privilege.id}"/>
                            <label for="${privilege.id}"><c:out value="${privilege.name}"/></label>
                        </li>
                    </c:forEach>
                </ul>
            </fieldset>
            <input name="submit" type="submit" class="e" value="Update Role"/>


        </form>
    </div>

</div>
<script type="text/javascript">
    // Get the modal
    const modal = document.getElementById("updateRoleModal");

    // Get the <span> element that closes the modal
    const span = document.getElementsByClassName("close")[0];


    function openModal(roleName, privileges) {
        modal.style.display = "block";
        console.log(roleName)
        console.log(privileges)
        let privilegeCheckboxes = document.getElementsByName("privilegesUpdate");
        for (const privCheckbox of privilegeCheckboxes) {
            privCheckbox.checked = false;
        }
        for (const privilege of privileges) {
            document.getElementById(privilege).checked = true;
        }
        document.getElementById('ModalWindowRoleName').innerHTML = "Updating <b>" + roleName + "</b> privileges";
        document.getElementById('roleName').value = roleName;

        // When the user clicks on <span> (x), close the modal
        span.onclick = function () {
            modal.style.display = "none";
        }

        // When the user clicks anywhere outside of the modal, close it
        window.onclick = function (event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }

    }

    function selectAllCheckBoxesModal(checkboxesName) {
        let privilegeCheckboxes = document.getElementsByName(checkboxesName);
        for (const privCheckbox of privilegeCheckboxes) {
            privCheckbox.checked = true;
        }
    }

    function deSelectAllCheckBoxesModal(checkboxesName) {
        let privilegeCheckboxes = document.getElementsByName(checkboxesName);
        for (const privCheckbox of privilegeCheckboxes) {
            privCheckbox.checked = false;
        }
    }


</script>
<div id="deleteUserModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content" style="height: auto">
        <span class="close">&times;</span>
        <p style="font-size:235%;text-align:center;" id="DeleteModalWindowRole">'ERROR!'</p>
        <form name='f' class="form__group field" action=
        <c:url value='/admin/delete_role'/> method='POST'>

            <input name="name" id='roleDeleteName' type="hidden" value=""/>
            <input name="id" id='roleIdDelete' type="hidden" value=""/>

            <input name="submit" style="align-self: center" type="submit" class="e" value="Delete Role"/>


        </form>
    </div>

</div>
<script type="text/javascript">
    // Get the modal
    const modalDelete = document.getElementById("deleteUserModal");

    function openModalDelete(id, roleName) {
        modalDelete.style.display = "block";
        document.getElementById('DeleteModalWindowRole').innerHTML = "Are you sure want to delete role <b>" + roleName.toString() + "</b> ?";
        document.getElementById('roleDeleteName').value = roleName;
        document.getElementById('roleIdDelete').value = id;

        const span = document.getElementsByClassName("close")[1];

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