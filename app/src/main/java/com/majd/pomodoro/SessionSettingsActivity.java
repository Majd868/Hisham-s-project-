package com.majd.pomodoro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SessionSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_settings);

        PrefsManager prefs = new PrefsManager(this);

        EditText focusInput = findViewById(R.id.settingsFocusInput);
        EditText breakInput = findViewById(R.id.settingsBreakInput);
        EditText blocksInput = findViewById(R.id.settingsBlocksInput);
        Button saveButton = findViewById(R.id.saveSettingsButton);

        focusInput.setText(String.valueOf(prefs.getFocusMin()));
        breakInput.setText(String.valueOf(prefs.getBreakMin()));
        blocksInput.setText(String.valueOf(prefs.getBlocks()));

        saveButton.setOnClickListener(v -> {
            try {
                int focus = Math.max(1, Integer.parseInt(focusInput.getText().toString()));
                int brk = Math.max(1, Integer.parseInt(breakInput.getText().toString()));
                int blocks = Math.max(1, Integer.parseInt(blocksInput.getText().toString()));
                prefs.saveSessionSettings(focus, brk, blocks);
                new FirebaseRepository().saveStudyState(
                        focus,
                        brk,
                        blocks,
                        prefs.getSessionsCompleted(),
                        prefs.getTotalFocusMin(),
                        prefs.getReminderHour(),
                        prefs.getReminderMinute());
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_values, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
