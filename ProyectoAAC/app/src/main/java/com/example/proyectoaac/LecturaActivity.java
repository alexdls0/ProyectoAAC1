package com.example.proyectoaac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LecturaActivity extends AppCompatActivity {

    private static final String ARCHIVO_LEER = "com.example.proyectoaac.ARCHIVO_LEER";
    private static final int INTERNO = 0;
    private static final int  PRIVADO = 1;
    private TextView tvContenidoSingle;
    private TextView tvTituloSingle;
    private String titulo;
    private String tipo;
    private String contenido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura);

        Intent intent = getIntent();

        String[] info = intent.getStringExtra(ARCHIVO_LEER).split("#");
        titulo = info[0];
        tipo = info[info.length-1];
        initComponents();
    }

    private void initComponents() {

        tvTituloSingle = findViewById(R.id.tvTituloSingle);
        tvTituloSingle.setText(titulo);
        tvContenidoSingle = findViewById(R.id.tvContenidoSingle);
        tvContenidoSingle.setText(sacarContenido(titulo));
    }

    public String sacarContenido(String titulo){
        String linea = "";
        File f = new File (getPath(LecturaActivity.this, Integer.parseInt(tipo)), titulo);
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));
            while ((linea = br.readLine()) != null) {
                contenido += linea+"\n";
            }
            br.close();
            return contenido;
        }
        catch(IOException e)
        {
            contenido = "File Not Found";
        }
        return contenido;
    }

    private static File getPath(Context context, int loc){
        File file = null;
        switch(loc){
            case INTERNO:
                file = context.getFilesDir();
                break;
            case PRIVADO:
                file = context.getExternalFilesDir(null);
                break;
        }
        return file;
    }
}
