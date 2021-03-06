import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class Eight {

    public static final int RECURSIVE_LIMIT = 1000;

    // count frequency
    public static void count(List<String> words, int idx, int length, List<String> stop_words, HashMap<String, Integer> word_freq){
        // base case
        if(idx == length || idx >= words.size()) return;

        // inductive case
        String word = words.get(idx);
        if(!stop_words.contains(word) && word.length() > 1) {
            if(word_freq.containsKey(word)) {
                word_freq.put(word, word_freq.get(word) + 1);
            } else {
                word_freq.put(word, 1);
            }
        }
        // tail recursion
        count(words, idx + 1, length, stop_words, word_freq);
    }

    // read word character by character
    public static int getWords(List<String> words, String raw_words, int idx, int length) {
        // base case
        if(idx >= length || idx >= raw_words.length()) return idx;

        // inductive case
        StringBuilder word = new StringBuilder();
        char letter = raw_words.charAt(idx);
        while(Character.isLetterOrDigit(letter)) {
            word.append(letter);
            ++idx;
            letter = raw_words.charAt(idx);
        }
        words.add(word.toString());

        // tail recursion
        return getWords(words, raw_words, idx + 1, length);
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> word_freq = new HashMap<>();
        List<String> stop_words = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
        String raw_words = new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase();
        List<String> words = new ArrayList<>();

        // Recursion
        for(int i = 0; i < raw_words.length(); ){
            i = getWords(words, raw_words, i, i + RECURSIVE_LIMIT);
        }

        // Recursion
        for(int i = 0; i < raw_words.length(); i += RECURSIVE_LIMIT){
            count(words, i, i + RECURSIVE_LIMIT, stop_words, word_freq);
        }

        word_freq.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(25)
                .forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));
    }
}
