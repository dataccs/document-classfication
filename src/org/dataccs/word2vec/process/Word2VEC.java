package org.dataccs.word2vec.process;

import java.io.*;
import java.util.*;  
import java.util.Map.Entry;

/**
 * 读取word2vec训练得到的向量文件
 * 计算每个词语之间的距离，从而得到词语的相似信息
 * @author dataccs
 */
public class Word2VEC {  
	// 提取的最相近词的数目
	private int topNSize = 1;  
	// 存放word2vec训练得到的词向量表
	private HashMap<String, float[]> wordMap = null;
	public Word2VEC(){
		wordMap = new HashMap<String, float[]>();
        loadModelBinary("D:/paperTest/vectors.corpus_3.1G.binary.bin");  
        //loadModelUTF8("D:/paperTest/vectors.corpus_3.1G.utf8.bin");
	}
	
    public Set<String> getNearestWordSet(String word, int nearestWordNum) { 
    	
    	if(nearestWordNum<=0){
    		return null;
    	}else{
    		this.topNSize = nearestWordNum;
        }
        Set<String> nearestWordSet = new HashSet<String>();
        Set<WordEntry> result = new TreeSet<WordEntry>();  
        result = distance(word);
        if(result!=null){
	        Iterator<WordEntry> iter = result.iterator();  
	        while (iter.hasNext()) {  
	            WordEntry resultWord = (WordEntry) iter.next();  
	            nearestWordSet.add(resultWord.name);
	        }  
        }
        /*System.out.println("*******************************");  
        System.out.println("Three word analysis");  
        result = vec.analogy("日本", "东京", "美国");  
        iter = result.iterator();  
        while (iter.hasNext()) {  
            WordEntry resultWord = (WordEntry) iter.next();  
            System.out.println(resultWord.name + " " + resultWord.score);  
        }*/ 
        return nearestWordSet;
    }  

    private int words;  
    private int size;  
   
  
    /** 
     * 加载模型（UTF8格式） 
     *  
     * @param path 模型的路径
     *  
     * @throws IOException 
     */  
    private void loadModelUTF8(String path){  
    	File file = new File(path);
    	FileInputStream fis = null;
    	InputStreamReader isr = null;
    	BufferedReader buff = null;
    	double len = 0;  
        float vector = 0;
    	try{
	    	fis = new FileInputStream(file);
	    	isr = new InputStreamReader(fis,"UTF-8");
	    	buff = new BufferedReader(isr);
	    	String firstLine = buff.readLine();
	    	String[] strs = firstLine.trim().split(" ");
	    	//读取总词数  
	        words = Integer.parseInt(strs[0]);  
	        //词向量大小  
	        size = Integer.parseInt(strs[1]);
	        
	        String word;  
            float[] vectors = null;  
            for (int i = 0; i < words; i++) {  
            	String line = buff.readLine();
            	String[] strArray = line.trim().split(" ");
                word = strArray[0];  
                vectors = new float[size];  
                len = 0;  
                for (int j = 1; j <= size; j++) {  
                    vector = Float.parseFloat(strArray[j]);  
                    len += vector * vector;  
                    vectors[j-1] = (float) vector;  
                }  
                len = Math.sqrt(len);  
  
                for (int j = 0; j < vectors.length; j++) {  
                    vectors[j] = (float) (vectors[j] / len);  
                }  
                wordMap.put(word, vectors);  
            }
    	} catch (IOException e){
        	e.printStackTrace();
        } finally {
        	try{
        		if(fis!=null) { fis.close(); }
	    		if(isr!=null) { isr.close(); }
	    		if(buff!=null) { buff.close(); }
	        } catch (IOException e) {
				e.printStackTrace();
			} 
    	}
        
    }
    
    
    /** 
     * 加载模型（二进制格式） 
     *  
     * @param path 模型的路径
     *  
     * @throws IOException 
     */  
    private void loadModelBinary(String path) {  
        DataInputStream dis = null;  
        BufferedInputStream bis = null;  
        double len = 0;  
        float vector = 0;  
        try {  
            bis = new BufferedInputStream(new FileInputStream(path));  
            dis = new DataInputStream(bis);  
            //读取总词数  
            words = Integer.parseInt(readString(dis));  
            //词向量大小   
            size = Integer.parseInt(readString(dis));  
  
            String word;  
            float[] vectors = null;  
            for (int i = 0; i < words; i++) {  
                word = readString(dis);  
                vectors = new float[size];  
                len = 0;  
                for (int j = 0; j < size; j++) {  
                    vector = readFloat(dis);  
                    len += vector * vector;  
                    vectors[j] = (float) vector;  
                }  
                len = Math.sqrt(len);  
  
                for (int j = 0; j < vectors.length; j++) {  
                    vectors[j] = (float) (vectors[j] / len);  
                }  
                wordMap.put(word, vectors);  
                dis.read();  
            }  
  
        }catch (IOException e){
        	e.printStackTrace();
        }finally {  
            try {
            	if(bis!=null){ bis.close(); }
            	if(dis!=null){ dis.close(); }
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }  
    }  
  
    private static final int MAX_SIZE = 50;  
  
    /** 
     * 得到近义词 
     *  
     * @param word 
     * @return 
     */  
    private Set<WordEntry> distance(String word) {  
        float[] wordVector = getWordVector(word);  
        if (wordVector == null) {  
            return null;  
        }  
        Set<Entry<String, float[]>> entrySet = wordMap.entrySet();  
        float[] tempVector = null;  
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);  
        String name = null;  
        for (Entry<String, float[]> entry : entrySet) {  
            name = entry.getKey();  
            if (name.equals(word)) {  
                continue;  
            }  
            float dist = 0;  
            tempVector = entry.getValue();  
            for (int i = 0; i < wordVector.length; i++) {  
                dist += wordVector[i] * tempVector[i];  
            }  
            insertTopN(name, dist, wordEntrys);  
        }  
        return new TreeSet<WordEntry>(wordEntrys);  
    }  
  
    /** 
     * 近义词 
     *  
     * @return 
     */  
    private TreeSet<WordEntry> analogy(String word0, String word1, String word2) {  
        float[] wv0 = getWordVector(word0);  
        float[] wv1 = getWordVector(word1);  
        float[] wv2 = getWordVector(word2);  
  
        if (wv1 == null || wv2 == null || wv0 == null) {  
            return null;  
        }  
        float[] wordVector = new float[size];  
        for (int i = 0; i < size; i++) {  
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];  
        }  
        float[] tempVector;  
        String name;  
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);  
        for (Entry<String, float[]> entry : wordMap.entrySet()) {  
            name = entry.getKey();  
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {  
                continue;  
            }  
            float dist = 0;  
            tempVector = entry.getValue();  
            for (int i = 0; i < wordVector.length; i++) {  
                dist += wordVector[i] * tempVector[i];  
            }  
            insertTopN(name, dist, wordEntrys);  
        }  
        return new TreeSet<WordEntry>(wordEntrys);  
    }  
  
    private void insertTopN(String name, float score,  
            List<WordEntry> wordsEntrys) {  
        if (wordsEntrys.size() < topNSize) {  
            wordsEntrys.add(new WordEntry(name, score));  
            return;  
        }  
        float min = Float.MAX_VALUE;  
        int minOffe = 0;  
        for (int i = 0; i < topNSize; i++) {  
            WordEntry wordEntry = wordsEntrys.get(i);  
            if (min > wordEntry.score) {  
                min = wordEntry.score;  
                minOffe = i;  
            }  
        }  
  
        if (score > min) {  
            wordsEntrys.set(minOffe, new WordEntry(name, score));  
        }  
  
    }  
  
    private class WordEntry implements Comparable<WordEntry> {  
        public String name;  
        public float score;  
  
        public WordEntry(String name, float score) {  
            this.name = name;  
            this.score = score;  
        }  
  
        @Override  
        public String toString() {  
            return this.name + '\t' + this.score;  
        }  
  
        @Override  
        public int compareTo(WordEntry o) {  
            if (this.score > o.score) {  
                return -1;  
            } else {  
                return 1;  
            }  
        }  
  
    }  
  
    /** 
     * 得到词向量 
     *  
     * @param word 
     * @return 
     */  
    private float[] getWordVector(String word) {  
        return wordMap.get(word);  
    }  
  
    private float readFloat(InputStream is) throws IOException {  
        byte[] bytes = new byte[4];  
        is.read(bytes);  
        return getFloat(bytes);  
    }  
  
    /** 
     * 读取一个float 
     *  
     * @param b 
     * @return 
     */  
    private float getFloat(byte[] b) {  
        int accum = 0;  
        accum = accum | (b[0] & 0xff) << 0;  
        accum = accum | (b[1] & 0xff) << 8;  
        accum = accum | (b[2] & 0xff) << 16;  
        accum = accum | (b[3] & 0xff) << 24;  
        return Float.intBitsToFloat(accum);  
    }  
  
    /** 
     * 读取一个字符串 
     *  
     * @param dis 
     * @return 
     * @throws IOException 
     */  
    private String readString(DataInputStream dis) throws IOException {  
        byte[] bytes = new byte[MAX_SIZE];  
        byte b = dis.readByte();  
        int i = -1;  
        StringBuilder sb = new StringBuilder();  
        while (b != 32 && b != 10) {  
            i++;  
            bytes[i] = b;  
            b = dis.readByte();  
            if (i == 49) {  
                sb.append(new String(bytes));  
                i = -1;  
                bytes = new byte[MAX_SIZE];  
            }  
        }  
        sb.append(new String(bytes, 0, i + 1));  
        return sb.toString();  
    }  
  
}  
