var dataMap = new Map();//存放当前任务的点，面信息;格式为key为主键ID，value为点，面json对象
var layerMap = new Map();//根据图层类型存放当前点，面信息;格式为key为类型ID或者图层ID，value为数组（数组存放当前类型的覆盖物对象overlay object）
var curr_overlay;//当前操作的覆盖物
var curr_taskId ;//当前任务ID
var curr_layerId;//当前选择图层ID
var curr_batchId;//当前子任务ID
var curr_proType;//当前项目类型
var curr_treeNode;//当前点击添加按钮的节点
var curr_treeNode_id;//当前点击树节点Id
var tree_rootId = "0000000000000000";
var styleOptions;//覆盖物样式
var initFlag = false;
//-----------------事件初始化---start------
//右键点击属性触发方法
var addPropOverlay = function(e,ee,overlay){
	curr_overlay = overlay;
	clearAllRow();
	$('#propModal').modal('show');
	var id = overlay.id;
	var jsondata;
	// 也可以随时从数据库查询数据展现，目前是查询一次保存到map中，每次展现都是从map中取数据
	/*$.ajax({ 
        type:"POST",
        url:contextPath+"/editPlatform/queryOverlayInfoById.html",
        dataType:"json",
        async: false,//设置为同步
        data:{"id":id},
        error:function(data) {
        	alert("根据ID查询失败，请联系管理员");
        },
        success:function(data){ 
        	if(data.result == "success") {
        		jsondata = data.data;
        	}else if(data.result == "fail") {
        		alert(data.data);
        	}
        }
	});*/
	jsondata = dataMap.get(id);
	var layerId = jsondata.layer_id;
	if(layerId != undefined){
		//下拉框类型设置默认值
		$("#layerId").val(layerId);
		var props = jsondata.props;
		if(props != undefined && props.length > 0) {
			for(var i = 0; i < props.length; i++) {
				var prop_name = props[i].prop_name;
				var prop_value = props[i].prop_value;
				addNewRow(prop_name, prop_value);
			}
		}else {
			addProp();
		}
		
	}
	
}
//右键点击编辑触发方法-拖动覆盖物坐标点
var editOverlay = function(e,ee,overlay){
	 curr_overlay = overlay;
	 if(overlay  instanceof BMap.Marker) {
		 overlay.enableDragging();
	 }else {
		 overlay.enableEditing();
	 }
}
//右键点击取消编辑触发方法-取消拖动覆盖物坐标点
var cancelEditOverlay = function(e,ee,overlay){
	 curr_overlay = overlay;
	 if(overlay  instanceof BMap.Marker) {
		 overlay.disableDragging();
	 }else {
		 overlay.disableEditing();
	 }
}

//右键点击编辑触发方法-删除覆盖物
var removeOverlay = function(e,ee,overlay){
	if(confirm("确认要删除此对象?")) {
		var id = overlay.id;
		$.ajax({ 
	        type:"POST",
	        url:contextPath+"/editPlatform/deleteOverlay.html",
	        dataType:"json",
	        data:{"id":id},
	        error:function(data) {
	        	alert("删除失败，请联系管理员");
	        },
	        success:function(data){ 
	        	if(data.result == "success") {
	        		alert("删除成功！");
	        		map.removeOverlay(overlay);
	        		var json = dataMap.get(id);
	        		if(json != undefined) {
	        			//从dataMap删除此对象json数据
	        			dataMap.remove(id);
	        			var layerId = json.layer_id;
	        			delFromLayerMap(layerId, overlay);
	        			//  需要从数据库删除 ajax 请求到后台删除
	        		}
	        	}else if(data.result == "fail") {
	        		alert(data.data);
	        	}
	        }
		});
	}
}
var addListener = function  (overlay) {
	//创建右键菜单
	var markerMenu=new BMap.ContextMenu();
	markerMenu.addItem(new BMap.MenuItem("属性",addPropOverlay.bind(overlay)));
	if(overlay instanceof BMap.Marker) {//点
		markerMenu.addItem(new BMap.MenuItem("拖动",editOverlay.bind(overlay)));
		markerMenu.addItem(new BMap.MenuItem("取消拖动",cancelEditOverlay.bind(overlay)));
		//markers.push(overlay);
		//拖拽结束时触发此事件
		overlay.addEventListener("dragend", function(e) {
			currOverlay = e.target;
			var  overlay_id  = currOverlay.id;
			var point = currOverlay.getPosition();
			var points = new Array();
			points.push({"lng":point.lng,"lat":point.lat,"overlay_id":overlay_id});
			//  更新数据库中的坐标点
			updatePoint(points,overlay_id);
		}); 
//	}else if(overlay instanceof BMap.Circle) {//圆
//		
//	}else if(overlay instanceof BMap.Polyline) {//线-折线
//		
	}else if(overlay instanceof BMap.Polygon) {//多边
		markerMenu.addItem(new BMap.MenuItem("编辑",editOverlay.bind(overlay)));
		markerMenu.addItem(new BMap.MenuItem("取消编辑",cancelEditOverlay.bind(overlay)));
		//覆盖物的属性发生变化时触发事件
		overlay.addEventListener("lineupdate", function(e) {
//			alert("lineupdate");
			//  更新数据库中的坐标点
			currOverlay = e.target;
			var  overlay_id  = currOverlay.id;
			var points = new Array();
			var pointArray = overlay.getPath();
			for(var i = 0; i < pointArray.length; i++) {
				points.push({"lng":pointArray[i].lng,"lat":pointArray[i].lat,"overlay_id":overlay_id});
			}
			//  更新数据库中的坐标点
			updatePoint(points,overlay_id);
		}); 
	}
	markerMenu.addItem(new BMap.MenuItem("删除",removeOverlay.bind(overlay)));
	//添加右键事件
	overlay.addContextMenu(markerMenu);
}

//覆盖物创建完成后触发的方法
var overlaycomplete = function(e){
	var overlay = e.overlay;
	if(curr_taskId == "" || curr_taskId == undefined) {
		map.removeOverlay(overlay);
		alert("请先选择任务！");
		return;
	}
	if(curr_layerId == "" || curr_layerId == undefined  || curr_batchId == "" || curr_batchId == undefined) {
		map.removeOverlay(overlay);
		alert("请先选择图层类型！");
		return;
	}
	var jsonOverlay = {};
	var points = new Array();
	jsonOverlay.type = e.drawingMode;
	jsonOverlay.task_id = curr_taskId;
	jsonOverlay.layer_id = curr_layerId;
	jsonOverlay.batch_id = curr_batchId;
	if(overlay instanceof BMap.Marker) {//点
		var point = overlay.getPosition();
		points.push({"lng":point.lng,"lat":point.lat});
	}else if(overlay instanceof BMap.Polygon) {//多边
		var pointArray = overlay.getPath();
		for(var i = 0; i < pointArray.length; i++) {
			points.push({"lng":pointArray[i].lng,"lat":pointArray[i].lat});
		}
	}
	jsonOverlay.points = points;	
	//  保存到库
	$.ajax({ 
        type:"POST",
        url:contextPath+"/editPlatform/insertOverlay.html",
        dataType:"json",
        data:JSON.stringify(jsonOverlay),
        contentType : 'application/json;charset=utf-8',
        error:function(data) {
        	alert("创建失败，请联系管理员");
        	map.removeOverlay(overlay);
        },
        success:function(data){ 
        	if(data.result == "success") {
        		var id= data.data;
        		map.removeOverlay(overlay);
        		if(overlay instanceof BMap.Marker) {//点
        			overlay = new Marker_e(e.overlay.getPosition(),id,curr_layerId);
        		}else if(overlay instanceof BMap.Polygon) {//多边
        			overlay =new Polygon_e(e.overlay.getPath(),styleOptions,id,curr_layerId);
        		}
        		map.addOverlay(overlay);  
//            	curr_overlay = overlay;
            	//添加监听
            	addListener(overlay);
            	jsonOverlay.id = id;
            	dataMap.put(id, jsonOverlay);
            	addToLayerMap(curr_layerId,overlay);
        	}else if(data.result == "fail") {
        		alert(data.data);
        		map.removeOverlay(overlay);
        	}
        }
	});
};
//-----------------事件初始化---end------
//-----------------数据初始化---start------
function initData() {
	//如果任务ID不存在，则不需加载
	if(curr_taskId == "" || curr_taskId == undefined) {
		return;
	}
	initLayer();//初始化图层
	dataMap.clear();
	layerMap.clear();
	initOverlay();//初始化覆盖物信息
	  //----------------------鼠标绘制点、线、面、多边形（矩形、圆）的编辑工具条初始化------------------------------start------------
		 styleOptions = {
		    strokeColor:"red",    //边线颜色。
		  //  fillColor:"red",      //填充颜色。当参数为空时，圆形将没有填充效果。
		    strokeWeight: 3,       //边线的宽度，以像素为单位。
		    strokeOpacity: 0.8,	   //边线透明度，取值范围0 - 1。
		    fillOpacity: 0.6,      //填充的透明度，取值范围0 - 1。
		    strokeStyle: 'solid' //边线的样式，solid或dashed。
		}
		   /*
		    //更改marker类型图标
		    var myIcon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {
			offset: new BMap.Size(10, 25), // 指定定位位置
			imageOffset: new BMap.Size(0, 0 - 10*25) // 设置图片偏移
			}); */
		//实例化鼠标绘制工具
		if(!initFlag){
			var drawingManager = new BMapLib.DrawingManager(map, {
				isOpen: false, //是否开启绘制模式
				enableDrawingTool: true, //是否显示工具栏
				//enableCalculate:true,//绘制是否进行测距(画线时候)、测面(画圆、多边形、矩形) 
				drawingToolOptions: {
					anchor: BMAP_ANCHOR_TOP_LEFT, //位置
					offset: new BMap.Size($("#allmap").width()/2, 5), //偏离值
					 scale: 0.7, 
					drawingModes : [
					                BMAP_DRAWING_MARKER,
					                //BMAP_DRAWING_CIRCLE, //圆
					                // BMAP_DRAWING_POLYLINE,//折线
					                BMAP_DRAWING_POLYGON,
					                // BMAP_DRAWING_RECTANGLE //矩形
					                ]
				},
				markerOptions:{},//icon:myIcon
				//  circleOptions: styleOptions, //圆的样式
				// polylineOptions: styleOptions, //线的样式
				polygonOptions: styleOptions //多边形的样式
				//rectangleOptions: styleOptions //矩形的样式
			});  
			//添加鼠标绘制工具监听事件，用于获取绘制结果
			drawingManager.addEventListener('overlaycomplete', overlaycomplete);
			initFlag = true;
		} 
	//----------------------鼠标绘制点、线、面、多边形（矩形、圆）的编辑工具条初始化------------------------------end------------
		
}
//初始化覆盖物信息
function initOverlay() {
	//----------------------覆盖物初始化------------------------------start------------
  	$.ajax({ 
            type:"POST",
            url:contextPath+"/editPlatform/queryAll.html",
            dataType:"json",
            data:{"task_id":curr_taskId},
            error:function(data) {
            	alert("覆盖物初始化失败，请联系管理员");
            },
            success:function(data){ 
            	if(data.result == "success"){  
            		var jsondatas = data.data;
            		for(var i = 0 ; i < jsondatas.length; i++) {
            			//  从数据库中拿数据初始化
            			var jsondata = jsondatas[i];
            			var id = jsondata.id;
            			var type = jsondata.type;
            			var layer_id = jsondata.layer_id;
            			var json_points = jsondata.points;
            			var overlay;
            			
            			if(jsondata.type == 'marker') {//点
//            				 overlay = new BMap.Marker(new BMap.Point(json_points[0].lng, json_points[0].lat));
            				overlay = new Marker_e(new BMap.Point(json_points[0].lng, json_points[0].lat),id,layer_id);
            			}else if(jsondata.type == 'polygon') {
            				var points = new Array();
            				for(var j = 0; j < json_points.length; j++) {
            					var point = new BMap.Point(json_points[j].lng, json_points[j].lat);
            					points.push(point);
            				}
//            				 overlay = new BMap.Polygon(points, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5}); 
            				 overlay = new Polygon_e(points,{strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5},id,layer_id);
            			}
            			//存放js 静态数据
            			dataMap.put(id, jsondata);
            			var overlayArray = layerMap.get(layer_id);//存放当前类型的覆盖物对象overlay object
            			if(overlayArray == undefined) {
            				overlayArray = new Array();
            				layerMap.put(layer_id, overlayArray);
            			}
            			overlayArray.push(overlay);
            			//地图添加覆盖物
            			map.addOverlay(overlay);  
            			//添加监听
            			addListener(overlay);
            		}
            	}else if(data.result == "fail"){
            		alert(data.data);
            	}
            }
         });  
  	//----------------------覆盖物初始化------------------------------end------------
}

//初始化图层
function initLayer() {
	//----------------初始化覆盖物类型------start---------------
	$("#typeDiv").empty();
	$.ajax({ 
        type:"POST",
        url:contextPath+"/editPlatform/queryLayerByProType.html",
        dataType:"json",
        data:{"pro_type":curr_proType},
        error:function(data) {
        	alert("初始化覆盖物类型失败，请联系管理员");
        },
        success:function(data){ 
        	if(data.result == "success") {
        		$("#typeDiv").empty();
        		var jsondatas = data.data;
            	for(var i = 0;  i < jsondatas.length; i++) {
            		var jsondata = jsondatas[i];
            		$("#typeDiv").append("<div>&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox'   typename='"+jsondata.name+"' name='typecheckbox'  id='"+jsondata.id+"'  onclick='showOrHide();'><label for='"+jsondata.id+"' onclick='selectLayer(this);'> "+jsondata.name+"</label></div>");
            	}
        	}else if(data.result == "fail") {
        		alert(data.data);
        	}
        }
	});
	//----------------初始化覆盖物类型------end---------------	
}

//更新坐标信息
function updatePoint(points,overlay_id) {
	$.ajax({ 
        type:"POST",
        url:contextPath+"/editPlatform/updatePoint.html",
        dataType:"json",
        data:JSON.stringify(points),
        contentType : 'application/json;charset=utf-8',
        error:function(data) {
        	alert("更新坐标信息失败，请联系管理员");
        },
        success:function(data){ 
        	if(data.result == "success") {
            	var jsondata = dataMap.get(overlay_id);
            	jsondata.points= points;
        	}else if(data.result == "fail") {
        		alert(data.data);
        	}
        }
	});
}
//显示或者隐藏当前图层覆盖物信息
function showOrHide() {
	  $("[name='typecheckbox']").each(function() {
		  	var flag = $(this).prop("checked");
		  	var overlays = layerMap.get($(this).attr("id"));
	  		if(overlays != undefined) {
	  			for(var i = 0; i < overlays.length; i++) {
	  				if(flag) {
	  					overlays[i].show();
				  	}else {
				  		overlays[i].hide();
				  	}
	  			}
	  		}
	  })
}
//时时从数据库查询当前图层下的覆盖物显示（之前先把地图上的覆盖物清空）
/*function showOrHide() {
	map.clearOverlays();//清空地图
	var layer_ids = new Array();
	  $("[name='typecheckbox']").each(function() {
		  	var flag = $(this).prop("checked");
		  	if(flag) {
		  		layer_ids.push({"layer_id":$(this).attr("id")});
		  	}
	  })
	  $.ajax({ 
	        type:"POST",
	        url:contextPath+"/editPlatform/queryOverlayByLayerIds.html",
	        dataType:"json",
//	        async: false,//设置为同步
	        data:JSON.stringify(layer_ids),
	        contentType : 'application/json;charset=utf-8',
	        error:function(data) {
	        	alert("查询失败，请联系管理员");
	        },
	        success:function(data){ 
	        	if(data.result == "success") {
	        		var jsondatas = data.data;
            		for(var i = 0 ; i < jsondatas.length; i++) {
            			//  从数据库中拿数据初始化
            			var jsondata = jsondatas[i];
            			var id = jsondata.id;
            			var type = jsondata.type;
//            			var layer_id = jsondata.layer_id;
            			var json_points = jsondata.points;
            			var overlay;
            			
            			if(jsondata.type == 'marker') {//点
            				 //overlay = new BMap.Marker(new BMap.Point(json_points[0].lng, json_points[0].lat));
            				 overlay = new Marker_e(new BMap.Point(json_points[0].lng, json_points[0].lat),id,layer_id);
            			}else if(jsondata.type == 'polygon') {
            				var points = new Array();
            				for(var j = 0; j < json_points.length; j++) {
            					var point = new BMap.Point(json_points[j].lng, json_points[j].lat);
            					points.push(point);
            				}
            				// overlay = new BMap.Polygon(points, {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5}); 
            				 overlay = new Polygon_e(points,{strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5},id,layer_id);
            			}
            			//存放js 静态数据
            			dataMap.put(id, jsondata);
//            			var overlayArray = layerMap.get(layer_id);//存放当前类型的覆盖物对象overlay object
//            			if(overlayArray == undefined) {
//            				overlayArray = new Array();
//            				layerMap.put(layer_id, overlayArray);
//            			}
//            			overlayArray.push(overlay);
            			//地图添加覆盖物
            			map.addOverlay(overlay);  
            			//添加监听
            			addListener(overlay);
            		}
	        	}else if(data.result == "fail") {
	        		alert(data.data);
	        	}
	        }
		});
}*/

//添加到layerMap
function addToLayerMap(layerId,overlay){
	var overlayerArray = layerMap.get(layerId);
	 if(overlayerArray == undefined) {
		 overlayerArray = new Array();
		 overlayerArray.push(overlay);
		 layerMap.put(layerId, overlay);
	 }else {
		 overlayerArray.push(overlay);
	 }
}
//从layerMap删除原有关系
function delFromLayerMap(layerId,overlay){
	var overlayerArray = layerMap.get(layerId);
	if(overlayerArray != undefined) {
		overlayerArray.remove(overlay);
	}
}

//------------------------------------------------------------覆盖物属性窗口 ----覆盖物属性 增加，删除，修改 和添加公共属性----------------------------------start------------------------

//清除覆盖物属性所有行，并添加行头
function clearAllRow() {
	var table1 = $("#time_table");
	table1.empty();
	var row = $("<tr></tr>");
	var td_1 = $("<th style='text-align: center;width:45%;'></th>");
	var td_2 = $("<th style='text-align: center;width:45%;'></th>");
	var td_3 = $("<th style='text-align: center;font-size:21px;vertical-align:middle;width:10%;'></th>");
	td_1.append("属性名称");
	td_2.append("属性值");
//	td_3.append($("<i class='glyphicon glyphicon-plus' onclick='addNewRow(\"\",\"\")'></i>"));
	row.append(td_1);
	row.append(td_2);
//	row.append(td_3);
	table1.append(row);
}
var row_count = 2;    //因为页面已经有一行了
//添加覆盖物属性行
function addNewRow(name,value) {
  var table1 = $("#time_table");
  var row = $("<tr></tr>");
  var td_1 = $("<td style='text-align: center;'></td>");
  var td_2 = $("<td style='text-align: center;'></td>");
  var td_3 = $("<td style='text-align: center;font-size:21px;vertical-align:middle;'></td>");
  
  td_1.append($("<input type='text'  class='form-control' name='prop_name'  id='prop_name_"+ row_count +"'  value='"+name+"'  placeholder='属性名称' readOnly/>"));
  td_2.append($("<input type='text' class='form-control' name='prop_value'  id='prop_value_"+ row_count +"'  value='"+value+"'  placeholder='属性值' />"));
//  td_3.append($("<i class='glyphicon glyphicon-trash'  id='"+ row_count +"' onclick='del("+ row_count +")'></i>"));
  row.append(td_1);
  row.append(td_2);
//  row.append(td_3);
  table1.append(row);
  row_count++;
}
//删除行
function del(e) {
  var ckbs = $("#"+e+"");
  ckbs.each(function() {
      $(this).parent().parent().remove();
  });
}
//添加公共属性
function addProp() {
	var layer_id = $("#layerId").val();
	$.ajax({ 
        type:"POST",
        url:contextPath+"/editPlatform/queryLayerProps.html",
        dataType:"json",
        data:{"layer_id":layer_id},
        error:function(data) {
        	alert("查询图层属性失败，请联系管理员");
        },
        success:function(data){ 
        	if(data.result == "success") {
        		var jsondatas = data.data;
            	for(var i = 0;  i < jsondatas.length; i++) {
            		var jsondata = jsondatas[i];
            		addNewRow(jsondata.prop_name,"")
            	}
        	}else if(data.result == "fail") {
        		alert(data.data);
        	}
        }
	});
}

//属性保存操作
function saveProp() {
	var prop_names =  $("[name='prop_name']");
	var prop_values =  $("[name='prop_value']");
	if( prop_names.length == 0) {
		alert("请至少添加一个属性！");
		return;
	}
	var props = new Array();
	for(var i = 0; i < prop_names.length; i++) {
//		if($.trim(prop_names[i].value) == "" || $.trim(prop_values[i].value) == "") {
//			alert("请输入属性值和属性名称！");
//			return;
//		}
		var prop = {};
		prop.prop_name = prop_names[i].value;
		prop.prop_value = prop_values[i].value;
		props.push(prop);
	}
	var id = curr_overlay.id;
	var jsondata = dataMap.get(id);
	$("#saveProp").attr("disabled",true); 
	//设置属性信息
	jsondata.props = props;
	 $.ajax({ 
	        type:"POST",
	        url:contextPath+"/editPlatform/updateOverlay.html",
	        dataType:"json",
	        data:JSON.stringify(jsondata),
	        contentType : 'application/json;charset=utf-8',
	        error:function(data) {
	        	alert("保存失败，请联系管理员");
	        	$("#saveProp").attr("disabled",false); 
	        }, 
	        success:function(data){ 
	        	if(data.result == "success") {
	        		alert("保存成功！");
	        		//隐藏模态窗口
	        		$('#propModal').modal('hide');
//	        		showOrHide();
	        	}else if(data.result == "fail") {
	        		alert(data.data);
	        	}
	        	$("#saveProp").attr("disabled",false); 
	        }
		});
}
//------------------------------------------------------------覆盖物属性窗口 ----覆盖物属性 增加，删除，修改 和添加公共属性----------------------------------end------------------------


//------------------------------------------------------------图层列表窗口-----图层选中，全选和取消全选，以及图层编辑 （显示图层树窗口）----------------------------------start------------------------
//选择当前图层
function selectLayer(obj) {
	$("#typeDiv >div").each(function(i,n){
		$(this).removeClass("active")
	});
	curr_layerId = $(obj).attr("for");
	$(obj).parent().addClass("active");
}

//--------------------初始化方法
$(function(){
	//类型复选框全选和取消全选
	$("#checkAll").click(function(){
		 var flag = $(this).prop("checked");
		  $("[name='typecheckbox']").each(function() {
			  $(this).prop("checked",flag);
		  })
		  showOrHide();
	});
	//编辑按钮 显示图层树窗口
	$("#editLayerButton").click(function(){
		$.fn.zTree.init($("#layerTree"), setting);
		showLayerTree("#layerTreeDiv");
		//重置 图层属性的layer_id标识
		$("#prop_layer_id").val("");
	});
});

//------------------------------------------------------------图层列表窗口-----图层选中，全选和取消全选，以及图层编辑 （显示图层树窗口）----------------------------------end------------------------


//------------------------------------------------------------图层树ztree操作----图层树 增加，修改，删除和图层属性的增加，修改，删除----------------------------------start------------------------
var setting = {
		view: {
//			addHoverDom: addHoverDom,
//			removeHoverDom: removeHoverDom,
			selectedMulti: false
		},
		edit: {
			drag:{
				isCopy: false,
				isMove :false 
			},
			enable : true,
//			showRemoveBtn : true,
//			showRenameBtn : true
			showRemoveBtn: false,
			showRenameBtn: showRenameBtn
		},
		async: {
			enable: true,
			url:contextPath+"/editPlatform/queryLayerTree.html",
			autoParam:["id", "name", "level"],
			//otherParam:{},
			dataFilter: filter
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			beforeEditName: beforeEditName,
//			beforeRemove: beforeRemove,
			onClick: onTreeClick,
			onAsyncSuccess:zTreeOnAsyncSuccess   
		}
	};
	//用于捕获异步加载正常结束的事件回调函数 展示所有节点
	function zTreeOnAsyncSuccess(event, treeId, treeNode, msg) {
		var zTree = $.fn.zTree.getZTreeObj("layerTree");
		var nodes;
		if(treeNode == undefined) {
			nodes = zTree.getNodes();
		}else {
			nodes = treeNode.children;
		}
		if(nodes != undefined && nodes.length > 0) {
			for(var i = 0; i < nodes.length; i++) {
				zTree.expandNode(nodes[i],true);
			}
		}
	}
	//懒加载，显示内容格式化
	function filter(treeId, parentNode, childNodes) {
		if (!childNodes) return null;
		for (var i=0, l=childNodes.length; i<l; i++) {
			childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
		}
		return childNodes;
	};
	//编辑之前
	function beforeEditName(treeId, treeNode) {
		openLayerDiv(treeNode, "updateLayer");
		var zTree = $.fn.zTree.getZTreeObj("layerTree");
		zTree.selectNode(treeNode);
		return false;
	}
	//删除之前的操作
	function beforeRemove(treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("layerTree");
		zTree.selectNode(treeNode);
		if (confirm("是否删除\"" + treeNode.name + "\"节点？")) {
			var isOK = false;
			//设为同步
			$.ajaxSetup({
				async : false
			});
			//含有节点
			if (treeNode.children && treeNode.children.length > 0) {
				alert("本节点存在下级节点，请删除下级节点再删除本节点。");
			}else {
				//后台删除
				var idArray = new Array();
				idArray.push({"id":treeNode.id});
				$.ajax({ 
			        type:"POST",
			        url:contextPath+"/editPlatform/deleteLayer.html",
			        dataType:"json",
			        data:JSON.stringify(idArray),
			        contentType : 'application/json;charset=utf-8',
			        error:function(data) {
			        	alert("删除失败，请联系管理员");
			        },
			        success:function(data){ 
			        	if(data.result == "success") {
			        		alert("删除成功！");
			        		isOK = true;
			        		//重置
			        		$("#prop_layer_id").val("");
			        		clearPropAllRow();
			        	}else if(data.result == "fail") {
			        		alert(data.data);
			        	}
			        }
				});
			}
			//设为异步
			$.ajaxSetup({
				async : true
			});
			return isOK;//返回true 调用removeHoverDom方法
		}
	}

	//显示删除按钮判断条件
	function showRemoveBtn(treeId, treeNode) {
		var flag = true;
		if(treeNode.level == 0) {//根节点不显示
			flag =false;
		}
		return flag;
	}
	//显示编辑按钮判断条件
	function showRenameBtn(treeId, treeNode) {
		var flag = true;
		if(treeNode.level == 0) {//根节点不显示
			flag =false;
		}
		return flag;
	}
	//添加按钮
	function addHoverDom(treeId, treeNode) {
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
		var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
			+ "' title='add node' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#addBtn_"+treeNode.tId);
		if (btn) btn.bind("click", function(){
			if(treeNode.pId != tree_rootId && treeNode.id != tree_rootId){
				alert("本节点下不能添加子节点");
				return false;
			}
			openLayerDiv(treeNode, "addLayer");
			return false;
		});
	};
	//删除节点
	function removeHoverDom(treeId, treeNode) {
		$("#addBtn_"+treeNode.tId).unbind().remove();
	};
	//点击事件
	function onTreeClick(event, treeId, treeNode, clickFlag) {
		//重置 图层属性的layer_id标识
		$("#prop_layer_id").val("");
		clearPropAllRow();
		if(clickFlag===1 && !treeNode.isParent) {
			$("#prop_layer_id").val(treeNode.id);
			$.ajax({ 
		        type:"POST",
		        url:contextPath+"/editPlatform/queryLayerProps.html",
		        dataType:"json",
		        data:{"layer_id":treeNode.id},
		        error:function(data) {
		        	alert("查询图层属性失败，请联系管理员");
		        },
		        success:function(data){ 
		        	if(data.result == "success") {
		        		clearPropAllRow();//避免网络差，或者点击过快 出现重复数据
		        		var jsondatas = data.data;
		            	for(var i = 0;  i < jsondatas.length; i++) {
		            		var jsondata = jsondatas[i];
		            		addPropNewRow(jsondata.prop_name,jsondata.order_no);
		            	}
		        	}else if(data.result == "fail") {
		        		alert(data.data);
		        	}
		        }
			});
		}
	}		
	
	//显示图层类型弹窗
	function showLayerTree(el){
		var _offset = $(el).data("_offset");
		if(_offset != undefined) {
			$(el)
			.show()
			.offset({ top: _offset.top, left: _offset.left });
		}else {
			var top_h =  ($(window).innerHeight() - 600)/2;
			var left_w = ($(window).innerWidth() - 600)/2;
			$(el).show().offset({ top: top_h, left: left_w});
		}
		clearPropAllRow();
	}
	//打开图层信息窗口 用户增加 和 更新
	function openLayerDiv(treeNode,type) {
		var zTree = $.fn.zTree.getZTreeObj("layerTree");
		zTree.selectNode(treeNode);
		var buttonOffset = $("#" + treeNode.tId + "_span").offset();
		$("#addLayerDiv")
		.slideToggle()
		.offset({top:buttonOffset.top,left:buttonOffset.left});
		curr_treeNode = treeNode;
//		showProType(treeNode,type);
		if(type == "addLayer") {
			curr_treeNode_id = "addBtn_"+treeNode.tId;
			$("#layer_pId").val(treeNode.id);
			$("#layer_id").val("");
			$("#pro_type").val("");
			$("#layer_name").val("");
			$("#order_no").val("");
		}else if(type == "updateLayer") {
			curr_treeNode_span = treeNode.tId + "_edit";
			$("#layer_id").val(treeNode.id);
			$("#layer_pId").val("");
			$("#pro_type").val(treeNode.pro_type);
			$("#layer_name").val(treeNode.name);
			$("#order_no").val(treeNode.order_no);
		}
	}
	
	function showProType(treeNode,type) {
		if(type == "addLayer") {
			if(treeNode.level == 0 && treeNode.pId == null) {
				$("#pro_type_div").css('display','block'); 
			}else {
				$("#pro_type_div").css('display','none'); 
			}
		}else if(type == "updateLayer") {
			if(treeNode.level == 1 && treeNode.pId == tree_rootId) {
				$("#pro_type_div").css('display','block'); 
			}else {
				$("#pro_type_div").css('display','none'); 
			}
		}
		
	}
	
	//清除图层属性所有行，并添加行头
	function clearPropAllRow() {
		var table1 = $("#addprop_table");
		table1.empty();
		var row = $("<tr></tr>");
		var td_1 = $("<th style='text-align: center;width:50%'></th>");
		var td_2 = $("<th style='text-align: center;width:30%'></th>");
	    var td_3 = $("<th style='text-align: center;font-size:21px;vertical-align:middle;width:20%'></th>");
	    td_1.append("属性名称");
	    td_2.append("排序号");
	    td_3.append($("<i class='glyphicon glyphicon-plus' onclick='addPropNewRow(\"\",\"\")'></i>"));
	    row.append(td_1);
	    row.append(td_2);
	    row.append(td_3);
	    table1.append(row);
	}
	//添加图层属性行
	function addPropNewRow(name,orderno) {
	    var table1 = $("#addprop_table");
	    var row = $("<tr></tr>");
	    var td_1 = $("<td style='text-align: center;'></td>");
	    var td_2 = $("<td style='text-align: center;'></td>");
	    var td_3 = $("<td style='text-align: center;font-size:21px;vertical-align:middle;'></td>");
	    
	    td_1.append($("<input type='text' class='form-control' name='addprop_name'  id='prop_name_"+ row_count +"'  value='"+name+"'  placeholder='属性名称'/>"));
	    td_2.append($("<input type='text' class='form-control' name='addprop_orderno'  id='prop_orderno_"+ row_count +"'  value='"+orderno+"'  placeholder='排序号'/>"));
	    td_3.append($("<i class='glyphicon glyphicon-trash'  id='"+ row_count +"' onclick='del("+ row_count +")'></i>"));
	    row.append(td_1);
	    row.append(td_2);
	    row.append(td_3);
	    table1.append(row);
	    row_count++;
	}
	
	//保存图层属性信息
	function saveLayerProp() {
		var layer_id = $("#prop_layer_id").val();
		if(layer_id == "") {
			alert("请先选择要添加属性的节点或本节点不能添加属性！");
			return;
		}
		var prop_names =  $("[name='addprop_name']");
		var addprop_ordernos =  $("[name='addprop_orderno']");
	/*	if(prop_names.length == 0) {
			alert("请至少添加一个属性！");
			return;
		}*/
		var layerProps = new Array();
		for(var i = 0; i < prop_names.length; i++) {
			if($.trim(prop_names[i].value) == "" ) {
				alert("请输入属性名称");
				return;
			}
			if(!isInteger(addprop_ordernos[i].value)) {
				alert("请输入正确的排序号（整数）");
				return;
			}
			var layerProp = {};
			layerProp.prop_name = prop_names[i].value;
			layerProp.order_no = addprop_ordernos[i].value;
			layerProp.layer_id = layer_id;
			layerProps.push(layerProp);
		}
		$("#saveLayerProp").attr("disabled",true); 
		var submitJson = {id:layer_id,layerProps:layerProps};
		$.ajax({ 
	        type:"POST",
	        url:contextPath+"/editPlatform/updateLayerProp.html",
	        dataType:"json",
	        data:JSON.stringify(submitJson),
	        contentType : 'application/json;charset=utf-8',
	        error:function(data) {
	        	alert("保存图层属性信息失败，请联系管理员");
	        	$("#saveLayerProp").attr("disabled",false); 
	        }, 
	        success:function(data){ 
	        	if(data.result == "success") {
	        		alert("保存成功！");
	        		$('#addPropModal').modal('hide');
	        	}else if(data.result == "fail") {
	        		alert(data.data);
	        	}
	        	$("#saveLayerProp").attr("disabled",false); 
	        }
		});
	}
	
	//检查是否为正整数
	function   isInteger(num)
	{
	    var   reg =/^[0-9]*$/;
	    return reg.test(num);
	}
	
	//-----------------------------------------初始化方法
	$(function(){
		//新增和更新图层
		$("#doSaveButton").click(function(){
			var name = $("#layer_name").val();
			var id = $("#layer_id").val();
			var pId = $("#layer_pId").val();
			var pro_type = $("#pro_type").val();
			var order_no = $("#order_no").val();
			if($("#pro_type").is(':visible')){
				if(pro_type == '' || pro_type == undefined) {
					alert("请输入图层标识！");
					return;
				}
			}
			
			if(!isInteger(order_no)) {
				alert("请输入正确的排序号（整数）");
				return;
			}
			if(name != '' && name != undefined) {
				$("#doSaveButton").attr("disabled",true); 
				if(id != "" && id != undefined) {
					//更新库中数据
					$.ajax({ 
				        type:"POST",
				        url:contextPath+"/editPlatform/updateLayer.html",
				        dataType:"json",
				        data:{"name":name,"id":id,"pId":pId,"pro_type":pro_type,"order_no":order_no},
				        error:function(data) {
				        	alert("更新失败，请联系管理员");
				        	$("#doSaveButton").attr("disabled",false); 
				        },
				        success:function(data){ 
				        	if(data.result == "success") {
				        		alert("更新成功！");
				        		var zTree = $.fn.zTree.getZTreeObj("layerTree");
				        		curr_treeNode.name = name;
				        		curr_treeNode.pro_type = pro_type;
				        		curr_treeNode.order_no = order_no;
				        		zTree.updateNode(curr_treeNode);
								$("#addLayerDiv").slideUp();
				        	}else if(data.result == "fail") {
				        		alert(data.data);
				        	}
				        	$("#doSaveButton").attr("disabled",false); 
				        }
					});
				}else {
					//新增
					$.ajax({ 
				        type:"POST",
				        url:contextPath+"/editPlatform/insertLayer.html",
				        dataType:"json",
				        data:{"name":name,"id":id,"pId":pId,"pro_type":pro_type,"order_no":order_no},
				        error:function(data) {
				        	alert("保存失败，请联系管理员");
				        	$("#doSaveButton").attr("disabled",false); 
				        },
				        success:function(data){ 
				           	if(data.result == "success") {
				           		 id = data.data;
								alert("保存成功！");
								var zTree = $.fn.zTree.getZTreeObj("layerTree");
								zTree.addNodes(curr_treeNode, {id:id, pId:pId, name:name,pro_type:pro_type,order_no:order_no});
								$("#addLayerDiv").slideUp();
				        	}else if(data.result == "fail") {
				        		alert(data.data);
				        	}
				           	$("#doSaveButton").attr("disabled",false); 
				        }
					});
				}
			}else {
				alert("必须输入类型名称");
			}
			
		});
		//关闭图层信息div
		$("#closeLayerDiv").click(function(){
			$("#addLayerDiv").slideUp();
		});
		//关闭添加图层框
		$("body").on("click", function(e){
			if(
				$("#addLayerDiv:visible").size() > 0 
				&& $(e.target).attr("id") != curr_treeNode_id
				&& $(e.target).attr("id") != "addLayerDiv" 
				&& ($(e.target).parents("div#addLayerDiv") 
					&& $(e.target).parents("div#addLayerDiv").attr("id") != "addLayerDiv")
				){
				$("#addLayerDiv").slideUp(); 
			}
		});
	});
//------------------------------------------------------------图层树ztree操作----图层树 增加，修改，删除和图层属性的增加，修改，删除--------------------------------------end------------------------

