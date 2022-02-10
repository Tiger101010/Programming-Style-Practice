import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Freq1 implements IFreq {
    @Override
    public Stream<Map.Entry<String, Integer>> freqCount(Stream<String> words) {
        return words.collect(Collectors.toMap(String::toLowerCase, w -> 1, Integer::sum))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(25);
    }
}
