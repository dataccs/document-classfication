package org.dataccs.document.processor.utils;

import java.io.*;

public class WriteDataIntoFile {

	private File file = null;
	private FileWriter fw = null;
	private BufferedWriter buffWriter = null;
	public WriteDataIntoFile(String filePath){
		this.file = new File(filePath);
		try{
			// 如果文件存在，先删除再重新创建
			if(this.file.exists()){
				this.file.delete();
			}
			this.file.createNewFile();
			fw= new FileWriter(file,true); // 第二个参数：true（追加写入），false(不追加写)
			buffWriter= new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void write(String data){
		try {
			buffWriter.write(data);
			buffWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newLine(){
		try {
			buffWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			fw.flush();
			buffWriter.flush();
			if(fw!=null){
				fw.close();
			}
			if(buffWriter!=null){
				buffWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
