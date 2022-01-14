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

public class Six {
    public static List<String> readFile(Path filepath) {
        try {
            try (Stream<String> lines = Files.lines(filepath)) {
                List<String> words = new ArrayList<>();
                lines.forEach(line -> {
                    String[] lineWord = line.split("[^a-zA-Z0-9]+");
                    words.addAll(Arrays.asList(lineWord));
                });
                return words;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // Keep only the non-stop words with 3 or more characters
    public static List<String> removeStopWords(List<String> words) {
        String str = "";
        List<String> stop_words = new ArrayList<String>();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("../stop_words.txt"));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
        String[] stopwords = str.split(",");
        stop_words.addAll(Arrays.asList(stopwords));

        List<String> toRemove = new ArrayList<>();
        words.forEach(word -> {
            String w = word.toLowerCase();
            if (stop_words.contains(w) || w.length() < 2) {
                toRemove.add(word);
            }
        });
        words.removeAll(toRemove);
        return words;
    }

    public static HashMap<String, Integer> countFrequency(List<String> words) {
        HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
        for (String word : words) {
            String w = word.toLowerCase();
            if (frequencies.containsKey(w))
                frequencies.put(w, frequencies.get(w) + 1);
            else
                frequencies.put(w, 1);
        }
        return frequencies;
    }

    public static List<Map.Entry<String, Integer>> sortByFrequency(HashMap<String, Integer> frequencies) {
        List<Map.Entry<String, Integer>> sortedMap = new ArrayList<>(frequencies.entrySet());
        Collections.sort(sortedMap, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return sortedMap;
    }

    // Only the top 25 words that are 3 or more characters
    public static String getResult(List<Map.Entry<String, Integer>> sortedMap) {
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

    public static void printResult(String result) {
        System.out.println(result);
    }

    public static void main(String[] args) {
        printResult(
                getResult(
                        sortByFrequency(
                                countFrequency(
                                        removeStopWords(
                                                readFile(Paths.get(args[0]))
                                        )
                                )
                        )
                )
        );
    }
}
