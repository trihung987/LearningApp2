package me.trihung.learningapp2.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import me.trihung.learningapp2.EnityDB.NgheHieuHoiDap;
import me.trihung.learningapp2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class NgheHieuHDTestNhanhActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_name_user,tvArrowBack,tv_ThoiGian,tvSoCauHoanhThanh,tvCurrent,tvA,tvB,tvC,tvThoiGianPlay,tvThoiGianAudio;
    Button btnXacNhan;

    private LottieAnimationView confettiAnimation;
    ImageButton btnPlay;
    MediaPlayer mp=null;
    SeekBar sbVoice;
    private long myDataTG;
    private long audioTime;

    private int count = 0;
    private int questionListSize;
    private int questionCounter;
    private CountDownTimer countDownTimer;

    private int score;
    private int sourceID;
    private Thread t;

    private CountDownTimer c;

    private boolean answered;
    private int currentQuestion = 0;
    private  ArrayList<NgheHieuHoiDap> arrayListDe;
    private NgheHieuHoiDap ngheHieuMoTaTranh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nghe_hieu_hd_test_nhanh);
        tv_name_user = findViewById(R.id.tv_name_user);
        tvArrowBack = findViewById(R.id.tvArrowBack);
        tv_ThoiGian = findViewById(R.id.tv_ThoiGian);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        tvSoCauHoanhThanh = findViewById(R.id.tvSoCauHoanhThanh);
        tvCurrent = findViewById(R.id.tvCurrent);
        sbVoice = findViewById(R.id.seekBarVoice);
        btnPlay = findViewById(R.id.btnPlay);
        sourceID = android.R.drawable.ic_media_play;
        btnPlay.setImageResource(sourceID);
        tvThoiGianAudio = findViewById(R.id.tvThoiGianAudio);
        tvThoiGianPlay = findViewById(R.id.tvThoiGianPlay);
      //  confettiAnimation = findViewById(R.id.confetti_animation);


        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        tvC = findViewById(R.id.tvC);

        tvArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConForm();
            }
        });
        // Lấy ArrayList từ Intent
        arrayListDe = getIntent().getParcelableArrayListExtra("list_de");


        //LẤY THỜI GIAN
        myDataTG = getIntent().getLongExtra("myDataThoiGian", 0);
        //LẤY KÍCH CÕ DANH SÁCH BẰNG TỔNG SỐ CÂU HỎI
        questionListSize = arrayListDe.size();
        //ĐẢO VỊ VÍ CÂU HỎI
        Collections.shuffle(arrayListDe);

        for (int i = 0; i < arrayListDe.size(); i++) {
            Log.d("TAG", "Đề"+ ": " + arrayListDe.get(i));
        }

        setDataQuestion(arrayListDe.get(currentQuestion));

        startCountDown();

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConFormNopBai();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sourceID == android.R.drawable.ic_media_play){
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                    // Check if audio has finished playing - need to reset to beginning
                    if (mp != null && !mp.isPlaying() && mp.getCurrentPosition() >= mp.getDuration() - 100) {
                        mp.seekTo(0);
                        sbVoice.setProgress(0);
                        tvThoiGianPlay.setText("00:00");
                    }

                    mp.start();
                    if (c == null) {
                        c = countDownAudio();
                        c.start();
                    } else {
                        // No need to cancel and recreate the timer when resuming
                        // The timer will continue from where it left off
                        c.start();
                    }

                    if (t == null || !t.isAlive()) {
                        chaySeekBar();
                    }

                    sourceID = android.R.drawable.ic_media_pause;

                } else{
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                    mp.pause();
                    // Pause the timer but don't reset it
                    if (c != null) {
                        c.cancel();
                    }

                    // We don't need to interrupt the thread - it will check if mp.isPlaying()
                    // and stop updating if the player is paused

                    sourceID = android.R.drawable.ic_media_play;
                }
            }
        });
    }
    private void resetMediaPlayer() {
        // Reset any existing MediaPlayer
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }

        // Reset SeekBar and timer
        sbVoice.setProgress(0);
        tvThoiGianPlay.setText("00:00");

        // Reset UI
        resetAudioPlaybackUI();

        // Cancel any running threads or timers
        if (t != null) {
            t.interrupt();
            t = null;
        }
        if (c != null) {
            c.cancel();
            c = null;
        }
    }


    private void setDataQuestion(NgheHieuHoiDap ngheHieuMoTaTranh1) {
        if (ngheHieuMoTaTranh1 == null){
            return;
        }
        ngheHieuMoTaTranh = ngheHieuMoTaTranh1;
        resetMediaPlayer();

        tvA.setBackgroundResource(R.drawable.border_black);
        tvB.setBackgroundResource(R.drawable.border_black);
        tvC.setBackgroundResource(R.drawable.border_black);

        if (tvA instanceof CompoundButton) {
            ((CompoundButton) tvA).setChecked(false);
        }
        if (tvB instanceof CompoundButton) {
            ((CompoundButton) tvB).setChecked(false);
        }
        if (tvC instanceof CompoundButton) {
            ((CompoundButton) tvC).setChecked(false);
        }


        String cauHoi = "Question "+ (currentQuestion+1);
        tvSoCauHoanhThanh.setText("Câu hỏi: "+currentQuestion+" / "+questionListSize);
        tvCurrent.setText(cauHoi);
        int resourceId = getResources().getIdentifier(arrayListDe.get(currentQuestion).getIdNH().getVoice(), "raw", getPackageName());
        mp = MediaPlayer.create(NgheHieuHDTestNhanhActivity.this, resourceId);
        // Set MediaPlayer completion listener to reset UI when audio ends
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                resetAudioPlaybackUI();
            }
        });
        int minutes = (int) ((mp.getDuration()/1000)/60);
        int seconds = (int) ((mp.getDuration()/1000)%60);
        String timeTG = String.format(Locale.getDefault(),"%02d:%02d", minutes,seconds);
        tvThoiGianAudio.setText(timeTG);
        tvA.setText("A");
        tvB.setText("B");
        tvC.setText("C");

        tvA.setOnClickListener(this);
        tvB.setOnClickListener(this);
        tvC.setOnClickListener(this);
    }
    private void resetAudioPlaybackUI() {
        // Reset play button image and state
        sourceID = android.R.drawable.ic_media_play;
        btnPlay.setImageResource(sourceID);
//        if (mp!=null)
//            mp.reset();
        // Reset SeekBar
        sbVoice.setProgress(0);

        //resetMediaPlayer();
    }

    private void startCountDown() {

        countDownTimer = new CountDownTimer(myDataTG, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                myDataTG  = millisUntilFinished;
                //update time
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //Hết giờ
                myDataTG = 0;
                updateCountDownText();

            }
        }.start();

    }
    private void showDA(){
        tvA.setText(arrayListDe.get(currentQuestion).getDapAnA());
        tvB.setText(arrayListDe.get(currentQuestion).getDapAnB());
        tvC.setText(arrayListDe.get(currentQuestion).getDapAnC());
    }

    private CountDownTimer countDownAudio() {
        audioTime = mp.getDuration();
        countDownTimer = new CountDownTimer(audioTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                audioTime  = millisUntilFinished;
                //update time
                updateCountDownTextAudio();
            }

            @Override
            public void onFinish() {
                //Hết giờ
                audioTime = 0;
                updateCountDownTextAudio();

            }
        };
        return countDownTimer;
    }

    private void updateCountDownText() {
        int minutes = (int) ((myDataTG/1000)/60);
        int seconds = (int) ((myDataTG/1000)%60);
        String timeTG = String.format(Locale.getDefault(),"%02d:%02d", minutes,seconds);
        tv_ThoiGian.setText(timeTG);
        if (myDataTG < 10000) {
            tv_ThoiGian.setTextColor(Color.RED);
        }else{
            tv_ThoiGian.setTextColor(Color.WHITE);
        }
    }

    private void updateCountDownTextAudio() {
        int minutes = (int) (((mp.getDuration()-audioTime)/1000)/60);
        int seconds = (int) (((mp.getDuration()-audioTime)/1000)%60);
        String timeTG = String.format(Locale.getDefault(),"%02d:%02d", minutes,seconds);
        tvThoiGianPlay.setText(timeTG);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        count++;
        if (count>=1){
            finishQuestion();
        }
        count = 0;
    }

    private void chaySeekBar() {
        final int duration = mp.getDuration();
        final int amountToUpdate = duration / 1000;
        sbVoice.setMax(amountToUpdate);

        // Set the initial position of the seekbar based on the current position
        int initialProgress = mp.getCurrentPosition() / 1000;
        sbVoice.setProgress(initialProgress);

        // Interrupt any existing thread
        if (t != null && t.isAlive()) {
            t.interrupt();
            try {
                t.join(500); // Wait for thread to terminate
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean wasInterrupted = false;

                while (!wasInterrupted && mp != null) {
                    // Check if thread was interrupted
                    if (Thread.currentThread().isInterrupted()) {
                        wasInterrupted = true;
                        break;
                    }

                    // Only update if MediaPlayer is playing
                    if (mp != null && mp.isPlaying()) {
                        try {
                            final int currentPosition = mp.getCurrentPosition();
                            final int progress = currentPosition / 1000;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (sbVoice != null) {
                                        sbVoice.setProgress(progress);
                                    }
                                }
                            });

                            // Check if we've reached the end
                            if (mp != null && mp.getCurrentPosition() >= mp.getDuration() - 100) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetAudioPlaybackUI();
                                    }
                                });
                                break;
                            }
                        } catch (IllegalStateException e) {
                            // MediaPlayer might be in an invalid state
                            break;
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        wasInterrupted = true;
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });

        t.start();
    }

    //THOÁT QUA GIAO DIỆN CHÍNH
    private void finishQuestion() {
        //Chứa dữ liệu gửi activity
        Intent intent = new Intent();
        intent.putExtra("sorce", score);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.tvA){
            tvA.setBackgroundResource(R.drawable.bg_select);
            checkAnswer(tvA,"A", ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());

        }else if (id==R.id.tvB){
            tvB.setBackgroundResource(R.drawable.bg_select);
            checkAnswer(tvB,"B", ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());

        }else if (id==R.id.tvC){
            tvC.setBackgroundResource(R.drawable.bg_select);
            checkAnswer(tvC,"C", ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());

        }
//        switch (v.getId()){
//            case R.id.tvA:
//                tvA.setBackgroundResource(R.drawable.bg_select);
//                checkAnswer(tvA,arrayListDe.get(currentQuestion).getDapAnA(), ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());
//                break;
//            case R.id.tvB:
//                tvB.setBackgroundResource(R.drawable.bg_select);
//                checkAnswer(tvB,arrayListDe.get(currentQuestion).getDapAnB(), ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());
//                break;
//            case R.id.tvC:
//                tvC.setBackgroundResource(R.drawable.bg_select);
//                checkAnswer(tvC,arrayListDe.get(currentQuestion).getDapAnC(), ngheHieuMoTaTranh,arrayListDe.get(currentQuestion).getDapAnDung());
//                break;
//        }
    }

    public void checkAnswer(TextView textView,String dapAnChon,NgheHieuHoiDap ngheHieuMoTaTranh, String danAn ){

        Log.d("TAG", "checkAnswer: "+textView.getText());
        Log.d("TAG", "checkAnswer: "+danAn);
        Log.d("TAG", "checkAnswer: "+ngheHieuMoTaTranh);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dapAnChon.equals(danAn)){
                    textView.setBackgroundResource(R.drawable.bg_answer);
                    showCorrect(ngheHieuMoTaTranh);
                 //   confettiAnimation.setVisibility(View.VISIBLE);
                 //   confettiAnimation.playAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextQuestion();
                        }
                    }, 1500); // Trễ 1.5 giây trước khi nextQuestion
                }else{
                    textView.setBackgroundResource(R.drawable.bg_sai);
//                    showCorrect(ngheHieuMoTaTranh);
                    gameOver();
                }
            }
        },1000);
    }

    private void gameOver() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDiaLog("Đán án bạn vừa chọn là sai, vui lòng chọn lại!");
            }
        },500);
    }

    private void showCorrect(NgheHieuHoiDap ngheHieuMoTaTranh) {
        if ( ngheHieuMoTaTranh.getDapAnA().equals(arrayListDe.get(0).getDapAnDung()) ){
            tvA.setBackgroundResource(R.drawable.bg_answer);
        }else if( ngheHieuMoTaTranh.getDapAnB().equals(arrayListDe.get(0).getDapAnDung())){
            tvB.setBackgroundResource(R.drawable.bg_answer);
        }else if( ngheHieuMoTaTranh.getDapAnC().equals(arrayListDe.get(0).getDapAnDung())) {
            tvC.setBackgroundResource(R.drawable.bg_answer);
        }
    }

    private void nextQuestion() {
        if (currentQuestion == arrayListDe.size() -1){
            showDiaLog("Hoàn thành tất cả các câu");
            Intent intent = new Intent(NgheHieuHDTestNhanhActivity.this, ResultNgheHieuActivity.class);
            startActivity(intent);
        }else{
            currentQuestion++;
            setDataQuestion(arrayListDe.get(currentQuestion));
        }

    }

    private void showDiaLog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                currentQuestion = 0;
//                showCorrect(mDocHieuHTCau);
                setDataQuestion(arrayListDe.get(currentQuestion));
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showConForm() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Thông báo");
        alertDialog.setIcon(R.drawable.question_mark);
        alertDialog.setMessage("Bạn có thật sự muốn thoát hay không?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Bỏ qua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }


    private void showConFormNopBai() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Thông báo");
        alertDialog.setIcon(R.drawable.question_mark);
        alertDialog.setMessage("Bạn có thể xem kết quả và đán án, sau khi đã nộp bài");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Nộp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(NgheHieuHDTestNhanhActivity.this, ResultNgheHieuActivity.class);
                startActivity(intent);

//                finish();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
}