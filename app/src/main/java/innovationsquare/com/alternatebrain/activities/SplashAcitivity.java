package innovationsquare.com.alternatebrain.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import innovationsquare.com.alternatebrain.services.MyService;
import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.utils.Prefs;

public class SplashAcitivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_splash_acitivity);
        //onAskPermission();
        //testing();
        //startNewScreen();
        des = true;
        if (isMyServiceRunning(MyService.class)) {
            //Toast.makeText(this, "Service running", Toast.LENGTH_LONG).show();
            //foo(this);
            startNewScreen();
        } else {
            //Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
            startService(new Intent(getBaseContext(), MyService.class));
            startNewScreen();
            //foo(this);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startNewScreen() {
        final Handler loadingHandler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (Prefs.getString(SplashAcitivity.this, Prefs.USER_ID).equals("")) {
                    startActivity(new Intent(SplashAcitivity.this, RegisterActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashAcitivity.this, HomeActivity.class));
                    finish();
                }
            }
        };
        loadingHandler.postDelayed(runnable, 3000);
    }

}
