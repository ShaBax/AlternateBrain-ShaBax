package innovationsquare.com.alternatebrain.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import innovationsquare.com.alternatebrain.activities.LoginActivity;
import innovationsquare.com.alternatebrain.models.LoginResponse;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.URL;

/**
 * Created by tariq on 10/20/2017.
 */

public class FragmentSetting extends Fragment {

    private Activity activity;
    EditText name, username, email, sector, city, country, phone, referral;
    TextView changePassword, unSubscribe;
    String usernameDummy;
    public View view;
    public RelativeLayout updateProfile;
    String eCode = "no error";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.update_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.emailEd);
        sector = view.findViewById(R.id.sectorEd);
        city = view.findViewById(R.id.cityEd);
        country = view.findViewById(R.id.countryEd);
        phone = view.findViewById(R.id.phoneEd);
        changePassword = view.findViewById(R.id.changePassword);
        unSubscribe = view.findViewById(R.id.unSubscribe);
        updateProfile = view.findViewById(R.id.updateProfile);
        referral = view.findViewById(R.id.referral);

        name.setText(Prefs.getString(getActivity(), Prefs.NAME));
        username.setText(Prefs.getString(getActivity(), Prefs.USER_NAME));
        email.setText(Prefs.getString(getActivity(), Prefs.EMAIL));
        sector.setText(Prefs.getString(getActivity(), Prefs.SECTOR));
        city.setText(Prefs.getString(getActivity(), Prefs.CITY));
        country.setText(Prefs.getString(getActivity(), Prefs.COUNTRY));
        phone.setText(Prefs.getString(getActivity(), Prefs.PHONE));
        referral.setText(Prefs.getString(getActivity(), Prefs.REFERRAL));

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog();
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Global.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateData();
            }
        });

        unSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performUnsubScribeOperation();
            }
        });

    }

    private void performUnsubScribeOperation() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(R.string.confirmation)
                .titleColorRes(R.color.navyBlue)
                .content(R.string.unsubscribe_message)
                .contentColorRes(R.color.darkTransparentBg)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.navyBlue)
                .negativeColorRes(R.color.navyBlue)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!Global.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getActivity(), "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        callUnSubScribeCall();
                    }
                });


        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void callUnSubScribeCall() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.UNSUBSCRIBE_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                boolean error = sucessResponse.error;
                Toast.makeText(activity, sucessResponse.message, Toast.LENGTH_SHORT).show();
                if (!error) {
                    Prefs.clearPrefernce(activity);
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.v("qwe", networkResponse.statusCode + "  " + error.toString());
                    eCode = networkResponse.statusCode + "  " + error.toString();
                }
                Log.v("qwe", error.toString());
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
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Prefs.getString(getActivity(), Prefs.USER_ID));
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        System.setProperty("http.keepAlive", "false");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void updateData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.UPDATE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.v("qwe", response);
                Gson gson = new Gson();
                LoginResponse sucessResponse = gson.fromJson(response, LoginResponse.class);
                boolean error = sucessResponse.error;
                if (!error) {
                    Toast.makeText(getActivity(), sucessResponse.message, Toast.LENGTH_SHORT).show();

                    Prefs.putString(getActivity(), Prefs.NAME, name.getText().toString());
                    Prefs.putString(getActivity(), Prefs.CITY, city.getText().toString());
                    Prefs.putString(getActivity(), Prefs.COUNTRY, country.getText().toString());
                    Prefs.putString(getActivity(), Prefs.PHONE, phone.getText().toString());
                    Prefs.putString(getActivity(), Prefs.SECTOR, sector.getText().toString());

                } else {
                    Toast.makeText(getActivity(), sucessResponse.message, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.v("qwe", networkResponse.statusCode + "  " + error.toString());
                    eCode = networkResponse.statusCode + "  " + error.toString();
                    //Toast.makeText(getActivity(), eCode + "  " + error.toString(), Toast.LENGTH_SHORT).show();
                }
                Log.v("qwe", error.toString());
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
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("username", username.getText().toString());
//                params.put("email", email.getText().toString());
                params.put("user_id", Prefs.getString(getActivity(), Prefs.USER_ID));
                params.put("referral", referral.getText().toString());
                params.put("name", name.getText().toString());
                params.put("sector", sector.getText().toString());
                params.put("city", city.getText().toString());
                params.put("country", country.getText().toString());
                params.put("phonenumber", phone.getText().toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        System.setProperty("http.keepAlive", "false");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public void addDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        RelativeLayout update = dialog.findViewById(R.id.updatePass);
        final EditText old = dialog.findViewById(R.id.oldPassEd);
        final EditText newPass = dialog.findViewById(R.id.newPass);
        final EditText conPass = dialog.findViewById(R.id.newPassConfirm);
        final TextView oldPasswordTv = dialog.findViewById(R.id.oldPasswordTv);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(newPass.getText().toString().equals(conPass.getText().toString()))) {
                    oldPasswordTv.setVisibility(View.VISIBLE);
                    return;
                }
                if (!Global.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                System.setProperty("http.keepAlive", "false");
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.CHANGE_PASSWORD, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.v("qwe", response);
                        Gson gson = new Gson();
                        LoginResponse sucessResponse = gson.fromJson(response, LoginResponse.class);
                        boolean error = sucessResponse.error;
                        if (!error) {
                            dialog.dismiss();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Password not changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.v("qwe", error + "");
                        if (error instanceof NoConnectionError) {
                            volleyErrorResponseMsg("Check your internet. No connection ");
                        } else if (error instanceof TimeoutError) {
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
                        params.put("username", Prefs.getString(getActivity(), Prefs.USER_NAME));
                        params.put("old_password", old.getText().toString());
                        params.put("new_password", newPass.getText().toString());
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        });
    }

}
