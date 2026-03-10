package com.example.ridewise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password;
    Button btnSignup;
    TextView goToLogin;
    ImageView loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);


        name = findViewById(R.id.inputName);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        btnSignup = findViewById(R.id.btnSignup);
        goToLogin = findViewById(R.id.goToLogin);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        btnSignup.setOnClickListener(v -> {

            if (name.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() ||
                    password.getText().toString().isEmpty()) {

                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            }
            else {

                loadingSpinner.setVisibility(View.VISIBLE);
                loadingSpinner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
                btnSignup.setEnabled(false);

                new Handler().postDelayed(() -> {

                    loadingSpinner.clearAnimation();
                    loadingSpinner.setVisibility(View.GONE);
                    btnSignup.setEnabled(true);

                    Toast.makeText(SignupActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();

                }, 2000);
            }
        });

        goToLogin.setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }
}
