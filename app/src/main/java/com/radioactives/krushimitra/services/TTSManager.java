package com.radioactives.krushimitra.services;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class TTSManager implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean isReady = false;
    private Context context;

    public TTSManager(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);  // You can change to Locale("hi", "IN")
            isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                      result != TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    public void speak(String text) {
        if (isReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
