package ir.ayhansalami.wordprediction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ayhan Salami on 2/7/2017.
 *         Email: ayhan.irta@gmail.com
 *         Social Networks: ayhansalami
 */
public class NGramTokenizer {
    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append(i > start ? " " : "").append(words[i]);
        return sb.toString();
    }
}
