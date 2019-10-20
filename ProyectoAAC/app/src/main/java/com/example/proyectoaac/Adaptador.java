package com.example.proyectoaac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class Adaptador extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context contexto;
    private List<Contacto> datos;

    public Adaptador(Context contexto, List<Contacto> datos){
        this.contexto = contexto;
        this.datos = datos;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.elemento_contacto, null);
        TextView tvContacto = vista.findViewById(R.id.tvContacto);
        tvContacto.setText(datos.get(i).toString());
        return vista;
    }

    @Override
    public int getCount() {
        return datos.size();
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
