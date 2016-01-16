package org.dataccs.document.processor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * 语料库的词袋模型所需要的基本信息
 * 抽象类
 * @author dataccs
 */
public abstract class DocumentMetaData {
	
	// 文档总数
	private int totalDocuCount;
	// 类别
	private final List<Label> labels = new ArrayList<Label>();
	// Map<类别，文档数量>
	private Map<Label, Integer> labeledTotalDocuCountMap= new HashMap<Label, Integer>();
	// Map<类别，Map<文档，Map<词，词信息>>>
	private Map<Label, Map<String,Map<String,Term>>> termsTable = new HashMap<Label, Map<String,Map<String,Term>>>();
	// Map<词，Map<类别，Set<文档>>>
	private Map<String,Map<Label,Set<String>>> wordTable = new HashMap<String,Map<Label,Set<String>>>();
	
	public DocumentMetaData(){
		super();
	}
	
	public void setTotalDocuCount(int totalDocuCount) {
		this.totalDocuCount = totalDocuCount;
	}
	public int getTotalDocuCount() {
		return totalDocuCount;
	}
	public List<Label> getLabels() {
		return labels;
	}
	public void addLabel(Label label) {
		labels.add(label);
	}
	
	public Map<Label, Integer> getLabeledTotalDocuCountMap() {
		return labeledTotalDocuCountMap;
	}
	public void addLabeledTotalDocuCount(Label label, int count) {
		labeledTotalDocuCountMap.put(label, count);
	}
	
	public Map<Label, Map<String, Map<String, Term>>> getTermsTable() {
		return termsTable;
	}
	public void addTerms(Label label, String file, Map<String,Term> terms) {
		Map<String,Map<String,Term>> file_terms = new HashMap<String, Map<String,Term>>();
		file_terms.put(file, terms);
		termsTable.put(label,file_terms);
	}
	
	public Map<String, Map<Label, Set<String>>> getWordTable() {
		return wordTable;
	}
	public void addTermToWordTable(Term term, Label label, String file) {

	}
	
	
}
