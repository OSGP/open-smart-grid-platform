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
<title>Add Device</title>
</head>
<body class="container">
	<jsp:include page="nav.jsp" />
	<br>
	<div>
		<h4>Add new device</h4>
		<form:form method="POST" action="/web-demo-app/doAddDevice">
			<table>
				<tr>
					<td><form:label path="deviceIdentification">Device Identification</form:label></td>
					<td><form:input path="deviceIdentification" /></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="Submit" /></td>
				</tr>
			</table>
		</form:form>
	</div>
</body>
</html>