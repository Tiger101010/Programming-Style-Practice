import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class FrequencyCounter {
    private static final List<String> stop_words = new ArrayList<String>();

    static final class Counter {
        private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();

        private void process(Path filepath) {
            try {
                try (Stream<String> lines = Files.lines(filepath)) {
                    lines.forEach(line -> {
                        process(line);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Keep only the non stop words with 3 or more characters
        private void process(String line) {
            String[] words = line.split("[^a-zA-Z0-9]+");
            for (String word : words) {
                String w = word.toLowerCase();
                if (!stop_words.contains(w) && w.length() > 1) {
                    if (frequencies.containsKey(w))
                        frequencies.put(w, frequencies.get(w) + 1);
                    else
                        frequencies.put(w, 1);
                }
            }
        }

        private List<Map.Entry<String, Integer>> sort() {
            Set<Map.Entry<String, Integer>> set = frequencies.entrySet();
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                    set);
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            return list;
        }

        private HashMap<String, Integer> getFrequencies() {
            return frequencies;
        }

        public void merge(Counter other) {
            other.getFrequencies().forEach((k, v) -> frequencies.merge(k, v, Integer::sum));
        }

        // Only the top 25 words that are 3 or more characters
        public String toString() {
            List<Map.Entry<String, Integer>> sortedMap = sort();
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Map.Entry<String, Integer> e : sortedMap) {
                String k = e.getKey();
                sb.append(k + "  -  " + e.getValue() + "\n");
                if (i++ > 23)
                    break;
            }
            return sb.toString();
        }

    }

    private static void loadStopWords() {
        String str = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("../stop_words.txt"));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
        String[] words = str.split(",");
        stop_words.addAll(Arrays.asList(words));
    }

    private static void countWords(Path p, Counter c) {
        c.process(p);
    }

    public static void main(String[] args) {

        loadStopWords();
        Counter c = new Counter();

        // Read first argument as filename
        countWords(Paths.get(args[0]), c);
      
        System.out.println(c);

    }
}
