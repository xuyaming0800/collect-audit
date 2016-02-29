<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<meta name="description" content="">
<meta name="author" content="">

<title>审核</title>
<style type="text/css">
body {
	padding-top: 70px;
}
</style>
<!-- Bootstrap core CSS -->
<link href="<%=request.getContextPath()%>/css/bootstrap.min.css"
	rel="stylesheet">

<!-- Custom styles for this template -->
<!-- <link href="navbar.css" rel="stylesheet"> -->

<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="<%=request.getContextPath()%>/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script
	src="<%=request.getContextPath()%>/js/ie-emulation-modes-warning.js"></script>

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>
<body>
	<nav id="menu" class="navbar navbar-fixed-top navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">审核平台</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a
						href="javascript:go2Page('<%=request.getContextPath()%>/audit/audit.html')"><span>审核(广告拍拍)</span></a></li>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/audit/auditEnvir.html')"><span>审核</span></a></li>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/audit/listForEnvir.html')"><span>任务查询</span></a></li>
					<%-- <li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/audit/taskAllocation.html')"><span>任务分配</span></a></li> --%>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/sys/list.html')"><span>项目管理</span></a></li>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/procHook/list.html')"><span>流程管理</span></a></li>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/pay/record/list.html')"><span>支付记录</span></a></li>
					<li><a
						href="javascript:go2Page('<%=request.getContextPath()%>/claim/list.html')"><span>任务分配</span></a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#">欢迎， <shiro:principal /></a></li>
					<li><a href="<%=request.getContextPath()%>/logout">退出</a></li>
				</ul>
			</div>
			<!-- /.nav-collapse -->
		</div>
		<!-- /.container -->
	</nav>
	<!-- /.navbar -->
<!-- 	<div class="embed-responsive embed-responsive-16by9"> -->
		<iframe id="subPage" width="100%"></iframe>
<!-- 	</div> -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="<%=request.getContextPath()%>/js/jquery-2.1.3.min.js"></script>
	<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script
		src="<%=request.getContextPath()%>/js/ie10-viewport-bug-workaround.js"></script>

	<script src="<%=request.getContextPath()%>/js/offcanvas.js"></script>

	<script type="text/javascript">
		function go2Page(_url) {
			$(function() {
				$('#subPage').attr('src', _url);
			});
		}
		function autoResizeIframe() {
			$(function() {
				$('#subPage').css({
					'position' : 'absolute',
					'height' : $(window).outerHeight() - 70,
					'border' : 0
				});
			});
		}
		$(function() {
			$("#navbar > ul > li.active > a > span").click();
			$("#navbar > ul > li").each(function() {
				$(this).click(function() {
					$(this).siblings("li").removeClass("active");
					$(this).addClass("active");
				});
			});
			autoResizeIframe();
			$(window).resize(function() {
				autoResizeIframe();
			});
		});
	</script>
</body>

</html>
