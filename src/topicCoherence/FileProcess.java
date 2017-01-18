package topicCoherence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import constant.EvaluationConstants;

public class FileProcess {
	
	public static ArrayList<ArrayList<String>> getTopicWordList(ArrayList<String> topicLists)
	{
		ArrayList<ArrayList<String>> topicWordList=new ArrayList<ArrayList<String>>();
		
		for (String topicLine : topicLists) {
			ArrayList<String> topicList= new ArrayList<String>();
			String tokens[]=topicLine.split(" : ");
			for (String word : tokens[1].split("\t| ")) {
				topicList.add(word);
//				System.out.println(word);
			}
			topicWordList.add(topicList);
		}
		return topicWordList;
	}

	public static ArrayList<String> readFile(String filePath)
			throws FileNotFoundException {
		ArrayList<String> arrayList = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(filePath);
		Scanner scanner = new Scanner(fis);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			arrayList.add(line);
			//System.out.println(line);
		}
		scanner.close();
		return arrayList;
	}

	public static void writeFile(String path, String str) {
		try {
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, true); // 如果追加方式用true
			StringBuffer sb = new StringBuffer();
			sb.append(str+"\n");
			out.write(sb.toString().getBytes("utf-8"));// 注意需要转换对应的字符集
			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
	}
	
	public static void test()
	{
		for (File file : new File(EvaluationConstants.DOCUMENTSS_PATH).listFiles()) {
			System.out.println(file.getAbsolutePath()+"\t"+file.getName());
		}
	}
	
}
