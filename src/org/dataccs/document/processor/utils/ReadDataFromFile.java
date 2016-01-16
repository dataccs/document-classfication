package org.dataccs.document.processor.utils;

import java.io.*;

public class ReadDataFromFile {

	private File file = null;
	
	public ReadDataFromFile(String path){
		file = new File(path);
	}
	
	public String read(){
		String str = "";
		FileReader in = null;
		BufferedReader br = null;
		try{
			in = new FileReader(file);
			br = new BufferedReader(in);
			String line = "";
			while((line=br.readLine())!=null){
				str+=line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
				if(in!=null){
					in.close();
				}
				if(br!=null){
					br.close();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		return str.trim();
	}
}
