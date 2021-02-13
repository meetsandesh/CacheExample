/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.entity.service;

import com.nucleus.cacheexample.entity.UserData;
import com.nucleus.cacheexample.orm.DAOService;
import com.nucleus.cacheexample.utils.Logger;

/**
 *
 * @author sandesh.singh
 */
public class UserDataService {
	
	private DAOService daoService = null;
	
	public UserDataService(BeanFactory beanFactory){
		daoService = beanFactory.getDAOService();
	}
	
	public UserData getUserData(int id){
		Logger.println("UserDataService__getUserData: "+id);
		return daoService.find(UserData.class, id);
	}
	
}
