/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
@Getter
@Setter
@ToString
public class CacheMetadata<T extends Serializable> {
	
	private int id;
	private T object;
	private Date date;
	private Long count;

	public CacheMetadata(int i, T obj, Date d, Long c){
		this.id=i;
		this.object=obj;
		this.date=d;
		this.count=c;
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
		final CacheMetadata<?> other = (CacheMetadata<?>) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
	
	
}
