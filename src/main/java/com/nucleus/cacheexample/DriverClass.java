/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample;

import com.nucleus.cacheexample.entity.UserData;
import com.nucleus.cacheexample.entity.service.BeanFactory;
import com.nucleus.cacheexample.entity.service.UserDataService;
import com.nucleus.cacheexample.utils.Logger;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author sandesh.singh
 */
public class DriverClass {
	
	private static final int TOTAL_RUNS = 100000;
	
	public static void main(String[] args) {
		float averageTime = 0;
		float currentRunTime = 0;
		BeanFactory beanFactory = new BeanFactory(true);
		UserDataService userDataService = beanFactory.getUserDataService();
		Logger.println("START****************************************");
		for(int i=1;i<=TOTAL_RUNS;i++){
			Logger.debug("*********************************************");
			Logger.debug("Run: " + i);
			int randomId = randonNumber(1, 100);
			Date d1=new Date();
			UserData userdata = userDataService.getUserData(randomId);
			Date d2=new Date();
			currentRunTime = (d2.getTime() - d1.getTime());
			averageTime = ((averageTime/i) * (i-1)) + (currentRunTime/i);
			Logger.println("Accessing data: " + userdata.toString());
			Logger.debug("Current accessing time: " + currentRunTime);
			Logger.debug("Average accessing time: " + averageTime);
			Logger.debug("*********************************************");
		}
		Logger.println("END******************************************");
	}
	
	public static int randonNumber(int low, int high){
		Random r = new Random();
		int result = r.nextInt(high-low) + low;
		return result;
	}
}
