/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.serivce;

import com.nucleus.cacheexample.cache.vo.CacheStatistics;
import java.io.Serializable;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public interface CacheService<T extends Serializable> {
	
	T get(int key);
	
	CacheStatistics getStatistics();
	
}
