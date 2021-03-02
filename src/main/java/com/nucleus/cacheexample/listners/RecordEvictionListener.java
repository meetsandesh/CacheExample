/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.listners;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
@FunctionalInterface
public interface RecordEvictionListener<T>{
	
	void evictFromCache(T cacheMetadata);
	
}
