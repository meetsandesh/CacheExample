/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.entity.service;

import com.nucleus.cacheexample.db.DBService;
import com.nucleus.cacheexample.entity.UserData;
import com.nucleus.cacheexample.orm.DAOService;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sandesh.singh
 */
public class BeanFactory {
	
	private final Map<String, Object> beanMap = new HashMap<>();
	
	public BeanFactory(boolean enableCache){
		createDBService();
		createDAOService(enableCache);
		createUserDataService();
	}
	
	public Object getBean(String beanName){
		return beanMap.get(beanName);
	}
	
	public UserDataService getUserDataService(){
		Object object = getBean("userDataService");
		UserDataService userDataService = (UserDataService) object;
		return userDataService;
	}
	
	public DBService getDBService(){
		Object object = getBean("dbService");
		DBService dBService = (DBService) object;
		return dBService;
	}
	
	public DAOService getDAOService(){
		Object object = getBean("daoService");
		DAOService daoService = (DAOService) object;
		return daoService;
	}

	private void createUserDataService() {
		UserDataService userDataService = new UserDataService(this);
		beanMap.put("userDataService", userDataService);
	}
	
	private void createDBService() {
		DBService dbService = new DBService();
		beanMap.put("dbService", dbService);
	}

	private void createDAOService(boolean enableCache) {
		DAOService daoService = new DAOService(UserData.class, this, enableCache);
		beanMap.put("daoService", daoService);
	}	
}
