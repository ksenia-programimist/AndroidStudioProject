package com.example.myapplicationproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvLevel, tvRadix, tvLives, tvQuestion;
    private EditText etAnswer;
    private Button btnSubmit, btnYes, btnNo;
    private LinearLayout layoutInputAnswer, layoutBinaryAnswer;
    private ProgressBar progressBarTimer;

    private QuestionEngine engine;
    private CountDownTimer timer;
    private AlertDialog currentDialog;

    private LinearLayout layoutStartScreen;
    private Button btnGameStart;

    private int currentLevel = 1;
    private int correctAnswersInRow = 0;
    private int lives = 3;
    private final long baseTimeLimitMs = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLevel = findViewById(R.id.tvLevel);
        tvRadix = findViewById(R.id.tvRadix);
        tvLives = findViewById(R.id.tvLives);
        tvQuestion = findViewById(R.id.tvQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        layoutInputAnswer = findViewById(R.id.layoutInputAnswer);
        layoutBinaryAnswer = findViewById(R.id.layoutBinaryAnswer);
        progressBarTimer = findViewById(R.id.progressBarTimer);

        engine = new QuestionEngine();

        btnSubmit.setOnClickListener(v -> checkTextInputAnswer());
        btnYes.setOnClickListener(v -> checkBooleanInputAnswer(true));
        btnNo.setOnClickListener(v -> checkBooleanInputAnswer(false));

        layoutStartScreen = findViewById(R.id.layoutStartScreen);
        btnGameStart = findViewById(R.id.btnGameStart);

        btnSubmit.setOnClickListener(v -> checkTextInputAnswer());
        btnYes.setOnClickListener(v -> checkBooleanInputAnswer(true));
        btnNo.setOnClickListener(v -> checkBooleanInputAnswer(false));

        btnGameStart.setOnClickListener(v -> {
            layoutStartScreen.setVisibility(View.GONE);
            startGame();
        });

    }

    private void startGame() {
        currentLevel = 1;
        correctAnswersInRow = 0;
        lives = 3;
        updateStatusBars();
        nextQuestion();
    }

    private void nextQuestion() {
        engine.generate(currentLevel);
        updateStatusBars();
        if (engine.currentType == QuestionEngine.Type.TEXT_INPUT) {
            layoutInputAnswer.setVisibility(View.VISIBLE);
            layoutBinaryAnswer.setVisibility(View.GONE);
            etAnswer.setText("");
        } else {
            layoutInputAnswer.setVisibility(View.GONE);
            layoutBinaryAnswer.setVisibility(View.VISIBLE);
        }

        tvQuestion.setText(engine.questionText);
        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        long currentTimeLimit = Math.max(5000, baseTimeLimitMs - ((currentLevel - 1) * 3000));
        progressBarTimer.setMax((int) currentTimeLimit);

        timer = new CountDownTimer(currentTimeLimit, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarTimer.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                progressBarTimer.setProgress(0);
                processWrongAnswer("Время вышло!");
            }
        }.start();
    }

    private void checkTextInputAnswer() {
        String userAnswer = etAnswer.getText().toString().trim().toUpperCase();
        if (userAnswer.equals(engine.correctTextAnswer)) {
            processCorrectAnswer();
        } else {
            processWrongAnswer("Неверно!\nПравильный ответ: " + engine.correctTextAnswer);
        }
    }

    private void checkBooleanInputAnswer(boolean userChoice) {
        if (userChoice == engine.correctBooleanAnswer) {
            processCorrectAnswer();
        } else {
            String correctStr = engine.correctBooleanAnswer ? "ДА" : "НЕТ";
            processWrongAnswer("Неверно!\nПравильный ответ: " + correctStr);
        }
    }

    private void processCorrectAnswer() {
        if (timer != null) timer.cancel();

        correctAnswersInRow++;

        if (correctAnswersInRow >= 7) {
            correctAnswersInRow = 0;
            currentLevel++;
        }

        nextQuestion();
    }

    private void processWrongAnswer(String message) {
        if (timer != null) timer.cancel();
        lives--;
        updateStatusBars();

        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Далее", (dialog, id) -> {
                    // ТЗ: Если 3 неверных ответа — игра завершается
                    if (lives <= 0) {
                        showGameOverDialog();
                    } else {
                        nextQuestion();
                    }
                });
        currentDialog = builder.create();
        currentDialog.show();
    }

    private void updateStatusBars() {
        tvLevel.setText("Уровень: " + currentLevel);
        tvRadix.setText("СС: " + engine.currentRadix);

        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) hearts.append("❤️");
        for (int i = lives; i < 3; i++) hearts.append("🖤");
        tvLives.setText("Жизни: " + hearts.toString());
    }

    private void showGameOverDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Игра окончена")
                .setMessage("Вы дошли до " + currentLevel + " уровня!")
                .setCancelable(false)
                .setPositiveButton("Играть снова", (dialog, id) -> {
                    layoutStartScreen.setVisibility(View.VISIBLE);
                });
        currentDialog = builder.create();
        currentDialog.show();
    }


    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        super.onDestroy();
    }
}