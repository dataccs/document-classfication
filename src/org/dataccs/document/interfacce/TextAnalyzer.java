package org.dataccs.document.interfacce;

import java.util.Map;
import java.io.File;

import org.dataccs.document.processor.common.Term;

/***
 * TextAnalyzer是分词器接口
 * 输入的是file对象
 * 输出的是该file的term map集合
 * @author dataccs
 */
public interface TextAnalyzer {

	Map<String, Term> analyze(File file);
}
