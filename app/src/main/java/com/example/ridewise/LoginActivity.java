package com.example.ridewise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView goToSignup;
    ImageView loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        goToSignup = findViewById(R.id.goToSignup);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        ImageView loginCar = findViewById(R.id.loginCar);
        loginCar.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.car_drive_in)
        );

        btnLogin.setOnClickListener(v -> {

            loadingSpinner.setVisibility(ImageView.VISIBLE);
            btnLogin.setEnabled(false);

            new Handler().postDelayed(() -> {

                loadingSpinner.setVisibility(ImageView.GONE);
                btnLogin.setEnabled(true);

                Toast.makeText(LoginActivity.this,
                        "Demo Login Successful 🚀",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LoginActivity.this,
                        MainActivity.class));
                finish();

            }, 1500);
        });

        goToSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this,
                        SignupActivity.class)));
    }
}