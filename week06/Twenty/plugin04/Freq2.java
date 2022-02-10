import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Freq2 implements IFreq {
    @Override
    public Stream<Map.Entry<String, Integer>> freqCount(Stream<String> words) {
        return words.collect(Collectors.toMap(w -> w.toLowerCase().substring(0, 1), w -> 1, Integer::sum))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()));
    }
}
