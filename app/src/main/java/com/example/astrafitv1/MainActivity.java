package com.example.astrafitv1;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a la UI
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView btnMenu = findViewById(R.id.btn_menu);

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
            // Marcar como seleccionado
            item.setChecked(true);
            
            int id = item.getItemId();
            
            if (id == R.id.nav_inicio) {
                // Ya estás aquí
            }
            
            // Cerrar el menú después de dar clic
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}