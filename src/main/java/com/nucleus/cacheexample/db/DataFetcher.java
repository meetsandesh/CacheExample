/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.db;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public interface DataFetcher<T> {
	
	T getDataFromDB(int i);
	
}
