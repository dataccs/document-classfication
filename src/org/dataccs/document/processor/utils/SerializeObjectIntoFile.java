package org.dataccs.document.processor.utils;

import java.io.*;

public class SerializeObjectIntoFile {

	
	public static void serialize(Object obj, File file){
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try{
				if(fos!=null){
					fos.close();
				}
				if(oos!=null){
					oos.close();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
