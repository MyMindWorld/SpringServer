<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: VelvetPc
  Date: 11.03.2020
  Time: 23:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Day planner</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="./vendor/sockjs/sockjs.js"></script>
    <script src="./vendor/stomp/stomp.js"></script>
    <meta <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>>
    <script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
    <script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-4.7.0/css/font-awesome.min.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/fonts/font-awesome-5.12.1/css/all.css"/>">
</head>

<body>
<h3>Planner</h3>
<p>Please select task, specify estimated time and press 'Add to plan'</p>
<p>When you're done feel free to press 'Copy to clipboard'</p>
<p>Additionally you can set up your personal search query for tasks. Be aware that task is shown only if you are Assignee or
    Reviewer.</p>
<input id="CustomTaskSearchQueryInput" style="width: auto" line-height="3em" value="${searchQuery}"
       onkeydown="this.style.width = ((this.value.length + 1) * 8) + 'px';"
       onclick="this.style.width = ((this.value.length + 1) * 8) + 'px';">
<button class="e"
        onclick="
                window.location.href = '<c:url
                value='/Planner'/>' + '?youtrackIssuesSearchTerm=' + document.getElementById('CustomTaskSearchQueryInput').value;
                ">
    Use custom task search query
</button>

<div width="50%">
    <label class="col-sm-2 control-label" for="AllIssuesId">
        <select id="AllIssuesId" name="AllIssuesName"
                class="single"
        <c:forEach items="${issues}" var="issue">
            <option value="${issue.idReadable}">${issue.idReadable} ${issue.summary}</option>
        </c:forEach>
        </select>
    </label>
    <script>
        $(document).ready(function () {
            $("#AllIssuesId").select2({
                placeholder: "AllIssues"
            });
        });
    </script>

    <label class="col-sm-2 control-label" for="HoursId">
        <select id="HoursId" name="HoursName"
                class="single"
                style="width: 3%; padding-left: 50px;">
            <<c:forEach var="hour" begin="0" end="8">
            <option value="${hour}">${hour}ч</option>
        </c:forEach>
        </select>
    </label>
    <script>
        $(document).ready(function () {
            $("#HoursId").select2({
                placeholder: "Select Hours"
            });
        });
    </script>

    <label class="col-sm-2 control-label" for="MinutesId">
        <select id="MinutesId" name="MinutesName"
                class="single"
                style="width: 3%; padding-left: 50px;">
            <option value="0">0м</option>
            <option value="15">15м</option>
            <option value="30">30м</option>
            <option value="45">45м</option>
        </select>
    </label>
    <script>
        $(document).ready(function () {
            $("#MinutesId").select2({
                placeholder: "Minutes"
            });
        });
    </script>
    <button class="e"
            onclick="
            let task = $('#AllIssuesId').select2('data')[0].text
            let hours = $('#HoursId').select2('data')[0].id
            let minutes = $('#MinutesId').select2('data')[0].id
            addToPlan(task,hours,minutes);
                    ">
        Add to plan
    </button>
    <button class="e"
            onclick="
                    window.open('https://youtrack.protei.ru/issue/' + $('#AllIssuesId').select2('data')[0].id);
                    ">>
        Open task
    </button>
</div>
<div width="50%">
    <button class="e"
            onclick="
            document.getElementById('CustomTaskInput').value = 'Митап '
                    ">
        Add meeting
    </button>

    <input id="CustomTaskInput" width="50%" line-height="3em">

    <label class="col-sm-2 control-label" for="HoursId">
        <select id="HoursIdInput" name="HoursNameInput"
                class="single"
                style="width: 3%; padding-left: 50px;">
            <<c:forEach var="hour" begin="0" end="8">
            <option value="${hour}">${hour}ч</option>
        </c:forEach>
        </select>
    </label>
    <script>
        $(document).ready(function () {
            $("#HoursIdInput").select2({
                placeholder: "Select Hours"
            });
        });
    </script>

    <label class="col-sm-2 control-label" for="MinutesIdInput">
        <select id="MinutesIdInput" name="MinutesNameInput"
                class="single"
                style="width: 3%; padding-left: 50px;">
            <option value="0">0м</option>
            <option value="15">15м</option>
            <option value="30">30м</option>
            <option value="45">45м</option>
        </select>
    </label>
    <script>
        $(document).ready(function () {
            $("#MinutesIdInput").select2({
                placeholder: "Minutes"
            });
        });
    </script>
    <button class="e"
            onclick="
            let task = document.getElementById('CustomTaskInput').value
            let hours = $('#HoursIdInput').select2('data')[0].id
            let minutes = $('#MinutesIdInput').select2('data')[0].id
            addToPlan(task,hours,minutes);
            document.getElementById('CustomTaskInput').value = ''
                    ">
        Add to plan
    </button>
</div>
<br/>

<div>
    <pre>
    <div class="script-content log-content" id="output"
         style="box-shadow: 0 0 10px rgba(0,0,0,0.5); background-color: darkgray; size: auto; width: 35%">
        <%
            final String DATE_FORMAT_NOW = "dd.MM.yy";
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_NOW);
            out.print("<p id='base'>План на " + df.format(new Date()) + ":<p>");
        %>
        <p id="response"></p>
    </div>
    </pre>
</div>
<div width="50%">
    <code>
        Planned: <strong id="hours">0</strong> hours and <strong id="minutes">0</strong> minutes
    </code>
</div>

<button id="copyToClip" class="e"
        onclick="
            var copyText = document.getElementById('base').innerText + '\n';
            copyText += document.getElementById('response').innerText;
            copyToClipboard(copyText)
                    ">
    Copy to clipboard
</button>
<script>
    const copyToClipboard = str => {
        const el = document.createElement('textarea');
        // el.value = str.replace(/(\r\n|\n|\r)/gm, "");
        el.value = str;
        el.setAttribute('readonly', '');
        el.style.position = 'absolute';
        el.style.left = '-9999px';
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
    };
</script>

</body>
<script>
    function addToPlan(task, hours, minutes) {
        const response = document.getElementById('response');
        const p = document.createElement('p');
        let resultText = " * [Оценка "
        // console.log(text)

        p.style.color = "black";

        // p.style.wordWrap = 'break-word';
        p.style.width = '100%';
        if (hours + minutes == 0) {
            alert("Оцените задачу!")
            return;
        }
        if (hours != 0) {
            resultText += hours + "ч";
            var currHours = getHours();
            var taskHours = parseInt(hours);
            setHours(currHours + taskHours);
        }
        if (hours != 0 & minutes != 0) {
            resultText += " " // Space between hours and minutes
        }
        if (minutes != 0) {
            resultText += minutes + "м"
            var currMinutes = getMinutes();
            var taskMinutes = parseInt(minutes);
            var res = currMinutes + taskMinutes;
            if (res >= 60) {
                setHours(getHours() + 1);
                setMinutes(res - 60);
            } else {
                setMinutes(res);
            }
        }
        resultText += "]: " + task


        const btn = document.createElement("BUTTON");   // Create a <button> element

        btn.innerHTML = "<i class=\"fa fa-trash\"></i>";                   // Insert text
        btn.addEventListener('click', function () {
            this.parentNode.parentNode.removeChild(this.parentNode);
            setHours(getHours() - hours)
            if ((getMinutes() - minutes) < 0) {
                setMinutes(60 - minutes);
                setHours(getHours() - 1);
            } else {
                setMinutes(getMinutes() - minutes)
            }
        }, false);

        p.appendChild(btn);
        p.appendChild(document.createTextNode(resultText));
        response.appendChild(p);
    }

    function getHours() {
        return parseInt(document.getElementById('hours').innerText)
    }

    function setHours(hours) {
        document.getElementById('hours').innerText = hours;
    }

    function getMinutes() {
        return parseInt(document.getElementById('minutes').innerText)
    }

    function setMinutes(hours) {
        document.getElementById('minutes').innerText = hours;
    }
</script>


</html>
