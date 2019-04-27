import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bshekhawat
 */
public class IntentTestHelper {

    public static final String TXT_FILE = "src/test/resources/testdata.txt";
    public static final String CSV_FILE = "src/test/resources/sample-data.csv";


    /**
     * Reads the text file line by line
     *
     * @return {@code Stream<String>}
     * @throws IOException
     */
    public static Stream<String> fileStreamer() throws IOException {
        return Files.lines(Paths.get(TXT_FILE), Charset.defaultCharset())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet())
                .stream();
    }


    /**
     * Extracts and maps test values from a string representation of each test line
     *
     * @throws IOException
     */
    public static List<Object[]> fileToObjectMapper() throws IOException {
        return fileStreamer().map(String::valueOf)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .map(s -> s.split(","))
                .map(array -> new Object[]{array[0], array[1], array[2]})
                .collect(Collectors.toList());
    }

    /**
     * Builds an output String representation of test data comparison
     *
     * @param utterance  the input text {@code String}
     * @param intent     Intent {@link Intent} behind the utterance
     * @param confidence a number that measures the correlation between utterance and intent
     * @param isReal
     * @return a {@code StringBuilder} representation of tests comparisons
     */
    public static StringBuilder testTemplateBuiilder(String utterance, String intent, double confidence, boolean isReal) {

        StringBuilder stringBuilder = new StringBuilder()
                .append(utterance)
                .append(", ")
                .append(intent)
                .append(", ");

        if (isReal) {
            stringBuilder.append(confidence * 100);
        } else {
            stringBuilder.append(confidence);
        }
        return stringBuilder;

    }



}
