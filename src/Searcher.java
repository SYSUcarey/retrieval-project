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

// 使用TF/IDF进行文档排序，通过索引查询文档，返回查询的结果数目
public class Searcher {

    public static int search(String indexDir, String q) throws Exception {
        List<Document> queryResult =  Searcher.searchResult(indexDir, q);
        List<Cell> header = new ArrayList<Cell>(){{
            add(new Cell("排名"));
            add(new Cell("文档名"));
//            add(new Cell("内容概览"));
            add(new Cell("关联内容"));
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
//                add(new Cell(Align.LEFT,(content == null || content.equals(""))?"暂无":content));
                add(new Cell(Align.LEFT,(importContent == null || importContent.equals(""))?"暂无":importContent));
            }});
            i++;
        }
        if (queryResult.size() > 0) {
            new ConsoleTable.ConsoleTableBuilder().addHeaders(header).addRows(body).build().print();
        }
        return queryResult.size();
    }

    public static List<Document> searchResult(String indexDir, String q) throws  Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));// 打开目录
        IndexReader reader = DirectoryReader.open(dir);// 进行读取
        IndexSearcher is = new IndexSearcher(reader);// 索引查询器
        is.setSimilarity(new ClassicSimilarity());// 设置为TF/IDF排序
        Analyzer analyzer = new SmartChineseAnalyzer();// 中文分词器
        QueryParser parser = new QueryParser("contents", analyzer);// 在哪查询，第一个参数为查询的Document，已在Indexer中创建
        Query query = parser.parse(q);// 对字段进行解析后返回给查询
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10);// 开始查询，10代表前10条数据；返回一个文档
        long end = System.currentTimeMillis();
        System.out.println("共有" + hits.totalHits.value + "文档匹配搜索\"" + q + "\"，共耗时 " + (end - start) + " ms");
        List<Document> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);// 根据文档的标识获取文档
            result.add(doc);
        }
        reader.close();
        return result;
    }

}

