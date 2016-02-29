<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<title>支付记录管理</title>
<style type="text/css">
#tbody_content td{
	text-align: center;
}
th {
	text-align: center;
}
.font_color{
	color: yellow; 
	margin-left: 5px;
}
.status_div{
    font-size: small;
    color: gainsboro;
    margin-left: 30%;
}
.bd{
	margin: 5px 0 0 7px;border: 0;font: 16px/18px arial;color: black;width: 120px;
}
</style>
</head>
<body>
	<div id="containerId" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading" style="display: -webkit-box;">
				<h3 class="panel-title">支付记录列表</h3>
				<div class="status_div">
					<label>任务名称：</label>
					<input type="text" name="taskName" id="taskName" value="" class="bd">
					<label style="margin-left: 20px;">支付状态：</label>
					<span>
						<input type="radio" id="pay_all_rd" name="status" checked="checked" value="">
						<label for="pay_all_rd">全部</label>
					</span>
					<span>
						<input type="radio" id="pay_fall_rd" name="status" value="2">
						<label for="pay_fall_rd">支付失败</label>
					</span>
					<span>
						<input type="radio" id="pay_succ_rd" name="status" value="1">
						<label for="pay_succ_rd">支付成功</label>
					</span>
					<span>
						<input type="radio" id="pay_o_rd" name="status" value="0">
						<label for="pay_o_rd">支付无响应</label>
					</span>
				</div>
			</div>
			<div class="panel-body">
			</div>
			<table class="table table-striped">
					<thead>
						<tr>
							<th>序号</th>
							<th>任务ID</th>
							<th>任务名称</th>
							<th>支付状态</th>
							<!-- <th>发送信息</th> -->
							<th>创建时间</th>
							<th>修改时间</th>
							<!-- <th>操作</th> -->
						</tr>
					</thead>
					<tbody id="tbody_content">
					</tbody>
				</table>
			</div>
		<div style="text-align: right">
			<ul id="projectPage" class="pagination-sm">
			</ul>
		</div>
	</div>
<script src="<%=request.getContextPath()%>/js/bootstrap-paginator.min.js"></script>
<script type="text/javascript">
	var pageNo = 1,//页码
	pageSize=10,//分页大小
	status=null,
	taskName='',
	context = "<%=request.getContextPath()%>";
	$(function(){
		$("input[name='status']").each(function(){//循环绑定事件  
			if(this.checked){
				old = this; //如果当前对象选中，保存该对象  
			}  
			this.onclick = function(){  
				if(this != old){ 
					query(this);
					old = this; 
				} 
			}  
		}); 
		$("#taskName").blur( 
			function () {
				if($("#taskName").val() != taskName){
					query();
					taskName = $("#taskName").val();
				}
			} 
		);
		query();
	});
	function query(op){
		if(op){
			status = $(op).val();
		}else
			status = $("input[name='status']:checked").val();
		$.post(context+"/pay/record/query.html",{
			pageNo:pageNo,
			pageSize:pageSize,
			status:status,
			taskName:$("#taskName").val()
		},function(data){
			if (data.success) {
				var containerBody = $("#tbody_content").empty();
				var htm = "";
				if(data.info.objectList != null && data.info.objectList.length > 0) {
					var startNo=(pageNo-1)*pageSize+1;
					$.each(data.info.objectList,function(i,n){
					    htm +=  (i%2==1?'<tr class="odd">':'<tr>')
	 	    			+ "<td>" + (startNo+i) + "</td>"
		    			+ "<td>" + n.taskId + "</td>"
		    			+ "<td>" + n.taskName + "</td>";
					    if(n.status==1){
					    	htm += "<td style='background-color:#D9FDA2;color:#109E59;font-weight:bold;'>支付成功</td>";
						}else if(n.status==0){
					    	htm += "<td style='background-color:#F3F5F9;color:#FDA003;font-weight:bold;'>支付无响应</td>";
						}else{
					    	htm += "<td style='background-color:#F4D3E3;color:#DB4537;font-weight:bold;'>支付失败</td>";
						}
					    htm +=
		    			/* + "<td>" + n.content + "</td>" */
		    			"<td>" + new Date(n.createDate).Format("yyyy-MM-dd hh:mm") + "</td>"
		    			+ "<td>" + new Date(n.updateDate).Format("yyyy-MM-dd hh:mm") + "</td>"
		    			//+ (n.status==1?"<td>无</td>":"<td><button type='button' class='btn btn-info' onclick='doPay(\""+n.id+"\",\""+n.status+"\");'>支付</button></td>")
		    			//+ "<td><button type='button' class='btn btn-info' onclick='doPay();'>支付</button></td>"
		    			+ "</tr>";
					});
					containerBody.html(htm);
					options.onPageClicked=function(event, originalEvent, type, page){
						pageNo = page;
						query();
			        };
			        options.currentPage = pageNo;
					initPage(options,"projectPage",data.info.totalCount,data.info.limit);
				}else {
					containerBody.html("<tr><td colspan='7'>未查询到数据</td></tr>");
				}
			} else {
				var containerBody = $("#tbody_content").empty();
				containerBody.html("<tr><td colspan='7'>"+data.desc+"</td></tr>");
			}
		},"json");
	}
	//分页参数
	var options = {
	   	size:"sm",
	    bootstrapMajorVersion:3,
	    currentPage:1,
	    numberOfPages:5,
	    onPageClicked:function(event, originalEvent, type, page){
		}
	}
	function initPage(options,id,totalCount,limit) {
		//分页显示
		var pageElement = $("#"+id+"");
	     options.totalPages=Math.floor(totalCount%limit==0?(totalCount/limit):(totalCount/limit+1));
	    pageElement.bootstrapPaginator(options);
	}
	
	//支付接口调用
	/* function doPay(op,sta){
		if(sta==1){
			alert("支付成功无法支付");return;
		}
		$.post(context+"/pay/record/doPay.html",{
			id:op,
			status:sta
		},function(data){
			if (data.success) {
				if(data.info && data.info.success){
					query();
				}else{
					alert("支付失败message：" + data.info.desc);
				}
			} else {
				alert(data.desc);
			}
		},"json");
	} */
</script>
</body>
</html>
