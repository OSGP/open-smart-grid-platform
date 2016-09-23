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
			<p>SetLight request for device ${device.deviceId} has been sent to the Platform</p>
			
			<p>Light Value will be set to ${device.lightValue}</p>
			<p>Light On will be set to ${device.lightOn}</p>

		</c:if>

	</div>
</body>