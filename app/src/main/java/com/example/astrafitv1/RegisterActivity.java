package com.example.astrafitv1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_reg_username);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        btnRegister = findViewById(R.id.btn_register);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresa al Login
            }
        });
    }

    private void registerUser() {
        String user = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar el nuevo usuario en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AstraFitUsers", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Guardamos la contraseña asociada al nombre de usuario
        editor.putString(user, pass);
        editor.apply();

        Toast.makeText(this, "Registro exitoso. ¡Ya puedes iniciar sesión!", Toast.LENGTH_LONG).show();
        finish(); // Volver al login
    }
}