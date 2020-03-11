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
<body>
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
</html>
