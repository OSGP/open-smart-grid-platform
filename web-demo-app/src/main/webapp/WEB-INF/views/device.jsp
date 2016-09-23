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
<body>
	<jsp:include page="nav.jsp" />
	<div class="container">

		<c:if test="${not empty device}">

			<h4>Switch Light</h4>
			<%-- 			<p><label>Device Id</label>${device.deviceId}</p> --%>
			<%-- 			<p><label>Light Value</label>${device.lightValue}</p> --%>
			<%-- 			<p><label>Light On</label>${device.lightOn}</p> --%>
			<form:form method="POST" action="/web-demo-app/doSwitchDevice">
				<table>
					<tr>
						<td><form:label path="deviceId">Device Identification</form:label></td>
						<td><form:input path="deviceId" value="${device.deviceId}" readonly="true"/></td>
					</tr>
					<tr>	
						<td><form:label path="lightValue">Light Value</form:label></td>
						<td><form:input path="lightValue"
								value="${device.lightValue}" /></td>
					</tr>
					<tr>
						<td><form:label path="lightOn">Light On</form:label></td>
						<td><form:checkbox path="lightOn"
								value="${device.lightOn}" /></td>
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