package bottalk.com.bottalk;

import android.content.Context;
import android.speech.tts.TextToSpeech;

/**
 * Created by Yashaswini on 7/12/17.
 */

public class TextToSpeechConverter {

    private static TextToSpeech textToSpeech;

    public static void init(final Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {

                }
            });
        }
    }

    public static void speak(final String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
