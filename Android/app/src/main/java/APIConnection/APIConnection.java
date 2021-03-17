package APIConnection;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class APIConnection {

    private Context context;
    private final String Url = "http://rocketruckus.westeurope.azurecontainer.io:8080/LoginUser";

    public APIConnection(Context context){
        this.context = context;
    }

    public void loginRequest(String user, String pass, Class<?> cls){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "?user=" + user + "&pass=" + pass,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        boolean logged = object.getBoolean("result");
                        Log.d("Login", logged + "");
                        if (logged) {
                            Intent intent = new Intent(this.context, cls);
                            this.context.startActivity(intent);
                        } else {
                            Toast.makeText(this.context, "Usuario o contraseña" +
                                    "mal introducidos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void registerRequest(){

    }
}
