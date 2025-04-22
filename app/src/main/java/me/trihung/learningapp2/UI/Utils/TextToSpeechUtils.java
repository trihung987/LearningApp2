package me.trihung.learningapp2.UI.Utils;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class TextToSpeechUtils {
    TextToSpeech tts;
    public TextToSpeechUtils(Context context){

        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = tts.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle unsupported language
                }
            }
        });
    }

    public void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
