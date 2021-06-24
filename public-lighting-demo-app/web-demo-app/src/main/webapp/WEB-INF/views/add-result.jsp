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
<title>Add Device Result</title>
</head>
<body class="container">
	<jsp:include page="nav.jsp" />
	<br>
	<div>
		<p>The device with id <i>${deviceId}</i> has been added to the Platform.</p>
		<p>Please simulate the device by adding and registering it to the <a href="/web-device-simulator/devices" target="_blank" rel="noopener noreferrer">device simulator</a>.</p>
		<br>
		<p><a class="btn" href="/web-demo-app/list">Return to Devices List</a></p>
	</div>
</body>
</html>