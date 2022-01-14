import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Four{
    public static void main(String[] args) {

        // load stop words
        List<String> stop_words = new ArrayList<String>();
        String str = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("../stop_words.txt"));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words");
        }
        String[] words = str.split(",");
        stop_words.addAll(Arrays.asList(words));

        // word frequncy list 
        List<String> wordsList = new ArrayList<>();
        List<Integer> freqList = new ArrayList<>();

        // process file
        Path filepath = Paths.get("../pride-and-prejudice.txt");
        try (Stream<String> lines = Files.lines(filepath)) {
            lines.forEach(line -> {
                int start_char = -1;
                int idx = 0;
                line += " ";
                for(int i = 0; i < line.length(); ++i) {
                    boolean isalnum = (line.charAt(i) >= '0' && line.charAt(i) <= '9')
                            || (line.charAt(i) >= 'a' && line.charAt(i) <= 'z')
                            || (line.charAt(i) >= 'A' && line.charAt(i) <= 'Z');
                    if (start_char == -1) {
                        if(isalnum) {
                            start_char = idx;
                        }
                    } else {
                        // end of a word
                        if (!(isalnum)) {
                            boolean found = false;
                            StringBuilder word = new StringBuilder(line.substring(start_char, i));
                            for(int j = 0; j < word.length(); ++j) {
                                if(word.charAt(j) >= 'A' && word.charAt(j) <= 'Z') {
                                    word.setCharAt(j, (char)(word.charAt(j) + 'a' - 'A'));
                                }
                            }
                            // ignore stop word
                            boolean exist = false;
                            for(String stop_word: stop_words) {
                                if(stop_word.equals(word.toString())) {
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist) {
                                int pair_idx = 0;
                                for(int k = 0; k < wordsList.size(); ++k) {
                                    if(wordsList.get(k).equals(word.toString())) {
                                        freqList.set(k, freqList.get(k) + 1);
                                        found = true;
                                        break;
                                    }
                                    pair_idx++;
                                }
                                if(!found) {
                                    wordsList.add(word.toString());
                                    freqList.add(1);
                                } else if(wordsList.size() > 1) {
                                    for(int n = pair_idx; n >= 0; n--){
                                        if(freqList.get(pair_idx) > freqList.get(n)) {
                                            // swap
                                            wordsList.set(n, wordsList.set(pair_idx, wordsList.get(n)));
                                            freqList.set(n, freqList.set(pair_idx, freqList.get(n)));
                                            pair_idx = n;
                                        }
                                    }
                                }
                            }
                            start_char = -1;
                        }
                    }
                    idx++;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        int idx = 0;
        for(int i = 0; i < wordsList.size(); i++) {
            if(wordsList.get(i).length() > 1) {
                System.out.println(wordsList.get(i) + "  -  " + freqList.get(i));
                idx++;
            }
            if(idx > 24) {
                break;
            }
        }

    }
}