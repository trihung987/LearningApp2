package me.trihung.learningapp2.DB;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.trihung.learningapp2.Entity.dto.SearchRequest;
import me.trihung.learningapp2.Entity.dto.SearchResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VocabularyService {
    private static final String BASE_URL = "https://molly-huge-tahr.ngrok-free.app/generate_search";
    private static final Gson gson = new GsonBuilder().create();

    public interface SearchCallback {
        void onSuccess(String content);
        void onFailure(String error);
    }

    public static void searchVocabulary(String keyword, String context, SearchCallback callback) {
        SearchRequest requestObj = new SearchRequest(keyword, context);
        String json = gson.toJson(requestObj);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Network error: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    try {
                        SearchResponse searchResponse = gson.fromJson(responseBody, SearchResponse.class);
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onSuccess(searchResponse.getContent())
                        );
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("Parse error: " + e.getMessage())
                        );
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Server error: " + response.code() + " " + response.message())
                    );
                }
            }
        });
    }
}