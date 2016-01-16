package org.dataccs.document.processor.test;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import org.dataccs.document.processor.analyzer.IctclasTextAnalyzer;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;
import org.junit.*;


public class TestIctclasAnalyzer {

	@Test
	public void analyze(){
		Configuration config = new Configuration("configuration/config.properties");
		IctclasTextAnalyzer ictclas = new IctclasTextAnalyzer(config);
		File file = new File("E:/article/C000007/10.txt");
		Map<String, Term> terms = ictclas.analyze(file);
		for(Entry<String, Term> entry: terms.entrySet()){
			Term term = entry.getValue();
			System.out.println(entry.getKey()+" "+term.getLexicalCategory()+" "+term.getFreq());
		}
		
	}
	
}
