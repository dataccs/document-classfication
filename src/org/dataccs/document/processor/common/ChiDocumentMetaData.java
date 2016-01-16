package org.dataccs.document.processor.common;

import java.util.*;

public class ChiDocumentMetaData extends DocumentMetaData{
	
	// Map<label, Map<word, term>>
	private final Map<Label, Map<String, Term>> chiLabelToWordsVectorsMap = new HashMap<Label, Map<String, Term>>();
	// Map<word, term>, finally merged vector
	private final Map<String, Term> chiMergedTermVectorMap = new HashMap<String, Term>();

	public ChiDocumentMetaData(){
		
	}

	
}
