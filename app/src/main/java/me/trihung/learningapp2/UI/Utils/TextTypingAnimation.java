package me.trihung.learningapp2.UI.Utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import io.noties.markwon.Markwon;


public class TextTypingAnimation {

    private static final int DEFAULT_TYPE_SPEED = 10; // milliseconds per character

    public static void animateTyping(Markwon markwon, TextView textView, String text, int typeSpeed, int charsPerFrame) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final StringBuilder stringBuilder = new StringBuilder();
        final int[] index = {0};

        textView.setText("");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int c = 0; c < charsPerFrame && index[0] < text.length(); c++) {
                    stringBuilder.append(text.charAt(index[0]));
                    index[0]++;
                }

                markwon.setMarkdown(textView, stringBuilder.toString());


                if (index[0] < text.length()) {
                    handler.postDelayed(this, typeSpeed);
                }
            }
        };

        handler.postDelayed(runnable, typeSpeed);
    }


    public static void animateTyping(Markwon markwon, TextView textView, String text, int characters) {
        animateTyping(markwon, textView, text, DEFAULT_TYPE_SPEED, characters);
    }
}