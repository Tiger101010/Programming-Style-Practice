import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Word2 implements IWord {
    @Override
    public Stream<String> loadWords(String filePath) {
        try{
            List<String> stop_words = Arrays.asList(new String(Files.readAllBytes(Paths.get("../../../stop_words.txt"))).split(","));
            
            return Arrays.asList(new String(Files.readAllBytes(Paths.get(filePath)))
                    .split("[^a-zA-Z]+"))
                    .stream()
                    .filter(s -> !(stop_words.contains(s.toLowerCase()) || (s.length() < 2)))
                    .filter(s -> s.contains("z"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
