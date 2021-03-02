/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.db;

import com.nucleus.cacheexample.entity.UserData;
import com.nucleus.cacheexample.utils.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author sandesh.singh
 */
public class DBService implements DataFetcher<UserData>{
	
	private final Map<Integer, UserData> allUsers=new HashMap<>();
	private final int TOTAL_RECORDS = 100;
	private final Set<String> uniqueNames = new HashSet<>();
	
	public DBService(){
		//populate allUsers, assuming its coming from DB
		for(int i = 1 ; i <= TOTAL_RECORDS; i++){
			UserData userData = new UserData(i, generateName());
			allUsers.put(i, userData);
		}
	}
	
	private String generateName(){
		boolean unique=false;
		String name = null;
		while(!unique){
			name = generateRandomString();
			unique = uniqueNames.add(name);
		}
		return name;
	}
	
	private String generateRandomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return generatedString;
	}
	
	public UserData fetchUserData(int id){
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException ex) {
			Logger.println("interrupted Exception occurred.");
		}
		return allUsers.get(id);
	}

	@Override
	public UserData getDataFromDB(String i) {
		return fetchUserData(Integer.parseInt(i));
	}
	
}
