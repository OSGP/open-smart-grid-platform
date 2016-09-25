<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Web Demo App</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="/web-demo-app/static/css/bootstrap.min.css" rel="stylesheet">
<link href="/web-demo-app/static/css/bootstrap-responsive.css"
	rel="stylesheet">
<script src="/web-demo-app/static/js/jquery-1.8.3.min.js"
	type="text/javascript"></script>
</head>
<body class="container">
	<jsp:include page="nav.jsp" />
	<br>
	<div>
		<h4>Device List</h4>
		<div class="row">
			<c:if test="${not empty deviceList}">

				<ul class="thumbnails">
					<c:forEach var="device" items="${deviceList}">

						<li class="span2">
							<div class="thumbnail">
								<img src="/web-demo-app/static/img/light_bulb_off.png" alt=""
									height="42" width="42">
								<div class="caption">
									<h5>${device.deviceIdentification}</h5>
									<p>
										<a
											href="/web-demo-app/deviceDetails/${device.deviceIdentification}"
											class="btn">Manage</a>
									</p>
								</div>
							</div>
						</li>
					</c:forEach>
				</ul>

			</c:if>
			<c:if test="${empty deviceList}">
				<p>No devices found, please <a href="/web-demo-app/addDevice">add a Device</a> </p>
			</c:if>
		</div>
	</div>
</body>
</html>