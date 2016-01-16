package org.dataccs.document.transfertraindata;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.dataccs.document.processor.common.Context;
import org.dataccs.document.processor.common.Label;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;
import org.dataccs.document.processor.utils.SerializeObjectIntoFile;
import org.dataccs.document.processor.utils.WriteDataIntoFile;

public class TransferTrainData {

	private Configuration config = null;
	private Context context = null;
	
	// 文档总数
	private int totalDocuCount;
	// 类别
	private List<Label> labels = null;
	// Map<类别，文档数量>
	private Map<Label, Integer> labeledTotalDocuCountMap= null;
	// Map<类别，Map<文档，Map<词，词信息>>>
	private Map<String, Map<File,Map<String,Term>>> termsTable = null;
	// Map<Feature,ID>
	private Map<String, Integer> featureWordMap = null;
	
	
	// Map<类别，Map<文档，Map<特征词，词信息>>>
	private Map<Label,Map<File,Map<String, Term>>> featureTermTable = new HashMap<Label, Map<File,Map<String,Term>>>();
	// Map<类别内容，类别>
	private Map<String, Label> labelsMap = new HashMap<String, Label>();
	// Map<File,该文档总词数>
	private Map<File, Integer> fileWordsCount = new HashMap<File, Integer>();
	// Map<featureWord, 语料库中包含该词的文档数>
	private Map<String, Integer> wordDocuCountsMap = new HashMap<String, Integer>();
	
	public TransferTrainData(Configuration config, Context ctx){
		this.config = config;
		this.context = ctx;
		totalDocuCount = this.context.getTotalDocuCount();
		labels = this.context.getLabels();
		labeledTotalDocuCountMap = this.context.getLabeledTotalDocuCountMap();
		termsTable = this.context.getTermsTable();
		featureWordMap = this.context.getFeatureWordMap();
		writeCountIntoFile(totalDocuCount);
	}
	
	
	private void writeCountIntoFile(int count){
		String count_str = String.valueOf(count);
		WriteDataIntoFile write = new WriteDataIntoFile("trainfile/totalDocu.count");
		write.write(count_str);
		write.close();
	}
	
	// 将语料库的数据跟特征词表进行比对，并计算其相应的TF-IDF值
	// 转换成SVM训练数据的格式
	public void transfer(){
		generateLabelsMap();
		generateWordDocuCountsMap();
		dimeReduction();
		computeEveryWordTFIDF();
	}
	
	private void generateLabelsMap(){
		for(Label label : labels){
			labelsMap.put(label.getLabel(), label);
		}
	}
	
	private void generateWordDocuCountsMap(){
		for(Entry<String, Integer> entry : featureWordMap.entrySet()){
			String featureWord = entry.getKey();
			int wordDocuCounter = 0;
			for(Entry<String, Map<File,Map<String,Term>>> entry0: termsTable.entrySet()){
				Map<File,Map<String,Term>> fileTerms = entry0.getValue();
				for(Entry<File,Map<String,Term>> entry1: fileTerms.entrySet()){
					Map<String,Term> terms = entry1.getValue();
					if(terms.containsKey(featureWord)){
						wordDocuCounter++;
					}
				}
			}
			wordDocuCountsMap.put(featureWord, wordDocuCounter);
		}
		// 将wordDocuCountsMap对象序列化到本地磁盘文件中
		File file = new File("trainfile/wordDocuCountsMap.seria");
		SerializeObjectIntoFile.serialize(wordDocuCountsMap, file);
	}
	
	// 降维
	private void dimeReduction(){
		// 对照特征词进行降维
		for(Entry<String, Map<File,Map<String,Term>>> entry0: termsTable.entrySet()){
			Map<File,Map<String,Term>> fileTerms = entry0.getValue();
			for(Entry<File,Map<String,Term>> entry1: fileTerms.entrySet()){
				File file = entry1.getKey();
				int featureWordsCount = 0;
				Map<String,Term> terms = entry1.getValue();
				for(Entry<String, Term> entry2: terms.entrySet()){
					String word = entry2.getKey();
					Term term = entry2.getValue();
					if(featureWordMap.containsKey(word)){
						featureWordsCount += term.getFreq();
					} 
				}
				fileWordsCount.put(file, featureWordsCount);
			}
		}
	}
	
	private void computeEveryWordTFIDF(){
		// 计算每个降维后文档里词的tf-idf值，并输出到svm.train文件里
		WriteDataIntoFile writer = new WriteDataIntoFile("trainfile/svm.train");
		double tfIdf = 0.0;
		for(Entry<String, Map<File,Map<String,Term>>> entry0: termsTable.entrySet()){
			String label_str = entry0.getKey();
			Label label = labelsMap.get(label_str);
			Map<File,Map<String,Term>> fileTerms = entry0.getValue();
			for(Entry<File,Map<String,Term>> entry1: fileTerms.entrySet()){
				System.out.print(label.getId()+" ");
				writer.write(label.getId()+" ");
				File file = entry1.getKey();
				Map<String,Term> terms = entry1.getValue();
				for(Entry<String, Integer> entry2: featureWordMap.entrySet()){
					String featureWord = entry2.getKey();
					int featureWordId = entry2.getValue();
					if(terms.containsKey(featureWord)){
						tfIdf = getTfIdf(featureWord, file, terms);
					} else {
						tfIdf = 0.0;
					}
					System.out.print(featureWordId+":"+tfIdf+" ");
					writer.write(featureWordId+":"+tfIdf+" ");
				}
				System.out.println();
				writer.newLine();
			}
		}
		writer.close();
	}
	
	private double getTfIdf(String featureWord, File file, Map<String,Term> terms){
		Term featureTerm = terms.get(featureWord);
		double tf = (double)featureTerm.getFreq()/fileWordsCount.get(file);
		double idf = Math.log((double)totalDocuCount/
				(wordDocuCountsMap.get(featureWord))+1);
		double tfIdf = tf * idf;
		return tfIdf;
	}
}
