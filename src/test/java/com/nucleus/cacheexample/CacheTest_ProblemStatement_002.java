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
import com.nucleus.cacheexample.listners.RecordEvictionListener;
import com.nucleus.cacheexample.utils.Logger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		DataFetcher<UserData> dataFetcher = (String identifier) -> {
			//for delay effect for miss
//			try {
//				Thread.currentThread().sleep(1000);
//			} catch (InterruptedException ex) {
//				Logger.println("interrupted Exception occurred.");
//			}
			int i = Integer.parseInt(identifier);
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
		cacheConfigVO.setEvictionRatio(0.3);
		cacheConfigVO.setMemoryThresholdSize(0);
		cacheConfigVO.setRecordEvictionListener(new RecordEvictionListener() {
			@Override
			public void evictFromCache(Object cacheMetadata) {
				Logger.debug("External method: Record evicted: "+cacheMetadata.toString());
			}
		});
		
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
//		cacheConfigVO1.setEvictionRatio(0.3);
//		
//		this.cache = new CacheServiceImpl<CarData>(cacheConfigVO) {
//			@Override
//			protected Date getCurrentDate() {
//				return date;
//			}
//		};
		
	}

	@Test
	public void cacheTest_Expiry() {
		Logger.debug("-------------------cacheTest_Expiry-------------------");
		int id = 123;
		Assert.assertNull(instanceCounter.get(id));		//initially no counter
		
		
		//Repeated access at no advancement of timer
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//counter = 1
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//counter = 1
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//counter = 1
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//counter = 1
		
		date = new Date(1355270401000l);		//forwarding the clock to 1 sec
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//still counter = 1
		
		date = new Date(1355270405000l);		//forwarding the clock to 5 sec
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id).intValue());	//still counter = 1
		
		date = new Date(1355270411000l);		//forwarding the clock to 11 sec
		cache.get(""+id);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(2, instanceCounter.get(id).intValue());	//counter = 2
		
		Logger.debug("------------------------------------------------------");
	}

	@Test
	public void cacheTest_LRU() {
		Logger.debug("---------------------cacheTest_LRU--------------------");
		int id1 = 123;
		int id2 = 456;
		int id3 = 789;
		
		date = new Date(1355270401000l);		//forwarding the clock to 1 sec
		cache.get(""+id1);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id1).intValue());	//counter = 1
		
		date = new Date(1355270402000l);		//forwarding the clock to 2 sec
		cache.get(""+id2);
		Logger.debug(cache.getStatistics().toString());
		cache.get(""+id2);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id2).intValue());	//counter = 1
		
		date = new Date(1355270403000l);		//forwarding the clock to 3 sec
		cache.get(""+id3);
		Logger.debug(cache.getStatistics().toString());
		cache.get(""+id3);
		Logger.debug(cache.getStatistics().toString());
		cache.get(""+id3);
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id3).intValue());	//counter = 1
		
		int id4 = 101112;
		cache.get(""+id4);			//here the LRU logic should kick in and evict 123
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(1, instanceCounter.get(id4).intValue());	//counter = 1
		
		cache.get(""+id1);		//recreation of 123 entry
		Logger.debug(cache.getStatistics().toString());
		Assert.assertEquals(2, instanceCounter.get(id1).intValue());	//counter = 2.. yay!!
		
		Logger.debug("------------------------------------------------------");
	}
	
}
