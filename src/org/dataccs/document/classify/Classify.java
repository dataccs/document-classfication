package org.dataccs.document.classify;

import java.util.Date;

import org.dataccs.document.classifier.LibSVM;
import org.dataccs.document.processor.config.Configuration;

public class Classify {

	public static void main(String[] args){
		Date begin = new Date();
		Configuration config = new Configuration("configuration/config.properties");
		new ClassificationData(config).generateSVMData();
		Date generateSVMDataTime = new Date();
		new LibSVM(config).classify();
		Date end = new Date();
		long totalCostTime = end.getTime()-begin.getTime();
		long generateSVMDataCostTime = generateSVMDataTime.getTime()-begin.getTime();
		double percent = (generateSVMDataCostTime*1.0/totalCostTime)*100;
		System.out.println("完成分类任务总时间开销："+totalCostTime/1000+"秒（约"+totalCostTime/60000+"分钟)");
		System.out.println("其中预处理时间开销占总时间开销的："+percent+"%");
	}
}
