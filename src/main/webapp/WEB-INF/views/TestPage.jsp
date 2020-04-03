<%--
  Created by IntelliJ IDEA.
  User: VelvetPc
  Date: 11.03.2020
  Time: 23:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="./vendor/sockjs/sockjs.js"></script>
    <script src="./vendor/stomp/stomp.js"></script>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</head>
</head>
<script src="<c:url value="/vendor/jquery/jquery-2.1.3.min.js"/>"></script>
<script src="<c:url value="/vendor/select2/select2.min.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/vendor/select2/select2.min.css"/>">
<body >
<h3> Я бы взял его, он легкий, сразу кликается поиск и закрывается при клике в другое место</h3>

<%-------- SECOND -----%>
<link href="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/css/select2.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/js/select2.min.js"></script>
<div class="center">
    <select class="multy" name="states[]" multiple="multiple" style="width: 50%">
        <option value="">States</option>
        <option value="AL">Alabama</option>
        <option value="AK">Alaska</option>
        <option value="AZ">Arizona</option>
        <option value="AR">Arkansas</option>
        <option value="CA">California</option>
        <option value="OH">Ohio</option>
        <option value="OK">Oklahoma</option>
        <option value="OR">Oregon</option>
        <option value="PA">Pennsylvania</option>
        <option value="RI">Rhode Island</option>
        <option value="SC">South Carolina</option>
        <option value="SD">South Dakota</option>
        <option value="TN">Tennessee</option>
        <option value="TX">Texas</option>
        <option value="UT">Utah</option>
        <option value="VT">Vermont</option>
        <option value="VA">Virginia</option>
        <option value="WA">Washington</option>
        <option value="WV">West Virginia</option>
        <option value="WI">Wisconsin</option>
        <option value="WY">Wyoming</option>
    </select>
</div>
<div class="center">
    <select class="single" name="states[]" style="width: 50%">
        <option value="">States</option>
        <option value="AL">Alabama</option>
        <option value="AK">Alaska</option>
        <option value="AZ">Arizona</option>
        <option value="AR">Arkansas</option>
        <option value="CA">California</option>
        <option value="OH">Ohio</option>
        <option value="OK">Oklahoma</option>
        <option value="OR">Oregon</option>
        <option value="PA">Pennsylvania</option>
        <option value="RI">Rhode Island</option>
        <option value="SC">South Carolina</option>
        <option value="SD">South Dakota</option>
        <option value="TN">Tennessee</option>
        <option value="TX">Texas</option>
        <option value="UT">Utah</option>
        <option value="VT">Vermont</option>
        <option value="VA">Virginia</option>
        <option value="WA">Washington</option>
        <option value="WV">West Virginia</option>
        <option value="WI">Wisconsin</option>
        <option value="WY">Wyoming</option>
    </select>
</div>
<div class="center">
    <select class="js-example-data-ajax" name="gitRepos[]" style="width: 50%">
    </select>
</div>
<script>
    $(document).ready(function() {
        $('.multy').select2();
        $('.single').select2();
        $(".js-example-data-ajax").select2({
            placeholder: "PLACEHOLDER",
            minimumInputLength: 0,
            allowClear: true,
            ajax: {
                url: "http://127.0.0.1:8080/ScriptServer/scripts/run_script_select",
                dataType: "json",
                type: "GET",
                data: function (params) {
                    return {
                        scriptName: 'ES_Start',
                        paramName: 'Ветка'
                    };

                },
                processResults: function (data) {
                    console.log(data)
                    return {
                        results: $.map(data.items, function (item) {
                            return {
                                text: item.result,
                                id: item.id
                            }
                        })
                    };
                }
            }
        });
    });
</script>

<%------------------ чекбоксы --%>

<h2>Toggle 'em</h2>
<h3>Прикольные стоит zoom: 0.8;, иначе большие</h3>
<ul class="tg-list">
    <li class="tg-list-item">
        <h4>Light</h4>
        <input class="tgl tgl-light" id="cb1" type="checkbox"/>
        <label class="tgl-btn" for="cb1"></label>
    </li>
    <li class="tg-list-item">
        <h4>iOS</h4>
        <input class="tgl tgl-ios" id="cb2" type="checkbox"/>
        <label class="tgl-btn" for="cb2"></label>
    </li>
    <li class="tg-list-item">
        <h4>Skewed</h4>
        <input class="tgl tgl-skewed" id="cb3" type="checkbox"/>
        <label class="tgl-btn" data-tg-off="OFF" data-tg-on="ON" for="cb3"></label>
    </li>
    <li class="tg-list-item">
        <h4>Flat</h4>
        <input class="tgl tgl-flat" id="cb4" type="checkbox"/>
        <label class="tgl-btn" for="cb4"></label>
    </li>
    <li class="tg-list-item">
        <h4>Flip</h4>
        <input class="tgl tgl-flip" id="cb5" type="checkbox"/>
        <label class="tgl-btn" data-tg-off="Nope" data-tg-on="Yeah!" for="cb5"></label>
    </li>
</ul>
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
