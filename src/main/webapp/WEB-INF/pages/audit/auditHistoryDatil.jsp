<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html>
<html>
<head>

<title>审核历史明细页</title>

<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script type="text/javascript">
	$(function() {
		var hid = <%=request.getParameter("hid")%>;//历史记录ID
		var op = $(window.opener.document);
		$.metadata.setType("attr", "data");
		var md = $("#auditHistory > tbody > tr#" + hid, op).metadata();
		$("#assignee").text(md.historicActivityInstance.assignee);//办理用户
		$("#activityName").text(md.historicActivityInstance.activityName);//活动名称
		$("#activityType").text(md.historicActivityInstance.activityType);//活动类型
		$("#startTime").text(
				new Date(md.historicActivityInstance.startTime)
						.toLocaleDateString()
						+ ' '
						+ new Date(md.historicActivityInstance.startTime)
								.toLocaleTimeString());//开始时间
		$("#endTime").text(
				(md.historicActivityInstance.endTime == null ? '' : (new Date(
						md.historicActivityInstance.endTime)
						.toLocaleDateString()
						+ ' ' + new Date(md.historicActivityInstance.endTime)
						.toLocaleTimeString())));//结束时间
		$("#durationInMillis").text(
				md.historicActivityInstance.durationInMillis / 1000 + "秒");//活动耗时

		//意见
		var tb = $("#comments tbody");
		$.each(md.comments, function(i, n) {
			$("<tr/>").append("<td>" + n.userId + "</td>")
					.append(
							"<td>"
									+ (new Date(n.time).toLocaleDateString()
											+ ' ' + new Date(n.time)
											.toLocaleTimeString()) + "</td>")
					.append("<td>" + n.fullMessage + "</td>").appendTo(tb);
		});

		//附件
		var tb = $("#attachments tbody");
		$.each(md.comments, function(i, n) {
			$("<tr/>").append("<td>" + n.userId + "</td>")
					.append(
							"<td>"
									+ (new Date(n.time).toLocaleDateString()
											+ ' ' + new Date(n.time)
											.toLocaleTimeString()) + "</td>")
					.append("<td>" + n.url + "</td>").append(
							"<td><a href='<%=request.getContextPath()%>/audit/getAttachmentContent.html?attachmentId="
									+ n.id + "'>下载</a></td>").appendTo(tb);
		});
	});
</script>
</head>

<body>
	<div class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">任务信息</div>
			<div class="panel-body">
				<div class="col-md-2">办理用户：</div>
				<div class="col-md-4" id="assignee"></div>
				<div class="col-md-2">活动名称：</div>
				<div class="col-md-4" id="activityName"></div>
				<div class="col-md-2">活动类型：</div>
				<div class="col-md-4" id="activityType"></div>
				<div class="col-md-2">开始时间：</div>
				<div class="col-md-4" id="startTime"></div>
				<div class="col-md-2">结束时间：</div>
				<div class="col-md-4" id="endTime"></div>
				<div class="col-md-2">活动耗时：</div>
				<div class="col-md-4" id="durationInMillis"></div>
			</div>
			<div class="panel-footer"></div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">意见</div>
			<div class="panel-body">
			</div>
			<table id="comments" class="table table-hover">
				<thead>
					<tr>
						<th>用户</th>
						<th>发表时间</th>
						<th>意见</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="panel-footer"></div>
		</div>
		<div class="panel panel-primary">
			<div class="panel-heading">附件</div>
			<div class="panel-body">
			</div>
			<table id="attachments" class="table table-hover">
				<thead>
					<tr>
						<th>用户</th>
						<th>发表时间</th>
						<th>URL</th>
						<th>#</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="panel-footer"></div>
		</div>
	</div>
</body>
</html>
