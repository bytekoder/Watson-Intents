import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bshekhawat
 */
public class RegressionTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegressionTool.class);
    private static int counter = 0;
    private static IntentClient intentClient;
    private IntentResponse intentResponse;

    static {
        System.out.println("Regression Tool Started");
        String workingDir = System.getProperty("user.dir");
        System.out.println("Your working directory is: " + workingDir);
    }


    public RegressionTool(final String username, final String password) {
        this.intentResponse = new IntentResponse();
        init();
        intentClient.init(username, password);
    }

    /**
     * @param workspaceId
     * @param fileLocation
     * @param outFileName
     * @param debug
     * @throws IOException
     */
    public void transferToCsv(final String workspaceId, final String fileLocation, final String outFileName, final String debug) throws
            IOException {

        CSVReader csvReader = new CSVReader(new FileReader(fileLocation), ',', '\"', 1);
        CSVWriter csvWriter = new CSVWriter(new FileWriter(outFileName), ',');
        String[] header = ("Utterance,ExpectedIntent,FoundIntent,ExpectedConfidence,FoundConfidence,IntentResult,ConfidenceResult," +
                "Channel," + "WatsonConversationId,WatsonDialogStack, WatsonOutputText").split(",");

        String[] nextLine;
        String utterance;
        String intent;
        String channel;

        double confidence;
        boolean hasIntentFailed;
        boolean hasConfidenceFailed;

        List<String[]> data = new ArrayList<>();
        data.add(header);

        while ((nextLine = csvReader.readNext()) != null) {
            utterance = nextLine[0].trim();
            confidence = Double.parseDouble(nextLine[1]);
            intent = nextLine[2].trim();
            channel = nextLine[10].trim();

            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            double roundedConfidence = Double.valueOf(decimalFormat.format(confidence * 100));

            intentResponse = intentClient.getIntent(utterance.replaceAll("\t", " ").replaceAll("\\n", ""), workspaceId);

            String foundIntent = intentResponse.getIntentName();
            double foundConfidence = Double.valueOf(decimalFormat.format(intentResponse.getConfidence() * 100));

            String watson = intentResponse.getWatsonMessage();

            JSONObject obj = new JSONObject(watson);
            String conversationId = obj.getJSONObject("context").getString("conversation_id");
            String dialogStack = obj.getJSONObject("context").getJSONObject("system").getJSONArray("dialog_stack").toString();
            String watsonOutput = obj.getJSONObject("output").getJSONArray("text").toString();

            hasIntentFailed = !intent.equals(foundIntent);

            hasConfidenceFailed = !(roundedConfidence < (intentResponse.getConfidence() * 100));

            if (debug.equals("true")) {

                LOGGER.info("Utterance: {} Exp. Intent: {} Found Intent: {} Exp. Confidence: {} Found Confidence: {} Intent " + "Result: " +
                        "" + "" + "" + "{} Confidence Result: {} Channel: {} ", utterance, intent, foundIntent, roundedConfidence,
                        intentResponse.getConfidence() * 100, (hasIntentFailed ? "Fail" : "Pass") + ", " + (hasConfidenceFailed ? "Fail"
                                : "Pass"), channel);
            }

            data.add((utterance.replaceAll(",", "") + ", " + intent + ", " + foundIntent + ", " + roundedConfidence + ", " +
                    (foundConfidence) + ", " + (hasIntentFailed ? "INTENT FAILED" : "INTENT PASSED") + ", " + (hasConfidenceFailed ?
                    "CONFIDENCE FAILED" : "CONFIDENCE PASSED") + ", " + channel + "," + conversationId + "," + dialogStack + "," + watsonOutput)
                    .split(","));

            increment();
        }

        LOGGER.info("Total tests ran: {}", counter);
        LOGGER.info("Analysis complete. Now writing to file: " + outFileName);
        csvWriter.writeAll(data);
        csvWriter.close();
    }

    private static void init() {
        intentClient = new IntentClient();
    }

    private static void increment() {
        counter++;
    }


    public static boolean checkForFile(final String fileName) {

        boolean doesFileExist;
        File f = new File(fileName);
        if (f.exists() && !f.isDirectory()) {
            LOGGER.info("File found: " + fileName);
            doesFileExist = true;
        } else {
            doesFileExist = false;
        }

        return doesFileExist;
    }


    public static void main(String[] args) {

        BasicConfigurator.configure();
        if (args.length < 6) {
            LOGGER.error("Not enough options specified. Exiting... ");
            LOGGER.info("Usage: <username> <password> <workspaceid> <csvfile> <outputfile>");
            System.exit(0);
        }

        RegressionTool regressionTool = new RegressionTool(args[0], args[1]);
        LOGGER.info("Checking Client...");
        System.out.println();

        if (!checkForFile(args[3])){
            LOGGER.error("File not found! Try again.");
            System.exit(0);
        }

        try {
            LOGGER.info("Running Regression Tests...");
            regressionTool.transferToCsv(args[2], args[3], args[4], args[5]);
        } catch (IOException e) {
            e.getCause();
        }
    }

}
