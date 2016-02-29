package com.autonavi.audit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.Layer;
import com.autonavi.audit.entity.LayerProp;
import com.autonavi.audit.entity.Overlay;
import com.autonavi.audit.entity.Point;
import com.autonavi.audit.entity.Property;
import com.autonavi.audit.entity.RespResult;
import com.autonavi.audit.service.EditPlatformService;
/**
 * 
 * @author jinwenpeng
 * @date 2015年7月21日
 * @description 编辑平台controller
 *
 */
@Controller
@RequestMapping("/editPlatform")
public class EditPlatformController {
	
	@Autowired
	private EditPlatformService editPlatformService;
	private Logger logger = LogManager.getLogger(getClass());
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 获取所有的覆盖物
	 * @param task_id
	 * @return
	 */
	@RequestMapping("/queryAll")
	public @ResponseBody RespResult queryAll(@RequestParam("task_id") String task_id) {
		logger.trace("进入queryAll方法------>查询start");
		logger.entry("任务id:"+task_id);
		RespResult rr = new RespResult();
		try{
			List<Overlay> list =  editPlatformService.queryAll(task_id);
			rr.setResult("success");
			rr.setData(list);
		}catch(Exception e){
			logger.error("queryAll 查询异常",e);
			rr.setResult("fail");
			rr.setData("查询失败，请联系管理员！");
		}
		logger.trace("进入queryAll方法------>查询end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月5日
	 * @description 根据ID查询覆盖物详细信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryOverlayInfoById")
	public @ResponseBody RespResult queryOverlayInfoById(@RequestParam("id") String id) {
		logger.trace("进入queryOverlayInfoById方法------>根据ID查询覆盖物详细信息查询start");
		logger.entry("覆盖物id:"+id);
		RespResult rr = new RespResult();
		try{
			Overlay overlay =  editPlatformService.queryOverlayInfoById(id);
			rr.setResult("success");
			rr.setData(overlay);
		}catch(Exception e){
			logger.error("queryOverlayInfoById 根据ID查询覆盖物详细信息查询异常",e);
			rr.setResult("fail");
			rr.setData("根据ID查询覆盖物详细信息查询失败，请联系管理员！");
		}
		logger.trace("进入queryOverlayInfoById方法------>根据ID查询覆盖物详细信息查询end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月5日
	 * @description 根据图层IDs查询覆盖物详细信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryOverlayByLayerIds")
	public  @ResponseBody RespResult  queryOverlayByLayerIds(@RequestBody List<Map<String, Object>>  layerIdList){
		logger.trace("进入queryOverlayByLayerIds方法------>根据图层IDs查询覆盖物详细信息start");
		logger.entry("图层IDS:"+layerIdList);
		RespResult rr = new RespResult();
		try{
			if(layerIdList.size() > 0) {
				List<Overlay> list =  editPlatformService.queryOverlayByLayerIds(layerIdList);
				rr.setData(list);
			}
			rr.setResult("success");
			
		}catch(Exception e){
			logger.error("queryOverlayByLayerIds 根据图层IDs查询覆盖物详细信息异常",e);
			rr.setResult("fail");
			rr.setData("查询失败，请联系管理员！");
		}
		logger.trace("进入queryOverlayByLayerIds方法------>根据图层IDs查询覆盖物详细信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物信息
	 * @param overlay
	 * @return
	 */
	@RequestMapping("/insertOverlay")
	public @ResponseBody RespResult insertOverlay(@RequestBody Overlay overlay) {
		logger.trace("进入insertOverlay方法------>插入覆盖物信息start");
		RespResult rr = new RespResult();
		try{
			String id = editPlatformService.insertOverlay(overlay);
			rr.setResult("success");
			rr.setData(id);
		}catch(Exception e){
			logger.error("insertOverlay插入覆盖物信息",e);
			rr.setResult("fail");
			rr.setData("插入异常，请联系管理员");
		}
		logger.trace("进入insertOverlay方法------>插入覆盖物信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物信息
	 * @param overlay
	 * @return
	 */
	@RequestMapping("/updateOverlay")
	public  @ResponseBody RespResult  updateOverlay(@RequestBody  Overlay overlay) {
		logger.trace("进入updateOverlay方法------>更新覆盖物信息start");
		RespResult rr = new RespResult();
		try{
			editPlatformService.updateOverlay(overlay);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("updateOverlay更新异常异常",e);
			rr.setResult("fail");
			rr.setData("更新异常，请联系管理员");
		}
		logger.trace("进入updateOverlay方法------>更新覆盖物信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除覆盖物信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteOverlay")
	public  @ResponseBody RespResult  deleteOverlay(@RequestParam("id") String id) {
		logger.trace("进入deleteOverlay方法------>删除覆盖物信息start");
		logger.entry("任务id:"+id);
		RespResult rr = new RespResult();
		try{
			editPlatformService.deleteOverlay(id);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("deleteOverlay删除异常",e);
			rr.setResult("fail");
			rr.setData("删除异常，请联系管理员");
		}
		logger.trace("进入deleteOverlay方法------>删除覆盖物信息end");
		return rr;
	}
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID查询覆盖物坐标信息 暂未用
	 * @param overlay_id
	 * @return
	 */
	@RequestMapping("/queryPointByOverlayId")
	public @ResponseBody RespResult queryPointByOverlayId(@RequestParam("overlay_id") String  overlay_id){
		logger.trace("进入queryPointByOverlayId方法------>根据覆盖物ID查询覆盖物坐标信息start");
		logger.entry("覆盖物id:"+overlay_id);
		RespResult rr = new RespResult();
		try{
			List<Point> points = editPlatformService.queryPointByOverlayId(overlay_id);
			rr.setResult("success");
			rr.setData(points);
		}catch(Exception e){
			logger.error("queryPointByOverlayId据覆盖物ID查询覆盖物坐标信息异常",e);
			rr.setResult("fail");
			rr.setData("查询覆盖物坐标信息异常，请联系管理员");
		}
		logger.trace("进入queryPointByOverlayId方法------>根据覆盖物ID查询覆盖物坐标信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物坐标信息 暂未用
	 * @param point
	 * @return
	 */
	@RequestMapping("/insertPoint")
	public @ResponseBody RespResult insertPoint(@RequestBody Point point){
		logger.trace("进入insertPoint方法------>插入覆盖物坐标信息start");
		RespResult rr = new RespResult();
		try{
			String id = editPlatformService.insertPoint(point);
			rr.setResult("success");
			rr.setData(id);
		}catch(Exception e){
			logger.error("insertPoint 插入覆盖物坐标信息异常",e);
			rr.setResult("fail");
			rr.setData("插入覆盖物坐标信息异常异常，请联系管理员");
		}
		logger.trace("进入insertPoint方法------>插入覆盖物坐标信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物坐标信息
	 * @param points
	 * @return
	 */
	@RequestMapping("/updatePoint")
	public @ResponseBody RespResult updatePoint(@RequestBody List<Point> points) {
		logger.trace("进入updatePoint方法------>更新覆盖物坐标信息start");
		RespResult rr = new RespResult();
		try{
			editPlatformService.updatePoint(points);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("updatePoint更新覆盖物坐标信息异常",e);
			rr.setResult("fail");
			rr.setData("更新覆盖物坐标信息异常，请联系管理员");
		}
		logger.trace("进入updatePoint方法------>更新覆盖物坐标信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除覆盖物坐标信息 暂未用
	 * @param overlay_id
	 * @return
	 */
	@RequestMapping("/deletePoint")
	public  @ResponseBody RespResult  deletePoint(@RequestParam("overlay_id") String overlay_id) {
		logger.trace("进入deletePoint方法------>删除覆盖物坐标信息start");
		logger.entry("覆盖物id:"+overlay_id);
		RespResult rr = new RespResult();
		try{
			editPlatformService.deletePoint(overlay_id);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("deletePoint删除覆盖物坐标信息异常",e);
			rr.setResult("fail");
			rr.setData("删除覆盖物坐标信息异常，请联系管理员");
		}
		logger.trace("进入deletePoint方法------>删除覆盖物坐标信息end");
		return rr;
	}
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID查询覆盖物属性信息 暂未用
	 * @param overlay_id
	 * @return
	 */
	@RequestMapping("/queryPropertyByOverlayId")
	public @ResponseBody RespResult queryPropertyByOverlayId(@RequestParam("overlay_id") String  overlay_id){
		logger.trace("进入queryPropertyByOverlayId方法------>根据覆盖物ID查询覆盖物属性信息start");
		logger.entry("覆盖物id:"+overlay_id);
		RespResult rr = new RespResult();
		try{
			List<Property> props = editPlatformService.queryPropertyByOverlayId(overlay_id);
			rr.setResult("success");
			rr.setData(props);
		}catch(Exception e){
			logger.error("queryPropertyByOverlayId 根据覆盖物ID查询覆盖物属性信息异常",e);
			rr.setResult("fail");
			rr.setData("根据覆盖物ID查询覆盖物属性信息异常，请联系管理员");
		}
		logger.trace("进入queryPropertyByOverlayId方法------>根据覆盖物ID查询覆盖物属性信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物属性信息 暂未用
	 * @param property
	 * @return
	 */
	@RequestMapping("/insertProperty")
	public @ResponseBody RespResult insertProperty(@RequestBody Property property){
		logger.trace("进入insertProperty方法------>插入覆盖物属性信息start");
		RespResult rr = new RespResult();
		try{
			String id = editPlatformService.insertProperty(property);
			rr.setResult("success");
			rr.setData(id);
		}catch(Exception e){
			logger.error("insertProperty插入覆盖物属性信息异常",e);
			rr.setResult("fail");
			rr.setData("插入覆盖物属性信息异常，请联系管理员");
		}
		logger.trace("进入insertProperty方法------>插入覆盖物属性信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物属性信息 暂未用
	 * @param props
	 * @return
	 */
	public @ResponseBody RespResult  updateProperty(@RequestBody List<Property> props){
		logger.trace("进入updateProperty方法------>更新覆盖物属性信息start");
		RespResult rr = new RespResult();
		try{
			editPlatformService.updateProperty(props);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("updateProperty更新覆盖物属性信息异常",e);
			rr.setResult("fail");
			rr.setData("更新覆盖物属性信息异常，请联系管理员");
		}
		logger.trace("进入updateProperty方法------>更新覆盖物属性信息end");
		return rr;
	}
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除覆盖物属性信息 暂未用
	 * @param overlay_id
	 * @return
	 */
	@RequestMapping("/deleteProperty")
	public  @ResponseBody RespResult  deleteProperty(@RequestParam("overlay_id") String overlay_id){
		logger.trace("进入deleteProperty方法------>删除覆盖物属性信息start");
		logger.entry("覆盖物id:"+overlay_id);
		RespResult rr = new RespResult();
		try{
			editPlatformService.deleteProperty(overlay_id);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("deleteProperty删除覆盖物属性信息异常",e);
			rr.setResult("fail");
			rr.setData("删除覆盖物属性信息异常，请联系管理员");
		}
		logger.trace("进入deleteProperty方法------>删除覆盖物属性信息end");
		return rr;
	}
	
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据任务类型（项目类型）查询图层信息
	 * @param task_id
	 * @return
	 */
	@RequestMapping("/queryLayerByProType")
	public @ResponseBody RespResult queryLayerByProType(@RequestParam("pro_type") String  pro_type){
		logger.trace("进入queryLayerByProType方法------>根据项目类型查询图层信息start");
		logger.entry("项目类型type:"+pro_type);
		RespResult rr = new RespResult();
		try{
			List<Layer> layers = editPlatformService.queryLayerByProType(pro_type);
			rr.setResult("success");
			rr.setData(layers);
		}catch(Exception e){
			logger.error("queryLayerByProType根据项目类型查询图层信息异常",e);
			rr.setResult("fail");
			rr.setData("根据项目类型查询图层信息异常，请联系管理员");
		}
		logger.trace("进入queryLayerByProType方法------>根据项目类型查询图层信息end");
		return rr;
	}
	
	/**
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入图层类型信息
	 * @param layer
	 * @return
	 */
	@RequestMapping("/insertLayer")
	public @ResponseBody RespResult insertLayer(Layer layer){
		logger.trace("进入insertLayer方法------>插入图层类型信息start");
		RespResult rr = new RespResult();
		try{
			String id = editPlatformService.insertLayer(layer);
			rr.setResult("success");
			rr.setData(id);
		}catch(Exception e){
			logger.error("insertLayer插入图层类型信息异常",e);
			rr.setResult("fail");
			rr.setData("插入图层类型信息异常，请联系管理员");
		}
		logger.trace("进入insertLayer方法------>插入图层类型信息end");
		return rr;
	}
	/**
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新图层类型信息
	 * @param layer
	 * @return
	 */
	@RequestMapping("/updateLayer")
	public @ResponseBody RespResult updateLayer(Layer layer){
		logger.trace("进入updateLayer方法------>更新图层类型信息start");
		RespResult rr = new RespResult();
		try{
			editPlatformService.updateLayer(layer);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("updateLayer更新图层类型信息异常",e);
			rr.setResult("fail");
			rr.setData("更新图层类型信息异常，请联系管理员");
		}
		logger.trace("进入updateLayer方法------>更新图层类型信息end");
		return rr;
	}
	/**
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除图层类型信息
	 * @param idList
	 * @return
	 */
	@RequestMapping("/deleteLayer")
	public  @ResponseBody RespResult  deleteLayer(@RequestBody List<Map<String, Object>> idList){
		logger.trace("进入deleteLayer方法------>删除图层类型信息start");
		logger.entry("图层id:"+idList.toString());
		RespResult rr = new RespResult();
		try{
			String result = editPlatformService.deleteLayer(idList);
			if("".equals(result)) {
				rr.setResult("success");
			}else {
				rr.setResult("fail");
				rr.setData(result);
			}
			
		}catch(Exception e){
			logger.error("deleteLayer删除图层类型信息异常",e);
			rr.setResult("fail");
			rr.setData("删除图层类型信息异常，请联系管理员");
		}
		logger.trace("进入deleteLayer方法------>删除图层类型信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月3日
	 * @description 查询图层树
	 * @param id
	 * @param name
	 * @param level
	 * @return
	 */
	@RequestMapping("/queryLayerTree")
	public @ResponseBody List<Object> queryLayerTree(@RequestParam(value="id", required=false) String id, @RequestParam(value="name", required=false) String name, @RequestParam(value="level", required=false) String level) {
		logger.trace("进入queryLayerTree方法------>查询图层树信息start");
		logger.trace("进入queryLayerTree方法------>id："+id+",名称："+name+",深度："+level);
		List<Object>  list = new ArrayList<Object>();
		try{
			list = editPlatformService.queryLayerTree(id, name, level);
		}catch(Exception e){
			logger.error("queryLayerTree查询图层树信息异常",e);
		}
		
		logger.trace("进入queryLayerTree方法------>查询图层树信息end");
		return list;
	}
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 更新图层属性信息
	 * @param idList
	 * @return
	 */
	@RequestMapping("/updateLayerProp")
	public  @ResponseBody RespResult  updateLayerProp(@RequestBody Layer layer){
		logger.trace("进入updateLayerProp方法------>更新图层属性信息start");
		logger.entry("图层id:"+layer.toString());
		RespResult rr = new RespResult();
		try{
			editPlatformService.updateLayerProp(layer);
			rr.setResult("success");
		}catch(Exception e){
			logger.error("updateLayerProp更新图层属性信息异常",e);
			rr.setResult("fail");
			rr.setData("更新图层属性信息异常，请联系管理员");
		}
		logger.trace("进入updateLayerProp方法------>更新图层属性信息end");
		return rr;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 根据图层ID查询图层属性信息
	 * @param layer_id
	 * @param task_id
	 * @return
	 */
	@RequestMapping("/queryLayerProps")
	public @ResponseBody RespResult queryLayerProps(@RequestParam("layer_id") String  layer_id){
		logger.trace("进入queryLayerProps方法------>根据图层ID查询图层属性信息start");
		logger.entry("图层id:"+layer_id);
		RespResult rr = new RespResult();
		try{
			List<LayerProp> layerProps = editPlatformService.queryLayerProps(layer_id);
			rr.setResult("success");
			rr.setData(layerProps);
		}catch(Exception e){
			logger.error("queryLayerProps 根据图层ID查询图层属性信息异常",e);
			rr.setResult("fail");
			rr.setData("根据图层ID查询图层属性信息异常，请联系管理员");
		}
		logger.trace("进入queryLayerProps方法------>根据图层ID查询图层属性信息end");
		return rr;
	}
	
	/**
	 * @Description: 查询指定pro_type的对象
	 * @author 刘旭升
	 * @date 2015年8月25日 下午6:50:51 
	 * @version V1.0 
	 * @param pro_type
	 * @return
	 */
	@RequestMapping("/queryOneLayerByProType")
	public @ResponseBody RespResult queryOneLayerByProType(
			@RequestParam("pro_type") String  pro_type,
			@RequestParam("rootId") String  rootId,
			@RequestParam("pProTypeName") String  pProTypeName){
		logger.entry("项目类型type:"+pro_type);
		RespResult rr = new RespResult();
		try{
			List<Layer> layers = editPlatformService.queryOneLayerByProType(pro_type,pProTypeName,rootId);
			rr.setResult("success");
			if(layers!=null&&layers.size()>0)
				rr.setData(layers.get(0));
		}catch(Exception e){
			logger.error("queryOneLayerByProType根据项目类型查询图层信息异常",e);
			rr.setResult("fail");
			rr.setData("根据项目类型查询图层信息异常，请联系管理员");
		}
		logger.trace("进入queryOneLayerByProType方法------>根据项目类型查询图层信息end");
		return rr;
	}

}
