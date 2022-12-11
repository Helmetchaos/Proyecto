package com.example.inicibasic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MisIncidencias extends AppCompatActivity {

    private EditText editTitulo, editDesc;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_incidencias);

        db = FirebaseFirestore.getInstance();
        editTitulo=(EditText) findViewById(R.id.edTitulo);
        editDesc=(EditText) findViewById(R.id.edDescripcion);

    }

    public void registrar(View view) {
         String titulo=editTitulo.getText().toString();
         String descripcion=editDesc.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put(titulo, descripcion);

        db.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MisIncidencias.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MisIncidencias.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void consultar(View view) {

        String titulo=editTitulo.getText().toString();
        String descripcion=editDesc.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put(titulo, descripcion);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                editTitulo.setText(document.getId().toString());
                                editDesc.setText(document.getData().toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void eliminar(View view) {
    }
}