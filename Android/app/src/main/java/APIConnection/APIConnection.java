package APIConnection;

import android.content.Context;
import android.util.Log;

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

    public boolean loginRequest(String user, String pass){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        final boolean[] logged = {false};
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "?user=" + user + "&pass=" + pass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            logged[0] = object.getBoolean("result");
                            Log.d("Login", logged[0] + "");
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
        return logged[0];
    }

    public void registerRequest(){

    }
}
