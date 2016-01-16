package org.dataccs.document.processor.test;

import java.util.*;

import org.junit.Test;

public class TestHashMapRepeatRead {

	Map<String,Map<Integer,String>> map = new HashMap<String, Map<Integer,String>>();
	@Test
	public void repeatRead(){
		
		Map<Integer,String> mapSite = new HashMap<Integer, String>();
		mapSite.put(1, "aaa");
		map.put("a", mapSite);
		map.put("b", mapSite);
		for(int i=0; i<10;i++){
			Map<Integer,String> mapSite1 = map.get("a");
			System.out.println(mapSite1.size());
		}
	}
}
