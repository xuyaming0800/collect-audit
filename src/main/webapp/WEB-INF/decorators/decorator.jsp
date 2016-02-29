<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>审核-<sitemesh:write property='title' /></title>

<!-- Bootstrap core CSS -->
<link href="<%=request.getContextPath()%>/css/bootstrap.min.css"
	rel="stylesheet">
	
<link href="<%=request.getContextPath()%>/css/bootstrap-datetimepicker.min.css"
	rel="stylesheet">
	
<link href="<%=request.getContextPath()%>/css/docs.min.css"
	rel="stylesheet">

<!-- Custom styles for this template -->
<!-- <link href="navbar.css" rel="stylesheet"> -->

<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script
	src="<%=request.getContextPath()%>/js/ie-emulation-modes-warning.js"></script>

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="<%=request.getContextPath()%>/js/jquery-2.1.3.min.js"></script>
	<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script
		src="<%=request.getContextPath()%>/js/ie10-viewport-bug-workaround.js"></script>
	<script
		src="<%=request.getContextPath()%>/js/map.js"></script>
	<script
		src="<%=request.getContextPath()%>/js/jquery.metadata.js"></script>
	<script
		src="<%=request.getContextPath()%>/js/jquery.json.min.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/jquery.form.js"></script>
	<script
		src="<%=request.getContextPath()%>/js/dateFormat.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/raphael-min.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/jquery.pin.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/jquery-ui.min.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/jquery.scrollview.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/bootstrap-datetimepicker.min.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/bootstrap-datetimepicker.zh-CN.js"></script>
		
	<script
		src="<%=request.getContextPath()%>/js/jquery.mobile.custom.events.min.js"></script>
		
		<sitemesh:write property='head' />
</head>
	
<body>
	<sitemesh:write property='body' />
</body>
</html>
