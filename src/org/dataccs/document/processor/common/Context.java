package org.dataccs.document.processor.common;

import java.io.File;
import java.util.*;

/**
 * Context是用来在不同模块之间
 * 共享训练中计算得到的中间结果
 * @author dataccs
 */
public class Context {

	// 文档总数
	private int totalDocuCount = 0;
	// 类别
	private List<Label> labels = null;
	// Map<类别，文档数量>
	private Map<Label, Integer> labeledTotalDocuCountMap = null;
	// Map<类别，Map<文档，Map<词，词信息>>>
	private Map<String, Map<File,Map<String,Term>>> termsTable = null;
	// Feature Word
	private Map<String, Integer> featureWordMap = null;
	
	public int getTotalDocuCount() {
		return totalDocuCount;
	}
	public void setTotalDocuCount(int totalDocuCount) {
		this.totalDocuCount = totalDocuCount;
	}
	
	public void setLabels(List<Label> labels){
		this.labels = labels;
	}
	public List<Label> getLabels(){
		return labels;
	}
	
	public Map<Label, Integer> getLabeledTotalDocuCountMap() {
		return labeledTotalDocuCountMap;
	}
	public void setLabeledTotalDocuCountMap(
			Map<Label, Integer> labeledTotalDocuCountMap) {
		this.labeledTotalDocuCountMap = labeledTotalDocuCountMap;
	}
	
	public Map<String, Map<File, Map<String, Term>>> getTermsTable() {
		return termsTable;
	}
	public void setTermsTable(Map<String, Map<File, Map<String, Term>>> termsTable) {
		this.termsTable = termsTable;
	}
	
	public Map<String, Integer> getFeatureWordMap() {
		return featureWordMap;
	}
	public void setFeatureWordMap(Map<String, Integer> featureWordMap) {
		this.featureWordMap = featureWordMap;
	}
	
	
}
