package com.majd.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigateRunnable = () -> {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        FirebaseRepository repository = new FirebaseRepository();
        Intent next = repository.currentUser() == null
                ? new Intent(this, LoginActivity.class)
                : new Intent(this, HomeActivity.class);
        startActivity(next);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(navigateRunnable, 900);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(navigateRunnable);
    }
}
