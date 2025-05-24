package me.trihung.learningapp2.UI;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.trihung.learningapp2.DB.SpeakingPronounService;
import me.trihung.learningapp2.Entity.dto.AccuracyResponse;
import me.trihung.learningapp2.R;
import me.trihung.learningapp2.UI.Utils.TextToSpeechUtils;

public class SpeakingPronounciationActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean hasRecording = false;
    private String base64Audio = "";
    private LottieAnimationView confettiAnimation;

    private List<String> currentRealTranscript;
    private String currentIpaTranscript;
    private String currentTranslation;
    private AccuracyResponse currentResult;

    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private Handler amplitudeHandler = new Handler();
    private Runnable amplitudeRunnable;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Executor executor = Executors.newSingleThreadExecutor();

    private static final String TAG = "PronunciationActivity";
    private static final String DEFAULT_LANGUAGE = "en";
    private String currentDifficulty = "1";

    // Views
    private ImageButton btnBack, soundBtn;
    private FloatingActionButton btnRecord;
    private FloatingActionButton btnPlay;
    private FloatingActionButton btnSubmit;
    private Button btnNewSentence;
    private Spinner spinnerDifficulty;
    private TextView tvTitle;
    private TextView tvStatus;
    private TextView tvTimer;
    private TextView tvSentence;
    private TextView tvIpa;
    private TextView tvAccuracyScore;
    private TextView tvYourTranscript;
    private LinearLayout layoutWordComparison;
    private CardView cardResults;
    private View loadingOverlay;
    private View visualizer;
    private TextToSpeechUtils textToSpeechUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_speaking_pronoun);

        initViews();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        setupUI();
        setupDifficultySpinner();
        setupTimerRunnable();
        setupAmplitudeRunnable();

        loadNewSentence();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNewSentence = findViewById(R.id.btnNewSentence);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tvStatus);
        tvTimer = findViewById(R.id.tvTimer);
        tvSentence = findViewById(R.id.tvSentence);
        tvIpa = findViewById(R.id.tvIpa);
        tvAccuracyScore = findViewById(R.id.tvAccuracyScore);
        tvYourTranscript = findViewById(R.id.tvYourTranscript);
        layoutWordComparison = findViewById(R.id.layoutWordComparison);
        cardResults = findViewById(R.id.cardResults);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        visualizer = findViewById(R.id.visualizer);
        confettiAnimation = findViewById(R.id.confetti_animation);
        soundBtn = findViewById(R.id.sound_button);
        textToSpeechUtils = new TextToSpeechUtils(this);
    }

    private void setupUI() {
        if (tvTitle != null) {
            tvTitle.setText("Luyện Phát Âm");
        }

        btnBack.setOnClickListener(v -> finish());

        btnRecord.setOnClickListener(v -> {
            if (loadingOverlay.getVisibility()==View.VISIBLE)
                return;
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (loadingOverlay.getVisibility()==View.VISIBLE)
                return;
            if (hasRecording) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pausePlayback();
                } else {
                    startPlayback();
                }
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (loadingOverlay.getVisibility()==View.VISIBLE)
                return;
            if (hasRecording) {
                submitRecording();
            }
        });

        btnNewSentence.setOnClickListener(v -> {
            if (loadingOverlay.getVisibility()==View.VISIBLE)
                return;
            loadNewSentence();
        });

        soundBtn.setOnClickListener(v->{
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                textToSpeechUtils.speak(tvSentence.getText().toString());
            }
        });
    }

    private void setupDifficultySpinner() {
        String[] difficulties = new String[]{"Dễ", "Trung bình", "Khó"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                difficulties
        );

        spinnerDifficulty.setAdapter(adapter);
        spinnerDifficulty.setSelection(0); // Default to "Dễ"

        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDifficulty = String.valueOf(position + 1);
                loadNewSentence();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupTimerRunnable() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = SystemClock.elapsedRealtime() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };
    }

    private void setupAmplitudeRunnable() {
        amplitudeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording && mediaRecorder != null) {
                    try {
                        int amplitude = mediaRecorder.getMaxAmplitude();
                        // Update visualizer with the amplitude
                        // Cast the view to AudioVisualizer to access updateAmplitude method
                        if (visualizer instanceof AudioVisualizer) {
                            ((AudioVisualizer) visualizer).updateAmplitude(amplitude);
                        }

                        // Post again after a delay
                        amplitudeHandler.postDelayed(this, 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
    private void loadNewSentence() {
        tvSentence.setText("Đang tải câu...");
        tvIpa.setText("Đang tải IPA...");
        cardResults.setVisibility(View.GONE);

        // Show loading overlay
        loadingOverlay.setVisibility(View.VISIBLE);

        // Reset recording state
        resetRecordingState();

        SpeakingPronounService.getSample(currentDifficulty, DEFAULT_LANGUAGE, new SpeakingPronounService.SampleCallback() {
            @Override
            public void onSuccess(List<String> realTranscript, String ipaTranscript, String transcriptTranslation) {
                currentRealTranscript = realTranscript;
                currentIpaTranscript = ipaTranscript;
                currentTranslation = transcriptTranslation;

                // Display sentence and IPA
                if (realTranscript != null && !realTranscript.isEmpty()) {
                    tvSentence.setText(realTranscript.get(0));
                }

                tvIpa.setText(ipaTranscript);

                // Hide loading overlay
                loadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "API call failed: " + error);
                Toast.makeText(SpeakingPronounciationActivity.this,
                        "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();

                // Hide loading overlay
                loadingOverlay.setVisibility(View.GONE);
            }
        });
    }

    private void startRecording() {
        // Create a temp file to store the recording
        File outputDir = getCacheDir();
        try {
            File outputFile = File.createTempFile("recording_", ".aac", outputDir);
            audioFilePath = outputFile.getAbsolutePath();

            // Set up the MediaRecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setOutputFile(audioFilePath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;

                // Update UI
                tvStatus.setText("Đang ghi âm... Hãy đọc câu rõ ràng");
                btnRecord.setImageResource(R.drawable.ic_stop);

                // Start visualizer animation
                if (visualizer instanceof AudioVisualizer) {
                    ((AudioVisualizer) visualizer).startAnimation();
                }

                // Start timer
                startTime = SystemClock.elapsedRealtime();
                timerHandler.postDelayed(timerRunnable, 0);

                // Start amplitude updates
                amplitudeHandler.postDelayed(amplitudeRunnable, 100);

                // Disable other buttons while recording
                btnPlay.setEnabled(false);
                btnSubmit.setEnabled(false);
                btnNewSentence.setEnabled(false);
                spinnerDifficulty.setEnabled(false);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể bắt đầu ghi âm", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tạo tệp tạm thời", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (isRecording && mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaRecorder = null;
                isRecording = false;
                hasRecording = true;

                // Update UI
                tvStatus.setText("Ghi âm hoàn tất. Nhấn nút gửi để kiểm tra phát âm của bạn");
                btnRecord.setImageResource(R.drawable.ic_mic);

                // Stop visualizer animation
                if (visualizer instanceof AudioVisualizer) {
                    ((AudioVisualizer) visualizer).stopAnimation();
                }

                // Stop timer
                timerHandler.removeCallbacks(timerRunnable);

                // Stop amplitude updates
                amplitudeHandler.removeCallbacks(amplitudeRunnable);

                // Enable buttons
                btnPlay.setEnabled(true);
                btnSubmit.setEnabled(true);
                btnNewSentence.setEnabled(true);
                spinnerDifficulty.setEnabled(true);

                // Convert to Base64 automatically in background
                convertToBase64();
            }
        }
    }

    private void startPlayback() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(mp -> {
                    btnPlay.setImageResource(R.drawable.ic_play);

                    // Stop visualizer animation
                    if (visualizer instanceof AudioVisualizer) {
                        ((AudioVisualizer) visualizer).stopAnimation();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        mediaPlayer.start();
        btnPlay.setImageResource(R.drawable.ic_pause);

        // Start playback animation
        if (visualizer instanceof AudioVisualizer) {
            ((AudioVisualizer) visualizer).playbackAnimation();
        }

        tvStatus.setText("Đang phát bản ghi âm của bạn...");
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setImageResource(R.drawable.ic_play);

            // Stop visualizer animation
            if (visualizer instanceof AudioVisualizer) {
                ((AudioVisualizer) visualizer).stopAnimation();
            }

            tvStatus.setText("Tạm dừng phát lại");
        }
    }

    private void convertToBase64() {
        executor.execute(() -> {
            try {
                File file = new File(audioFilePath);
                byte[] bytes = new byte[(int) file.length()];

                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                byte[] buffer = new byte[1024];
                while ((bytesRead = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                fis.close();

                byte[] audioBytes = baos.toByteArray();
                base64Audio = "data:audio/ogg;base64," + Base64.encodeToString(audioBytes, Base64.NO_WRAP);

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SpeakingPronounciationActivity.this,
                            "Không thể chuyển đổi âm thanh", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void submitRecording() {
        if (base64Audio.isEmpty()) {
            Toast.makeText(this, "Vui lòng đợi, đang chuẩn bị âm thanh...", Toast.LENGTH_SHORT).show();
            return;
        }

        tvStatus.setText("Đang phân tích phát âm của bạn...");
        btnSubmit.setEnabled(false);

        // Show loading overlay
        loadingOverlay.setVisibility(View.VISIBLE);

//        // Get the current sentence title (first 30 chars or so)
//        String title = "pronunciation";
//        if (currentRealTranscript != null && !currentRealTranscript.isEmpty()) {
//            String transcript = currentRealTranscript.get(0);
//            title = transcript.length() > 30 ? transcript.substring(0, 30) : transcript;
//        }

        SpeakingPronounService.getAccuracy(base64Audio, DEFAULT_LANGUAGE, tvSentence.getText().toString(), new SpeakingPronounService.AccuracyCallback() {
            @Override
            public void onSuccess(AccuracyResponse result) {
                btnSubmit.setEnabled(true);
                currentResult = result;
                displayResults(currentResult);

                loadingOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                btnSubmit.setEnabled(true);
                Log.e(TAG, "API call failed: " + error);
                Toast.makeText(SpeakingPronounciationActivity.this,
                        "Lỗi kết nối. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                tvStatus.setText("Lỗi kết nối. Vui lòng thử lại");

                // Hide loading overlay
                loadingOverlay.setVisibility(View.GONE);
            }
        });
    }

    private void displayResults(AccuracyResponse result) {
        tvStatus.setText("Đánh giá hoàn tất");
        cardResults.setVisibility(View.VISIBLE);

        try {
            int accuracy = Integer.parseInt(result.getPronunciationAccuracy());
            if (accuracy>80){
                confettiAnimation.setVisibility(View.VISIBLE);
                confettiAnimation.playAnimation();
                confettiAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        confettiAnimation.setVisibility(View.GONE);
                        confettiAnimation.removeAnimatorListener(this);
                    }
                });
            }
        }catch (Exception e){

        }

        tvAccuracyScore.setText(result.getPronunciationAccuracy() + "%");

        if (result.getRealTranscript() != null && !result.getRealTranscript().isEmpty()) {
            tvYourTranscript.setText(result.getIpaTranscript());
        } else {
            tvYourTranscript.setText("--");
        }

        createWordComparison(result);
    }

    private void createWordComparison(AccuracyResponse result) {
        layoutWordComparison.removeAllViews();

        String fullText = result.getRealTranscripts();
        if (fullText == null || fullText.isEmpty()) {
            return;
        }

        String[] words = fullText.split("\\s+");

        String[] correctnessFlags = result.getIsLetterCorrectAllWords().trim().split("\\s+");

        for (int i = 0; i < words.length && i < correctnessFlags.length; i++) {
            String word = words[i];
            String wordFlags = correctnessFlags[i];

            LinearLayout wordLayout = new LinearLayout(this);
            wordLayout.setOrientation(LinearLayout.HORIZONTAL);
            wordLayout.setBackgroundResource(R.drawable.bg_word_container);

            LinearLayout.LayoutParams wordLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            wordLayoutParams.setMargins(8, 4, 8, 4);
            wordLayout.setLayoutParams(wordLayoutParams);

            for (int j = 0; j < word.length() && j < wordFlags.length(); j++) {
                char letter = word.charAt(j);
                char flag = wordFlags.charAt(j);

                // Create a TextView for each character
                TextView tvChar = new TextView(this);
                tvChar.setText(String.valueOf(letter));
                tvChar.setTextSize(16); // Set appropriate text size

                if (flag == '1') {
                    tvChar.setTextColor(getResources().getColor(R.color.correct_text3));
                } else {
                    tvChar.setTextColor(getResources().getColor(R.color.incorrect_text3));
                }

                wordLayout.addView(tvChar);
            }

            layoutWordComparison.addView(wordLayout);
        }
    }

    private void resetRecordingState() {
        if (isRecording) {
            stopRecording();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        isRecording = false;
        hasRecording = false;
        base64Audio = "";

        btnPlay.setEnabled(false);
        btnSubmit.setEnabled(false);
        btnRecord.setImageResource(R.drawable.ic_mic);
        tvStatus.setText("Nhấn nút mic để bắt đầu ghi âm");
        tvTimer.setText("00:00");

        if (visualizer instanceof AudioVisualizer) {
            ((AudioVisualizer) visualizer).stopAnimation();
        }

        cardResults.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền ghi âm được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền ghi âm bị từ chối", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        resetRecordingState();
    }

    public interface AudioVisualizer {
        void updateAmplitude(int amplitude);
        void startAnimation();
        void stopAnimation();
        void playbackAnimation();
    }
}