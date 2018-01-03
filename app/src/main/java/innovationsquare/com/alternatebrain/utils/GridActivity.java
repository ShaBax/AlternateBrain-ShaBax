package innovationsquare.com.alternatebrain.utils;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.models.ImageResponse;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter.Section;

public class GridActivity extends AppCompatActivity {


    private GridView grid;
    private ImageAdapter mAdapter;
    private ArrayList<Section> sections = new ArrayList<Section>();
    public View view;
    List<Image> images;
    List<Image> images1;
    DatePickerDialog datePickerDialog;
    ImageView sort, right_arrow, search;
    LinearLayout toplayout, searching_layout;
    EditText firstdate, seconddate;
    boolean des = false;

    private String[] mHeaderNames = {"Cute Cats", "Few Cats"};
    private Integer[] mHeaderPositions = {0, 5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        getImages();
        //dateFormatting();
        //initControls();
    }

    public void getImages() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                    mapImages(images);
//                    if (images.size() > 0) {
//                      //  toplayout.setVisibility(View.VISIBLE);
//
//                        grid = (GridView)findViewById(R.id.grid);
//                        mAdapter = new ImageAdapter(GridActivity.this, images);
//                        for (int i = 0; i < mHeaderPositions.length; i++) {
//                            sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
//                        }
//                        SimpleSectionedGridAdapter simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(GridActivity.this, mAdapter,
//                                R.layout.grid_item_header, R.id.header_layout, R.id.header);
//                        simpleSectionedGridAdapter.setGridView(grid);
//                        simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
//                        grid.setAdapter(simpleSectionedGridAdapter);
//
//                    } else {
//                        Toast.makeText(GridActivity.this, "No File Exists.", Toast.LENGTH_SHORT).show();
//                    }
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
                params.put("user_id", 46 + "");
                params.put("start_date", "");
                params.put("end_date", "");
                return params;
            }
        };
        ;
        requestQueue.add(stringRequest);
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

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            }

            image = ViewHolder.get(convertView, R.id.image);
            Glide.with(con)
                    .load(URL.IMAGE_BASE + item.attachment)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .into(image);

            //loadBitmap(mImageIds[position], image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(con, "clicked", Toast.LENGTH_SHORT).show();
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

    public void mapImages(List<Image> imges) {

        if (imges.size() > 0) {
            //  toplayout.setVisibility(View.VISIBLE);

            grid = (GridView) findViewById(R.id.grid);
            mAdapter = new ImageAdapter(GridActivity.this, imges);


            for (int i = 0; i < imges.size(); i++) {
                if (i == 0) {
                    sections.add(new Section(0, dateFormatting(imges.get(i).createdAt)));
                } else {
                    if (dateFormatting(imges.get(i).createdAt).equals(dateFormatting(imges.get(i-1).createdAt))) {
                    } else {
                        sections.add(new Section(i, dateFormatting(imges.get(i).createdAt)));
                    }
                }

            }


            SimpleSectionedGridAdapter simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(GridActivity.this, mAdapter,
                    R.layout.grid_item_header, R.id.header_layout, R.id.header);
            simpleSectionedGridAdapter.setGridView(grid);
            simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
            grid.setAdapter(simpleSectionedGridAdapter);

        } else {
            Toast.makeText(GridActivity.this, "No File Exists.", Toast.LENGTH_SHORT).show();
        }
    }
}



