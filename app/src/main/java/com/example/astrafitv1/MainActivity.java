package com.example.astrafitv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView tvHr, tvMaxHr, tvCal, tvTotalCal, tvTime, tvTotalTime, tvDist, tvTotalDist, tvAvgHrChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a la UI
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView btnMenu = findViewById(R.id.btn_menu);

        // Referencias a los valores del dashboard
        tvHr = findViewById(R.id.hr_value);
        tvMaxHr = findViewById(R.id.max_hr_value);
        tvCal = findViewById(R.id.cal_value);
        tvTotalCal = findViewById(R.id.total_cal_value);
        tvTime = findViewById(R.id.time_value);
        tvTotalTime = findViewById(R.id.total_week_time);
        tvDist = findViewById(R.id.dist_value);
        tvTotalDist = findViewById(R.id.total_week_dist);
        tvAvgHrChart = findViewById(R.id.avg_hr_chart);

        // Cargar datos "reales" (simulados)
        loadFitnessData();

        // Marcar Inicio como seleccionado por defecto
        navigationView.setCheckedItem(R.id.nav_inicio);

        // 1. Lógica para abrir el menú con las rayitas naranjas
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 2. Lógica para manejar los clics dentro del menú
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_inicio) {
                // Ya estás aquí
                item.setChecked(true);
            } else if (id == R.id.nav_logout) {
                logout();
            }
            
            // Cerrar el menú después de dar clic
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadFitnessData() {
        // Aquí simulamos datos que vendrían de una base de datos o sensor
        FitnessData data = new FitnessData(
                78, 162, 512, 1874, 
                57, "4d 32min", 6.8, 38.2
        );

        // Actualizar la UI con los datos
        tvHr.setText(String.valueOf(data.getHeartRate()));
        tvMaxHr.setText(String.format(Locale.getDefault(), "%d lpm", data.getMaxHeartRate()));
        tvCal.setText(String.valueOf(data.getCalories()));
        tvTotalCal.setText(String.format(Locale.getDefault(), "%d kcal", data.getTotalCalories()));
        tvTime.setText(String.valueOf(data.getActiveMinutes()));
        tvTotalTime.setText(data.getTotalActiveTime());
        tvDist.setText(String.format(Locale.getDefault(), "%.1f", data.getDistance()));
        tvTotalDist.setText(String.format(Locale.getDefault(), "%.1f km", data.getTotalDistance()));
        tvAvgHrChart.setText(String.format(Locale.getDefault(), "PROMEDIO %d LPM", data.getHeartRate()));
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AstraFitPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Borra la sesión
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}