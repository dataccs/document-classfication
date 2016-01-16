package org.dataccs.document.train;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dataccs.document.classify.Classify;
import org.dataccs.document.processor.config.Configuration;
import org.dataccs.document.processor.utils.WriteDataIntoFile;

/**
 * 训练模型入口
 * @author dataccs
 */
public class Train {

	
	public static void main(String[] args) {
		
		Date begin = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("训练开始时间: "+dateFormat.format(begin));
		
		// 根据配置文件读取配置信息
		Configuration config = new Configuration("configuration/config.properties");
		// 将configuration传给训练模型
		TrainModel model = new TrainModel(config);
		// 训练
		model.train();
		
		Date end = new Date();
		System.out.println("训练结束时间： "+dateFormat.format(end));
		int sec = (int)(end.getTime() - begin.getTime())/(1000);
		System.out.println("训练耗时："+sec+"秒（约"+(sec/60)+"分钟）");
		
		WriteDataIntoFile writer = new WriteDataIntoFile("trainfile/traintime.txt");
		writer.write("训练耗时："+sec+"秒（约"+(sec/60)+"分钟）");
		writer.close();
		Classify.main(args);
	}

}
