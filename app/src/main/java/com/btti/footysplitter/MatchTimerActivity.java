package com.btti.footysplitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.btti.teamfutsorteio.R;

public class MatchTimerActivity extends AppCompatActivity {
    private TextView textViewTimer, textViewTeam1Score, textViewTeam2Score;
    private Button buttonStartPause, buttonBackToTeams;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 10 * 60 * 1000;
    private SharedPreferences preferences;
    private Button buttonRestartTimer;


    private static final String PREFS_NAME = "MatchPrefs";
    private static final String KEY_TIME_LEFT = "timeLeft";
    private static final String KEY_TIMER_RUNNING = "timerRunning";
    private static final String KEY_TEAM1_SCORE = "team1Score";
    private static final String KEY_TEAM2_SCORE = "team2Score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_timer);

        textViewTimer = findViewById(R.id.textViewTimer);
        textViewTeam1Score = findViewById(R.id.textViewTeam1Score);
        textViewTeam2Score = findViewById(R.id.textViewTeam2Score);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        buttonBackToTeams = findViewById(R.id.buttonBackToTeams);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadTimerState();
        loadScoreState();

        buttonStartPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        buttonBackToTeams.setOnClickListener(v -> {
            saveTimerState();
            saveScoreState();
            Intent intent = new Intent(MatchTimerActivity.this, TeamsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.buttonIncreaseTeam1).setOnClickListener(v -> updateScore(1, 1));
        findViewById(R.id.buttonDecreaseTeam1).setOnClickListener(v -> updateScore(1, -1));
        findViewById(R.id.buttonIncreaseTeam2).setOnClickListener(v -> updateScore(2, 1));
        findViewById(R.id.buttonDecreaseTeam2).setOnClickListener(v -> updateScore(2, -1));

        buttonRestartTimer = findViewById(R.id.buttonRestartTimer);
        buttonRestartTimer.setVisibility(View.GONE);

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                buttonStartPause.setText("Iniciar");
                saveTimerState();
            }
        }.start();

        isTimerRunning = true;
        buttonStartPause.setText("Pausar");
        saveTimerState();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        buttonStartPause.setText("Iniciar");

        buttonRestartTimer.setVisibility(View.VISIBLE);

        saveTimerState();

        buttonRestartTimer.setOnClickListener(v -> restartTimer());

    }

    private void restartTimer() {
        timeLeftInMillis = 10 * 60 * 1000;
        updateTimerText();
        buttonRestartTimer.setVisibility(View.GONE);
        buttonStartPause.setText("Iniciar");
        isTimerRunning = false;
        saveTimerState();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        textViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void updateScore(int team, int change) {
        int score;
        if (team == 1) {
            score = Integer.parseInt(textViewTeam1Score.getText().toString());
            score = Math.max(0, score + change);
            textViewTeam1Score.setText(String.valueOf(score));
        } else {
            score = Integer.parseInt(textViewTeam2Score.getText().toString());
            score = Math.max(0, score + change);
            textViewTeam2Score.setText(String.valueOf(score));
        }
        saveScoreState();
    }

    private void saveTimerState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(KEY_TIMER_RUNNING, isTimerRunning);
        editor.apply();
    }

    private void saveScoreState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_TEAM1_SCORE, Integer.parseInt(textViewTeam1Score.getText().toString()));
        editor.putInt(KEY_TEAM2_SCORE, Integer.parseInt(textViewTeam2Score.getText().toString()));
        editor.apply();
    }

    private void loadScoreState() {
        textViewTeam1Score.setText(String.valueOf(preferences.getInt(KEY_TEAM1_SCORE, 0)));
        textViewTeam2Score.setText(String.valueOf(preferences.getInt(KEY_TEAM2_SCORE, 0)));
    }

    private void loadTimerState() {
        timeLeftInMillis = preferences.getLong(KEY_TIME_LEFT, 10 * 60 * 1000);
        isTimerRunning = preferences.getBoolean(KEY_TIMER_RUNNING, false);

        if (isTimerRunning) {
            startTimer();
        } else {
            updateTimerText();
        }
    }
}
