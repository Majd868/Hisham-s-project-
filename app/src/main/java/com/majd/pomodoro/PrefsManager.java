package com.majd.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(AppConstants.PREFS, Context.MODE_PRIVATE);
    }

    public int getFocusMin() {
        return prefs.getInt(AppConstants.KEY_FOCUS_MIN, 25);
    }

    public int getBreakMin() {
        return prefs.getInt(AppConstants.KEY_BREAK_MIN, 5);
    }

    public int getBlocks() {
        return prefs.getInt(AppConstants.KEY_BLOCKS, 4);
    }

    public void saveSessionSettings(int focusMin, int breakMin, int blocks) {
        prefs.edit()
                .putInt(AppConstants.KEY_FOCUS_MIN, focusMin)
                .putInt(AppConstants.KEY_BREAK_MIN, breakMin)
                .putInt(AppConstants.KEY_BLOCKS, blocks)
                .apply();
    }

    public int getReminderHour() {
        return prefs.getInt(AppConstants.KEY_REMINDER_HOUR, 20);
    }

    public int getReminderMinute() {
        return prefs.getInt(AppConstants.KEY_REMINDER_MINUTE, 0);
    }

    public void saveReminderTime(int hour, int minute) {
        prefs.edit()
                .putInt(AppConstants.KEY_REMINDER_HOUR, hour)
                .putInt(AppConstants.KEY_REMINDER_MINUTE, minute)
                .apply();
    }

    public int getSessionsCompleted() {
        return prefs.getInt(AppConstants.KEY_SESSIONS_COMPLETED, 0);
    }

    public long getTotalFocusMin() {
        return prefs.getLong(AppConstants.KEY_TOTAL_FOCUS_MIN, 0L);
    }

    public void trackCompletedSession(long focusMinutesCompleted) {
        int sessions = getSessionsCompleted() + 1;
        long total = getTotalFocusMin() + Math.max(focusMinutesCompleted, 0L);
        prefs.edit()
                .putInt(AppConstants.KEY_SESSIONS_COMPLETED, sessions)
                .putLong(AppConstants.KEY_TOTAL_FOCUS_MIN, total)
                .apply();
    }
}
