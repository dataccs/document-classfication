package org.dataccs.document.processor.test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class TestSerializable {

	private Map<String, Integer> wordCounterMap = new HashMap<String, Integer>();
	
	private void init(){
		wordCounterMap.put("银河系", 31);
		wordCounterMap.put("太阳", 19);
		wordCounterMap.put("地球", 90);
		wordCounterMap.put("世界", 10);
		wordCounterMap.put("你好", 39);
	}
	
	@Test
	public void seria(){
		init();
		try{
			File file = new File("trainfile/seria.txt");
			if(file.exists()){
				file.delete();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(wordCounterMap);
			oos.flush();
			System.out.println("序列化成功！");
			
		} catch (Exception e){
			e.printStackTrace();
		}
		deSeria();
	}
	
	private void deSeria(){
		try{
			File file = new File("trainfile/seria.txt");
			if(file.exists()){
				file.delete();
			}
			FileInputStream fos = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fos);
			Map<String, Integer> map = (HashMap<String, Integer>)oos.readObject();
			
			for(Entry<String, Integer> entry : map.entrySet()){
				System.out.println(entry.getKey()+" "+entry.getValue());
			}
			
			System.out.println("反序列化成功！");
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
