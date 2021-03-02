/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.listners;

import com.nucleus.cacheexample.utils.Logger;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public interface RecordEvictionListener<T>{
	
	default void evictFromCache(T cacheMetadata){
		Logger.debug("Record evicted: "+cacheMetadata.toString());
	}
	
}
