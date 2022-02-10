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

        try {
            properties.load(new FileInputStream("config.properties"));
            String[] word = properties.getProperty("word").split(",");
            String[] freq = properties.getProperty("freq").split(",");
            String path = properties.getProperty("jarPath");

            IWord wordLoader;
            IFreq wordFreq;

            for(int i = 0; i < 2; i++) {
                wordLoader = (IWord) new URLClassLoader(new URL[]{new File(path + "/" + word[i] + ".jar").toURI().toURL()})
                        .loadClass(word[i])
                        .newInstance();
                for(int j = 0; j < 2; j++) {
                    wordFreq = (IFreq) new URLClassLoader(new URL[]{new File(path + "/" + freq[j] + ".jar").toURI().toURL()})
                            .loadClass(freq[j])
                            .newInstance();
                    System.out.println( "-------------------\n  " + 
                                        word[i] + " + " + freq[j] + "\n" +
                                        "-------------------");
                    top25(args[0], wordLoader, wordFreq);
                }
            }


        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
