package innovationsquare.com.alternatebrain.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by tariq on 11/7/2017.
 */

public class MyService extends Service {

    public BroadcastReceiver broadcastReceiver;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
