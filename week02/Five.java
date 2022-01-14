import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Five {
    
    private static final List<String> stop_words = new ArrayList<String>();
    private static HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
    private static List<String> words = new ArrayList<>();
    private static List<Map.Entry<String, Integer>> sortedMap = new ArrayList<>();
    private static String result;
    
    public static void readFile(Path filepath) {
        try {
            try (Stream<String> lines = Files.lines(filepath)) {
                lines.forEach(line -> {
                    words.addAll(Arrays.asList(line.split("[^a-zA-Z0-9]+")));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadStopWords() {
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

    // Keep only the non-stop words with 3 or more characters
    public static void removeStopWords() {
        List<String> toRemove = new ArrayList<>();
        words.forEach(word -> {
            String w = word.toLowerCase();
            if (stop_words.contains(w) || w.length() < 2) {
                toRemove.add(word);
            }
        });
        words.removeAll(toRemove);
    }

    public static void countFrequency() {
        for (String word : words) {
            String w = word.toLowerCase();
            if (frequencies.containsKey(w))
                frequencies.put(w, frequencies.get(w) + 1);
            else
                frequencies.put(w, 1);
        }
    }

    public static void sortByFrequency() {
        sortedMap = new ArrayList<>(frequencies.entrySet());
        Collections.sort(sortedMap, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    }

    // Only the top 25 words that are 3 or more characters
    public static void getResult() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Integer> e : sortedMap) {
            String k = e.getKey();
            sb.append(k + "  -  " + e.getValue() + "\n");
            if (i++ > 23)
                break;
        }
        result = sb.toString();
    }

    public static void printResult() {
        System.out.println(result);
    }


    public static void main(String[] args) {
        readFile(Paths.get(args[0]));
        loadStopWords();
        removeStopWords();
        countFrequency();
        sortByFrequency();
        getResult();
        printResult();
    }
}