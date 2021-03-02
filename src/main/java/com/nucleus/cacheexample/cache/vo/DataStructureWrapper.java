/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nucleus.cacheexample.cache.vo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nucleus.cacheexample.listners.RecordEvictionListener;
import com.nucleus.cacheexample.utils.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author sandesh.singh
 * @param <T>
 */
public class DataStructureWrapper<T> {
	
	private Class<T> resultantClass;
	private int memoryThresholdSize;
	//actual records
	private List<T> list = new ArrayList<>();
	//file system records
	private File cacheLocation;
	//key is id, and value is position of object in list
	private Map<String, String>  hashMap = new HashMap<>();
	//comparator
	private final Comparator<T> comparator;
	//listener
	private RecordEvictionListener recordEvictionListener = null;
	
	public DataStructureWrapper(Class<T> result, Comparator c, RecordEvictionListener<T> recordEvictionListener, int memoryThresholdSize){
		this.resultantClass = result;
		this.memoryThresholdSize = memoryThresholdSize;
		this.comparator = c;
		this.recordEvictionListener = recordEvictionListener;
		String path = System.getProperty("user.dir");
		Logger.debug("File System Cache is located at : "+path);
		cacheLocation = new File(path+"\\FS_CACHE");
		if(this.cacheLocation.exists()){
			this.cacheLocation.delete();
		}
		this.cacheLocation.mkdir();
	}
	
	public void add(T obj){
		Logger.println("Adding object: "+getKey(obj));
		if(hashMap.get(""+getKey(obj)) == null){
			int size = list.size();
			if(size<this.memoryThresholdSize){
				list.add(obj);
				hashMap.put(""+getKey(obj), STORAGE.MEMORY+"~"+size);
			} else {
				String fileName = ""+hashMap.size();
				writeToFSCache(this.cacheLocation, fileName, obj);
				hashMap.put(""+getKey(obj), STORAGE.FILE+"~"+fileName);
			}
		}
	}
	
	public void delete(T obj, boolean callListener){
		Logger.println("Deleting object: "+getKey(obj));
		String indexMetadata = hashMap.get(""+getKey(obj));
		if(indexMetadata != null){
			String arr[] = indexMetadata.split("~");
			if(arr[0].equals(STORAGE.MEMORY)){
				int index = Integer.parseInt(arr[1]);
				int size = list.size();
				int totalSize = hashMap.size();
				if(totalSize<=this.memoryThresholdSize){
					if(index==size-1){
						list.remove(size-1);
						hashMap.remove(""+getKey(obj));
					} else {
						T lastItem = list.get(size-1);
						Collections.swap(list, index, size-1);
						list.remove(size-1);
						hashMap.remove(""+getKey(obj));
						hashMap.put(""+getKey(lastItem), STORAGE.MEMORY+"~"+index);
					}
				} else {
					String lastFileName = ""+(hashMap.size()-1);
					T lastObj = getFromFSCache(this.cacheLocation, lastFileName);
					list.add(lastObj);
					hashMap.put(""+getKey(lastObj), STORAGE.MEMORY+"~"+(list.size()-1));
					T lastItem = list.get(list.size()-1);
					Collections.swap(list, index, list.size()-1);
					hashMap.remove(""+getKey(obj));
					hashMap.put(""+getKey(lastItem), STORAGE.MEMORY+"~"+index);
				}
			} else {
				//delete from file system
				int index = Integer.parseInt(arr[1]);
				if(index==hashMap.size()-1){
					File f = new File(this.cacheLocation.getAbsolutePath()+"\\"+arr[1]);
					f.delete();
					hashMap.remove(""+getKey(obj));
				} else {
					String lastFileName = ""+(hashMap.size()-1);
					File f = new File(this.cacheLocation.getAbsolutePath()+"\\"+arr[1]);
					f.delete();
					hashMap.remove(""+getKey(obj));
					//rename last file
					T lastObj = getFromFSCache(this.cacheLocation, lastFileName);
					File f1 = new File(this.cacheLocation.getAbsolutePath()+"\\"+lastFileName);
					File f2 = new File(this.cacheLocation.getAbsolutePath()+"\\"+arr[1]);
					f1.renameTo(f2);
					hashMap.put(""+getKey(lastObj), STORAGE.FILE+"~"+arr[1]);
				}
			}
			//call listener
			if(callListener){
				this.recordEvictionListener.evictFromCache(obj);
			}
		} else {
			Logger.debug("Error occurred: index is null for "+getKey(obj));
			System.exit(11);
		}
    }

	public T searchAndFetch(String id){
		Logger.println("Searching for id: "+id);
		String indexMetadata = hashMap.get(id);
		if(indexMetadata!=null){
			String arr[] = indexMetadata.split("~");
			if(arr[0].equals(STORAGE.MEMORY)){
				int index = Integer.parseInt(arr[1]);
				Logger.println("Found id: "+id+" at index: "+index+", list size: "+list.size()+" map size: "+hashMap.size()+" within limit: "+(index<list.size()));
				return list.get(index);
			} else {
				//pick from file sytem
				return getFromFSCache(this.cacheLocation, arr[1]);
			}
		} else {
			Logger.println("Not found id: "+id);
			return null;
		}
	}
	
	public int getSize(){
		return hashMap.size();
	}
	
	public int getMemorySize(){
		return list.size();
	}
	
	public int getDiskSize(){
		return hashMap.size()-list.size();
	}
	
	public void sort(){
		//add everything in memory
		List<T> allRecords = new ArrayList<>();
		//from memory
		for(T rec:list){
			allRecords.add(rec);
		}
		//from file system
		String contents[] = this.cacheLocation.list();
		for(int i=0; i<contents.length; i++) {
			T obj = getFromFSCache(this.cacheLocation, contents[i]);
			allRecords.add(obj);
		}
		Logger.println("Sorting list");
		Logger.println("INIT:");
		printWrapperMetadata();
		allRecords.sort(this.comparator);
		//clear everything
		hashMap = new HashMap<>();
		list = new ArrayList<>();
		File[] files = this.cacheLocation.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					//will never happen
				} else {
					f.delete();
				}
			}
		}
		//add everything back and update hashMap
		for(int i=0;i<allRecords.size();i++){
			T obj=allRecords.get(i);
			int size = list.size();
			if(size<this.memoryThresholdSize){
				//add in memory
				list.add(obj);
				hashMap.put(""+getKey(obj), STORAGE.MEMORY+"~"+i);
			} else {
				//add in file system
				String fileName = ""+i;
				writeToFSCache(this.cacheLocation, fileName, obj);
				hashMap.put(""+getKey(obj), STORAGE.FILE+"~"+fileName);
			}
		}
		Logger.println("FINAL:");
		printWrapperMetadata();
	}

	public void deleteFromLast(int count) {
		Logger.println("Deleting items from last: "+count);
		printSize();
		List<String> deleted=new ArrayList<>();
		for(int i=0;i<count;i++){
			//get last item
			T temp = getLastRecord();
			delete(temp, true);
			deleted.add(""+getKey(temp));
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
			Logger.println("P_"+i+" :: ID_"+getKey(list.get(i)));
		}
		Logger.println("HashMap:");
		for(Map.Entry<String, String> e:hashMap.entrySet()){
			Logger.println("ID_ "+e.getKey()+" -> P_"+e.getValue());
		}
		Logger.println("Mix Up:");
		for(Map.Entry<String, String> e:hashMap.entrySet()){
			String indexMetadata = e.getValue();
			String[] arr = indexMetadata.split("~");
			if(arr[0].equals(STORAGE.MEMORY)){
				int index = Integer.parseInt(arr[1]);
				Logger.println("HM_ "+e.getKey()+" -> P_"+e.getValue()+" -> L_"+getKey(list.get(index)));
			} else {
				//file system
				T obj = getFromFSCache(this.cacheLocation, arr[1]);
				Logger.println("HM_ "+e.getKey()+" -> P_"+e.getValue()+" -> F_"+getKey(obj));
			}
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

	private void writeToFSCache(File location, String fileName, T obj) {
		try{
			File file = new File(location.getAbsolutePath()+"\\"+fileName);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			String json = new Gson().toJson(obj);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
			bufferedWriter.write(json);
			bufferedWriter.close();
		} catch(Exception e){
			//think what can be done
		}
	}

	private T getFromFSCache(File location, String fileName) {
		T object = null;
		try{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(location.getAbsolutePath()+"\\"+fileName));
			String line = bufferedReader.readLine();
			String jsonData = "";
			while(line != null) {
				jsonData+=line;
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			Gson gson = new GsonBuilder().create();
			object = gson.fromJson(jsonData, this.resultantClass);
		} catch(Exception e){
			//think what can be done
		}
		return object;
	}

	private T getLastRecord() {
		int size = list.size();
		if(size<this.memoryThresholdSize){
			//get from in memory
			return list.get(size-1);
		} else {
			//return from file system
			String contents[] = this.cacheLocation.list();
			//return last
			return getFromFSCache(this.cacheLocation, contents[contents.length-1]);
		}
	}
	
	private static class STORAGE{
		public static final String MEMORY = "MEMORY";
		public static final String FILE = "FILE";
	}
	
	private String getKey(T obj){
		Method m;
		String str=null;
		try {
			m = obj.getClass().getMethod("getIdentifier");
			str = (String) m.invoke(obj);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			java.util.logging.Logger.getLogger(DataStructureWrapper.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(12);
		}
		return str;
	}
	
}
