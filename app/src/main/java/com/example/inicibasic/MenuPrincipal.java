package com.example.inicibasic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuPrincipal extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
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

    public void logOut(View view) {
        mAuth.signOut();
        Intent intent3=new Intent(this, MainActivity.class);
        startActivity(intent3);
    }
}