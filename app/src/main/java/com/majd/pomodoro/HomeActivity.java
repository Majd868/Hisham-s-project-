package com.majd.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private PomodoroEngine engine;
    private CountDownTimer timer;
    private TextView statusText;
    private TextView timerText;
    private TextView blockText;
    private EditText focusInput;
    private EditText breakInput;
    private EditText blocksInput;

    private final FirebaseRepository repository = new FirebaseRepository();
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefsManager = new PrefsManager(this);
        statusText = findViewById(R.id.statusText);
        timerText = findViewById(R.id.timerText);
        blockText = findViewById(R.id.blockText);
        focusInput = findViewById(R.id.focusInput);
        breakInput = findViewById(R.id.breakInput);
        blocksInput = findViewById(R.id.blocksInput);
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button settingsButton = findViewById(R.id.sessionSettingsButton);
        Button statsButton = findViewById(R.id.statsButton);
        Button profileButton = findViewById(R.id.profileButton);

        focusInput.setText(String.valueOf(prefsManager.getFocusMin()));
        breakInput.setText(String.valueOf(prefsManager.getBreakMin()));
        blocksInput.setText(String.valueOf(prefsManager.getBlocks()));

        startButton.setOnClickListener(v -> startPomodoro());
        stopButton.setOnClickListener(v -> stopPomodoro());
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SessionSettingsActivity.class)));
        statsButton.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileReminderActivity.class)));

        statusText.setText(R.string.status_idle);
        timerText.setText(formatTime(0));
        blockText.setText(getString(R.string.block_indicator, 0, 0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPomodoro();
    }

    private void startPomodoro() {
        int focus;
        int brk;
        int blocks;
        try {
            focus = Math.max(1, Integer.parseInt(focusInput.getText().toString()));
            brk = Math.max(1, Integer.parseInt(breakInput.getText().toString()));
            blocks = Math.max(1, Integer.parseInt(blocksInput.getText().toString()));
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.invalid_values, Toast.LENGTH_SHORT).show();
            return;
        }

        prefsManager.saveSessionSettings(focus, brk, blocks);
        engine = new PomodoroEngine(focus, brk, blocks);
        runPhase(engine.getCurrentDurationMillis());
        updateUiForPhase();
    }

    private void runPhase(long durationMs) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                if (engine == null) {
                    return;
                }
                PomodoroEngine.Phase next = engine.advance();
                if (next == PomodoroEngine.Phase.COMPLETED) {
                    onSessionCompleted();
                } else {
                    updateUiForPhase();
                    runPhase(engine.getCurrentDurationMillis());
                }
            }
        };
        timer.start();
    }

    private void updateUiForPhase() {
        if (engine == null) {
            return;
        }
        statusText.setText(engine.getPhase() == PomodoroEngine.Phase.FOCUS ? R.string.status_focus : R.string.status_break);
        blockText.setText(getString(R.string.block_indicator, engine.getCurrentBlock(), engine.getTotalBlocks()));
        timerText.setText(formatTime(engine.getCurrentDurationMillis()));
    }

    private void onSessionCompleted() {
        stopPomodoro();
        statusText.setText(R.string.status_completed);
        int focusMinutes = engine.getFocusMin() * engine.getTotalBlocks();
        prefsManager.trackCompletedSession(focusMinutes);
        repository.saveStudyState(
                prefsManager.getFocusMin(),
                prefsManager.getBreakMin(),
                prefsManager.getBlocks(),
                prefsManager.getSessionsCompleted(),
                prefsManager.getTotalFocusMin(),
                prefsManager.getReminderHour(),
                prefsManager.getReminderMinute()
        );
        Toast.makeText(this, R.string.session_completed, Toast.LENGTH_SHORT).show();
        blockText.setText(getString(R.string.block_indicator, engine.getTotalBlocks(), engine.getTotalBlocks()));
    }

    private void stopPomodoro() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String formatTime(long millis) {
        long totalSeconds = Math.max(0, millis / 1000);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
