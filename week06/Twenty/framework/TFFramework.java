import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class TFFramework {

    public static void top25(String filePath, IWord wordLoader, IFreq wordFreq) {
        wordFreq.freqCount(wordLoader.loadWords(filePath)).forEach(System.out::println);
    }

    public static void main(String[] args) {

        Properties properties = new Properties();

        System.out.println(
            "---------------------------------------------------------------------\n" +
            "You can change module you want to use in config.properties file.\n" +
            "For Word Loading: Word1, Word2\n" +
            "For Frequency Counting: Freq1, Freq2\n" +
            "---------------------------------------------------------------------\n"
        );

        try {
            // Read plugin and jar path in config file
            properties.load(new FileInputStream("config.properties"));
            String word = properties.getProperty("word");
            String freq = properties.getProperty("freq");
            String path = properties.getProperty("jarPath");

            // Create two interface for our task
            IWord wordLoader;
            IFreq wordFreq;

            // dynamically load plugin when running
            wordLoader = (IWord) new URLClassLoader(new URL[]{new File(path + "/" + word + ".jar").toURI().toURL()})
                .loadClass(word)
                .newInstance();
            wordFreq = (IFreq) new URLClassLoader(new URL[]{new File(path + "/" + freq + ".jar").toURI().toURL()})
                .loadClass(freq)
                .newInstance();

            System.out.println( "-------------------\n  " + 
                                word + " + " + freq + "\n" +
                                "-------------------");
            top25(args[0], wordLoader, wordFreq);


        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
