package com.autonavi.audit.service;

import java.util.List;
import java.util.Map;

import com.autonavi.audit.entity.Layer;
import com.autonavi.audit.entity.LayerEntity;
import com.autonavi.audit.entity.LayerProp;
import com.autonavi.audit.entity.Overlay;
import com.autonavi.audit.entity.Point;
import com.autonavi.audit.entity.Property;



public interface EditPlatformService {
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据任务ID查询所有的覆盖物信息
	 * @param task_id
	 * @return
	 */
	public List<Overlay> queryAll(String task_id) ;
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月5日
	 * @description 根据Id查询覆盖物详细信息（坐标和属性）
	 * @param id
	 * @return
	 */
	public Overlay queryOverlayInfoById(String id);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据Id查询覆盖物
	 * @param id
	 * @return
	 */
	public Overlay queryOverlayById(String id);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月5日
	 * @description 根据图层IDs查询覆盖物详细信息
	 * @param layerIdList
	 * @return
	 */
	public List<Overlay>  queryOverlayByLayerIds(List<Map<String, Object>>  layerIdList);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物基本信息
	 * @param overlay
	 * @return
	 */
	public String insertOverlay(Overlay overlay);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物基本信息
	 * @param overlay
	 */
	public void updateOverlay(Overlay overlay);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除覆盖物所有信息
	 * @param id
	 */
	public void deleteOverlay(String id);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID查询覆盖物坐标信息
	 * @param overlay_id
	 * @return
	 */
	public List<Point> queryPointByOverlayId(String  overlay_id);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物坐标信息  单记录插入 暂未用
	 * @param point
	 * @return
	 */
	public String insertPoint(Point point);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物坐标信息 
	 * @param points
	 */
	public void  updatePoint(List<Point> points);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID删除覆盖物坐标信息
	 * @param overlay_id
	 */
	public void deletePoint(String overlay_id);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID查询覆盖物属性信息
	 * @param overlay_id
	 * @return
	 */
	public List<Property> queryPropertyByOverlayId(String  overlay_id);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入覆盖物属性信息 单记录插入 暂未用
	 * @param property
	 * @return
	 */
	public String insertProperty(Property property);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新覆盖物属性信息 暂未用
	 * @param props
	 */
	public void  updateProperty(List<Property> props);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据覆盖物ID删除覆盖物属性信息
	 * @param overlay_id
	 */
	public void deleteProperty(String overlay_id);
	
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 根据任务ID查询图层信息
	 * @param task_id
	 * @return
	 */
	public List<Layer> queryLayerByProType(String  pro_type);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入图层类型信息
	 * @param layer
	 * @return
	 */
	public String insertLayer(Layer layer);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 插入图层类型信息
	 * @param layer
	 * @return
	 */
	public void insertLayer(LayerEntity layerEntity);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 更新图层类型信息
	 * @param layer
	 */
	public void updateLayer(Layer layer);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月21日
	 * @description 删除图层类型信息
	 * @param idList
	 */
	public String deleteLayer(List<Map<String, Object>> idList);
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
	public List<Object> queryLayerTree( String id, String name, String level) ;
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 更新图层属性信息
	 * @param layer
	 */
	public void updateLayerProp(Layer layer);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 根据图层ID 查询图层属性
	 * @param task_id
	 * @return
	 */
	public List<LayerProp> queryLayerProps(String  layer_id);

	/**
	 * @Description: 根据pro_type查询layer
	 * @author 刘旭升
	 * @date 2015年8月25日 下午6:55:59 
	 * @version V1.0 
	 * @param pro_type
	 * @param pProTypeName 
	 * @param rootId 
	 * @return
	 */
	public List<Layer> queryOneLayerByProType(String  pro_type, String pProTypeName, String rootId);

}

