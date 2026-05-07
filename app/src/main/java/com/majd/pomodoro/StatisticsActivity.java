package com.majd.pomodoro;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        PrefsManager prefs = new PrefsManager(this);
        int sessions = prefs.getSessionsCompleted();
        int totalFocusMin = prefs.getTotalFocusMin();
        int avg = sessions == 0 ? 0 : totalFocusMin / sessions;

        ((TextView) findViewById(R.id.sessionsValue)).setText(String.valueOf(sessions));
        ((TextView) findViewById(R.id.totalFocusValue)).setText(getString(R.string.minutes_value, totalFocusMin));
        ((TextView) findViewById(R.id.avgFocusValue)).setText(getString(R.string.minutes_value, avg));
        ((TextView) findViewById(R.id.lastSettingsValue)).setText(
                getString(R.string.last_settings_pattern, prefs.getFocusMin(), prefs.getBreakMin(), prefs.getBlocks()));
    }
}
