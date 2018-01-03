package innovationsquare.com.alternatebrain.activities;

import android.Manifest;
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
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;

public class RegisterActivity extends AppCompatActivity {

    EditText name, username, password, confirmPassword, email, sector, city, country, phone, referral;
    TextView nametv, usernametv, passwordtv, conpasswordtv, emailtv, signin, serverwrongResponse, reftv, contry_name, city_name;
    String usernameDummy;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rightin, R.anim.rightout);
        setContentView(R.layout.activity_register);
        onAskPermission();

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPass);
        email = findViewById(R.id.emailEd);
        sector = findViewById(R.id.sectorEd);
        city = findViewById(R.id.cityEd);
        country = findViewById(R.id.countryEd);
        phone = findViewById(R.id.phoneEd);
        referral = findViewById(R.id.referral);
        serverwrongResponse = findViewById(R.id.serverwrongResponse);

        nametv = findViewById(R.id.nametv);
        usernametv = findViewById(R.id.usernametv);
        passwordtv = findViewById(R.id.passtv);
        conpasswordtv = findViewById(R.id.passcontv);
        reftv = findViewById(R.id.reftv);
        emailtv = findViewById(R.id.emailtv);
        signin = findViewById(R.id.signin);
        contry_name = findViewById(R.id.contry_name);
        city_name = findViewById(R.id.city_name);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    public void signUpClicked(View view) {
        offVisibility();
        if(!validation()){
            return;
        }
        if(!Global.isNetworkAvailable(this)){
            Toast.makeText(this, "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }
        putData();
        //testRegister();
    }

    public void putData() {
        final ProgressDialog progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.SIGN_UP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                String newResponse = response.substring(usernameDummy.length());
                Log.v("qwe", newResponse);
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(newResponse, SuccessResponse.class);
                boolean error = sucessResponse.error;
                //Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_SHORT).show();
                if (!error) {
                    Toast.makeText(RegisterActivity.this, sucessResponse.message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }else{
                    serverwrongResponse.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("qwe", error.toString());
                Toast.makeText(RegisterActivity.this, "fail", Toast.LENGTH_SHORT).show();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    volleyErrorResponseMsg("Check your internet connection");
                } else if (error instanceof AuthFailureError) {
                    volleyErrorResponseMsg("AuthFailureError");
                } else if (error instanceof ServerError) {
                    volleyErrorResponseMsg("ServerError");
                } else if (error instanceof NetworkError) {
                    volleyErrorResponseMsg("Check Internet");
                } else if (error instanceof ParseError) {
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
                usernameDummy = username.getText().toString();
                params.put("username", username.getText().toString());
                params.put("name", name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("sector", sector.getText().toString());
                params.put("city", city.getText().toString());
                params.put("country", country.getText().toString());
                params.put("phonenumber", phone.getText().toString());
                params.put("referral", referral.getText().toString());
                return params;
            }
        };

        System.setProperty("http.keepAlive", "false");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public boolean validation() {
        boolean b = true;
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            nametv.setVisibility(View.VISIBLE);
            return false;
        } else {
            nametv.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(username.getText().toString().trim())) {
            usernametv.setVisibility(View.VISIBLE);
            return false;
        } else {
            usernametv.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(password.getText().toString().trim())) {
            passwordtv.setVisibility(View.VISIBLE);
            return false;
        } else {
            passwordtv.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(confirmPassword.getText().toString().trim())) {
            conpasswordtv.setVisibility(View.VISIBLE);
            return false;
        } else {
            conpasswordtv.setVisibility(View.GONE);
        }

        if (password.getText().toString().trim().equals(confirmPassword.getText().toString())) {
            conpasswordtv.setVisibility(View.GONE);
        } else {
            conpasswordtv.setVisibility(View.VISIBLE);
            return false;
        }

        if (TextUtils.isEmpty(email.getText().toString().trim()) || !email.getText().toString().trim().matches(emailPattern)) {
            emailtv.setVisibility(View.VISIBLE);
            return false;
        } else {
            emailtv.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(referral.getText().toString())) {
            reftv.setVisibility(View.VISIBLE);
            return false;
        } else {
            reftv.setVisibility(View.GONE);
        }
/////////////////////////////////////////////////////////////////////////////
        if (TextUtils.isEmpty(city.getText().toString())) {
            city_name.setVisibility(View.VISIBLE);
            return false;
        } else {
            city_name.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(country.getText().toString())) {
            contry_name.setVisibility(View.VISIBLE);
            return false;
        } else {
            contry_name.setVisibility(View.GONE);
        }


        return b;
    }

    public void signInTxtClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void offVisibility(){
        serverwrongResponse.setVisibility(View.GONE);
        nametv.setVisibility(View.GONE);
        usernametv.setVisibility(View.GONE);
        passwordtv.setVisibility(View.GONE);
        conpasswordtv.setVisibility(View.GONE);
        emailtv.setVisibility(View.GONE);
        reftv.setVisibility(View.GONE);
        city_name.setVisibility(View.GONE);
        contry_name.setVisibility(View.GONE);
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
