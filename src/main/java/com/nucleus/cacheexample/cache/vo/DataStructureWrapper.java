/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.vo;

import com.nucleus.cacheexample.listners.RecordEvictionListener;
import com.nucleus.cacheexample.utils.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public class DataStructureWrapper<T> {
	
	//actual records
	private List<T> list = new ArrayList<>();
	//key is id, and value is position of object in list
	private Map<String, String>  hashMap = new HashMap<>();
	//comparator
	private final Comparator<T> comparator;
	//listener
	private RecordEvictionListener recordEvictionListener = null;
	
	public DataStructureWrapper(Comparator c, RecordEvictionListener<T> recordEvictionListener){
		this.comparator = c;
		this.recordEvictionListener = recordEvictionListener;
	}
	
	public void add(T obj){
		Logger.println("Adding object: "+obj.hashCode());
		if(hashMap.get(""+obj.hashCode()) == null){
			int size = list.size();
			list.add(obj);
			hashMap.put(""+obj.hashCode(), STORAGE.MEMORY+"~"+size);
		}
	}
	
	public void delete(T obj, boolean callListener){
		Logger.println("Deleting object: "+obj.hashCode());
		String indexMetadata = hashMap.get(""+obj.hashCode());
		if(indexMetadata != null){
			String arr[] = indexMetadata.split("~");
			if(arr[0].equals(STORAGE.MEMORY)){
				int index = Integer.parseInt(arr[1]);
				int size = list.size();
				if(size>1){
					T lastItem = list.get(size-1);
					Collections.swap(list, index, size-1);
					hashMap.put(""+lastItem.hashCode(), STORAGE.MEMORY+"~"+index);
				}
				list.remove(size-1);
				hashMap.remove(""+obj.hashCode());
			}
			//call listener
			if(callListener){
				this.recordEvictionListener.evictFromCache(obj);
			}
		}
    }

	public T searchAndFetch(String id){
		Logger.println("Searching for id: "+id);
		String indexMetadata = hashMap.get(id);
		if(indexMetadata!=null){
			String arr[] = indexMetadata.split("~");
			int index = Integer.parseInt(arr[1]);
			Logger.println("Found id: "+id+" at index: "+index+", list size: "+list.size()+" map size: "+hashMap.size()+" within limit: "+(index<list.size()));
			return list.get(index);
		} else {
			Logger.println("Not found id: "+id);
			return null;
		}
	}
	
	public int getSize(){
		return list.size();
	}
	
	public void sort(){
		Logger.println("Sorting list");
		Logger.println("INIT:");
		printWrapperMetadata();
		list.sort(this.comparator);
		//update index hashMap
		hashMap=new HashMap<>();
		for(int i=0;i<list.size();i++){
			T temp=list.get(i);
			hashMap.put(""+temp.hashCode(), STORAGE.MEMORY+"~"+i);
		}
		Logger.println("FINAL:");
		printWrapperMetadata();
	}

	public void deleteFromLast(int count) {
		Logger.println("Deleting items from last: "+count);
		printSize();
		List<String> deleted=new ArrayList<>();
		for(int i=0;i<count;i++){
			T temp = list.get(list.size()-1);
			delete(temp, true);
			deleted.add(""+temp.hashCode());
			printSize();
		}
		//delete from hashMap, idk why its happening
		for(String i:deleted){
			hashMap.remove(i);
			printSize();
		}
	}

	private void printWrapperMetadata() {
		Logger.println("_________________________________________________");
		Logger.println("List:");
		for(int i=0;i<list.size();i++){
			Logger.println("P_"+i+" :: ID_"+list.get(i).hashCode());
		}
		Logger.println("HashMap:");
		for(Map.Entry<String, String> e:hashMap.entrySet()){
			Logger.println("ID_ "+e.getKey()+" -> P_"+e.getValue());
		}
		Logger.println("Mix Up:");
		for(Map.Entry<String, String> e:hashMap.entrySet()){
			String indexMetadata = e.getValue();
			String[] arr = indexMetadata.split("~");
			int index = Integer.parseInt(arr[1]);
			Logger.println("HM_ "+e.getKey()+" -> P_"+e.getValue()+" -> L_"+list.get(index).hashCode());
		}
		Logger.println("_________________________________________________");
	}
	
	public void printSize(){
		if(list.size() == hashMap.size()){
			Logger.println("Cache Size: L_"+list.size()+" HM_"+hashMap.size()+" L_HM");
		} else {
			Logger.println("Cache Size: L_"+list.size()+" HM_"+hashMap.size()+" L_NE_HM");
		}
	}
	
	private static class STORAGE{
		public static final String MEMORY = "MEMORY";
		public static final String FILE = "FILE";
	}
	
}
