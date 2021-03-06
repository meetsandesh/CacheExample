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
public class CarData {
	
	private int id;
	private String carNumber;
	
	public CarData(int i, String name) {
		this.id = i;
		this.carNumber = name;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CarData other = (CarData) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
	
	
	
}
