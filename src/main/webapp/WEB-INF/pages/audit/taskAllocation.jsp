<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<title>任务分配页面</title>
<link href="<%=request.getContextPath()%>/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<style>
.box {
	/*非IE的主流浏览器识别的垂直居中的方法*/
	display: table-cell;
	vertical-align: middle;
	/*设置水平居中*/
	text-align: center;
	/* 针对IE的Hack */
	*display: block;
	*font-size: 175px; /*约为高度的0.873，200*0.873 约为175*/
	*font-family: Arial; /*防止非utf-8引起的hack失效问题，如gbk编码*/
	background-color: #DDD;
	vertical-align: middle;
}

.box img {
	/*设置图片垂直居中*/
	display: block;
	margin: 0 auto;
}

.center {
	width: auto;
	display: table;
	margin-left: auto;
	margin-right: auto;
}

.ui-autocomplete {
	max-height: 100px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
}

* html .ui-autocomplete {
	height: 100px;
}
</style>
<script type="text/javascript">
var contextPath = "<%=request.getContextPath()%>";
	$(function() {
		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC",
				"C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang",
				"Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp",
				"Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#user_name_in_group").autocomplete({
			source : availableTags
		});
		//选中某一个选项
		$(".list-group-item").bind("click", function() {
			$(".list-group-item").attr("class", "list-group-item");
			$(this).attr("class", "list-group-item active");
		})

		$("#audit_status li > a").click(function() {
			var self = $(this);
			$("#audit_status_val").text(self.text());
			auditStatus = self.attr("auditStatus");
		})

		//组员名绑定onchange事件
		$("#user_name_in_group").bind("change", function() {
			$.post(contextPath+"/taskDistribution/getUseNames.html", {
				"keyName" : $("#user_name_in_group").val()
			}, function(data) {
				alert(data); // John
			}, "json");
		});
	});
	//添加组员
	function addOneForGroup() {
		alert("添加组员");
	}
	//删除组员
	function delOneForGroup() {
		alert("删除组员");
	}
	//创建群组
	function createGroup() {
		alert("创建群组");
	}
	//创建群组
	function cancelGroup() {
		alert("取消创建群组");
	}
</script>
</head>
<body>
	<div id="containerId" class="container">
		<div class="row">
			<div class="col-xs-12 col-md-12">
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">创建群组</h3>
					</div>
					<form class="form-horizontal">
						<br>
						<div class="form-group">
							<label for="groupName" class="col-sm-2 control-label">组名：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="groupName"
									placeholder="组名">
							</div>
						</div>
						<div class="form-group">
							<label for="administrator" class="col-sm-2 control-label">管理员：</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" id="administrator"
									placeholder="管理员">
							</div>
						</div>
						<div class="btn-group" style="margin-left: 17%;">
							<button type="button" class="btn btn-default dropdown-toggle"
								data-toggle="dropdown" aria-expanded="false">
								<span id="audit_status_val">类型</span><input id="auditStatus"
									type="hidden"><span class="caret"></span>
							</button>
							<ul id="audit_status" class="dropdown-menu" role="menu">
								<li><a href="#">类型</a></li>
								<li class="divider"></li>
								<li><a href="#" auditStatus="0">初审</a></li>
								<li><a href="#" auditStatus="1">抽检</a></li>
							</ul>
						</div>
						<br>
						<div class="form-group" style="padding-top: 15px;">
							<label for="user_name_in_group" class="col-sm-2 control-label">选择组员：</label>
							<div class="col-sm-8 ui-widget">
								<input type="text" class="form-control" id="user_name_in_group"
									name="collect_task_name" placeholder="组员名"
									style="width: 20%;display:initial;">
								<button onclick="addOneForGroup();" type="button"
									class="btn btn-success">添加</button>
								<button onclick="delOneForGroup();" type="button"
									class="btn btn-success">删除</button>
							</div>
							<div class="col-sm-8" style="margin-top: 15px;">
								<ul class="list-group" style="width: 30%;margin-left: 26%;"
									id="name_group_list">
									<li class="list-group-item">Crasodio</li>
									<li class="list-group-item">Crasodio</li>
									<li class="list-group-item">Crasodio</li>
								</ul>
							</div>
						</div>
						<div class="form-group" style="margin-top: 10px;">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="button" onclick="createGroup();"
									class="btn btn-primary">创建</button>
								<button type="button" onclick="cancelGroup();"
									class="btn btn-primary" style="margin-left: 100px;">取消</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>