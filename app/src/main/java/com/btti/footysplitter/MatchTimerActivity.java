package com.btti.footysplitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.btti.teamfutsorteio.R;

public class MatchTimerActivity extends AppCompatActivity {
    private TextView textViewTimer;
    private Button buttonStartPause, buttonStopVibration, buttonReset, buttonBackToTeams, buttonRestart;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private long endTime;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "MatchPrefs";
    private static final String KEY_TIME_LEFT = "timeLeft";
    private static final String KEY_TIMER_RUNNING = "timerRunning";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_TIMER_FINISHED = "timerFinished";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_timer);

        textViewTimer = findViewById(R.id.textViewTimer);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        buttonStopVibration = findViewById(R.id.buttonStopVibration);
        buttonReset = findViewById(R.id.buttonReset);
        buttonBackToTeams = findViewById(R.id.buttonBackToTeams);
        buttonRestart = findViewById(R.id.buttonReset);

        mediaPlayer = MediaPlayer.create(this, R.raw.whistle);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadTimerState();

        buttonStartPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        buttonStopVibration.setOnClickListener(v -> stopVibrationAndSound());

        buttonReset.setOnClickListener(v -> resetTimer());

        buttonRestart.setOnClickListener(v -> restartTimer());

        buttonBackToTeams.setOnClickListener(v -> {
            if (timeLeftInMillis <= 0) {
                resetTimer();
            }
            saveTimerState();
            Intent intent = new Intent(MatchTimerActivity.this, TeamsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        buttonStopVibration.setVisibility(View.GONE);
        buttonReset.setVisibility(View.GONE);
        buttonRestart.setVisibility(View.GONE);
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                onFinishTimer();
            }
        }.start();

        isTimerRunning = true;
        buttonStartPause.setText("Pausar");
        buttonRestart.setVisibility(View.GONE);
        saveTimerState();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        buttonStartPause.setText("Iniciar");
        buttonRestart.setVisibility(View.VISIBLE);
        saveTimerState();
    }

    private void restartTimer() {
        timeLeftInMillis = 10 * 60 * 10;
        updateTimerText();
        buttonRestart.setVisibility(View.GONE);
        buttonStartPause.setText("Iniciar");
        isTimerRunning = false;
        saveTimerState();
    }

    private void resetTimer() {
        stopVibrationAndSound();
        timeLeftInMillis = 10 * 60 * 1000;
        updateTimerText();
        isTimerRunning = false;
        buttonStartPause.setVisibility(View.VISIBLE);
        buttonStartPause.setText("Iniciar");
        buttonReset.setVisibility(View.GONE);
        buttonStopVibration.setVisibility(View.GONE);
        buttonRestart.setVisibility(View.GONE);
        saveTimerState();
    }


    private void onFinishTimer() {
        isTimerRunning = false;
        buttonStartPause.setVisibility(View.GONE);
        buttonStopVibration.setVisibility(View.VISIBLE);
        buttonReset.setVisibility(View.VISIBLE);
        buttonRestart.setVisibility(View.GONE);

        startSoundLoop();
        startVibration();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_TIMER_FINISHED, true);
        editor.apply();
    }

    private void startVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{500, 1000}, 0));
        }
    }

    private void startSoundLoop() {
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void stopVibrationAndSound() {
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
        buttonStopVibration.setVisibility(View.GONE);
        buttonReset.setVisibility(View.VISIBLE);
        buttonRestart.setVisibility(View.VISIBLE);
        buttonStartPause.setVisibility(View.GONE);
        saveTimerState();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeFormatted);
    }

    private void saveTimerState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_TIME_LEFT, timeLeftInMillis);
        editor.putBoolean(KEY_TIMER_RUNNING, isTimerRunning);
        editor.putLong(KEY_END_TIME, endTime);
        editor.apply();
    }

    private void loadTimerState() {
        timeLeftInMillis = preferences.getLong(KEY_TIME_LEFT, 10 * 60 * 10);
        isTimerRunning = preferences.getBoolean(KEY_TIMER_RUNNING, false);
        endTime = preferences.getLong(KEY_END_TIME, 0);

        if (isTimerRunning) {
            timeLeftInMillis = endTime - System.currentTimeMillis();
            if (timeLeftInMillis <= 0) {
                onFinishTimer();
            } else {
                startTimer();
            }
        } else {
            updateTimerText();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibrationAndSound();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
