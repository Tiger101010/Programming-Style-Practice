import java.util.Map;
import java.util.stream.Stream;

public interface IFreq {
    Stream<Map.Entry<String, Integer>> freqCount(Stream<String> words);
}
