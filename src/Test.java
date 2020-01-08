import org.apache.commons.lang.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] argv) throws IOException {
        Indexer.bulidIndex("index", "data/page");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++精简版搜索引擎++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println("请输入搜索关键词:");
            String str = bufferedReader.readLine();
            String[] suggestWords = SpellCheck.check(str);
            if (str != null && str.length() > 0) {
                try {
                    System.out.println("正在为您搜索...");
                    int num = Searcher.search("index", str);
                    if (suggestWords != null && suggestWords.length != 0) {
                        System.out.println("关联搜索:");
                        for (String word : suggestWords) {
                            System.out.print(word + " ");
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
