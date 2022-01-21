import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class Eight {

    public static void count(List<String> words, int idx, List<String> stop_words, HashMap<String, Integer> word_freq){

        if(idx == words.size()) return;

        String word = words.get(idx);
        if(!stop_words.contains(word) && word.length() > 1) {
            if(word_freq.containsKey(word)) {
                word_freq.put(word, word_freq.get(word) + 1);
            } else {
                word_freq.put(word, 1);
            }
        }
        count(words, idx + 1, stop_words, word_freq);
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> word_freq = new HashMap<>();
        List<String> stop_words = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
        List<String> words = asList(new String(Files.readAllBytes(Paths.get(args[0])))
                .toLowerCase()
                .split("[^a-zA-Z0-9]+"));
        
        // Recursion
        count(words, 0, stop_words, word_freq);

        word_freq.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(25)
                .forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));

    }
}
