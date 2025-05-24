package me.trihung.learningapp2.DB;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import me.trihung.learningapp2.Entity.dto.AccuracyRequest;
import me.trihung.learningapp2.Entity.dto.AccuracyResponse;
import me.trihung.learningapp2.Entity.dto.SampleRequest;
import me.trihung.learningapp2.Entity.dto.SampleResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpeakingPronounService {
    private static final String BASE_URL = "https://molly-huge-tahr.ngrok-free.app";
    private static final String SAMPLE_URL = "/getSample";
    private static final String ACCURACY_URL = "/GetAccuracyFromRecordedAudio";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().create();

    public interface SampleCallback {
        void onSuccess(List<String> realTranscript, String ipaTranscript, String transcriptTranslation);
        void onFailure(String error);
    }

    public static void getSample(String category, String language, SampleCallback callback) {
        SampleRequest requestObj = new SampleRequest(category, language);
        String json = gson.toJson(requestObj);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL+SAMPLE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure(e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    SampleResponse sampleResponse = gson.fromJson(responseBody, SampleResponse.class);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(
                                    sampleResponse.getRealTranscript(),
                                    sampleResponse.getIpaTranscript(),
                                    sampleResponse.getTranscriptTranslation()
                            )
                    );
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Error: " + response.code())
                    );
                }
            }
        });
    }

    public interface AccuracyCallback {
        void onSuccess(AccuracyResponse accuracyResponse);
        void onFailure(String error);
    }

    public static void getAccuracy(String base64Audio, String language, String title, AccuracyCallback callback) {
        AccuracyRequest requestObj = new AccuracyRequest(base64Audio, language, title);
        String json = gson.toJson(requestObj);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL+ACCURACY_URL)

                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure(e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    AccuracyResponse accuracyResponse = gson.fromJson(responseBody, AccuracyResponse.class);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(accuracyResponse)
                    );
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Error: " + response.code())
                    );
                }
            }
        });
    }
}