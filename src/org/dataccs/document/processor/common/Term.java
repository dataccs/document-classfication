package org.dataccs.document.processor.common;

 /***
  * Term是词的对象，里面包含该词必要的诸如词内容，词频，tf,idf等属性和信息
  * @author dataccs
  */
public class Term {
	
	private int id;
	private String word;
	private String lexicalCategory = "unknown";//词性
	private int freq = 0; //词频
	private double tf; //tf
	private double idf; //idf
	private double tfIdf = 0; //tf-idf值
	private double chi = 0; //卡方统计量
	
	public Term(String word){
		super();
		this.word = word;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public String getLexicalCategory() {
		return lexicalCategory;
	}

	public void setLexicalCategory(String lexicalCategory) {
		this.lexicalCategory = lexicalCategory;
	}
	
	public int getFreq() {
		return freq;
	}
	public void increaseFreq() {
		freq++;
	}
	
	public double getTf() {
		return tf;
	}
	public void setTf(double tf) {
		this.tf = tf;
	}
	
	public double getIdf() {
		return idf;
	}
	public void setIdf(double idf) {
		this.idf = idf;
	}
	
	public double getTfIdf() {
		return tfIdf;
	}
	public void setTfIdf(double tfIdf) {
		this.tfIdf = tfIdf;
	}

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) {
		this.chi = chi;
	}
	
	@Override
	public int hashCode(){
		return this.word.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		Term term = (Term) o;
		return this.word.equals(term.getWord());
	}
	
}
