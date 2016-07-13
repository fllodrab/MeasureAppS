package com.example.fllodrab.measureappss;

import java.util.Date;

/**
 * Created by FllodraB.
 */
public class Usuarios {
    public int id;
    public String nick;
    public String nombre;
    public String apellidos;
    public String email;
    public Date fecha;

    public Usuarios(int anInt, String string, String string1, String string2) {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public Date getFecha() {
        return fecha;
    }
}
