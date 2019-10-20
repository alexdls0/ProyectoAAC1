package com.example.proyectoaac;

import java.util.List;

public class Contacto {
    private long id;
    private String nombre;
    private String telefonos;

    public long getId() {
        return id;
    }

    public Contacto setId(long id) {
        this.id = id;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Contacto setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getTelefonos(){
        return telefonos;
    }

    public Contacto setTelefonos(List<String> telefonos) {
        String listadoTelefonos = "";
        for (int i = 0; i < telefonos.size(); i++) {
            listadoTelefonos +=telefonos.get(i).toString() + "\n";
        }
        this.telefonos = listadoTelefonos;
        return this;
    }

    @Override
    public String toString() {
        //return "id," + this.id + ",nombre," + this.nombre + "telefonos" +this.telefonos;
        return this.nombre + ":\n" +this.telefonos;
    }
}
