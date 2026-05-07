package com.majd.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseRepository repository = new FirebaseRepository();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent next = repository.currentUser() == null
                    ? new Intent(this, LoginActivity.class)
                    : new Intent(this, HomeActivity.class);
            startActivity(next);
            finish();
        }, 900);
    }
}
