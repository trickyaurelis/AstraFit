package com.example.astrafitv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verificar si ya está logueado
        sharedPreferences = getSharedPreferences("AstraFitPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        TextView tvGoToRegister = findViewById(R.id.tv_go_to_register); // Necesitaremos este ID en el XML

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString();
                String pass = etPassword.getText().toString();

                // 1. Verificar contra SharedPreferences (Usuarios registrados)
                SharedPreferences userPrefs = getSharedPreferences("AstraFitUsers", MODE_PRIVATE);
                String savedPass = userPrefs.getString(user, null);

                // 2. Permitir también el admin/1234 por defecto
                if ((user.equals("admin") && pass.equals("1234")) || (savedPass != null && savedPass.equals(pass))) {
                    // Guardar sesión de forma ligera y local
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", user);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "¡Bienvenido a Astra Fit!", Toast.LENGTH_SHORT).show();
                    
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}