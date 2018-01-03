package innovationsquare.com.alternatebrain.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.models.events.EventManager;
import innovationsquare.com.alternatebrain.models.events.UpdateDataEvent;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.GsonFactory;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.TouchImageView;
import innovationsquare.com.alternatebrain.utils.URL;

public class SingleImageActivity extends AppCompatActivity {

    private TouchImageView imgShown;
    private View upload;
    private String bill_date, location, placeName, amount, remarks;
    private Image imageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_single_image);
        String jsonData = getIntent().getExtras().getString("imageStr", "");
        imageItem = GsonFactory.getInstance().getGson().fromJson(jsonData, Image.class);

        if (imageItem == null) {
            finish();
        }
        ImageButton closeBtn = (ImageButton) findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDeleteBtnOperation();
            }
        });

        upload = findViewById(R.id.upload);
        imgShown = (TouchImageView) findViewById(R.id.imgShown);
        if (imageItem.attachment.length() > 0) {

            final ProgressDialog progressDialog = new ProgressDialog(SingleImageActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            Glide.with(this)
                    .load(URL.IMAGE_BASE + imageItem.attachment)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            volleyErrorResponseMsg("Image downloading faild");
                            progressDialog.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressDialog.dismiss();
                            return false;
                        }
                    })
                    .into(imgShown);
        }
        imgShown.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {

            @Override
            public void onMove() {
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataDialog();
            }
        });
    }

    private void performDeleteBtnOperation() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(SingleImageActivity.this)
                .title(R.string.confirmation)
                .titleColorRes(R.color.navyBlue)
                .content(R.string.delete_message)
                .contentColorRes(R.color.darkTransparentBg)
                .positiveText(R.string.yes)
                .positiveColorRes(R.color.navyBlue)
                .negativeColorRes(R.color.navyBlue)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!Global.isNetworkAvailable(SingleImageActivity.this)) {
                            Toast.makeText(SingleImageActivity.this, "Please Check your internet connection.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        performDeleteOperation();
                    }
                });


        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    public void updateDataDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_file_dialog);
        dialog.show();
        final TextView dateEtx = dialog.findViewById(R.id.dateEtx);
        final EditText nameEdt = dialog.findViewById(R.id.nameEdt);
        final EditText locatonTxt = dialog.findViewById(R.id.locatonTxt);
        final EditText amountTxt = dialog.findViewById(R.id.amountTxt);
        final EditText remarksTxt = dialog.findViewById(R.id.remarksTxt);
        RelativeLayout uploadNow = dialog.findViewById(R.id.uploadNow);
        TextView uploadTxt = dialog.findViewById(R.id.uploadTxt);
        uploadTxt.setText("Update Now");

        dateEtx.setText(imageItem.bill_date);
        nameEdt.setText(imageItem.place_name);
        locatonTxt.setText(imageItem.placeLocation);
        amountTxt.setText(imageItem.amount);
        remarksTxt.setText(imageItem.remark);

        dateEtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                final DatePickerDialog datePickerDialog = new DatePickerDialog(SingleImageActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateEtx.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }

                        }, mYear, mMonth, mDay);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                }, 100);
            }
        });

        uploadNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateEtx.getText().toString() == null || dateEtx.getText().toString().equalsIgnoreCase("")) {
                    volleyErrorResponseMsg("Must enter date on picture");
                    return;
                }

                bill_date = dateEtx.getText().toString();
                placeName = nameEdt.getText().toString();
                location = locatonTxt.getText().toString();
                amount = amountTxt.getText().toString();
                remarks = remarksTxt.getText().toString();
                dialog.dismiss();
                uploadImage();

            }
        });
    }

    private void resetLocalFields() {
        bill_date = "";
        placeName = "";
        location = "";
        amount = "";
        remarks = "";
    }

    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(SingleImageActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(SingleImageActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.UPDATE_RECORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                resetLocalFields();
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                boolean error = sucessResponse.error;
                Toast.makeText(SingleImageActivity.this, sucessResponse.message, Toast.LENGTH_SHORT).show();
                if (!error) {
                    EventManager.bus.post(new UpdateDataEvent());
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                resetLocalFields();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.v("qwe", networkResponse.statusCode + "  " + error.toString());
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
                headers.put("Content-Type", Global.CONTENT_TYPE);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Prefs.getString(SingleImageActivity.this, Prefs.USER_ID));
                params.put("image_id", imageItem.id + "");

                params.put("place_name", placeName);
                params.put("bill_date", bill_date);
                params.put("location", location);
                params.put("amount", amount);
                params.put("remark", remarks);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {
        Toast.makeText(SingleImageActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void performDeleteOperation() {
        if (imageItem != null) {
            final ProgressDialog progressDialog = new ProgressDialog(SingleImageActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            RequestQueue requestQueue = Volley.newRequestQueue(SingleImageActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.DELETE_IMAGES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.v("qwe", response);
                    Gson gson = new Gson();
                    SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                    boolean error = sucessResponse.error;
                    volleyErrorResponseMsg(sucessResponse.message);
                    if (!error) {
                        EventManager.bus.post(new UpdateDataEvent());
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    error.printStackTrace();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.v("qwe", networkResponse.statusCode + "  " + error.toString());
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
                    params.put("user_id", Prefs.getString(SingleImageActivity.this, Prefs.USER_ID));
                    params.put("image_ids", imageItem.id +"");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

}
