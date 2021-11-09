package com.example.conversormonedainternet;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;





public class AdminBBDD extends SQLiteOpenHelper {

    public AdminBBDD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //creo tabla monedas  con campos id, currency y ratio
        db.execSQL("create table monedas(id int primary key, currency text, ratio text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        //se ejecuta cuando se actualiza la version
        db.execSQL("drop table monedas");
        onCreate(db);
    }
}
