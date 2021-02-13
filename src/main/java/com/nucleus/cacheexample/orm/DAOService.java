/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.orm;

import com.nucleus.cacheexample.db.DBService;
import com.nucleus.cacheexample.entity.service.BeanFactory;
import com.nucleus.cacheexample.cache.serivce.CacheService;
import com.nucleus.cacheexample.utils.Logger;

/**
 *
 * @author sandesh.singh
 */
public class DAOService {
	
	private CacheService cacheService = null;
	private DBService dBService = null;
	private boolean enableCache;
	
	public <T> DAOService(Class<T> resultClass, BeanFactory beanFactory, boolean enableCache){
		dBService = beanFactory.getDBService();
		this.enableCache = enableCache;
		if(enableCache){
			cacheService = new CacheService(resultClass, 10, 10, dBService, 0.5);
		}
	}

	public <T> T find(Class<T> resultClass, int id){
		Logger.println("DaoService__find: "+id);
		if(this.enableCache){
			Logger.println("Accessing Cache: ");
			return resultClass.cast(cacheService.get(id));
		} else {
			Logger.println("Accessing DB: ");
			return (T)this.dBService.fetchUserData(id);
		}
	}
	
}
