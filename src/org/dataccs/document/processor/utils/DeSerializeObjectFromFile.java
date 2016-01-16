package org.dataccs.document.processor.utils;

import java.io.*;

public class DeSerializeObjectFromFile {
	
	public static Object deSerialize(File file){
		Object obj = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} finally {
			try{
				if(fis!=null){
					fis.close();
				}
				if(ois!=null){
					ois.close();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		return obj;
	}

}
