package innovationsquare.com.alternatebrain.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.URL;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText username;
    TextView usernametv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = (EditText) findViewById(R.id.username);
        usernametv = (TextView) findViewById(R.id.usernametv);
    }

    public void forgot() {
        final ProgressDialog progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL+ URL.FORGOT_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.v("qwe", response);
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                boolean error = sucessResponse.error;
                if(!error){
                    finish();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, "This email does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    volleyErrorResponseMsg("Check your internet connection");
                }else if(error instanceof AuthFailureError) {
                    volleyErrorResponseMsg("AuthFailureError");
                }else if(error instanceof ServerError) {
                    volleyErrorResponseMsg("ServerError");
                }else if(error instanceof NetworkError) {
                    volleyErrorResponseMsg("Check Internet");
                }else if(error instanceof ParseError) {
                    volleyErrorResponseMsg("ParseError");
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", Global.CONTENT_TYPE);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", username.getText().toString());
                return params;
            }
        };
        System.setProperty("http.keepAlive", "false");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public boolean validation() {
        boolean b = true;

        if (TextUtils.isEmpty(username.getText().toString())) {
            usernametv.setVisibility(View.VISIBLE);
            return false;
        } else {
            usernametv.setVisibility(View.GONE);
        }
        return b;
    }

    public void btnClicked(View view) {
        if (!validation()) {
            return;
        }

        if (!Global.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        forgot();
    }

    public void forgotSignINTxtClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
