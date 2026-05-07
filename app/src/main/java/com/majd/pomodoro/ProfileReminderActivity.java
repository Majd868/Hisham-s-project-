package com.majd.pomodoro;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ProfileReminderActivity extends AppCompatActivity {
    private TextView reminderValue;
    private PrefsManager prefs;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Toast.makeText(this, R.string.notification_permission_needed, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reminder);

        prefs = new PrefsManager(this);
        FirebaseRepository repository = new FirebaseRepository();
        FirebaseUser user = repository.currentUser();

        TextView userEmail = findViewById(R.id.userEmail);
        reminderValue = findViewById(R.id.reminderValue);
        Button setReminderButton = findViewById(R.id.setReminderButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        userEmail.setText(user != null ? user.getEmail() : getString(R.string.no_user));
        renderReminderTime();

        setReminderButton.setOnClickListener(v -> openTimePicker());
        logoutButton.setOnClickListener(v -> {
            repository.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        maybeRequestNotificationPermission();
    }

    private void renderReminderTime() {
        reminderValue.setText(String.format(Locale.getDefault(), "%02d:%02d", prefs.getReminderHour(), prefs.getReminderMinute()));
    }

    private void openTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            prefs.saveReminderTime(hourOfDay, minute);
            ReminderScheduler.scheduleDailyReminder(this, hourOfDay, minute);
            renderReminderTime();
            new FirebaseRepository().saveStudyState(
                    prefs.getFocusMin(),
                    prefs.getBreakMin(),
                    prefs.getBlocks(),
                    prefs.getSessionsCompleted(),
                    prefs.getTotalFocusMin(),
                    hourOfDay,
                    minute);
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        }, prefs.getReminderHour(), prefs.getReminderMinute(), true).show();
    }

    private void maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
