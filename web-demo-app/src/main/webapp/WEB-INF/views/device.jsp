<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/web-demo-app/static/css/bootstrap.min.css" rel="stylesheet">
<link href="/web-demo-app/static/css/bootstrap-responsive.css"
	rel="stylesheet">
<script src="/web-demo-app/static/js/jquery-1.8.3.min.js"
	type="text/javascript"></script>
<title>Device details</title>
</head>
<body class="container">
	<jsp:include page="nav.jsp" />
	<br>
	<div>

		<c:if test="${not empty device}">

			<h4>Switch Light</h4>
			<form:form method="POST" action="/web-demo-app/doSwitchDevice">
				<table>
					<tr>
						<td><form:label path="deviceId">Device Identification</form:label></td>
						<td><form:input path="deviceId" value="${device.deviceId}"
								readonly="true" /></td>
					</tr>
					<tr>
						<td><form:label path="lightValue">Light Value</form:label></td>
						<c:choose>
							<c:when test="${device.lightValue <= -10}">
								<td><form:input path="lightValue"
										value="${device.lightValue}" readonly="true" /></td>
							</c:when>
							<c:when test="${device.lightValue > 100}">
								<td><form:input path="lightValue"
										value="${device.lightValue}" readonly="true" /></td>
							</c:when>
							<c:otherwise>
								<td><form:input path="lightValue" type="number"
										value="${device.lightValue}" /></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td><form:label path="lightOn">Light On</form:label></td>
						<c:choose>
							<c:when test="${device.lightOn == true}">
								<td><form:checkbox path="lightOn" value="${device.lightOn}"
										checked="true" /></td>
								<td><img src="/web-demo-app/static/img/light_bulb_on.png" alt="" height="32" width="32"></td>
							</c:when>
							<c:otherwise>
								<td><form:checkbox path="lightOn" value="${device.lightOn}" /></td>
								<td><img src="/web-demo-app/static/img/light_bulb_off.png" alt="" height="32" width="32"></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td colspan="2"><input type="submit" value="Submit" /></td>
					</tr>
				</table>
			</form:form>
		</c:if>
		<c:if test="${not empty error}">
			<p>An error occurred: ${error}</p>
			<%-- 		<p>${error-message}</p> --%>
			<a class="btn" href="/web-demo-app/list/">Return</a>
		</c:if>
	</div>
</body>
</html>