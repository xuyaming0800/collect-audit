package com.autonavi.audit.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.audit.constant.EditPlatformConstant;
import com.autonavi.audit.dao.EditPlatformDao;
import com.autonavi.audit.entity.Layer;
import com.autonavi.audit.entity.LayerEntity;
import com.autonavi.audit.entity.LayerProp;
import com.autonavi.audit.entity.LayerTree;
import com.autonavi.audit.entity.Overlay;
import com.autonavi.audit.entity.Point;
import com.autonavi.audit.entity.Property;
import com.autonavi.audit.service.EditPlatformService;
import com.autonavi.audit.util.PrimaryByRedis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.implement.MySearchServiceImp;
import com.search.model.LocationEntity;
@Service("editPlatformService")
public class EditPlatformServiceImpl implements EditPlatformService{
	
	@Autowired
	private EditPlatformDao editPlatformDao;
	@Autowired
	private PrimaryByRedis primaryByRedis;
	@Autowired
	private MySearchServiceImp searchServiceImp;
	
	private Logger logger = LogManager.getLogger(getClass());

	@Override
	public List<Overlay> queryAll(String task_id) {
		logger.info("------>查询覆盖物基本信息");
		List<Overlay> oList = editPlatformDao.queryOverlayByTaskId(task_id);
		logger.info(oList);
		if(oList != null && oList.size() > 0) {
			logger.info("------>查询覆盖物坐标和属性信息--start---");
			for(int i = 0; i < oList.size(); i++) {
				Overlay overlay = oList.get(i);
				List<Point> points = editPlatformDao.queryPointByOverlayId(overlay.getId());
				overlay.setPoints(points);
				List<Property> props = editPlatformDao.queryPropertyByOverlayId(overlay.getId());
				overlay.setProps(props);
			}
			logger.info("------>查询覆盖物坐标和属性信息--end---");
		}
		logger.info(oList);
		return oList;
	}
	@Override
	public Overlay queryOverlayInfoById(String id) {
		logger.info("------>根据ID查询覆盖物详细信息");
		Overlay overlay = editPlatformDao.queryOverlayById(id);
		if(overlay != null){
			logger.info("------>查询覆盖物坐标和属性信息---start--");
			List<Point> points = editPlatformDao.queryPointByOverlayId(overlay.getId());
			overlay.setPoints(points);
			List<Property> props = editPlatformDao.queryPropertyByOverlayId(overlay.getId());
			overlay.setProps(props);
			logger.info("------>查询覆盖物坐标和属性信息---end--");
		}
		return overlay;
	}
	@Override
	public Overlay queryOverlayById(String id){
		logger.info("------>根据ID查询覆盖物基本信息");
		return editPlatformDao.queryOverlayById(id);
	}
	@Override
	public List<Overlay>  queryOverlayByLayerIds(List<Map<String, Object>>  layerIdList){
		logger.info("------>根据图层IDs查询覆盖物基本信息");
		List<Overlay> oList = editPlatformDao.queryOverlayByLayerIds(layerIdList);
		logger.info(oList);
		if(oList != null && oList.size() > 0) {
			logger.info("------>查询覆盖物坐标和属性信息--start---");
			for(int i = 0; i < oList.size(); i++) {
				Overlay overlay = oList.get(i);
				List<Point> points = editPlatformDao.queryPointByOverlayId(overlay.getId());
				overlay.setPoints(points);
				List<Property> props = editPlatformDao.queryPropertyByOverlayId(overlay.getId());
				overlay.setProps(props);
			}
			logger.info("------>查询覆盖物坐标和属性信息--end---");
		}
		logger.info(oList);
		return oList;
	}

	@Override
	public String insertOverlay(Overlay overlay) {
		logger.info("------>插入覆盖物基本信息");
//		String id = UUID.randomUUID().toString();
		Long gid = primaryByRedis.generateEcode();
		String id = String.valueOf(gid);
		overlay.setId(id);
		editPlatformDao.insertOverlay(overlay);
		logger.info(id);
		List<Point> points = overlay.getPoints();
		logger.info("------>插入覆盖物坐标信息");
		if(points != null && points.size() > 0) {
			for(int i = 0; i < points.size(); i++) {
//				String idp = UUID.randomUUID().toString();
				Long gidp = primaryByRedis.generateEcode();
				String idp = String.valueOf(gidp);
				Point point = points.get(i);
				point.setId(idp);
				point.setOverlay_id(id);
				point.setOrder_no(i);
//				insertPoint(points.get(i));
			}
			editPlatformDao.insertPoints(points);
		}
		logger.info(points);
		logger.info("------>插入覆盖物属性信息");
		List<Property> props = overlay.getProps();
		if(props != null && props.size() > 0) {
			for(int i = 0; i < props.size(); i++) {
//				String idp = UUID.randomUUID().toString();
				Long gidp = primaryByRedis.generateEcode();
				String idp = String.valueOf(gidp);
				Property property = props.get(i);
				property.setId(idp);
				property.setOverlay_id(id);
//				insertProperty(props.get(i));
			}
			editPlatformDao.insertProps(props);
		}
		logger.info(props);
		//插入到搜索索引中
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------start---");
		insertORupdatePolygon(overlay.getId(), overlay.getType(),overlay.getTask_id());
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------end---");
		return id;
	}

	@Override
	public void updateOverlay(Overlay overlay) {
		logger.info("------>更新覆盖物基本信息");
		editPlatformDao.updateOverlay(overlay);
		//不更新坐标信息
//		deletePoint(overlay.getId());
//		List<Point> points = overlay.getPoints();
//		if(points != null && points.size() > 0) {
//			for(int i = 0; i < points.size(); i++) {
//				Point point = points.get(i);
//				point.setOrder_no(i);
//				point.setOverlay_id(overlay.getId());
//				insertPoint(point);
//			}
//		}
		logger.info("------>删除覆盖物原属性信息，覆盖物ID:"+overlay.getId());
		deleteProperty(overlay.getId());
		logger.info("------>插入覆盖物新属性信息，覆盖物ID:"+overlay.getId());
		List<Property> props = overlay.getProps();
		if(props != null && props.size() > 0) {
			for(int i = 0; i < props.size(); i++) {
//				String idp = UUID.randomUUID().toString();
				Long gidp = primaryByRedis.generateEcode();
				String idp = String.valueOf(gidp);
				Property property = props.get(i);
				property.setId(idp);
				property.setOverlay_id(overlay.getId());
//				insertProperty(props.get(i));
			}
			editPlatformDao.insertProps(props);
		}
		logger.info(props);
		//插入到搜索索引中
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------start---");
		insertORupdatePolygon(overlay.getId(), overlay.getType(),overlay.getTask_id());
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------end---");
	}

	@Override
	public void deleteOverlay(String id) {
		logger.info("------>删除覆盖物基本信息，覆盖物ID:"+id);
		editPlatformDao.deleteOverlay(id);
		logger.info("------>删除覆盖物坐标信息，覆盖物ID:"+id);
		editPlatformDao.deletePoint(id);
		logger.info("------>删除覆盖物属性信息，覆盖物ID:"+id);
		editPlatformDao.deleteProperty(id);
		//删除搜索索引库中数据
		logger.info("------>从索引库中删除坐标信息，ID："+id+"---------start---");
		deletePolygon(id);
		logger.info("------>从索引库中删除坐标信息，ID："+id+"---------end---");
	}

	@Override
	public List<Point> queryPointByOverlayId(String overlay_id) {
		logger.info("------>查询坐标信息，覆盖物ID:"+overlay_id);
		return editPlatformDao.queryPointByOverlayId(overlay_id);
	}

	@Override
	public String insertPoint(Point point) {
		logger.info("------>新增坐标信息");
//		String id = UUID.randomUUID().toString();
		Long gid = primaryByRedis.generateEcode();
		String id = String.valueOf(gid);
		point.setId(id);
		editPlatformDao.insertPoint(point);
		logger.info(id);
		return id;
	}

	@Override
	public void updatePoint(List<Point> points) {
		logger.info("------>更新坐标信息");
		Overlay overlay = new Overlay();
		for(int i = 0; i < points.size(); i++) {
//			String id = UUID.randomUUID().toString();
			Long gid = primaryByRedis.generateEcode();
			String id = String.valueOf(gid);
			Point point = points.get(i);
			if(i == 0) {
				deletePoint(point.getOverlay_id());
				overlay = queryOverlayById(point.getOverlay_id());
			}
			point.setId(id);
			point.setOrder_no(i);
//			insertPoint(point);
		}
		editPlatformDao.insertPoints(points);
		logger.info(points);
		//插入到搜索索引中
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------start---");
		insertORupdatePolygon(overlay.getId(), overlay.getType(),overlay.getTask_id());
		logger.info("------>插入或者更新坐标信息到搜索索引库中，ID："+overlay.getId()+"，类型type："+overlay.getType()+"---------end---");
	}

	@Override
	public void deletePoint(String overlay_id) {
		logger.info("------>删除坐标信息，覆盖物ID:"+overlay_id);
		editPlatformDao.deletePoint(overlay_id);
	}

	@Override
	public List<Property> queryPropertyByOverlayId(String overlay_id) {
		logger.info("------>查询属性信息，覆盖物ID:"+overlay_id);
		return editPlatformDao.queryPropertyByOverlayId(overlay_id);
	}

	@Override
	public String insertProperty(Property property) {
		logger.info("------>新增属性信息");
//		String id = UUID.randomUUID().toString();
		Long gid = primaryByRedis.generateEcode();
		String id = String.valueOf(gid);
		property.setId(id);
		editPlatformDao.insertProperty(property);
		logger.info(id);
		return id;
	}

	@Override
	public void updateProperty(List<Property> props) {
		logger.info("------>更新属性信息");
		if(props != null && props.size() > 0) {
			for(int i = 0; i < props.size(); i++) {
//				String id = UUID.randomUUID().toString();
				Long gid = primaryByRedis.generateEcode();
				String id = String.valueOf(gid);
				Property property = props.get(i);
				if(i == 0) {
					deleteProperty(property.getOverlay_id());
				}
				property.setId(id);
//				insertProperty(props.get(i));
			}
			editPlatformDao.insertProps(props);
			logger.info(props);
		}
	}

	@Override
	public void deleteProperty(String overlay_id) {
		logger.info("------>删除属性信息，覆盖物ID:"+overlay_id);
		editPlatformDao.deleteProperty(overlay_id);
	}

	@Override
	public List<Layer> queryLayerByProType(String pro_type) {
		logger.info("------>查询图层信息，项目类型type:"+pro_type);
		return editPlatformDao.queryLayerByProType(pro_type);
	}

	@Override
	public String insertLayer(Layer layer) {
		logger.info("------>新增图层信息");
//		String id = UUID.randomUUID().toString();
		Long gid = primaryByRedis.generateEcode();
		String id = String.valueOf(gid);
		layer.setId(id);
		editPlatformDao.insertLayer(layer);
		logger.info(id);
		return id;
	}
	
	@Override
	public void insertLayer(LayerEntity layerEntity) {
		if(layerEntity == null) {
			logger.info("------>无图层信息");
			return;
		}
		String pro_type = layerEntity.getPro_type();
		if(pro_type==null||"".equals(pro_type)) {
			logger.info("------>无图层类型");
			return;
		}
		List<Layer> layerList = editPlatformDao.queryLayerCountByProType(pro_type,EditPlatformConstant.treeRootId);
		String pId = "";
		if(layerList.size() > 0) {
			pId = layerList.get(0).getId();
			logger.info("------>存在父类图层信息，id:"+pId);
		}else {
			logger.info("------>新增父类图层信息");
			Layer layer = new Layer();
			layer.setName(layerEntity.getName());
			layer.setPro_type(layerEntity.getPro_type());
			Long id = primaryByRedis.generateEcode();
			layer.setId(String.valueOf(id));
			layer.setpId(EditPlatformConstant.treeRootId);
			pId = String.valueOf(id);
			editPlatformDao.insertLayer(layer);
			logger.info(id);
		}
		List<LayerEntity> layerEntityList = layerEntity.getList();
		if(layerEntityList != null && layerEntityList.size() > 0) {
			for(LayerEntity layerEntityTmp:layerEntityList) {
				if(isExist(layerEntityTmp.getPro_type(),pId)) {
					logger.info("------>新增子类图层信息");
					if(layerEntityTmp.getPro_type()!=null&&!"".equals(layerEntityTmp.getPro_type())&&
							layerEntityTmp.getName()!=null&&!"".equals(layerEntityTmp.getName())){
						Layer layer = new Layer();
						layer.setName(layerEntityTmp.getName());
						layer.setPro_type(layerEntityTmp.getPro_type());
						Long id = primaryByRedis.generateEcode();
						layer.setId(String.valueOf(id));
						layer.setpId(pId);
						editPlatformDao.insertLayer(layer);
						logger.info(id);
					}
				}else {
					logger.info("------>子类图层信息名称："+ layerEntityTmp.getName()+"  已存在！");
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @author wenpeng.jin
	 * @date 2015年8月21日
	 * @description 图层是否存在
	 * @param pro_type
	 * @param id 
	 * @return true:不存在；false：存在
	 */
	public boolean isExist(String pro_type, String pId) {
		List<Layer> layers = editPlatformDao.queryLayerCountByProType(pro_type,pId);
		boolean flag = true;
		if(layers.size() > 0) {
			flag = false;
		}
		return flag;
	}

	@Override
	public void updateLayer(Layer layer) {
		logger.info("------>更新图层信息");
		editPlatformDao.updateLayer(layer);
	}

	@Override
	public String deleteLayer(List<Map<String, Object>> idList) {
		String result = checkLayer(idList);
		if("".equals(result)) {
			logger.info("------>删除图层信息");
			editPlatformDao.deleteLayer(idList);
			logger.info("------>删除图层属性信息");
			for(int i = 0; i < idList.size(); i++) {
				Map<String, Object> map = idList.get(i);
				deleteLayerProp((String)map.get("id"));
			}
		}
		return result;
	}
	//核查当前图层下 是否还有下级节点或则含有覆盖物标识
	public String checkLayer(List<Map<String, Object>> idList) {
		String result = "";
		for(int i = 0; i < idList.size(); i++) {
			Map<String, Object> map = idList.get(i);
			List<Object> list = editPlatformDao.queryLayerTree((String)map.get("id"));
			if(list != null && list.size() > 0) {
				logger.info("------>删除图层信息时：图层ID:"+map.get("id")+",下含有下级节点");
				result = "节点存在下级节点，请删除下级节点再删除本节点。";
				break;
			}else {
				 List<Overlay> olist = editPlatformDao.queryOverlayByLayerId((String)map.get("id"));
				 if(olist != null && olist.size() > 0) {
					 logger.info("------>删除图层信息时：图层ID:"+map.get("id")+",下含有覆盖物标识");
					 result = "节点含有覆盖物标识，请删除节点下覆盖物标识再删除本节点。";
						break;
				 }
			}
		}
		return result;
		
	}
	
	@Override
	public List<Object> queryLayerTree(String id, String name, String level) {
		logger.info("------>查询图层树信息service");
		List<Object> list = new ArrayList<Object>();
		if(id == null || "".equals(id)) {
			logger.info("------>查询图层树信息service-查询根节点  固定拼装信息");
			LayerTree lt = new LayerTree();
			lt.setId(EditPlatformConstant.treeRootId);
			lt.setName("图层信息");
			lt.setIsParent(true);
			lt.setOpen(true);
			list.add(lt);
			
		}else {
			logger.info("------>查询图层树信息service-查询非根节点");
			list = editPlatformDao.queryLayerTree(id);
			for(int i = 0; i < list.size(); i++) {
				LayerTree lt = (LayerTree)list.get(i);
				//第一层 一定包含子节点 所以设置为true
				if(EditPlatformConstant.treeRootId.equals(lt.getpId())) {
					lt.setIsParent(true);
				}
			}
		}
		return list;
	}
	
	public void updateLayerProp(Layer layer){
		logger.info("------>更新图层属性信息");
		deleteLayerProp(layer.getId());
		List<LayerProp> layerProps =layer.getLayerProps();
		if(layerProps != null && layerProps.size() > 0) {
		/*	for(int i = 0; i < layerProps.size(); i++) {
//				String id = UUID.randomUUID().toString();
				Long gid = primaryByRedis.generateEcode();
				String id = String.valueOf(gid);
				LayerProp layerProp = layerProps.get(i);
				if(i == 0) {
					deleteLayerProp(layerProp.getLayer_id());
				}
			}*/
			editPlatformDao.insertLayerProps(layerProps);
			logger.info(layerProps);
		}
	}
	
	public void deleteLayerProp(String layer_id) {
		logger.info("------>删除图层属性信息");
		editPlatformDao.deleteLayerProp(layer_id);
	}

	public List<LayerProp> queryLayerProps(String  layer_id){
		logger.info("------>查询图层属性信息，图层ID:"+layer_id);
		return editPlatformDao.queryLayerProps(layer_id);
	}
	/**
	 * @author jinwenpeng
	 * @date 2015年8月4日
	 * @description 插入搜索索引中
	 */
	public  void insertORupdatePolygon(String overlay_id,String type,String task_id) {
		logger.info("开始批量更新索引，overlay_id："+overlay_id+",type："+type+",task_id："+task_id);
		List<Point> points = queryPointByOverlayId(overlay_id);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_id", overlay_id);
		map.put("task_id", task_id);
		LocationEntity locationEntity = new LocationEntity();
		if(EditPlatformConstant.marker.equals(type)) {
			type = EditPlatformConstant.point;
		}
		locationEntity.setType(type);
		locationEntity.setCoordinates(makeArray(points, type));
		map.put("location", locationEntity);
		list.add(map);
		try {
			searchServiceImp.bulkUpdate(EditPlatformConstant.index_name, EditPlatformConstant.index_type, list);
			logger.info("完成批量更新索引，overlay_id："+overlay_id+",type："+type+",task_id："+task_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("批量更新索引库异常，list："+list.toString(),e);
		}
	
	}
	
//	PropertiesConfig.getProperty(PropConstants.AUDIT_LIST_SIZE);
	/**
	 * @author jinwenpeng
	 * @date 2015年8月4日
	 * @description 删除搜索索引
	 */
	public  void deletePolygon(String overlay_id) {
		logger.info("开始删除索引，overlay_id："+overlay_id);
		List<String> list = new ArrayList<String>();
		// 删除时候传入的值为主键id
		list.add(overlay_id);
		try {
			searchServiceImp.bulkDelete(EditPlatformConstant.index_name, EditPlatformConstant.index_type, list);
			logger.info("完成删除索引，overlay_id："+overlay_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除索引库异常，overlay_id："+overlay_id+",list："+list.toString(),e);
		}
		
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月5日
	 * @description 转化成数组
	 * @param points
	 * @param type
	 * @return
	 */
	public Object makeArray(List<Point> points,String type) {
		logger.info("开始转化数组对象，points的个数："+points.size()+",type"+type);
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = "";
		Object obj = new Object(); ;
		try {
			if(EditPlatformConstant.point.equals(type)) {
				jsonStr = makeJsonStrForPoint(points);
				obj =  mapper
						.readValue(
								jsonStr,
								double[].class);
				
			}else if(EditPlatformConstant.polygon.equals(type)) {
				jsonStr = makeJsonStrForPolygon(points);
				obj =  mapper
						.readValue(
								jsonStr,
								double[][][].class);
			}
			logger.info("完成转化数组对象，points的个数："+points.size()+",type"+type);
		} catch (IOException e) {
			logger.error("JSON字符串转化成数组异常,type："+type+"，jsonStr："+jsonStr,e);
			e.printStackTrace();
		}
		return obj;
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月4日
	 * @description 组装json字符串
	 * @param points
	 * @return
	 */
	public String makeJsonStrForPolygon(List<Point> points) {
		logger.info("开始组装----多边形------json字符串");
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("[");
		for(int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			sb.append("[");
			sb.append(point.getLng());
			sb.append(",");
			sb.append(point.getLat());
			sb.append("]");
			if(i < points.size() -1) {
				sb.append(",");
			}
			if(i == points.size() -1) {
					sb.append(",");
					Point pointFirst = points.get(0);
					sb.append("[");
					sb.append(pointFirst.getLng());
					sb.append(",");
					sb.append(pointFirst.getLat());
					sb.append("]");
			}
		}
		sb.append("]");
		sb.append("]");
		logger.info("完成组装----多边形------json字符串,jsonStr："+ sb.toString());
		return sb.toString();
	}
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月4日
	 * @description 组装json字符串
	 * @param points
	 * @return
	 */
	public String makeJsonStrForPoint(List<Point> points) {
		logger.info("开始组装----点------json字符串");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			sb.append("[");
			sb.append(point.getLng());
			sb.append(",");
			sb.append(point.getLat());
			sb.append("]");
			if(i < points.size() -1) {
				sb.append(",");
			}
		}
		logger.info("完成组装----点------json字符串,jsonStr："+ sb.toString());
		return sb.toString();
	}
	@Override
	public List<Layer> queryOneLayerByProType(String pro_type,String pProTypeName, String rootId) {
		List<Layer> type = editPlatformDao.queryLayerCountByProType(pProTypeName,rootId);
		if(type!=null&&type.size()>0)
			return editPlatformDao.queryLayerCountByProType(pro_type,type.get(0).getId());
		return null;
	}

}   

