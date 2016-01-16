package org.dataccs.document.processor.test;

import java.util.Set;

import org.dataccs.word2vec.process.Word2VEC;
import org.junit.Test;

public class testWord2VEC {

	@Test
	public void test(){
		Word2VEC word2vec = new Word2VEC();
		Set<String> sets = word2vec.getNearestWordSet("月亮", 3);
		for(String word : sets){
			System.out.println(word);
		}
		System.out.println("------------------");
		Set<String> sets1 = word2vec.getNearestWordSet("火星", 3);
		for(String word : sets1){
			System.out.println(word);
		}
	}
}
