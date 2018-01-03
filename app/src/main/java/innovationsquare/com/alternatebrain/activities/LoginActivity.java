package innovationsquare.com.alternatebrain.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import innovationsquare.com.alternatebrain.models.Data;
import innovationsquare.com.alternatebrain.models.LoginResponse;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.URL;
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    TextView usernametv, passwordtv, serverwrongResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rightin, R.anim.rightout);
        setContentView(R.layout.activity_login);
        onAskPermission();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        usernametv = (TextView) findViewById(R.id.usernametv);
        passwordtv = (TextView) findViewById(R.id.passtv);
        serverwrongResponse = (TextView) findViewById(R.id.serverwrongResponse);
    }

    public void signInClicked(View view) {
        if(!validation()){
            return;
        }
        if(!Global.isNetworkAvailable(this)){
            Toast.makeText(this, "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        signIn();
    }

    public void signIn() {
        final ProgressDialog progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.LOG_IN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.v("qwe", response);
                Gson gson = new Gson();
                LoginResponse sucessResponse = gson.fromJson(response, LoginResponse.class);
                boolean error = sucessResponse.error;
                if(!error){
                    Data data = sucessResponse.data;
                    Prefs.putString(LoginActivity.this, Prefs.NAME, data.name);
                    Prefs.putString(LoginActivity.this, Prefs.API_KEY, data.apiKey);
                    Prefs.putString(LoginActivity.this, Prefs.USER_NAME, data.username);
                    Prefs.putString(LoginActivity.this, Prefs.CITY, data.city);
                    Prefs.putString(LoginActivity.this, Prefs.COUNTRY, data.country);
                    Prefs.putString(LoginActivity.this, Prefs.USER_ID, data.id.toString());
                    Prefs.putString(LoginActivity.this, Prefs.EMAIL, data.email);


                    if(data.referral != null) {
                        Prefs.putString(LoginActivity.this, Prefs.REFERRAL, data.referral);
                    }else {
                        Prefs.putString(LoginActivity.this, Prefs.REFERRAL, "");
                    }

                    if(data.phonenumber != null) {
                        Prefs.putString(LoginActivity.this, Prefs.PHONE, data.phonenumber);
                    }else {
                        Prefs.putString(LoginActivity.this, Prefs.PHONE, "");
                    }

                    if(data.sector != null) {
                        Prefs.putString(LoginActivity.this, Prefs.SECTOR, data.sector);
                    }else {
                        Prefs.putString(LoginActivity.this, Prefs.SECTOR, "");
                    }



                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    LoginActivity.this.finish();
                }else{
                    serverwrongResponse.setVisibility(View.VISIBLE);
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
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };;
        System.setProperty("http.keepAlive", "false");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str){

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

//        avi.hide();
//        avi.setVisibility(View.GONE);
//        errortext.setVisibility(View.VISIBLE);
//        errortext.setText(str);
//        reloadImg.setVisibility(View.VISIBLE);
    }

    public boolean validation() {
        boolean b = true;
        if (TextUtils.isEmpty(username.getText().toString())) {
            usernametv.setVisibility(View.VISIBLE);
            return false;
        } else {
            usernametv.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            passwordtv.setVisibility(View.VISIBLE);
            return false;
        } else {
            passwordtv.setVisibility(View.GONE);
        }
        return b;
    }

    public void forgotClicked(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    public void signUpTxtClicked(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void onAskPermission() {
        new PermissionWrapper.Builder(this).addPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION})
                //enable rationale message with a custom message
                .addPermissionRationale("")
                //show settings dialog,in this case with default message base on requested permission/s
                .addPermissionsGoSettings(false)
                //enable callback to know what option was choose
                .addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        //Toast.makeText(getApplicationContext(), "Permission was grant.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied(String permission) {
//                        Toast.makeText(getApplicationContext(), "Permission was not grant.", Toast.LENGTH_SHORT).show();
                    }
                }).build().request();
    }

}
