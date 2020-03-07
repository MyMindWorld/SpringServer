<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Script Server Login</title>
    <jsp:include page="init.jsp"/>
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
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/main.css"/>">
    <!--===============================================================================================-->
</head>
<c:if test="${error}">
    <div class="alertError">
        <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
        Login or password is incorrect!
    </div>
</c:if>
<body>


<div class="limiter">

    <div class="container-login100" style="background-image: url('images/img-01.jpg');">
        <div class="wrap-login100 p-t-190 p-b-30">
            <form class="login100-form validate-form" action="perform_login" method="post">
                <div class="login100-form-avatar">
                    <img src="images/cutLogo.png" alt="ERROR">
                </div>

                <span class="login100-form-title p-t-20 p-b-45">
						
					</span>

                <div class="wrap-input100 validate-input m-b-10" data-validate="Username is required">
                    <input class="input100" type="text" name="username" placeholder="Username">
                    <span class="focus-input100"></span>
                    <span class="symbol-input100">
							<i class="fa fa-user"></i>
						</span>
                </div>

                <div class="wrap-input100 validate-input m-b-10" data-validate="Password is required">
                    <input class="input100" type="password" name="password" placeholder="Password">
                    <span class="focus-input100"></span>
                    <span class="symbol-input100">
							<i class="fa fa-lock"></i>
						</span>
                </div>

                <div class="container-login100-form-btn p-t-10">
                    <button class="login100-form-btn" type="submit">

                        Login
                    </button>
                </div>

                <div class="text-center w-full p-t-25 p-b-230">
                    <a onclick="openModal()">
                        Forgot Username / Password?
                    </a>
                </div>

                <div class="text-center w-full">
                    <a class="txt1" href="#">
                        Create new account
                        <i class="fa fa-long-arrow-right"></i>
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="updateUserModal" class="modal">

    <!-- Modal content -->
    <div class="modal-content">
        <span class="close">&times;</span>
<%--        <h3><i class="fa fa-lock fa-4x"></i></h3>--%>
        <h2 class="text-center">Forgot Password?</h2>
        <p>You can reset your password here.</p>

        <form name='forgotPassword' class="form__group field" action=
        <c:url value='/forgotPassword'/> method='POST'>

            <fieldset>
                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-envelope color-blue"></i></span>

                        <input id="emailInput" placeholder="email address" class="form-control" type="email"
                               oninvalid="setCustomValidity('Please enter a valid email address!')"
                               onchange="try{setCustomValidity('')}catch(e){}" required="">
                    </div>
                </div>
                <div class="form-group">
                    <input class="btn btn-lg btn-primary btn-block" value="Send My Password" type="submit">
                </div>
            </fieldset>

        </form>
    </div>

</div>

<script type="text/javascript">
    // Get the modal
    const modal = document.getElementById("updateUserModal");

    // Get the <span> element that closes the modal
    const span = document.getElementsByClassName("close")[0];


    function openModal(e) {
        modal.style.display = "block";

    }

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
</script>


<!--===============================================================================================-->
<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/bootstrap/js/popper.js"></script>
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
<script src="js/main.js"></script>

</body>
</html>