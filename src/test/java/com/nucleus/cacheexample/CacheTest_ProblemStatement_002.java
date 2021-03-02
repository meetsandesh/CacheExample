/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample;

import com.nucleus.cacheexample.cache.serivce.CacheService;
import com.nucleus.cacheexample.cache.serivce.CacheServiceImpl;
import com.nucleus.cacheexample.cache.vo.CacheConfigVO;
import com.nucleus.cacheexample.db.DataFetcher;
import com.nucleus.cacheexample.entity.UserData;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;

/**
 *
 * @author sandesh.singh
 */
public class CacheTest_ProblemStatement_002 {
	
	private CacheService<UserData> cache;
	private Date date = new Date(1355270400000l);
	Map<Integer, AtomicInteger> instanceCounter = new HashMap<>();
	
	@Before
	public void init() {
		DataFetcher<UserData> dataFetcher = (int i) -> {
			UserData userData = new UserData(i, "RANDOM_STRING_"+i);
			if(!instanceCounter.containsKey(i)) {
				instanceCounter.put(i, new AtomicInteger(0));
			}
			instanceCounter.get(i).getAndAdd(1);
			return userData;
		};
		
		CacheConfigVO cacheConfigVO = new CacheConfigVO();
		cacheConfigVO.setCapacity(3);
		cacheConfigVO.setRecordExpiryInseconds(5);
		cacheConfigVO.setDataFetcher(dataFetcher);		//value callback
		cacheConfigVO.setLoadRatio(0.4);
		
		this.cache = new CacheServiceImpl<UserData>(cacheConfigVO) {
			@Override
			protected Date getCurrentDate() {
				return date;
			}
		};
		
//		Following will give error
//
//		DataFetcher<CarData> dataFetcher1 = (int i) -> {
//			UserData userData = new UserData(i, "RANDOM_STRING_"+i);
//			if(!instanceCounter.containsKey(i)) {
//				instanceCounter.put(i, new AtomicInteger(0));
//			}
//			instanceCounter.get(i).getAndAdd(1);
//			return userData;
//		};
//		
//		CacheConfigVO cacheConfigVO1 = new CacheConfigVO();
//		cacheConfigVO1.setCapacity(3);
//		cacheConfigVO1.setRecordExpiryInseconds(5);
//		cacheConfigVO1.setDataFetcher(dataFetcher1);		//value callback
//		cacheConfigVO1.setLoadRatio(0.4);
//		
//		this.cache = new CacheServiceImpl<CarData>(cacheConfigVO) {
//			@Override
//			protected Date getCurrentDate() {
//				return date;
//			}
//		};
		
	}

	

}
