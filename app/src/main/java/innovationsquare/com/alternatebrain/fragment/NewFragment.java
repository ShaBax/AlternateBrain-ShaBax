package innovationsquare.com.alternatebrain.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.activities.SingleImageActivity;
import innovationsquare.com.alternatebrain.adapters.HomeItemAdapter;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.models.ImageResponse;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.models.events.EventManager;
import innovationsquare.com.alternatebrain.models.events.UpdateDataEvent;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.URL;

/**
 * Created by tariq on 11/14/2017.
 */

public class NewFragment extends Fragment {

    private Activity activity;
    private GridView grid;
    private ImageAdapter mAdapter;
    private HomeItemAdapter homeItemAdapter;
    private ArrayList<SimpleSectionedGridAdapter.Section> sections = new ArrayList<SimpleSectionedGridAdapter.Section>();
    public View view;
    List<Image> images;
    DatePickerDialog datePickerDialog;
    ImageView sort, right_arrow, search;
    LinearLayout toplayout, searching_layout;
    EditText firstdate, seconddate;
    boolean des = false;
    SimpleSectionedGridAdapter simpleSectionedGridAdapter;

    private EditText nameFilter;

    private View lVcontainer, selectionMenu, selectAllBtn, deselectAllBtn, deleteBtn;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_grid, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getImages(false);
        EventManager.bus.register(this);
        sort = view.findViewById(R.id.sort);
        toplayout = view.findViewById(R.id.toplayout);
        searching_layout = view.findViewById(R.id.searching_layout);
        search = view.findViewById(R.id.search);
        grid = (GridView) getView().findViewById(R.id.grid);

        lVcontainer = getView().findViewById(R.id.containerLV);
        listView = (ListView) getView().findViewById(R.id.listView);
        selectionMenu = getView().findViewById(R.id.selectionMenu);
        selectAllBtn = getView().findViewById(R.id.selectAllBtn);
        deselectAllBtn = getView().findViewById(R.id.deselectAllBtn);
        deleteBtn = getView().findViewById(R.id.deleteBtn);

        firstdate = view.findViewById(R.id.firstdate);
        seconddate = view.findViewById(R.id.seconddate);
        right_arrow = view.findViewById(R.id.right_arrow);
        nameFilter = (EditText) view.findViewById(R.id.nameFilter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searching_layout.getVisibility() == View.VISIBLE) {
                    searching_layout.setVisibility(View.GONE);
                    des = false;
                } else {
                    searching_layout.setVisibility(View.VISIBLE);
                    des = true;
                }
            }
        });
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images != null && images.size() > 0) {
                    sortListViewItesm();
                }
            }
        });
        right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()) {
                    return;
                }
                searching_layout.setVisibility(View.GONE);
                des = false;
                if (homeItemAdapter != null) {
                    homeItemAdapter.clearAllItems();
                }
                lVcontainer.setVisibility(View.GONE);
                selectionMenu.setVisibility(View.GONE);
                getImages(true);
            }
        });

        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeItemAdapter != null) {
                    homeItemAdapter.selectAllByValue(true);
                }
            }
        });

        deselectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeItemAdapter != null) {
                    homeItemAdapter.selectAllByValue(false);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performDeleteOperation();
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

    private void performDeleteOperation() {
        if (homeItemAdapter != null) {
            final String ids = homeItemAdapter.getSelectedId();
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.BASE_URL + URL.DELETE_IMAGES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.v("qwe", response);
                    Gson gson = new Gson();
                    SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                    boolean error = sucessResponse.error;
                    ResponseMsg(sucessResponse.message);
                    if (!error) {
                        getImages(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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
                    params.put("user_id", Prefs.getString(activity, Prefs.USER_ID));
                    params.put("image_ids", ids);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }


    public void getImages(final boolean isFiltered) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
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
                    if (!isFiltered) {
                        mapImages(images);
                    } else {
                        mapSearchImages(images);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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
                params.put("user_id", Prefs.getString(activity, Prefs.USER_ID));
                params.put("start_date", firstdate.getText().toString());
                params.put("end_date", seconddate.getText().toString());
                if (isFiltered) {
                    params.put("place_name", nameFilter.getText().toString());
                }
                return params;
            }
        };
        ;
        requestQueue.add(stringRequest);
    }

    private void sort() {
        Collections.reverse(images);
        sections.clear();
        grid.setAdapter(null);
        simpleSectionedGridAdapter = null;
        mAdapter = new ImageAdapter(activity, images);

        for (int i = 0; i < images.size(); i++) {
            if (i == 0) {
                sections.add(new SimpleSectionedGridAdapter.Section(0, dateFormatting(images.get(i).createdAt)));
            } else {
                if (dateFormatting(images.get(i).createdAt).equals(dateFormatting(images.get(i - 1).createdAt))) {
                } else {
                    sections.add(new SimpleSectionedGridAdapter.Section(i, dateFormatting(images.get(i).createdAt)));
                }
            }
        }

        simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(activity, mAdapter, R.layout.grid_item_header, R.id.header_layout, R.id.header);
        simpleSectionedGridAdapter.setGridView(grid);
        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
        grid.setAdapter(simpleSectionedGridAdapter);
    }

    private void sortListViewItesm() {
        Collections.reverse(images);
        homeItemAdapter = new HomeItemAdapter(activity, images);
        listView.setAdapter(homeItemAdapter);
    }

    public void showDatePiker(final EditText editText) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        // date picker dialog
        datePickerDialog = new DatePickerDialog(activity, R.style.DialogTheme,
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
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        }, 100);
    }

    public boolean validation() {
        boolean b = true;

//        if (firstdate.getText().toString().equals(seconddate.getText().toString())) {
//            Toast.makeText(activity, "Please select a valid date.", Toast.LENGTH_LONG).show();
//            return false;
//        }

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
                Toast.makeText(activity, "Please select a valid date.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }


        if (firstdate.getText().toString().equals("")) {
            Toast.makeText(activity, "Please enter from date.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                date = fmt.parse(firstdate.getText().toString());
                fmt.format(date);
                int diff1 = new Date().compareTo(date);
                if (diff1 < 0) {
                    Toast.makeText(activity, "Please select a valid date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException pe) {
            }
        }

        if (seconddate.getText().toString().equals("")) {
            Toast.makeText(activity, "Please enter to date.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                date1 = fmt.parse(seconddate.getText().toString());
                fmt.format(date1);
                int diff2 = new Date().compareTo(date1);
                if (diff2 < 0) {
                    Toast.makeText(activity, "Please select a valid date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException pe) {
            }
        }


        return true;
    }

    private class ImageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        Context con;
        public List<Image> imageList;

        public ImageAdapter(Context context, List<Image> images2) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageList = images2;
            con = context;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image;
            Image item = imageList.get(position);
            final Image myItem = item;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            }

            image = NewFragment.ViewHolder.get(convertView, R.id.image);

            Glide.with(con)
                    .load(URL.IMAGE_BASE + item.attachment)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .into(image);

            //loadBitmap(mImageIds[position], image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(con, SingleImageActivity.class);
                    intent.putExtra("imageKey", myItem.attachment);
                    con.startActivity(intent);
                }
            });

            return convertView;
        }

    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }

    }

    public String dateFormatting(String mDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String now = dateFormat.format(myDate);

        Log.v("mDate", now);
        return now;
    }

    private void mapSearchImages(List<Image> imges) {
        if (imges.size() > 0) {
            grid.setVisibility(View.GONE);
            lVcontainer.setVisibility(View.VISIBLE);
            selectionMenu.setVisibility(View.VISIBLE);

            homeItemAdapter = new HomeItemAdapter(activity, images);
            listView.setAdapter(homeItemAdapter);

        }
    }

    public void mapImages(List<Image> imges) {

        if (imges.size() > 0) {
            //  toplayout.setVisibility(View.VISIBLE);

            sections.clear();
            grid.setAdapter(null);
            simpleSectionedGridAdapter = null;

            mAdapter = new ImageAdapter(activity, imges);


            for (int i = 0; i < imges.size(); i++) {
                if (i == 0) {
                    sections.add(new SimpleSectionedGridAdapter.Section(0, dateFormatting(imges.get(i).createdAt)));
                } else {
                    if (dateFormatting(imges.get(i).createdAt).equals(dateFormatting(imges.get(i - 1).createdAt))) {
                    } else {
                        sections.add(new SimpleSectionedGridAdapter.Section(i, dateFormatting(imges.get(i).createdAt)));
                    }
                }
            }

            simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(activity, mAdapter,
                    R.layout.grid_item_header, R.id.header_layout, R.id.header);
            simpleSectionedGridAdapter.setGridView(grid);
            simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
            grid.setAdapter(simpleSectionedGridAdapter);

        } else {
            Toast.makeText(activity, "No File Exists.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ResponseMsg(String str) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onUpdateData(UpdateDataEvent event) {
        getImages(true);
    }
}