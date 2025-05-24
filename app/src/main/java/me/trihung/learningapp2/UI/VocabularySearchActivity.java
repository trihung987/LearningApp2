package me.trihung.learningapp2.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.noties.markwon.Markwon;
import me.trihung.learningapp2.DB.VocabularyService;
import me.trihung.learningapp2.R;

public class VocabularySearchActivity extends AppCompatActivity {

    // UI Components
    private Toolbar toolbar;
    private ImageButton btnBack;
    private ScrollView mainScrollView;
    private LinearLayout headerSection;
    private MaterialCardView layoutSearch;
    private TextInputLayout keywordInputLayout;
    private TextInputLayout contextInputLayout;
    private TextInputEditText etKeyword;
    private TextInputEditText etContext;
    private MaterialButton btnSearch;
    private ProgressBar progressBar;
    private View viewDivider;
    private MaterialCardView cardResult;
    private LinearLayout layoutResult;
    private ImageView ivResultIcon;
    private TextView tvResultTitle;
    private TextView tvResult;
    private TextView footerText;
    private View loadingOverlay;
    private ImageView ivSearchIcon;

    // Animation variables
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_search);

        initViews();
        btnBack.setOnClickListener(v->{


            if (isSearching) {
                stopSearchAnimation();
                Toast.makeText(this, "Đã hủy tìm kiếm", Toast.LENGTH_SHORT).show();
            }

            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            findViewById(R.id.main_content_layout).startAnimation(slideOut);

        });
        setupListeners();
        startEntranceAnimations();
    }

    private void initViews() {
        // Initialize all views
        toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btnBack);
        mainScrollView = findViewById(R.id.main_scroll_view);
        headerSection = findViewById(R.id.header_section);
        layoutSearch = findViewById(R.id.layout_search);
        keywordInputLayout = findViewById(R.id.keyword_input_layout);
        contextInputLayout = findViewById(R.id.context_input_layout);
        etKeyword = findViewById(R.id.et_keyword);
        etContext = findViewById(R.id.et_context);
        btnSearch = findViewById(R.id.btn_search);
        progressBar = findViewById(R.id.progress_bar);
        viewDivider = findViewById(R.id.view_divider);
        cardResult = findViewById(R.id.card_result);
        layoutResult = findViewById(R.id.layout_result);
        ivResultIcon = findViewById(R.id.iv_result_icon);
        tvResultTitle = findViewById(R.id.tv_result_title);
        tvResult = findViewById(R.id.tv_result);
        footerText = findViewById(R.id.footer_text);
        loadingOverlay = findViewById(R.id.loading_overlay);
        ivSearchIcon = findViewById(R.id.iv_search_icon);
    }



    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            animateButtonPress(btnBack);
            handler.postDelayed(this::onBackPressed, 150);
        });

        // Text watcher for keyword input
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearchButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSearch.setOnClickListener(v -> {
            if (!isSearching) {
                animateButtonPress(btnSearch);
                cardResult.setVisibility(View.GONE);
                handler.postDelayed(this::performSearch, 150);
            }
        });

        ivSearchIcon.setOnClickListener(v -> animateSearchIcon());
    }

    private void startEntranceAnimations() {
        Animation fadeInUp1 = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp1.setStartOffset(200);
        headerSection.startAnimation(fadeInUp1);

        Animation fadeInUp2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp2.setStartOffset(400);
        layoutSearch.startAnimation(fadeInUp2);

        Animation fadeInUp3 = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp3.setStartOffset(600);
        footerText.startAnimation(fadeInUp3);

        headerSection.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.VISIBLE);
        footerText.setVisibility(View.VISIBLE);
    }

    private void updateSearchButtonState() {
        String keyword = etKeyword.getText().toString().trim();
        boolean isEnabled = !TextUtils.isEmpty(keyword);

        btnSearch.setEnabled(isEnabled);

        if (isEnabled) {
            btnSearch.animate()
                    .alpha(1.0f)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start();
        } else {
            btnSearch.animate()
                    .alpha(0.6f)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(200)
                    .start();
        }
    }

    private void performSearch() {
        if (isSearching) return;

        String keyword = etKeyword.getText().toString().trim();
        String context = etContext.getText().toString().trim();

        if (TextUtils.isEmpty(keyword)) {
            shakeView(keywordInputLayout);
            return;
        }

        startSearchAnimation();

        VocabularyService.searchVocabulary(keyword, context, new VocabularyService.SearchCallback() {
            @Override
            public void onSuccess(String content) {
                runOnUiThread(() -> {
                    showSearchResult(content, true);
                    stopSearchAnimation();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    showSearchResult("❌ Lỗi khi tìm kiếm: " + error + "\n\n" +
                            "🔄 Vui lòng thử lại sau hoặc kiểm tra kết nối mạng.", false);
                    stopSearchAnimation();
                    Toast.makeText(VocabularySearchActivity.this, "Tìm kiếm thất bại: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void startSearchAnimation() {
        isSearching = true;

        // Clear any existing listener and reset alpha
        loadingOverlay.animate().setListener(null);
        loadingOverlay.setAlpha(0.0f);

        // Show loading overlay
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start();

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_pulse);
        progressBar.startAnimation(rotateAnimation);

        // Disable search button
        btnSearch.setEnabled(false);
        btnSearch.setText("Đang tìm kiếm...");
    }

    private void stopSearchAnimation() {
        isSearching = false;

        // Hide loading overlay
        loadingOverlay.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingOverlay.setVisibility(View.GONE);
                        // Clear the listener after use
                        loadingOverlay.animate().setListener(null);
                    }
                })
                .start();

        // Hide progress bar
        progressBar.clearAnimation();
        progressBar.setVisibility(View.GONE);

        // Reset search button
        btnSearch.setEnabled(true);
        btnSearch.setText("Tìm kiếm");
        updateSearchButtonState();
    }
    private void showSearchResult(String result, boolean isSuccess) {
        // Show divider
        viewDivider.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        viewDivider.startAnimation(fadeIn);
        Markwon markwon = Markwon.create(this);
        // Setup result card
        cardResult.setVisibility(View.VISIBLE);
        markwon.setMarkdown(tvResult, result);

        if (isSuccess) {
            tvResultTitle.setText("Kết quả tìm kiếm");
            tvResultTitle.setTextColor(getResources().getColor(R.color.success_text, null));
            ivResultIcon.setImageResource(R.drawable.ic_check_circle);
            cardResult.setCardBackgroundColor(getResources().getColor(R.color.white, null));
        } else {
            tvResultTitle.setText("Lỗi tìm kiếm");
            tvResultTitle.setTextColor(getResources().getColor(R.color.error_text, null));
            ivResultIcon.setImageResource(R.drawable.ic_error_circle);
            cardResult.setCardBackgroundColor(getResources().getColor(R.color.error_background, null));
        }

        // Animate result card
        Animation bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        bounceIn.setStartOffset(200);
        cardResult.startAnimation(bounceIn);

        // Scroll to result
        handler.postDelayed(() -> {
            mainScrollView.smoothScrollTo(0, cardResult.getTop());
        }, 800);
    }

    private void animateButtonPress(View button) {
        Animation pressAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press);
        Animation releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_release);

        pressAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                button.startAnimation(releaseAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        button.startAnimation(pressAnimation);
    }

    private void animateSearchIcon() {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(ivSearchIcon, "rotation", 0f, 360f);
        rotateAnimator.setDuration(500);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(ivSearchIcon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(ivSearchIcon, "scaleY", 1f, 1.2f, 1f);
        scaleXAnimator.setDuration(500);
        scaleYAnimator.setDuration(500);

        rotateAnimator.start();
        scaleXAnimator.start();
        scaleYAnimator.start();
    }

    private void shakeView(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}