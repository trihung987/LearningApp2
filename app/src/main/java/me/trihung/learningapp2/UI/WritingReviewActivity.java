package me.trihung.learningapp2.UI;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

import io.noties.markwon.Markwon;
import me.trihung.learningapp2.DB.WritingService;
import me.trihung.learningapp2.Entity.type.EnglishLevel;
import me.trihung.learningapp2.R;
import me.trihung.learningapp2.UI.Utils.TextTypingAnimation;

public class WritingReviewActivity extends AppCompatActivity {

    private TextInputEditText topicEditText, contentEditText;
    private AutoCompleteTextView englishLevelAutoComplete;
    private MaterialButton submitButton;
    private ImageButton btnBack;
    private CardView resultCardView, formCardView;
    private TextView resultTopicTextView, resultLevelTextView, resultContentTextView;
    private ConstraintLayout loadingOverlay;
    private Animation slideDownAnimation, textAppearAnimation, formAppearAnimation;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_writing);

        // Initialize views
        topicEditText = findViewById(R.id.topicEditText);
        contentEditText = findViewById(R.id.contentEditText);
        englishLevelAutoComplete = findViewById(R.id.englishLevelAutoComplete);
        submitButton = findViewById(R.id.submitButton);
        resultCardView = findViewById(R.id.resultCardView);
        formCardView = findViewById(R.id.formCardView);
        resultTopicTextView = findViewById(R.id.resultTopicTextView);
        resultLevelTextView = findViewById(R.id.resultLevelTextView);
        resultContentTextView = findViewById(R.id.resultContentTextView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_animation);
        textAppearAnimation = AnimationUtils.loadAnimation(this, R.anim.text_appear_animation);
        formAppearAnimation = AnimationUtils.loadAnimation(this, R.anim.form_appear_animation);

        formCardView.startAnimation(formAppearAnimation);

        String[] levelNames = Arrays.stream(EnglishLevel.values())
                .map(EnglishLevel::getName)
                .toArray(String[]::new);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                levelNames
        );
        englishLevelAutoComplete.setAdapter(adapter);
        Markwon markwon = Markwon.create(this);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingOverlay.getVisibility()== View.VISIBLE)
                    return;
                if (resultCardView.getVisibility() == View.VISIBLE) {
                    resultCardView.setVisibility(View.GONE);
                }

                String topic = topicEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                String levelName = englishLevelAutoComplete.getText().toString().trim();

                EnglishLevel selectedLevel = Arrays.stream(EnglishLevel.values())
                        .filter(l -> l.getName().equalsIgnoreCase(levelName))
                        .findFirst()
                        .orElse(null);

                if (selectedLevel == null || topic.isEmpty() || content.isEmpty()) {
                    Toast.makeText(WritingReviewActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingOverlay.setVisibility(View.VISIBLE);

                Animator pulseAnimation = AnimatorInflater.loadAnimator(
                        WritingReviewActivity.this,
                        R.animator.pulse_animation);
                pulseAnimation.setTarget(progressBar);
                pulseAnimation.start();

                WritingService.generateReview(selectedLevel, topic, content, new WritingService.ReviewCallback() {
                    @Override
                    public void onSuccess(String review) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingOverlay.setVisibility(View.GONE);

                                resultTopicTextView.setText(topic);
                                resultLevelTextView.setText(selectedLevel.getName());

                                resultContentTextView.setText("");

                                resultCardView.setVisibility(View.VISIBLE);
                                resultCardView.startAnimation(slideDownAnimation);

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    resultTopicTextView.startAnimation(textAppearAnimation);
                                    resultLevelTextView.startAnimation(textAppearAnimation);

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        TextTypingAnimation.animateTyping(markwon, resultContentTextView, review, 5);
                                    }, 400);
                                }, 300);
                            }
                        }, 1500); // 1.5 s delay
                    }

                    @Override
                    public void onFailure(String error) {
                        // Hide loading overlay
                        loadingOverlay.setVisibility(View.GONE);

                        Toast.makeText(WritingReviewActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnBack.setOnClickListener(v->{
            finish();
        });
    }


}