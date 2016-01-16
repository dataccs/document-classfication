package org.dataccs.document.processor.utils;

import java.util.Map.Entry;

import org.dataccs.document.processor.common.Term;


public class Result {
	private Entry<String, Term>[] array;
	private int startIndex;
	private int endIndex;
	
	public Entry<String, Term>[] getArray() {
		return array;
	}
	public void setArray(Entry<String, Term>[] array) {
		this.array = array;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public Entry<String, Term> get(int index) {
		return array[index];
	}
}
