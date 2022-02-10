import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Fifteen {

    public interface Handler {
        default void run() {
            System.out.println("run");
        }

        default void run(String word) {
            System.out.println(word);
        }
    }

    static class WFFrameWork{
        private List<Handler> loadEventHandlerList = new ArrayList<>();
        private List<Handler> processEventHandlerList = new ArrayList<>();
        private List<Handler> endEventHandlerList = new ArrayList<>();

        public void registerLoadHandler(Handler handler) {
            this.loadEventHandlerList.add(handler);
        }

        public void registerProcessHandler(Handler handler) {
            this.processEventHandlerList.add(handler);
        }

        public void registerEndHandler(Handler handler) {
            this.endEventHandlerList.add(handler);
        }

        public void run(String path){
            this.loadEventHandlerList.forEach(handler -> handler.run(path));

            this.processEventHandlerList.forEach(Handler::run);

            this.endEventHandlerList.forEach(Handler::run);
        }
    }

    static class DataStorage {
        private List<String> words;
        private StopWordFilter stopWordFilter;
        private List<Handler> eventHandler = new ArrayList<>();


        public DataStorage(WFFrameWork wf, StopWordFilter stopWordFilter){
            this.stopWordFilter = stopWordFilter;
            wf.registerLoadHandler(new LoadHandler());
            wf.registerProcessHandler(new ProduceWordsHandler());
        }

        public void registerProcessHandler(Handler handler) {
            this.eventHandler.add(handler);
        }

        class LoadHandler implements Handler {
            @Override
            public void run(String path) {
                try {
                    try (Stream<String> lines = Files.lines(Paths.get(path))) {
                        words = new ArrayList<>();
                        lines.forEach(line -> {
                            String[] lineWord = line.toLowerCase().split("[^a-zA-Z0-9]+");
                            words.addAll(Arrays.asList(lineWord));
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        class ProduceWordsHandler implements Handler {
            @Override
            public void run() {
                for (String word : words) {
                    if(!stopWordFilter.isStopWord(word)) {
                        eventHandler.forEach(handler -> handler.run(word));
                    }
                }
            }
        }
    }

    static class StopWordFilter {

        List<String> stopWords = new ArrayList<>();

        public StopWordFilter(WFFrameWork wf) {
            wf.registerLoadHandler(new LoadHandler());
        }

        class LoadHandler implements Handler{
            @Override
            public void run(String path) {
                String str = "";
                try {
                    byte[] encoded = Files.readAllBytes(Paths.get("../stop_words.txt"));
                    str = new String(encoded);
                } catch (IOException e) {
                    System.out.println("Error reading stop_words");
                }
                String[] stopwords = str.split(",");
                stopWords.addAll(Arrays.asList(stopwords));
            }
        }

        boolean isStopWord(String word) {
            return stopWords.contains(word) || word.length() < 2;
        }
    }

    static class WFCounter {
        HashMap<String, Integer> frequencies = new HashMap<>();

        public WFCounter(WFFrameWork wf, DataStorage data) {
            data.registerProcessHandler(new FreqCountHandler());
            wf.registerEndHandler(new PrintHandler());
        }

        class FreqCountHandler implements Handler {
            @Override
            public void run(String word) {
                if (frequencies.containsKey(word))
                    frequencies.put(word, frequencies.get(word) + 1);
                else
                    frequencies.put(word, 1);
            }
        }

        class PrintHandler implements Handler {
            @Override
            public void run() {
                frequencies.entrySet()
                        .stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                        .limit(25)
                        .forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));
            }
        }
    }

    static class WFzCounter {
        int zCount = 0;

        public WFzCounter(WFFrameWork wf, DataStorage data) {
            data.registerProcessHandler(new FreqCountHandler());
            wf.registerEndHandler(new PrintHandler());
        }

        class FreqCountHandler implements Handler {
            @Override
            public void run(String word) {
                if(word.contains("z")) {
                    zCount++;
                }
            }
        }

        class PrintHandler implements Handler {
            @Override
            public void run() {
                System.out.println( "\n\n--------------------\n" +
                                    "      Count z\n" +
                                    "--------------------\n");
                System.out.println("Word contains z: " +zCount + "\n\n\n\n");
            }
        }
    }

    public static void main(String[] args) {
        WFFrameWork wf = new WFFrameWork();
        StopWordFilter stopWordFilter = new StopWordFilter(wf);
        DataStorage data = new DataStorage(wf, stopWordFilter);
        WFCounter wfCounter = new WFCounter(wf, data);
        WFzCounter wFzCounter = new WFzCounter(wf, data);
        wf.run(args[0]);
    }
}
