package org.dataccs.document.processor.test;

import java.util.*;

import org.junit.Test;

public class TestMapGetKey {

	@Test
	public void test(){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int num = (int)map.get(12);
		System.out.println(num);
	}
}
