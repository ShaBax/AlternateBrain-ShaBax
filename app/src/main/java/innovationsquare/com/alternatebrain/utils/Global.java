package innovationsquare.com.alternatebrain.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tariq on 10/12/2017.
 */

public class Global {
    public static String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static String LATITUDE = "0.0";
    public static String LONGITUDE = "0.0";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
