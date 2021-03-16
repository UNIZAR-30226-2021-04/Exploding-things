package com.example.explodingthings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import APIConnection.APIConnection;

//Clase destinada a la actividad de Login
public class Login extends AppCompatActivity {

    private EditText mUserText;
    private EditText mPassword;
    private Button loginButton;
    private Button registerButton;
    private APIConnection api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        api = new APIConnection(this);
         mUserText = findViewById(R.id.usernameTextLogin);
         mPassword = findViewById(R.id.passwordTextLogin);
         loginButton = findViewById(R.id.loginButton);
         registerButton = findViewById(R.id.registerButton);
         //Aqui iria el boton de registro pero de momento queremos un login lo mas simple posible
        loginButton.setOnClickListener (view ->
                requestLogin()
        );
    }

    private void requestLogin(){
        String user = mUserText.getText().toString();
        String pass = mPassword.getText().toString();
        boolean logged = api.loginRequest(user,pass);
        if (logged) {
            Toast.makeText(this, "loggeado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no loggeado", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestRegister(){
        api.registerRequest();
    }
}
