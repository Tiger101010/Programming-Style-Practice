import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static java.util.Arrays.asList;

public class Thirty {
    private static BlockingDeque<String> wordSpace;
    private static BlockingDeque<Map<String, Integer>> freqSpace;
    private static List<String> stopWords;

    private static final int QUEUE_SIZE = 160000;
    private static final int THREAD_NUM = 8;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
    Collection<Future<?>> tasks = new LinkedList<>();

    public void run(String filePath) {
        wordSpace = new LinkedBlockingDeque<>(QUEUE_SIZE);
        freqSpace = new LinkedBlockingDeque<>(QUEUE_SIZE);

        try {
            stopWords = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
            Files.lines(Paths.get(filePath))
                    .flatMap(line -> Arrays.stream(line.split("[^a-zA-Z0-9]+")))
                    .map(String::toLowerCase)
                    .filter(word -> word.length() > 1)
                    .forEach(wordSpace::add);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Runnable processWords = () -> {
            Map<String, Integer> wordFreq = new HashMap<>();
            while(!wordSpace.isEmpty()) {
                try {
                    String word = wordSpace.poll(1, TimeUnit.SECONDS);
                    if(!stopWords.contains(word)) {
                        if(wordFreq.containsKey(word)) {
                            wordFreq.put(word, wordFreq.get(word) + 1);
                        } else {
                            wordFreq.put(word, 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {
                freqSpace.put(wordFreq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // submit process words task
        for(int i = 0; i < THREAD_NUM; ++i) {
            Future<?> future = executorService.submit(processWords);
            tasks.add(future);
        }

        // wait for threads
        for (Future<?> task: tasks) {
            try {
                task.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // merge frequency map
        Map<String, Integer> mergedFreq = new HashMap<>();
        while (!freqSpace.isEmpty()) {
            try {
                Map<String, Integer> freq = freqSpace.poll(1, TimeUnit.SECONDS);
                for (Map.Entry<String, Integer> entry : freq.entrySet()) {
                    String key = entry.getKey();
                    Integer count;
                    if (mergedFreq.containsKey(key)) {
                        count = freq.get(key) + mergedFreq.get(key);
                    } else {
                        count = freq.get(key);
                    }
                    mergedFreq.put(key, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mergedFreq.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(25)
                .forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));

        executorService.shutdown();
    }


    public static void main(String[] args) {
        try {
            new Thirty().run(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
