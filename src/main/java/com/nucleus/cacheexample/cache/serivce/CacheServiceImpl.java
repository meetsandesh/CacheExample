/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.serivce;

import com.nucleus.cacheexample.cache.vo.CacheConfigVO;
import com.nucleus.cacheexample.cache.vo.CacheMetadata;
import com.nucleus.cacheexample.cache.vo.DataStructureWrapper;
import com.nucleus.cacheexample.db.DataFetcher;
import com.nucleus.cacheexample.listners.RecordEvictionListener;
import com.nucleus.cacheexample.utils.Logger;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public class CacheServiceImpl<T extends Serializable> implements CacheService<T> {
	
	private final DataStructureWrapper<CacheMetadata<T>> cacheWrapper;
	private final int capacity;
	private final int recordExpiryInseconds;
	private final DataFetcher<T> dataFetcher;
	private final double loadRatio;
	private int memoryThresholdSize = 0;
	private RecordEvictionListener recordEvictionListener = null;
	private final Comparator<CacheMetadata<T>> comparator = (CacheMetadata<T> m1, CacheMetadata<T> m2) -> {
//		int comparision = m1.getCount().compareTo(m2.getCount());
		int comparision = m1.getDate().compareTo(m2.getDate());
		if(comparision==0){
			return 0;
		} else if(comparision<0){
			return 1;
		} else {
			return -1;
		}
	};
	
	public CacheServiceImpl(CacheConfigVO cacheConfigVO){
		this.capacity = cacheConfigVO.getCapacity();
		this.recordExpiryInseconds = cacheConfigVO.getRecordExpiryInseconds();
		this.dataFetcher = cacheConfigVO.getDataFetcher();
		this.loadRatio = cacheConfigVO.getEvictionRatio();	//always less than 1
		this.memoryThresholdSize = cacheConfigVO.getMemoryThresholdSize();
		this.recordEvictionListener = cacheConfigVO.getRecordEvictionListener();
		if(this.recordEvictionListener==null){
			this.recordEvictionListener = new RecordEvictionListener() {};
		}
		this.cacheWrapper = new DataStructureWrapper<>(this.comparator, this.recordEvictionListener);
	}
	
	@Override
	public T get(int key){
		CacheMetadata<T> obj = cacheWrapper.searchAndFetch(key);
		//check if object is in cache
		if(obj==null){
			//not in cache
			Logger.debug("Cache MISS");
			return updateInCache(key, 1L);
		} else {
			//exists in cache
			//compare time
			Date initDate = obj.getDate();
			Date now = getCurrentDate();
			long seconds = (now.getTime()-initDate.getTime())/1000;
			if(seconds > (long)this.recordExpiryInseconds){
				//expiry time elapsed
				Logger.debug("CACHE HIT_EXPIRY");
				return updateInCache(key, obj.getCount()+1);
			} else {
				//within expiry
				//update count only
				Logger.debug("CACHE HIT");
				cacheWrapper.delete(obj, false);
				cacheWrapper.printSize();
				obj.setCount(obj.getCount()+1);
				cacheWrapper.add(obj);
				cacheWrapper.printSize();
				return obj.getObject();
			}
		}
	}
	
	private T updateInCache(int key, long count){
		//fetch from dataFetcher
		Logger.println("Fetching from DB");
		T temp = dataFetcher.getDataFromDB(key);
		//check cahe size
		int size = cacheWrapper.getSize();
		cacheWrapper.printSize();
		if(size==this.capacity){
			//delete few records
			Logger.println("Capacity reached: "+capacity);
			cacheWrapper.sort();
			cacheWrapper.deleteFromLast((int)Math.ceil(capacity*(loadRatio)));
			cacheWrapper.printSize();
		}
		//add it in cache
		Logger.println("Add to cache");
		CacheMetadata<T> cacheMetadata = new CacheMetadata<>(key, temp, getCurrentDate(), count);
		cacheWrapper.add(cacheMetadata);
		cacheWrapper.printSize();
		return temp;
	}
	
	protected Date getCurrentDate(){
		return new Date();
	}
	
}
