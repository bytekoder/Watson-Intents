import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * @author bshekhawat
 */
public class IntentRegressionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntentRegressionTest.class);
    private static final double EXPECTED_CONFIDENCE = 90.0;

    private IntentClient intentClient;
    private IntentResponse intentResponse;

    @Before
    public void setUp() {

        intentClient = new IntentClient();
        intentResponse = new IntentResponse();
        //intentClient.init();
    }

    @After
    public void tearDown() {

        intentResponse = null;
        intentClient = null;
    }

    @Test
    @Ignore
    public void testIntentAndConfidence() throws IOException {
        String utterance;
        String intent;
        double confidence;

        List<Object[]> array = IntentTestHelper.fileToObjectMapper();
        for (Object[] objects : array) {
            utterance = ((String) objects[0]).trim();
            intent = ((String) objects[1]).trim();
            confidence = Double.parseDouble((String) objects[2]);

            intentResponse = intentClient.getIntent(utterance, "");

            LOGGER.info("Expected: {} {} {}", utterance, intent, confidence);
            LOGGER.info("Actual: {} {} {}", intentResponse.getInputText(), intentResponse.getIntentName(), intentResponse.getConfidence());

            System.out.println("Expected: " + IntentTestHelper.testTemplateBuiilder(utterance, intent, confidence, false));
            System.out.println("Found: " + IntentTestHelper.testTemplateBuiilder(intentResponse.getInputText(), intentResponse.getIntentName(), intentResponse.getConfidence(), true));
            System.out.println();

            Assert.assertEquals("Comparing Intent", intent.trim(), intentResponse.getIntentName());
            assertThat("Comparing Confidence Levels", intentResponse.getConfidence() * 100, greaterThanOrEqualTo(confidence));

        }
    }

}
