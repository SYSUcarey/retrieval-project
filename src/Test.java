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
            System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++�������������++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println("�����������ؼ���:");
            String str = bufferedReader.readLine();
            String[] suggestWords = SpellCheck.check(str);
            if (str != null && str.length() > 0) {
                try {
                    System.out.println("����Ϊ������...");
                    int num = Searcher.search("index", str);
                    if (suggestWords != null && suggestWords.length != 0) {
                        System.out.println("��������:");
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
