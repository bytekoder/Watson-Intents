import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.List;

/**
 * Created by bshekhawat
 */
public class IntentClient {

    private final static String URL = "https://gateway.watsonplatform.net/conversation/api";

    private ConversationService service;

    public void init(String username, String password) {
        service = new ConversationService(ConversationService.VERSION_DATE_2016_07_11, username, password);
        System.out.println("Conversation service initialized.");
    }


    public IntentResponse getIntent(String inputText, String workspaceId) {

        MessageRequest newMessage = new MessageRequest.Builder()
                .inputText(inputText)
                .build();

        MessageResponse response = service
                .message(workspaceId, newMessage)
                .execute();

        Intent intent = getIntent(response.getIntents());

        IntentResponse myIntent = new IntentResponse();
        myIntent.setConfidence(intent.getConfidence());
        myIntent.setInputText(inputText);
        myIntent.setIntentName(intent.getIntent());
        myIntent.setWatsonMessage(String.valueOf(response));

        return myIntent;
    }

    private Intent getIntent(List<Intent> intents) {
        double maxConfidence = -1;
        Intent bestIntent = null;

        for (Intent intent : intents) {
            if (intent.getConfidence() > maxConfidence) {
                bestIntent = intent;
                maxConfidence = intent.getConfidence();
            }
        }

        return bestIntent;

    }

}
