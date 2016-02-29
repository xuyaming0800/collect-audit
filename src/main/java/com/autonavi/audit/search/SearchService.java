package com.autonavi.audit.search;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.declare.SearchResult;
import com.search.implement.MySearchServiceImp;
import com.search.model.AroundType;
import com.search.model.MyRequest;
import com.search.model.ShapeType;

@Component
public class SearchService {

	@Autowired
	private MySearchServiceImp searchServiceImp;

	/**
	 * 面与面相交查询
	 * 
	 * @param indexNames
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> doSearch(String[] indexNames, double x,
			double y, int radius) throws Exception {
		SearchResult searchResult = new SearchResult(0);
		MyRequest request = new MyRequest();
		request.setIndexNameArray(indexNames);

		// 周边搜索
		request.setX(x);
		request.setY(y);
		request.setRadius(radius);
		request.setAroundType(AroundType.shape);
		request.setShapeType(ShapeType.intersects);

		searchResult = searchServiceImp.search(request);
		System.out.println("status:" + searchResult.getStatus());
		System.out.println("cost:" + searchResult.getSearchCost());
		System.out.println("count:" + searchResult.getCount());

		return searchResult.getList();
	}
}
