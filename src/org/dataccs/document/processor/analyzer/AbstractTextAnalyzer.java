package org.dataccs.document.processor.analyzer;

import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.dataccs.document.interfacce.TextAnalyzer;

/***
 * 抽象文本分词器，将停用词表装载到内存中
 * @author dataccs
 */
public abstract class AbstractTextAnalyzer implements TextAnalyzer {
	
	private String stopWordsFilePath = "userDir/stopWords.txt";
	private final Set<String> stopWords = new HashSet<String>();
	protected String charSet = "UTF-8";
	
	public AbstractTextAnalyzer(){
		super();
		File file = new File(stopWordsFilePath);
		loadFile(file);
	}
	
	//判断word是否是停用词
	public boolean isStopWord(String word){
		if(word!=null){
			word = word.trim();
			if(word.isEmpty()){
				return true;
			} else {
				return stopWords.contains(word);
			}
		}
		return true;
	}
	
	//载入停用词表到内存中
	private void loadFile(File file){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String word = null;
			while((word = reader.readLine()) != null){
				word = word.trim();
				if(!word.isEmpty()){
					if(!stopWords.contains(word)){
						stopWords.add(word);
					}
				}
			}
			
		}catch (FileNotFoundException e) {
			System.out.println("系统找不到该文件："+file.getAbsolutePath());
			e.printStackTrace();
			
		}catch(IOException e){
			e.printStackTrace();
			
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
