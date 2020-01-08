import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import consoletable.table.*;
import consoletable.enums.*;
import consoletable.ConsoleTable;

// ʹ��TF/IDF�����ĵ�����ͨ��������ѯ�ĵ������ز�ѯ�Ľ����Ŀ
public class Searcher {

    public static int search(String indexDir, String q) throws Exception {
        List<Document> queryResult =  Searcher.searchResult(indexDir, q);
        List<Cell> header = new ArrayList<Cell>(){{
            add(new Cell("����"));
            add(new Cell("�ĵ���"));
//            add(new Cell("���ݸ���"));
            add(new Cell("��������"));
        }};
        List<List<Cell>> body = new ArrayList<List<Cell>>();
        int i = 0;
        for (Document doc : queryResult) {
            String index = String.valueOf(i+1);
            String name = doc.get("fileName");
            String file = FileUtils.readFile(doc.get("filePath"));
            String content = file.substring(0, file.length()>40?40:file.length()).replaceAll("\n", "")+"...";
            String importContent = file.substring((file.indexOf(q)-30<0)?0:file.indexOf(q)-30, (file.indexOf(q)+30>file.length())?file.length():file.indexOf(q)+30).replaceAll("\n","")+"...";
            body.add(new ArrayList<Cell>(){{
                add(new Cell(index));
                add(new Cell(name));
//                add(new Cell(Align.LEFT,(content == null || content.equals(""))?"����":content));
                add(new Cell(Align.LEFT,(importContent == null || importContent.equals(""))?"����":importContent));
            }});
            i++;
        }
        if (queryResult.size() > 0) {
            new ConsoleTable.ConsoleTableBuilder().addHeaders(header).addRows(body).build().print();
        }
        return queryResult.size();
    }

    public static List<Document> searchResult(String indexDir, String q) throws  Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));// ��Ŀ¼
        IndexReader reader = DirectoryReader.open(dir);// ���ж�ȡ
        IndexSearcher is = new IndexSearcher(reader);// ������ѯ��
        is.setSimilarity(new ClassicSimilarity());// ����ΪTF/IDF����
        Analyzer analyzer = new SmartChineseAnalyzer();// ���ķִ���
        QueryParser parser = new QueryParser("contents", analyzer);// ���Ĳ�ѯ����һ������Ϊ��ѯ��Document������Indexer�д���
        Query query = parser.parse(q);// ���ֶν��н����󷵻ظ���ѯ
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10);// ��ʼ��ѯ��10����ǰ10�����ݣ�����һ���ĵ�
        long end = System.currentTimeMillis();
        System.out.println("����" + hits.totalHits.value + "�ĵ�ƥ������\"" + q + "\"������ʱ " + (end - start) + " ms");
        List<Document> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);// �����ĵ��ı�ʶ��ȡ�ĵ�
            result.add(doc);
        }
        reader.close();
        return result;
    }

}

