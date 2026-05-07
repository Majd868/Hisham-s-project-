package com.majd.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView title;
    private boolean registerMode = false;

    private final FirebaseRepository repository = new FirebaseRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        title = findViewById(R.id.authTitle);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button submitButton = findViewById(R.id.submitButton);
        Button toggleButton = findViewById(R.id.toggleModeButton);

        submitButton.setOnClickListener(v -> submit());
        toggleButton.setOnClickListener(v -> {
            registerMode = !registerMode;
            updateMode();
        });

        updateMode();
    }

    private void updateMode() {
        title.setText(registerMode ? R.string.register : R.string.login);
        nameInput.setEnabled(registerMode);
        nameInput.setAlpha(registerMode ? 1f : 0.5f);
    }

    private void submit() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || (registerMode && TextUtils.isEmpty(name))) {
            Toast.makeText(this, R.string.fill_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (registerMode) {
            repository.register(name, email, password,
                    unused -> openHome(),
                    () -> Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show());
        } else {
            repository.signIn(email, password,
                    unused -> openHome(),
                    () -> Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show());
        }
    }

    private void openHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
