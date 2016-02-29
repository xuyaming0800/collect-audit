<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<title>任务认领列表</title>
<style type="text/css">
#tbody_content td{
	text-align: center;
}
th {
	text-align: center;
}
.panel-body{
	padding: 5px;
}
.td_sty{
    font-size: larger;
    font-weight: bolder;
    width:20%;
}
.required{
	color: red;padding-right: 5px;
}
.status_div{
    font-size: small;
    color: gainsboro;
    margin-left: 30%;
}
.bd{
	border: 0;font: 16px/18px arial;color: black;width: 100px;
}
</style>
</head>
<body>
	<div id="containerId" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading" style="display: -webkit-box;">
				<h3 class="panel-title">任务认领列表（双击修改）</h3>
				<div class="status_div">
					<label style="margin-bottom:0px">客户名称：</label>
					<input type="text" id="customName_query" value="" class="bd">
					<label style="margin-bottom:0px">项目名称：</label>
					<input type="text" id="systemName_query" value="" class="bd">
					<label style="margin-bottom:0px">认领人名称：</label>
					<input type="text" id="claimName_query" value="" class="bd">
				</div>	
			</div>
			<div class="panel-body">
				<button onclick="javascript:openAuditDialog();" type="button"
					class="btn btn-success btn-xs" style="float:right;">添加分配</button>
			</div>
			<table class="table table-striped">
					<thead>
						<tr>
							<th>序号</th>
							<th>客户名称</th>
							<th>项目名称</th>
							<th>认领类型</th>
							<th>认领人名称</th>
							<th>创建人名称</th>
							<th>创建时间</th>
							<th>修改人名称</th>
							<th>修改时间</th>
							<th>操作</th>
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
	
	<!-- Modal -->
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="border-radius:0px">
				<div class="modal-header" style="background-color:ivory;">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">添加/修改项目分配</h4>
				</div>
				<div class="modal-body" style="padding-bottom:inherit;padding-top:inherit;">
					<table class="table dssBasic" style="margin-bottom: auto;">
						<tr>
							<td class="td_sty"><span class="required">*</span>客户:</td>
							<td width="80%"><select id="customId" name="customId"
								class="form-control">
									<option value="">请选择</option>
							</select>
							</td>
						</tr>
						<tr>
							<td class="td_sty"><span class="required">*</span>项目:</td>
							<td width="80%"><select id="systemId" name="systemId"
								class="form-control">
									<option value="">请选择</option>
							</select>
							</td>
						</tr>
						<tr>
							<td class="td_sty"><span class="required">*</span>认领类型</td>
							<td><select id="claimType" name="claimType"
								class="form-control">
									<option value="">请选择</option>
									<option value="1">初审</option>
									<option value="2">抽检</option>
									<option value="4">申诉</option>
							</select></td>
						</tr>
						<tr>
							<td class="td_sty"><span class="required">*</span>认领人:</td>
							<td width="80%"><select id="claimUserId" name="claimUserId"
								class="form-control">
									<option value="">请选择</option>
							</select></td>
						</tr>
					</table>
					<input type="hidden" id="id" value="">
				</div>
				<div class="modal-footer" style="padding:8px;margin-right:15px;">
					<button type="button" class="btn btn-primary" data-dismiss="modal">取消</button>
					<button type="button" onclick="addClaim();" class="btn btn-primary">提交</button>
				</div>
			</div>
		</div>
	</div>
<script src="<%=request.getContextPath()%>/js/bootstrap-paginator.min.js"></script>
<script type="text/javascript">
	var pageNo = 1,//页码
	pageSize=10,//分页大小
	status=null,
	claimName_query='',
	customName_query='',
	systemName_query='',
	context = "<%=request.getContextPath()%>";
	$(function(){
		
		$("#claimName_query").blur( 
			function () {
				if($("#claimName_query").val() != claimName_query){
					query();
					claimName_query = $("#claimName_query").val();
				}
			} 
		);
		$("#customName_query").blur( 
			function () {
				if($("#customName_query").val() != customName_query){
					query();
					customName_query = $("#customName_query").val();
				}
			} 
		);
		$("#systemName_query").blur( 
			function () {
				if($("#systemName_query").val() != systemName_query){
					query();
					systemName_query = $("#systemName_query").val();
				}
			} 
		);
		
		//初始化要审核的项目
		initCustom();
		
		//初始化认领人
		initClaimUsers();

		//查询
		query();
	});
	
	//初始化
	function initCustom(){
		$.post(context+"/claim/getCustoms.html",{
			customName:""
		},function(data){
			if(data!=null && data!=""){
				$("#customId").empty().append("<option value=''>请选择</option>");
				$.each(data.info,function(i,n){
					$("<option value='" + n.id + "'>"+n.name+"</option>").appendTo("#customId");
				});
			}
		},"json");
	}
	function initClaimUsers(){
		$.post(context+"/claim/getClaimUsers.html",{
			customName:""
		},function(data){
			if(data!=null && data!=""){
				$("#claimUserId").empty().append("<option value=''>请选择</option>");
				$.each(data.info,function(i,n){
					$("<option value='" + n.id + "'>"+n.name+"</option>").appendTo("#claimUserId");
				});
			}
		},"json");
		
		$("#customId").bind( "change", function(){
            getProjects($( "#customId").val(),null);
     	});
	}
	
	//查询
	function query(op){
		if(op){
			status = $(op).val();
		}else
			status = $("input[name='status']:checked").val();
		$.post(context+"/claim/queryClaimList.html",{
			pageNo:pageNo,
			pageSize:pageSize,
			claimUserName:$("#claimName_query").val(),
			customName:$("#customName_query").val(),
			systemName:$("#systemName_query").val()
		},function(data){
			if (data.success) {
				var containerBody = $("#tbody_content").empty();
				var htm = "";
				if(data.info.objectList != null && data.info.objectList.length > 0) {
					var startNo=(pageNo-1)*pageSize+1;
					$.each(data.info.objectList,function(i,n){
		    			$("<tr/>")
						.append("<td>" + (startNo+i) + "</td>")
						.append("<td>" + n.customName + "</td>")
						.append("<td>" + n.systemName + "</td>")
						.append("<td>" + transform(n.claimType) + "</td>")
						.append("<td>" + n.claimUserName + "</td>")
						.append("<td>" + n.createByName + "</td>")
						.append("<td>" + new Date(n.createTime).Format("yyyy-MM-dd hh:mm") + "</td>")
						.append("<td>" + (n.updateByName==null?'-':n.updateByName) + "</td>")
						.append("<td>" + (n.updateTime==null?'-':new Date(n.updateTime).Format("yyyy-MM-dd hh:mm")) + "</td>")
						.append("<td><button type='button' class='btn btn-danger' onclick='delClaim(\""
		    					+n.id+"\");'>删除</button></td>")
		    			.dblclick(function(e){
		    				updateClaim(n.id,n.claimType,n.customId,n.systemId,n.claimUserId);
		    			}).appendTo(containerBody);
					});
					//containerBody.html(htm);
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
	function transform(ct){
		var claimTypeName = "";
		if(ct==1){
			claimTypeName = "初审";
		}else if(ct==2){
			claimTypeName = "抽检";
		}else{
			claimTypeName = "申诉";
		}
		return claimTypeName;
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
	function addClaim(){
		var customId = $("#customId").val();
		var systemId = $("#systemId").val();
		var claimType = $("#claimType").val();
		var claimUserId = $("#claimUserId").val();
		var id = $("#id").val();
		if(!(customId && systemId && claimType && claimUserId)){
			alert("必填项不能为空");return;
		}
		$.post(context+"/claim/add.html",{
			customId:customId,
			systemId:systemId,
			claimType:claimType,
			claimUserId:claimUserId,
			claimUserName:$("#claimUserId").find("option:selected").text(),
			customName:$("#customId").find("option:selected").text(),
			systemName:$("#systemId").find("option:selected").text(),
			id:id
		},function(data){
			if (data.success) {
				closeAuditDialog();
				query();
			} else {
				alert(data.desc);
			}
		},"json");
	};
	
	function openAuditDialog() {
  		emptyDialog();
  		$("#addModal").modal({});
  	};
	
	function closeAuditDialog() {
  		emptyDialog();
  		$("#addModal").modal("hide");
  	};
	
	function emptyDialog() {
		$("#id").val('');
		$("#customId option[value='']").attr("selected",true);
		$("#systemId option[value='']").attr("selected",true);
		$("#claimType option[value='']").attr("selected",true);
		$("#claimUserId option[value='']").attr("selected",true);
	};
	
	function updateClaim(id,claimType,customId,systemId,claimUserId){
		getProjects(customId,systemId);
		$("#customId option[value="+customId+"]").attr("selected",true);
		$("#claimType option[value="+claimType+"]").attr("selected",true);
		$("#claimUserId option[value="+claimUserId+"]").attr("selected",true);
		$("#systemId option[value="+systemId+"]").attr("selected",true);
		$("#id").val(id);
		$('#addModal').modal('show')
	};
	
	//查询客户对应的项目信息
	function getProjects(customId,systemId){
		$.post(context+"/claim/getProjects.html",{
        	customId:customId
        },function(data){
         	$("#systemId").empty().append("<option value=''>请选择</option>");
         	if(data.info.objectList.length <= 0 )
         		alert("当前用户没有可用项目");
           	$.each(data.info.objectList,function(i,n){
           		if(systemId && systemId==n.id)
					$("<option value='" + n.id + "' selected='true'>"+n.projectName+"</option>").appendTo("#systemId");
           		else $("<option value='" + n.id + "'>"+n.projectName+"</option>").appendTo("#systemId");
			});
		},"json");
	}
	
	//删除
	function delClaim(op){
		 if(window.confirm("确定删除该项吗?")){
		 	$.post(context+"/claim/del.html",{
        		id:op
	        },function(data){
	        	if (data.success) {
					query();
				} else 
					alert(data.desc);
			},"json");
		 }
	};
</script>
</body>
</html>
