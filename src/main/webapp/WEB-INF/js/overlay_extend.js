// 扩展覆盖物Marker属性 增加id和图层ID属性
function Marker_e(point,id,layerId){
	BMap.Marker.call(this, point); 
	this.id = id;
	this.layerId = layerId;
}
//继承API的BMap.Marker
Marker_e.prototype = new BMap.Marker();

//扩展覆盖物Polygon属性增加id和图层ID属性
function Polygon_e(points,opts,id,layerId){
	BMap.Polygon.call(this, points,opts); 
	this.id = id;
	this.layerId= layerId;
}
//继承API的BMap.Polygon
Polygon_e.prototype = new BMap.Polygon();