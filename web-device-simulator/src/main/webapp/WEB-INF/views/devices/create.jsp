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
<title><spring:message code="org.opensmartgridplatform.webdevicesimulator.web.title" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="../static/css/bootstrap.min.css" rel="stylesheet">
<style>
body {
	padding-top: 10px;
	padding-bottom: 10px;
}
</style>
<link href="../static/css/bootstrap-responsive.css" rel="stylesheet">
</head>

<body>
	<div class="container">
		<div style="margin-bottom: 10px">
			<img src="/web-device-simulator/static/img/opensmartgridplatform_logo.png" style="height:50px;" />
			<div class="pull-right">${project.version}-${BUILD_TAG}</div>
		</div>

		<div class="navbar">
			<div class="navbar-inner">
				<a class="brand" href="#"><spring:message code="org.opensmartgridplatform.webdevicesimulator.web.title" /></a>
				<ul class="nav">
					<li class="active"><a href="#">Home</a></li>
					<li class="inactive"><a href="<c:url value="/logs"/>">Logs</a></li>
				</ul>
			</div>
		</div>

		<!-- align center -->
		<div class="row">
			<div class="offset1">
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

				<div class="row">
					<div class="span10">
						<h1>
							<spring:message code="device.create.page.title" />
						</h1>
					</div>
				</div>

				<div class="row">
					<div class="span10">
						<fieldset>
							<legend>
								<spring:message code="device.create.page.device.label" />
							</legend>
							<form:form action="create" commandName="device" method="POST" class="form-horizontal">
								<div class="control-group">
									<form:label path="deviceIdentification" class="control-label">
										<spring:message code="entity.device.deviceIdentification" />
									</form:label>
									<div class="controls">
										<form:input path="deviceIdentification" />
									</div>
								</div>
								<div class="control-group">
									<form:label path="ipAddress" class="control-label">
										<spring:message code="entity.device.ipAddress" />
									</form:label>
									<div class="controls">
										<form:input path="ipAddress" value="127.0.0.1" />
									</div>
								</div>
								<div class="control-group">
									<form:label path="deviceType" class="control-label">
										<spring:message code="entity.device.deviceType" />
									</form:label>
									<div class="controls">
										<form:input path="deviceType" value="SSLD" />
									</div>
								</div>
								<div class="control-group">
									<form:label path="protocol" class="control-label">
										<spring:message code="entity.device.protocol" />
									</form:label>
									<div class="controls">
										<form:input path="protocol" value="OSLP_ELSTER" />
									</div>
								</div>

								<div class="row">
									<div class="span4">
										<a href="../devices" class="btn"><spring:message code="device.list.link.label" /></a> <input type="submit" class="btn btn-primary"
											value="<spring:message code="device.create.page.submit.label"/>" />
									</div>
								</div>
							</form:form>
						</fieldset>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>