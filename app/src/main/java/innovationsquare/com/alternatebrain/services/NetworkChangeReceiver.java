package innovationsquare.com.alternatebrain.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.utils.FileUtil;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.URL;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tariq on 11/7/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    String mImage = "";
    boolean des = true;
    public Context con;
    private File actualImage, compressedImage;
    Bitmap myBitmap;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        con = context;
        //Toast.makeText(context, "testing", Toast.LENGTH_SHORT).show();
        Log.v("qwe", "BroadCast registed");
        if (isOnline(con)) {
            // Do something

            if (Prefs.getString(con, Prefs.UP_IMAGE) != null) {
                //Toast.makeText(context, "testing", Toast.LENGTH_SHORT).show();
                Log.v("qwe", Prefs.getString(con, Prefs.BACK_FILE_NAME) + "  qqqq" + Prefs.getString(con, Prefs.UP_IMAGE));
                if (!(Prefs.getString(con, Prefs.UP_IMAGE).equals(""))) {
                    //Toast.makeText(context, "load", Toast.LENGTH_SHORT).show();
                    try {
                        customCompressImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(context, "Available", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public void uploadImage(final String imggg) {

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.UPLOAD_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                des = true;
                Log.v("qwe", response);
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                boolean error = sucessResponse.error;
                Toast.makeText(con, "sucess", Toast.LENGTH_SHORT).show();
                if (!error) {
                    Prefs.putString(con, Prefs.UP_IMAGE, "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                des = true;
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
                params.put("image_name", Prefs.getString(con, Prefs.BACK_FILE_NAME));
                params.put("image", imggg);
                params.put("user_id", Prefs.getString(con, Prefs.USER_ID));
                params.put("lat", Prefs.getString(con, Prefs.MY_LATITUDE_FOR_UPLOAD));
                params.put("long", Prefs.getString(con, Prefs.MY_LONGITUDE_FORUPLOAD));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {
        //Toast.makeText(con, str, Toast.LENGTH_SHORT).show();
    }

    public void imageToString(Bitmap btmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        btmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        String sds = Base64.encodeToString(imgBytes, Base64.DEFAULT);
        uploadImage(sds);
    }


    ///////////////////////////////////////////////////////compreesion////////////////////////////////////////


    public void customCompressImage() throws IOException {
        actualImage = FileUtil.from(con, Uri.parse(Prefs.getString(con, Prefs.UP_IMAGE)));
        if (actualImage == null) {
            Toast.makeText(con, "Please choose an image!", Toast.LENGTH_SHORT).show();
        } else {

            new Compressor(con)
                    .setMaxWidth(480)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            compressedImage = file;
                            setCompressedImage();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(con, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setCompressedImage() {
        Bitmap btmap = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        imageToString(btmap);
        //uploadImage(sds);
        //Log.v("size", "  " + getReadableFileSize(compressedImage.length()) + "");
    }

}