import java.util.stream.Stream;

// the interface for word loading
// The interface will be packed into ITF.jar, which is given to plugin developer
public interface IWord {
    Stream<String> loadWords(String filePath);
}
