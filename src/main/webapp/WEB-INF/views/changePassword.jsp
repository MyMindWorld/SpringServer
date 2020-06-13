<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="th" uri="http://jakarta.apache.org/taglibs/standard/permittedTaglibs" %>
<head>
    <title>Script Server Login</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
    <meta xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="<c:url value="/images/icons/favicon.ico"/>"/>
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/Linearicons-Free-v1.0.0/icon-font.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/animate/animate.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/css-hamburgers/hamburgers.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/util.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/login.css"/>">
    <!--===============================================================================================-->
    <!--===============================================================================================-->
    <script src="vendor/jquery/jquery-3.2.1.min.js"></script>
    <!--===============================================================================================-->
    <script src="vendor/bootstrap/js/popper.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
    <!--===============================================================================================-->
    <script src="vendor/select2/select2.min.js"></script>
    <!--===============================================================================================-->
    <script src="js/main.js"></script>
</head>
<html>
<body>
<div class="container-login100" style="background-image: url(<c:url value="/images/img-01.jpg"/>);">
    <div class="wrap-login100 p-t-190 p-b-30">
        <form class="login100-form validate-form" action=
        <c:url value='/user/savePassword'/> method='POST' onsubmit="return check(document.getElementById('matchPassword'))">
            <span class="login100-form-title p-t-20 p-b-45">
						Enter new password and confirmation
					</span>
            <div class="wrap-input100 validate-input m-b-10" data-validate="Username is required">
                <input class="input100" id="password" name="newPassword" type="password" value="">
                <span class="focus-input100"></span>
                <span class="symbol-input100">
							<i class="fa fa-lock"></i>
						</span>
            </div>

            <div class="wrap-input100 validate-input m-b-10" data-validate="Password is required">
                <input class="input100" id="matchPassword" type="password" value="" oninput="check(this)">
                <span class="focus-input100"></span>
                <span class="symbol-input100">
							<i class="fa fa-lock"></i>
						</span>
            </div>
            <input id="token" name="token" style="display: none" value="${token}"/>
            <div class="container-login100-form-btn p-t-10">
                <button class="login100-form-btn" type="submit">

                    Change password
                </button>
            </div>
        </form>
    </div>
</div>
    <script language='javascript' type='text/javascript'>
        function check(input) {
            console.log(input.value)
            if (input.value == ""){
                input.setCustomValidity('Password Must be present!');
                return false
            }
            if (input.value != document.getElementById('password').value) {
                input.setCustomValidity('Passwords Must be Matching.');
                return false
            } else {
                // input is valid -- reset the error message
                input.setCustomValidity('');
            }
        }
    </script>

    <script>
        $(document).ready(function () {
            $('form').submit(function (event) {
                savePass(event);
            });
        });
    </script>
</div>
</body>
</html>
