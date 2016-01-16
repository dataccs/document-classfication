package org.dataccs.document.featurecollector;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.dataccs.document.processor.analyzer.AbstractTextAnalyzer;
import org.dataccs.document.processor.analyzer.IctclasTextAnalyzer;
import org.dataccs.document.processor.common.Context;
import org.dataccs.document.processor.common.Label;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;
import org.dataccs.document.processor.utils.Result;
import org.dataccs.document.processor.utils.SortUtils;
import org.dataccs.document.processor.utils.WriteDataIntoFile;
import org.dataccs.word2vec.process.Word2VEC;

public class ChiFeatureWordCollector {
	
	private Configuration config = null;
	private Context context = null;
	private int totalDocuCount;//训练数据集文档总数
	private String train_root_dir = null;
	private File[] dirs = null;
	
	private AbstractTextAnalyzer textAnalyzer;
	
	// 类别
	private final List<Label> labels = new ArrayList<Label>();
	
	private final Map<String,Label> labelMap = new HashMap<String, Label>();
	// Map<类别，文档数量>
	private Map<Label, Integer> labeledTotalDocuCountMap = new HashMap<Label, Integer>();
	// Map<类别，Map<文档，Map<词，词信息>>>
	private Map<String, Map<File,Map<String,Term>>> termsTable = new HashMap<String, Map<File,Map<String,Term>>>();
	// Map<类别，未计算Chi值之前的词>
	private Map<String, Set<String>> labelWordSet = new HashMap<String, Set<String>>();
	// Map<类别，计算Chi值之后的词>
	private Map<String, Map<String,Term>> chiLabelTerm = new HashMap<String, Map<String,Term>>();
	// Set<word>, 经过计算之后，合并所有类别得到的特征词
	private final Set<String> chiMergedTermMap = new HashSet<String>();
	// Set<word>，根据word2vec词向量扩充后的特征词,2016/1/5新添加
	private final Set<String> extendChiMergedFeatureWords = new HashSet<String>();
	// Map<id，特征词>
	private final Map<String, Integer> featureWords = new HashMap<String, Integer>();
	// Map<类别,文档数>
	private Map<String, Integer> labelDocuCounts = new HashMap<String, Integer>();
	
	public ChiFeatureWordCollector(Configuration config, Context context){
		this.config = config;
		this.context = context;
	}
	public void select(){
		totalDocuCount = getTotalDocuCountAndLabels();//训练数据集文档总数
		context.setTotalDocuCount(totalDocuCount);
		analyze();
		computeWordChi();
		mergeFeatureWords();
	}
	
	// Map<文件，类别>
	private Map<File, Label> fileLabelMap = new HashMap<File, Label>();
	
	private int getTotalDocuCountAndLabels(){
		WriteDataIntoFile writer = new WriteDataIntoFile("trainfile/labels.txt");
		int totalDocuCount = 0;
		train_root_dir = config.get("train.root.directory");
		File files = new File(train_root_dir);
		dirs = files.listFiles();
		int i =1;
		for(File dir: dirs){
			
			File[] label_files = dir.listFiles();
			totalDocuCount += label_files.length;
			
			writer.write(i+" "+dir.getName());
			writer.newLine();
			
			Label label = new Label();
			label.setId(i++);
			label.setLabel(dir.getName());
			labels.add(label);
			labelMap.put(dir.getName(), label);
			//类别，对应的文档数量
			labeledTotalDocuCountMap.put(label, label_files.length);
			
			for(File file: label_files){
				fileLabelMap.put(file, label);
			}
		}
		context.setLabeledTotalDocuCountMap(labeledTotalDocuCountMap);
		context.setLabels(labels);
		writer.close();
		return totalDocuCount; 
	}
	
	
		
	private void analyze(){
		textAnalyzer = new IctclasTextAnalyzer(config);
		for(File dir: dirs){
			File[] files = dir.listFiles();
			Map<File,Map<String, Term>> fileWordTermMap = new HashMap<File, Map<String,Term>>();
			Set<String> wordSet = new HashSet<String>();
			for(File file: files){
				Map<String, Term> terms = textAnalyzer.analyze(file);
				fileWordTermMap.put(file, terms);
				Iterator<Entry<String,Term>> iter = terms.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Term> it = iter.next();
					String word = it.getKey();
					if(!wordSet.contains(word)){
						wordSet.add(word);
					}
				}
			}
			labelWordSet.put(dir.getName(), wordSet);
			termsTable.put(dir.getName(), fileWordTermMap);
			labelDocuCounts.put(dir.getName(), fileWordTermMap.size());
		}
		context.setTermsTable(termsTable);
	}
	
	
	private void computeWordChi(){
		System.out.println("开始计算每个词语的CHI值...");
		for(Entry<String, Set<String>> wordSet: labelWordSet.entrySet()){
			String label = wordSet.getKey();
			Map<String,Term> wordTermMap = new HashMap<String, Term>();
			System.out.println("正在计算类别["+label+"]每个词语的CHI值...");
			for(String word: wordSet.getValue()){
				double chi = processOneLabel(label, word);
				Term term = new Term(word);
				term.setChi(chi);
				wordTermMap.put(word, term);
			}
			chiLabelTerm.put(label, wordTermMap);
		}
		System.out.println("结束计算每个词语的CHI值。");
	}
	
	private void mergeFeatureWords(){
		int topN = config.getInt("each.label.kept.term.count");
		for(Entry<String, Map<String,Term>> chiTerm: chiLabelTerm.entrySet()){
			Result result = sort(chiTerm.getValue(), topN);
			for (int i = result.getStartIndex(); i <= result.getEndIndex(); i++) {
				Entry<String, Term> termEntry = result.get(i);
				// merge CHI terms for all labels
				chiMergedTermMap.add(termEntry.getKey());
			}
		}

		boolean hasWord2vec = config.getBoolean("feature.collector.hasword2vec");
		// 如果开启word2vec特征词扩充功能，则执行扩充
		if(hasWord2vec){
			Word2VEC word2vec = new Word2VEC();
			int nearestWordNum = config.getInt("nearestWordNum"); // 取最相近的词的个数
			// 此处对原特征词根据word2vec词向量进行扩充，扩充比例为1:nearestWordNum
			for(String featureword : chiMergedTermMap){
				Set<String> nearestFeatureword = word2vec.
						getNearestWordSet(featureword, nearestWordNum);
				extendChiMergedFeatureWords.add(featureword);
				extendChiMergedFeatureWords.addAll(nearestFeatureword);
			}
		}
		// TODO 此处优化有问题，导致分类准确率下降
		// tip:不能一边遍历Set一边删除，需要借助iterator实现遍历删除元素的目的
		/*Iterator<String> it = chiMergedTermMap.iterator();
		while(it.hasNext()){
			// 特征词在所有类别中都出现的次数
			int counter = 0;
			String featureWord = it.next();
			for(Entry<String, Map<String,Term>> chiTerm: chiLabelTerm.entrySet()){
				if(chiTerm.getValue().containsKey(featureWord)){
					counter++;
				}
			}
			//如果特征词在所有类别中都出现了，则说明该特征词没有区分能力，故将其从特征词表中删除
			 此处需要注意的是：
			 * 之前将特征词设为只要出现的次数占总类别的75%则将其删除，导致分类准确率迅速从84.6%迅速降到69.8%
			 * 分析其原因主要有两点：
			 * 1.这样很容易删除很多特征词，导致特征词表里的词数降到之前的1/3,维度降低很多，导致分类准确率下降较多；
			 * 2.设置75%或者80%、90%都是不合理的,因为有的类别是靠不出现该词作为分类时的依据，所以应该将其设为100%
			if(counter == 1){
				it.remove();
			}
		}*/
		
		// 将数据写入文件
		WriteDataIntoFile writer = new WriteDataIntoFile("trainfile/featurewords.txt");
		Iterator<String> iter = null;
		if(hasWord2vec){
			iter = extendChiMergedFeatureWords.iterator();
		}else{
			iter = chiMergedTermMap.iterator();
		}
		int id =1;
		while(iter.hasNext()){
			String word = iter.next();
			featureWords.put(word, id);
			writer.write(id+" "+word);
			writer.newLine();
			id++;
		}
		context.setFeatureWordMap(featureWords);
		writer.close();
	}
	
	private Result sort(Map<String, Term> terms, int topN) {
		SortUtils sorter = new SortUtils(terms, true, topN);
		Result result = sorter.heapSort();
		return result;
	}
	
	private double processOneLabel(String label, String word){
		// N:文档总数
		int N = totalDocuCount;
		
		// A：在一个类别中，包含某个词的文档的数量
		int A = getA(label, word);
		
		// B：在一个类别中，排除该类别，其他类别包含某个词的文档的数量
		int B = getB(label, word);
		
		// C：在一个类别中，不包含某个词的文档的数量（int C = getC(label, word);）
		int C = labelDocuCounts.get(label)-A;//此处进行了优化，该类别的文档总数=A+C
		
		// D：在一个类别中，排除该类别，其它类别不包含某个词的文档的数量（int D = getD(label, word);）
		int D = totalDocuCount-labelDocuCounts.get(label)-B;//此处进行了优化，排除该类别的文档总数=B+D
		
		int temp = (A*D-B*C);
		double chi = (double) N*temp*temp / ((A+C)*(A+B)*(B+D)*(C+D));
		return chi;
	}
	
	// 在一个类别中，包含某个词的文档的数量
	private int getA(String label, String word){
		int docCountContainingWordInLabel = 0;
		Map<File,Map<String,Term>> fileWordMap = termsTable.get(label);
		for(Entry<File,Map<String,Term>> entryA: fileWordMap.entrySet()){
			if(entryA.getValue().containsKey(word)){
				docCountContainingWordInLabel++;
			}
		}
		return docCountContainingWordInLabel;
	}
	
	// 在一个类别中，排除该类别，其他类别包含某个词的文档的数量
	private int getB(String label, String word){
		int docCountContainingWordNotInLabel = 0;
		for(Entry<String, Map<File,Map<String,Term>>> termTable: termsTable.entrySet()){
			String labelT = termTable.getKey();
			if(!label.equals(labelT)){
				Map<File,Map<String,Term>> fileWordMap = termTable.getValue();
				for(Entry<File,Map<String,Term>> entryB: fileWordMap.entrySet()){
					if(entryB.getValue().containsKey(word)){
						docCountContainingWordNotInLabel++;
					}
				}
			}
		}
		return docCountContainingWordNotInLabel;
	}
	
	// 在一个类别中，不包含某个词的文档的数量
	private int getC(String label, String word){
		int docCountNotContainingWordInLabel = 0;
		Map<File,Map<String,Term>> fileWordMap = termsTable.get(label);
		for(Entry<File,Map<String,Term>> entryC: fileWordMap.entrySet()){
			if(!entryC.getValue().containsKey(word)){
				docCountNotContainingWordInLabel++;
			}
		}
		return docCountNotContainingWordInLabel;
	}
	
	// 在一个类别中，不包含某个词也不在该类别中的文档的数量
	private int getD(String label, String word){
		int docCountNotContainingWordNotInLabel = 0;
		for(Entry<String, Map<File,Map<String,Term>>> termTable: termsTable.entrySet()){
			String labelT = termTable.getKey();
			if(!label.equals(labelT)){
				Map<File,Map<String,Term>> fileWordMap = termTable.getValue();
				for(Entry<File,Map<String,Term>> entryD: fileWordMap.entrySet()){
					if(!entryD.getValue().containsKey(word)){
						docCountNotContainingWordNotInLabel++;
					}
				}
			}
		}
		return docCountNotContainingWordNotInLabel;
	}
}
