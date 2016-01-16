package org.dataccs.document.train;

import org.dataccs.document.classifier.LibSVM;
import org.dataccs.document.featurecollector.ChiFeatureWordCollector;
import org.dataccs.document.processor.common.Context;
import org.dataccs.document.processor.config.Configuration;
import org.dataccs.document.transfertraindata.TransferTrainData;

/**
 * 训练模型
 * @author dataccs
 */
public class TrainModel {
	
	private Configuration config = null;
	private final Context context = new Context();
	
	public TrainModel(Configuration config){
		this.config = config;
	}
	
	public void train(){
		// 利用卡方检验进行特征选择
		new ChiFeatureWordCollector(config, context).select();
		// 将训练语料库结合特征词进行降维并转换成分类器所需格式
		new TransferTrainData(config, context).transfer();
		// 利用LibSVM分类器对训练集进行训练，得到分类模型
		new LibSVM(config).train();
	}
	
}
