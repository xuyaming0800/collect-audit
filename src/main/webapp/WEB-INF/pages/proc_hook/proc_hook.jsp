<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>My JSP 'proc_hook.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<style type="text/css">
.project_lab{
	font-weight: initial;
    color: magenta;
    font-size: small;
}
</style>
<script type="text/javascript">
	var contextPath = "<%=request.getContextPath()%>";
	function findAll(){
		$.post("<%=basePath%>procHook/findAll.html", null, function(data) {
			if(data){
				$("#procHookTable > tbody").empty();
				data=$.parseJSON(data);
				$.each(data,function(i,n){
					$("<tr/>")
					.append("<td>"+n.proc_def_id+"</td>")
					.append("<td style='word-break: break-word;'>"+n.expression+"</td>")
					.append("<td>"+n.create_user+"</td>")
					.append("<td>"+new Date(n.create_time).toLocaleDateString()+" "+new Date(n.create_time).toLocaleTimeString()+"</td>")
					.append("<td><button class=\"btn btn-danger\" onclick=\"deleteDeployment('"+n.proc_def_id+"')\">删除</button></td>")
					.dblclick(function(e){
						$.post("<%=basePath%>procHook/detail.html", {
							processDefinitionId:n.proc_def_id
						}, function(data) {
							if(data){
								data=$.parseJSON(data);
								$('#myModal').find('div.modal-body').empty()
								.append('<div>流程定义ID：'+data.id+'<input type="hidden" id="processDefinitionId" value="'+data.id+'"/><input type="hidden" id="deploymentId" value="'+data.deploymentId+'"/><input type="hidden" id="resourceName" value="'+data.resourceName+'"/></div>')
								.append('<div>版本：'+data.version+'</div>')
								.append('<div>是否挂起：'+data.suspended+'</div>')
								.append('<div>创建用户：'+n.create_user+'</div>')
								.append('<div>创建时间：'+new Date(n.create_time).toLocaleDateString()+" "+new Date(n.create_time).toLocaleTimeString()+'</div>')
								.append('<div class="form-inline">表达式：<label class="sr-only" for="expression">表达式</label><input id="expression" value="'+n.expression+'" class="form-control"/><button class="btn btn-info" onclick="updateExpression()">修改</button>(<font style="color: #EF4704;font-size: 8px">bsType为项目id，勿随意修改。</font>)</div>')
								.append('<div>流程图：<img src="<%=basePath%>procHook/processDiagram.png?processDefinitionId='+data.id+'"/></div>')
								;
								$('#myModal').modal('toggle');
							}
						});
					})
					.appendTo("#procHookTable > tbody");
				});
			}
		});
	}
	
	//删除部署
	function deleteDeployment(processDefinitionId){
		$.post("<%=basePath%>procHook/deleteDeployment.html",{
			processDefinitionId:processDefinitionId
		},function(data){
			if(data != null && data != ""){
				alert(data);
				findAll();
			}
		});
	}
	
	//激活流程定义
	function activateProcessDefinition(){
		$.post("<%=basePath%>procHook/activateProcessDefinition.html",{
			processDefinitionId:$("#myModal #processDefinitionId").val()
		},function(data){
			alert("激活成功");
			findAll();
		});
	}
	
	//挂起流程定义
	function suspendProcessDefinition(){
		$.post("<%=basePath%>procHook/suspendProcessDefinition.html",{
			processDefinitionId:$("#myModal #processDefinitionId").val()
		},function(data){
			alert("挂起成功");
			findAll();
		});
	}
	
	//下载流程定义文件
	function downloadProcessDefinitionFile(){
        window.open("<%=basePath%>procHook/downloadProcessDefinitionFile.html?deploymentId="+$("#myModal #deploymentId").val()+"&resourceName="+$("#myModal #resourceName").val());  
	}
	
	//修改表达式
	function updateExpression(){
		$.post("<%=basePath%>procHook/updateExpression.html",{
			processDefinitionId:$("#myModal #processDefinitionId").val(),
			expression:$("#myModal #expression").val()
		},function(data){
			alert("修改成功");
			findAll();
		});
	}
	
	$(function(){
		findAll();
		// 部署按钮
		$("#doDeploy").click(function(){
			var resourceName = $("#resourceName").val();
			var text = $("#text").val();
			var expression = $("#expression_dis").val();
			if(resourceName==""){
				alert("请输入资源名称");return;
			}if(text==""){
				alert("请输入流程xml内容");return;
			}if(expression==""){
				alert("请选择项目，生成表达式");return;
			}
			$.post("<%=basePath%>procHook/doDeploy.html",{
				resourceName:resourceName,
				text:text,
				expression:expression
			},function(data){
				if(data != null &&data != ""){
					alert("部署成功！部署ID"+data);
					findAll();
					$('#deployModal').modal('hidden');
				}else{
					alert("部署失败！");
				}
			});
		});
		
		//初始化要审核的项目
		$.post(contextPath+"/audit/getObjectList.html",null,function(data){
			if(data!=null && data!=""){
				$.each(data.info.objectList,function(i,n){
					$("<label class='project_lab' onclick='doCreateExp();'><input type='checkbox' value='" + n.id + "'/>"+n.projectName+"</label>").appendTo("#projects");
				});
			}
		},"json");
	});
	
	function doCreateExp(){
		$("#expression_dis").val("");
		var pros = $("#projects input:checked");
		var exp = "";
		if(pros&&pros.size()>0){
			exp += "(";
			for(var i=0;i<pros.size();i++){
				if(i!=pros.size()-1)
					exp += "bsType=='" + $(pros[i]).val() + "'||";
				else
					exp += "bsType=='" + $(pros[i]).val()+"'";
			}
			exp += ")&&type==4";
			$("#expression_dis").val(exp);
		}
	}
</script>
</head>

<body>
	<div class="container">
		<div class="row">
			<div class="panel panel-primary">
				<div class="panel-heading">流程挂接管理（双击查看明细）</div>
				<div class="panel-body"></div>
				<table id="procHookTable" class="table table-hover">
					<thead>
						<tr>
							<th>流程ID</th>
							<th>表达式</th>
							<th>创建用户</th>
							<th>创建时间</th>
							<th>#</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div class="panel-footer">
					<button class="btn btn-success" data-toggle="modal"
						data-target="#deployModal">部署新流程</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="width: 800px;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">流程明细</h4>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button class="btn btn-success"
						onclick="activateProcessDefinition()">激活流程</button>
					<button class="btn btn-danger" onclick="suspendProcessDefinition()">挂起流程</button>
					<button class="btn btn-default"
						onclick="downloadProcessDefinitionFile()">下载流程定义文件</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="deployModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="deployModalLabel">部署新流程</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="resourceName">资源名称</label> <input type="text"
							class="form-control" id="resourceName" placeholder="资源名称" value="AuditProcess">
					</div>
					<div class="form-group">
						<label for="text">xml</label>
						<textarea class="form-control" id="text" placeholder="xml"
							rows="5" cols="3"></textarea>
					</div>
					<div class="form-group">
						<label for="expression">项目选择</label>
						<div id="projects"></div>
						<input type="text" disabled="disabled" class="form-control" id="expression_dis" placeholder="表达式">
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="doDeploy"
						data-dismiss="modal">部署</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
