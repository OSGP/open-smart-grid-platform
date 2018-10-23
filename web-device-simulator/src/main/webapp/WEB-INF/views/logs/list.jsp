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
<link href="<c:url value="/static/css/bootstrap.min.css"/>" rel="stylesheet">
<style>
body {
	padding-top: 10px;
	padding-bottom: 10px;
}
</style>
<link href="<c:url value="/static/css/bootstrap-responsive.css"/>" rel="stylesheet">
</head>

<body>
	<div class="container">
		<div style="margin-bottom: 10px">
			<img src="/web-device-simulator/static/img/opensmartgridplatform_logo.png" style="height:50px;"/>
		</div>

		<div class="navbar">
			<div class="navbar-inner">
				<a class="brand" href="#"><spring:message code="org.opensmartgridplatform.webdevicesimulator.web.title" /></a>
				<ul class="nav">
					<li class="inactive"><a href="<c:url value="/devices"/>">Devices</a></li>
					<li class="active"><a href="<c:url value="/logs"/>">Logs</a></li>
				</ul>
			</div>
		</div>

		<div class="row" style="margin-top: 25px">
			<div class="span12">
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th><spring:message code="entity.log.time" /></th>
							<th><spring:message code="entity.log.incoming" /></th>
							<th><spring:message code="entity.log.device" /></th>
							<th><spring:message code="entity.log.deviceIdentification" /></th>
							<th><spring:message code="entity.log.message" /></th>
							<th><spring:message code="entity.log.encoded" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${logs}" var="log">
							<tr>
								<td><c:out value="${log.modificationTime}" /></td>
								<td><c:out value="${log.incoming}" /></td>
								<td><c:out value="${log.deviceUid}" /></td>
								<td><c:out value="${log.deviceIdentification}" /></td>
								<td><c:out value="${log.decodedMessage}" /></td>
								<td><c:out value="${log.encodedMessage}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

	</div>

	<script type="text/javascript" src="<c:url value="/static/js/jquery-1.8.3.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/static/js/bootstrap.min.js"/>"></script>

</body>
</html>