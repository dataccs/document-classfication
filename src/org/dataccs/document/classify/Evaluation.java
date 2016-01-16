package org.dataccs.document.classify;

import java.io.*;
import java.util.*;

import org.dataccs.document.processor.utils.WriteDataIntoFile;

public class Evaluation {
	
	//				二值列联表
	//-------------------------------------
	//				实际属于此类	实际不属于此类
	//系统判定属于此类		a			b
	//系统判定不属于此类		c			d
	//-------------------------------------
	//查准率（精度）		P = a/(a+b)
	//查全率（召回率）	R = a/(a+c)
	//F-测量值		F = 2P*R/(P+R)
	private static class PRF{
		double P;
		double R;
		double F;
	}
	
	
	// 类别个数
	private static int maxLabelID = 0;
	// Map<类别ID，P、R、F>
	private static Map<Integer, PRF> result = new HashMap<Integer, PRF>();
	// Map<类别ID，系统判定此类并且实际属于此类的个数(PRF.a)>
	private static Map<Integer, Integer> rightResult = new HashMap<Integer, Integer>();
	// Map<类别ID，系统判定属于此类个数(PRF.a+PRF.b)>
	private static Map<Integer, Integer> classifierResult = new HashMap<Integer, Integer>();
	// Map<类别ID，实际属于此类(PRF.a+PRF.c)>
	private static Map<Integer, Integer> trueResult = new HashMap<Integer, Integer>();
	
	// 对分类结果进行评估
	public static void evaluate(String classificationResultFilePath){
		File file = new File(classificationResultFilePath);
		FileReader in = null;
		BufferedReader br = null;
		try{
			in = new FileReader(file);
			br = new BufferedReader(in);
			String line = "";
			while((line=br.readLine())!=null){
				String[] arr = line.split(" ");
				int target = Integer.parseInt(arr[0]);
				int v = Integer.parseInt(arr[1]);
				if(maxLabelID < target){
					maxLabelID = target;
				}
				if(v == target){
					if(rightResult.get(target) == null){
						rightResult.put(target, 1);
					}else{
						int num = rightResult.get(target);
						rightResult.put(target, ++num);
					}
					
				}
				if(trueResult.get(target) == null){
					trueResult.put(target, 1);
				}else{
					int counter_true = trueResult.get(target);
					trueResult.put(target, ++counter_true);
				}
				if(classifierResult.get(v) == null){
					classifierResult.put(v, 1);
				}else{
					int counter_classifier = classifierResult.get(v);
					classifierResult.put(v, ++counter_classifier);
				}
			}
			calculate();
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
	} 
	
	private static void calculate(){
		WriteDataIntoFile writer = new WriteDataIntoFile("testfile/evaluation.txt");
		double average_P = 0;
		double average_R = 0;
		double average_F = 0;
		
		for(int labelID=1; labelID<=maxLabelID; labelID++){
			PRF prf = new PRF();
			prf.P = (double)rightResult.get(labelID)/classifierResult.get(labelID);
			average_P += prf.P;
			prf.R = (double)rightResult.get(labelID)/trueResult.get(labelID);
			average_R += prf.R;
			prf.F = (2 * prf.P * prf.R)/(prf.P + prf.R);
			average_F += prf.F;
			result.put(labelID, prf);
			writer.write(labelID+"\tP="+prf.P+"\tR="+prf.R+"\tF="+prf.F);
			writer.newLine();
		}
		average_P = average_P/maxLabelID;
		average_R = average_R/maxLabelID;
		average_F = average_F/maxLabelID;
		writer.write("average\tP="+average_P+"\tR="+average_R+"\tF="+average_F);
		writer.close();
	}
}
