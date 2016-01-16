package org.dataccs.document.pretreatment.allocatedata;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 按指定比例，
 * 随机从原始语料库中
 * 分配指定比例的文档到训练语料库和测试语料库
 * @author dataccs
 */
public class RandomAllocateData {

	// 原始语料库根目录
	private final String corpusPath = "D:/paperTest/SogouC-UTF8/ClassFile/";
	// 待训练语料库根目录
	private final String trainDataPath = "D:/corpus/train/";
	// 待分类语料库根目录
	private final String testDataPath = "D:/corpus/test/";
	// 待训练语料库文档数量占原始语料库比例
	private final double trainDataPercent = 0.75;
	// 待分类语料库文档数量占原始语料库比例
	private final double testDataPercent = 0.25;
	// 用来产生随机数
	private Random random = new Random();
	
	// 分配
	private void allocate(){
		
		try{
			File dir = new File(corpusPath);
			File[] labelDirs = dir.listFiles();
			for(File labelDir : labelDirs){
				// 每一个类别的所有文档存于files数组里
				File[] files = labelDir.listFiles();
				String sourceLabelDir = corpusPath+labelDir.getName()+"/";
				String destTrainLabelDir = trainDataPath+labelDir.getName()+"/";
				String destTestLabelDir = testDataPath+labelDir.getName()+"/";
				creatNewDir(destTrainLabelDir);
				creatNewDir(destTestLabelDir);
				copy(files,sourceLabelDir,destTrainLabelDir,destTestLabelDir);
			}
			System.out.println("训练和测试语料库分配拷贝完成！");
		} catch(IOException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void creatNewDir(String directory) throws Exception{
		File dir = new File(directory);
		//如果该目录已经存在，则将其整个目录删除，重新创建新目录
		if(dir.exists()){
			File[] files = dir.listFiles();
			for(File file : files){
				file.delete();
			}
			dir.delete();
		}
		boolean suc = dir.mkdir();
		if(suc){
			System.out.println("创建["+directory+"]目录成功！");
		} else {
			throw new Exception("创建["+directory+"]目录失败！");
		}
	}
	
	private void copy(File[] files, String sourceDir, String destDirTain, String destDirTest) throws IOException{
		int trainDocuNums = (int)(files.length * trainDataPercent);
		int testDocuNums = files.length - trainDocuNums;
		Set<Integer> randomIndexSet = new HashSet<Integer>();
		int counter = 0;
		// 把语料库中需要训练的文档拷贝到训练语料库
		while(counter<trainDocuNums){
			int randomIndex = getDifferentRandomIndex(randomIndexSet,files.length);
			String oldFilePath = sourceDir + files[randomIndex].getName();
			String newFilePath = destDirTain + files[randomIndex].getName();
			copyFile(oldFilePath, newFilePath);
			counter++;
		}
		// 把语料库中剩下的文档拷贝到测试语料库
		for(int i=0; i<files.length; i++){
			if(!randomIndexSet.contains(i)){
				String oldFilePath = sourceDir + files[i].getName();
				String newFilePath = destDirTest + files[i].getName();
				copyFile(oldFilePath, newFilePath);
			}
		}
	}
	
	public void copyFile(String oldPath, String newPath) throws IOException { 
		System.out.println("复制单个文件从["+oldPath+"]到["+newPath+"]");    
		int byteread = 0; 
		File oldfile = new File(oldPath); 
		File newFile = new File(newPath);
		if(newFile.exists()){
			newFile.delete();
		}
		newFile.createNewFile();
		//源文件文件存在时 
		if (oldfile.exists()) {                  
			InputStream inStream = new FileInputStream(oldfile);  //读入原文件 
			FileOutputStream fs = new FileOutputStream(newFile); 
			byte[] buffer = new byte[4096]; 
			while ( (byteread = inStream.read(buffer)) != -1) { 
				fs.write(buffer, 0, byteread); 
			} 
			inStream.close(); 
		} 
	} 
	
	private int getDifferentRandomIndex(Set<Integer> randomIndexSet, int length){
		int num = random.nextInt(length);
		while(randomIndexSet.contains(num)){
			num = random.nextInt(length);
		}
		// 防止重复拷贝
		randomIndexSet.add(num);
		return num;
	}
	
	public static void main(String[] args){
		// 按指定比例随机分配训练集和测试集
		RandomAllocateData rad = new RandomAllocateData();
		rad.allocate();
	}
}
