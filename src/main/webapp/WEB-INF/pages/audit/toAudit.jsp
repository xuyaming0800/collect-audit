<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>

<title>审核页面</title>
<link href="<%=request.getContextPath()%>/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.box {
     /*非IE的主流浏览器识别的垂直居中的方法*/
     display: table-cell;																	
     vertical-align:middle;

     /*设置水平居中*/
     text-align:center;

     /* 针对IE的Hack */
     *display: block;
     *font-size:175px;/*约为高度的0.873，200*0.873 约为175*/
     *font-family:Arial;/*防止非utf-8引起的hack失效问题，如gbk编码*/

     background-color:#DDD;
    vertical-align:middle;
}
.box img {
     /*设置图片垂直居中*/
     display:block; margin:0 auto;
}
.center {
  width: auto;
  display: table;
  margin-left: auto;
  margin-right: auto;
}
</style>


<script type="text/javascript">

	function selectItem() {
		
		initAuditDetailInfo();
	}
	
	
	
	
	function initAuditDetailInfo() {
		htm = '<li role="presentation" ><a  href="#">7条数据需要审核</a></li>'
			+ '<li role="presentation"><a href="#">9条缺失数据需要审核</a></li>'
			+ '<li role="presentation"><a href="#">3条缺失数据需要审核</a></li>';
			
		$("#auditInfoId").empty();
		$("#auditInfoId").html(htm);
	}
	
	/***
	*显示附件信息
	*/	
	function loadAttachment() {
		$.post("<%=request.getContextPath()%>/audit/getAttachments.html",
			{
				taskId:$("#taskModal #taskId").val()
			},
			function(data){
				$("#attachmentBodyId").empty();
					data = $.parseJSON(data);
					info = data.info;
					var htm = '';
					$.each(info,function(i,n){
						typ = n.type;
						name = '';
						if('url' == typ) {
							name = '<a target="_blank" href="' + n.url + '">' + n.name + '</a>';
						}else {
							name = '<a target="_blank" href="<%=request.getContextPath()%>/audit/downAttachment.html?attachmentId=' + n.id + '">' + n.name + '</a>';
						}
						htm += '<tr>'
							+ '<td style="width: 30%">' + name + '</td>' 
							+ '	<td style="width: 50%">' + n.description + '</td>'
							+ '<td style="width: 20%"><button type="button" class="btn btn-default" onclick="javascript:delAttachment(' + n.id + ')" >删除</button></td>'
							+ '	</tr>';
						
					});
					
					$("#attachmentBodyId").html(htm);
				
		});
	}
	
	//删除附件
	function delAttachment(attachmentId) {
			$.post("<%=request.getContextPath()%>/audit/delAttachment.html",
			{
				attachmentId:attachmentId
			},
			function(data){
				data = $.parseJSON(data);
				if(data.success) {
					loadAttachment();
				}else {
					alert(data.desc);
					return;
				}
				
		});
	}
	
	
	//添加注释信息
	function addComment() {
		taskId = $("#taskId").val();
		processInstanceId = $("#processInstanceId").val();
		commentMessage = $("#commentMessage").val();
		if('' == commentMessage) {
			alert("审核意见不能为空!");
			return;
		}
		$.post("<%=request.getContextPath()%>/audit/addComment.html",
			{
				taskId:taskId,
				processInstanceId:processInstanceId,
				message:commentMessage
			},function(data){
				data = $.parseJSON(data);
				if(data.success) {
					$("#commentMessage").val('');
					loadComments();
				}else {
					alert(data.desc);
				}
			}
		);
	}
	
	/**
	* 加载审批意见
	**/
	function loadComments() {
		taskId = $("#taskModal #taskId").val();
		$.post("<%=request.getContextPath()%>/audit/getComments.html",
			{
				taskId:taskId
			},function(data){
				data = $.parseJSON(data);
				if(data.success) {
					auditinfoBody = $("#auditinfoBodyId").empty();
				 	commentBody = '';
				 	comments = data.info;
					for(var i = 0; i < comments.length; i++) {
						ci = comments[i];
						commentBody += '<tr> <td style="width: 80%">' + ci.fullMessage + '</td>'
							+ '<td style="width: 20%">' 
							+ '<button type="button" class="btn btn-default" onclick="javascript:delComment(' + ci.id + ')" >删除</button> ' 
							+ '</td>'
							+ '</tr>';
					}
					auditinfoBody.html(commentBody);
				}else {
					alert(data.desc);
				}
			}
		);
	}
	
	/**
	*删除审核意见
	*/
	function delComment(commentId) {
		$.post("<%=request.getContextPath()%>/audit/delComment.html",
			{
				commentId:commentId
			},function(data){
				data = $.parseJSON(data);
				if(data.success) {
					loadComments();
				}else {
					alert(data.desc);
				}
			}
		);
	
	}

</script>


</head>

<body >
<div class="container-fluid" >
	<div class="row " >
	  <div class="col-md-2">
	  	<div class="row">
		  <div class="col-md-12">
		  
			<div class="panel panel-default">
			  <div class="panel-heading">审核的项目</div>
			  <div class="panel-body" >
			    
				  
				  
			  <select id="projectId" class="form-control" onchange="selectItem()" >
			    <c:forEach items="${collectAuditSystem}" var="stm">
				  <option value="${stm.id}">${stm.system_name}</option>
				</c:forEach>
     		  </select>
				  
				  
				  
			   
			 <ul id="auditInfoId" class="nav nav-pills nav-stacked">
			  <li role="presentation" ><a href="<%=request.getContextPath()%>/audit/audit.html" target="tsubPage"><span id="auditId">10</span>条数据需要审核</a></li>
			  <li role="presentation"><a href="<%=request.getContextPath()%>/audit/audit.html" target="tsubPage"><span id="lessId">2</span>条缺失数据需要审核</a></li>
			  <li role="presentation"><a href="<%=request.getContextPath()%>/audit/audit.html" target="tsubPage"><span id="">3</span>条申诉数据需要审核</a></li>
			</ul>
			    
			    
			  </div>
			</div>
		  
		  
	
		  
		  
		  
		  
		  </div>
		</div>
	  </div>
	  <div class="col-md-10">
	  	<iframe name="tsubPage" id="tsubPage" width="100%" class="embed-responsive-item"></iframe>
	  </div>
	</div>
</div>

							<!-- 审核信息 -->
							<div class="panel panel-info" id="auditinfoDiv">
								<div class="panel-heading">审核意见信息列表</div>
								<div class="panel-body" id="auditinfo">
									<table class="table">
										<thead>
											<tr>
												<td>信息</td>
												<td>操作</td>
											</tr>
										</thead>
										<tbody id="auditinfoBodyId">
										</tbody>
										<tfoot></tfoot>
									</table>
								</div>
								<span>审核意见：</span>
								<textarea class="form-control" rows="3" id="commentMessage"></textarea>
								<button type="button" onclick="javascript:addComment();"
									class="btn btn-default">添加</button>
							</div>
							<!-- 审核信息 结束-->

							<!-- 附件信息 -->
							<div class="panel panel-info" id="auditAttachmentDiv">
								<div class="panel-heading">审核附件信息列表</div>
								<div class="panel-body">
									<table class="table">
										<thead>
											<tr>
												<td>名称</td>
												<td>描述</td>
												<td>操作</td>
											</tr>
										</thead>
										<tbody id="attachmentBodyId">
											<tr>
												<td style="width: 30%">测试</td>
												<td style="width: 50%">测111试</td>
												<td style="width: 20%"><button type="button"
														class="btn btn-default">删除</button></td>
											</tr>
										</tbody>
										<tfoot></tfoot>
									</table>
								</div>

								<div role="tabpanel">

									<!-- Nav tabs -->
									<ul class="nav nav-tabs" role="tablist">
										<li role="presentation" class="active"><a href="#home"
											aria-controls="home" role="tab" data-toggle="tab">URL</a></li>
										<li role="presentation"><a href="#profile"
											aria-controls="profile" role="tab" data-toggle="tab">文件</a></li>
									</ul>

									<!-- Tab panes -->
									<div class="tab-content">
										<div role="tabpanel" class="tab-pane active" id="home">
											<form id="my_form" method="post"
												action="<%=request.getContextPath()%>/audit/addUrlAttachment.html"
												target="hiddenIframe">
												<input type="hidden" name="taskId" id="taskId" /> <input
													type="hidden" name="processInstanceId"
													id="processInstanceId" />
												<table class="table">
													<tr>
														<td>名称：</td>
														<td><input name="attachmentName" style="width: 100%"></td>
													</tr>
													<tr>
														<td>描述：</td>
														<td><input name="attachmentDescription"
															style="width: 100%"></td>
													</tr>
													<tr>
														<td>URL：</td>
														<td><input name="url" style="width: 100%"></td>
													</tr>

													<tr>
														<td colspan="2"><input type="submit"
															class="btn btn-default" value="添加"></td>
													</tr>
												</table>
											</form>

										</div>
										<div role="tabpanel" class="tab-pane" id="profile">
											<form method="post"
												action="<%=request.getContextPath()%>/audit/addUrlAttachment.html"
												target="hiddenIframe">
												<input type="hidden" name="taskId" id="taskId" /> <input
													type="hidden" name="processInstanceId"
													id="processInstanceId" />
												<table class="table">
													<tr>
														<td>名称：</td>
														<td><input name="attachmentName" style="width: 100%"></td>
													</tr>
													<tr>
														<td>描述：</td>
														<td><input name="attachmentDescription"
															style="width: 100%"></td>
													</tr>
													<tr>
														<td>URL：</td>
														<td><input name="url" style="width: 100%"></td>
													</tr>

													<tr>
														<td colspan="2"><input type="submit"
															class="btn btn-default" value="添加"></td>
													</tr>
												</table>
											</form>
										</div>
									</div>
								</div>

							</div>
							<!-- 附件信息 结束-->
	<script type="text/javascript">
		function go2Page(itemId, _url) {
			$(function() {
				$('#tsubPage').attr('src', _url);
			});
		}
		function autoResizeIframe() {
			$(function() {
				$('#tsubPage').css({
					'position' : 'absolute',
					'top' : $('#menu').height(),
					'height' : $(window).outerHeight() - $(top.document.getElementById("#menu")).height(),
					'border' : 0
				});
			});
		}
		
		
		
		$(function() {
			autoResizeIframe();
			$(window).resize(function() {
				autoResizeIframe();
			});
		});

	</script>
</body>
</html>