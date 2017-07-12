package bottalk.com.bottalk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.ui.AIDialog;

import com.google.gson.JsonElement;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener ,AIListener{

    private AIService aiService;
    private TextView resultTextview;
    private AIDialog aiDialog;
    AIConfiguration aiConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_listen).setOnClickListener(this);
        findViewById(R.id.btn_hello).setOnClickListener(this);
        findViewById(R.id.btn_leave).setOnClickListener(this);
        findViewById(R.id.btn_leave_approval).setOnClickListener(this);
        resultTextview = (TextView)findViewById(R.id.result_textview);
         TextToSpeechConverter.init(this);
         aiConfiguration = new AIConfiguration("fd32b0a793874d49ac41d8c137c222a2", AIConfiguration.SupportedLanguages.English,AIConfiguration.RecognitionEngine.System);

        //aiService = AIService.getService(this, aiConfiguration);
        //aiService.setListener(this);






    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_listen:

                startListening();
                break;
            case R.id.btn_hello:
                final AIRequest aiRequest = new AIRequest();
                aiRequest.setQuery("Hello BotTalk");
                executeAIRequest(aiRequest);
                break;
            case R.id.btn_leave:
                final AIRequest aiRequest1 = new AIRequest();
                aiRequest1.setQuery("Leave");
                executeAIRequest(aiRequest1);
                break;

            case R.id.btn_leave_approval:
                final AIRequest aiRequest2 = new AIRequest();
                aiRequest2.setQuery("Leave Approval");
                executeAIRequest(aiRequest2);
                break;
        }

    }

    @Override
    public void onResult(AIResponse response) {

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }


    public void startListening(){
        aiDialog = new AIDialog(this,aiConfiguration);
        startRecognition();
        aiDialog.setResultsListener(new AIDialog.AIDialogListener() {
            @Override
            public void onResult(AIResponse response) {
                    Log.d("Response ",response.toString());
            }

            @Override
            public void onError(AIError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                aiDialog.close();
            }

            @Override
            public void onCancelled() {

            }
        });

    }

    public void startRecognition(){
       aiDialog.showAndListen();
    }


    private void executeAIRequest(AIRequest request){

        final AIDataService aiDataService = new AIDataService(this,aiConfiguration);
        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(request);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    final Result result = aiResponse.getResult();
                    String parameterString = "";
                    if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                        for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                            parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                        }
                    }
                    resultTextview.setText("Query:" + result.getResolvedQuery() +
                            "\nAction: " + result.getAction() +"\nfulfillment: " + result.getFulfillment().getSpeech()+
                            "\nParameters: " + parameterString);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextToSpeechConverter.speak(result.getFulfillment().getSpeech());
                        }
                    });
                }
            }
        }.execute(request);

    }
}
