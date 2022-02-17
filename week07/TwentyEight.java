import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;

public class TwentyEight {
    public static class LineIt implements Iterator<String> {
        public Iterator<String> lines;

        public LineIt(String filePath) throws IOException {
            this.lines = Files.lines(Paths.get(filePath)).iterator();
        }

        @Override
        public boolean hasNext() {
            return lines.hasNext();
        }

        @Override
        public String next() {
            return lines.next();
        }
    }

    public static class WordIt implements Iterator<String> {
        public Iterator<String> word;
        public Iterator<String> lines;

        public WordIt(Iterator<String> lines) {
            this.lines = lines;
            if(lines.hasNext()) {
                word = Arrays.stream(lines.next().split("[^a-zA-Z0-9]+")).iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return lines.hasNext() || word.hasNext();
        }

        @Override
        public String next() {
            if(word.hasNext()) {
                return word.next();
            } else {
                if(lines.hasNext()) {
                    word = Arrays.stream(lines.next().toLowerCase().split("[^a-zA-Z0-9]+")).iterator();
                }
            }
            return null;
        }
    }

    public static class StopWordFilter implements Iterator<String> {
        public static List<String> stopWords;

        static {
            try {
                stopWords = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Iterator<String> wordIt;

        public StopWordFilter(Iterator<String> wordIt) throws IOException {
           this.wordIt = wordIt;
        }

        @Override
        public boolean hasNext() {
            return wordIt.hasNext();
        }

        @Override
        public String next() {
            String next = wordIt.next();
            if(next != null && !stopWords.contains(next) && next.length() > 1) {
                return next;
            }
            return null;
        }
    }

    public static class CountNSort implements Iterator<List<Map.Entry<String, Integer>>> {
        public Iterator<String> wordIt;
        public Map<String, Integer> freq = new HashMap<>();

        public CountNSort(Iterator<String> wordIt) {
            this.wordIt = wordIt;
        }

        @Override
        public boolean hasNext() {
            return wordIt.hasNext();
        }

        @Override
        public List<Map.Entry<String, Integer>> next() {
            int i = 0;
            while(wordIt.hasNext()) {
                String next = wordIt.next();
                i++;
                if(next == null) {
                    continue;
                }
                if(freq.containsKey(next)) {
                    freq.put(next, freq.get(next) + 1);
                } else {
                    freq.put(next, 1);
                }
                if(i % 5000 == 0) {
                    List<Map.Entry<String, Integer>> sortedMap = new ArrayList<>(freq.entrySet());
                    sortedMap.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                    return sortedMap;
                }
            }
            List<Map.Entry<String, Integer>> sortedMap = new ArrayList<>(freq.entrySet());
            sortedMap.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            return sortedMap;
        }
    }

    public static void main(String[] args) throws IOException {
        LineIt lineIt = new LineIt(args[0]);
        WordIt wordIt = new WordIt(lineIt);
        StopWordFilter nonStopWordIt = new StopWordFilter(wordIt);
        CountNSort listIt = new CountNSort(nonStopWordIt);

        int updateTime = 1;
        while (listIt.hasNext()){
            System.out.println("-----------Update: "+ updateTime++ + "------------");
            List<Map.Entry<String, Integer>> list = listIt.next();
            list.stream().limit(25).forEach(System.out::println);
        }
    }
}
