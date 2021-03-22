package com.example.explodingthings.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.explodingthings.R;

import com.example.explodingthings.APIConnection.APIConnection;

//Clase destinada a la actividad de Login
public class Login extends AppCompatActivity {

    private EditText mUserText;
    private EditText mPassword;
    private Button loginButton;
    private Button registerButton;
    private APIConnection api;

    private String id_user;

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
        id_user = user;
        api.loginRequest(user,pass,this);
    }

    private void requestRegister(){
        api.registerRequest();
    }

    public void checkLoggedAPI(boolean logged) {
        if (logged) {
            SharedPreferences userName = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userName.edit();
            editor.putString("user", id_user);
            editor.putBoolean("logged", true);
            editor.apply();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Usuario o contraseña" +
                    " mal introducidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLogged(){
        SharedPreferences userName = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        if (userName.getBoolean("logged",false)){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogged();
    }
    // Hacer clase Sharepreference

}
