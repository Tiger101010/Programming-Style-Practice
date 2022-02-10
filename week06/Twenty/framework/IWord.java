import java.util.stream.Stream;

public interface IWord {
    Stream<String> loadWords(String filePath);
}
