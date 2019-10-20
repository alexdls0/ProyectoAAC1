package com.example.proyectoaac;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdaptadorHistorial extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private static final String ARCHIVO_LEER = "com.example.proyectoaac.ARCHIVO_LEER";
    private Context contexto;
    private String[] datos;
    private String nombre;
    private String i;

    public AdaptadorHistorial(Context contexto, String[] datos){
        this.contexto = contexto;
        this.datos = datos;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.elemento_historial, null);
        String[] archivo = datos[position].split("#");
        nombre = archivo[0];
        TextView tvFileName = vista.findViewById(R.id.tvFileName);
        tvFileName.setText("File: "+nombre);

        i = archivo[archivo.length-1];
        if(i.equalsIgnoreCase("1")){
            TextView tvFileLocation = vista.findViewById(R.id.tvFileLocation);
            tvFileLocation.setText("Private file");
        }

        if(i.equalsIgnoreCase("0")){
            TextView tvFileLocation = vista.findViewById(R.id.tvFileLocation);
            tvFileLocation.setText("Internal file");
        }

        Button btAbrir = vista.findViewById(R.id.btOpen);

        btAbrir.setTag(datos[position]);
        btAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( contexto, LecturaActivity.class);
                intent.putExtra(ARCHIVO_LEER, (String) v.getTag());
                contexto.startActivity(intent);
            }
        });

        return vista;
    }

    @Override
    public int getCount() {
        return datos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
