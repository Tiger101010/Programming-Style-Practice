import java.util.Map;
import java.util.stream.Stream;

// The interface for frequcency count
// The interface will be packed into ITF.jar, which is given to plugin developer
public interface IFreq {
    Stream<Map.Entry<String, Integer>> freqCount(Stream<String> words);
}
