<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device List</title>
</head>
<body>
	<h2>Device List</h2>
	<c:if test="${not empty deviceList}">

		<ul>
			<c:forEach var="device" items="${deviceList}">
				<li>${device.deviceIdentification}</li>
			</c:forEach>
		</ul>

	</c:if>
	<c:if test="${empty deviceList}">
		<p>No devices found</p>
	</c:if>
</body>
</html>