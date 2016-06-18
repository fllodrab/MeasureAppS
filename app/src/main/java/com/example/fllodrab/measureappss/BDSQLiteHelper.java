package com.example.fllodrab.measureappss;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by FllodraB.
 */
public class BDSQLiteHelper extends SQLiteOpenHelper {

    public BDSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuarios (id INTEGER, nick VARCHAR(50), nombre VARCHAR(50), apellidos VARCHAR(100), email VARCHAR(50), fecha_alta DATE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Crear una lista para recuperar los usuarios
        ArrayList<Usuarios> usuarios = new ArrayList<Usuarios>();

        //Recuperar todos los registros para no perder los datos
        Cursor c = db.rawQuery(" SELECT id, nick, nombre, apellidos, fecha_alta FROM Usuarios", null);
        if (c.moveToFirst()) {
            do {
                Usuarios u = new Usuarios(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
                usuarios.add(u);
            } while(c.moveToNext());
        }

        //Se elimina la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");

        //Se crea la nueva versión
        db.execSQL("CREATE TABLE Usuarios (id INTEGER, nick VARCHAR(50), nombre VARCHAR(50), apellidos VARCHAR(100), email VARCHAR(30), fecha_alta DATE)");

        //Insertar los datos recuperados
        for(Usuarios u : usuarios){
            db.execSQL("INSERT INTO Usuarios (id, nick, nombre, apellidos, email, fecha_alta) " +
                    "VALUES (" + u.getId() + ", '" + u.getNick() + "', '" + u.getNombre() + "', '" + u.getApellidos() + "', '" + u.getEmail() + "')");
        }
    }
}
