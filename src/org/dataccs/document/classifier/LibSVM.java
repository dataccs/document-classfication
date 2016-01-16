package org.dataccs.document.classifier;

import java.io.IOException;

import org.dataccs.document.classify.Evaluation;
import org.dataccs.document.processor.config.Configuration;

import svmHelper.svm_predict;
import svmHelper.svm_scale;
import svmHelper.svm_train;

public class LibSVM {
	
	private Configuration config = null;
	
	public LibSVM(Configuration config){
		this.config = config;
	}

	public void train(){
		//scale参数
		String[] sarg = {"-l","0","-s","trainfile/svm.scale","-o","trainfile/svmscale.train","trainfile/svm.train"};
		//train参数
		String[] arg = {"-t","0","trainfile/svmscale.train","trainfile/svm.model"};
		try{
			System.out.println("训练-开始缩放");
			svm_scale scale = new svm_scale();
			scale.main(sarg);
			System.out.println("训练-缩放结束");
			
			System.out.println("训练开始");
			svm_train.main(arg);
			System.out.println("训练结束");
			
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void classify(){
		//scale参数
		String[] sarg = {"-l","0","-s","testfile/svm.scale","-o","testfile/svmscale.test","testfile/svm.test"};
		//predict参数
		String[] parg = {"testfile/svmscale.test","trainfile/svm.model","testfile/result.txt"};
		
		try {
			System.out.println("测试-开始缩放");
			svm_scale scale = new svm_scale();
			scale.main(sarg);
			System.out.println("测试-缩放结束");
			
			System.out.println("测试开始");
			svm_predict.main(parg);
			Evaluation.evaluate(parg[2]);
			System.out.println("测试结束");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
