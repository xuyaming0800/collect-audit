<%@page import="com.autonavi.audit.constant.TASK_STATUS"%>
<%@ page language="java" import="java.util.*,com.autonavi.audit.service.Config" pageEncoding="utf-8"%>
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
/* 关闭按钮的样式  */
.close_style_two{
	float: right;
    font-size: 21px;
    font-weight: 700;
    line-height: 1;
    color: #000;
    text-shadow: 0 1px 0 #fff;
    opacity: .2;
	-webkit-appearance: none;
    padding: 0;
    cursor: pointer;
    background: 0 0;
    border: 0;
}
/**编辑平台样式**/
#addLayerDiv {
	display: none;
	position: absolute;
	border: 1px solid #DDDDDD;
	background-color: #fff;
	z-index: 1009;
	padding: 5px;
	border-radius: 5px;
	width:300px;
}
.active{
	background-color:#337ab7;
	border-color:#337ab7;
}

.ztree li span.button.add {margin-left:2px; margin-right: -1px; background-position:-144px 0; vertical-align:top; *vertical-align:middle}
</style>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.json2tree.css">
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=GGBqMj92BHolANNEDpe6TlIc"></script>
<script src="<%=request.getContextPath()%>/js/squareOverlay.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.rotate.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.json2tree.js" type="application/javascript"></script>
<!--加载鼠标绘制工具-->
<script type="text/javascript" src="<%=request.getContextPath()%>/js/DrawingManager_min.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/DrawingManager_min.css" />
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
	attrs,//mongodb 任务明细包含图片样张信息
	attrList,//mongodb 任务明细状态等
	baseId,//mongodb 任务的业务id
	keysMap;//存放从数据库中获取的小区字典集合
	$(function(){
		//字典-查询审核通过的字典信息
		keysMap = new Map();
		$.ajax({
			url:"<%=Config.get_dictionary_url%>&pId=auditok&username=1",
			dataType:'jsonp',
			jsonp:'jsoncallback',
			success: function(data){
				var _ap = $("#approval");
				if(data!=null && data!=""){
					if(data.success){
						for(var i=0;i<data.info.length;i++){
							keysMap.put(data.info[i].dictTypeName, data.info[i].remark);
							var ns = data.info[i];
							var _div = $("<div class='newSel' id='div_"+ns.dictTypeName+"'>"+ns.remark+"<div/>");
							$(_div).prependTo(_ap);
							$.ajax({
								url:"<%=Config.get_dictionary_type_url%>&key="+ns.dictTypeName+"&username=1",
								dataType:'jsonp',
								jsonp:'jsoncallback',
								success: function(data1){
									if(data1!=null && data1!=""){
										if(data1.success){
											var dataDictType = data1.info;
											var _sel = $("<select/>").attr({
													"id":dataDictType.dictTypeName,
													"class":"form-control"
												});
											$("#div_"+dataDictType.dictTypeName,_ap).append(_sel);
											$.ajax({
												url:"<%=Config.get_dictionary_data_url%>&dicttypeid="+dataDictType.id+"&username=1",
												dataType:'jsonp',
												jsonp:'jsoncallback',
												success: function(data2){
													if(data2.success){
														var dataDictData = data2.info;
														for(var j=0;j<data2.info.length;j++){
															var _op = $("<option/>").attr({value:data2.info[j].dictItemName}).html(data2.info[j].dictItemValue);
															_op.appendTo(_sel);
														}
													}
												}
											});
										}
									}
								}
							});
						}
					};
				}
			}
		});
		//查询审核失败字典存储信息
		$.ajax({
			url:"<%=Config.get_dictionary_url%>&pId=auditfall&username=1",
			dataType:'jsonp',
			jsonp:'jsoncallback',
			success: function(data){
				var _noap = $("#noapproval");
				if(data!=null && data!=""){
					if(data.success){
						var dictData = data.info[0];
						var _div1 = $("<div class='newSel'>"+dictData.remark+"<div/></div>");
						keysMap.put(dictData.dictTypeName, dictData.remark);
						$(_div1).prependTo(_noap);
						$.ajax({
							url:"<%=Config.get_dictionary_type_url%>&key="+dictData.dictTypeName+"&username=1",
							dataType:'jsonp',
							jsonp:'jsoncallback',
							success: function(data1){
								if(data1!=null && data1!=""){
									if(data1.success){
										var dataDictType = data1.info;
										var _sel1 = $("<select/>").attr({
												"id":"noapprovalReason",
												"class":"form-control"
											});
										$("div.newSel > div:empty:first",_noap).append(_sel1);
										$.ajax({
											url:"<%=Config.get_dictionary_data_url%>&dicttypeid="+dataDictType.id+"&username=1",
											dataType:'jsonp',
											jsonp:'jsoncallback',
											success: function(data2){
												if(data2.success){
													var dataDictData = data2.info;
													for(var j=0;j<data2.info.length;j++){
														var _op = $("<option/>").attr({value:data2.info[j].dictItemName}).html(data2.info[j].dictItemValue);
														_op.appendTo(_sel1);
													}
												}
											}
										});
									}
								}
							}
						});
					}
				}
			}
		});
		//小区字典信息
		$.ajax({
			url:"<%=Config.get_dictionary_url%>&pId=cell&username=1",
			dataType:'jsonp',
			jsonp:'jsoncallback',
			success: function(data){
				if(data!=null && data!=""){
					if(data.success){
						for(var i=0;i<data.info.length;i++){
							var n = data.info[i];
							$.ajax({
								url:"<%=Config.get_dictionary_type_url%>&key="+n.dictTypeName+"&username=1",
								dataType:'jsonp',
								jsonp:'jsoncallback',
								success: function(data1){
									if(data1!=null && data1!=""){
										if(data1.success){
											var dataDictType = data1.info;
											$.ajax({
												url:"<%=Config.get_dictionary_data_url%>&dicttypeid="+dataDictType.id+"&username=1",
												dataType:'jsonp',
												jsonp:'jsoncallback',
												success: function(data2){
													if(data2.success){
														var dataDictData = data2.info;
														for(var j=0;j<data2.info.length;j++){
															keysMap.put(dataDictData[j].dictItemName, dataDictData[j].dictItemValue);
														}
													}
												}
											});
										}
									}
								}
							});
						}
					};
				}
			}
		});
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
		map.addControl(new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT,offset: new BMap.Size(170, 20)}));   //添加地图类型控件
		map.addControl(new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT}));// 左上角，添加比例尺
		map.addControl(new BMap.NavigationControl());  //左上角，添加默认缩放平移控件
		map.disableDoubleClickZoom();//2015.8.7lxs增加双击放大事件
		//$("#allmap").pin();//将地图钉在固定位置
		
			
		//初始化要审核的项目
		$.post(contextPath+"/audit/getObjects.html",null,function(data){
			if(data!=null && data!=""){
				data = $.parseJSON(data);
				$.each(data,function(i,n){
					if(n.id==3)
					$('<li><a href="#" objectId="'+n.id+'">'+n.system_name+'</a></li>').appendTo("#getObjects");
				});
				//绑定事件
				$("#getObjects li > a").click(function(){
					var self = $(this);
					$("#oName").text(self.text());
					bsType = self.attr("objectId");//项目ID
					refreshCount();//刷新个数
					curr_taskId = "";//重置当前任务ID为空
					curr_proType = "";//重置当前任务类型
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
					curr_taskId = "";//重置当前任务ID为空
					curr_proType = "";//重置当前任务类型
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
				curr_taskId = "";//重置当前任务ID为空
				curr_proType = "";//重置当前任务类型
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
				var _p = $("#searchCondition > form").serializeArray();
				var p = {
					page:cpage,
					bsType: bsType,
					type:type
				};
				$.each(_p,function(i,n){
					if(n.value)
				   		eval("p."+n.name+"='"+n.value+"';");
				});
				$.post(url,p,function(data){
					var taskList = $("#taskList");
					if(isEmpty) taskList.empty();
					if(data != null && data.length > 0){
						data = $.parseJSON(data);
						$.each(data,function(i,n){
							$('<a href="#" class="list-group-item"/>')
							.data("taskId",n.taskId)
							.append('<h5 class="list-group-item-heading">'+n.collect_task_name+'（'+n.task_class_name+'）<font color="red" style="font-weight:bold;">'+(n.bpm_task_name==null?"":n.bpm_task_name)+'</font></h5>')
							.append('<p class="list-group-item-text">用户：'+n.user_name+' 采集时间：'+new Date(n.submit_time).Format("yyyy-MM-dd hh:mm:ss")+'</p>')
							.click(function(e){
								$(this).siblings("a.list-group-item").removeClass("active").end().addClass("active");//选中
								taskId = n.taskId;//工作流任务ID放在公共区域
								processInstanceId = n.process_instance_id;//工作流流程实例ID
								processDefinitionId = n.process_definition_id;//工作流流程定义ID
								baseId = n.id;//任务id
								//*************************
								curr_taskId = baseId;//标记当前任务ID
								curr_proType = n.task_class_name;
								if(curr_proType == "" || curr_proType == null || curr_proType == undefined) {
									setTimeout('alert("此任务无类型无法加载相关图层信息！")', 3000 );
								}
								//initData();//初始化覆盖物信息
								//显示图层类型窗口
								showLayerTypeDialog("#layerType");
								//**** 1,左下角列表-点击事件:显示树形结构
								//***************************************************************************************************************
								if(!$("#ztreeContent").is(":empty")){
									$("#ztreeContent").json2tree("destroy");
								}
								ctree();
								function ctree(){
									$.post(contextPath+"/audit/getDetail.html",{
										baseId:baseId
									},function(data){
										if(data!=""){
											data = $.parseJSON(data);
											attrs = data.attrs;
											attrList = data.attrList;
											baseId = data.baseId;
											for ( var p in attrs ){ 
												for(var m in attrs[p]){
													if(m=="collectClassId"||m=="batchId")
														delete attrs[p][m];
													var vals = keysMap.get(m);
													if(vals){
														attrs[p][vals]=attrs[p][m];
														delete attrs[p][m];
													}
													if(m=="reasons"){
														var reasonList = attrs[p][m];
														for(var t=0;t<reasonList.length;t++){
															for(var nn in reasonList[t]){
																var valreason = keysMap.get(nn);
																if(valreason){
																	attrs[p][m][t][valreason]=attrs[p][m][t][nn];
																	delete attrs[p][m][t][nn];
																}
															}
														}
													}
												}
											}
											//删除之前的点
											var myPosition_envir,circle_envir,marker_envir;
											//放入列表
											$("#ztreeContent").json2tree({
												json:data.attrs,
												after:function(level,key){
													var __status;
													for(var j=0;j<attrList.length;j++){
														if(attrList[j].name==key){
															__status = attrList[j].status;
															break;
														}
													}
													if(level == 1){
														var img = $('<span aria-hidden="true" style="font-size: 20px; margin-left:10px;"></span>');
														if(__status==1){
															img.addClass("glyphicon glyphicon-ok-sign").css("color","#5CB85C");
														} else if(__status==0){
															img.addClass("glyphicon glyphicon-remove-sign").css("color","#D9534F");
														} else{
															img.addClass("glyphicon glyphicon-question-sign").css("color","#F0AD4E");
														}
														return img;
													}
													return '';
												},
												click: function(e){
													var t= $(e.target);//
													if(t.parents("li.imgs").size()>0 && !t.parent("li").hasClass("imgs")){
														//删除之前的点
														map.removeOverlay(marker_envir);
														map.removeOverlay(circle_envir);
														map.removeOverlay(myPosition_envir);
														//显示图片出来
														//economicEnvironment_showPic(attrs);
														var typeEnvir = $(t).parent().parent().parent().parent().parent().children(".json2tree_li_div").text();
														if(typeEnvir=="") typeEnvir = $(t).parent().parent().parent().parent().parent().parent().children(".json2tree_li_div").text();
														var imgs = attrs[typeEnvir].imgs;
														var imgsIndex = t.parent().prevAll("li").length;//角标
														
														//图片坐标定位
														point = new BMap.Point(imgs[imgsIndex].lon, imgs[imgsIndex].lat);
														map.centerAndZoom(point, 18);  // 设置中心点坐标和地图级别
														map.enableScrollWheelZoom();
														circle_envir = new BMap.Circle(point,imgs[imgsIndex].point_accury,{strokeColor:"#358ced", strokeWeight:2, strokeOpacity:0.1,fillColor:"#358ced", fillOpacity: 0.01}); //创建圆（经度圈）
														map.addOverlay(circle_envir);//增加圆
														//创建方向指示
														myPosition_envir = new SquareOverlay(point, 100, 100, null, function(div){
															return $(div).append($("<img id='positionImg' src='"+contextPath+"/images/position.png'/>").rotate({angle:parseInt(imgs[imgsIndex].position)})).get(0);
														});
														marker_envir = new BMap.Marker(point); // 创建点
														map.addOverlay(myPosition_envir);//增加方向指示
														map.addOverlay(marker_envir);//增加点
														
														//显示图片
														if(imgs[imgsIndex].image_url!=null&&imgs[imgsIndex].image_url!=""){
															$(".inner","#userPhotoOnePanel").find("*").remove();
															var _img = $('<div class="item text-center"><input name="isuse" type="hidden"/><input name="imgId" type="hidden"/><input name="index" type="hidden"/>'
																	+'<img class="center-block img-responsive" style="cursor:pointer;" src="'+imgs[imgsIndex].thumbnai_url+'"/>'
																	+'<div class="carousel-caption">拍照时间:'+new Date(imgs[imgsIndex].photograph_time*1000).Format("yyyy-MM-dd hh:mm:ss")
																	+';卫星颗数:'+imgs[imgsIndex].point_level
																	+';拍摄方式:'+(imgs[imgsIndex].index%2 == 1?"远景":"近景")+'</div>'
																	+'</div>');
															//点击图片事件
															_img.click(function(){
																window.open(imgs[imgsIndex].image_url);
															});
															$("input[name=isuse]",_img).val(imgs[imgsIndex].used);
															$("input[name=imgId]",_img).val(imgs[imgsIndex].id);
															$("input[name=index]",_img).val(imgs[imgsIndex].index);
															//插入图片
															$("#userPhotoOnePanel > .inner").append(_img);
															showDialog($("#userPhotoOnePanel"));
														}else{//显示摄影
															$('#userMp4Panel video#videoMp4').attr('src',imgs[imgsIndex].video_url);
															$('#userMp4Panel a#videoMp4dow').attr('href',imgs[imgsIndex].video_url);
															//缺失原因
															$('#userMp4Panel #noExistReason').html(imgs[imgsIndex].no_exist_reason);
															showDialog("#userMp4Panel");
														}
													}else if(!t.parent("li").parent("ul").parent().is("li")){//更新状态的点击事件
														if (taskTypeId == "fcTask"){//已认领任务
															if(n.bpm_task_name=="冻结期"){
																alert("冻结期无法修改状态");return;
															}
															for(var i=0;i<attrList.length;i++){
																if(attrList[i].name==$(t).text()){
																	var status = attrList[i].status;
																	var name = attrList[i].name;
																	var money = attrList[i].money;
																	if(status==1){
																		//显示审核不通过弹窗
																		$("#auditSuccessPanel").hide();
																		showDialog("#auditFailurePanel");
																		$("#auditFailurePanel").show().offset({ top: $("#allmap").offset().top, left: $("#allmap").offset().left+$("#ztree").width() });
																		//绑定事件
																		$("#doAuditFailureButton").unbind().bind("click",function(){
																			$.post(contextPath+"/audit/updateStatus.html",
																				{
																					taskClassNameForAudit:"",
																					no_approval_reason:($("#auditFailurePanel #noapprovalReason").find("option:selected").text()),// 不通过原因
																 					comment_message:($("#auditFailurePanel #commentMessage").val()),// 给采集者的留言
																 					baseId:baseId,		//任务id
																					type:name,	//任务类型名称
																					status:0,
																					money:money
																				},
																				function(data){
																					data = $.parseJSON(data);
																					if(data.flag){
																						//刷新树列表
																						$("#ztreeContent").json2tree("destroy");
																						ctree();
																					}else{
																						alert("修改状态失败,请联系管理员");
																					}
																					$("#auditFailurePanel").hide();
																			});
																		});
																	}else{
																		//显示审核通过弹窗
																		$("#auditFailurePanel").hide();
																		showDialog("#auditSuccessPanel");
																		$("#auditSuccessPanel").show().offset({ top: $("#allmap").offset().top, left: $("#allmap").offset().left+$("#ztree").width() });
																		$("#doAuditSuccessButton").unbind().bind("click",function(){
																			$.post(contextPath+"/audit/updateStatus.html",
																				{
																					appearance:($("#auditSuccessPanel #appearance").find("option:selected").text()),//外观
																					damaged:($("#auditSuccessPanel #damaged").find("option:selected").text()),// 破损
																					lighting:($("#auditSuccessPanel #lighting").find("option:selected").text()),// 亮灯
																					occlusion:($("#auditSuccessPanel #dodging").find("option:selected").text()),// 遮挡
																					taskClassNameForAudit:($("#auditSuccessPanel #taskClassNameFromAudit").val()),// 审核认为的任务类型
																					audit_task_name:($("#auditSuccessPanel #audit_task_name").val()),// 审核任务名
																					baseId:baseId,		//任务id
																					type:name,	//任务类型名称
																					status:1,
																					money:money
																				},
																				function(data){
																					data = $.parseJSON(data);
																					if(data.flag){
																						//刷新树列表
																						$("#ztreeContent").json2tree("destroy");
																						ctree();
																					}else{
																						alert("修改状态失败,请联系管理员");
																					}
																					$("#auditSuccessPanel").hide();
																			});
																		});
																	}
																}
															}
														}else alert("请先认领任务");
													}
												}
											});
											$("json2tree_li_key")
											$("li.imgs > ul > li > ul","#ztreeContent").remove();
											$("#ztree").show().offset({ top: $("#allmap").offset().top, left: $("#allmap").offset().left+$("#allmap").width()-$("#ztree").width() });
										}
									});
								}
								//***************************************************************************************************************
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
								//清空确认类型
								$("#taskClassNameFromAudit").val("");
								//根据类型显示按钮
								$("#optionButtons > button").hide();
								$("#auditHistoryButton").show();//审核历史明细
								//$("#addTypeButton").show();//添加类型
								//$("#delTypeButton").show();//删除类型
								//$("#editTypeButton").show();//编辑
								
								if (taskTypeId == "frTask") {//未认领任务
									$("#claimTaskButton").show();//认领
									if(n.bpm_task_name=="冻结期") $("#claimTaskButton").hide();
								} else if (taskTypeId == "fcTask"){//已认领任务
									$("#unClaimTaskButton").show();//取消认领
									$("#auditSuccessButton").show();//审核通过
									if(n.status==6||n.status==7){//审核状态为6或7则隐藏按钮
										$("#appealTaskButton").hide();//申诉
										$("#auditSuccessButton").hide();//审核通过
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
					   initData();//初始化覆盖物数据
						if(centerPoint.lon==null || centerPoint.lat==null){
							alert("没有近景图片或者远近景标识为空，地图定位失败");
							return;
						}
						var slideMap = new Map();
						
						//用户拍摄
						$(".carousel-indicators,.carousel-inner","#userPhotoPanel #userPhotoCarousel").find("*").remove();
						$.each(data.userPhoto,function(i,n){
							//轮播组件
							if(i==0) {
								_addOverlay(false,data.userPhoto,map,centerPoint.lon,centerPoint.lat,null,null,oc,ooc,area,data.gisType,"#358ced");//如果是第一个，直接添加
							}
							slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:oc,gisType:data.gisType});//将事件加入map
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
								_addOverlay(true,data.userPhoto,cdata.map,cdata.lon,cdata.lat,cdata.point_accury,cdata.position,cdata.oc,ooc,area,cdata.gisType,"#358ced");//添加覆盖物
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
				function _addOverlay(flag,photos,map,lon,lat,point_accury,position,oc,ooc,area,gisType,color){
					map.removeOverlay(point);//清除点
					map.removeOverlay(circle);//清除圆
					map.removeOverlay(myPositionObj);//清除方向指标
					map.removeOverlay(marker);//清除点
					//用户坐标
					point = new BMap.Point(lon, lat);
					map.centerAndZoom(point, 18);  // 设置中心点坐标和地图级别
					map.enableScrollWheelZoom();
					circle = new BMap.Circle(point,point_accury,{strokeColor:"#358ced", strokeWeight:2, strokeOpacity:0.1,fillColor:"#358ced", fillOpacity: 0.01}); //创建圆（经度圈）
					map.addOverlay(circle);//增加圆
					//创建方向指示
					myPositionObj = new SquareOverlay(point, 100, 100, null, function(div){
						return $(div).append($("<img id='positionImg' src='"+contextPath+"/images/position.png'/>").rotate({angle:position})).get(0);
					});
					marker = new BMap.Marker(point,{icon:new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
					    scale: 1,//图标缩放大小
					    fillColor: color,//填充颜色
					    fillOpacity: 0.4 //填充透明度
					  })}); // 创建点
					if(flag){
						map.addOverlay(myPositionObj);//增加方向指示
						map.addOverlay(marker);//增加点
					}
					//最近的地址
					var geoc = new BMap.Geocoder();
					geoc.getLocation(point, function(rs){
						var addComp = rs.addressComponents;
						//道路名称赋值
						$("#audit_task_name").val(addComp.street);
						//参考项目,点击事件,样式等
						/* $("#consult,#audit_task_name","#auditSuccessPanel")
						.text(addComp.street  + ", " + addComp.streetNumber )
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
						); */
					});
					
					//原始坐标(***)
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
									BMap.Marker.prototype.setCoordinateX = function(_x){
										this.coordinateX=_x;
									}
									BMap.Marker.prototype.setCoordinateY = function(_y){
										this.coordinateY=_y;
									}
									var markerOther = new BMap.Marker(pointOther, {icon: myIconOther});
									markerOther.setAuditId(obj[0].auditId);
									markerOther.setCoordinateX(obj[0].coordinateX);
									markerOther.setCoordinateY(obj[0].coordinateY);
									//周边任务增加点击显示明细事件--000
									markerOther.addEventListener("click",function(){
										var _auditId = this.auditId;
										var _coordinateX = this.coordinateX;
										var _coordinateY = this.coordinateY;
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
												$(".panel-body,.text-center","#userMp4Panel").find("*").remove();
												$(".panel-body,.text-center","#userMp4Panel").append("<div>"
														+"<div class='embed-responsive embed-responsive-4by3'>"
														+"<video id='videoMp4' class='embed-responsive-item'"
														+"controls='controls'> Your browser does not support"
														+"the video tag."
														+"</video>"
														+"</div>"
														+"<p>如果无法播放视频，请点击<a id='videoMp4dow'>这里</a>下载。</p>"
														+"<p>缺失原因：<span id='noExistReason'></span></p></div>");
												var slideMap = new Map();
												$.each(rdata.userPhoto,function(i,n){
													$("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-indicators")
													.append('<li data-target="#userPhotoCarouselAround" data-slide-to="'+i+'" '+(i==0?'class="active"':'')+'></li>');
													//轮播组件
													if(i==0) {
														_addOverlay(false,rdata.userPhoto,map,n.lon,n.lat,n.point_accury,n.position,null,ooc,area,rdata.gisType,"#FBF7E2");//如果是第一个，直接添加
													}
													slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:null,gisType:rdata.gisType});//将事件加入map
													if(type == 7 || type == 9){
														//录像
														if(i==0){
															$('#userMp4PanelAround video#videoMp4Around').attr('src',n.video_url);
															$('#userMp4PanelAround a#videoMp4dowAround').attr('href',n.video_url);
															//缺失原因
															$('#userMp4PanelAround #noExistReasonAround').html(n.no_exist_reason);
														}else{
															$('#userMp4PanelAround .panel-body>div:last').clone().appendTo('#userMp4PanelAround .panel-body');
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
														
														//默认停止轮播，等待用户操作
														$("#userPhotoPanelAround #userPhotoCarouselAround").carousel('pause');
														//轮播事件
														//当调用 slide 实例方法时立即触发该事件。
														$('#userPhotoCarouselAround').unbind().on('slid.bs.carousel', function () {
															var _index = $("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-indicators li.active").attr("data-slide-to");
															var cdata = slideMap.get(_index);//得到当前选中的页面ID
															_addOverlay(true,rdata.userPhoto,cdata.map,cdata.lon,cdata.lat,cdata.point_accury,cdata.position,null,ooc,area,cdata.gisType,"#FBF7E2");//添加覆盖物
														});
													}
												});
												//显示周边任务的图片的第一张
												var pointOther1 = new BMap.Point(_coordinateX, _coordinateY);
												var markerOther1 = new BMap.Marker(pointOther1, {icon: new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
												    scale: 1, fillColor: "#FBF7E2", fillOpacity: 1 //填充透明度
												  })});
												map.addOverlay(markerOther1);//增加点
												
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
										polylineorgon = new BMap.Polygon(plArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0});//创建多边形
									}
									map.addOverlay(polylineorgon);//增加折线或多边形
								}
							}
						}
					}
					//新增查询周边任务范围(经济环境)
					$.post(contextPath+"/audit/findAroundForEnvir.html",{ x:lon, y:lat },
					function(rdata){
						if(rdata != null && rdata.length > 0){
							rdata = $.parseJSON(rdata);
							for (var m =0;m<rdata.length;m++) {
								var objs = rdata[m];
								if(objs !=null){
									var ob = objs.location;
									var obj = ob.coordinates;
									if(objs.location.type == "point"){//点
										var pointOther = new BMap.Point(obj[0].coordinateX, obj[0].coordinateY);
										 //设置marker图标为水滴
										var myIconOther = new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
										    scale: 1,//图标缩放大小
										    fillColor: "blue",//填充颜色
										    fillOpacity: 1 //填充透明度
										  });
										// 创建标注对象并添加到地图   
										BMap.Marker.prototype.setAuditId = function(_id){
											this.auditId=_id;
										}
										BMap.Marker.prototype.setCoordinateX = function(_x){
											this.coordinateX=_x;
										}
										BMap.Marker.prototype.setCoordinateY = function(_y){
											this.coordinateY=_y;
										}
										var markerOther = new BMap.Marker(pointOther, {icon: myIconOther});
										markerOther.setCoordinateX(obj[0].coordinateX);
										markerOther.setCoordinateY(obj[0].coordinateY);
										markerOther.addEventListener("click",function(){
											var _auditId = this.auditId;
											var _coordinateX = this.coordinateX;
											var _coordinateY = this.coordinateY;
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
													$(".panel-body,.text-center","#userMp4Panel").find("*").remove();
													$(".panel-body,.text-center","#userMp4Panel").append("<div>"
															+"<div class='embed-responsive embed-responsive-4by3'>"
															+"<video id='videoMp4' class='embed-responsive-item'"
															+"controls='controls'> Your browser does not support"
															+"the video tag."
															+"</video>"
															+"</div>"
															+"<p>如果无法播放视频，请点击<a id='videoMp4dow'>这里</a>下载。</p>"
															+"<p>缺失原因：<span id='noExistReason'></span></p></div>");
													var slideMap = new Map();
													$.each(rdata.userPhoto,function(i,n){
														$("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-indicators")
														.append('<li data-target="#userPhotoCarouselAround" data-slide-to="'+i+'" '+(i==0?'class="active"':'')+'></li>');
														//轮播组件
														if(i==0) {
															_addOverlay(false,rdata.userPhoto,map,n.lon,n.lat,n.point_accury,n.position,null,ooc,area,rdata.gisType,"#FBF7E2");//如果是第一个，直接添加
														}
														slideMap.put(i+"",{map:map,lon:n.lon,lat:n.lat,point_accury:n.point_accury,position:n.position,oc:null,gisType:rdata.gisType});//将事件加入map
														if(type == 7 || type == 9){
															//录像
															if(i==0){
																$('#userMp4PanelAround video#videoMp4Around').attr('src',n.video_url);
																$('#userMp4PanelAround a#videoMp4dowAround').attr('href',n.video_url);
																//缺失原因
																$('#userMp4PanelAround #noExistReasonAround').html(n.no_exist_reason);
															}else{
																$('#userMp4PanelAround .panel-body>div:last').clone().appendTo('#userMp4PanelAround .panel-body');
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
															
															//默认停止轮播，等待用户操作
															$("#userPhotoPanelAround #userPhotoCarouselAround").carousel('pause');
															//轮播事件
															//当调用 slide 实例方法时立即触发该事件。
															$('#userPhotoCarouselAround').unbind().on('slid.bs.carousel', function () {
																var _index = $("#userPhotoPanelAround #userPhotoCarouselAround > .carousel-indicators li.active").attr("data-slide-to");
																var cdata = slideMap.get(_index);//得到当前选中的页面ID
																_addOverlay(true,rdata.userPhoto,cdata.map,cdata.lon,cdata.lat,cdata.point_accury,cdata.position,null,ooc,area,cdata.gisType,"#FBF7E2");//添加覆盖物
															});
														}
													});
													//显示周边任务的图片的第一张
													var pointOther1 = new BMap.Point(_coordinateX, _coordinateY);
													var markerOther1 = new BMap.Marker(pointOther1, {icon: new BMap.Symbol(BMap_Symbol_SHAPE_POINT,{
													    scale: 1, fillColor: "#FBF7E2", fillOpacity: 1 //填充透明度
													  })});
													map.addOverlay(markerOther1);//增加点
													
													$("#userPhotoPanelAround #userPhotoCarouselAround").carousel('pause');
													showDialog($("#userPhotoPanelAround"));
												}
											});
										});
										map.addOverlay(markerOther);//增加点
									}else{//线面
										if(objs.task_id!=baseId){
											var plArray = new Array();
											for(var j=0;j<obj[0].length;j++){
												plArray.push(new BMap.Point(obj[0][j][0], obj[0][j][1]));
											}
											var polylineorgon;
											if(ob.type == "polyline"){
												polylineorgon = new BMap.Polyline(plArray, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});//创建折线
											}else if(ob.type == "polygon"){
												BMap.Polygon.prototype.setPolygonId = function(_id){
													this.polygonId=_id;
												}
												polylineorgon = new BMap.Polygon(plArray, {strokeColor:"red", strokeWeight:2, strokeOpacity:0.5,fillOpacity: 0.1});//创建多边形
												polylineorgon.setPolygonId(objs._id);
												polylineorgon.addEventListener("dblclick",function(){
													var _polygonId = this.polygonId;
													$.post(contextPath+"/editPlatform/queryOverlayInfoById.html",{
															id:_polygonId
														},
													function(rdata){
														if(rdata != null && rdata.length > 0){
															rdata = $.parseJSON(rdata);
															if(rdata.result=="success"){
																if(rdata.data){
																	var props = rdata.data.props;
																	if(props != undefined) {
																		var tbody = $("#tbody_around");
																		tbody.empty().append("<tr><th style='text-align:center;'>属性名称</th><th style='text-align: center;'>属性值</th></tr>");
																		if(props.length>0){
																			for(var i = 0; i < props.length; i++) {
																				var prop_name = props[i].prop_name;
																				var prop_value = props[i].prop_value;
																				tbody.append("<tr><td style='text-align:center;'>"+prop_name+"</td><td style='text-align: center;'>"+prop_value+"</td></tr>");
																			}
																			$("#propModal_around").modal('show');
																		}else alert("当前覆盖物无属性");
																	}
																}else alert("当前覆盖物无属性");
															}else alert(rdata.data);
														}
													});
												});
											}
											map.addOverlay(polylineorgon);//增加折线或多边形
										}
									}
								}
							}
						}
					});		
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
		//新增关闭事件,防止关闭显示在初始左上角位置
		$(".mydialog").css("position","absolute")
			.addClass("opacity ui-widget-content ui-resizable")
			.addClass("ui-widget-content")
			.find("button.close_style_two").click(function(){//关闭按钮事件
			$($(this).parent(".panel-heading").parent(".mydialog")).hide();
		})
		
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
		
		//显示图层类型弹窗
		function showLayerTypeDialog(el){
			curr_layerId = "";
			var _offset = $(el).data("_offset");
			if(_offset != undefined) {
				$(el)
				.show()
				.offset({ top: _offset.top, left: _offset.left });
			}else {
				$(el).show();
			}
			
		}
		
		//两个额外的翻页按钮
		$("#prevButton").click(function(){
			$("#userPhotoCarousel").carousel("prev");
		});
		$("#nextButton").click(function(){
			$("#userPhotoCarousel").carousel("next");
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
					$("#ztree").hide();
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
					$("#ztree").hide();
					$("#taskType > li > a[id='frTask'] ").click();//刷新未认领表格
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
		
		//真正的审核通过
		/* $("#doAuditSuccessButton").click(function(){
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
		}); */
		
		//审核通过(按钮触发事件)
		$("#auditSuccessButton").click(function(){
			$.post(contextPath+"/audit/auditSuccess.html",
				{
					taskId:taskId,
					baseId:baseId
				},
				function(data){
					data = $.parseJSON(data);
					if(data.flag==null)
						alert("任务不存在或参数缺失");
					else if(data.flag==0){
						alert("审核不通过完成");
						refresh();						
					}
					else if(data.flag==1){
						alert("审核通过完成");
						refresh();						
					}
					else if(data.flag==2){
						alert("审核无法执行:状态不完全");
					}
			});
		});
		//刷新个数列表,以及隐藏相关项
		function refresh(){
			refreshCount();//刷新个数
			$("#taskType > li > a[id='fcTask'] ").click();//刷新表格
			$("#auditFailurePanel").hide();
			$("#ztree").hide();
			$("#optionButtons > button").hide();
		}
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
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/ztree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ztree/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript"	src="<%=request.getContextPath()%>/js/ztree/jquery.ztree.excheck-3.5.min.js"></script>
<script type="text/javascript"	src="<%=request.getContextPath()%>/js/ztree/jquery.ztree.exedit-3.5.min.js"></script>
<script type="text/javascript"	src="<%=request.getContextPath()%>/js/ztree/jquery.ztree.exhide-3.5.min.js"></script>
<script src="<%=request.getContextPath()%>/js/overlay_extend.js"></script>
<script src="<%=request.getContextPath()%>/js/editPlatform.js"></script>
<script src="<%=request.getContextPath()%>/js/array.js"></script>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-xs-12 col-md-10">
				<div class="panel panel-default">
				  <div class="panel-body">
				    <div id="allmap" style="width: 100%;height:300px;overflow: hidden;margin:0;font-family:'微软雅黑';"></div>
				  </div>
				  <!-- 添加图层类型 -->
				  <div id="layerType" class="panel panel-default mydialog draggable" style="display:none;width:30%;overflow: auto;bottom:71px;right:35%;">
					<div class="panel-heading">图层<font style="color:red;font-size:80%;">(点击图层名称变蓝即选中为当前图层，点击复选框可选择多个图层)</font>
						<button type="button" class="close_style_two">
							<span>&times;</span>
						</button>
					</div>
					<div style="height:100px;overflow-y:auto">
						<div id="typeDiv"></div>
				  	</div>
				  	<div class="modal-footer" style="text-align:left;padding-left: 15px;padding-top: 2px;padding-bottom: 2px;" >
						 <div style="text-align:left;float:left;" >
						  	     <input type="checkbox"   name="checkAll"  id="checkAll" ><label for="checkAll"> 全选</label>
				  	     </div>
				  	     <div style="text-align:center;float:right;" >
							<button class="btn btn-info" type="button" id="editLayerButton">编辑</button>
				  	     </div>
				  	</div>
				</div>
				  <div class="panel-footer" id="optionButtons">
				  	<!-- <button class="btn btn-default" id="specimenPageButton" style="display: none;"><span class="glyphicon glyphicon-file" aria-hidden="true"></span> 样张</button>
				  	<button class="btn btn-default" id="userMp4Button" style="display: none;"><span class="glyphicon glyphicon-film" aria-hidden="true"></span> 用户拍摄的录像</button> -->
				  	<button class="btn btn-default" id="claimTaskButton" style="display: none;"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>认领</button>
				  	<button class="btn btn-default" id="unClaimTaskButton" style="display: none;"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>取消认领</button>
				  	<!-- <button class="btn btn-default" id="appealTaskButton" style="display: none;"><span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> 申诉</button>
				  	<button class="btn btn-default" id="taskDetailButton" style="display: none;"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>任务明细</button> -->
				  	<button class="btn btn-default" id="auditHistoryButton" style="display: none;"><span class="glyphicon glyphicon-transfer" aria-hidden="true"></span>审核历史明细</button>
				  	<button class="btn btn-default" id="auditSuccessButton" style="display: none;"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>审核</button>
				  	<!-- <button class="btn btn-default" id="auditFailureButton" style="display: none;"><span class="glyphicon glyphicon-thumbs-down" aria-hidden="true"></span> 审核不通过</button>
				  	<button class="btn btn-default" id="userPhotoButton" style="display: none;"><span class="glyphicon glyphicon-camera" aria-hidden="true"></span> 用户拍摄的照片</button> -->
				  	<div class="btn-group opacity" style="display: none;" id="slideOption">
					  	<button type="button" class="btn btn-default" id="prevButton"><span class="glyphicon glyphicon-step-backward" aria-hidden="true"></span></button>
					  	<button type="button" class="btn btn-default" id="nextButton"><span class="glyphicon glyphicon-step-forward" aria-hidden="true"></span></button>
					</div>
				</div>
			</div>
		</div>
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
						controls="controls">Your browser does not support the video tag.</video>
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
						controls="controls"> Your browser does not support the video tag.
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
				<!-- 新增类型选项 -->
				<div>
					确认类型：<input class="form-control" id="taskClassNameFromAudit" value="" placeholder="确认类型"  maxlength ="100">
				</div>
				<div>
					道路名称：<input class="form-control" id="audit_task_name" placeholder="道路名称"/>
				</div>
				<button type="button" class="btn btn-primary"
					style="margin-top: 5px;" id="doAuditSuccessButton"><span class="glyphicon glyphicon-thumbs-up" aria-hidden="true"></span> 通过</button>
			</div>
		</div>
	</div>
	
	<!-- 审核不通过 -->
	<div id="auditFailurePanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">审核不通过
			
			<button type="button" class="close_style_two" >
				<span>&times;</span>
			</button>
		</div>
		<div class="panel-body">
			<!-- 审核不通过时出现 -->
			<div id="noapproval">
				<div>
					给采集者留言：
					<textarea class="form-control" rows="3" id="commentMessage"></textarea>
				</div>
				<button type="button" class="btn btn-primary"
					style="margin-top: 5px;" id="doAuditFailureButton"><span class="glyphicon glyphicon-thumbs-down" aria-hidden="true"></span> 不通过</button>
			</div>
		</div>
	</div>
	<div id="ztree" class="panel panel-default mydialog draggable" style="display:none;width:20%;overflow: auto;height: 70%;">
		<div class="panel-heading">任务明细
			<button type="button" class="close">
				<span>&times;</span>
			</button>
		</div>
		<div id="ztreeContent"></div>
	</div>
	
	
	<!-- 添加属性模态框（Modal） -->
	<div class="modal fade" id="propModal" tabindex="-1" role="dialog" 
	   aria-labelledby="myModalLabel" aria-hidden="true">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" 
	               aria-hidden="true">×
	            </button>
	            <input id="layerId"  type="hidden" />
	            <h4 class="modal-title" id="myModalLabel" >
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
	                                <th style="text-align: center;"><i class="icon-download-alt" onclick="addNewRow('','')">add</i></th>
	                            </tr>
	                        </tbody>
	                    </table>
	                    </div>
	                </form>
	         </div>
	         <div class="modal-footer " style="text-align:center;" >
	         	<button type="button" class="btn btn-primary" onclick="addProp();">
	               添加公共属性
	            </button>
	         	<button type="button" class="btn btn-primary"  id="saveProp" onclick="saveProp();">
	               保存
	            </button>
	            <button type="button" class="btn btn-default" 
	               data-dismiss="modal">关闭
	            </button>
	         </div>
	      </div>
	   </div>
	</div>
	
	<!-- 添加图层tree -->
	<div id="layerTreeDiv" class="panel panel-default mydialog draggable" style="display:none;">
		<div class="panel-heading">图层类型
			<button type="button" class="close_style_two">
				<span>&times;</span>
			</button>
		</div>
		<div style="width:600px;">
			<div  style="height:600px;width:40%;overflow-y:auto;float:left;border:1px solid #DDDDDD;">
				<ul id="layerTree" class="ztree"></ul>
			</div>
			<div  style="height:600px;width:60%;overflow-y:auto;float:right;border:1px solid #DDDDDD;">
				<div class="modal-body">
		            <form id="addpropForm">
						<div style="height:500px;overflow-y:auto">
							<input id="prop_layer_id"  type="hidden" />
		                    <table id="addprop_table" class="table table-bordered">
		                        <tbody>
		                            <tr>
		                                <th style="text-align: center;">属性名称</th>
		                                <th style="text-align: center;"><i class="glyphicon glyphicon-plus" onclick="addPropNewRow('')"></i></th>
		                            </tr>
		                        </tbody>
		                    </table>
		                    </div>
		               </form>
		         </div>
	         <div class="modal-footer " style="text-align:center;" >
	         	<button type="button" class="btn btn-primary" id="saveLayerProp" onclick="saveLayerProp();">
	               保存
	            </button>
	         </div>
			</div>
	  	</div>
	</div>

	<!-- 添加图层 -->
	<div id="addLayerDiv">
		<form>	
			<div class="text-right panel-heading">
				<button type="button" class="close"  id="closeLayerDiv"><span>&times;</span></button>
			</div>	
			<div class="form-group" id="pro_type_div">
				<input
					type="text" class="form-control" id="pro_type" name="pro_type"
					placeholder="图层标识">
			</div>
			<div class="form-group">
				<input
					type="text" class="form-control"
					id="layer_name" name="layer_name" placeholder="图层名称"  >
					<input id="layer_pId"  type="hidden" />
					<input id="layer_id"  type="hidden" />
			</div>
			<div class="form-group">
				<input
					type="text" class="form-control" id="order_no" name="order_no"
					placeholder="排序号">
			</div>
			<div class="text-center">
				<button class="btn btn-info" type="button"  id="doSaveButton">
					<span class="glyphicon glyphicon-save" aria-hidden="true"></span>保存
				</button>
			</div>
		</form>
	</div>
	
	<!-- 经济环境数据开始 -->
	<!-- 用户拍摄照片 -->
	<div id="userPhotoOnePanel" class="panel panel-default mydialog" style="display:none;width:30%;">
		<div class="panel-heading">用户拍摄的照片
			<button type="button" class="close">
				<span>&times;</span>
			</button>
			<span id="invalidDiv" style="display: none;float: right;padding-right: 10px;">&nbsp;&nbsp;<input type="checkbox" id="invalid"/>无效拍摄</span>
		</div>
		<div class="inner">
			<div>
				<img src="" alt="First slide">
			</div>
		</div>
	</div>
	
	<!-- 周边覆盖物属性显示 -->
	<div class="modal fade" id="propModal_around" tabindex="-1" role="dialog" 
	   aria-labelledby="myModalLabel" aria-hidden="true">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" 
	               aria-hidden="true">×
	            </button>
	            <h4 class="modal-title" id="myModalLabel" >当前覆盖物属性信息</h4>
	         </div>
	         <div class="modal-body">
	            <form id="advForm_around">
					<div style="height:auto;overflow-y:auto">
	                    <table id="time_table_around" class="table table-bordered">
	                        <tbody id="tbody_around">
	                            <tr>
	                                <th style="text-align: center;">属性名称</th>
	                                <th style="text-align: center;">属性值</th>
	                            </tr>
	                        </tbody>
	                    </table>
                    </div>
                </form>
	         </div>
	      </div>
	   </div>
	</div>
</body>
</html>
