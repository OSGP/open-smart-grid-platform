<%--

    Copyright 2015 Smart Society Services B.V.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

--%>
<!DOCTYPE html>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><spring:message
        code="org.opensmartgridplatform.webdevicesimulator.web.title" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="/web-device-simulator/static/css/bootstrap.min.css"
    rel="stylesheet">
<style>
body {
	padding-top: 10px;
	padding-bottom: 10px;
}

.font-size13 {
	font-size: 13px;
}

.control-label {
	float: left;
}

.controls {
	margin-left: 160px;
}

.btn-group {
	margin-bottom: 10px;
}

.btn-group-controls {
	margin-left: 160px;
	border-bottom: 1px solid rgb(229, 229, 229);
	padding-bottom: 10px;
}

.btn-group+.btn-group {
	margin-left: 0px;
}

.btn-group .controls input, .btn-group .controls select {
	margin-left: 160px;
}
</style>
<link href="/web-device-simulator/static/css/bootstrap-responsive.css"
    rel="stylesheet">
</head>

<body>
    <div class="container">
        <div style="margin-bottom: 10px">
            <img
                src="/web-device-simulator/static/img/opensmartgridplatform_logo.png"
                style="height: 50px;" />
            <div class="pull-right">5.6.0-SNAPSHOT-${BUILD_TAG}</div>
        </div>

        <!-- menu -->
        <div class="navbar">
            <div class="navbar-inner">
                <a class="brand" href="#"><spring:message
                        code="org.opensmartgridplatform.webdevicesimulator.web.title" /></a>
                <ul class="nav">
                    <li class="active"><a
                        href="/web-device-simulator/devices">Home</a></li>
                    <li class="inactive"><a
                        href="<c:url value="/logs"/>">Logs</a></li>
                </ul>
            </div>
        </div>

        <!-- align center -->
        <div class="row">
            <div class="offset1">

                <div class="row">
                    <div class="span10">
                        <div class="messages">
                            <div id="feedback"
                                class="alert alert-success"
                                style="display: none;"></div>
                            <c:if test="${feedbackMessage != null}">
                                <div class="alert alert-success">
                                    <c:out value="${feedbackMessage}" />
                                </div>
                            </c:if>
                            <c:if test="${errorMessage != null}">
                                <div class="alert alert-error">
                                    <c:out value="${errorMessage}" />
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="span10">
                        <h1>
                            <spring:message
                                code="device.edit.page.title" />
                        </h1>
                    </div>
                </div>

                <!-- Device details -->
                <div class="row">
                    <div class="span10">
                        <fieldset>
                            <legend>
                                <spring:message
                                    code="device.edit.page.device.label" />
                            </legend>
                            <form:form modelAttribute="device"
                                method="POST" class="form-horizontal">
                                <form:hidden id="deviceId" path="id" />
                                <div class="control-group">
                                    <form:label
                                        path="deviceIdentification"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.deviceIdentification" />
                                    </form:label>
                                    <div class="controls">
                                        <form:label
                                            path="deviceIdentification"
                                            class="uneditable-input">
                                            <c:out
                                                value="${device.deviceIdentification}" />
                                        </form:label>
                                    </div>
                                </div>
                                <div class="control-group">
                                    <form:label path="ipAddress"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.ipAddress" />
                                    </form:label>
                                    <div class="controls">
                                        <form:input path="ipAddress" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <form:label path="deviceType"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.deviceType" />
                                    </form:label>
                                    <div class="controls">
                                        <form:input path="deviceType" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <form:label path="protocol"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.protocol" />
                                    </form:label>
                                    <div class="controls">
                                        <form:input path="protocol" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <form:label path="firmwareVersion"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.firmwareVersion" />
                                    </form:label>
                                    <div class="controls">
                                        <form:input
                                            path="firmwareVersion" />
                                    </div>
                                </div>
                                <div class="control-group">
                                    <form:label path="actualLinkType"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.actualLinkType" />
                                    </form:label>
                                    <div class="controls">
                                        <form:select
                                            path="actualLinkType">
                                            <form:options />
                                        </form:select>
                                    </div>
                                </div>

                                <div class="control-group">
                                    <form:label path="tariffOn"
                                        class="control-label">
                                        <spring:message
                                            code="entity.device.tariffOn" />
                                    </form:label>
                                    <div class="controls">
                                        <form:checkbox path="tariffOn" />
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="span4">
                                        <a
                                            href="/web-device-simulator/devices"
                                            class="btn"><spring:message
                                                code="device.list.link.label" /></a>
                                        <input type="submit"
                                            class="btn btn-primary"
                                            value="<spring:message code="device.edit.page.submit.label"/>" />
                                    </div>
                                </div>
                            </form:form>
                        </fieldset>
                    </div>
                </div>

                <!-- Device commands -->
                <div class="row">
                    <div class="span10">
                        <fieldset>
                            <legend>
                                <spring:message
                                    code="device.edit.page.commands.label" />
                            </legend>

                            <div class="btn-group font-size13">
                                <div class="control-label">
                                    <spring:message
                                        code="device.edit.page.commands.registerdevice.label" />
                                </div>
                                <div class="btn-group-controls">
                                    <div class="control-label">
                                        <spring:message
                                            code="device.edit.page.commands.registerdevice.hasschedule.label" />
                                    </div>
                                    <div class="controls">
                                        <input type="checkbox"
                                            id="hasSchedule" />
                                    </div>
                                    <button id="registerDevice"
                                        class="btn">
                                        <spring:message
                                            code="device.edit.page.commands.registerdevice.label" />
                                    </button>
                                    <button
                                        id="confirmDeviceRegistration"
                                        class="btn">
                                        <spring:message
                                            code="device.edit.page.commands.confirmdeviceregistration.label" />
                                    </button>
                                </div>
                            </div>

                            <div class="btn-group font-size13">
                                <div class="control-label">
                                    <spring:message
                                        code="device.edit.page.commands.eventnotification.label" />
                                </div>
                                <div class="btn-group-controls">
                                    <div class="control-label">
                                        <spring:message
                                            code="device.edit.page.commands.eventnotification.event.label" />
                                    </div>
                                    <div class="controls">
                                        <select id="event">
                                            <option value="0">DIAG_EVENTS_GENERAL</option>
                                            <option value="1">DIAG_EVENTS_UNKNOWN_MESSAGE_TYPE</option>
                                            <option value="1000">HARDWARE_FAILURE_RELAY</option>
                                            <option value="1001">HARDWARE_FAILURE_FLASH_WRITE_ERROR</option>
                                            <option value="1002">HARDWARE_FAILURE_FLASH_MEMORY_CORRUPT</option>
                                            <option value="1003">HARDWARE_FAILURE_RTC_NOT_SET</option>
                                            <option value="2000">LIGHT_EVENTS_LIGHT_ON</option>
                                            <option value="2001">LIGHT_EVENTS_LIGHT_OFF</option>
                                            <option value="2500">LIGHT_FAILURE_DALI_COMMUNICATION</option>
                                            <option value="2501">LIGHT_FAILURE_BALLAST</option>
                                            <option value="2502">LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT</option>
                                            <option value="3000">TARIFF_EVENTS_TARIFF_ON</option>
                                            <option value="3001">TARIFF_EVENTS_TARIFF_OFF</option>
                                            <option value="4000">MONITOR_EVENTS_LONG_BUFFER_FULL</option>
                                            <option value="4500">MONITOR_FAILURE_P1_COMMUNICATION</option>
                                            <option value="4600">MONITOR_SHORT_DETECTED</option>
                                            <option value="4601">MONITOR_SHORT_RESOLVED</option>
                                            <option value="4700">MONITOR_DOOR_OPENED</option>
                                            <option value="4701">MONITOR_DOOR_CLOSED</option>
                                            <option value="4702">MONITOR_EVENTS_TEST_RELAY_ON</option>
                                            <option value="4703">MONITOR_EVENTS_TEST_RELAY_OFF</option>
                                            <option value="4800">MONITOR_EVENTS_LOSS_OF_POWER</option>
                                            <option value="4900">MONITOR_EVENTS_LOCAL_MODE</option>
                                            <option value="4901">MONITOR_EVENTS_REMOTE_MODE</option>
                                            <option value="5000">FIRMWARE_EVENTS_ACTIVATING</option>
                                            <option value="5501">FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND</option>
                                            <option value="5502">FIRMWARE_EVENTS_DOWNLOAD_FAILED</option>
                                            <option value="5503">FIRMWARE_EVENTS_CONFIGURATION_CHANGED</option>
                                            <option value="6000">COMM_EVENTS_ALTERNATIVE_CHANNEL</option>
                                            <option value="6001">COMM_EVENTS_RECOVERED_CHANNEL</option>
                                            <option value="7000">SECURITY_EVENTS_OUT_OF_SEQUENCE</option>
                                            <option value="7001">SECURITY_EVENTS_OSLP_VERIFICATION_FAILED</option>
                                            <option value="7002">SECURITY_EVENTS_INVALID_CERTIFICATE</option>
                                        </select>
                                    </div>
                                    <div class="control-label">
                                        <spring:message
                                            code="device.edit.page.commands.eventnotification.description.label" />
                                    </div>
                                    <div class="controls">
                                        <input id="description" />
                                    </div>
                                    <div class="control-label">
                                        <spring:message
                                            code="device.edit.page.commands.eventnotification.index.label" />
                                    </div>
                                    <div class="controls">
                                        <input id="index" />
                                    </div>
                                    <div class="control-label">
                                        <spring:message
                                            code="device.edit.page.commands.sendnotification.hastimestamp.label" />
                                    </div>
                                    <div class="controls">
                                        <input type="checkbox"
                                            id="hasTimestamp" checked />
                                    </div>
                                    <button id="sendNotification"
                                        class="btn">
                                        <spring:message
                                            code="device.edit.page.commands.sendnotification.label" />
                                    </button>
                                </div>
                            </div>
                            <div class="btn-group font-size13">
                                <div class="control-label">
                                    <spring:message
                                        code="device.edit.page.commands.sequencenumber.label" />
                                </div>
                                <div class="btn-group-controls">
                                    <input id="sequenceNumber"></input>
                                    <button id="setSequenceNumber"
                                        class="btn">
                                        <spring:message
                                            code="device.edit.page.commands.setsequencenumber.label" />
                                    </button>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="span10">
                    <div id="result"></div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript"
        src="/web-device-simulator/static/js/jquery-1.8.3.min.js"></script>
    <script type="text/javascript"
        src="/web-device-simulator/static/js/bootstrap.min.js"></script>

    <script type="text/javascript">
                    $(document).ready(function() {

                        var getSequenceNumber = function() {
                            var request = new Object();
                            request.deviceId = $('#deviceId').val();

                            $.ajax({
                                type : 'POST',
                                url : '/web-device-simulator/devices/commands/get-sequence-number',
                                contentType : 'application/json',
                                data : JSON.stringify(request),
                                async : true,
                                success : function(data) {
                                    var div = $('#feedback');
                                    if (data <= -1) {
                                        div.removeClass('alert-success');
                                        div.addClass('alert-error');
                                        div.html(data + ' Error');
                                        div.show();
                                    } else {
                                        div.addClass('alert-success');
                                        div.removeClass('alert-error');

                                        $('#sequenceNumber').val(data);
                                    }
                                }
                            });
                        };

                        getSequenceNumber();

                        $('#setSequenceNumber').click(function() {
                            $('#feedback').hide();

                            var request = new Object();
                            request.deviceId = $('#deviceId').val();
                            request.sequenceNumber = $('#sequenceNumber').val();

                            $.ajax({
                                type : 'POST',
                                url : '/web-device-simulator/devices/commands/set-sequence-number',
                                contentType : 'application/json',
                                data : JSON.stringify(request),
                                async : true,
                                success : function(data) {
                                    var div = $('#feedback');
                                    if (data <= -1) {
                                        div.removeClass('alert-success');
                                        div.addClass('alert-error');
                                        div.html(data + ' Error');
                                        div.show();
                                    } else {
                                        div.addClass('alert-success');
                                        div.removeClass('alert-error');

                                        $('#sequenceNumber').val(data);
                                    }
                                }
                            });
                        });

                        $('#registerDevice').click(function() {
                            $('#feedback').hide();

                            var request = new Object();
                            request.deviceId = $('#deviceId').val();
                            request.deviceType = $('#deviceType').val();
                            request.hasSchedule = $('#hasSchedule').attr('checked') ? "on" : "off";
                            request.protocol = $('#protocol').val();

                            $.ajax({
                                type : 'POST',
                                url : '/web-device-simulator/devices/commands/register',
                                contentType : 'application/json',
                                data : JSON.stringify(request),
                                async : true,
                                success : function(data) {
                                    var div = $('#feedback');
                                    if (data.indexOf("ERROR") == 0) {
                                        div.removeClass('alert-success');
                                        div.addClass('alert-error');
                                    } else {
                                        div.addClass('alert-success');
                                        div.removeClass('alert-error');
                                    }

                                    div.html(data);
                                    div.show();

                                    getSequenceNumber();
                                }
                            });
                        });

                        $('#confirmDeviceRegistration').click(function() {
                            $('#feedback').hide();

                            var request = new Object();
                            request.deviceId = $('#deviceId').val();

                            $.ajax({
                                type : 'POST',
                                url : '/web-device-simulator/devices/commands/register/confirm',
                                contentType : 'application/json',
                                data : JSON.stringify(request),
                                async : true,
                                success : function(data) {
                                    var div = $('#feedback');
                                    if (data.indexOf("ERROR") == 0) {
                                        div.removeClass('alert-success');
                                        div.addClass('alert-error');
                                    } else {
                                        div.addClass('alert-success');
                                        div.removeClass('alert-error');
                                    }

                                    div.html(data);
                                    div.show();

                                    getSequenceNumber();
                                }
                            });
                        });

                        $('#sendNotification').click(function() {
                            $('#feedback').hide();

                            var request = new Object();
                            request.deviceId = $('#deviceId').val();
                            request.event = $('#event').val();
                            request.description = $('#description').val();
                            request.index = $('#index').val();
                            request.hasTimestamp = $('#hasTimestamp').attr('checked') ? "true" : "false";

                            $.ajax({
                                type : 'POST',
                                url : '/web-device-simulator/devices/commands/sendnotification',
                                contentType : 'application/json',
                                data : JSON.stringify(request),
                                async : true,
                                success : function(data) {
                                    var div = $('#feedback');
                                    if (data.indexOf("ERROR") == 0) {
                                        div.removeClass('alert-success');
                                        div.addClass('alert-error');
                                    } else {
                                        div.addClass('alert-success');
                                        div.removeClass('alert-error');
                                    }

                                    div.html(data);
                                    div.show();

                                    // Reset form.
                                    $("#event").val($("#event option:first").val());
                                    $("#description").val("");
                                    $("#index").val("");

                                    getSequenceNumber();
                                }
                            });
                        });
                    });
                </script>

</body>
</html>