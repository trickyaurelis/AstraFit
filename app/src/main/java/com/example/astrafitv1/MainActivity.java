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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView tvHr, tvMaxHr, tvCal, tvTotalCal, tvTime, tvTotalTime, tvDist, tvTotalDist, tvAvgHrChart;
    private TextView tvUserWelcome;
    private LineChart heartRateChart;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration dataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Referencias a la UI
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView btnMenu = findViewById(R.id.btn_menu);
        tvUserWelcome = findViewById(R.id.tv_user_welcome);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_activity);

        // Actualizar Header y Bienvenida
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserEmail = headerView.findViewById(R.id.header_subtitle);
        if (currentUser.getEmail() != null) {
            tvUserEmail.setText(currentUser.getEmail());
            // Usar la parte del correo antes del @ como nombre provisional
            String name = currentUser.getEmail().split("@")[0];
            tvUserWelcome.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        }

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
        heartRateChart = findViewById(R.id.heartRateChart);

        // Escuchar cambios en Firestore en tiempo real
        listenToFitnessData(currentUser.getUid());

        fabAdd.setOnClickListener(v -> showAddActivityDialog(currentUser.getUid()));

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

    private void listenToFitnessData(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        dataListener = docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                updateUI(snapshot.getData());
            } else {
                // Inicializar datos si no existen
                initUserData(userId);
            }
        });
    }

    private void initUserData(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("heartRate", 78);
        data.put("maxHeartRate", 162);
        data.put("calories", 512);
        data.put("totalCalories", 1874);
        data.put("activeMinutes", 57);
        data.put("totalActiveTime", "4d 32min");
        data.put("distance", 6.8);
        data.put("totalDistance", 38.2);
        
        db.collection("users").document(userId).set(data);
    }

    private void updateUI(Map<String, Object> data) {
        if (data == null) return;

        try {
            tvHr.setText(String.valueOf(data.get("heartRate")));
            tvMaxHr.setText(String.format(Locale.getDefault(), "%s lpm", data.get("maxHeartRate")));
            tvCal.setText(String.valueOf(data.get("calories")));
            tvTotalCal.setText(String.format(Locale.getDefault(), "%s kcal", data.get("totalCalories")));
            tvTime.setText(String.valueOf(data.get("activeMinutes")));
            tvTotalTime.setText(String.valueOf(data.get("totalActiveTime")));
            tvDist.setText(String.format(Locale.getDefault(), "%.1f", Double.parseDouble(data.get("distance").toString())));
            tvTotalDist.setText(String.format(Locale.getDefault(), "%.1f km", Double.parseDouble(data.get("totalDistance").toString())));
            tvAvgHrChart.setText(String.format(Locale.getDefault(), "PROMEDIO %s LPM", data.get("heartRate")));
            
            // Re-setup chart with a bit of randomness for "live" feel if desired, 
            // or just refresh static points
            setupChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddActivityDialog(String userId) {
        // Por simplicidad, añadiremos una actividad fija para demostrar el flujo
        // En una app real, aquí abrirías un AlertDialog con campos de texto
        
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long currentCal = documentSnapshot.getLong("calories");
                long currentMin = documentSnapshot.getLong("activeMinutes");
                
                Map<String, Object> updates = new HashMap<>();
                updates.put("calories", currentCal + 100);
                updates.put("activeMinutes", currentMin + 15);
                
                db.collection("users").document(userId).update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "¡Actividad registrada!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataListener != null) {
            dataListener.remove();
        }
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

    private void setupChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 70));
        entries.add(new Entry(4, 78));
        entries.add(new Entry(8, 72));
        entries.add(new Entry(12, 112)); // Pico
        entries.add(new Entry(16, 85));
        entries.add(new Entry(20, 95));
        entries.add(new Entry(24, 75));

        LineDataSet dataSet = new LineDataSet(entries, "Ritmo Cardíaco");
        dataSet.setColor(android.graphics.Color.parseColor("#FF6600"));
        dataSet.setCircleColor(android.graphics.Color.parseColor("#FF6600"));
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(android.graphics.Color.parseColor("#33FF6600"));

        LineData lineData = new LineData(dataSet);
        heartRateChart.setData(lineData);
        
        // Estilo del chart
        heartRateChart.getAxisRight().setEnabled(false);
        heartRateChart.getXAxis().setTextColor(android.graphics.Color.WHITE);
        heartRateChart.getAxisLeft().setTextColor(android.graphics.Color.WHITE);
        heartRateChart.getLegend().setTextColor(android.graphics.Color.WHITE);
        heartRateChart.getDescription().setEnabled(false);
        
        heartRateChart.invalidate();
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