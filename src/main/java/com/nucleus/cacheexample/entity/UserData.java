/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author sandesh.singh
 */
@Getter
@Setter
@ToString
public class UserData {
	
	private int id;
	private String firstName;

	public UserData(int i, String name) {
		this.id = i;
		this.firstName = name;
	}
	
}
