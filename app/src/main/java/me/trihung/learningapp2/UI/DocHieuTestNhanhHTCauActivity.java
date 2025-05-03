package me.trihung.learningapp2.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import me.trihung.learningapp2.EnityDB.DocHieuHTDoanVan;
import me.trihung.learningapp2.R;

public class DocHieuTestNhanhHTCauActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_name_user, tvArrowBack, tv_ThoiGian, tvCauHoi, tvSoCauHoanhThanh, tvCurrent, tvA1, tvB1, tvC1, tvD1, tvA2, tvB2, tvC2, tvD2, tvA3, tvB3, tvC3, tvD3, tvA4, tvB4, tvC4, tvD4;
    Button btnXacNhan;

    private long myDataTG;

    private int count = 0;
    private int answeredQuestionsCount = 0;
    private int questionListSize;
    private int questionCounter;
    private CountDownTimer countDownTimer;
    private int score;

    private boolean answered;
    private int currentQuestion = 0;
    private ArrayList<DocHieuHTDoanVan> arrayListDe;
    private DocHieuHTDoanVan mDocHieuHTDoanVan;

    private TextView[] selectedAnswers = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_hieu_test_nhanh_htcau);
        tv_name_user = findViewById(R.id.tv_name_user);
        tvArrowBack = findViewById(R.id.tvArrowBack);
        tv_ThoiGian = findViewById(R.id.tv_ThoiGian);
        tvCauHoi = findViewById(R.id.tvCauHoi);
        btnXacNhan = findViewById(R.id.btnXacNhan);
//        tvSoCauHoanhThanh = findViewById(R.id.tvSoCauHoanhThanh);
        tvCurrent = findViewById(R.id.tvCurrent);

        tvA1 = findViewById(R.id.tvA1);
        tvB1 = findViewById(R.id.tvB1);
        tvC1 = findViewById(R.id.tvC1);
        tvD1 = findViewById(R.id.tvD1);

        tvA2 = findViewById(R.id.tvA2);
        tvB2 = findViewById(R.id.tvB2);
        tvC2 = findViewById(R.id.tvC2);
        tvD2 = findViewById(R.id.tvD2);

        tvA3 = findViewById(R.id.tvA3);
        tvB3 = findViewById(R.id.tvB3);
        tvC3 = findViewById(R.id.tvC3);
        tvD3 = findViewById(R.id.tvD3);

        tvA4 = findViewById(R.id.tvA4);
        tvB4 = findViewById(R.id.tvB4);
        tvC4 = findViewById(R.id.tvC4);
        tvD4 = findViewById(R.id.tvD4);

        tvArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConForm();
            }
        });
        // Lấy ArrayList từ Intent
        arrayListDe = getIntent().getParcelableArrayListExtra("list_de_ht_cau");

        Log.d("TAG", "onCreate-----------: " + arrayListDe.size());

        //LẤY THỜI GIAN
        myDataTG = getIntent().getLongExtra("myDataThoiGian", 0);
        //LẤY KÍCH CÕ DANH SÁCH BẰNG TỔNG SỐ CÂU HỎI
        questionListSize = arrayListDe.size();
        //ĐẢO VỊ VÍ CÂU HỎI
        Collections.shuffle(arrayListDe);

        setDataQuestion(arrayListDe.get(currentQuestion));

        startCountDown();

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConFormNopBai();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int questionIndex = -1;
        String answerLetter = null;
        TextView selectedTextView = null;
        int groupIndex = 0; // To track which group of answers (0-3) this belongs to

        if (id == R.id.tvA1 || id == R.id.tvB1 || id == R.id.tvC1 || id == R.id.tvD1) {
            questionIndex = currentQuestion;
            groupIndex = 0;
        } else if (id == R.id.tvA2 || id == R.id.tvB2 || id == R.id.tvC2 || id == R.id.tvD2) {
            questionIndex = currentQuestion + 1;
            groupIndex = 1;
        } else if (id == R.id.tvA3 || id == R.id.tvB3 || id == R.id.tvC3 || id == R.id.tvD3) {
            questionIndex = currentQuestion + 2;
            groupIndex = 2;
        } else if (id == R.id.tvA4 || id == R.id.tvB4 || id == R.id.tvC4 || id == R.id.tvD4) {
            questionIndex = currentQuestion + 3;
            groupIndex = 3;
        }

        if (questionIndex != -1 && questionIndex < arrayListDe.size()) {
            selectedTextView = findViewById(id);
            answerLetter = getAnswerLetterFromId(id); // Lấy A/B/C/D từ id

            if (selectedTextView != null && answerLetter != null) {
                // If there was a previously selected answer for this question, reset its background
                if (selectedAnswers[groupIndex] != null) {
                    selectedAnswers[groupIndex].setBackgroundResource(R.drawable.border_black);
                }

                // Set this as the new selected answer for this question
                selectedAnswers[groupIndex] = selectedTextView;
                selectedTextView.setBackgroundResource(R.drawable.bg_select);
                checkAnswer(answerLetter, selectedTextView, arrayListDe.get(questionIndex), arrayListDe.get(questionIndex).getDapAnDung());
            }
        }
    }
    private void resetAllAnswerViews() {
        List<View> answerViews = Arrays.asList(
                tvA1, tvB1, tvC1, tvD1,
                tvA2, tvB2, tvC2, tvD2,
                tvA3, tvB3, tvC3, tvD3,
                tvA4, tvB4, tvC4, tvD4
        );

        for (View v : answerViews) {
            // Reset background
            v.setBackgroundResource(R.drawable.border_black);

            // Nếu là CompoundButton thì setChecked(false)
            if (v instanceof CompoundButton) {
                ((CompoundButton) v).setChecked(false);
            }
        }
    }


    private void setDataQuestion(DocHieuHTDoanVan docHieuHTDoanVan) {
        // Reset selection tracking
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = null;
        }
        if (docHieuHTDoanVan == null) {
            return;
        }
        mDocHieuHTDoanVan = docHieuHTDoanVan;
//        tvCurrent.setText("Question: " + arrayListDe.get(currentQuestion).getIdCau());

//        tvSoCauHoanhThanh.setText(currentQuestion+"/"+arrayListDe.size());

        //reset orange checked
        resetAllAnswerViews();
        tvA1.setBackgroundResource(R.drawable.border_black);
        tvB1.setBackgroundResource(R.drawable.border_black);
        tvC1.setBackgroundResource(R.drawable.border_black);
        tvD1.setBackgroundResource(R.drawable.border_black);

        tvA2.setBackgroundResource(R.drawable.border_black);
        tvB2.setBackgroundResource(R.drawable.border_black);
        tvC2.setBackgroundResource(R.drawable.border_black);
        tvD2.setBackgroundResource(R.drawable.border_black);

        tvA3.setBackgroundResource(R.drawable.border_black);
        tvB3.setBackgroundResource(R.drawable.border_black);
        tvC3.setBackgroundResource(R.drawable.border_black);
        tvD3.setBackgroundResource(R.drawable.border_black);

        tvA4.setBackgroundResource(R.drawable.border_black);
        tvB4.setBackgroundResource(R.drawable.border_black);
        tvC4.setBackgroundResource(R.drawable.border_black);
        tvD4.setBackgroundResource(R.drawable.border_black);

        String cauHoi = "Question " + currentQuestion + 1;
//        tvSoCauHoanhThanh.setText("Câu hỏi: "+currentQuestion+" / "+questionListSize);
        tvCurrent.setText(cauHoi);
        tvCauHoi.setText(arrayListDe.get(currentQuestion).getIdDH().getDe());
        tvA1.setText(arrayListDe.get(currentQuestion).getDapAnA());
        tvB1.setText(arrayListDe.get(currentQuestion).getDapAnB());
        tvC1.setText(arrayListDe.get(currentQuestion).getDapAnC());
        tvD1.setText(arrayListDe.get(currentQuestion).getDapAnD());

        tvA2.setText(arrayListDe.get(currentQuestion + 1).getDapAnA());
        tvB2.setText(arrayListDe.get(currentQuestion + 1).getDapAnB());
        tvC2.setText(arrayListDe.get(currentQuestion + 1).getDapAnC());
        tvD2.setText(arrayListDe.get(currentQuestion + 1).getDapAnD());

        tvA3.setText(arrayListDe.get(currentQuestion + 2).getDapAnA());
        tvB3.setText(arrayListDe.get(currentQuestion + 2).getDapAnB());
        tvC3.setText(arrayListDe.get(currentQuestion + 2).getDapAnC());
        tvD3.setText(arrayListDe.get(currentQuestion + 2).getDapAnD());

        tvA4.setText(arrayListDe.get(currentQuestion + 3).getDapAnA());
        tvB4.setText(arrayListDe.get(currentQuestion + 3).getDapAnB());
        tvC4.setText(arrayListDe.get(currentQuestion + 3).getDapAnC());
        tvD4.setText(arrayListDe.get(currentQuestion + 3).getDapAnD());

        tvA1.setOnClickListener(this);
        tvB1.setOnClickListener(this);
        tvC1.setOnClickListener(this);
        tvD1.setOnClickListener(this);

        tvA2.setOnClickListener(this);
        tvB2.setOnClickListener(this);
        tvC2.setOnClickListener(this);
        tvD2.setOnClickListener(this);

        tvA3.setOnClickListener(this);
        tvB3.setOnClickListener(this);
        tvC3.setOnClickListener(this);
        tvD3.setOnClickListener(this);

        tvA4.setOnClickListener(this);
        tvB4.setOnClickListener(this);
        tvC4.setOnClickListener(this);
        tvD4.setOnClickListener(this);
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
                Intent intent = new Intent(DocHieuTestNhanhHTCauActivity.this, ResultDocHieuActivity.class);
                startActivity(intent);
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

    private void startCountDown() {

        countDownTimer = new CountDownTimer(myDataTG, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                myDataTG = millisUntilFinished;
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

    private void updateCountDownText() {
        int minutes = (int) ((myDataTG / 1000) / 60);
        int seconds = (int) ((myDataTG / 1000) % 60);
        String timeTG = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv_ThoiGian.setText(timeTG);
        if (myDataTG < 10000) {
            tv_ThoiGian.setTextColor(Color.RED);
        } else {
            tv_ThoiGian.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        count++;
        if (count >= 1) {
            finishQuestion();
        }
        count = 0;
    }

    //THOÁT QUA GIAO DIỆN CHÍNH
    private void finishQuestion() {
        //Chứa dữ liệu gửi activity
        Intent intent = new Intent();
        intent.putExtra("sorce", score);
        setResult(RESULT_OK, intent);
        finish();

    }


    public void checkAnswer(String dapAnChon, TextView textView, DocHieuHTDoanVan cauHoi, String dapAnDung) {
        Log.d("TAG", "checkAnswer: " + dapAnChon);
        Log.d("TAG", "checkAnswer: " + dapAnDung);
        Log.d("TAG", "checkAnswer: " + cauHoi);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dapAnChon.equalsIgnoreCase(dapAnDung)) {
                    textView.setBackgroundResource(R.drawable.bg_answer);
                    showCorrect(cauHoi);

                    // Wait for 3 seconds after showCorrect completes
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Code to execute after the 3-second delay
                            answeredQuestionsCount++; // If you're tracking answered questions

                            // Check if all questions on this page are answered
                            if (answeredQuestionsCount >= 4) {
                                // Reset counter for next page
                                answeredQuestionsCount = 0;
                                // Move to next set of questions
                                nextQuestion();
                            }
                        }
                    }, 3000); // 3 seconds delay

                } else {
                    textView.setBackgroundResource(R.drawable.bg_sai);
                    showCorrect(cauHoi);

                    // Wait for 3 seconds before showing game over dialog
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameOver();
                        }
                    }, 3000); // 3 seconds delay
                }
            }
        }, 1000); // Original 1 second delay
    }

    private void gameOver() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDiaLog("Rất tiếp bạn vừa chọn đán án sai !");
            }
        }, 500);
    }

    private void showCorrect(DocHieuHTDoanVan mDocHieuHTDoanVan) {
        Log.d("TAG", "showCorrect: " + mDocHieuHTDoanVan);
        if (mDocHieuHTDoanVan.getDapAnA().equals(arrayListDe.get(0).getDapAnDung())) {
            tvA1.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnB().equals(arrayListDe.get(0).getDapAnDung())) {
            tvB1.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnC().equals(arrayListDe.get(0).getDapAnDung())) {
            tvC1.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnD().equals(arrayListDe.get(0).getDapAnDung())) {
            tvD1.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnA().equals(arrayListDe.get(1).getDapAnDung())) {
            tvA2.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnB().equals(arrayListDe.get(1).getDapAnDung())) {
            tvB2.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnC().equals(arrayListDe.get(1).getDapAnDung())) {
            tvC2.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnD().equals(arrayListDe.get(1).getDapAnDung())) {
            tvD2.setBackgroundResource(R.drawable.bg_answer);
        }
        if (mDocHieuHTDoanVan.getDapAnA().equals(arrayListDe.get(2).getDapAnDung())) {
            tvA3.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnB().equals(arrayListDe.get(2).getDapAnDung())) {
            tvB3.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnC().equals(arrayListDe.get(2).getDapAnDung())) {
            tvC3.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnD().equals(arrayListDe.get(2).getDapAnDung())) {
            tvD3.setBackgroundResource(R.drawable.bg_answer);
        }
        if (mDocHieuHTDoanVan.getDapAnA().equals(arrayListDe.get(3).getDapAnDung())) {
            tvA4.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnB().equals(arrayListDe.get(3).getDapAnDung())) {
            tvB4.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnC().equals(arrayListDe.get(3).getDapAnDung())) {
            tvC4.setBackgroundResource(R.drawable.bg_answer);
        } else if (mDocHieuHTDoanVan.getDapAnD().equals(arrayListDe.get(3).getDapAnDung())) {
            tvD4.setBackgroundResource(R.drawable.bg_answer);
        }
        int id = tvCauHoi.getId();
        TextView selectedTextView = null;
        int questionIndex = currentQuestion;  // Default index

        if (id == R.id.tvA1 || id == R.id.tvB1 || id == R.id.tvC1 || id == R.id.tvD1) {
            questionIndex = currentQuestion;
        } else if (id == R.id.tvA2 || id == R.id.tvB2 || id == R.id.tvC2 || id == R.id.tvD2) {
            questionIndex = currentQuestion + 1;
        } else if (id == R.id.tvA3 || id == R.id.tvB3 || id == R.id.tvC3 || id == R.id.tvD3) {
            questionIndex = currentQuestion + 2;
        } else if (id == R.id.tvA4 || id == R.id.tvB4 || id == R.id.tvC4 || id == R.id.tvD4) {
            questionIndex = currentQuestion + 3;
        }

        if (questionIndex < arrayListDe.size()) {
            selectedTextView = findViewById(id);
            if (selectedTextView != null) {
                selectedTextView.setBackgroundResource(R.drawable.bg_select);

                String dapAnChon = getAnswerLetterFromId(id);  //  Lấy A/B/C/D từ ID
                String dapAnDung = arrayListDe.get(questionIndex).getDapAnDung();
                DocHieuHTDoanVan cauHoi = arrayListDe.get(questionIndex);

                if (dapAnChon != null) {
                    checkAnswer(dapAnChon, selectedTextView, cauHoi, dapAnDung);
                }
            }
        }
    }

    private void nextQuestion() {
        if (currentQuestion + 4 >= arrayListDe.size()) {
            showDiaLog("Hoàn thành tất cả các câu");
            Intent intent = new Intent(DocHieuTestNhanhHTCauActivity.this, ResultDocHieuActivity.class);
            startActivity(intent);
        } else {
            currentQuestion += 4; // Move forward by 4 questions
            answeredQuestionsCount = 0; // Reset the counter
            setDataQuestion(arrayListDe.get(currentQuestion));
        }
    }

    private void showDiaLog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                setDataQuestion(arrayListDe.get(currentQuestion));
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private String getAnswerLetterFromId(int id) {
        if (id == R.id.tvA || id == R.id.tvA1 || id == R.id.tvA2 || id == R.id.tvA3 || id == R.id.tvA4) {
            return "A";
        } else if (id == R.id.tvB || id == R.id.tvB1 || id == R.id.tvB2 || id == R.id.tvB3 || id == R.id.tvB4) {
            return "B";
        } else if (id == R.id.tvC || id == R.id.tvC1 || id == R.id.tvC2 || id == R.id.tvC3 || id == R.id.tvC4) {
            return "C";
        } else if (id == R.id.tvD || id == R.id.tvD1 || id == R.id.tvD2 || id == R.id.tvD3 || id == R.id.tvD4) {
            return "D";
        } else {
            return null;
        }
    }
}
