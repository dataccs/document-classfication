package org.dataccs.document.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dataccs.document.processor.analyzer.IctclasTextAnalyzer;
import org.dataccs.document.processor.common.Label;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;
import org.dataccs.document.processor.utils.DeSerializeObjectFromFile;
import org.dataccs.document.processor.utils.ReadDataFromFile;
import org.dataccs.document.processor.utils.WriteDataIntoFile;

public class ClassificationData {

	private Configuration config = null;
	
	// 训练集文档总数
	private int totalDocuCount = 0;
	
	private File[] dirs = null;
	// Map<类别，id>
	private final Map<String, Integer> label = new HashMap<String, Integer>();
	// Map<特征词，id>
	private final Map<String, Integer> featureWordMap = new HashMap<String, Integer>();
	// 待分类的文档，Map<类别，Map<文档，Map<词，词信息>>>
	private Map<String, Map<File,Map<String,Term>>> termsTable = new HashMap<String, Map<File,Map<String,Term>>>();
	
	// Map<featureWord, 训练语料库中包含该词的文档数>
	private Map<String, Integer> wordDocuCountsMap = new HashMap<String, Integer>();
	
	public ClassificationData(Configuration config){
		this.config = config;
		File files = new File(config.get("testDir"));
		dirs = files.listFiles();
		getTotalDocuCount();
		getWordDocuCountsMap();
	}
	
	// 得到训练集文档总数
	private void getTotalDocuCount(){
		ReadDataFromFile rdf = new ReadDataFromFile("trainfile/totalDocu.count");
		String result = rdf.read();
		totalDocuCount = Integer.parseInt(result);
	}
	
	// 得到Map<featureWord, 训练语料库中包含该词的文档数>
	private void getWordDocuCountsMap(){
		File file = new File("trainfile/wordDocuCountsMap.seria");
		wordDocuCountsMap = (HashMap<String, Integer>) DeSerializeObjectFromFile.deSerialize(file);
	}
	
	public void generateSVMData(){
		loadClassLabel();
		loadFeatureWords();
		analyze();
		computeTfIdf();
	}
	
	private void loadClassLabel(){
		File file = new File("trainfile/labels.txt");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String str = "";
			while((str=br.readLine())!=null){
				str = str.trim();
				if(str!="" && str!=null){
					String[] strArray = str.split(" ");
					int labelID = Integer.parseInt(strArray[0]);
					String labelStr = strArray[1];
					label.put(labelStr, labelID);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadFeatureWords(){
		File file = new File("trainfile/featurewords.txt");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String str = "";
			while((str=br.readLine())!=null){
				str = str.trim();
				if(str!="" && str!=null){
					String[] strArray = str.split(" ");
					int featureWordID = Integer.parseInt(strArray[0]);
					String featureWordStr = strArray[1];
					featureWordMap.put(featureWordStr, featureWordID);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void analyze(){
		IctclasTextAnalyzer textAnalyzer = new IctclasTextAnalyzer(config);
		for(File dir: dirs){
			File[] files = dir.listFiles();
			Map<File,Map<String, Term>> fileWordTermMap = new HashMap<File, Map<String,Term>>();
			Set<String> wordSet = new HashSet<String>();
			for(File file: files){
				Map<String, Term> terms = textAnalyzer.analyze(file);
				Map<String, Term> termsDimenReduction = new HashMap<String, Term>();
				// 降维
				for(Entry<String, Term> term : terms.entrySet()){
					if(featureWordMap.containsKey(term.getKey())){
						termsDimenReduction.put(term.getKey(), term.getValue());
					}
				}
				fileWordTermMap.put(file, termsDimenReduction);
			}
			termsTable.put(dir.getName(), fileWordTermMap);
		}
	}
	
	private void computeTfIdf(){
		// 计算每个降维后文档里词的tf-idf值，并输出到svm.test文件里
		WriteDataIntoFile writer = new WriteDataIntoFile("testfile/svm.test");
		double tfIdf = 0.0;
		for(Entry<String, Map<File,Map<String,Term>>> entry0: termsTable.entrySet()){
			String label_str = entry0.getKey();
			int labelID = label.get(label_str);
			Map<File,Map<String,Term>> fileTerms = entry0.getValue();
			for(Entry<File,Map<String,Term>> entry1: fileTerms.entrySet()){
				System.out.print(labelID+" ");
				writer.write(labelID+" ");
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
		int fileWordCountSum = getFileWordCountSum(terms);
		Term featureTerm = terms.get(featureWord);
		double tf = (double)featureTerm.getFreq()/fileWordCountSum;
		double idf = Math.log((double)totalDocuCount/
				(wordDocuCountsMap.get(featureWord))+1);
		double tfIdf = tf * idf;
		return tfIdf;
	}
	
	private int getFileWordCountSum(Map<String,Term> terms){
		int count = 0;
		for(Entry<String, Term> entry : terms.entrySet()){
			Term term = entry.getValue();
			count += term.getFreq();
		}
		return count;
	}
}
