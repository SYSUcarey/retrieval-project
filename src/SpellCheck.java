import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


// 拼写检查
public class SpellCheck {
    public static String[] check(String q) throws IOException {
        String spellIndexPath = "spell-index";// 新索引目录
        String oriIndexPath = "index";// 已有索引目录
        Directory directory = FSDirectory.open((new File(spellIndexPath)).toPath());
        SpellChecker spellChecker = new SpellChecker(directory);
        IndexReader reader = DirectoryReader.open(FSDirectory.open((new File(oriIndexPath)).toPath()));// 初始化索引
        Dictionary dictionary = new LuceneDictionary(reader, "contents");// 利用已有索引
        IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
        spellChecker.indexDictionary(dictionary, config, true);
        int numSug = 10;
        String[] suggestWords = spellChecker.suggestSimilar(q, numSug);
        reader.close();
        spellChecker.close();
        directory.close();
        return suggestWords;
    }

    public static void main(String[] argv) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str = bufferedReader.readLine();
        String[] suggestWords = check(str);
        for (String word : suggestWords) {
            System.out.println(word);
        }
    }
}
