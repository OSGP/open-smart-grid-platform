<%--

    Copyright 2015 Smart Society Services B.V.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

--%>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<title><spring:message code="org.opensmartgridplatform.webdevicesimulator.web.title" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="/web-device-simulator/static/css/bootstrap.min.css" rel="stylesheet">
<style>
body {
	padding-top: 10px;
	padding-bottom: 10px;
}
</style>
<link href="/web-device-simulator/static/css/bootstrap-responsive.css" rel="stylesheet">
</head>

<body>
	<div class="container">
		<div style="margin-bottom: 10px">
			<img src="/web-device-simulator/static/img/opensmartgridplatform_logo.png" style="height:50px;" />
			<div class="pull-right">${project.version}-${BUILD_TAG}</div>
		</div>

		<!-- menu -->
		<div class="navbar">
			<div class="navbar-inner">
				<a class="brand" href="#"><spring:message code="org.opensmartgridplatform.webdevicesimulator.web.title" /></a>
				<ul class="nav">
					<li class="active"><a href="/web-device-simulator/devices">Devices</a></li>
					<li class="inactive"><a href="<c:url value="/logs"/>">Logs</a></li>
				</ul>
			</div>
		</div>

		<!-- align center -->
		<div class="row">
			<div class="offset1">

				<!-- notifications -->
				<div class="row">
					<div class="span10">
						<div class="messages">
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

				<!-- buttons -->
				<div class="row">
					<div class="span10">
						<h1>
							<spring:message code="device.list.page.title" />
						</h1>
						<a href="/web-device-simulator/devices/create" class="btn btn-primary"><spring:message code="device.create.link.label" /></a>
							<spring:message code="device.edit.page.device.registration" />
							<input id="devRegistration" name="devRegistration" type="checkbox"/>
							<spring:message code="device.edit.page.device.reboot" />
							<input id="devReboot" name="devReboot" type="checkbox"/>
							<spring:message code="device.edit.page.device.tariff.switching" />
							<input id="tariffSwitching" name="tariffSwitching" type="checkbox"/>
							<spring:message code="device.edit.page.device.light.switching" />
							<input id="lightSwitching" name="lightSwitching" type="checkbox"/>
							<spring:message code="device.edit.page.device.event.notification" />
							<input id="eventListener" name="eventListener" type="checkbox"/>
					</div>
				</div>

				<!-- devices list -->
				<div class="row" style="margin-top: 25px">
					<div class="span10">
						<table class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th>Id</th>
									<th><spring:message code="entity.device.deviceIdentification" /></th>
									<th><spring:message code="entity.device.ipAddress" /></th>
									<th><spring:message code="entity.device.deviceType" /></th>
									<th><spring:message code="entity.device.preferredLinkType" /></th>
									<th><spring:message code="entity.device.actualLinkType" /></th>
									<th><spring:message code="entity.device.lightType" /></th>
									<th><spring:message code="entity.device.lightOn" /></th>
									<th><spring:message code="entity.device.dimValue" /></th>
									<th><spring:message code="entity.device.selfTestActive" /></th>
									<th><spring:message code="entity.device.sequenceNumber" /></th>
									<th><spring:message code="entity.device.eventNotifications" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${devices}" var="device">
									<tr>
										<td><c:out value="${device.id}" /></td>
										<td><a href="/web-device-simulator/devices/edit/${device.id}"><c:out value="${device.deviceIdentification}" /></a></td>
										<td><c:out value="${device.ipAddress}" /></td>
										<td><c:out value="${device.deviceType}" /></td>
										<td id="preferredLinkType${device.id}"></td>
										<td><c:out value="${device.actualLinkType}" /></td>
										<td id="lightType${device.id}"></td>
										<td id="lightState${device.id}"></td>
										<td id="dimValue${device.id}"></td>
										<td id="selfTestState${device.id}"></td>
										<td id="sequenceNumber${device.id}"></td>
										<td id="eventNotifications${device.id}"></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

	</div>

	<script type="text/javascript" src="/web-device-simulator/static/js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="/web-device-simulator/static/js/bootstrap.min.js"></script>

	<script type="text/javascript">
        $(document).ready(
                function() {
                	
                	$('#devRegistration').change(function() {
                		
                	    var request = new Object();
                	    request.autonomousStatus = $('#devRegistration').prop('checked');
                		
                	    $.ajax({
                	        type : 'POST',
                	        url : '/web-device-simulator/devices/deviceRegistrationCheck',
                	        contentType : 'application/json',
                	        dataType : 'json',
                	        data : JSON.stringify(request),
                	        async : true
                	    });

                		});
                	
                	$('#devReboot').change(function() {
                		
                	    var request = new Object();
                	    request.autonomousStatus = $('#devReboot').prop('checked');
                		
                	    $.ajax({
                	        type : 'POST',
                	        url : '/web-device-simulator/devices/deviceRebootCheck',
                	        contentType : 'application/json',
                	        dataType : 'json',
                	        data : JSON.stringify(request),
                	        async : true
                	    });

                		});
                	
                	$('#tariffSwitching').change(function() {
                		
                	    var request = new Object();
                	    request.autonomousStatus = $('#tariffSwitching').prop('checked');
                		
                	    $.ajax({
                	        type : 'POST',
                	        url : '/web-device-simulator/devices/tariffSwitchingCheck',
                	        contentType : 'application/json',
                	        dataType : 'json',
                	        data : JSON.stringify(request),
                	        async : true
                	    });

                		});
                	
                	$('#lightSwitching').change(function() {
                		
                	    var request = new Object();
                	    request.autonomousStatus = $('#lightSwitching').prop('checked');
                		
                	    $.ajax({
                	        type : 'POST',
                	        url : '/web-device-simulator/devices/lightSwitchingCheck',
                	        contentType : 'application/json',
                	        dataType : 'json',
                	        data : JSON.stringify(request),
                	        async : true
                	    });

                		});
                	
                	$('#eventListener').change(function() {
                		
                	    var request = new Object();
                	    request.autonomousStatus = $('#eventListener').prop('checked');
                		
                	    $.ajax({
                	        type : 'POST',
                	        url : '/web-device-simulator/devices/eventNotificationCheck',
                	        contentType : 'application/json',
                	        dataType : 'json',
                	        data : JSON.stringify(request),
                	        async : true
                	    });

                		});
                	
                    refreshLightStates();
                    setInterval(refreshLightStates, 4000);

                    function refreshLightStates() {
                        $.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                                for (var i = 0; i < data.length; i++) {
                                    // Set preferred link type
                                    if (data[i].preferredLinkType == null) {
                                        $('#preferredLinkType' + data[i].id).html('');
                                    } else {
                                        $('#preferredLinkType' + data[i].id).html(data[i].preferredLinkType);
                                    }

                                    // Set light type
                                    if (data[i].lightType == null) {
                                        $('#lightType' + data[i].id).html('');
                                    } else {
                                        $('#lightType' + data[i].id).html(data[i].lightType);
                                    }

                                    // Set light state
                                    var isOn = data[i].lightOn;
                                    var dimVal = data[i].dimValue;

                                    if (isOn) {
                                        if (dimVal != null) {
                                            $('#lightState' + data[i].id).html(
                                                    '<img src="/web-device-simulator/static/img/light_bulb_on.png" style="height: 40px; width: 40px; opacity:' + dimVal/100 + ';" />');
                                        } else {
                                            $('#lightState' + data[i].id).html('<img src="/web-device-simulator/static/img/light_bulb_on.png" style="height: 40px; width: 40px;" />');
                                        }
                                    } else {
                                        $('#lightState' + data[i].id).html('<img src="/web-device-simulator/static/img/light_bulb_off.png" style="height: 40px; width: 40px;" />');
                                    }

                                    // Set dim values
                                    if (data[i].dimValue == null) {
                                        $('#dimValue' + data[i].id).html('');
                                    } else {
                                        $('#dimValue' + data[i].id).html(data[i].dimValue);
                                    }

                                    // Set selftest
                                    if (data[i].selftestActive) {
                                        $('#selfTestState' + data[i].id).html('<span class="badge badge-Success">Started</span>');
                                    } else {
                                        $('#selfTestState' + data[i].id).html('<span class="badge">Stopped</span>');
                                    }

                                    // Set sequence number
                                    if (data[i].sequenceNumber == null) {
                                        $('#sequenceNumber' + data[i].id).html('');
                                    } else {
                                        $('#sequenceNumber' + data[i].id).html(data[i].sequenceNumber);
                                    }

                                    // Set event notifications
                                    if (data[i].eventNotifications == null) {
                                        $('#eventNotifications' + data[i].id).html('');
                                    } else {
                                        var split = data[i].eventNotifications.split(',');
                                        var list = '';
                                        for (var j = 0; j < split.length; j++) {
                                            list += split[j] + '<br />';
                                        }
                                        $('#eventNotifications' + data[i].id).html(list);
                                    }

                                }
                            }
                        });
						$.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/deviceRegistrationCheck/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                            	$('#devRegistration').prop("checked" , data);
							}
						});
						$.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/deviceRebootCheck/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                            	$('#devReboot').prop("checked" , data);
							}
						});
						$.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/tariffSwitchingCheck/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                            	$('#tariffSwitching').prop("checked" , data);
							}
						});
						$.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/lightSwitchingCheck/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                            	$('#lightSwitching').prop("checked" , data);
							}
						});
						$.ajax({
                            type : 'GET',
                            url : '/web-device-simulator/devices/eventNotificationCheck/json',
                            dataType : 'json',
                            contentType : 'application/json',
                            async : true,
                            cache : false,
                            success : function(data) {
                            	$('#eventListener').prop("checked" , data);
							}
						});
                    }
                });
    </script>

</body>
</html>