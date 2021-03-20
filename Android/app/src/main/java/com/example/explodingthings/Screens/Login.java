package com.example.explodingthings.Screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
        api.loginRequest(user,pass,GameList.class);
    }

    private void requestRegister(){
        api.registerRequest();
    }

}
