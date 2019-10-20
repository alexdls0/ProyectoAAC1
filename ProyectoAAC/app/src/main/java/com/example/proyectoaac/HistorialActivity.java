package com.example.proyectoaac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class HistorialActivity extends AppCompatActivity {

    private static final String FICHEROS = "ficheros";
    private ListView lvHistorial;
    private String[] archivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        SharedPreferences preferences = getSharedPreferences("exportar", Context.MODE_PRIVATE);
        String allFiles = preferences.getString(FICHEROS, null);

        lvHistorial = findViewById(R.id.lvHistorial);
        crearArray(allFiles);
        lvHistorial.setAdapter(new AdaptadorHistorial(this, archivos));
    }

    private void crearArray(String ficheros) {
        if(ficheros.length() > 5){
            ficheros = ficheros.substring(1);
            archivos = ficheros.split("-");
        }
    }
}
