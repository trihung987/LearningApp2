package me.trihung.learningapp2.UI;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.Entity.TuVung;
import me.trihung.learningapp2.MainActivity;
import me.trihung.learningapp2.R;
import me.trihung.learningapp2.UI.Utils.GradientDrawable;
import me.trihung.learningapp2.UI.Utils.TextToSpeechUtils;

public class FlashCardActivity extends AppCompatActivity {

    private CardView flashcard;
    private TextView englishTextView;
    private TextView vietnameseTextView;
    private TextView transcriptionTextView;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private ImageButton soundButton;
    private ImageButton bookmarkButton, btnBack;
    private ProgressBar progressBar;
    private TextView progressText;
    private LottieAnimationView confettiAnimation;
    private ConstraintLayout mainLayout;

    private List<TuVung> flashcards;
    private int currentCardIndex = 0;
    private boolean isCardFlipped = false;
    private boolean isCardBookmarked = false;
    private String[] gradientBackgrounds = {
            "#FF9AA2-#FFB7B2", // Pink
            "#FFDAC1-#E2F0CB", // Peach to green
            "#B5EAD7-#C7CEEA", // Mint to lavender
            "#E2F0CB-#FFDAC1", // Light green to peach
            "#C7CEEA-#FF9AA2"  // Lavender to pink
    };

    private Database db;

    private TextToSpeechUtils textToSpeechUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tu_vung_flashcard);

        // Initialize views
        flashcard = findViewById(R.id.flashcard);
        englishTextView = findViewById(R.id.english_text);
        vietnameseTextView = findViewById(R.id.vietnamese_text);
        transcriptionTextView = findViewById(R.id.transcription_text);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        soundButton = findViewById(R.id.sound_button);
        bookmarkButton = findViewById(R.id.bookmark_button);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        confettiAnimation = findViewById(R.id.confetti_animation);
        mainLayout = findViewById(R.id.flashcard_layout);
        btnBack = findViewById(R.id.btnBack);

        vietnameseTextView.setVisibility(View.INVISIBLE);
        transcriptionTextView.setVisibility(View.INVISIBLE);

        db = new Database(this);
        textToSpeechUtils = new TextToSpeechUtils(this);
        flashcards = new ArrayList<>();
        setupFlashcards();

        updateProgress();

        //setRandomBackground();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//// Enable the Up button
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

        btnBack.setOnClickListener(v -> finish());

        displayFlashcard(currentCardIndex);

        flashcard.setOnClickListener(view -> flipCard1());

        nextButton.setOnClickListener(view -> {
            if (currentCardIndex < flashcards.size() - 1) {
                Animation slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
                slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        currentCardIndex++;
                        resetCardState();
                        updateProgress();
                        //setRandomBackground();
                        displayFlashcard(currentCardIndex);
                        flashcard.startAnimation(AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.slide_in_right));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                flashcard.startAnimation(slideOutLeft);
            } else {
                // Show completion animation
                confettiAnimation.setVisibility(View.VISIBLE);
                confettiAnimation.playAnimation();
            }
        });

        prevButton.setOnClickListener(view -> {
            if (currentCardIndex > 0) {
                Animation slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
                slideOutRight.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        currentCardIndex--;
                        resetCardState();
                        updateProgress();
                        //setRandomBackground();
                        displayFlashcard(currentCardIndex);
                        flashcard.startAnimation(AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.slide_in_left));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                flashcard.startAnimation(slideOutRight);
            }
        });

        soundButton.setOnClickListener(view -> {
            soundButton.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() ->
                    soundButton.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            ).start();

            //play sound of text
            textToSpeechUtils.speak(flashcards.get(currentCardIndex).getTiengAnh());
        });


        bookmarkButton.setOnClickListener(view -> {
            isCardBookmarked = !isCardBookmarked;
            TuVung tuVung = flashcards.get(currentCardIndex);
            if (isCardBookmarked) {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_fill);
                db.luuTuVungVaoSoTay(this, MainActivity.getIdND(), db.getIdTheoTiengAnh(tuVung.getTiengAnh()));
            } else {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
                db.xoaTuVungKhoiSoTay(this, MainActivity.getIdND(), db.getIdTheoTiengAnh(tuVung.getTiengAnh()));

            }
        });


    }


    private void setRandomBackground() {
        String randomGradient = gradientBackgrounds[new Random().nextInt(gradientBackgrounds.length)];
        String[] colors = randomGradient.split("-");

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor(colors[0]), Color.parseColor(colors[1])}
        );
        mainLayout.setBackground(gradientDrawable);
    }

    private void updateProgress() {
        int progress = (int) (((float) (currentCardIndex + 1) / flashcards.size()) * 100);
        progressBar.setProgress(progress);
        progressText.setText((currentCardIndex + 1) + "/" + flashcards.size());
    }

    private void setupFlashcards() {
        flashcards = getListTuVung();

    }

    private List<TuVung> getListTuVung() {
        List<TuVung> list = new ArrayList<>();
        try {
            Cursor c = db.query_hasresult("SELECT tiengAnh, phienAm, tiengViet, grouptv FROM TuVung");
            while (c.moveToNext()) {
                String tiengAnh = c.getString(0);
                String phienAm = c.getString(1);
                String tiengViet = c.getString(2);
                String group = c.getString(3);
                list.add(new TuVung(tiengAnh, tiengViet, phienAm, group));
            }
            Collections.shuffle(list);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void displayFlashcard(int index) {
        TuVung card = flashcards.get(index);
        //update state bookmark after check
        if (db.checkTuVungTrongSoTay(card.getTiengAnh(), MainActivity.getIdND())){
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_fill);
            Log.w("db check ","true");
            isCardBookmarked = true;
        }else {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
            isCardBookmarked = false;
            Log.w("db check ","false");
        }
        englishTextView.setText(card.getTiengAnh() + " " + card.getGroup());
        vietnameseTextView.setText(card.getTiengViet());
        transcriptionTextView.setText(card.getPhienAm());


        // Update navigation button states
        prevButton.setEnabled(currentCardIndex > 0);
        prevButton.setAlpha(currentCardIndex > 0 ? 1.0f : 0.5f);

        nextButton.setEnabled(currentCardIndex < flashcards.size() - 1);
        nextButton.setAlpha(currentCardIndex < flashcards.size() - 1 ? 1.0f : 0.5f);
    }

    private void flipCard1() {
        Animation animation;
        if (!isCardFlipped) {
            // Show translation
            animation = AnimationUtils.loadAnimation(this, R.anim.flip_to_back);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vietnameseTextView.setVisibility(View.VISIBLE);
                    transcriptionTextView.setVisibility(View.VISIBLE);
                    flashcard.startAnimation(AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.flip_from_back));
                    confettiAnimation.setVisibility(View.VISIBLE);
                    confettiAnimation.playAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        } else {
            // Hide translation
            animation = AnimationUtils.loadAnimation(this, R.anim.flip_to_front);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vietnameseTextView.setVisibility(View.GONE);
                    transcriptionTextView.setVisibility(View.GONE);
                    flashcard.startAnimation(AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.flip_from_front));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        flashcard.startAnimation(animation);
        isCardFlipped = !isCardFlipped;
    }

    private void flipCard2() {
        Animation animation;
        if (!isCardFlipped) {
            // Show translation
            animation = AnimationUtils.loadAnimation(this, R.anim.flip_3d_vertical);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vietnameseTextView.setVisibility(View.VISIBLE);
                    transcriptionTextView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            flashcard.startAnimation(animation);
        } else {
            // Hide translation
            animation = AnimationUtils.loadAnimation(this, R.anim.flip_3d_vertical_back);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vietnameseTextView.setVisibility(View.INVISIBLE);
                    transcriptionTextView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            flashcard.startAnimation(animation);
        }

        isCardFlipped = !isCardFlipped;
    }

    private void resetCardState() {
        vietnameseTextView.setVisibility(View.INVISIBLE);
        transcriptionTextView.setVisibility(View.INVISIBLE);
        isCardFlipped = false;
    }
}

