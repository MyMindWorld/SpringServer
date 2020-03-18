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
    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility
                = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/ScriptServer/chat');
            var connectionName = document.getElementById('from').value;
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/messages/', function(messageOutput) {
                    showMessageOutput(JSON.parse(messageOutput.body));
                });
            });
        }

        function disconnect() {
            if(stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            var from = document.getElementById('from').value;
            var text = document.getElementById('text').value;
            stompClient.send("/app/chat", {},
                JSON.stringify({'from':from, 'text':text}));
        }

        function showMessageOutput(messageOutput) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": "
                + messageOutput.text + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        }
    </script>
</head>
</head>
<style>
    body {
        background-color: #eee;
    }

    label {
        display: inline-block;
        cursor: pointer;
    }
    label + label {
        margin-left: 10px;
    }

    .break {
        display: block;
        width: 100%;
    }
    .break::after {
        display: block;
        content: "";
        clear: both;
        height: 20px;
    }

    .selectbox {
        display: none;
    }

    .select-selectbox {
        position: relative;
        width: 100%;
        max-width: 300px;
        display: inline-block;
    }
    .select-selectbox .selected {
        position: relative;
        list-style: none;
        padding: 15px 50px 15px 25px;
        margin: 0;
        border: 1px solid #dedede;
        background-color: #ffffff;
    }
    .select-selectbox .selected li {
        margin: 0;
        text-align: left;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .select-selectbox .selected:hover {
        cursor: pointer;
    }
    .select-selectbox .selected::after {
        content: "";
        position: absolute;
        top: 50%;
        right: 15px;
        border: 8px solid transparent;
        transform: translateY(-4px);
        border-top-color: #dedede;
    }
    .select-selectbox .selectors {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        max-height: 0;
        overflow: auto;
        transition: 0.3s cubic-bezier(0.79, 0.18, 0.24, 0.99);
        border: 1px solid #dedede;
        border-top: 0;
        list-style: none;
        margin: 0;
        padding: 0;
        z-index: 10;
    }
    .select-selectbox .selectors li {
        padding: 15px 25px;
        text-align: left;
        margin: 0;
        transition: .3s ease;
        background-color: #ffffff;
    }
    .select-selectbox .selectors li input {
        margin: -15px -25px;
        width: calc(100% + 50px);
        padding: 15px 25px;
        border: 1px solid #dedede;
        border-left: 0;
        border-right: 0;
        background-color: #efefef;
        font-size: 16px;
        font-family: inherit;
        box-sizing: border-box;
    }
    .select-selectbox .selectors li input:focus {
        outline: 0;
    }
    .select-selectbox .selectors li:hover {
        background-color: #efefef;
        cursor: pointer;
    }
    .select-selectbox.disabled .selected {
        background-color: #dedede;
    }
    .select-selectbox.disabled .selected:after {
        border-top-color: #cacaca;
    }
    .select-selectbox.disabled .selected:hover {
        cursor: default;
    }

    .checkedbox {
        display: none;
    }
    .checkedbox ~ span.check {
        display: inline-block;
        box-sizing: border-box;
        width: 18px;
        height: 18px;
        border: 2px solid #ffffff;
        position: relative;
        top: 3px;
        margin-right: 5px;
        background-color: #ffffff;
        transition: .3s ease;
    }
    .checkedbox ~ span.check:hover {
        cursor: pointer;
    }
    .checkedbox ~ span.check::before {
        content: "";
        position: absolute;
        top: calc(50% - 1px);
        left: 50%;
        border: 0px solid #ffffff;
        width: 0;
        height: 0;
        transform: translate(-50%, -50%) rotate(45deg);
        transition: 0.2s cubic-bezier(0.79, 0.18, 0.24, 0.99), height 0s cubic-bezier(0.79, 0.18, 0.24, 0.99) 0.2s, width 0s cubic-bezier(0.79, 0.18, 0.24, 0.99) 0.2s;
        opacity: 0;
    }
    .checkedbox:checked ~ span.check {
        background-color: #00de00;
    }
    .checkedbox:checked ~ span.check::before {
        opacity: 1;
        width: 4px;
        height: 8px;
        border-right-width: 2px;
        border-bottom-width: 2px;
        transition: 0.2s cubic-bezier(0.79, 0.18, 0.24, 0.99), height 0.2s cubic-bezier(0.79, 0.18, 0.24, 0.99) 0.2s, border-right 0s cubic-bezier(0.79, 0.18, 0.24, 0.99) 0.2s;
    }
    .checkedbox:disabled ~ span.check {
        background-color: #f4f4f4;
    }
    .checkedbox:disabled:checked ~ span.check {
        background-color: #898989;
    }

    .radiobox {
        display: none;
    }
    .radiobox ~ span.check {
        display: inline-block;
        box-sizing: border-box;
        width: 18px;
        height: 18px;
        border-radius: 50%;
        border: 2px solid #ffffff;
        position: relative;
        top: 3px;
        margin-right: 3px;
        background-color: #ffffff;
        transition: .3s ease;
    }
    .radiobox ~ span.check::before {
        content: "";
        background-color: #00de00;
        width: 0;
        height: 0;
        transform: translate(-50%, -50%);
        position: absolute;
        top: 50%;
        left: 50%;
        transition: 0.3s cubic-bezier(0.79, 0.18, 0.24, 0.99);
        border-radius: 50%;
    }
    .radiobox ~ span.check:hover {
        cursor: pointer;
    }
    .radiobox:checked ~ span.check:before {
        width: 100%;
        height: 100%;
    }
    .radiobox:disabled ~ span.check {
        background-color: #f4f4f4;
        border-color: #f4f4f4;
    }
    .radiobox:disabled:checked ~ span.check:before {
        width: 100%;
        height: 100%;
        background-color: #898989;
    }

    .togglebox {
        display: none;
    }
    .togglebox ~ span.check {
        display: inline-block;
        box-sizing: border-box;
        width: 40px;
        height: 25px;
        border-radius: 20px;
        border: 2px solid #ffffff;
        position: relative;
        top: 7px;
        margin-right: 2px;
        background-color: #dedede;
        transition: 0.3s cubic-bezier(0.79, 0.18, 0.24, 0.99);
    }
    .togglebox ~ span.check:hover {
        cursor: pointer;
    }
    .togglebox ~ span.check::before {
        content: "";
        position: absolute;
        top: 50%;
        left: 2px;
        width: 18px;
        height: 18px;
        background-color: #ffffff;
        border-radius: 20px;
        box-shadow: 0 0 15px rgba(255, 255, 255, 0);
        transform: translate(0, -50%);
        transition: 0.1s cubic-bezier(0.79, 0.18, 0.24, 0.99);
        opacity: 1;
    }
    .togglebox:disabled ~ span.check:before {
        background-color: #f4f4f4;
    }
    .togglebox:checked ~ span.check {
        background-color: #00de00;
    }
    .togglebox:checked ~ span.check::before {
        left: calc(100% - 20px);
        background-color: #ffffff;
    }
    .togglebox:checked:disabled ~ span.check {
        background-color: #898989;
    }

    /*
     * Make the fields presentable.
     * This has no influence on the fields themselves.
     */
    body {
        font-family: 'Oswald', 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
        text-align: center;
        min-height: 100vh;
        height: 100%;
        padding: 45px 25px;
        background: #deefff;
        background: -moz-linear-gradient(-45deg, #deefff 0%, #98bede 100%);
        background: -webkit-linear-gradient(-45deg, #deefff 0%, #98bede 100%);
        background: linear-gradient(135deg, #deefff 0%, #98bede 100%);
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#deefff', endColorstr='#98bede',GradientType=1 );
        color: #000000;
    }

    h2 {
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-top: 45px;
        margin-bottom: 25px;
        color: #000000;
        font-size: 3em;
        text-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3);
        font-family: 'Bungee Shade';
    }
    h2:first-of-type {
        margin-top: 0;
    }

    select + span,
    select + div ul,
    input + span,
    input + div ul {
        box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3);
    }

    .select-selectbox li {
        color: #323232;
    }

</style>
<body onload="disconnect()">
<div>
    <div>
        <input type="text" id="from" placeholder="Choose a nickname"/>
    </div>
    <br />
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">
            Disconnect
        </button>
    </div>
    <br />
    <div id="conversationDiv">
        <input type="text" id="text" placeholder="Write a message..."/>
        <button id="sendMessage" onclick="sendMessage();">Send</button>
        <p id="response"></p>
    </div>
</div>
<h2>Selects</h2>
<!-- Select -->
<select class="selectbox">
    <option disabled readonly selected>Select something</option>
    <option value="1">First option</option>
    <option value="2">Second option</option>
    <option value="3">Third option</option>
    <option value="4">Fourth option is a very long option, so we can se what happens when it is selected</option>
    <option value="5">Fifth option</option>
    <option value="6">Sixth option</option>
</select>

<!-- Search Select -->
<select class="selectbox search-select">
    <option disabled readonly selected>Search-select something</option>
    <option value="1">First option</option>
    <option value="2">Second option</option>
    <option value="3">Third option</option>
    <option value="4">Fourth option</option>
    <option value="5">Fifth option</option>
    <option value="6">Sixth option</option>
</select>

<!-- Select Disabled -->
<select class="selectbox" disabled>
    <option disabled readonly selected>Disabled Select</option>
    <option value="1">First option</option>
    <option value="2">Second option</option>
    <option value="3">Third option</option>
    <option value="4">Fourth option</option>
    <option value="5">Fifth option</option>
</select>

<div class="break"></div>

<h2>Checkboxes</h2>
<!-- Checkbox -->
<label>
    <input class="checkedbox" type="checkbox" value="1">
    <span class="check"></span>
    Value
</label>
<label>
    <input class="checkedbox" type="checkbox" value="3" checked>
    <span class="check"></span>
    Checked Value
</label>
<label>
    <input class="checkedbox" type="checkbox" value="2" disabled>
    <span class="check"></span>
    Disabled Value
</label>
<label>
    <input class="checkedbox" type="checkbox" value="3" disabled checked>
    <span class="check"></span>
    Checked Disabled Value
</label>

<div class="break"></div>

<h2>Radio buttons</h2>
<!-- Checkbox -->
<label>
    <input class="radiobox" name="radio_group[]" type="radio" value="1">
    <span class="check"></span>
    Radio Value
</label>
<label>
    <input class="radiobox" name="radio_group[]" type="radio" value="2" disabled>
    <span class="check"></span>
    Disabled Radio Value
</label>
<label>
    <input class="radiobox" name="radio_group1[]" type="radio" value="3" disabled checked>
    <span class="check"></span>
    Disabled Checked Value
</label>
<label>
    <input class="radiobox" name="radio_group[]" type="radio" value="3" checked>
    <span class="check"></span>
    Checked Radio Value
</label>

<div class="break"></div>

<h2>Toggle Boxes</h2>
<!-- Checkbox -->
<label>
    <input class="togglebox" type="checkbox" value="1">
    <span class="check"></span>
    Value
</label>
<label>
    <input class="togglebox" type="checkbox" value="2" disabled>
    <span class="check"></span>
    Disabled Value
</label>
<label>
    <input class="togglebox" type="checkbox" value="2" checked disabled>
    <span class="check"></span>
    Disabled Checked
</label>
<label>
    <input class="togglebox" type="checkbox" value="3" checked>
    <span class="check"></span>
    Toggled Value
</label>

</body>
<script>
    //Selectbox
    function closeOpenSelectboxes(){
        jQuery('.selectors.open').each(function(){
            jQuery(this).removeClass('open');
            jQuery(this).css('max-height', 0);
        });

        jQuery('.selected.opened').each(function(){
            jQuery(this).removeClass('opened');
        });
    }

    jQuery('select.selectbox').after('<div class="select-selectbox"><ul class="selected"></ul><ul class="selectors"></ul></div>');
    jQuery('select.selectbox:disabled + .select-selectbox').addClass('disabled');

    jQuery('select.selectbox.search-select + .select-selectbox ul.selectors').prepend('<li class="search-select"><input class="search-select-input" type="text" placeholder="Search.."></li>');

    jQuery('.search-select-input').on('keyup', function(){
        var $this = jQuery(this),
            val = $this.val(),
            $parent = $this.closest('.selectors');

        if(val != ''){
            $parent.children('li:not(.search-select):not([data-search^="' + val + '"])').hide();
        } else {
            $parent.children('li').show();
        }
    });

    jQuery('select.selectbox option').each(function () {
        var $this = jQuery(this);
        if(!$this.attr('readonly')){
            $this.parent('select').next('.select-selectbox').children('.selectors').append('<li data-search="' + $this.text() + '" data-value="' + $this.attr('value') + '">' + $this.text() + '</li>');
        }

        if ($this.prop('selected') === true) {
            $this.parent('select').next('.select-selectbox').children('.selected').html('<li data-value="' + $this.attr('value') + '">' + $this.text() + '</li>');
        }
    });

    jQuery(document).on('click', '.select-selectbox:not(.disabled) .selectors li:not(.search-select)', function () {
        var $this = jQuery(this),
            selectSelectbox = $this.closest('.select-selectbox'),
            select = selectSelectbox.prev('select');

        selectSelectbox.find('.selected').html('<li data-value="' + $this.data('value') + '">' + $this.text() + '</li>');

        select.val($this.data('value'));
        selectSelectbox.find('.selectors').css('max-height', 0);
        selectSelectbox.find('.selected').removeClass('opened');
        select.trigger('change');

        setTimeout(function(){
            selectSelectbox.find('input').val('');
            selectSelectbox.find('li').show();
        }, 300);
    });

    jQuery(document).on('click', '.select-selectbox:not(.disabled) .selected', function (ev, el) {
        ev.stopPropagation();
        var $this = jQuery(this),
            isset = ($this.hasClass('opened') === true) ? true : false,
            height = 0,
            selectSelectbox = $this.closest('.select-selectbox');

        if(!isset){
            closeOpenSelectboxes();
        }

        $this.toggleClass('opened');
        $this.next('.selectors').toggleClass('open');

        if (!isset) {
            var round = 0;
            selectSelectbox.find('.selectors').children('li').each(function () {
                round += 1;

                if(round < 5){
                    height += jQuery(this).innerHeight();
                } else if(round == 5){
                    height += parseInt(jQuery(this).innerHeight()) / 2;
                }
            });
        }

        selectSelectbox.find('.selectors').css('max-height', height);
    });
</script>

<h2> Второй вариант для селектов</h2>
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
<script>
    $(document).ready(function() {
        $('.multy').select2();
        $('.single').select2();
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
