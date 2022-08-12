package com.example.inicibasic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }

    public void incidencias(View view) {
        Intent intent=new Intent(this, MisIncidencias.class);
        startActivity(intent);
    }

    public void comisarias(View view) {
        Intent intent1=new Intent(this, Comisarias.class);
        startActivity(intent1);
    }

    public void usuarios(View view) {
        Intent intent2=new Intent(this, MiUsuario.class);
        startActivity(intent2);
    }
}