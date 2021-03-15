package com.example.explodingthings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
//Clase destinada a la actividad de Login
public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
         EditText mUserText = (EditText) findViewById(R.id.editTextTextPersonName);
         EditText mPassword = (EditText) findViewById(R.id.editTextTextPassword3);
         Button loginButton = (Button)  findViewById(R.id.button5);
         //Aqui iria el boton de registro pero de momento queremos un login lo mas simple posible
        loginButton.setOnClickListener (new View. OnClickListener () {
            public void onClick ( View view ) {
                setResult ( RESULT_OK );
                finish ();
                }
            });
        }

    //Implementar funcionalidad de login al pulsar el boton de login comprobar cuadros de texto etc
    private void PopulateFields(){
        //Metodo que lea de los campos de texto y pida a nuestro servlet maestro que compruebe los datos
    }
    //metodos OnResume...

}
