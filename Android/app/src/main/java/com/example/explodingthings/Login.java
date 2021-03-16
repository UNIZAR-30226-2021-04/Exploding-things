package com.example.explodingthings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Clase destinada a la actividad de Login
public class Login extends AppCompatActivity {

    private EditText mUserText;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
         mUserText = findViewById(R.id.editTextTextPersonName);
         mPassword = findViewById(R.id.editTextTextPassword3);
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
        String user = mUserText.getText().toString();
        String pass = mPassword.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://196.1.1.1/myurl?user=" + user + "&user=" + pass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            JSONArray array=object.getJSONArray("users");
                            for(int i=0;i<array.length();i++) {
                                JSONObject object1=array.getJSONObject(i);
                                //textView.setText(object1.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }
    //metodos OnResume...

}
