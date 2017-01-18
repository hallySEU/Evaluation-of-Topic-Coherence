package topicCoherence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

import constant.EvaluationConstants;

public class QueryWords {

	public static void main(String[] args) throws CorruptIndexException,
			IOException, ParseException {
		
		for (File file : new File(EvaluationConstants.DOCUMENTSS_PATH).listFiles()) {
			ArrayList<String> topicKeywords = FileProcess.readFile(
					file.getAbsolutePath());
			String fileString=EvaluationConstants.RESULTS_PATH+file.getName()+".score";
			ArrayList<ArrayList<String>> topicWordList=FileProcess.getTopicWordList(topicKeywords);
			double totalScore=0;
			for (int k = 0; k < topicWordList.size(); k++) {		
				double score=calTopWordScore(topicWordList.get(k),k/3,fileString);	
				totalScore += score;
			}
			FileProcess.writeFile(fileString, "Average Coherence score: " + totalScore/topicWordList.size());
		}
	
		
	}
	
	public static double calTopWordScore(ArrayList<String> topWordList, int topicNum,String fileString) 
			throws CorruptIndexException, IOException, ParseException
	{
		QueryWords queryWords = new QueryWords();
		Searcher searcher = queryWords.getSearcher(EvaluationConstants.WIKIPEDIA_PATH);
		ArrayList<HashSet<String>> word2DocIndexCollection = new ArrayList<HashSet<String>>(); //存储包含关键词的文档索引

		for (int wordIndex = 0; wordIndex < topWordList.size(); wordIndex++) {// 查询索引
			System.out.println("TopicNum: "+topicNum + "\tTopwordIndex: " + wordIndex);
			word2DocIndexCollection.add(queryWords.testTime(searcher,
					queryWords.getQueryParser(topWordList.get(wordIndex))));

		}
		double score = 0.0;
		for (int m = 1; m < word2DocIndexCollection.size(); m++) {
			for (int l = 0; l < m; l++) {
				Set indexSet1 = word2DocIndexCollection.get(m);
				Set indexSet2 = word2DocIndexCollection.get(l);
				Set jointIndexSet = new HashSet(indexSet1);
				jointIndexSet.retainAll(indexSet2);
				long jointSetSize = jointIndexSet.size();
				long set1Size = indexSet1.size();
				long set2Size = indexSet2.size();
				double intersection = 0.0;
				if (set2Size > 0) {
					intersection = (double) (jointSetSize + 1)
							/ set2Size;
					score += Math.log(intersection);
				} else {
					score += (-10.0);
				}
				System.out.println(topWordList.get(m) + "-" + set1Size + "-"
						+ topWordList.get(l) + "-" + set2Size + "-intersection:"
						+ jointSetSize + "-score:" + Math.log10(intersection + 1));
			}
		}
		FileProcess.writeFile(fileString, "Topic: " +topicNum+ "\tTopic Coherence score: " + score);
		System.out.println("Topic" + topicNum + " Topic Coherence score: "+ score);
		return score;
	}

	public HashSet<String> testTime(Searcher search, Query query)
			throws IOException {
		Date start = new Date();
		Hits hits = search.search(query);

		HashSet<String> articles = new HashSet<String>();
		for (int i = 0; i < hits.length(); i++) {
			String DocID = hits.doc(i).get("wid");
			articles.add(DocID);
		}
		return articles;
	}

	public Searcher getSearcher(String path) throws CorruptIndexException,
			IOException {
		return new IndexSearcher(path);
	}

	public Query getDoubleQueryParser(String query1, String query2)
			throws ParseException {
		BooleanQuery bquery = new BooleanQuery();

		Query pQuery1 = new TermQuery(new Term("wcontent", query1));
		BooleanClause clause1 = new BooleanClause(pQuery1,
				BooleanClause.Occur.MUST);
		bquery.add(clause1);

		Query pQuery2 = new TermQuery(new Term("wcontent", query2));
		BooleanClause clause2 = new BooleanClause(pQuery2,
				BooleanClause.Occur.MUST);
		bquery.add(clause2);

		return bquery;
	}

	public Query getQueryParser(String query) throws ParseException {

		Query pQuery = new TermQuery(new Term("wcontent", query));
		return pQuery;
	}
}