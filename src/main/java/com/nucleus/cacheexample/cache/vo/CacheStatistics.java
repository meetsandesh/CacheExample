/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.vo;

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
public class CacheStatistics {
	
	private int cacheCapacity;
	private int cacheSize;
	private int memorySize;
	private int diskSize;
	private int accessCount;
	private double hitRatio;
	private double hitExpiryRatio;
	private double missRatio;
	private double avgRecordReplenishmentTime;
	private double avgLRUOptimizationTime;
	
}
