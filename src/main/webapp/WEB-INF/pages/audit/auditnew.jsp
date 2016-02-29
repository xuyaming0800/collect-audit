<%@page import="com.autonavi.audit.constant.TASK_STATUS"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>

<title>审核</title>
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

.opacity {
	filter: alpha(opacity = 9); /*IE滤镜，透明度50%*/
	-moz-opacity: 0.9; /*Firefox私有，透明度50%*/
	opacity: 0.9; /*其他，透明度50%*/
}

.hover {
	color: #68cdf6;
}

#searchCondition {
	display: none;
	position: absolute;
	border: 1px solid #DDDDDD;
	background-color: #fff;
	z-index: 1009;
	padding: 5px;
	border-radius: 5px;
	width:300px;
}

.topbutton {
	z-index: 99999999;
}
/**编辑平台样式**/
#addTypeDiv {
	display: none;
	position: absolute;
	border: 1px solid #DDDDDD;
	background-color: #fff;
	z-index: 1009;
	padding: 5px;
	border-radius: 5px;
	width:300px;
}
</style>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.json2tree.css">
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=GGBqMj92BHolANNEDpe6TlIc"></script>
<script src="<%=request.getContextPath()%>/js/squareOverlay.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.rotate.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.json2tree.js" type="application/javascript"></script>
<!--加载鼠标绘制工具-->
<script type="text/javascript" src="http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.js"></script>
<link rel="stylesheet" href="http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.css" />
<script type="text/javascript">
	var contextPath = "<%=request.getContextPath()%>",
	map,//百度地图
	bsType,//项目ID
	type,//任务类型ID(普通任务、缺失任务、申诉)
	cpage,//页号
	taskTypeId,//任务类ID（未认领、已认领、经办）
	taskId,//工作流任务ID
	id,//业务ID
	processInstanceId,//工作流流程实例ID
	processDefinitionId,//工作流流程定义ID
	attrs;
	$(function(){
	
		//计算baiduMap的高度
		function resizeBaiduMap() {
			var newHeight = $(window).innerHeight()-$("#allmap").offset().top-95;
			$("#allmap").height(newHeight);
		}
		//计算baiduMap的高度
		resizeBaiduMap();
		$(window).resize(resizeBaiduMap);
		// 百度地图API功能
		map = new BMap.Map("allmap");    // 创建Map实例
		map.centerAndZoom(new BMap.Point(116.404, 39.915), 18);  // 设置中心点坐标和地图级别
		map.setCurrentCity("北京");          // 设置地图显示的城市 此项是必须设置的
		map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
		map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
		map.addControl(new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT}));// 左上角，添加比例尺
		map.addControl(new BMap.NavigationControl());  //左上角，添加默认缩放平移控件
		
		//$("#allmap").pin();//将地图钉在固定位置
		
			
		//初始化要审核的项目
		$.post(contextPath+"/audit/getObjects.html",null,function(data){
			if(data!=null && data!=""){
				data = $.parseJSON(data);
				$.each(data,function(i,n){
					$('<li><a href="#" objectId="'+n.id+'">'+n.system_name+'</a></li>').appendTo("#getObjects");
				});
				//绑定事件
				$("#getObjects li > a").click(function(){
					var self = $(this);
					$("#oName").text(self.text());
					bsType = self.attr("objectId");//项目ID
					refreshCount();//刷新个数
				})
				
				//定义任务类型ID(普通任务、缺失任务、申诉任务)
				$("#auditCount,#lessCount,#appealCount").parent("a")
				.click(function(){
					var ad = $(this);
					ad.siblings("a").removeClass("active").end().addClass("active");
					if(ad.find("span#auditCount").size()==1){
						type=4;
					} else if(ad.find("span#lessCount").size()==1){
						type=7;
					}else if(ad.find("span#appealCount").size()==1){
						type=9;
					}
					$("#taskType > li > a[id='frTask'] ").click();//未认领任务表格
				});
			}
		});
		
		//刷新个数
		function refreshCount(){
			if(bsType){
				$.post(contextPath+"/audit/getObjectCounts.html",{
					objectId:bsType
				},function(data){
					if(data!=null && data!=""){
						data = $.parseJSON(data);
						$("#auditCount").text(data.auditCount);
						$("#lessCount").text(data.lessCount);
						$("#appealCount").text(data.appealCount);
					}
				});
			}
		}
		
		setInterval(refreshCount,10000);//每10秒自动刷新一次个数
	
		//计算taskList的高度
		function resizeTaskList() {
			var newHeight = $(window).innerHeight()-$("#taskList").offset().top-60;
			$("#taskList").height(newHeight < 300 ? 300 : newHeight);
		}
		//定义菜单的事件
		//认领全部
		$("#taskListContextMenu > li#claimAll").click(function(){
			var msg = "",count = 0;
			$("#taskList > a").each(function(i,n){
				var taskName = $(this).children("h5:eq(0)").text();
				var _taskId = $(this).data("taskId");
				$.post(contextPath+"/audit/claimTask.html",
					{
						taskId:_taskId
					},
					function(data){
						if(data == ""){
							msg += "任务【"+taskName+"】认领成功！\n";
						}
						else msg += "任务【"+taskName+"】认领失败！原因："+data+"\n" ;
						if(count == i){
							alert(msg);
							$("#taskType > li > a[id='frTask'] ").click();//刷新表格
						}
						count++;
				});
			});
			
		});
		
		//计算taskList的高度
		resizeTaskList();
		$(window).resize(resizeTaskList);

		//任务类型菜单初始化
		$("#taskType > li ").click(
				function() {
					$(this).parent("ul").prev("button").children("span#taskTypeName").text($(this).children("a").text());//修改文字
					taskTypeId = $(this).children("a").attr("id");
					cpage = 0;
					showTask(taskTypeId, cpage, true, function(){
						//taskList无限下滑
						$("#taskList").scrollview({
							callback : function(el,fn){
								showTask(taskTypeId, ++cpage, false);
								fn();
							}
						});
					});
				});

		//显示任务明细
		function showTask(taskTypeId, cpage, isEmpty, callback) {
			if (!cpage) {
				cpage = 0;
			}
			if (taskTypeId == "frTask") {//未认领任务
				fullDetail(contextPath+"/audit/findRepresentativeTask.html");
			} else if (taskTypeId == "fcTask"){//已认领任务
				fullDetail(contextPath+"/audit/findClaimTask.html");
			} else if (taskTypeId == "fmiTask"){//经办任务
				fullDetail(contextPath+"/audit/findMyselfInvolvedTask.html");
			}
			
			//填充明细信息
			function fullDetail(url){//:visible
				var _p = $("#searchCondition > form").serialize();
				if(_p && _p != "")
					url = url + "?" + _p;
				$.post(url,{
					page:cpage,
					bsType: bsType,
					type:type
				},function(data){
					var taskList = $("#taskList");
					if(isEmpty) taskList.empty();
					if(data != null && data.length > 0){
						data = $.parseJSON(data);
						$.each(data,function(i,n){
							$('<a href="#" class="list-group-item"/>')
							.data("taskId",n.taskId)
							.append('<h5 class="list-group-item-heading">'+n.collect_task_name+'（'+n.task_class_name+'）</h5>')
							.append('<p class="list-group-item-text">用户：'+n.user_name+' 采集时间：'+new Date(n.submit_time).Format("yyyy-MM-dd hh:mm:ss")+'</p>')
							.click(function(e){
								$(this).siblings("a.list-group-item").removeClass("active").end().addClass("active");//选中
								taskId = n.taskId;//工作流任务ID放在公共区域
								processInstanceId = n.process_instance_id;//工作流流程实例ID
								processDefinitionId = n.process_definition_id;//工作流流程定义ID
								//**** 1,左下角列表-点击事件:显示树形结构
								//***********************************************
								$.post(contextPath+"/audit/getDetail.html",{
									taskId:taskId
								},function(data){
									if(data!=""){
										data = $.parseJSON(data);
										attrs = data.attrs;
										$("#ztreeContent").json2tree({
											json:data,
											click: function(e){
												var t= $(e.target);
												if(t.parents("li.imgs").parentsUntil("li.imgs").size()>0){
													//显示图片出来
													//economicEnvironment_showPic(t.image_url);
													alert(t.next("ui #image_url").val());
												}
											}
										});
										//$("li.imgs > ul > li > ul","#ztreeContent").remove();
										toggleDialog($("#ztree"));
									}
								});
								//***********************************************
								fullPhotoAndMarkMap(n.id,n.system_type);//填充图片并标注地图
								//任务明细
								$("span","#taskDetialPanel table").text("");
								for(tmp in n){
									if(tmp == "submit_time"){
										$("span#"+tmp,"#taskDetialPanel").text(new Date(n[tmp]).Format("yyyy-MM-dd hh:mm:ss"));
										$("span#"+tmp,"#taskDetialPanel").text(n[tmp]);	
									}else if(tmp == "collect_task_name"){
										$("span#"+tmp,"#taskDetialPanel").text(n[tmp]+'（'+n["task_class_name"]+')');
										$("span#"+tmp,"#taskDetialPanel").text(n[tmp]);	
									}else{
										$("span#"+tmp,"#taskDetialPanel").text(n[tmp]);
									}
								}
								//审核通过的道路赋值
								$("#audit_task_name").val(n.collect_task_name);
								//清空确认类型
								$("#taskClassNameFromAudit").val("");
								//根据类型显示按钮
								$("#optionButtons > button").hide();
								$("#specimenPageButton").show();//样张
								$("#taskDetailButton").show();//任务明细
								$("#auditHistoryButton").show();//审核历史明细
								
								if(type == 4){//普通任务
									$("#userPhotoButton").show();//用户拍摄的照片
								}else if(type == 7){//未找到任务
									$("#userMp4Button").show();//用户拍摄的录像
								}else if(type == 9){//申诉任务
									$("#userMp4Button").show();//用户拍摄的录像
									$("#userPhotoButton").show();//用户拍摄的照片
								}
								
								if (taskTypeId == "frTask") {//未认领任务
									$("#claimTaskButton").show();//认领
								} else if (taskTypeId == "fcTask"){//已认领任务
									$("#unClaimTaskButton").show();//取消认领
									$("#auditSuccessButton").show();//审核通过
									$("#auditFailureButton").show();//审核不通过
									if(n.status==6||n.status==7){//审核状态为6或7则隐藏按钮
										$("#appealTaskButton").hide();//申诉
										$("#auditSuccessButton").hide();//审核通过
										$("#auditFailureButton").hide();//审核不通过
									}
								} else if (taskTypeId == "fmiTask"){//经办任务
									
								}
							})
							//.on("taphold",function(e){//taphold 事件在用户敲击某个元素并保持一秒时被触发
							//  showContextMenu(e);
							//})
							.bind("contextmenu",function(e){
								if(taskTypeId == "frTask"){
									showContextMenu(e);
							    	return false;//屏蔽右键菜单
								}
								return true;
						    }).appendTo(taskList);
						    
						    //显示或隐藏右键菜单
						    function showContextMenu(e){
						    	var targetOffset = $(e.target).offset();
						    	var targetHeight = $(e.target).height();
						    	$("#taskListContextMenu")
						    	.show()
						    	.offset({
						    		top: targetOffset.top + targetHeight, left: targetOffset.left 
						    	});
						    	$("body").one("click", function(){
									$("#taskListContextMenu").hide();
								});
						    }
						});
					}
					if(callback) setInterval(callback,250);
				});
			}
			
			//填充图片并标注地图
			function fullPhotoAndMarkMap(id,sysType){
				//照片、录像、地图
				$.post(contextPath+"/audit/findPhoto.html",{
					id:id,
					system_type:sysType
				},function(data){
					if(data != null && data.length > 0){
						data = $.parseJSON(data);
						var oc = data.originalCoordinate;//原始坐标
						var ooc = data.otherCoordinates;//区域内其他坐标
						var area = data.area;//区域
						var centerPoint = data.centerPoint;//任务中心点
						map.clearOverlays();//清空地图
						if(centerPoint.lon==null || centerPoint.lat==null){
							alert("没有近景图片或者远近景标识为空，地图定位失败");
							return;
						}
						//样张
						$(".carousel-indicators,.carousel-inner","#specimenPagePanel #specimenPageCarousel").find("*").remove();
						$.each(data.specimenImage,function(i,n){
							//轮播组件
							//小点
							$("#specimenPagePanel #specimenPageCarousel > .carousel-indicators")
							.append('<li data-target="#specimenPageCarousel" data-slide-to="'+i+'" '+(i==0?'class="active"':'')+'></li>');
							//图片
							var _img = $('<div class="item '+(i==0?"active":"")+' text-center"><img class="center-block img-responsive" style="cursor:pointer;" src="'+n.thumbnai_url+'"/></div>');
							//点击图片事件
							_img.click(function(){
								window.open(n.image_url);
							});
							//插入图片
							$("#specimenPagePanel #specimenPageCarousel > .carousel-inner").append(_img);
						});
						//默认停止轮播，等待用户操作
						$("#specimenPagePanel #specimenPageCarousel").carousel('pause');
						var slideMap = new Map();
						
						//用户拍摄
						$(".carousel-indicators,.carousel-inner","#userPhotoPanel #userPhotoCarousel").find("*").remove();
						$.each(data.userPhoto,function(i,n){
							//轮播组件
							//小点
							$("#userPhotoPanel #userPhotoCarousel > .carousel-indicators")
							.append('<li data-target="#userPhotoCarousel" data-slide-to="'+i+'" '+(i==0?'class="active"':'')+'></li>');
							if(type == 7){
								//录像
								if(i==0){
									$('#userMp4Panel video#videoMp4').attr('src',n.video_url);
									$('#userMp4Panel a#videoMp4dow').attr('href',n.video_url);
									
									//缺失原因
									$('#userMp4Panel #noExistReason').html(n.no_exist_reason);
								}else{
									$('#userMp4Panel .panel-body>div').clone().appendTo('#userMp4Panel .panel-body');
									$('#userMp4Panel video#videoMp4:last').attr('src',n.video_url);
									$('#userMp4Panel a#videoMp4dow:last').attr('href',n.video_url);
									
									//缺失原因
									$('#userMp4Panel #noExistReason:last').html(n.no_exist_reason);
								}
								
								//地图2
								if(i==0) _addOverlay(false,data.userPhoto,map,centerPoint.lon,centerPoint.lat,n.point_accury,n.position,oc,ooc,area,data.gisType);//如果是第一个，直接添加
								slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:oc,gisType:data.gisType});//将事件加入map
							}else if(type == 4){
								//图片
								var _img = $('<div class="item '+(i==0?"active":"")+' text-center"><input name="isuse" type="hidden"/><input name="imgId" type="hidden"/><input name="index" type="hidden"/>'
											+'<img class="center-block img-responsive" style="cursor:pointer;" src="'+n.thumbnai_url+'"/>'
											+'<div class="carousel-caption">拍照时间:'+new Date(n.photograph_time).Format("yyyy-MM-dd hh:mm:ss")
											+';卫星颗数:'+n.point_level
											+';拍摄方式:'+(n.index%2 == 1?"远景":"近景")+'</div>'
											+'</div>');
								//点击图片事件
								_img.click(function(){
									window.open(n.image_url);
								});
								$("input[name=isuse]",_img).val(n.used);
								$("input[name=imgId]",_img).val(n.id);
								$("input[name=index]",_img).val(n.index);
								//插入图片
								$("#userPhotoPanel #userPhotoCarousel > .carousel-inner").append(_img);
								//对公交站亭增加无效图片的功能
								if(data.className=="公交站亭"){
									$("#invalidDiv").show();
									var isUsedFlag;
									$("#invalid").unbind().on("change",function(e){
										if(document.getElementById("invalid").checked){
											$(".item.text-center.active input[name=isuse]").val(0);
											isUsedFlag=false;
										}else{
											$(".item.text-center.active input[name=isuse]").val(1);
											isUsedFlag=true;
										}
										$.post(contextPath+"/audit/updateMoneyPhotoUsed.html",{
											taskId:n.audit_id,		//任务id
											index:$(".item.text-center.active input[name=index]").val(),			//远景近景
											imgId:$(".item.text-center.active input[name=imgId]").val(),
											type:data.className,	//任务类型名称
											isUsed:isUsedFlag		//使用与否
										},function(price){
											if(price!=""){
												price = $.parseJSON(price);
												$("#task_amount").text(price);
											}else{
												alert("修改无效");
												if(isUsedFlag){
													$(".item.text-center.active input[name=isuse]").val(0);
													$("#invalid").attr("checked", false);
												}else{
													$(".item.text-center.active input[name=isuse]").val(1);
													$("#invalid").attr("checked", true);
												}
											}
										});
									});
								}else
									$("#invalidDiv").hide().unbind();
								if(i==0) {
									if(n.used==0)
										$("#invalidDiv #invalid").prop("checked",true);
									else $("#invalidDiv #invalid").prop("checked",false);
									_addOverlay(false,data.userPhoto,map,centerPoint.lon,centerPoint.lat,n.point_accury,n.position,oc,ooc,area,data.gisType);//如果是第一个，直接添加
								}
								slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:oc,gisType:data.gisType});//将事件加入map
							}else if(type == 9){
								//录像
								if(i==0){
									$('#userMp4Panel video#videoMp4').attr('src',n.video_url);
									$('#userMp4Panel a#videoMp4dow').attr('href',n.video_url);
									//缺失原因
									$('#userMp4Panel #noExistReason').html(n.no_exist_reason);
								}else{
									$('#userMp4Panel .panel-body>div').clone().appendTo('#userMp4Panel .panel-body');
									$('#userMp4Panel video#videoMp4:last').attr('src',n.video_url);
									$('#userMp4Panel a#videoMp4dow:last').attr('href',n.video_url);
									//缺失原因
									$('#userMp4Panel #noExistReason:last').html(n.no_exist_reason);
								}
								//图片
								var _img = $('<div class="item '+(i==0?"active":"")+' text-center"><input name="isuse" type="hidden"/><input name="imgId" type="hidden"/><input name="index" type="hidden"/>'
											+'<img class="center-block img-responsive" style="cursor:pointer;" src="'+n.thumbnai_url+'"/>'
											+'<div class="carousel-caption">拍照时间:'+new Date(n.photograph_time).Format("yyyy-MM-dd hh:mm:ss")
											+';卫星颗数:'+n.point_level
											+';拍摄方式:'+(n.index%2 == 1?"远景":"近景")+'</div>'
											+'</div>');
								//点击图片事件
								_img.click(function(){
									window.open(n.image_url);
								});
								$("input[name=isuse]",_img).val(n.used);
								$("input[name=imgId]",_img).val(n.id);
								$("input[name=index]",_img).val(n.index);
								//插入图片
								$("#userPhotoPanel #userPhotoCarousel > .carousel-inner").append(_img);
								//对公交站亭增加无效图片的功能
								if(data.className=="公交站亭"){
									$("#invalidDiv").show();
									var isUsedFlag;
									$("#invalid").unbind().on("change",function(e){
										if(document.getElementById("invalid").checked){
											$(".item.text-center.active input[name=isuse]").val(0);
											isUsedFlag=false;
										}else{
											$(".item.text-center.active input[name=isuse]").val(1);
											isUsedFlag=true;
										}
										$.post(contextPath+"/audit/updateMoneyPhotoUsed.html",{
											taskId:n.audit_id,		//任务id
											index:$(".item.text-center.active input[name=index]").val(),			//远景近景
											imgId:$(".item.text-center.active input[name=imgId]").val(),
											type:data.className,	//任务类型名称
											isUsed:isUsedFlag		//使用与否
										},function(price){
											if(price!=""){
												price = $.parseJSON(price);
												$("#task_amount").text(price);
											}else{
												alert("修改无效");
												if(isUsedFlag){
													$(".item.text-center.active input[name=isuse]").val(0);
													$("#invalid").attr("checked", false);
												}else{
													$(".item.text-center.active input[name=isuse]").val(1);
													$("#invalid").attr("checked", true);
												}
											}
										});
									});
								}else
									$("#invalidDiv").hide().unbind();
								if(i==0) {
									if(n.used==0)
										$("#invalidDiv #invalid").prop("checked",true);
									else $("#invalidDiv #invalid").prop("checked",false);
									_addOverlay(false,data.userPhoto,map,centerPoint.lon,centerPoint.lat,n.point_accury,n.position,oc,ooc,area,data.gisType);//如果是第一个，直接添加
								}
								slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:oc,gisType:data.gisType});//将事件加入map
							}
						});
						if(type == 4||type==9){
							//默认停止轮播，等待用户操作
							$("#userPhotoPanel #userPhotoCarousel").carousel('pause');
							//轮播事件
							//当调用 slide 实例方法时立即触发该事件。
							$('#userPhotoCarousel').unbind().on('slid.bs.carousel', function () {
								var _index = $("#userPhotoPanel #userPhotoCarousel > .carousel-indicators li.active").attr("data-slide-to");
								if($("input[name=isuse]:eq("+_index+")").val()=="0")
									$("#invalidDiv #invalid").prop("checked",true);
								else if($("input[name=isuse]:eq("+_index+")").val()=="1")
									$("#invalidDiv #invalid").prop("checked",false);
								var cdata = slideMap.get(_index);//得到当前选中的页面ID
								_addOverlay(true,data.userPhoto,cdata.map,cdata.lon,cdata.lat,cdata.point_accury,cdata.position,cdata.oc,ooc,area,cdata.gisType);//添加覆盖物
							});
						}
						findAround(data.userPhoto,map,ooc,data.gisType,area,centerPoint.lon,centerPoint.lat);
					}
				});
				//添加覆盖物
				var point;
				var circle;
				var myPositionObj;
				var marker;
				function _addOverlay(flag,photos,map,lon,lat,point_accury,position,oc,ooc,area,gisType){
					map.removeOverlay(point);//清除点
					map.removeOverlay(circle);//清除圆
					map.removeOverlay(myPositionObj);//清除方向指标
					map.removeOverlay(marker);//清除点
					//map.clearOverlays();
					
					//用户坐标
					point = new BMap.Point(lon, lat);
					map.centerAndZoom(point, 18);  // 设置中心点坐标和地图级别
					map.enableScrollWheelZoom();
					
					circle = new BMap.Circle(point,point_accury,{strokeColor:"#358ced", strokeWeight:2, strokeOpacity:0.1,fillColor:"#358ced"}); //创建圆（经度圈）
					map.addOverlay(circle);//增加圆
					
					//创建方向指示
					myPositionObj = new SquareOverlay(point, 100, 100, null, function(div){
						return $(div).append($("<img id='positionImg' src='"+contextPath+"/images/position.png'/>").rotate({angle:position})).get(0);
					});
					marker = new BMap.Marker(point); // 创建点
					if(flag){
						map.addOverlay(myPositionObj);//增加方向指示
						map.addOverlay(marker);//增加点
					}
					//最近的地址
					var geoc = new BMap.Geocoder();
					geoc.getLocation(point, function(rs){
						var addComp = rs.addressComponents;
						$("#consult,#audit_task_name","#auditSuccessPanel")
						.text(addComp.street /* + ", " + addComp.streetNumber */)
						.click(function(){
							$(this).prev("input[id='audit_task_name']").val($(this).text());
						})
						.css({
							"cursor":"pointer"
						}).hover(
						  function () {
						    $(this).addClass("hover");
						  },
						  function () {
						    $(this).removeClass("hover");
						  }
						);
					});
					
					//原始坐标
					if(oc != null && oc.length >= 2){
						if(gisType == 1){//如果是点
							var point2 = new BMap.Point(oc[0], oc[1]);
							var myIcon = new BMap.Icon(contextPath+"/images/target.png", new BMap.Size(20, 30), {    
								// 指定定位位置。   
								// 当标注显示在地图上时，其所指向的地理位置距离图标左上    
								// 角各偏移10像素和25像素。您可以看到在本例中该位置即是   
							  	// 图标中央下端的尖角位置。    
							   anchor: new BMap.Size(20, 15),    
							   // 设置图片偏移。   
							   // 当您需要从一幅较大的图片中截取某部分作为标注图标时，您   
							   // 需要指定大图的偏移位置，此做法与css sprites技术类似。    
							   //imageOffset: new BMap.Size(0, 0 - index * 25)   // 设置图片偏移    
							 });      
							// 创建标注对象并添加到地图   
							var marker2 = new BMap.Marker(point2, {icon: myIcon});
							//marker2.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
							map.addOverlay(marker2);//增加点
						}else if(gisType == 2){//如果是线
							var plArray = new Array();
							for(var i=0;i<oc.length;i=i+2){
								plArray.push(new BMap.Point(oc[i], oc[i+1]));
							}
							var polyline = new BMap.Polyline(plArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});   //创建折线
							map.addOverlay(polyline);//增加折线
						}else if(gisType == 3){//如果是面
							var pgArray = new Array();
							for(var i=0;i<oc.length;i=i+2){
								pgArray.push(new BMap.Point(oc[i], oc[i+1]));
							}
							var polygon = new BMap.Polygon(pgArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});  //创建多边形
							map.addOverlay(polygon);//增加多边形
						}
					}
				}
				function findAround(photos,map,ooc,gisType,area,lon,lat){
					//增加周边任务显示
					map.removeOverlay(markerOther);  
					map.removeOverlay(polylineorgon);
					//中心点
					var centerPoint = new BMap.Point(lon,lat);
					var myIcon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {  
                        offset: new BMap.Size(10, 25), // 指定定位位置  
                        imageOffset: new BMap.Size(0, 0 - 10 * 25) // 设置图片偏移  
                    });  
					var vect = new BMap.Marker(centerPoint, {
						icon:myIcon});
					vect.addEventListener("click",showTaskPhotos);//注册点击事件
					map.addOverlay(vect);//画出中心点
					//点击显示该任务的所有图片的点
					function showTaskPhotos(){
						var arr = new Array();
						for(var i=0;i<photos.length;i++){
							var pointOther1 = new BMap.Point(photos[i].lon, photos[i].lat);
							var myIconOther = new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
							    scale: 1,//图标缩放大小
							    fillColor: "#82FF49",//填充颜色
							    fillOpacity: 0.4 //填充透明度
							  });
							var markerOther1 = new BMap.Marker(pointOther1, {icon: myIconOther});
							map.addOverlay(markerOther1);//增加点
						}
					}
					if(ooc != null){
					 	for (coor in ooc) {
							var obj = ooc[coor];
							//obj.myAuditId;
							if(obj !=null){
								if(gisType == 1){//点
									var pointOther = new BMap.Point(obj[0].coordinateX, obj[0].coordinateY);
									 //设置marker图标为水滴
									var myIconOther = new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
									    scale: 1,//图标缩放大小
									    fillColor: getColor(obj[0].taskClassName),//填充颜色
									    fillOpacity: 1 //填充透明度
									  });
									// 创建标注对象并添加到地图   
									BMap.Marker.prototype.setAuditId = function(_id){
										this.auditId=_id;
									}
									var markerOther = new BMap.Marker(pointOther, {icon: myIconOther});
									markerOther.setAuditId(obj[0].auditId);
									//周边任务增加点击显示明细事件--000
									markerOther.addEventListener("click",function(){
										var _auditId = this.auditId;
										$.post(contextPath+"/audit/taskDetial.html",{
												taskId:_auditId
											},
										function(rdata){
											if(rdata != null && rdata.length > 0){
												rdata = $.parseJSON(rdata);
												//周边任务点的明细
												$("span","#taskDetialPanelAround table").text("");
												var auditDetail = rdata.auditDetail;
												$("#location_name_around").text(auditDetail.location_name);//任务报名
												$("#location_address_around").text(auditDetail.location_address);//任务包地址
												$("#original_task_name_around").text(auditDetail.original_task_name);//原任务包名
												$("#collect_task_name_around").text(auditDetail.collect_task_name+'（'+auditDetail.task_class_name+'）');//采集任务名
												$("#user_name_around").text(auditDetail.user_name);//采集用户名
												$("#statusString_around").text(auditDetail.statusString);//状态
												$("#submit_time_around").text(new Date(auditDetail.create_time).Format("yyyy-MM-dd hh:mm:ss"));//提交时间
												$("#task_amount_around").text(auditDetail.task_amount);//任务金额
												$("#task_freezing_time_around").text(auditDetail.task_freezing_time);//任务冷冻时间
												showDialog($("#taskDetialPanelAround"));
												//周边任务点的用户拍摄图片
												$(".carousel-indicators,.carousel-inner","#userPhotoPanelAround #userPhotoCarouselAround").empty();
												$.each(rdata.userPhoto,function(i,n){
													$("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-indicators")
													.append('<li data-target="#userPhotoCarouselAround" data-slide-to="'+i+'" '+(i==0?'class="active"':'')+'></li>');
													if(type == 7 || type == 9){
														//录像
														if(i==0){
															$('#userMp4PanelAround video#videoMp4Around').attr('src',n.video_url);
															$('#userMp4PanelAround a#videoMp4dowAround').attr('href',n.video_url);
															//缺失原因
															$('#userMp4PanelAround #noExistReasonAround').html(n.no_exist_reason);
														}else{
															$('#userMp4PanelAround .panel-body>div').clone().appendTo('#userMp4PanelAround .panel-body');
															$('#userMp4PanelAround video#videoMp4Around:last').attr('src',n.video_url);
															$('#userMp4PanelAround a#videoMp4dowAround:last').attr('href',n.video_url);
															//缺失原因
															$('#userMp4PanelAround #noExistReasonAround:last').html(n.no_exist_reason);
														}
													}
													if(type == 4 || type == 9){
														//图片
														var _img = $('<div class="item '+(i==0?"active":"")+' text-center">'
																	+'<img class="center-block img-responsive" style="cursor:pointer;" src="'+n.thumbnai_url+'"/>'
																	+'<div class="carousel-caption">拍照时间:'+new Date(n.photograph_time).Format("yyyy-MM-dd hh:mm:ss")
																	+';卫星颗数:'+n.point_level+'</div>'
																	+'</div>');
														//点击图片事件
														_img.click(function(){
															window.open(n.image_url);
														});
														//插入图片
														$("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-inner").append(_img);
													}
												});
												$("#userPhotoPanelAround #userPhotoCarouselAround").carousel('pause');
												showDialog($("#userPhotoPanelAround"));
											}
										});
									});
									map.addOverlay(markerOther);//增加点
								}else{//线面
									var plArray = new Array();
									for(var j=0;j<obj.length;j++){
										plArray.push(new BMap.Point(obj[j].coordinateX, obj[j].coordinateY));
									}
									var polylineorgon;
									if(gisType == 2){
										polylineorgon = new BMap.Polyline(plArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0});//创建折线
									}else if(gisType == 3){
										polylineorgon = new BMap.Polygon(pgArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0});//创建多边形
									}
									map.addOverlay(polylineorgon);//增加折线或多边形
								}
							}
						}
					}
				}
				function getColor(taskClassName){
					if(taskClassName=="过路天桥"){
						return "black";
					}else if(taskClassName=="绿化带灯箱"){
						return "khaki";
					}else if(taskClassName=="灯箱"){
						return "pink";
					}else if(taskClassName=="楼顶广告"){
						return "powderblue";
					}else if(taskClassName=="指路牌灯箱"){
						return "purple";
					}else if(taskClassName=="墙体广告位"){
						return "red";
					}else if(taskClassName=="公共自行车亭"){
						return "skyblue";
					}else if(taskClassName=="公交站亭"){
						return "snow";
					}else if(taskClassName=="侧牌"){
						return "tan";
					}else if(taskClassName=="交通标志牌"){
						return "purple";
					}else if(taskClassName=="两面立柱"){
						return "tomato";
					}else if(taskClassName=="三面立柱"){
						return "violet";
					}else if(taskClassName=="LED"){
						return "yellow";
					}else{
						return "gray";
					}
				}
			}
		}
		
		
		//两个轮播组件、用户 拍摄的录像、审核历史和任务明细
		var mydialog = $(".mydialog")
		.css("position","absolute")
		.addClass("opacity ui-widget-content ui-resizable")
		.addClass("ui-widget-content")
		.find("button.close").click(function(){//关闭按钮事件
			toggleDialog($(this).parent(".panel-heading").parent(".mydialog"));
		})
		.end();
		mydialog.each(function(i,n){
			if(!$(n).hasClass("nodraggable")){
				$(n).draggable({//可拖拽
					stop: function( event, ui ) {//拖放事件
						var self = $(event.target);
						$(self).data("_offset",self.offset());
					}
				});
			}
		});
		
		//显示/隐藏弹窗
		function toggleDialog(el){
			var _offset = $(el).data("_offset")||$("#allmap").offset();
			$(el)
			.fadeToggle(function(){
				if($(this).attr("id") == "userPhotoPanel"){//用户拍摄的照片添加两个额外的翻页扭
					if($(this).is(":visible")){
						$("#slideOption")
						.show();
					} else {
						$("#slideOption")
						.hide();
					}
				}
			})
			.offset({ top: _offset.top, left: _offset.left });
		}
		
		//显示弹窗
		function showDialog(el){
			var _offset = $(el).data("_offset")||$("#allmap").offset();
			$(el)
			.show()
			.offset({ top: _offset.top, left: _offset.left });
		}
		
		//显示样张
		$("#specimenPageButton").click(function(){
			toggleDialog($("#specimenPagePanel"));
		});
		//显示用户拍摄的照片
		$("#userPhotoButton").click(function(e){
			toggleDialog($("#userPhotoPanel"));
		});
		//两个额外的翻页按钮
		$("#prevButton").click(function(){
			$("#userPhotoCarousel").carousel("prev");
		});
		$("#nextButton").click(function(){
			$("#userPhotoCarousel").carousel("next");
		});
		
		//显示用户拍摄的录像
		$("#userMp4Button").click(function(){
			toggleDialog($("#userMp4Panel"));
		});
		
		//认领任务
		$("#claimTaskButton").click(function (){
			$.post(contextPath+"/audit/claimTask.html",
				{
					taskId:taskId
				},
				function(data){
					if(data == ""){
						alert("认领成功");
					}
					else alert(data);
					$("#taskType > li > a[id='frTask'] ").click();//刷新未认领表格
			});
		});
		//退领任务
		$("#unClaimTaskButton").click(function (){
			$.post(contextPath+"/audit/unclaimTask.html",
				{
					taskId:taskId
				},function(data){
					if(data == ""){
						alert("取消认领成功");
					}
					else alert(data);
					$("#taskType > li > a[id='frTask'] ").click();//刷新未认领表格
			});
		});
		
		//申诉任务
		$("#appealTaskButton").click(function (){
			$.post(contextPath+"/audit/appealTask.html",
				{taskId:taskId},
				function(data){
					if(data == ""){
						alert("申诉成功");
					}
					else alert(data);
					$("#taskType > li > a[id='fcTask'] ").click();//刷新已认领表格
			});
		});
		
		//审核历史明细
		$("#auditHistoryButton").click(function(){
			//加载流程图片
			$("#auditHistoryPanel #processTracking").attr("src",contextPath+"/audit/processTracking.png?taskId="+(taskId?taskId:"")+"&processInstanceId="+(processInstanceId?processInstanceId:"")+"&processDefinitionId="+(processDefinitionId?processDefinitionId:"")+"&falg="+new Date());
			//加载审核历史记录
			$.post(contextPath+"/audit/findHistory.html",
				{
					taskId:taskId,
					processInstanceId:processInstanceId
				},function(data){
					var ah_tbody = $("#auditHistoryPanel #auditHistory > tbody");
					ah_tbody.empty();
					if(data != null && data.length > 0){
						var data = $.parseJSON(data);
						$.each(data,function(i,n){
							ah_tbody
							.append("<tr id=\""+n.historicActivityInstance.id+"\" data='"+$.toJSON(n)+"'></tr>").find("tr:last")
							.append("<td>"+(n.historicActivityInstance.assignee==null?'':n.historicActivityInstance.assignee)+"</td>")
							.append("<td>"+n.historicActivityInstance.activityName+"</td>")
							.append("<td>"+new Date(n.historicActivityInstance.startTime).Format("yyyy-MM-dd hh:mm:ss")+"</td>")
							.append("<td>"+(n.historicActivityInstance.endTime==null?'':new Date(n.historicActivityInstance.endTime).Format("yyyy-MM-dd hh:mm:ss"))+"</td>")
							.append("<td><button type=\"button\" class=\"btn btn-default\" onclick=\"window.open(contextPath+'/audit/auditHistoryDatil.html?hid="+n.historicActivityInstance.id+"');\">明细</button></td>");
						});
					} else {
						ah_tbody.append("未查询到数据");
					}
			});
			
			toggleDialog($("#auditHistoryPanel"));
		});
		
		//任务明细
		$("#taskDetailButton").click(function(){
			toggleDialog($("#taskDetialPanel"));
			
		});
		
		//审核通过
		$("#auditSuccessButton").click(function(){
			toggleDialog($("#auditSuccessPanel"));
		});
		
		//审核不通过
		$("#auditFailureButton").click(function(){
			toggleDialog($("#auditFailurePanel"));
		});
		
		//真正的审核通过
		$("#doAuditSuccessButton").click(function(){
			$.post(contextPath+"/audit/doAudit.html",
				{
					taskId:taskId,
					flag:"true",
					appearance:($("#auditSuccessPanel #appearance").val()),//外观
					damaged:($("#auditSuccessPanel #damaged").val()),// 破损
					lighting:($("#auditSuccessPanel #lighting").val()),// 亮灯
					occlusion:($("#auditSuccessPanel #occlusion").val()),// 遮挡
					task_class_name_for_audit:($("#auditSuccessPanel #taskClassNameFromAudit").val()),// 审核认为的任务类型
					audit_task_name:($("#auditSuccessPanel #audit_task_name").val()),// 审核任务名
				},
				function(data){
					alert(data);
					refreshCount();//刷新个数
					$("#taskType > li > a[id='fcTask'] ").click();//刷新表格
					$("#auditSuccessPanel").hide();
					$("#optionButtons > button").hide();
			});
		});
		
		//真正的不审核通过
		$("#doAuditFailureButton").click(function(){
			$.post(contextPath+"/audit/doAudit.html",
				{
					taskId:taskId,
					flag:"false",
					no_approval_reason:($("#auditFailurePanel #noapprovalReason").val()),// 不通过原因
 					comment_message:($("#auditFailurePanel #commentMessage").val())// 给采集者的留言
				},
				function(data){
					alert(data);
					refreshCount();//刷新个数
					$("#taskType > li > a[id='fcTask'] ").click();//刷新表格
					$("#auditFailurePanel").hide();
					$("#optionButtons > button").hide();
			});
		});
		
		//查询按钮
		$("#searchButton").click(function(){
			var buttonOffset = $(this).offset();
			$("#searchCondition")
			.slideToggle()
			.offset({left:buttonOffset.left});
		});
		//日历
		$('.form_datetime').datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			forceParse: 0,
	        showMeridian: 1
	    });
		
		//关闭查询框
		$("body").on("click", function(e){			if(
				$("#searchCondition:visible").size() > 0
				&& $(e.target).attr("id") != "searchButton" 
				&& ($(e.target).parents("button#searchButton") 
					&& $(e.target).parents("button#searchButton").attr("id") != "searchButton")
				&& $(e.target).attr("id") != "searchCondition" 
				&& ($(e.target).parents("div#searchCondition") 
					&& $(e.target).parents("div#searchCondition").attr("id") != "searchCondition")
				){
				$("#searchCondition").slideUp();
			}
		});
		
		$("#doSearchButton").click(function(){
			cpage = 0;
			showTask(taskTypeId, cpage, true);
			$("#searchCondition").css("width","").hide();
		});
		
	});
</script>
<script src="<%=request.getContextPath()%>/js/editPlatform.js"></script>
<script src="<%=request.getContextPath()%>/js/array.js"></script>
</head>

<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-xs-12 col-md-2">
				<div class="row">
					<div class="panel panel-primary">
						<div class="panel-heading">
							<h3 class="panel-title">项目</h3>
						</div>
						<div class="panel-body">
							<div class="btn-group">
								<button type="button" class="btn btn-default dropdown-toggle"
									data-toggle="dropdown" aria-expanded="false">
									<span id="oName">请选择要审核的项目 </span><input id="objectId"
										type="hidden" /><span class="caret"></span>
								</button>
								<ul id="getObjects" class="dropdown-menu" role="menu">
									<li><a href="#">请选择要审核的项目</a></li>
									<li class="divider"></li>
								</ul>
							</div>
							<div class="list-group">
								<a href="#" class="list-group-item"><span id="auditCount"
									class="badge">0</span>普通任务</a> <a href="#" class="list-group-item"><span
									id="lessCount" class="badge">0</span>缺失任务</a> <a href="#"
									class="list-group-item"><span id="appealCount" class="badge">0</span>申诉</a>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-primary">
						<div class="panel-heading">
							<h3 class="panel-title">
								任务列表
								<button class="btn btn-default pull-right"
									style="margin-top:-8px;" type="button" id="searchButton">
									<span class="glyphicon glyphicon-search" aria-hidden="true"></span>
								</button>
								<div id="searchCondition" >
									<form>
										<div class="form-group">
											<div class="input-group date form_datetime"
												data-date=""
												data-date-format="yyyy-mm-dd hh:ii:ss"
												data-link-field="submit_time_start">
												<input class="form-control" size="16" type="text" value="" placeholder="提交时间 从"
													readonly> <span class="input-group-addon"><span
													class="glyphicon glyphicon-remove"></span></span> <span
													class="input-group-addon"><span
													class="glyphicon glyphicon-th"></span></span>
											</div>
											<input type="hidden" id="submit_time_start" name="submit_time_start" value="" />
										</div>
										<div class="form-group">
											<div class="input-group date form_datetime"
												data-date=""
												data-date-format="yyyy-mm-dd hh:ii:ss"
												data-link-field="submit_time_end">
												<input class="form-control" size="16" type="text" value="" placeholder="提交时间 到"
													readonly> <span class="input-group-addon"><span
													class="glyphicon glyphicon-remove"></span></span> <span
													class="input-group-addon"><span
													class="glyphicon glyphicon-th"></span></span>
											</div>
											<input type="hidden" id="submit_time_end" name="submit_time_end" value="" />
										</div>
										<div class="form-group">
											<input
												type="text" class="form-control" id="location_name" name="location_name"
												placeholder="任务包名">
										</div>
										<div class="form-group">
											<input
												type="text" class="form-control"
												id="location_address" name="location_address" placeholder="任务包地址">
										</div>
										<div class="form-group">
											<input
												type="text" class="form-control"
												id="original_task_name" name="original_task_name" placeholder="原始任务名">
										</div>
										<div class="form-group">
											<input
												type="text" class="form-control"
												id="collect_task_name" name="collect_task_name" placeholder="采集任务名">
										</div>
										<div class="form-group">
											<input
												type="text" class="form-control"
												id="user_name" name="user_name" placeholder="采集用户">
										</div>
										<div class="text-center">
											<button class="btn btn-info" type="button"
												id="doSearchButton">
												<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 查询
											</button>
										</div>
									</form>
								</div>
							</h3>
						</div>
						<div class="panel-body">
							<div class="btn-group">
							  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
							     <span id="taskTypeName">请选择任务类型</span>  <span class="caret"></span>
							  </button>
							  <ul class="dropdown-menu" role="menu" id="taskType">
							    <li><a href="#">请选择任务类型</a></li>
								<li class="divider"></li>
							    <li><a href="#" id="frTask">未认领任务</a></li>
							    <li><a href="#" id="fcTask">已认领任务</a></li>
							    <li><a href="#" id="fmiTask">经办任务</a></li>
							  </ul>
							</div>
							<div class="list-group" id="taskList" style="overflow:auto;">
							</div>
							<ul class="dropdown-menu" id="taskListContextMenu">
							  <li role="presentation" id="claimAll"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span> 全部认领</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div class="col-xs-12 col-md-10">
				<div class="panel panel-default">
				  <div class="panel-body">
				    <div id="allmap" style="width: 100%;height:300px;overflow: hidden;margin:0;font-family:'微软雅黑';"></div>
				  </div>
				   <div  id="typeDiv">
				  	<button class="btn btn-info" type="button" id="addTypeButton" >
						添加类型
					</button>
					<button class="btn btn-info" type="button" id="editTypeButton">
						编辑类型
					</button>
					<button class="btn btn-info" type="button" id="delTypeButton">
						删除类型
					</button>
					<!-- 添加类型 -->
					<div id="addTypeDiv" >
						<form>			
							<!-- <div class="form-group">
								<input
									type="text" class="form-control" id="layer_id" name="layer_id"
									placeholder="类型ID">
							</div> -->
							<div class="form-group">
								<input
									type="text" class="form-control"
									id="layer_name" name="layer_name" placeholder="类型名称"  >
									<input id="layer_id"  type="hidden" />
							</div>
							<div class="text-center">
								<button class="btn btn-info" type="button"
									id="doSaveButton">
									<span class="glyphicon glyphicon-save" aria-hidden="true"></span>保存
								</button>
							</div>
						</form>
					</div>
				  </div>
				  
				  <div class="panel-footer" id="optionButtons">
				  	<button class="btn btn-default" id="specimenPageButton" style="display: none;"><span class="glyphicon glyphicon-file" aria-hidden="true"></span> 样张</button>
				  	<button class="btn btn-default" id="userMp4Button" style="display: none;"><span class="glyphicon glyphicon-film" aria-hidden="true"></span> 用户拍摄的录像</button>
				  	<button class="btn btn-default" id="claimTaskButton" style="display: none;"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span> 认领</button>
				  	<button class="btn btn-default" id="unClaimTaskButton" style="display: none;"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 取消认领</button>
				  	<button class="btn btn-default" id="appealTaskButton" style="display: none;"><span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> 申诉</button>
				  	<button class="btn btn-default" id="taskDetailButton" style="display: none;"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> 任务明细</button>
				  	<button class="btn btn-default" id="auditHistoryButton" style="display: none;"><span class="glyphicon glyphicon-transfer" aria-hidden="true"></span> 审核历史明细</button>
				  	<button class="btn btn-default" id="auditSuccessButton" style="display: none;"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span> 审核通过</button>
				  	<button class="btn btn-default" id="auditFailureButton" style="display: none;"><span class="glyphicon glyphicon-thumbs-down" aria-hidden="true"></span> 审核不通过</button>
				  	<button class="btn btn-default" id="userPhotoButton" style="display: none;"><span class="glyphicon glyphicon-camera" aria-hidden="true"></span> 用户拍摄的照片</button>
				  	<div class="btn-group opacity" style="display: none;" id="slideOption">
					  	<button type="button" class="btn btn-default" id="prevButton"><span class="glyphicon glyphicon-step-backward" aria-hidden="true"></span></button>
					  	<button type="button" class="btn btn-default" id="nextButton"><span class="glyphicon glyphicon-step-forward" aria-hidden="true"></span></button>
				  	</div>
				  </div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 样张轮播 -->
	<div id="specimenPagePanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">样张
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<div class="carousel slide" id="specimenPageCarousel"
				data-ride="carousel">
				<!-- 轮播（Carousel）指标 -->
				<ol class="carousel-indicators"></ol>
				<!-- 轮播（Carousel）项目 -->
				<div class="carousel-inner" role="listbox">
					<div class="item active">
						<img src="" alt="First slide">
					</div>
				</div>
				<!-- 轮播（Carousel）导航 -->
				<a class="left carousel-control" href="#specimenPageCarousel"
					role="button" data-slide="prev"> <span
					class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
					<span class="sr-only">Previous</span>
				</a> <a class="right carousel-control" href="#specimenPageCarousel"
					role="button" data-slide="next"> <span
					class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
					<span class="sr-only">Next</span>
				</a>
			</div>
		</div>
	</div>

	<!-- 用户拍摄的照片轮播 -->
	<div id="userPhotoPanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">用户拍摄的照片
			<button type="button" class="close">
				<span>&times;</span>
			</button>
			<span id="invalidDiv" style="display: none;float: right;padding-right: 10px;">&nbsp;&nbsp;<input type="checkbox" id="invalid"/>无效拍摄</span>
		</div>
		<div class="panel-body">
			<div class="carousel slide" id="userPhotoCarousel"
				data-ride="carousel">
				<!-- 轮播（Carousel）指标 -->
				<ol class="carousel-indicators"></ol>
				<!-- 轮播（Carousel）项目 -->
				<div class="carousel-inner" role="listbox">
					<div class="item active">
						<img src="" alt="First slide">
					</div>
				</div>
				<!-- 轮播（Carousel）导航 -->
				<a class="left carousel-control" href="#userPhotoCarousel"
					role="button" data-slide="prev"> <span
					class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
					<span class="sr-only">Previous</span>
				</a> <a class="right carousel-control" href="#userPhotoCarousel"
					role="button" data-slide="next"> <span
					class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
					<span class="sr-only">Next</span>
				</a>
			</div>
		</div>
	</div>
	
	<!-- 用户拍摄的mp4 -->
	<div id="userMp4Panel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">用户拍摄的录像
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body text-center">
			<div>
				<div class="embed-responsive embed-responsive-4by3">
					<video id="videoMp4" class="embed-responsive-item"
						controls="controls"> Your browser does not support
						the video tag.
					</video>
				</div>
				<p>
					如果无法播放视频，请点击<a id="videoMp4dow">这里</a>下载。
				</p>
				<p>缺失原因：<span id="noExistReason"></span></p>
			</div>
		</div>
	</div>

	<!-- 用户拍摄的照片轮播(周边任务) -->
	<div id="userPhotoPanelAround" class="panel panel-warning mydialog" style="display:none;width:400px;">
		<div class="panel-heading">用户拍摄的照片【周边任务】
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<div class="carousel slide" id="userPhotoCarouselAround"
				data-ride="carousel">
				<!-- 轮播（Carousel）指标 -->
				<ol class="carousel-indicators"></ol>
				<!-- 轮播（Carousel）项目 -->
				<div class="carousel-inner" role="listbox">
					<div class="item active">
						<img src="" alt="First slide">
					</div>
				</div>
				<!-- 轮播（Carousel）导航 -->
				<a class="left carousel-control" href="#userPhotoCarouselAround"
					role="button" data-slide="prev"> <span
					class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
					<span class="sr-only">Previous</span>
				</a> <a class="right carousel-control" href="#userPhotoCarouselAround"
					role="button" data-slide="next"> <span
					class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
					<span class="sr-only">Next</span>
				</a>
			</div>
		</div>
	</div>
	
	<!-- 用户拍摄的mp4(周边任务) -->
	<div id="userMp4PanelAround" class="panel panel-warning mydialog" style="display:none;width:400px;">
		<div class="panel-heading">用户拍摄的录像【周边任务】
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body text-center">
			<div>
				<div class="embed-responsive embed-responsive-4by3">
					<video id="videoMp4Around" class="embed-responsive-item"
						controls="controls"> Your browser does not support
						the video tag.
					</video>
				</div>
				<p>
					如果无法播放视频，请点击<a id="videoMp4dowAround">这里</a>下载。
				</p>
				<p>缺失原因：<span id="noExistReasonAround"></span></p>
			</div>
		</div>
	</div>

	<!-- 审核历史 -->
	<div id="auditHistoryPanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">审核历史
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<img id="processTracking" class="img-responsive" />
		</div>
		<table id="auditHistory" class="table table-hover">
			<thead>
				<tr>
					<th>办理用户</th>
					<th>活动名称</th>
					<th>开始时间</th>
					<th>结束时间</th>
					<th>#</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>

	<!-- 任务明细 -->
	<div id="taskDetialPanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">任务明细 
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<table class="table table-hover">
			<tr>
				<td style="width: 200px;"><strong>任务包名：</strong></td>
				<td><span id="location_name"></span></td>
			</tr>
			<tr>
				<td><strong>任务包地址：</strong></td>
				<td><span id="location_address"></span></td>
			</tr>
			<tr>
				<td><strong>原始任务名：</strong></td>
				<td><span id="original_task_name"></span></td>
			</tr>
			<tr>
				<td><strong>采集任务名：</strong></td>
				<td><span id="collect_task_name"></span></td>
			</tr>
			<tr>
				<td><strong>采集用户：</strong></td>
				<td><span id="user_name"></span></td>
			</tr>
			<tr>
				<td><strong>状态：</strong></td>
				<td><span id="statusString"></span></td>
			</tr>
			<tr>
				<td><strong>提交时间：</strong></td>
				<td><span id="submit_time"></span></td>
			</tr>
			<shiro:lacksRole name="audit_casual_inspection">  
			<tr>
				<td><strong>任务金额（￥）：</strong></td>
				<td><span id="task_amount"></span></td>
			</tr></shiro:lacksRole>
			<tr>
				<td><strong>任务冻结时间（小时）：</strong></td>
				<td><span id="task_freezing_time"></span></td>
			</tr>
		</table>
	</div>
	
	<!-- 任务明细(周边任务) -->
	<div id="taskDetialPanelAround" class="panel panel-warning mydialog" style="display:none;width:450px;">
		<div class="panel-heading">任务明细【周边任务】
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<table class="table table-hover">
			<tr>
				<td style="width: 200px;"><strong>任务包名：</strong></td>
				<td><span id="location_name_around"></span></td>
			</tr>
			<tr>
				<td><strong>任务包地址：</strong></td>
				<td><span id="location_address_around"></span></td>
			</tr>
			<tr>
				<td><strong>原始任务名：</strong></td>
				<td><span id="original_task_name_around"></span></td>
			</tr>
			<tr><!-- 000 -->
				<td><strong>采集任务名：</strong></td>
				<td><span id="collect_task_name_around"></span></td>
			</tr>
			<tr>
				<td><strong>采集用户：</strong></td>
				<td><span id="user_name_around"></span></td>
			</tr>
			<tr>
				<td><strong>状态：</strong></td>
				<td><span id="statusString_around"></span></td>
			</tr>
			<tr>
				<td><strong>提交时间：</strong></td>
				<td><span id="submit_time_around"></span></td>
			</tr>
			<tr>
				<td><strong>任务金额（￥）：</strong></td>
				<td><span id="task_amount_around"></span></td>
			</tr>
			<tr>
				<td><strong>任务冻结时间（小时）：</strong></td>
				<td><span id="task_freezing_time_around"></span></td>
			</tr>
		</table>
	</div>
	
	<!-- 审核通过 -->
	<div id="auditSuccessPanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">审核通过
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<div id="approval">
				<div>
					外观：<select id="appearance" class="form-control">
						<option value="完好">完好</option>
						<option value="少量贴纸">少量贴纸</option>
						<option value="大量贴纸">大量贴纸</option>
					</select>
				</div>
				<div>
					破损：<select id="damaged" class="form-control">
						<option value="完好">完好</option>
						<option value="轻微破损，不影响画面">轻微破损，不影响画面</option>
						<option value="部分磨损，影响画面">部分磨损，影响画面</option>
					</select>
				</div>
				<div>
					亮灯：<select id="lighting" class="form-control">
						<option value="完好">完好</option>
						<option value="少数未亮">少数未亮</option>
						<option value="多数未亮">多数未亮</option>
						<option value="不亮">不亮</option>
					</select>
				</div>
				<div>
					遮挡：<select id="occlusion" class="form-control">
						<option value="无任何遮挡">无任何遮挡</option>
						<option value="轻微遮挡，不影响画面">轻微遮挡，不影响画面</option>
						<option value="1/4遮挡">1/4遮挡</option>
					</select>
				</div>
				<!-- 新增类型选项 -->
				<div>
					确认类型：<input class="form-control" id="taskClassNameFromAudit" value="" placeholder="确认类型"  maxlength ="100">
				</div>
				<div>
					道路名称：<input class="form-control" id="audit_task_name" placeholder="道路名称"/>参考：<span id="consult"></span>
				</div>
				<button type="button" class="btn btn-primary"
					style="margin-top: 5px;" id="doAuditSuccessButton"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span> 通过</button>
			</div>
		</div>
	</div>
	
	<!-- 审核不通过 -->
	<div id="auditFailurePanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">审核不通过
			
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<!-- 审核不通过时出现 -->
			<div id="noapproval">
				<div>
					请选择不通过原因： <select id="noapprovalReason" class="form-control">
						<option value="照片模糊不清晰">照片模糊不清晰</option>
						<option value="拍摄主体错误">拍摄主体错误</option>
						<option value="照片拍摄距离过远">照片拍摄距离过远</option>
						<option value="照片拍摄角度不合格">照片拍摄角度不合格</option>
						<option value="拍摄位置不对">拍摄位置不对</option>
					</select>
				</div>
				<div>
					给采集者留言：
					<textarea class="form-control" rows="3" id="commentMessage"></textarea>
				</div>
				<button type="button" class="btn btn-primary"
					style="margin-top: 5px;" id="doAuditFailureButton"><span class="glyphicon glyphicon-thumbs-down" aria-hidden="true"></span> 不通过</button>
			</div>
		</div>
	</div>
	<div id="ztree" class="panel panel-default mydialog nodraggable" style="display:none;width:20%;overflow: auto;height: 70%;">
		<div class="panel-heading">任务明细
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div id="ztreeContent"></div>
	</div>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="propModal" tabindex="-1" role="dialog" 
	   aria-labelledby="myModalLabel" aria-hidden="true">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" 
	               aria-hidden="true">×
	            </button>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <div class="btn-group" >
		            <button type="button" class="btn btn-default dropdown-toggle"
						data-toggle="dropdown" aria-expanded="false">
						<span id="layerName">请选择类型</span><input id="layerId"
							type="hidden" /><span class="caret"></span>
					</button>
		             <ul id="getlayerIds" class="dropdown-menu" role="menu">
							<li><a href="#">请选择类型</a></li>
							<li class="divider"></li>
					</ul>
				</div>
	            <h4 class="modal-title" id="myModalLabel"  style="float:left;">
	               添加属性信息
	            </h4>
	         </div>
	         <div class="modal-body">
	            <form id="advForm">
					<div style="height:300px;overflow-y:auto">
	                    <table id="time_table" class="table table-bordered">
	                        <tbody>
	                            <tr>
	                                <th style="text-align: center;">属性名称</th>
	                                <th style="text-align: center;">属性值</th>
	                                <th style="text-align: center;"><i class="icon-download-alt" onclick="addNewRow('33','33')">add</i></th>
	                            </tr>
	                            <!-- <tr>
	                                <td style="text-align: center;">
	                                    <input type="text" class="form-control" id="1" name="prop_name" placeholder="属性名称">
	                                </td>
	                                <td style="text-align: center;">
	                                	<input type="text" class="form-control" id="1" name="prop_value" placeholder="属性值">
	                                </td>
	                                <td style="text-align: center;"><i class="icon-trash'" id="1" onclick="del('1')">delete</i></td>
	                            </tr> -->
	                        </tbody>
	                    </table>
	                    </div>
	                </form>
	         </div>
	         <div class="modal-footer " style="text-align:center;" >
	         	<button type="button" class="btn btn-primary" onclick="saveProp();">
	               保存
	            </button>
	            <button type="button" class="btn btn-default" 
	               data-dismiss="modal">关闭
	            </button>
	         </div>
	      </div><!-- /.modal-content -->
	   </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
</body>
</html>
