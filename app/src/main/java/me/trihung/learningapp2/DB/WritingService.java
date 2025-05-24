package me.trihung.learningapp2.DB;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.trihung.learningapp2.Entity.type.EnglishLevel;
import me.trihung.learningapp2.Entity.dto.ReviewRequest;
import me.trihung.learningapp2.Entity.dto.ReviewResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WritingService {
    private static final String BASE_URL = "https://molly-huge-tahr.ngrok-free.app/generate_review";
    private static final Gson gson = new GsonBuilder().create();

    public interface ReviewCallback {
        void onSuccess(String review);
        void onFailure(String error);
    }

    public static void generateReview(EnglishLevel level, String requirement, String content, ReviewCallback callback) {
        ReviewRequest requestObj = new ReviewRequest(level.getLevelCode(), requirement, content);
        String json = gson.toJson(requestObj);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)   // Set connection timeout
                .writeTimeout(60, TimeUnit.SECONDS)     // Set write timeout
                .readTimeout(60, TimeUnit.SECONDS)      // Set read timeout
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure(e.getMessage())
                );
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    ReviewResponse reviewResponse = gson.fromJson(responseBody, ReviewResponse.class);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(reviewResponse.getReview())
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
