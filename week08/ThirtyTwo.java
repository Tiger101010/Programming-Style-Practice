import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ThirtyTwo {
    public static List<String> stopWords;

    public static void getStopWords() {
        try{
            stopWords = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            return "";
        }
    }

    public static List<List<String>> partition(String data, int nLines) {
        List<String> lines = Arrays.asList(data.split("\n"));
        int numOfLines = lines.size();
        List<List<String>> parts = new ArrayList<>();

        for(int i = 0; i < numOfLines; i += nLines){
            parts.add(new ArrayList<>(lines.subList(i, Math.min(numOfLines, i + nLines))));
        }

        return parts;
    }

    public static List<Pair> splitWords(List<String> part) {
        List<Pair> result = new ArrayList<>();
        for (String line: part) {
             Arrays.stream(line.toLowerCase().split("[^a-zA-Z]+"))
                     .filter(word -> !(stopWords.contains(word) || word.length() < 2))
                     .collect(Collectors.toList())
                     .forEach(word -> result.add(new Pair(word, 1)));
        }
        return result;
    }

    public static Map<String, List<Pair>> regroup(List<List<Pair>> splits) {
        Map<String, List<Pair>> mapping = new HashMap<>();
        for (List<Pair> split: splits) {
            for (Pair p: split) {
                String firstChar = String.valueOf(p.key.charAt(0));
                if(mapping.containsKey(firstChar)) {
                    mapping.get(firstChar).add(p);
                } else {
                    mapping.put(firstChar, new ArrayList<>());
                    mapping.get(firstChar).add(p);
                }
            }
        }
        System.out.println("There are 26 Group: ");
        mapping.entrySet().forEach(entry -> System.out.print(entry.getKey() + " "));
        System.out.println(" ");

        return mapping;
    }

    public static List<Pair> countWords(List<Pair> mapping) {
        return new ArrayList<>(mapping.parallelStream().collect(Collectors.toConcurrentMap(
                Pair::getKey,
                pair -> pair,
                (p1, p2) -> new Pair(p1.key, p1.value + p2.value)))
                .values());
    }

    public void run(String arg) {
        getStopWords();

        List<List<Pair>> splits = partition(readFile(arg), 200).parallelStream()
                .map(ThirtyTwo::splitWords)
                .collect(Collectors.toList());

        Map<String, List<Pair>> splitPerLetter = regroup(splits);

        List<Pair> wordFreq = splitPerLetter.values().parallelStream()
                .map(ThirtyTwo::countWords)
                .collect(ArrayList::new, List::addAll, List::addAll);

        wordFreq.sort(Comparator.comparingInt(p -> -p.value));

        System.out.println();
        wordFreq.stream().limit(25).forEach(System.out::println);

    }

    public static void main(String[] args) {
        new ThirtyTwo().run(args[0]);
    }


    public static class Pair {
        public String key;
        public int value;

        public Pair(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {return this.key;}
        public int getValue() {return this.value;}

        public String toString() {
            return key + "   -   " + value;
        }
    }
}
