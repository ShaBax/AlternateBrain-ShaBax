package innovationsquare.com.alternatebrain.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.adapters.GridViewAdapter;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.models.ImageItem;
import innovationsquare.com.alternatebrain.models.ImageResponse;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.URL;

/**
 * Created by tariq on 10/20/2017.
 */

public class FragmentArchives extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    public View view;
    List<Image> images;
    DatePickerDialog datePickerDialog;
    ImageView sort, right_arrow, search;
    LinearLayout toplayout, searching_layout;
    EditText firstdate, seconddate;
    boolean des = false;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_archies, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sort = view.findViewById(R.id.sort);
        gridView = view.findViewById(R.id.gridView);
        toplayout = view.findViewById(R.id.toplayout);
        searching_layout = view.findViewById(R.id.searching_layout);
        search = view.findViewById(R.id.search);

        firstdate = view.findViewById(R.id.firstdate);
        seconddate = view.findViewById(R.id.seconddate);
        right_arrow = view.findViewById(R.id.right_arrow);

///////////////////////////////////////////////////////////////////////////////////////////////////////

        String permission = Manifest.permission.ACCESS_NETWORK_STATE;
        int grant = ContextCompat.checkSelfPermission(getActivity(), permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(getActivity(), permission_list, 1);
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////

        getImages();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(images != null && images.size() > 0) {
                    if (searching_layout.getVisibility() == View.VISIBLE) {
                        searching_layout.setVisibility(View.GONE);
                        des = false;
                    } else {
                        searching_layout.setVisibility(View.VISIBLE);
                        des = true;
                    }
                }
            }
        });
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(images != null && images.size() > 0) {
                    sort();
                }
            }
        });
        right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()) {
                    return;
                }
                getImages();
            }
        });

        firstdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePiker(firstdate);
            }
        });
        seconddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePiker(seconddate);
            }
        });
    }

    public void getImages() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.ALL_IMAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.v("qwe", response);
                Gson gson = new Gson();
                ImageResponse sucessResponse = gson.fromJson(response, ImageResponse.class);
                boolean error = sucessResponse.error;
                if (!error) {
                    images = sucessResponse.images;
                    if (images.size() > 0) {
                        toplayout.setVisibility(View.VISIBLE);
                        gridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, images);
                        gridView.setAdapter(gridAdapter);
                    } else {
                        Toast.makeText(getActivity(), "No File Exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("qwe", error+"");
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
                Log.v("idd", Prefs.getString(getActivity(), Prefs.USER_ID));
                params.put("user_id", Prefs.getString(getActivity(), Prefs.USER_ID));
                params.put("start_date", firstdate.getText().toString());
                params.put("end_date", seconddate.getText().toString());
                return params;
            }
        };
        ;
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {

        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

//        avi.hide();
//        avi.setVisibility(View.GONE);
//        errortext.setVisibility(View.VISIBLE);
//        errortext.setText(str);
//        reloadImg.setVisibility(View.VISIBLE);
    }

    private void sort() {
        Collections.reverse(images);
        gridAdapter.notifyDataSetChanged();
    }

    public void showDatePiker(final EditText editText) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        // date picker dialog
        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        editText.setText(year + "-"
                                + (monthOfYear + 1) + "-" + dayOfMonth);

                    }

                }, mYear, mMonth, mDay);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                datePickerDialog.show();
            }
        }, 100);
    }

    public boolean validation() {
        boolean b = true;

        if (firstdate.getText().toString().equals(seconddate.getText().toString())) {
            Toast.makeText(getActivity(), "Please select a valid date.", Toast.LENGTH_LONG).show();
            return false;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date date1 = null;


        if (!firstdate.getText().toString().equals("") && !seconddate.getText().toString().equals("")) {

            try {
                date1 = fmt.parse(seconddate.getText().toString());
                date = fmt.parse(firstdate.getText().toString());
                fmt.format(date);
                fmt.format(date1);
            } catch (ParseException pe) {
            }

            int diff1 = new Date().compareTo(date);
            int diff2 = new Date().compareTo(date1);
            int dd = date1.compareTo(date);
            if (diff1 < 0 || diff2 < 0 || dd < 0) {
                Toast.makeText(getActivity(), "Please select a valid date.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }


        if (firstdate.getText().toString().equals("")) {

        } else {
            try {
                date = fmt.parse(firstdate.getText().toString());
                fmt.format(date);
                int diff1 = new Date().compareTo(date);
                if (diff1 < 0) {
                    Toast.makeText(getActivity(), "Please select a valid date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException pe) {
            }
        }

        if (seconddate.getText().toString().equals("")) {

        } else {
            try {
                date1 = fmt.parse(seconddate.getText().toString());
                fmt.format(date1);
                int diff2 = new Date().compareTo(date1);
                if (diff2 < 0) {
                    Toast.makeText(getActivity(), "Please select a valid date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException pe) {
            }
        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"permission granted", Toast.LENGTH_SHORT).show();
                // perform your action here

            } else {
                Toast.makeText(getActivity(),"permission not granted", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
