import java.io.*;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


//构建倒排索引
public class Indexer {
    IndexWriter writer;

    // 构造函数 实例化IndexWriter
    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        writer = new IndexWriter(dir, config);
    }

    public void close() throws Exception {
        writer.close();
    }

    public int index(String dataDir) throws IOException {
        File[] files = new File(dataDir).listFiles();
        int count = 0;
        for (File f : files) {
            indexFile(f);
            count++;
        }
        return count;
    }

    private void indexFile(File file) throws IOException {
//        System.out.println("index file: " + file.getCanonicalPath());

        Document doc = getDocument(file);
        writer.addDocument(doc);
    }

    private Document getDocument(File file) throws IOException {
        Document doc = new Document();

        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        fieldType.setStored(true);

        StringBuffer buffer = new StringBuffer();
        // 采用UTF-8编码读取中文文档，防止乱码
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader reader = new BufferedReader(isr);
        String tmp = null;
        while ((tmp = reader.readLine()) != null) {
            buffer.append(tmp.trim());
        }
        doc.add(new Field("contents", buffer.toString(), fieldType));
        doc.add(new Field("fileName", file.getName(), fieldType));
        doc.add(new Field("filePath", file.getCanonicalPath(), fieldType));

        return doc;
    }

    public static void bulidIndex(String indexDir, String dataDir) {
        String path = "index";
        File file = new File(path);
        File[] listFiles = file.listFiles();
        if(listFiles.length > 0){
            // 已建立搜索索引
            // do nothing
        } else {
            System.out.println("正在为您建立搜索索引，请稍等...");
            Indexer indexer = null;
            int numIndexed = 0;
            long start = System.currentTimeMillis();
            try {
                indexer = new Indexer(indexDir);
                numIndexed = indexer.index(dataDir);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    indexer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("已建立" + numIndexed + " 个文档的搜索索引.\t总耗时 : " + (end - start) + "ms");
        }
    }

    // 主函数
    public static void main(String[] argv) {
        String indexDir = "index";
        String dataDir = "data/page";

        Indexer indexer = null;
        int numIndexed = 0;
        long start = System.currentTimeMillis();
        try {
            indexer = new Indexer(indexDir);
            numIndexed = indexer.index(dataDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                indexer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Building index " + numIndexed + " files.\ttime : " + (end - start) + "ms");
    }
}
