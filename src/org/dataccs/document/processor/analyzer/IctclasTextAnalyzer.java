package org.dataccs.document.processor.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.dataccs.document.interfacce.TextAnalyzer;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;

import ICTCLAS.I3S.AC.ICTCLAS50;

/***
 * ICTCLAS分词器
 * @author dataccs
 */
public class IctclasTextAnalyzer extends AbstractTextAnalyzer{
	
	private ICTCLAS50 ictclas = null;
	
	public IctclasTextAnalyzer(Configuration config){
		super();
		IctclasInit();
		charSet = config.get("charset");
	}
	
	/*** ICTCLAS分词器初始化 */
	private void IctclasInit(){
		try{
			ictclas = new ICTCLAS50();
			String argu = ".";
			if((ictclas.ICTCLAS_Init(argu.getBytes(charSet))) == false){
				System.out.println("ICTCLAS init failed!");
			} else {
				System.out.println("ICTCLAS init succeed!");
			}
			
			//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			ictclas.ICTCLAS_SetPOSmap(2);
			String userdict = "userdict.txt";
			
			/* 第一个参数为用户字典路径，
    		 * 第二个参数为用户字典的编码类型(0:type unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5)
    		 */ 
			int nCount = ictclas.ICTCLAS_ImportUserDictFile(userdict.getBytes(), 0);
			System.out.println("用户词典数目"+nCount);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Term> analyze(File file) {
		String doc = file.getAbsolutePath();
		System.out.println("Analyze document : file = "+doc);
		Map<String, Term> terms = new HashMap<String, Term>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
			StringBuffer strbuff = new StringBuffer();
			String line = null;
			while((line = reader.readLine()) != null){
				strbuff.append(line);
			}
			String context_document = strbuff.toString();
			
			//文件分词(第一个参数为文件编码类型,第三个参数为是否标记词性集 1 yes ,0 no) 
            //第二个参数为用户字典的编码类型(0:type unknown;1:ASCII码;2:GB2312,GBK,GB10380;3:UTF-8;4:BIG5) 
            byte nativeBytes[] = ictclas.ICTCLAS_ParagraphProcess(context_document.getBytes(charSet), 0, 1);
            String nativeStr = new String(nativeBytes, 0, nativeBytes.length, charSet);
            String[] rawWords = nativeStr.split("\\s+");
            for(String rawWord : rawWords){
            	String[] wordPro = rawWord.split("/");
            	if(wordPro.length == 2){
            		String word = wordPro[0];//词的内容
            		String lexicalCategory = wordPro[1];//词性
            		//判断是否为停用词，如果是停用词则忽略
            		if( !isStopWord(word) ){
            			//只取名词，且删掉单字词
            			if(lexicalCategory.startsWith("n") && word.length()>=2){
	            			Term term = terms.get(word);
	            			if(term == null){
	            				term = new Term(word);
	            				term.setLexicalCategory(lexicalCategory);
	            				terms.put(word, term);
	            			}
	            			term.increaseFreq();
            			}
            		}
            	}
            }
            
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return terms;
	}
	
}
