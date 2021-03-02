/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.vo;

import com.nucleus.cacheexample.db.DataFetcher;
import com.nucleus.cacheexample.listners.RecordEvictionListener;
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
public class CacheConfigVO {

	private int capacity = 0;
	private int recordExpiryInseconds = 0;
	private DataFetcher dataFetcher;
	private double evictionRatio = 0;
	private int memoryThresholdSize = 0;
	private RecordEvictionListener recordEvictionListener = null;

}
