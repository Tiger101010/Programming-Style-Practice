import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Arrays.asList;

public class Seven {
    /** Clear Version

    List<String> stop_words = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
    
    asList(new String(Files.readAllBytes(Paths.get(args[0])))
            .split("[^a-zA-Z0-9]+"))
            .parallelStream()
            .filter(s -> !(stop_words.contains(s.toLowerCase()) || (s.length() < 2)))
            .collect(Collectors.toConcurrentMap(String::toLowerCase, w -> 1, Integer::sum))
            .entrySet()
            .stream()
            .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
            .limit(25)
            .forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));

    */
    public static void main(String[] args) throws IOException {
        // Java 2 lines solution
        // code-golf
        List<String> stop_words = asList(new String(Files.readAllBytes(Paths.get("../stop_words.txt"))).split(","));
        asList(new String(Files.readAllBytes(Paths.get(args[0]))).split("[^a-zA-Z0-9]+")).parallelStream().filter(s -> !(stop_words.contains(s.toLowerCase()) || (s.length() < 2))).collect(Collectors.toConcurrentMap(String::toLowerCase, w -> 1, Integer::sum)).entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).limit(25).forEach(entry -> System.out.println(entry.getKey() + "   -   " + entry.getValue()));
    }
}
