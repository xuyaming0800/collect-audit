<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>


<script type="text/javascript">
	var contextPath = "<%=request.getContextPath()%>";
  	function delAuditSys(id) {
  		if (!confirm("确认要删除？")) {
           return;
        }
  	
  		$.post("<%=request.getContextPath()%>/sys/deleteauditsystem.html",
			{id:id},
			function(data){
				if(data != null && data.length > 0){
					data = $.parseJSON(data);
					if(!data.success) {
						alert("删除失败");
						return;
					}
					refresh();
				}
			});
  	}
  
  	function addAuditSys() {
  			uditId = $("#auditSysId").val();
  			sysId = $("#systemId").val();
  			system_name = $("#systemId option:selected").text();
  			mqurl = $("#mqurl").val();
  			input_queue = $("#input_queue").val();
  			output_queue = $("#output_queue").val();
  			gistype = $("#gistypeId").val(); 
  			
  			if(null == system_name || '' == system_name) {
  				alert("请选择项目");
  				return;
  			}
  			
  			if(null == mqurl || '' == mqurl) {
  				alert("MQ地址不能为空");
  				return;
  			}
  			
  			if(null == input_queue || '' == input_queue) {
  				alert("输入队列名不能为空");
  				return;
  			}
  			
  			if(null == output_queue || '' == output_queue) {
  				alert("输出队列名不能为空");
  				return;
  			}
  			
  			$.post("<%=request.getContextPath()%>/sys/updateauditsystem.html",
			{id:uditId, system_name:system_name, mqurl:mqurl, input_queue:input_queue, 
			output_queue:output_queue, gis_type:gistype,system_id:sysId},
			function(data){
				if(data != null && data.length > 0){
					data = $.parseJSON(data);
					if(!data.success) {
						alert("删除失败");
						return;
					}
					refresh();
				}
			});
  	}
  
  
  	/**
  		加载系统管理信息
  	**/
  	function queryAuditSys() {
  				$.post("<%=request.getContextPath()%>/sys/queryauditsystem.html",
			{},
			function(data){
				if(data != null && data.length > 0){
					data = $.parseJSON(data);
					
					info = data.info;
					var containerBody = $("#bodyid").empty();
					
					var htm = "";
					$.each(info,function(i,n){
						var gt = '';
						if(1 == n.gis_type) {
							gt = '点';
						}else if(2 == n.gis_type) {
							gt = '线';
						}else {
							gt = '面';
						}
		
					    htm += "<tr>"
			    			+ "<td>" + n.id + "</td>"
			    			+ "<td>" + n.system_name + "</td>"
			    			+ "<td>" + n.mqurl + "</td>"
			    			+ "<td>" + n.input_queue + "</td>"
			    			+ "<td>" + n.output_queue + "</td>"
			    			+ "<td>" + gt + "</td>"
			    			+ "<td>"
			    				 + '<div class="btn-group" role="group"><button type="button" class="btn btn-primary" onclick="javascript:updateAuditSys(\'' + n.id + '\');" name="addDss">修改</button>'
			    				 + '<button type="button" class="btn btn-danger" onclick="javascript:delAuditSys(' + n.id + ');" name="addDss">删除</button></div>'
			    			+ "</td>"
			    			+ "</tr>";
						
					});
					containerBody.append(htm);
				}
		});
  	}
  	
  	
  	function updateAuditSys(id){
  		$.post("<%=request.getContextPath()%>/sys/queryauditsystembyid.html",
			{id:id},
			function(data){
				if(data != null && data.length > 0){
					data = $.parseJSON(data);
					if(!data.success) {
						alert("查询数据失败");
						return;
					}
					data = data.info;
					emptyAuditDialog();
					$("#systemId option[value="+data.id+"]").attr("selected",true);
		  			//$("#system_name").val(data.system_name);
		  			$("#mqurl").val(data.mqurl);
		  			$("#input_queue").val(data.input_queue);
		  			$("#output_queue").val(data.output_queue);
		  			$("#gistypeId").val(data.gis_type);
		  			$("#auditSysId").val(data.id);
		  			
		  			$('#addModal').modal({});
			}
		});
  	}
  	
  	
  	function emptyAuditDialog() {
  			$("#systemId").val("");
	  		$("#system_name").val("");
	  		$("#mqurl").val("");
	  		$("#input_queue").val("");
	  		$("#output_queue").val("");
  	}
  	
  	function openAuditDialog() {
  		emptyAuditDialog();
  	//	$("#btnId").click();
  		defaultAuditDialog();
  		$('#addModal').modal({});
  	}
  	
  	function defaultAuditDialog(){
  		$("#mqurl").val("10.171.53.13:5672");
  		$("#input_queue").val("collect_out");
  		$("#output_queue").val("collect_in");
  	}
  
  	function refresh() {
  		top.window.document.getElementById("subPage").src = '<%=request.getContextPath()%>/sys/list.html';
	}

	queryAuditSys();
	
	$(function(){
		//初始化要审核的项目
		$.post(contextPath+"/audit/getObjectList.html",null,function(data){
			if(data!=null && data!=""){
				$.each(data.info.objectList,function(i,n){
					$("<option value='" + n.id + "'>"+n.projectName+"</option>").appendTo("#systemId");
				});
			}
		},"json");
	});
</script>
</head>
<body>
	<!-- Modal -->
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">添加项目管理</h4>
				</div>
				<div class="modal-body">
					<table class="table table-striped dssBasic">
						<tr>
							<td width="20%" style="line-height:35px">项目:</td>
							<td width="80%"><select id="systemId" name="systemId"
								style="width: 100%; height: 34px">
									<option value="">请选择项目</option>
							</select>
							<input type="hidden" id="auditSysId">
								<!-- <input type="hidden" id="system_name" name="system_name"
								class="form-control"> --></td>
						</tr>
						<tr>
							<td width="20%" style="line-height:35px">MQ地址:</td>
							<td width="80%"><input type="text" id="mqurl" name="mqurl"
								class="form-control" value=""></td>
						</tr>
						<tr>
							<td style="line-height:35px">输入队列名</td>
							<td><input type="text" id="input_queue" name="input_queue"
								class="form-control" value=""></td>
						</tr>
						<tr>
							<td style="line-height:35px">输出队列名</td>
							<td><input type="text" id="output_queue" name="output_queue"
								class="form-control" value=""></td>
						</tr>
						<tr>
							<td style="line-height:35px">GIS类型</td>
							<td><select id="gistypeId" name="gistype"
								style="width: 100%; height: 34px">
									<option value="1">点</option>
									<option value="2">线</option>
									<option value="3">面</option>
							</select> <!-- 		<input type="radio"  name="gistype" value="1">点&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio"  name="gistype" value="2">线&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="radio"  name="gistype" value="3">面 --></td>
						</tr>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="stopDss"
						data-dismiss="modal">取消</button>
					<button type="button" onclick="javascript:addAuditSys();"
						class="btn btn-primary" name="addDss">提交</button>
				</div>
			</div>
		</div>
	</div>
	<div id="containerId" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">项目配置列表</h3>
			</div>
			<div class="panel-body">
				
			</div>
			<table class="table table-striped">
					<thead>
						<tr>
							<th>项目ID</th>
							<th>项目名称</th>
							<th>MQ地址</th>
							<th>输入队列名</th>
							<th>输出队列名</th>
							<th>GIS类型</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="bodyid">
					</tbody>
				</table>
			<div class="panel-footer">
				<button onclick="javascript:openAuditDialog();" type="button"
					class="btn btn-success">添加</button>
			</div>
		</div>
	</div>
</body>
</html>
