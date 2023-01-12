package com.example.inicibasic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edEmail, edContra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inisesion);

        mAuth=FirebaseAuth.getInstance();
        edEmail=(EditText) findViewById(R.id.et_email);
        edContra=(EditText) findViewById(R.id.et_pass);

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

    public void iniSesion(View view) {

        mAuth.signInWithEmailAndPassword(edEmail.getText().toString(), edContra.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                }else{
                    task.getException();
                    Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void registrarse(View view) {

        mAuth.createUserWithEmailAndPassword(edEmail.getText().toString(), edContra.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    //task.getException();
                    Toast.makeText(MainActivity.this, "Fallo al registrarse, la contraseña tiene que ser de 6 o más digitos", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}