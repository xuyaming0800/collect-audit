package com.autonavi.audit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.Layer;
import com.autonavi.audit.entity.LayerProp;
import com.autonavi.audit.entity.Overlay;
import com.autonavi.audit.entity.Point;
import com.autonavi.audit.entity.Property;

@MyBatisRepository
public interface EditPlatformDao {
	/**
	 *  根据任务ID查询覆盖物基本信息
	 * @param task_id
	 * @return
	 */
	public List<Overlay> queryOverlayByTaskId(@Param(value = "task_id") String  task_id);
	
	/**
	 *  根据图层ID查询覆盖物基本信息
	 * @param layer_id
	 * @return
	 */
	public List<Overlay> queryOverlayByLayerId(@Param(value = "layer_id") String  layer_id);
	/**
	 *  根据图层IDs查询覆盖物基本信息
	 * @param layer_id
	 * @return
	 */
	public List<Overlay>  queryOverlayByLayerIds(@Param(value = "layerIdList") List<Map<String, Object>>  layerIdList);
	/**
	 *  根据ID查询覆盖物基本信息
	 * @param id
	 * @return
	 */
	public Overlay queryOverlayById(@Param(value = "id") String id);
	/**
	 * 插入覆盖物基本信息
	 * @param overlay
	 */
	public void insertOverlay(Overlay overlay);
	/**
	 * 更新覆盖物基本信息
	 * @param overlay
	 */
	public void updateOverlay(Overlay overlay);
	/**
	 * 删除覆盖物基本信息
	 * @param id
	 */
	public void deleteOverlay(@Param(value = "id") String id);
	/**
	 *  根据覆盖物ID查询覆盖物坐标信息
	 * @param overlay_id
	 * @return
	 */
	public List<Point> queryPointByOverlayId(@Param(value = "overlay_id") String  overlay_id);
	/**
	 * 插入覆盖物坐标信息
	 * @param point
	 */
	public void insertPoint(Point point);
	/**
	 * 批量插入覆盖物坐标信息
	 * @param points
	 */
	public void insertPoints(@Param(value = "points") List<Point> points);
	/**
	 *  根据覆盖物ID删除覆盖物坐标信息
	 * @param overlay_id
	 */
	public void deletePoint(@Param(value = "overlay_id") String overlay_id);
	
	/**
	 * 根据覆盖物ID查询覆盖物属性信息
	 * @param overlay_id
	 * @return
	 */
	public List<Property> queryPropertyByOverlayId(@Param(value = "overlay_id") String  overlay_id);
	
	/**
	 * 插入覆盖物属性信息
	 * @param property
	 */
	public void insertProperty(Property property);
	/**
	 * 批量插入覆盖物属性信息
	 * @param props
	 */
	public void insertProps(@Param(value = "props") List<Property> props);
	/**
	 *  根据覆盖物ID删除覆盖物属性信息
	 * @param overlay_id
	 */
	public void deleteProperty(@Param(value = "overlay_id") String overlay_id);
	
	
	/**
	 *  根据任务ID查询图层信息
	 * @param task_id
	 * @return
	 */
	public List<Layer> queryLayerByProType(@Param(value = "pro_type") String  pro_type);
	/**
	 *  根据 项目类型查询图层数量
	 * @param pro_type
	 * @param id 
	 * @return
	 */
	public  List<Layer>  queryLayerCountByProType(@Param(value = "pro_type") String  pro_type, @Param(value = "pId")String pId);
	
	/**
	 * 插入图层类型信息
	 * @param property
	 */
	public void insertLayer(Layer layer);
	/**
	 * 更新图层类型信息
	 * @param property
	 */
	public void updateLayer(Layer layer);
	/**
	 *  删除图层类型信息
	 * @param id
	 */
	public void deleteLayer(@Param(value = "idList") List<Map<String, Object>> idList);
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年8月3日
	 * @description 查询图层树
	 * @param pro_type
	 * @return
	 */
	public List<Object> queryLayerTree(@Param(value = "id") String id );
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 插入图层属性信息
	 * @param layerProps
	 */
	public void insertLayerProps(@Param(value = "layerProps") List<LayerProp> layerProps) ;
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 删除图层属性信息
	 * @param layer_id
	 */
	public void deleteLayerProp(@Param(value = "layer_id") String  layer_id);
	
	/**
	 * 
	 * @author jinwenpeng
	 * @date 2015年7月31日
	 * @description 根据任务ID和图层ID 查询图层属性
	 * @param layer_id
	 * @return
	 */
	public List<LayerProp> queryLayerProps(@Param(value = "layer_id")String  layer_id);

	/**
	 * @Description: 根据BATCHID来查询覆盖物坐标信息
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午4:04:30 
	 * @version V1.0 
	 * @param batchId
	 */
	public List<Point> queryPointByBatchId(@Param(value = "batch_id") String batchId);

	/**
	 * @Description: 根据BATCHID来查询覆盖物属性信息
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午4:20:01 
	 * @version V1.0 
	 * @param batchId
	 * @return
	 */
	public List<Property> queryPropertyByBatchId(@Param(value = "batch_id")String batchId);

	public List<String> findProNamesBySystemId(@Param(value = "systemId")String systemId);

}
