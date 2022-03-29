import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class TwentyNine {

    public static final int QUEUE_SIZE = 160000;

    public static void send(ActiveWFObject receiver, Object[] msg) {
        receiver.blockingQueue.add(msg);
    }

    public abstract static class ActiveWFObject implements Runnable {

        public BlockingQueue<Object[]> blockingQueue;
        public Boolean stopMe;
        public String name;

        public ActiveWFObject() {
            this.name = this.getClass().getName();
            this.blockingQueue =  new ArrayBlockingQueue<Object[]>(QUEUE_SIZE);
            this.stopMe = false;
        }

        abstract public void dispatch(Object[] msg) throws Exception;

        @Override
        public void run() {
            while(!this.stopMe) {
                try {
                    Object[] message = this.blockingQueue.poll(1, TimeUnit.SECONDS);
                    if(message != null) {
                        this.dispatch(message);
                        if(message[0].equals("die")) this.stopMe = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class DataStorageManager extends ActiveWFObject {
        public List<String> data;
        public StopWordManager stopWordManager;


        @Override
        public void dispatch(Object[] msg) {
            if(msg[0].equals("init")) {
                init(new Object[]{msg[1], msg[2]});
            } else if(msg[0].equals("send_word_freqs")) {
                processWord(msg[1]);
            } else {
                send(this.stopWordManager, msg);
            }
        }

        public void init(Object[] msg) {
            data = new ArrayList<>();
            try {
                Stream<String> lines = Files.lines(Paths.get((String) msg[0]));
                this.stopWordManager = (StopWordManager) msg[1];
                data = lines.flatMap(line -> Arrays.stream(line.split("[^a-zA-Z0-9]+")))
                        .map(String::toLowerCase)
                        .filter(word -> word.length() > 1)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void processWord(Object msg) {
            WordFrequencyController recipient = (WordFrequencyController) msg;
            for (String word : data)
                send(this.stopWordManager, new Object[]{"filter", word});
            send(this.stopWordManager, new Object[]{"top25", recipient});
        }
    }

    public class StopWordManager extends ActiveWFObject {
        private List<String> stopWords;
        private WordFrequencyManager wordFrequencyManager;

        @Override
        public void dispatch(Object[] msg) {
            if (msg[0].equals("init"))
                init(msg[1]);
            else if (msg[0].equals("filter")) {
                filter(msg[1]);
            } else
                send(this.wordFrequencyManager, msg);
        }

        private void init(Object msg) {
            try {
                this.wordFrequencyManager = (WordFrequencyManager) msg;
                stopWords = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void filter(Object msg) {
            String word = (String) msg;
            if (!stopWords.contains(word))
                send(this.wordFrequencyManager, new Object[]{"word", word});
        }
    }

    public class WordFrequencyManager extends ActiveWFObject {
        public Map<String, Integer> wordFreqs;

        WordFrequencyManager() {
            wordFreqs = new HashMap<>();
        }

        @Override
        public void dispatch(Object[] msg) {
            if (msg[0].equals("word"))
                incrementCount(msg[1]);
            else if (msg[0].equals("top25"))
                top25(msg[1]);
        }

        private void incrementCount(Object msg) {
            String word = (String) msg;

            if (wordFreqs.containsKey(word))
                wordFreqs.put(word, wordFreqs.get(word) + 1);
            else
                wordFreqs.put(word, 1);
        }

        private void top25(Object msg) {
            WordFrequencyController recipient = (WordFrequencyController) msg;
            List<Map.Entry<String, Integer>> freqsSorted = wordFreqs.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(p -> -p.getValue()))
                    .limit(25)
                    .collect(Collectors.toList());
            send(recipient, new Object[]{"top25", freqsSorted});
        }
    }

    public class WordFrequencyController extends ActiveWFObject {
        DataStorageManager dataStorageManager;

        @Override
        public void dispatch(Object[] msg) throws Exception {
            if (msg[0].equals("run"))
                run(msg[1]);
            else if (msg[0].equals("top25"))
                display(msg[1]);
            else
                throw new Exception("Message not understood " + msg[0]);
        }

        public void run(Object msg) {
            this.dataStorageManager = (DataStorageManager) msg;
            send(this.dataStorageManager, new Object[]{"send_word_freqs", this});
        }

        public void display(Object msg){
            List<Map.Entry<String, Integer>> top25 = (List<Map.Entry<String, Integer>>) msg;
            for (Map.Entry<String, Integer> entry : top25) {
                System.out.println(entry.getKey() + "   -   " + entry.getValue());
            }
            send(this.dataStorageManager, new Object[]{"die"});
            this.stopMe = true;
        }
    }

    public void run(String arg) {
        WordFrequencyManager wfm = new WordFrequencyManager();

        StopWordManager swm = new StopWordManager();
        send(swm, new Object[]{"init", wfm});

        DataStorageManager dsm = new DataStorageManager();
        send(dsm, new Object[]{"init", arg, swm});

        WordFrequencyController wfc = new WordFrequencyController();
        send(wfc, new Object[]{"run", dsm});

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Runnable[] runnables = new ActiveWFObject[]{wfm, swm, dsm, wfc};
        Collection<Future<?>> tasks = new LinkedList<>();
        for(Runnable runnable: runnables) {
            Future<?> future = executorService.submit(runnable);
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

        executorService.shutdown();
    }

    public static void main(String[] args) {
        new TwentyNine().run(args[0]);
    }
}
