package innovationsquare.com.alternatebrain.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.fragment.FragmentAddFile;
import innovationsquare.com.alternatebrain.fragment.FragmentInfo;
import innovationsquare.com.alternatebrain.fragment.FragmentSetting;
import innovationsquare.com.alternatebrain.fragment.NewFragment;
import innovationsquare.com.alternatebrain.utils.Prefs;
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;

public class HomeActivity extends AppCompatActivity {

    double cLat = 0.0, cLog = 0.0;
    boolean b;
    String lat = "0.0";
    String lng = "0.0";
    boolean des = true;
    public LocationManager lm;
    public LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.rightin, R.anim.rightout);
        setContentView(R.layout.activity_home);
        onAskPermission();

//        broadcastReceiver = new NetworkChangeReceiver();

        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
//        FragmentArchives myfragment = new FragmentArchives();
//        addFragment(myfragment);
        getLoc();

        NewFragment myfragment = new NewFragment();
        addFragment(myfragment);
    }

    public void addfileClicked(View view) {
            FragmentAddFile myfragment = new FragmentAddFile();
            addFragment(myfragment);
    }

    public void addFragment(Fragment f){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fragup, R.animator.fragdown, 0, 0);
        fragmentTransaction.replace(R.id.fragment_layout, f);
        fragmentTransaction.commit();
    }

    public void settingClicked(View view) {
            FragmentSetting myfragment = new FragmentSetting();
            addFragment(myfragment);
    }

    public void homeClicked(View view) {
            NewFragment myfragment = new NewFragment();
            addFragment(myfragment);
    }

    public void infoClicked(View view) {
            FragmentInfo myfragment = new FragmentInfo();
            addFragment(myfragment);
    }

    public void logoutClicked(View view) {
        Prefs.putString(this, Prefs.USER_ID, "");
        Prefs.putString(this, Prefs.UP_IMAGE, "");
        Prefs.putString(this, Prefs.BACK_FILE_NAME, "");
        Prefs.putString(this, Prefs.MY_LATITUDE, "");
        Prefs.putString(this, Prefs.MY_LONGITUDE, "");
        Prefs.putString(this, Prefs.MY_LATITUDE_FOR_UPLOAD, "");
        Prefs.putString(this, Prefs.MY_LONGITUDE_FORUPLOAD, "");

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private void onAskPermission() {
        new PermissionWrapper.Builder(this).addPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE})
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
                        Toast.makeText(getApplicationContext(), "Permission was not grant.", Toast.LENGTH_SHORT).show();
                    }
                }).build().request();
    }

    public void getLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Prefs.putString(HomeActivity.this, Prefs.MY_LATITUDE,  "");
            Prefs.putString(HomeActivity.this, Prefs.MY_LONGITUDE, "");
            return;
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                    Log.v("tgb", location+"  dsdssd");
                    if(location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.v("tgb", longitude + " " + latitude);
                        Prefs.putString(HomeActivity.this, Prefs.MY_LATITUDE, latitude + "");
                        Prefs.putString(HomeActivity.this, Prefs.MY_LONGITUDE, longitude + "");
                        lm.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
}
