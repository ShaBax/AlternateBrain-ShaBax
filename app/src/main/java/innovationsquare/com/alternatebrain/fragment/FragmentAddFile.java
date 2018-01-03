package innovationsquare.com.alternatebrain.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;
import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.activities.HomeActivity;
import innovationsquare.com.alternatebrain.models.SuccessResponse;
import innovationsquare.com.alternatebrain.utils.FileUtil;
import innovationsquare.com.alternatebrain.utils.Global;
import innovationsquare.com.alternatebrain.utils.OnPermssionCallBackListener;
import innovationsquare.com.alternatebrain.utils.Prefs;
import innovationsquare.com.alternatebrain.utils.ProviderUtil;
import innovationsquare.com.alternatebrain.utils.RuntimeUtil;
import innovationsquare.com.alternatebrain.utils.TouchImageView;
import innovationsquare.com.alternatebrain.utils.URL;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tariq on 10/20/2017.
 */

public class FragmentAddFile extends Fragment {
    String TAG = "FragmentAddFile";
    public View view;
    private File actualImage, compressedImage;
    private String pictureImagePath = "";
    public int isCap = 0;
    double fileSize = 0;
    private TouchImageView imgShown;
    private DecimalFormat df;
    public RelativeLayout uploadBtn;
    String fileName = "";
    String mImage = "";
    Bitmap myBitmap;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int PICK_IMAGE_REQUEST = 3;
    private Uri outputFileUri;

    ProgressDialog progressDialogProcessingPic;
    private Activity activity;
    private Point size;
    private String bill_date = "", location = "", placeName = "", amount ="", remarks ="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        defineDisplaySize();
        progressDialogProcessingPic= new ProgressDialog(getActivity());
        progressDialogProcessingPic.setMessage("Processing Picture...");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_addfile, container, false);

        return view;
    }

    protected void defineDisplaySize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //openBackCamera();
        ImageView gallery = view.findViewById(R.id.gallery);
        ImageView camera = view.findViewById(R.id.camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                outputFileUri = ProviderUtil.getOutputMediaFileUri(getActivity());
                i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCameraCode();
            }
        });

        uploadBtn = view.findViewById(R.id.imageUploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Global.isNetworkAvailable(getActivity())) {
                    if (Prefs.getString(getActivity(), Prefs.UP_IMAGE).equals("")) {
                        Toast.makeText(getActivity(), "Your image will upload when internet is available.", Toast.LENGTH_SHORT).show();
                        Prefs.putString(getActivity(), Prefs.MY_LATITUDE_FOR_UPLOAD, Prefs.getString(getActivity(), Prefs.MY_LATITUDE));
                        Prefs.putString(getActivity(), Prefs.MY_LONGITUDE_FORUPLOAD, Prefs.getString(getActivity(), Prefs.MY_LONGITUDE));
                        //saveImageToExternalStorage(myBitmap);
                        getImageDateForOffLine();
                    } else {
                        Toast.makeText(getActivity(), "You already have an image pending for upload.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
//                uploadImage();
                addUploadFileDialog();

            }
        });

        df = new DecimalFormat("#.##");
        imgShown = view.findViewById(R.id.imgShown);
        imgShown.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "onActivityResult");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            progressDialogProcessingPic.show();
            progressDialogProcessingPic.setCancelable(false);
            Log.v(TAG, "onActivityResult coming from Camera");

            //outputFileUri=data.getData();
            if(outputFileUri!=null)
            {
                galleryAddPic(outputFileUri.getPath());
                try {
                    fileName = getFileName(outputFileUri);
                    Log.v(TAG, "File name: "+fileName.toString());

            /*        File path = new File(outputFileUri.getPath());
                    if (!path.exists()) path.mkdirs();
                    Log.v(TAG, "path: "+path.getAbsolutePath());
                    File imageFile = new File(path.getAbsolutePath());*/

                    actualImage = FileUtil.from(getActivity(), outputFileUri);
                    //actualImage = imageFile;
                    Log.v(TAG, "Actual image: "+actualImage.getAbsolutePath());
                    customCompressImage();
                    //setCompressedImage();
                } catch (Exception e){
                    Log.v(TAG, "Exception: "+e.toString());
                    e.printStackTrace();
                }
            }
            else
            {
                Log.v(TAG, "Uri null: ");
            }

        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            progressDialogProcessingPic.show();
            progressDialogProcessingPic.setCancelable(false);
            Log.v(TAG, "onActivityResult coming from Gallery");

            outputFileUri = data.getData();
            if (outputFileUri != null) {
                try {
                    fileName = getFileName(outputFileUri);
                    actualImage = FileUtil.from(getActivity(), outputFileUri);
                    customCompressImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        else {
            Log.v(TAG, "onActivityResult coming from Nowhere");
        }

        ///

    }


    public void newCameraCode() {
        RuntimeUtil.checkPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, RuntimeUtil.PERMISSION_ALBUM, new OnPermssionCallBackListener() {
            @Override
            public void OnGrantPermission() {
                RuntimeUtil.checkPermission(getActivity(), getActivity().getWindow().getDecorView(), Manifest.permission.CAMERA, RuntimeUtil.PERMISSION_CAMERA, null, new OnPermssionCallBackListener() {
                    @Override
                    public void OnGrantPermission() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            outputFileUri = ProviderUtil.getOutputMediaFileUri(getActivity());
                            Log.v(TAG, "outputFileUri: "+outputFileUri.getPath());
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                        }
                        else
                        {
                            Log.v(TAG, "Onlick Camera uri: "+outputFileUri.getPath());
                        }
                    }
                });
            }
        });

    }
    public void customCompressImage() {
        if (actualImage == null) {
            Toast.makeText(getActivity(), "Please choose an image!", Toast.LENGTH_SHORT).show();
        } else {
            Log.v(TAG, "customCompressImage()");
            Log.v(TAG,"checkinenv: "+Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath());
            new Compressor(getActivity())
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
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
                            Log.v(TAG, "Error: "+throwable.getMessage());
                            throwable.printStackTrace();
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setCompressedImage() {

        Log.d(TAG,"setCompressedImage()");
        Log.d(TAG,"compressedImage: "+compressedImage.getAbsolutePath());

        Bitmap btmap = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());

        // Scalling imGW (RESIZE)

        int maxSize=500;
        int widthImage=btmap.getHeight();
        int heightImage=btmap.getWidth();
        Log.d(TAG,"widthImage: "+widthImage);
        Log.d(TAG,"heightImage: "+heightImage);

        if(widthImage>=heightImage)
        {
            widthImage=maxSize;
            heightImage=(heightImage*maxSize)/widthImage;
        }
        else
        {
            heightImage=maxSize;
            widthImage=(widthImage*maxSize)/heightImage;
        }

        Log.d(TAG,"----------after change-------------");
        Log.d(TAG,"widthImage: "+widthImage);
        Log.d(TAG,"heightImage: "+heightImage);

        btmap=Bitmap.createScaledBitmap(btmap,widthImage,heightImage,false);
        imgShown.setImageBitmap(btmap);
        progressDialogProcessingPic.dismiss();
        uploadBtn.setVisibility(View.VISIBLE);
        mImage = imageToString(btmap);
        myBitmap = btmap;
        Log.v(TAG,"size " + getReadableFileSize(compressedImage.length()) + "");
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        Log.v("long", size + "");
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        String abc = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "";
        try {
            double dbl = Double.parseDouble(abc);
            //Toast.makeText(getActivity(), "" + dbl, Toast.LENGTH_SHORT).show();
            fileSize = dbl;
        } catch (NumberFormatException ex) {
            //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    private void galleryAddPic(String path) {
        Log.d(TAG,"Adding to Gallery :"+path);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.UPLOAD_IMAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.v("qwe", response);
                Gson gson = new Gson();
                SuccessResponse sucessResponse = gson.fromJson(response, SuccessResponse.class);
                boolean error = sucessResponse.error;
                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                if (!error) {
                    resetLocalFields();
                    NewFragment fragment = new NewFragment();
                    ((HomeActivity) getActivity()).addFragment(fragment);
                } else {
                    Toast.makeText(getActivity(), "Image not uploaded", Toast.LENGTH_SHORT).show();
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
                headers.put("Content-Type", Global.CONTENT_TYPE);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.v("wsxz", Prefs.getString(getActivity(), Prefs.MY_LATITUDE) + "  " + Prefs.getString(getActivity(), Prefs.MY_LONGITUDE));
                Map<String, String> params = new HashMap<>();
                params.put("image_name", fileName);
                params.put("image", mImage);
                params.put("user_id", Prefs.getString(getActivity(), Prefs.USER_ID));
                params.put("lat", Prefs.getString(getActivity(), Prefs.MY_LATITUDE));
                params.put("long", Prefs.getString(getActivity(), Prefs.MY_LONGITUDE));

                params.put("place_name", placeName);
                params.put("bill_date", bill_date);
                params.put("location", location);
                params.put("amount", amount);
                params.put("remark", remarks);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void volleyErrorResponseMsg(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public String imageToString(Bitmap btmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        btmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    private void openBackCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        //Uri outputFileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID+".innovationsquare.com.alternatebrain.fileprovider", file);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 1);
    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) throws IOException {
        //Bitmap finalBitmap = myBitmap;
        //String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = Environment.getExternalStorageDirectory();
        File file = new File(myDir + "/sss/");
        file.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        Prefs.putString(getActivity(), Prefs.UP_IMAGE, myDir + "/sss/" + fname);
        Prefs.putString(getActivity(), Prefs.BACK_FILE_NAME, fname);
        Log.v("pathi", myDir + "/sss/" + fname);
        File mfile = new File(file, fname);
//        if (file.exists()) {
//            file.delete();
//        }
        try {
            OutputStream out = new FileOutputStream(mfile);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////


    private void drawFile() {
        Bitmap bitmapImage;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputFileUri);
            Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapImage);
            imgShown.setImageDrawable(bitmapDrawable);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "IOException:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        //showImage(bitmapImage);
    }

    public void getImageDateForOffLine() {
        Prefs.putString(getActivity(), Prefs.UP_IMAGE, String.valueOf(outputFileUri));
        Prefs.putString(getActivity(), Prefs.BACK_FILE_NAME, fileName);
    }

    public void addUploadFileDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_file_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final TextView dateEtx = dialog.findViewById(R.id.dateEtx);
        final EditText nameEdt = dialog.findViewById(R.id.nameEdt);
        final EditText locatonTxt = dialog.findViewById(R.id.locatonTxt);
        final EditText amountTxt = dialog.findViewById(R.id.amountTxt);
        final EditText remarksTxt = dialog.findViewById(R.id.remarksTxt);
        RelativeLayout uploadNow = dialog.findViewById(R.id.uploadNow);

        dateEtx.setText(bill_date);
        nameEdt.setText(placeName);
        locatonTxt.setText(location);
        amountTxt.setText(amount);
        remarksTxt.setText(remarks);

        dateEtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme,
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
}
